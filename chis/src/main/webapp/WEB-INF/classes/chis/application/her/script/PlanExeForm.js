$package("chis.application.her.script")

$import("chis.script.BizTableFormView");

chis.application.her.script.PlanExeForm = function(cfg) {
	cfg.colCount = 3;
	cfg.showButtonOnTop = false;
	chis.application.her.script.PlanExeForm.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.her.script.PlanExeForm, chis.script.BizTableFormView, {
	
})