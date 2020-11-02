$package("phis.application.pha.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.pha.script.PharmacyInventoryProcessingWcForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.width=700;
	phis.application.pha.script.PharmacyInventoryProcessingWcForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingWcForm,
		phis.script.TableForm, {
			doCancel : function() {
				this.getWin().hide();
			},
			doSave : function() {
				this.fireEvent("save", this);
				this.doCancel();
			},
			loadData : function(body) {
				if (this.loading) {
					return
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
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
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

						}, this)// jsonRequest
			}

		})