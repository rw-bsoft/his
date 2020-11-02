/**
 * 贫困人群随访列表页面
 * 
 * @author : tianj
 */
$package("chis.application.ppvr.script")

$import("chis.script.BizSimpleListView")

chis.application.ppvr.script.PoorPeopleVisitRecord = function(cfg) {
	cfg.initCnd = ["eq", ["$", "c.status"], ["s", '0']];
	chis.application.ppvr.script.PoorPeopleVisitRecord.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.ppvr.script.PoorPeopleVisitRecord, chis.script.BizSimpleListView, {
	onEmpiSelected : function(data) {
		var cfg = {};
		cfg.empiId = data.empiId;
		cfg.initModules = ['B_08'];
		cfg.closeNav = true;
		cfg.mainApp = this.mainApp;
		var module = this.midiModules["PoorPeopleVisit_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			module.on("save", this.onSave, this);
			module.exContext.args.visitId = data.visitId;
			this.midiModules["PoorPeopleVisit_EHRView"] = module;
		} else {
			module.exContext.ids["empiId"] = data.empiId;
			module.exContext.args["visitId"] = data.visitId;
			module.refresh();
		}
		module.getWin().show();
	},
	
	onSave : function() {
		this.refresh();
	},
	
	doModify : function() {
		if (this.store.getCount() == 0) {
			return;
		}
		var r = this.grid.getSelectionModel().getSelected();
		this.onEmpiSelected(r.data);
	},

	onDblClick : function(grid, index, e) {
		this.doModify();
	}
});