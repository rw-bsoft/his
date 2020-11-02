$package("phis.application.ivc.script");

$import("util.Accredit", "app.modules.form.SimpleFormView",
		"app.modules.form.TableFormView", "app.modules.list.SimpleListView",
		"phis.application.pix.script.SubTableList",
		"phis.application.pix.script.SubTableEditList",
		"app.modules.common", "util.widgets.LookUpField", "util.Vtype",
		"phis.script.CardReader", "phis.script.SimpleModule");
phis.application.ivc.script.ClinicPhysicalTjModule = function(cfg) {
	cfg.width = "820";
	cfg.height = "350";
	phis.application.ivc.script.ClinicPhysicalTjModule.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this);
//    this.on("shortcutKey", this.shortcutKeyFunc, this);
}
Ext.extend(phis.application.ivc.script.ClinicPhysicalTjModule, phis.script.SimpleModule,
		{
	initPanel : function() {
		//this.doLoad();
		if (this.panel) {
			return this.panel;
		}
		var panel = new Ext.Panel({
			buttonAlign : "center",
			deferredRender : false,
			border : false,
			frame : false,
			activeTab : 0,
			autoHeight : true,
			defaults : {
				frame : false,
				autoHeight : true,
				autoWidth : true
			},
					items : [{
								layout : "fit",
								border : false,
								split : true,
								title : '基本信息',
								region : 'center',
								width : 960,
								height : 350,
								items : this.getForm()
							}],
							buttons : [{
								// id : "saveBut",
								cmd : "save",
								xtype : "button",
								text : "确定",
								handler : this.onBeforeSave,
								scope : this
							}, {
								xtype : "button",
								text : "取消",
								handler : this.doCancel,
								scope : this
							}
							/**,{
								cmd : "newMr",
								xtype : "button",
								text : "默认信息录入",
								handler : this.newMeSet,
								scope : this
							}*/]
				});
		this.panel = panel;
		return this.panel;
	},
	doLoad : function() {
		var key="GRXX_CX";
		var moduleFprm = this.createModule("form", this.refForm);
		phis.script.rmi.jsonRequest({
			serviceId : "clinicChargesProcessingService",	
			serviceAction : "queryPhysicalMr",
			method : "execute",
			body: key,
		}, function(code, msg, json) {
			if (code > 300) { 
			}else{
				 moduleFprm.loadData(json);
			}
		}, this);
	},
	onWinShow : function() {
		var moduleFprm = this.createModule("form", this.refForm);
	    moduleFprm.doNew();
	},
	doCancel : function() {
		this.getWin().hide()
		var moduleFprm = this.createModule("form", this.refForm);
		    moduleFprm.doNew();
	},
	getForm : function() {
		var module = this.createModule("form", this.refForm);
		    module.opener = this;
		this.module=module;
		return this.module.initPanel();
		//焦点控制
	//	var form = module.form.getForm();
     //   this.form = form;
 	//	var idCard = this.form.findField("idCard");
 	//	idCard.on("blur", this.onblur2, this);
 		
	},onBeforeSave : function(){
		this.saveend = false;
	  this.module.doSave();
	  if(this.saveend){
		  this.module.on("onFormLoad2", this.onFormLoad2, this);
		  this.doCancel();
	  }
	},newMeSet : function(){
		var form =  this.createModule("newMr", this.newMr);
		if (!this.AduitFormWin) {
			this.AduitFormWin = form.getWin();
			this.AduitFormWin.add(form.initPanel());
		}
		this.AduitFormWin.setPosition(250, 100);
		this.AduitFormWin.show();
	},onFormLoad2 : function(){
		  this.fireEvent("onFormLoad",this.formData);
	},onblur2 : function(){
		  alert(5656);
	}
});