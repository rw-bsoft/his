$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyIncompleteList = function(cfg) {
	cfg.autoLoadData=false;
	cfg.disablePagingTbr=true;
	phis.application.pha.script.PharmacyIncompleteList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyIncompleteList,
		phis.script.SimpleList, {
			
		});