$package("phis.application.pha.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.pha.script.PharmacyBasicInfomationForm = function(cfg) {
	cfg.modal = true;
	cfg.repeatInspectionServiceId = "pharmacyManageService";
	cfg.repeatInspectionActionId = "repeatInspection";
	cfg.butRule=false;//去掉新建按钮
	phis.application.pha.script.PharmacyBasicInfomationForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyBasicInfomationForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.actions = [{
							id : "save",
							name : "确认",
							iconCls : "save24",
							scale : 'large'
						}, {
							id : "cancel",
							name : "关闭",
							iconCls : "close24",
							notReadOnly : true,
							scale : 'large'
						}]
				this.check = new Ext.form.FieldSet({
							title : '发药科室选择',
							checkboxToggle : {
								tag : 'input',
								type : "checkbox",
								checked : false
							},
							// onCheckClick:this.onKsCheck,
							height : 'auto', // width:200, height:250 
							// : true,
							items : this.getItems('FYKS')
						})
				this.check.unCheck = function() {
					this.checkbox.dom.checked = false;
				}
				this.check.doCheck = function() {
					this.checkbox.dom.checked = true;
				}
				this.check.onCheckClick = function() {
					this.fireEvent("check", this.checkbox.dom.checked)
				}
				this.check.on("check", this.onKsCheck, this)
				this.form = new Ext.FormPanel({
							labelWidth : 85, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1024,
							autoHeight : true,
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										title : '药品权限',
										// collapsible : true,
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('YPQX')
									}

									, this.check],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.form.on("afterrender", this.onReady, this)		
				if (!this.isCombined) {
					this.addPanelToWin();
				}
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
				if (para == "FYKS") {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "pharmacyManageService",
								serviceAction : "departmentSearch"
							});
					if (ret.code > 300) {
						return null;
					}
				}
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.getItems)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
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
			expand : function() {
				this.win.center();
			},
			// 保存前检验名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				var body = {};
				body["mc"] = saveRequest.YFMC;
				body["pkey"] = (saveRequest.YFSB == null || saveRequest.YFSB == "")
						? 0
						: saveRequest.YFSB;
				body["pb"] = "yf";
				body["jgid"] = saveRequest.JGID;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.repeatInspectionServiceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "药房名称已经存在!";
					this.processReturnMsg(ret.code, msg, this.onBeforeSave);
					return false;
				} else {
					return true;
				}
			},
			// 保存后重载字典
			onSave : function() {
				phis.script.rmi.jsonRequest({
							serviceId : "publicService",
							serviceAction : "reloadDictionarys",
							body : ['phis.dictionary.pharmacy', 'phis.dictionary.pharmacy_bq', 'phis.dictionary.pharmacy_bqtj',
									'phis.dictionary.pharmacy_db']
						}, function(code, msg, json) {

						}, this);
			},
			onKsCheck : function(checked) {
				if (checked) {
					this.form.getForm().findField("LYKS").checkAll();
				} else {
					this.form.getForm().findField("LYKS").unCheckAll();
				}
			},
			// 回填时根据科室是否全部选中来决定全选Box是否选中
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if(it.id=="SJYF"){
					this.setSJYF();
					}
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v !== "" && v === 0) {// add by
									// yangl
									// 解决字典类型值为0(int)时无法设置的BUG
									v = "0";
								}
								f.setValue(v)
								if (it.id == "LYKS") {
									if (f.isAllChecked()) {
										this.check.doCheck();
									} else {
										this.check.unCheck();
									}
								}
								if (it.dic && v != "0" && f.getValue() != v) {
									f.counter = 1;
									this.setValueAgain(f, v, it);
								}

							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true)
					this.focusFieldAfter(-1, 800)
				}
			},
			// 新增默认科室全选
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
						if (it.id == "LYKS") {
							f.checkAll();
						}
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
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
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
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
			phis.application.pha.script.PharmacyBasicInfomationForm.superclass.onReady.call(this);
			var form=this.form.getForm();
			form.findField("SJJG").on("select", this.setSJYF, this);
			form.findField("SJJG").on("blur", this.setSJYF, this);
			},
			setSJYF:function(){
			var sjyf = this.form.getForm().findField("SJYF");
			var jgid=this.form.getForm().findField("SJJG").getValue();
			var filters ="['eq',['$','item.properties.JGID'],['s','"+jgid+"']]";
			sjyf.store.removeAll();
			sjyf.store.proxy = new Ext.data.HttpProxy({
					method : "GET",
					url : util.dictionary.SimpleDicFactory.getUrl({
								id : "phis.dictionary.pharmacy_db",
								filter : filters
							})
				})
			sjyf.store.load();
			}
		});
