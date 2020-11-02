$package("chis.application.dc.script")

$import("chis.script.BizTableFormView", "util.Accredit")

chis.application.dc.script.RabiesRecordLogoutForm = function(cfg) {
	chis.application.dc.script.RabiesRecordLogoutForm.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.dc.script.RabiesRecordLogoutForm, chis.script.BizTableFormView, {

	onReady : function() {
		chis.application.dc.script.RabiesRecordLogoutForm.superclass.onReady.call(this);
		var cancellationReason = this.form.getForm()
				.findField("cancellationReason");
		if (cancellationReason)
			cancellationReason.on("select", function(field) {
						var value = field.getValue();
						this.setDeadReason(value);
					}, this);
		var cancellationUser = this.form.getForm()
				.findField("cancellationUser");
		if (cancellationUser) {
			cancellationUser.disable();
		}
		var cancellationDate = this.form.getForm()
				.findField("cancellationDate");
		if (cancellationDate) {
			cancellationDate.disable();
		}
	},
	setDeadReason : function(value) {
		var deadReason = this.form.getForm().findField("deadReason");
		var deadDate = this.form.getForm().findField("deadDate");
		if (value == "1") {
			deadReason.enable();
			deadReason.allowBlank = false;

			deadDate.enable();
			deadDate.allowBlank = false;
		} else {
			deadReason.reset();
			deadReason.disable();
			deadReason.allowBlank = true;

			deadDate.reset();
			deadDate.disable();
			deadDate.allowBlank = true;
		}
		deadReason.validate();
		deadDate.validate();
	},

	doSave : function() {
		var values =  this.listData;
		var data=this.getFormData();
		Ext.apply(values,data);
		var title;
		var msg;
		var cancellationReason=values.cancellationReason;
		if (cancellationReason== '1'
				|| cancellationReason == '2') {
			title = '档案注销';
			msg = '注销原因是迁出或死亡，将同时注销['+values.personName +']所有相关档案，是否继续？';
		}else if (cancellationReason == '6'){
			var title = '狂犬病档案注销';
			var msg = values.personName + '的该狂犬病档案将被作废且不能恢复，确定是否继续？';
		} else {
			var title = '狂犬病档案注销';
			var msg = values.personName + '的所有狂犬病档案将被注销，确定是否继续？';
		}
		Ext.Msg.show({
					title : title,
					msg : msg,
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.saveToServer(values);
						}
					},
					scope : this
				});
	},
	onSave : function() {
		this.fireEvent("remove");
		this.getWin().hide();
	},
	getSaveRequest : function(saveData) {
		if (saveData.cancellationReason == "1") {
			saveData.deadFlag = "y"
		}
		this.saveServiceId = "chis.rabiesRecordService";
		this.saveAction = "logoutRabiesRecord";
		if(saveData.cancellationReason == '6'){
			this.saveAction = "logoutRabiesRecordThis";
		}
		if (saveData.cancellationReason == '1' || saveData.cancellationReason == '2') {
			this.saveServiceId = "chis.healthRecordService";
			this.saveAction = "logoutAllRecords";
		}
		return saveData;
	},

	onWinShow : function() {
		this.doNew()
	},
	saveToServer : function(saveData) {
		saveData["status"] = "2";
		var saveRequest = this.getSaveRequest(saveData);
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction,
					method:"execute",
					body : saveRequest,
					op : "update",
					schema : "chis.application.dc.schemas.DC_RabiesRecord"
				}, function(code, msg, json) {
					this.form.el.unmask();
					this.saving = false;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveRequest]);
						return
					}
					Ext.apply(this.data, saveRequest);
					this.doCancel();
					this.fireEvent("save", this.entryName, this.op, json,
							this.data);

					this.op = "update";
				}, this);// jsonRequest
	}


});
