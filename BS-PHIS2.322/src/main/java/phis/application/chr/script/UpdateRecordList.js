$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.UpdateRecordList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.UpdateRecordList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.UpdateRecordList,
		phis.script.SimpleList, {
		});