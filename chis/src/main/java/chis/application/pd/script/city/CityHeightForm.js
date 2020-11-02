/**
 * 9市标准-身高别体重表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.city")
$import("chis.script.BizTableFormView")
chis.application.pd.script.city.CityHeightForm = function(cfg) {
	cfg.colCount = 2
	cfg.width = 540
	cfg.labelWidth = 120
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	chis.application.pd.script.city.CityHeightForm.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.city.CityHeightForm, chis.script.BizTableFormView, {});