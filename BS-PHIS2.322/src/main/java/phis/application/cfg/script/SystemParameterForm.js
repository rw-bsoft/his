$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.SystemParameterForm = function(cfg) {
	cfg.colCount = 2;
	cfg.saveServiceId = "systemParamsSave";
	phis.application.cfg.script.SystemParameterForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.cfg.script.SystemParameterForm,
				phis.script.TableForm, {
				
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}
		if((saveRequest.CSMC=="ZYFPSFZCGY"&&saveRequest.CSZ==1)||(saveRequest.CSMC=="JKSJSFZCGY"&&saveRequest.CSZ==1)){
			var pjs = phis.script.rmi.miniJsonRequestSync({
				serviceId : "configSystemParameterService",
				serviceAction : "queryAblePjGyQy",
				CSMC : saveRequest.CSMC
			});
			if(pjs.code==600){
				MyMessageTip.msg("提示", pjs.msg, true);
				return;
			}
		}
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		phis.script.rmi.jsonRequest({
					serviceId : "systemParamsSave",
					serviceAction : this.saveAction || "",
					method : "execute",
					op : this.op,
					schema : this.entryName,
					module : this._mId, // 增加module的id
					body : saveRequest
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], json.body);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					this.op = "update"
					MyMessageTip.msg("提示", "保存成功!", true)
				}, this)// jsonRequest
	}
				})