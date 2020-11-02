$package("chis.application.mzf.script");

$import("chis.script.BizSimpleListView");

chis.application.mzf.script.MZFVisitList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.mzf.script.MZFVisitList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.mzf.script.MZFVisitList ,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','b.empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		chis.application.mzf.script.MZFVisitList .superclass.loadData.call(this);
	}		
});