$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicPatientMedicalRecords = function(cfg) {
	phis.application.cic.script.ClinicPatientMedicalRecords.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicPatientMedicalRecords,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'column',
							width : 1000,
							defaults : {
								border : false
							},
							autoScroll :true,
							items : [this.getBLForm()]
						});
				this.panel = panel;
				return panel;
			},
			getBLForm : function() {
				var p = this.createModule("blform","CLINIC0201");
				return p.initPanel();
			},
			getZDList : function() {
				var exCfg = {columnWidth :'70%',fieldLabel :'初步诊断'};
				var p = this.createModule("zdlist","CLINIC03",exCfg);
				return p.initPanel();
			},
			getSpeLabel : function(labelName) {
				var label = new Ext.form.Label({
								fieldLabel : labelName
							});
				return label;
			}
		})
