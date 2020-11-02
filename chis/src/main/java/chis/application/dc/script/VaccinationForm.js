$package("chis.application.dc.script");

$import("chis.script.BizTableFormView");

chis.application.dc.script.VaccinationForm = function(cfg) {
	cfg.colCount = 2;
	cfg.showButtonOnTop = true;
	cfg.autoLoadSchema= true;
	this.op=cfg.op;
	chis.application.dc.script.VaccinationForm.superclass.constructor.apply(this,
			[cfg]);

};

Ext.extend(chis.application.dc.script.VaccinationForm, chis.script.BizTableFormView, {
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : "chis.rabiesRecordService",
					serviceAction : "saveVaccination",
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
					this.fireEvent("save", this.entryName, this.op, json,
							this.data);
					this.op = "update"
				}, this)
	},
	getSaveRequest : function(saveData) {
		var values = saveData;
		if(this.exContext.args.rabiesId){
			values.rabiesID=this.exContext.args.rabiesId;
		}
		return values;
	}
});