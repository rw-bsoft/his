$package("phis.application.xnh.script");

$import("phis.script.SimpleList");

phis.application.xnh.script.NhFyxxCompareList = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhFyxxCompareList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhFyxxCompareList,
		phis.script.SimpleList, {
		});
