$package("chis.application.psy.script.paper");

$import("chis.script.BizSimpleListView");

chis.application.psy.script.paper.RecordPaperList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.enableCnd = false;
	cfg.disablePagingTbr = true;// 分页工具栏
	chis.application.psy.script.paper.RecordPaperList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.psy.script.paper.RecordPaperList, chis.script.BizSimpleListView, {
	loadData : function(){
		this.requestData.cnd = ['eq', ['$', 'phrId'], ['s', this.exContext.ids.phrId]];
		chis.application.psy.script.paper.RecordPaperList.superclass.loadData.call(this);
	}
});