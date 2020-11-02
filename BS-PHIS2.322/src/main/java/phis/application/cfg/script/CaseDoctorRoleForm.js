$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.CaseDoctorRoleForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 400;
	this.serviceId = "caseHistoryControlService";
	this.actionId = "saveUserManage";
	phis.application.cfg.script.CaseDoctorRoleForm.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.cfg.script.CaseDoctorRoleForm, phis.script.TableForm, {
	
	saveToServer : function(saveData) {
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
			MyMessageTip.msg("提示", "医疗角色名称已存在！", true);
			return;
		}
		if (this.initDataId == null) {
			this.op = "create";
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
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData]);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					this.op = "update"
				}, this)// jsonRequest
	}
})