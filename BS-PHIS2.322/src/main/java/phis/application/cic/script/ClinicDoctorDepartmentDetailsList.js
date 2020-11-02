$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicDoctorDepartmentDetailsList = function(cfg) {
	phis.application.cic.script.ClinicDoctorDepartmentDetailsList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.ClinicDoctorDepartmentDetailsList,
		phis.script.SimpleList,{
			doNew : function() {
				if (this.win) {
					this.win.show();
					//this.win.center();
					//this.win.setPosition(300,80);
					return;
				}
				var module = this.createModule("ClinicDepartmentForm",
						"CIC1103");
				this.module = module;
				this.win = module.getWin();
				this.win.add(module.initPanel());
				this.win.show();
				//this.win.setPosition(300,80);
			}
		});