$package("chis.application.hy.script.baseline");

$import("chis.script.BizSimpleListView");
chis.application.hy.script.baseline.HyBaselinePersonalList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.hy.script.baseline.HyBaselinePersonalList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.hy.script.baseline.HyBaselinePersonalList ,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','b.empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		chis.application.hy.script.baseline.HyBaselinePersonalList.superclass.loadData.call(this);
	}		
});