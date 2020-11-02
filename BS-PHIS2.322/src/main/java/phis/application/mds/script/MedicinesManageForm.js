$package("phis.application.mds.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.mds.script.MedicinesManageForm = function(cfg) {
	cfg.fldDefaultWidth = 120;
	cfg.showButtonOnTop=false;
//	cfg.entryName="phis.application.mds.schemas.YK_TYPK";
//	cfg.serviceId="medicinesManageService";
//	cfg.verifiedUsingServiceAction="verifiedUsing";
//	cfg.queryPljcActionId="queryPljc";
	phis.application.mds.script.MedicinesManageForm.superclass.constructor.apply(this,[cfg])
	this.on("doNew", this.onWinShow, this);
	this.on("loadData", this.onWinShow, this)
}

Ext.extend(phis.application.mds.script.MedicinesManageForm, phis.script.SimpleForm, {
	onReady : function() {
		phis.application.mds.script.MedicinesManageForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("YFBZ").on("blur", this.onYfbzChange, this);
		form.findField("ZXBZ").on("blur", this.onZxbzChange, this);
		form.findField("BFBZ").on("blur", this.onBfbzChange, this);
		if(!this.yszhlyy){//update by caijy at 2015.1.19 for医生站合理用药信息调阅,医生站打开的 不显示其他和抗生素
		form.findField("KSBZ").on("select", this.onKsbzChange, this);
		}
	},
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var items=[{
								xtype : 'fieldset',
								title : '基本信息',
								// collapsible : true,
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 5,
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
								title : '药品包装',
								// collapsible : true,
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 4,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('YPBZ')
							}]
				if(!this.yszhlyy){//update by caijy for 医生站合理用药不显示其他和抗生素
				items.push({
								xtype : 'fieldset',
								title : '其他',
								// collapsible : true,
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 4,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('QT')
							});
							items.push({
								xtype : 'fieldset',
								title : '抗生素',
								// collapsible : true,
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 4,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('KSS')
							})
				}
		this.form = new Ext.FormPanel({
					labelWidth : 85, // label settings here cascade
					// unless overridden
					frame : true,
					//	bodyStyle : 'padding:5px 5px 0',
					width : 1024,
					autoHeight : true,
					items : items
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
	expand : function() {
		this.win.center();
	},
	// 药房包装验证
	onYfbzChange : function() {
		var form = this.form.getForm();
		var zxbz = form.findField("ZXBZ").getValue();
		var yfbz = form.findField("YFBZ").getValue();
		if (zxbz % yfbz != 0) {
			Ext.Msg.alert("提示", "药房包装必须能被最小包装整除");
			return 0;
		}
		return 1;
	},
	// 病房包装验证
	onBfbzChange : function() {
		var form = this.form.getForm();
		if ((form.findField("ZXBZ").getValue()) % (form.findField("BFBZ").getValue()) != 0) {
			Ext.Msg.alert("提示", "病房包装必须能被最小包装整除");
			return 0;
		}
		return 1;
	},
	onZxbzChange:function(){
		if(this.op=="create"){
		return;
		}
		var body={};
		body["YPXH"]=this.initDataId
		var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verifiedUsingServiceAction,
							body : body
						});
					if (ret.code > 300) {
						MyMessageTip.msg("提示", ret.msg, true);
						this.form.getForm().findField("ZXBZ").setValue(this.data.ZXBZ);
				}
	},
	// 保存前进行FORM验证
	doValidation : function() {
		if (this.onYfbzChange() == 0) {
			return 0;
		}
		if (this.onYfbzChange() == 0) {
			return 0;
		}
	},
	// 抗生素判别
	onKsbzChange : function() {
		if(this.yszhlyy){//update by caijy at 2015.1.19 for医生站合理用药信息调阅,医生站打开的 不显示其他和抗生素
		return;}
		var form = this.form.getForm();
		var sfkss = form.findField("KSBZ").getValue();
		if (sfkss.key) {
			sfkss = sfkss.key;
		}
		if (sfkss != 1) {
			form.findField("YCYL").disable();
			form.findField("KSSDJ").disable();
			form.findField("DDDZ").disable();
			form.findField("KSSDJ").allowBlank = true;
			form.findField("KSSDJ").setValue("1");
			form.findField("KSSDJ").setValue("");
		} else {
			form.findField("YCYL").enable();
			form.findField("KSSDJ").enable();
			form.findField("DDDZ").enable();
			form.findField("KSSDJ").allowBlank = false;
			//form.findField("KSSDJ").setValue(1);
		}
	},
	doNew: function(){
		this.op = "create"
		if(this.data){
			this.data = {}
		}
		if(!this.schema){
			return;
		}
		var form = this.form.getForm();
		form.reset();
		var items = this.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
				if(!(arguments[0] == 1)){	// whether set defaultValue, it will be setted when there is no args.
					var dv = it.defaultValue;
					if(dv){
						if((it.type == 'date' || it.xtype=='datefield') && typeof dv == 'string' && dv.length > 10){
							dv = dv.substring(0,10);
						}
						f.setValue(dv);
					}
				}
				if(!it.update && !it.fixed && !it.evalOnServer){
					f.enable();
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
				if(it.id!="KSSDJ"){
				f.validate();
				}
			}		
		}
		this.setKeyReadOnly(false)
		//this.startValues = form.getValues(true);
		this.fireEvent("doNew",this.form)
		this.focusFieldAfter(-1,800)
		this.onKsbzChange();
		//this.afterDoNew();
		this.resetButtons();
	},
	onWinShow : function() {
		this.onKsbzChange();
	},
	//update by caijy at 2015.1.19 for 医生站合理用药信息调阅,只读
	doRead:function(){
	var form = this.form.getForm();
		form.reset();
		var items = this.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
			f.disable();
			}
			}
	this.loadData();
	}
});