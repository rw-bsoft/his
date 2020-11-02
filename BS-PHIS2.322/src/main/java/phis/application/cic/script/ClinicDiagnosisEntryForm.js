$package("phis.application.cic.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cic.script.ClinicDiagnosisEntryForm = function(cfg) {
	cfg.colCount = 2;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 200;
	phis.application.cic.script.ClinicDiagnosisEntryForm.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicDiagnosisEntryForm,
		phis.script.TableForm, {
		});