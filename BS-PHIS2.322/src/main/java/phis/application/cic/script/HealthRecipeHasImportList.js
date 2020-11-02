$package("phis.application.cic.script")

$import("phis.script.SimpleList")
phis.application.cic.script.HealthRecipeHasImportList = function(cfg) {
	phis.application.cic.script.HealthRecipeHasImportList.superclass.constructor
			.apply(this, [cfg]);
	this.autoLoadData = false;
	this.disablePagingTbr=true;
}
Ext.extend(phis.application.cic.script.HealthRecipeHasImportList,
		phis.script.SimpleList, {
			
		});