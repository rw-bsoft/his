$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleLeftList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleLeftList, phis.script.SimpleList, {
			
		})