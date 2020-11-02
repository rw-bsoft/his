$package("chis.application.hsr.script")
$import("chis.script.BizTableFormView");
chis.application.hsr.script.HSR_AssistingRegistrationForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 145;
	cfg.labelWidth = 95;
	cfg.width=750; 
	chis.application.hsr.script.HSR_AssistingRegistrationForm.superclass.constructor
			.apply(this, [cfg]);
};
Ext.extend(chis.application.hsr.script.HSR_AssistingRegistrationForm,
		chis.script.BizTableFormView,{ 
});