/**
 * WHO标准-身高别体重表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.who")
$import("chis.script.BizTableFormView")
chis.application.pd.script.who.WHOHeightForm = function(cfg) {
	cfg.colCount = 2
	cfg.width = 540
	cfg.labelWidth = 120
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	chis.application.pd.script.who.WHOHeightForm.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.who.WHOHeightForm, chis.script.BizTableFormView, {});