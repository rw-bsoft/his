$package("chis.application.fhr.script")
$import("chis.script.BizTableFormView")

chis.application.fhr.script.MasterplateMaintainForm = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 145
	cfg.width = cfg.width || 680;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.fhr.script.MasterplateMaintainForm.superclass.constructor.apply(this, [cfg]);
	this.saveServiceId="chis.templateService";
	this.saveAction="saveMasterplateMaintain";
}

Ext.extend(chis.application.fhr.script.MasterplateMaintainForm, chis.script.BizTableFormView, {
	doCancel : function() {
		this.fireEvent("cancel");
	},
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData);
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			Ext.Msg.alert("提示", "模版["
							+ saveRequest.masterplateName + "]已存在！")
			return;
		}
		if (!this.initDataId) {
			this.op = "create";
		} else {
			this.op = "update";
		}
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction,
					method:"execute",
					schema : this.entryName,
					op : this.op,
					body : saveRequest
				}, function(code, msg, json) {
					this.form.el.unmask();
					this.saving = false;
					var resBody = json.body;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], resBody);
						this.fireEvent("exception", code, msg, saveData);
						return;
					}
					Ext.apply(this.data, saveData);
					if (resBody) {
						this.initFormData(resBody);
					}
					this.fireEvent("save",this.entryName,this.op,json,this.data);
					this.afterSaveData(this.entryName, this.op, json,
									this.data);
					this.op = "update"
				}, this)
	}
});