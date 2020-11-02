$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.InventoryInEjForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.InventoryInEjForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.InventoryInEjForm, phis.script.TableForm, {

})