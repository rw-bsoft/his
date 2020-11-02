$package("chis.application.wl.script");
$import("chis.application.psy.script.record.PsychosisRecordListView");

chis.application.wl.script.MyWorkPsychosisRecordList = function(cfg) {
	cfg.initCnd = cfg.cnds || ['and',['eq',['$','a.status'],['s','0']],['eq',['$','d.workType'],['s','08']]];
	chis.application.wl.script.MyWorkPsychosisRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkPsychosisRecordList,chis.application.psy.script.record.PsychosisRecordListView, {
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	onEmpiSelected : function(data) {
				var empiId = data.empiId
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['P_01', 'P_02','P_03','P_04']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.activeTab=3
				var module = this.midiModules["PsychosisRecordListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["PsychosisRecordListView_EHRView"] = module
				} else {
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
			}
});