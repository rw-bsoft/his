$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicPersonalComboNameForm = function(cfg) {
	phis.application.cic.script.ClinicPersonalComboNameForm.superclass.constructor
			.apply(this, [cfg]);
			
}

Ext.extend(phis.application.cic.script.ClinicPersonalComboNameForm,
		phis.script.TableForm, {
			doSave : function(){
			alert(this.mainApp.zzz);
			}
		});