$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalDetailsAccountingList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hos.script.HospitalDetailsAccountingList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalDetailsAccountingList, phis.script.SimpleList, {
			
		});