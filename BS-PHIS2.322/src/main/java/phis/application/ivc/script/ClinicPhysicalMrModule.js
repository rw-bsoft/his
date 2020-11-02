$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");
phis.application.ivc.script.ClinicPhysicalMrModule = function(cfg) {
	cfg.width = "820";
	cfg.height = "350";
	phis.application.ivc.script.ClinicPhysicalMrModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ClinicPhysicalMrModule, phis.script.SimpleModule,
		{
	initPanel : function() {
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
								title : '系统参数录入',
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
								handler : function() {
									this.getWin().hide()
								},
								scope : this
							}]
				});
		this.panel = panel;
		return panel;
	},
	getForm : function() {
		this.form = this.createModule("newMr", this.newMr);
		return this.form.initPanel();
		
	},onBeforeSave : function(){
	  this.form.doSave();
	}
});