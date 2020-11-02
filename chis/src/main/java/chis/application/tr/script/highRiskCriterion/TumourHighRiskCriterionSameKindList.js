$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizSimpleListView");

chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionSameKindList = function(cfg){
	chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionSameKindList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
    this.isCombined = true;
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionSameKindList,chis.script.BizSimpleListView,{
	loadData : function(){
		var highRiskType=this.exContext.args.highRiskType;
		if(typeof highRiskType=="object"){
			highRiskType=highRiskType.key;
		}
		this.requestData.cnd=['eq',['$','highRiskType'],['s',highRiskType||'']];
		chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionSameKindList.superclass.loadData.call(this);
	}
});