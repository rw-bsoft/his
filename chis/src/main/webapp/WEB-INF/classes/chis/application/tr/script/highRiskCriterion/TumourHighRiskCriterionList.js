$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizSimpleListView");

chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionList = function(cfg){
	chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionList,chis.script.BizSimpleListView,{
	doCreateHRC : function(){
		var module = this.createSimpleModule("TumourHighRiskCriterionModule",this.refModule);
		module.initPanel();
		module.on("save", this.afterSave, this);
		module.initDataId = null;
		module.exContext.control = {};
		this.showWin(module);
		module.doNew();
	},
	afterSave : function(){
		this.refresh();
	},
	doModify : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var hrcId = r.get("hrcId");
		var highRiskType = r.get("highRiskType");
		var highRiskType_text = r.get("highRiskType_text");
		var module = this.createSimpleModule("TumourHighRiskCriterionModule",this.refModule);
		module.initPanel();
		module.on("save", this.afterSave, this);
		module.initDataId = null;
		module.exContext.control = {};
		module.exContext.args={
					hrcId:hrcId,
					highRiskType:highRiskType,
					highRiskType_text : highRiskType_text
				};
		this.showWin(module);
		module.loadData();
	},
	onDblClick : function(){
		this.doModify();
	},
	getRemoveRequest:function(r){
		var body = {};
		body.hrcId = r.get("hrcId");
		body.highRiskFactor = r.get("highRiskFactor");
		return body;
	}
});