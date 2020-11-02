$package("phis.application.pcm.script")

$import("phis.script.TableForm")

phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleTopForm = function(cfg) {
	cfg.autoLoadData = false;
	this.labelWidth = 55
	//cfg.colCount=4
	phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleTopForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleTopForm,
		phis.script.TableForm, {
			
		})