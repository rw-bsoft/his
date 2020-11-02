$package("phis.application.emr.script")

$import("phis.script.TableForm", "phis.script.widgets.ModuleQueryField")

phis.application.emr.script.EMRMedicalDetailsForm = function(cfg) {
	cfg.colCount = 2;
	cfg.showButtonOnTop = false;
	cfg.disAutoHeight=true;
	phis.application.emr.script.EMRMedicalDetailsForm.superclass.constructor.apply(this,
			[cfg]);
	//this.fldDefaultWidth = 120;
}

Ext.extend(phis.application.emr.script.EMRMedicalDetailsForm, phis.script.TableForm,
		{
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "medicalRecordsQueryService",
							serviceAction : "getMedicalRecordData",
							schema : this.entryName,
							pkey : this.initDataId
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
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)
			}
		});