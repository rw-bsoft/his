/**
 * 药库高低储提示
 * 
 * @author shiwy
 */
$package("phis.application.sto.script");
$import("phis.script.SimpleList");
phis.application.sto.script.StoreDepartmentConsumptionList = function(cfg) {
	cfg.modal = true;
	phis.application.sto.script.StoreDepartmentConsumptionList.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sto.script.StoreDepartmentConsumptionList,
		phis.script.SimpleList, {

		});