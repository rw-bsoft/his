$package("phis.application.med.script");
$import("phis.script.SimpleList","phis.script.widgets.MyMessageTip", "phis.script.util.DateUtil");
phis.application.med.script.MedicalProjectApplication = function(cfg) {
	cfg.listServiceId="medicalTechnicalSectionService",
	cfg.serverParams={serviceAction:"QueryProject"}
	
	phis.application.med.script.MedicalProjectApplication.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.med.script.MedicalProjectApplication,
		phis.script.SimpleList, {
			
			testrefresh:function(ksdm){
				alert(ksdm);
			}
		
});