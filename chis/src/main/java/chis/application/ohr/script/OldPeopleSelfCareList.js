$package("chis.application.ohr.script");

$import("chis.script.BizSimpleListView");

chis.application.ohr.script.OldPeopleSelfCareList=function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	chis.application.ohr.script.OldPeopleSelfCareList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.ohr.script.OldPeopleSelfCareList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','empiId'],['s',this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		chis.application.ohr.script.OldPeopleSelfCareList.superclass.loadData.call(this);
	}
});