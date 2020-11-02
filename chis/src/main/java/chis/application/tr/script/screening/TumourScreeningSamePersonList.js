$package("chis.application.tr.script.screening")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.screening.TumourScreeningSamePersonList=function(cfg){
	chis.application.tr.script.screening.TumourScreeningSamePersonList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
    this.isCombined = true;
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningSamePersonList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd=['eq',['$','empiId'],['s',this.exContext.args.empiId||'']];
		chis.application.tr.script.screening.TumourScreeningSamePersonList.superclass.loadData.call(this);
	}
});