/**
 * 非重点人群随访列表页面
 * 
 * @author : tianj
 */
$package("chis.application.npvr.script")

$import("chis.script.BizSimpleListView")

chis.application.npvr.script.NormalPopulationVisitRecord = function(cfg) {
	cfg.initCnd = ["eq", ["$", "c.status"], ["s", '0']];
	chis.application.npvr.script.NormalPopulationVisitRecord.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.npvr.script.NormalPopulationVisitRecord, chis.script.BizSimpleListView, {
	onEmpiSelected : function(data) {
		var cfg = {};
		cfg.empiId = data.empiId;
		cfg.initModules = ['B_01', 'B_09'];
		cfg.activeTab = 1;
		cfg.closeNav = true;
		cfg.mainApp = this.mainApp;
		var module = this.midiModules["NormalPopulationVisit_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			module.on("save", this.onSave, this);
			module.exContext.args.visitId = data.id;
			this.midiModules["NormalPopulationVisit_EHRView"] = module;
		} else {
			module.exContext.ids["empiId"] = data.empiId;
			module.exContext.args["visitId"] = data.id;
			module.refresh();
		}
		module.actionName = "EHR_HealthRecord";
		module.getWin().show();
	},

	onSave : function() {
		this.refresh();
	},
	
	doModify : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		this.onEmpiSelected(r.data);
	},

	onDblClick : function() {
		this.doModify();
	}
});