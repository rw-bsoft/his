$package("phis.application.chr.script")

$import("phis.script.SimpleList")

phis.application.chr.script.QualityInfoList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.QualityInfoList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.QualityInfoList,
		phis.script.SimpleList, {
		});