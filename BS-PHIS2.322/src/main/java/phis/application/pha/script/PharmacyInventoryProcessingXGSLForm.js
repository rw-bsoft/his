$package("phis.application.pha.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.pha.script.PharmacyInventoryProcessingXGSLForm = function(cfg) {
	//cfg.showButtonOnTop = false;
	phis.application.pha.script.PharmacyInventoryProcessingXGSLForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingXGSLForm, phis.script.TableForm, {
		
		})