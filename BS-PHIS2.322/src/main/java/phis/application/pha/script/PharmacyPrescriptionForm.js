$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyPrescriptionForm = function(cfg) {
	cfg.height=this.height=100
	cfg.width = this.width=1000
	cfg.labelWidth=60;
	cfg.showButtonOnTop = false;
	cfg.entryName="phis.application.pha.schemas.YF_CF01_SH_FORM";
	cfg.serviceId="pharmacyManageService";
	cfg.queryServiceAction="queryPrescriptionDetail";
	phis.application.pha.script.PharmacyPrescriptionForm.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyPrescriptionForm, phis.script.TableForm, {
	loadData : function(data) {
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
				var form = this.form.getForm();
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.queryServiceAction,
					body : {
						"cfsb" : data['CFSB'],
						"jgid" : data['JGID']
					}
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
})