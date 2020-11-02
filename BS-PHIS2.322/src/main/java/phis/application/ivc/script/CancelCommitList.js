$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.CancelCommitList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.height = 181
	phis.application.ivc.script.CancelCommitList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.CancelCommitList,
		phis.script.SimpleList, {
		});