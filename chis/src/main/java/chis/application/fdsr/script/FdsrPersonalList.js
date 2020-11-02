$package("chis.application.fdsr.script");

$import("chis.script.BizSimpleListView");

chis.application.fdsr.script.FdsrPersonalList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.fdsr.script.FdsrPersonalList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.fdsr.script.FdsrPersonalList ,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','b.empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		if (this.mainApp.jobtitleId == "chis.01") {
			this.requestData.schema = "chis.application.fdsr.schemas.FDSR_01";
		}
		chis.application.fdsr.script.FdsrPersonalList .superclass.loadData.call(this);
	}		
});


