$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedCancelCommitList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.height = 181
	phis.application.fsb.script.FamilySickBedCancelCommitList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedCancelCommitList,
		phis.script.SimpleList, {
		});