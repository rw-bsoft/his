$package("chis.application.tb.script");

$import("chis.script.BizSimpleListView");

chis.application.tb.script.TuberculosisVisitList = function(cfg) {
	cfg.autoLoadData = true;
	chis.application.tb.script.TuberculosisVisitList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.tb.script.TuberculosisVisitList,
		chis.script.BizSimpleListView, {
            
	loadData : function() {
		this.initCnd = ['and',
							['eq',['$', 'a.RecordId'],['s',this.exContext.args.recordId]]
						]
		this.requestData.cnd = this.initCnd
		chis.application.tb.script.TuberculosisVisitList.superclass.loadData.call(this);
	}
});