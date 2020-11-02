$package("chis.application.dr.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest")

chis.application.dr.script.DRReferralsMoudle = function(cfg) {
	chis.application.dr.script.DRReferralsMoudle.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.dr.script.DRReferralsMoudle, chis.script.BizSimpleListView, {
	doRefers : function() {
		var r = {};
		var m = this.getReferralsForm(false, r);
		m.op = "update";
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	getReferralsForm : function(isReferral, r) {
		var m = this.midiModules["DRReferralsForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager
					.loadModuleCfg("chis.application.dr.DR/DR/DR0101");
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
//					m.on("save", this.refresh, this);
//					m.on("close", this.active, this);
			this.midiModules["DRReferralsForm"] = m;
		} else {
			m.initDataId = r.id;
		}
		m.isReferral = isReferral;
		return m;
	},
	doRefersReport : function() {
		var r = {};
		var m = this.getReferralsReportForm(false, r);
		m.op = "update";
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	getReferralsReportForm : function(isReferral, r) {
		var m = this.midiModules["DRExchangeReportForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager
					.loadModuleCfg("chis.application.dr.DR/DR/DR0102");
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			this.midiModules["DRExchangeReportForm"] = m;
		} else {
			m.initDataId = r.id;
		}
		m.isReferral = isReferral;
		return m;
	}
});