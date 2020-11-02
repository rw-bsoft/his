$package("chis.application.wl.script");
$import("chis.script.BizSimpleListView");

chis.application.wl.script.MyWorkDiabetesRiskAssessmentList = function(cfg) {
	this.initCnd = cfg.cnds
	chis.application.wl.script.MyWorkDiabetesRiskAssessmentList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkDiabetesRiskAssessmentList, chis.script.BizSimpleListView, {
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
	doEstimate : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return
		}
		this.empiId = r.get("empiId")
		this.riskId = r.get("otherId")
		this.showEhrViewWin()
	}
	,
	onDblClick:function(){
		this.doEstimate()
	}
	,
	showEhrViewWin : function() {
		var empiId = this.empiId
		var cfg = {}
		cfg.empiId = empiId
		cfg.initModules = ['D_06']
		cfg.closeNav = true
		cfg.mainApp = this.mainApp
		cfg.exContext = this.exContext
		var module = this.midiModules["DiabetesRiskListView_EHRView"]
		if (!module) {
			$import("chis.script.EHRView")
			module = new chis.script.EHRView(cfg)
			module.exContext.ids.riskId = this.riskId
			module.on("save", this.onSave, this)
			this.midiModules["DiabetesRiskListView_EHRView"] = module
		} else {
			Ext.apply(module, cfg)
			module.exContext.ids = {}
			module.exContext.ids.empiId = empiId
			module.exContext.ids.riskId = this.riskId
			module.refresh()
		}
		module.getWin().show()
	}
});