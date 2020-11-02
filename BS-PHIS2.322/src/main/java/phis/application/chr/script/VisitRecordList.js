$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.VisitRecordList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.VisitRecordList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.VisitRecordList,
		phis.script.SimpleList, {
		});