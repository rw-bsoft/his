$package("chis.application.scm.script");

$import("chis.script.BizSimpleListView");

chis.application.scm.script.JYFWPersonalList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.scm.script.JYFWPersonalList.superclass.constructor.apply(this,[cfg]);
	
};

Ext.extend(chis.application.scm.script.JYFWPersonalList ,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','b.empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		if (this.mainApp.jobtitleId == "chis.01") {
			this.requestData.schema = "chis.application.scm.schemas.JYFWJL_01";
		}
		chis.application.scm.script.JYFWPersonalList .superclass.loadData.call(this);
	}		
});


