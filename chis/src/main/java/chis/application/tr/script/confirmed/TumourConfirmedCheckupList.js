$package("chis.application.tr.script.confirmed")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.confirmed.TumourConfirmedCheckupList = function(cfg){
	chis.application.tr.script.confirmed.TumourConfirmedCheckupList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.showButtonOnTop = false;
	this.showButtonOnPT = true;
	this.disablePagingTbr = true;
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedCheckupList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd=['and',['eq',['$','empiId'],['s',this.exContext.ids.empiId||'']],['eq',['$','highRiskType'],['s',this.exContext.args.highRiskType||'']]]
		chis.application.tr.script.confirmed.TumourConfirmedCheckupList.superclass.loadData.call(this);
	}
});