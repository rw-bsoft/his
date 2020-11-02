$package("chis.application.pd.script.cdc")

$import("chis.script.BizTableFormView")

chis.application.pd.script.cdc.ChildrenDictCorrectionForm = function(cfg) {
	cfg.colCount = 2
	cfg.width = 640
	chis.application.pd.script.cdc.ChildrenDictCorrectionForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.cdc.ChildrenDictCorrectionForm,
		chis.script.BizTableFormView, {
      
		});