$package("chis.application.psy.script.assessment");

$import("chis.script.BizSimpleListView");

chis.application.psy.script.assessment.AnnualAssessmentList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.enableCnd = false;
	cfg.disablePagingTbr = true;// 分页工具栏
	chis.application.psy.script.assessment.AnnualAssessmentList.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.psy.script.assessment.AnnualAssessmentList, chis.script.BizSimpleListView, {
	loadData : function(){
		this.requestData.cnd = ['eq', ['$', 'empiId'], ['s', this.exContext.ids.empiId]];
		chis.application.psy.script.assessment.AnnualAssessmentList.superclass.loadData.call(this);
	}
});