$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.PrintRecordList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.PrintRecordList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.PrintRecordList,
		phis.script.SimpleList, {
		});