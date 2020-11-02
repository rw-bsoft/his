$package("chis.application.cvd.script")

$import("chis.script.BizTableFormView")

chis.application.cvd.script.CategoryForm = function(cfg) {
	cfg.colCount = 1;
	cfg.fldDefaultWidth = 300
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.width=450;
	chis.application.cvd.script.CategoryForm.superclass.constructor.apply(this, [cfg]);
	this.saveServiceId = "chis.categorySave";
	this.saveAction = "saveCategory";
}
Ext.extend(chis.application.cvd.script.CategoryForm, chis.script.BizTableFormView, {
	
})