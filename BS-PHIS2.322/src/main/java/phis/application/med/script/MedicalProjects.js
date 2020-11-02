$package("phis.application.med.script");
$import("phis.script.SimpleList","phis.script.widgets.MyMessageTip", "phis.script.util.DateUtil");
phis.application.med.script.MedicalProjects = function(cfg) {
	
	phis.application.med.script.MedicalProjects.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.med.script.MedicalProjects,
		phis.script.SimpleList, {
			
			
		
});