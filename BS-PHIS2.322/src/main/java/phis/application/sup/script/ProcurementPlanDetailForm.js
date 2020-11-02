$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.ProcurementPlanDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.width = 400;
	phis.application.sup.script.ProcurementPlanDetailForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ProcurementPlanDetailForm,
		phis.script.TableForm, {
			
		})