$package("phis.application.war.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.war.script.WarTransferDeptForm = function(cfg) {

	cfg.colCount = 2;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 250;
	cfg.showButtonOnTop = false;
	phis.application.war.script.WarTransferDeptForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.war.script.WarTransferDeptForm, phis.script.TableForm, {
			getManaUnit : function(key) {
				var form = this.form.getForm();
				var hhks = form.findField("HHKS").getValue();

				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionQuery,
							body : {
								hhks : hhks
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var combox = this.form.getForm().findField("HHBQ");

							if (json.body && json.body.length > 0) {
								var body = json.body[0];

								if (body) {
									var manageUnit = body.KSDM;
									var manageUnitText = body.KSMC;
									if (manageUnit) {
										combox.setValue({
													key : manageUnit,
													text : manageUnitText
												})
									} else {
										combox.setValue({
													key : "",
													text : ""
												})
									}
								}
							} else {
								combox.setValue({
											key : "",
											text : ""
										})
							}
						}, this)
			},
			onReady : function() {

				var form = this.form.getForm();
				var newDoctorF = form.findField("HHKS");
				newDoctorF.on("select", function(combo, r, idx) {// 根据所选择的换后科室获取相应病区.
					if(!this.opener.HHBQ){
						this.getManaUnit(newDoctorF.getValue())
					}
				}, this)

			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v)
						}
						if (it.update == "false") {
							f.disable();
						}
					}
				}
				if (this.opener.HHKS) {
					var f = form.findField("HHKS")
					f.setValue(this.opener.HHKS)
					f.disable();
				}
				if (this.opener.HHYS) {
					var f = form.findField("HHYS")
					f.setValue(this.opener.HHYS)
					f.disable();
				}
				if (this.opener.HHBQ) {
					var f = form.findField("HHBQ")
					f.setValue(this.opener.HHBQ)
					f.disable();
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			}
		});