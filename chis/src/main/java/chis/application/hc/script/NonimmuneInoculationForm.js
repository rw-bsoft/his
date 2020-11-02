$package("app.bia.hc")
$import("chis.script.BizTableFormView")

chis.application.hc.script.NonimmuneInoculationForm = function(cfg) {
	chis.application.hc.script.NonimmuneInoculationForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(chis.application.hc.script.NonimmuneInoculationForm, chis.script.BizTableFormView, {
	
	getSaveRequest : function(saveData) {
		saveData.healthCheck = this.exContext.args.healthCheck;
		return saveData;
	}
	
})