$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.hos.script.HospitalCostsListForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop=false;
	cfg.labelWidth = 55;
	phis.application.hos.script.HospitalCostsListForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.hos.script.HospitalCostsListForm, phis.script.TableForm,
		{
			loadData : function(data) {
				if (this.loading) {
					return
				}
				if (!data) {
					return;
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPatientSelectionService",
							serviceAction : "getSelectionForm",
							body : data
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew()
								// Ext.apply(json.body, data)
								if(json.body.RYRQ){
									json.body.RYRQ = json.body.RYRQ.substring(0,10);
								}
								if(json.body.CYRQ){
									json.body.CYRQ = json.body.CYRQ.substring(0,10);
								}
								console.log(json.body);
								this.initFormData(json.body)
							}
						}, this)// jsonRequest
			}
		});