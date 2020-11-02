$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRForcedUnlockRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	phis.application.emr.script.EMRForcedUnlockRecordList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.emr.script.EMRForcedUnlockRecordList, phis.script.SimpleList, {
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			}
		});