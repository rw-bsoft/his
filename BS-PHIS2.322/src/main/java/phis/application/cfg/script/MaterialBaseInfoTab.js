$package("phis.application.cfg.script")
$import("phis.script.common", "phis.script.TableForm")

phis.application.cfg.script.MaterialBaseInfoTab = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.cfg.script.MaterialBaseInfoTab.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.MaterialBaseInfoTab, phis.script.TableForm, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var _layoutConfig = {
			columns : 3,
			tableAttrs : {
				border : 0,
				cellpadding : '2',
				cellspacing : "2"
			}
		};
		this.form = new Ext.FormPanel({
					labelWidth : 80, // label settings here cascade
					// unless overridden
					frame : true,
					// autoScroll : true,
					defaultType : 'fieldset',
					items : [{
								// title : '',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : _layoutConfig,
								defaultType : 'textfield',
								items : this.getItems('JBXX')
							}, {
								title : '属性',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : _layoutConfig,
								defaultType : 'textfield',
								items : this.getItems('SX')
							}, {
								title : '条码定义',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : _layoutConfig,
								defaultType : 'textfield',
								items : this.getItems('TMDY')
							}]
				});
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		this.form.on("afterrender", this.onReady, this)
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		return this.form
	},
	getItems : function(para) {
		var ac = util.Accredit;
		var MyItems = [];
		var schema = null;
		var re = util.schema.loadSync(this.entryName)
		if (re.code == 200) {
			schema = re.schema;
		} else {
			this.processReturnMsg(re.code, re.msg, this.initPanel)
			return;
		}
		var items = schema.items
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.layout || it.layout != para) {
				continue;
			}
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				// alert(it.acValue);
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)
			MyItems.push(f);
		}
		return MyItems;
	},
	doNew : function() {
		this.op = "create"
		if (this.data) {
			this.data = {}
		}
		if (!this.schema) {
			return;
		}
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				f.setValue(it.defaultValue)
				// update by caijy for 新增页面的时间动态生成
				if (it.type == "timestamp") {
					f.setValue(Date.getServerDateTime());
				}
				// @@ 2010-01-07 modified by chinnsii, changed the condition
				// "it.update" to "!=false"
				if (!it.fixed && !it.evalOnServer) {
					f.enable();
				} else {
					f.disable();
				}

				if (it.type == "date") { // ** add by yzh 20100919 **
					if (it.minValue)
						f.setMinValue(it.minValue)
					if (it.maxValue)
						f.setMaxValue(it.maxValue)
				}
				// add by yangl 2012-06-29
				if (it.dic && it.dic.defaultIndex) {
					if (f.store.getCount() == 0)
						continue;
					if (isNaN(it.dic.defaultIndex)
							|| f.store.getCount() <= it.dic.defaultIndex)
						it.dic.defaultIndex = 0;
					f.setValue(f.store.getAt(it.dic.defaultIndex).get('key'));
				}
			}
		}
		this.setKeyReadOnly(false)
		this.resetButtons(); // ** add by yzh **
		this.fireEvent("doNew")
		this.focusFieldAfter(-1, 800)
		this.validate()
	},
	onReady : function() {
		phis.application.cfg.script.MaterialBaseInfoTab.superclass.onReady.call(this);
		var _form = this.form.getForm();
		this.opener.win.on("show", function() {
					this.opener.tab.setActiveTab(0);
					if (this.opener.cfg.op == "create") {
						this.opener.refresh();
					} else if (this.opener.cfg.op == "update") {
						this.opener.refresh();
						this.initFormData(this.opener.cfg._record.data);
					}

				}, this);
		if (this.opener.cfg.op == "update") {
			this.initFormData(this.opener.cfg._record.data);
			this.form.getForm().findField("ZBLB").disable();
			this.form.getForm().findField("HSLB").disable();
		}
		var jlbzobj=this.form.getForm().findField("JLBZ");
		jlbzobj.on("select",this.onSelectjlbz,this);
		this.initForm();
		var field = this.form.getForm().findField("ZBLB");
		var fieldKFXH = this.form.getForm().findField("KFXH");
		// var glfs = this.form.getForm().findField("GLFS");
		var filters = "";
		if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
			filters = "['eq',['$','item.properties.JGID'],['s'," + this.mainApp['phisApp'].deptId
					+ "]]";
		} else {
			filters = "['in',['$','item.properties.ZBLB','i'],["
					+ this.mainApp['phis'].treasuryKfzb + "]]";
		}
		field.store.removeAll();
		field.store.proxy = new Ext.data.HttpProxy({
					method : "GET",
					url : util.dictionary.SimpleDicFactory.getUrl({
								id : "phis.dictionary.booksCategory",
								filter : filters
							})
				})
		field.store.load();
		var defaultValue = 0;
		if(this.mainApp['phis'].treasuryId){
			defaultValue=this.mainApp['phis'].treasuryId;
		}
		fieldKFXH.store.removeAll();
		fieldKFXH.store.proxy = new Ext.data.HttpProxy({
					method : "GET",
					url : util.dictionary.SimpleDicFactory.getUrl({
								id : "phis.dictionary.treasury",
								defaultValue:defaultValue
							})
				})
		fieldKFXH.store.load();
		_form.findField("ZBLB").on("select", this.setHSLB, this)
		_form.findField("ZBLB").on("blur", this.setHSLB, this)
	},

	initForm : function() {
		this.initFormData({
					"FLMC" : this.opener.opener.LBXH != 0
							? this.opener.opener.selectedNode.attributes.text
							: "",
					"KFXH" : this.mainApp['phis'].treasuryId
							? this.mainApp['phis'].treasuryId
							: "",
					"HSLB" : this.opener.opener.selectedNode.id,
					"ZBLB" : this.opener.opener.selectedNode.parentNode.id			
							
				})
//		this.initFormData({
//					"KFXH" : this.mainApp['phis'].treasuryId
//							? this.mainApp['phis'].treasuryId
//							: ""
//				});
//		this.initFormData({
//			"HSLB" : this.opener.opener.selectedNode.id
//		});
//		this.initFormData({
//			"ZBLB" : this.opener.opener.selectedNode.parentNode.id
//		});
	},
	setHSLB : function() {
		var hslb = [];
		var field = this.form.getForm().findField("HSLB");
		if (this.opener.cfg.op != "update") {
			field.enable();
			field.setValue("");
		}
		var zblb = 0;
		if (this.form.getForm().findField("ZBLB").getValue()) {
			zblb = this.form.getForm().findField("ZBLB").getValue();
		}
		if (zblb == 0) {
			field.disable();
			field.setValue("");
		}
		 var r = phis.script.rmi.miniJsonRequestSync({
		 serviceId : this.serviceId,
		 serviceAction : "queryHSLB",
		 ZBLB : zblb
		 });
		 for (var i = 0; i < r.json.body.length; i++) {
			 hslb.push(r.json.body[i].HSLB);
		 }
		 if (hslb.length <= 0) {
			 return;
		 }
		  var filters ="['and',['and',['and',['or',['eq',['$','item.properties.JGID'],['s'," +this.mainApp['phisApp'].deptId + "]],['eq',['$','item.properties.JGID'],['s'," +this.mainApp.topUnitId +
		  "]]],['eq',['$','item.properties.ZBLB'],['i'," + zblb + "]]],['ne',['$','item.properties.SJHS'],['i',-1]]],['in',['$','item.properties.HSLB','i'],["+hslb+"]]]";
		field.store.removeAll();
		field.store.proxy = new Ext.data.HttpProxy({
					method : "GET",
					url : util.dictionary.SimpleDicFactory.getUrl({
								id : "phis.dictionary.WL_HSLB_SJHS",
								filter : filters
							})
				})
		field.store.load();
	},
	doCancel : function() {
		this.opener.win.hide();
	},
	isDirty : function(op) {
		var dirty = false;
		var form = this.form.getForm();
		if (op == "create") {
			var items = this.schema.items;
			var n = items.length;
			for (var i = 0; i < n; i++) {
				var it = items[i]
				var f = form.findField(it.id)
				if (f && f.isDirty()) {
					dirty = true;
					break;
				}
			}
		}
		if (op == "update") {

		}
		return dirty;
	},
	getSaveData : function() {
		var ac = util.Accredit;
		var form = this.form.getForm()
		// if (!this.validate()) {
		// return
		// }
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items
		Ext.apply(this.data, this.exContext.empiData)
		if (items) {
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
				if (v == undefined) {
					v = it.defaultValue
					if (it.type == "timestamp" && this.op == "create") {// update
						// by
						// caijy
						// 2013-3-21
						// for
						// 新增页面的时间动态生成
						v = Date.getServerDateTime();
					}
				}
				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					v = f.getValue()
					// add by caijy from checkbox
					if (f.getXType() == "checkbox") {
						var checkValue = 1;
						var unCheckValue = 0;
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
						}
						if (v == true) {
							v = checkValue;
						} else {
							v = unCheckValue;
						}
					}
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
					// end
				}
				if (v == null || v === "") {
					if (!it.pkey && it["not-null"] && !it.ref) {
						Ext.Msg.alert("提示", it.alias + "不能为空")
						return;
					}
				}
				if (it.type && it.type == "int") {
					v = (v == "0" || v == "" || v == undefined)
							? 0
							: parseInt(v);
				}
				values[it.id] = v;
			}
		}
		values.JGID = this.mainApp['phisApp'].deptId;
		return values;
	},
	selectGlfs : function() {
		var ejjk = this.form.getForm().findField("EJJK");
		var zjff = this.form.getForm().findField("ZJFF");
		var zjnx = this.form.getForm().findField("ZJNX");
		var zgzl = this.form.getForm().findField("ZGZL");
		var jczl = this.form.getForm().findField("JCZL");
		var glfsvalue = this.form.getForm().findField("GLFS");
		if (glfsvalue.getValue() != "1") {
			ejjk.setDisabled(true);
			ejjk.setValue(0);
			if(glfsvalue.getValue()=="2"){
//				zjff.setDisabled(true);
				zjff.setValue(0);
				zjnx.setDisabled(true);
				zjnx.setValue("");
				zgzl.setDisabled(true);
				zgzl.setValue("");
				jczl.setDisabled(true);
				jczl.setValue("");
			}
			if(glfsvalue.getValue()=="3"){
//				zjff.setDisabled(false);
				zjnx.setDisabled(false);
				zgzl.setDisabled(false);
				jczl.setDisabled(false);
			}			
		} else {
			ejjk.setDisabled(false);
//			zjff.setDisabled(true);
			zjff.setValue(0);
			zjnx.setDisabled(true);
			zjnx.setValue("");
			zgzl.setDisabled(true);
			zgzl.setValue("");
			jczl.setDisabled(true);
			jczl.setValue("");
		}
	},
	updHSLB : function() {
		this.form.getForm().findField("ZBLB").disable();
		this.form.getForm().findField("HSLB").disable();
	},
	loadData:function(){
		this.form.getForm().findField("GLFS").on("select", this.selectGlfs, this)
		this.form.getForm().findField("GLFS").on("blur", this.selectGlfs, this)
	},
	onSelectjlbz:function(jlbz){
		if(this.opener.cfg.op=="update"){
			if(jlbz.getValue()==1){
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "queryJLXX",
					WZXH : this.opener.cfg.WZXH
			});
				if(ret.json.JLXH==1){
					jlbz.disable();
				}else{
					jlbz.enable();
				}
			}
		}
	}
})