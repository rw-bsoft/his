$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.HealthRecordRevertList = function(cfg) {
	cfg.initCnd = ["eq", ["s", "1"], ["s", "1"]]
	this.serviceId = "chis.healthRecordService"
	this.serviceAction = "setHealthRecordNormal"
	chis.application.hr.script.HealthRecordRevertList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hr.script.HealthRecordRevertList, chis.script.BizSimpleListView, {
	doRevert : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected()
		var cfg = {}
		cfg.title = r.get("personName") + "的档案列表";
		cfg.phrId = r.get("phrId");
		cfg.empiId = r.get("empiId");
		cfg.autoLoadSchema = false;
		cfg.autoLoadData = false;
		cfg.isCombined = true;
		cfg.showButtonOnTop = true;
		cfg.constrainHeader = false;
		var module = this.midiModules["RecordRevert"];
		if (!module) {
			$import("chis.application.hr.script.RecordRevertList");
			module = new chis.application.hr.script.RecordRevertList(cfg);
			module.on("logout", this.onLogout, this);
			this.midiModules["RecordRevert"] = module;
		} else {
			Ext.apply(module, cfg);
		}
		module.getWin().show();
	},

	onLogout : function() {
		this.loadData()
	},

	onDblClick : function() {
		this.doRevert();
	}
});