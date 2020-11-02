$package("chis.application.sch.script")

$import("chis.script.BizSimpleListView")

chis.application.sch.script.SchistospmaVisitList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	chis.application.sch.script.SchistospmaVisitList.superclass.constructor.apply(this, [cfg]);

}

Ext.extend(chis.application.sch.script.SchistospmaVisitList, chis.script.BizSimpleListView, {
	loadData : function() {
		this.requestData.cnd = ['eq', ['$', "a.schisRecordId"],
				['s', this.exContext.args.schisRecordId]]
		chis.application.sch.script.SchistospmaRecordList.superclass.loadData.call(this);
	}

	});