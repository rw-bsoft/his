$package("phis.application.pha.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.pha.script.PharmacyInventoryProcessingRightModuleTopForm = function(cfg) {
	this.showButtonOnTop = false;
	cfg.showButtonOnTop = false;
	phis.application.pha.script.PharmacyInventoryProcessingRightModuleTopForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingRightModuleTopForm, phis.script.TableForm, {
			
		})