$package("chis.application.hy.script.inquire");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.inquire.HypertensionInquireList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.hy.script.inquire.HypertensionInquireList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.hy.script.inquire.HypertensionInquireList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		chis.application.hy.script.inquire.HypertensionInquireList.superclass.loadData.call(this);
	}
	
	
});