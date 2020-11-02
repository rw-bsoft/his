$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HosCancelCommitList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.height = 181
	phis.application.hos.script.HosCancelCommitList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HosCancelCommitList,
		phis.script.SimpleList, {
		});