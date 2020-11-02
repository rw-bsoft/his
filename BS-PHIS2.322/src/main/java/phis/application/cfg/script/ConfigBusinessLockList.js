$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigBusinessLockList = function(cfg) {
	phis.application.cfg.script.ConfigBusinessLockList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cfg.script.ConfigBusinessLockList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.cfg.script.ConfigBusinessLockList.superclass.onReady
						.call(this);
			}
		});