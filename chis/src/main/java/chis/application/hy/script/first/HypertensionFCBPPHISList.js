$package("chis.application.hy.script.first");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.first.HypertensionFCBPPHISList = function(cfg) {
	chis.application.hy.script.first.HypertensionFCBPPHISList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
};

Ext.extend(chis.application.hy.script.first.HypertensionFCBPPHISList,
		chis.script.BizSimpleListView, {});