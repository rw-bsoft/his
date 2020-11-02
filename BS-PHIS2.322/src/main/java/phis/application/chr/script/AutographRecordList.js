$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.AutographRecordList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.AutographRecordList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.AutographRecordList,
		phis.script.SimpleList, {
		});