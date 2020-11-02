$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigBusinessLockMgmtList = function(cfg) {
	cfg.initCnd = ['eq', ['$', 'a.JGID'], ['$', '%user.manageUnitId']];
	phis.application.cfg.script.ConfigBusinessLockMgmtList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cfg.script.ConfigBusinessLockMgmtList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.cfg.script.ConfigBusinessLockMgmtList.superclass.onReady
						.call(this);
			}
		});