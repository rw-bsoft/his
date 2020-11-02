$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSimpleListView","chis.script.EHRView");

chis.application.tr.script.highRisk.TumourHighRiskVisitView = function(cfg){
	chis.application.tr.script.highRisk.TumourHighRiskVisitView.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskVisitView,chis.script.BizSimpleListView,{
	onDblClick : function() {
		this.doVisit();
	},
	doVisit:function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		this.empiId = r.get("empiId");
		this.recordStatus = r.get("status");
		this.highRiskType = r.get("highRiskType");
		this.activeTab = 2;
		var planId = r.get("planId");
		this.showEhrViewWin(planId);
	},
	showEhrViewWin : function(planId) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_01','T_02','T_03'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourHighRisk_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourHighRisk_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.refresh();
		}
		module.getWin().show();
	},
	doViewPMH : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var module = this.createSimpleModule("TumourPastMedicalHistoryView",this.refPMHModule);
		module.initPanel();
		module.on("save", this.refresh, this);
		module.initDataId = null;
		module.exContext.control = {"create":false,"update":false};
		module.exContext.args.empiId = empiId;
		var win = module.getWin();
		var width = (Ext.getBody().getWidth()-990)/2
		win.setPosition(width, 10);
		win.show();
		module.loadData();
	}
});