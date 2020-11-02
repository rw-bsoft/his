$package("chis.application.fhr.script")
$import("chis.script.BizTableFormView")

chis.application.fhr.script.DictionaryMaintainForm = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.fhr.script.DictionaryMaintainForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.fhr.script.DictionaryMaintainForm, chis.script.BizTableFormView, {
	onWinShow : function() {
		if (this.op == "update") {
			this.initFormData(this.formData);
		}
		if (this.hasUsed == true) {
			this.setFormButton(false);
		} else {
			this.setFormButton(true);
		}
	},
	setFormButton : function(flag) {
		var btns = this.form.getTopToolbar().items
		var n = btns.getCount()
		for (var i = 0; i < n; i++) {
			var btn = btns.item(i);
			if (!flag) {
				btn.disable();
			} else {
				btn.enable();
			}
		}
	},
	saveToServer : function(saveData) {
		saveData.fieldId = this.fieldId;
		var saveRequest = this.getSaveRequest(saveData);
		if (!saveRequest) {
			return;
		}
		if (!this.validate()) {
			return
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			Ext.Msg.alert("提示", "字典" + saveRequest.text + "的代码["
							+ saveRequest.keys + "]已存在！")
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
					serviceId : "chis.templateService",
					serviceAction : "saveDicMaintain",
					schema : this.entryName,
					method:"execute",
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
					if (this.win) {
						this.win.hide();
					}
				}, this)
	}
});