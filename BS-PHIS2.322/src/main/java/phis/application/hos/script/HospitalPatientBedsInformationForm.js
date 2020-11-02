$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.hos.script.HospitalPatientBedsInformationForm = function(cfg) {
	cfg.colCount = 2;
	cfg.autoFieldWidth=false;
	cfg.fldDefaultWidth=250;
	cfg.showButtonOnTop = false;
	phis.application.hos.script.HospitalPatientBedsInformationForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalPatientBedsInformationForm,
		phis.script.TableForm, {});