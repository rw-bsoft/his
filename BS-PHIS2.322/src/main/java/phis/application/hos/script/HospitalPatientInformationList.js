$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalPatientInformationList = function(cfg) {
	phis.application.hos.script.HospitalPatientInformationList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalPatientInformationList,
		phis.script.SimpleList, {

		});