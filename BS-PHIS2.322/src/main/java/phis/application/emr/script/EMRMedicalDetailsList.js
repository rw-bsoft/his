$package("phis.application.emr.script");

$import("phis.script.SimpleList");
phis.application.emr.script.EMRMedicalDetailsList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = true;
	cfg.enableCnd=false;
	phis.application.emr.script.EMRMedicalDetailsList.superclass.constructor.apply(this,
			[cfg]);
},

Ext.extend(phis.application.emr.script.EMRMedicalDetailsList, phis.script.SimpleList, {
		});