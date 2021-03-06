$package("chis.application.dr.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest")

chis.application.dr.script.DRReferralsReportListMoudle = function(cfg) {
	this.schema = cfg.entryName;
	chis.application.dr.script.DRReferralsReportListMoudle.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.dr.script.DRReferralsReportListMoudle, chis.script.BizSimpleListView, {
	getWin : function() {
		var win = this.win
		var closeAction = "close"
		if (!this.mainApp || this.closeAction) {
			closeAction = "hide"
		}
		if(this.screenMax && this.screenMax=="true"){
					this.width=screen.availWidth
					this.height=screen.availHeight;
				}
		if (!win) {
			win = new Ext.Window({
				title : this.title || this.name,
				width : this.width,
				height : this.height,
				iconCls : 'icon-grid',
				shim : true,
				layout : "fit",
				animCollapse : true,
				closeAction : closeAction,
				constrainHeader : true,
				constrain : true,
				minimizable : true,
				maximizable : true,
				shadow : false,
				modal : this.modal || false
					// add by huangpf.
				})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		win.maximize();
		win.instance = this;
		return win;
	},
	
	onDblClick : function(grid, index, e) {
		this.doModify();
	},
	doModify : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var m = this.getReferralsForm(r);
		m.op = "read";
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
		var formData = this.castListDataToForm(r.data, this.schema);
		m.initFormData(formData);
	},
	getReferralsForm : function(r) {
		var m = this.midiModules["referralsReportForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var obj={id:"DR0301",name:"下转记录表单",ref:"chis.application.dr.DR/DR/DR0301",script:"chis.application.dr.script.DRReferralsReportListView",type:"1",entryName:"chis.application.dr.schemas.drIt_sendExchangeReportForm"};
			//var moduleCfg = this.mainApp.taskManager
				//	.loadModuleCfg("chis.application.dr.DR/DR/DR0301");
			//Ext.apply(cfg, moduleCfg.json.body);
			//Ext.apply(cfg, moduleCfg.json.body.properties);
			Ext.apply(cfg, obj);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			m.on("save", this.refresh, this);
			m.on("close", this.active, this);
			this.midiModules["referralsReportForm"] = m;
		} else {
			m.initDataId = r.id;
		}
		return m;
	}
});