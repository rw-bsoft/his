$package("phis.application.cic.script");

$import("phis.script.TableForm", "phis.script.util.DateUtil");

phis.application.cic.script.ClinicHospitalAppointmentForm = function(cfg) {
	cfg.colCount = 4;
	phis.application.cic.script.ClinicHospitalAppointmentForm.superclass.constructor
			.apply(this, [ cfg ]);
};
Ext
		.extend(
				phis.application.cic.script.ClinicHospitalAppointmentForm,
				phis.script.TableForm,
				{
					onReady : function() {
						phis.application.cic.script.ClinicHospitalAppointmentForm.superclass.onReady
								.call(this);
						this.form.getForm().findField("YYRQ").setValue(
								Date.getServerDate());
						this.form.getForm().findField("YYRQ").on("select",
								this.onYyrqselect, this)
						if (!this.birthday || this.birthday == "") {
							return 0;
						}
						var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "personAge",
							body : {
								birthday : this.birthday + "T00:00:00.000Z"
							}
						});
						var body = null;
						if (result.json.body) {
							body = result.json.body;
						}
						this.form.getForm().findField("NL").setValue(body.age);
					},
					afterDoNew : function() {
						var resultzdmc = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicHospitalAppointmentService",
							serviceAction : "queryZDMC",
							jzxh : this.jzxh
						});
						this.form.getForm().findField("ZDMC").setValue(
								resultzdmc.json.ZDMC);
					},
					onYyrqselect:function(yyrq){
						this.fireEvent("loadList", yyrq.getValue())
					}
				});