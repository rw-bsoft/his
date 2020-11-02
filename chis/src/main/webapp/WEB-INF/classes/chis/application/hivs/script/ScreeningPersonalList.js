$package("chis.application.hivs.script");

$import("chis.script.BizSimpleListView");

chis.application.hivs.script.ScreeningPersonalList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.hivs.script.ScreeningPersonalList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.hivs.script.ScreeningPersonalList ,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','b.empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		if (this.mainApp.jobtitleId == "chis.01") {
			this.requestData.schema = "chis.application.hivs.schemas.HIVS_Screening_01";
		}
		chis.application.hivs.script.ScreeningPersonalList .superclass.loadData.call(this);
	}		
});


