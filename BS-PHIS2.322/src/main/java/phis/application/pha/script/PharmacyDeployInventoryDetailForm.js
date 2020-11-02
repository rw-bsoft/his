$package("phis.application.pha.script")

$import("phis.application.pha.script.PharmacyMySimpleDetailForm", "phis.script.util.DateUtil")

phis.application.pha.script.PharmacyDeployInventoryDetailForm = function(cfg) {
	cfg.conditionId = "SQYF";
	cfg.disabledField=["SQRQ","BZXX"];
	phis.application.pha.script.PharmacyDeployInventoryDetailForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyDeployInventoryDetailForm, phis.application.pha.script.PharmacyMySimpleDetailForm, {
			
		})