$package("chis.application.tr.script.pmh");

$import("chis.script.BizSimpleListView");

chis.application.tr.script.pmh.TumourPastMedicalHistoryList = function(cfg){
	chis.application.tr.script.pmh.TumourPastMedicalHistoryList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadData = false;
	this.initCnd = ['eq',['$','empiId'],['s',this.exContext.args.empiId||this.empiId||'']];
}

Ext.extend(chis.application.tr.script.pmh.TumourPastMedicalHistoryList,chis.script.BizSimpleListView,{
	
});