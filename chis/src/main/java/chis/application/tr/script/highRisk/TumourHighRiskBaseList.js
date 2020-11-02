$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.highRisk.TumourHighRiskBaseList = function(cfg){
	chis.application.tr.script.highRisk.TumourHighRiskBaseList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.showButtonOnTop = false;
	this.showButtonOnPT = true;
	this.disablePagingTbr = true;
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskBaseList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd=['and',['eq',['$','empiId'],['s',this.exContext.ids.empiId||'']],['eq',['$','highRiskType'],['s',this.exContext.args.highRiskType||'']]]
		chis.application.tr.script.highRisk.TumourHighRiskBaseList.superclass.loadData.call(this);
	}
});