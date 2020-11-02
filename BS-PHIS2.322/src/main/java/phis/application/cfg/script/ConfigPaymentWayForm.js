$package("phis.application.cfg.script")

/**
 * 付款方式维护from zhangyq 2012.5.25
 * 
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigPaymentWayForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width = 500;
	this.serviceId = "configPaymentTypeService";
	this.actionId = "savePayment";
	phis.application.cfg.script.ConfigPaymentWayForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.ConfigPaymentWayForm, phis.script.TableForm, {
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							method : "execute",
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data)
							this.op = "update"
						}, this)// jsonRequest
			}
		})