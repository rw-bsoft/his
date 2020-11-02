$package("chis.application.cvd.script")
$import("chis.script.common.form.SuperFormView")
chis.application.cvd.script.DiseaseVerification = function(cfg) {
	chis.application.cvd.script.DiseaseVerification.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.cvd.script.DiseaseVerification,
		chis.script.common.form.SuperFormView, {
		
});