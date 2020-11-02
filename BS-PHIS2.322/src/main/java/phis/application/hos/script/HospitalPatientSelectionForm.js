$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.hos.script.HospitalPatientSelectionForm = function(cfg) {
	phis.application.hos.script.HospitalPatientSelectionForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalPatientSelectionForm,
		phis.script.TableForm, {
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
						if(f.getName()!="JSLX"){
							var v = data[it.id]
							if (v != undefined) {
								f.setValue(v)
							}
							if (it.update == "false") {
								f.disable();
							}
						}
					}
				}
				this.setKeyReadOnly(true)
			},
			doChoose : function(data){
				this.initFormData(data);
			}
		});