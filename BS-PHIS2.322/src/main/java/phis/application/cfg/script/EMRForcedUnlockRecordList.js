$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.EMRForcedUnlockRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	phis.application.cfg.script.EMRForcedUnlockRecordList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cfg.script.EMRForcedUnlockRecordList, phis.script.SimpleList, {
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			}
		});