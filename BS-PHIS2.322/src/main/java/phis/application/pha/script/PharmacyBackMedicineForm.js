$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyBackMedicineForm = function(cfg) {
	cfg.height=this.height=100
	cfg.width = this.width=1000
	cfg.showButtonOnTop = false;
	phis.application.pha.script.PharmacyBackMedicineForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyBackMedicineForm,
		phis.script.TableForm, {
		
		//由于form不支持多表查询,查不出病人姓名 故重写
			loadData : function(cflx) {
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
				var body = {};
				body["cfsb"] = this.initDataId;
				this.loading = true
				var form = this.form.getForm()
				if(cflx==3){
					form.findField("CFTS").show();
				}else{
				form.findField("CFTS").hide();
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction,
							body : body
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