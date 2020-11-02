/**
 * WHO标准-年龄别身高，体重，头围表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.who")
$import("chis.script.BizTableFormView")
chis.application.pd.script.who.WHOAgeForm = function(cfg) {
	cfg.colCount = 2
	cfg.width = 540
	cfg.labelWidth = 120
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	chis.application.pd.script.who.WHOAgeForm.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.who.WHOAgeForm, chis.script.BizTableFormView, {});