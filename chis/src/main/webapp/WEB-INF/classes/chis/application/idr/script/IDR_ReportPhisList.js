$package("chis.application.idr.script")

$import("chis.script.BizSimpleListView");

chis.application.idr.script.IDR_ReportPhisList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	cfg.enableCnd = false;
	cfg.disablePagingTbr = false;// 分页工具栏
	cfg.width=260;
	chis.application.idr.script.IDR_ReportPhisList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.idr.script.IDR_ReportPhisList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.initCnd = ['eq',['$','a.empiId'],['s',this.exContext.ids.empiId || '']];
		this.requestData.cnd = this.initCnd;
		chis.application.idr.script.IDR_ReportPhisList.superclass.loadData.call(this);
	}
});