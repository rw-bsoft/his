$package("chis.application.hsr.script")
$import("chis.script.BizTableFormView");
chis.application.hsr.script.HSR_AssistantInformationForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 145;
	cfg.labelWidth = 80;
	cfg.width=700;
	chis.application.hsr.script.HSR_AssistantInformationForm.superclass.constructor
			.apply(this, [cfg]);
};
Ext.extend(chis.application.hsr.script.HSR_AssistantInformationForm,
		chis.script.BizTableFormView,{ 

	
});