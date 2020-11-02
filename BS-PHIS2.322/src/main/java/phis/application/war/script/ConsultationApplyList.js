$package("phis.application.war.script");

$import("app.biz.BizSimpleListView");

phis.application.war.script.ConsultationApplyList = function(cfg){
	phis.application.war.script.ConsultationApplyList.superclass.constructor.apply(this,[cfg]);
	this.disablePagingTbr = true;
};

Ext.extend(phis.application.war.script.ConsultationApplyList,app.biz.BizSimpleListView,{
	
});