$package("phis.application.pha.script")

$import("phis.application.pha.script.PharmacyMySimpleDetailForm", "phis.script.util.DateUtil")

phis.application.pha.script.PharmacyRequisitionDetailForm = function(cfg) {
	cfg.conditionId = "MBYF";
	cfg.disabledField=["SQRQ","BZXX"];
	phis.application.pha.script.PharmacyRequisitionDetailForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyRequisitionDetailForm, phis.application.pha.script.PharmacyMySimpleDetailForm, {
			
		})