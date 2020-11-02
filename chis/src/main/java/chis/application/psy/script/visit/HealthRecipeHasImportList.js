$package("chis.application.psy.script.visit")

$import("chis.script.BizSimpleListView")
chis.application.psy.script.visit.HealthRecipeHasImportList = function(cfg) {
	chis.application.psy.script.visit.HealthRecipeHasImportList.superclass.constructor
			.apply(this, [cfg]);
	this.autoLoadData = false;
	this.disablePagingTbr=true;
}
Ext.extend(chis.application.psy.script.visit.HealthRecipeHasImportList,
		chis.script.BizSimpleListView, {
			
		});