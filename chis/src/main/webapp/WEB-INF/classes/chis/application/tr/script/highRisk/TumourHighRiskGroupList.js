$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.highRisk.TumourHighRiskGroupList = function(cfg){
	//cfg.initCnd = cfg.initCnd || ['eq',['$','empiId'],['s',cfg.exContext.ids.empiId||'']];
	chis.application.tr.script.highRisk.TumourHighRiskGroupList.superclass.constructor.apply(this,[cfg]);
	this.showButtonOnTop = true;
	//this.disablePagingTbr = true;
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskGroupList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd = ['eq',['$','empiId'],['s',this.exContext.ids.empiId||'']];
		chis.application.tr.script.highRisk.TumourHighRiskGroupList.superclass.loadData.call(this);
	}
});