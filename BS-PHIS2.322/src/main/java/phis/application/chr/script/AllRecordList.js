$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.AllRecordList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.AllRecordList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.chr.script.AllRecordList, phis.script.SimpleList, {
		});
