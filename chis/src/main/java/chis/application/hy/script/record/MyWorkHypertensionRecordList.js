$package("chis.application.hy.script.record");
$import("chis.application.hy.script.record.HypertensionRecordListView");

chis.application.hy.script.record.MyWorkHypertensionRecordList = function(cfg) {
	chis.application.hy.script.record.MyWorkHypertensionRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hy.script.record.MyWorkHypertensionRecordList, chis.application.hy.script.record.HypertensionRecordListView, {
	
	showEhrViewWin : function() {
		var m = this.midiModules["ehrView_hypertension_workList"];
		if (!m) {
			$import("chis.script.EHRView");
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : ['C_01', 'C_02', 'C_03', 'C_05',
								'C_04'],
						mainApp : this.mainApp,
						empiId : this.empiId,
						activeTab : 1
					});
			this.midiModules["ehrView_hypertension_workList"] = m;
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids = {};
			m.exContext.ids["empiId"] = this.empiId;
			m.activeTab = 1
			m.refresh();
		}
		m.getWin().show();
	}
});