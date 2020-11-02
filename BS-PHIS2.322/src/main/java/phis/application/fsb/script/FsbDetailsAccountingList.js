$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbDetailsAccountingList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FsbDetailsAccountingList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.fsb.script.FsbDetailsAccountingList, phis.script.SimpleList, {
			
		});