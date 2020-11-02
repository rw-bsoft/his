/**
 * 老年人档案注销页面
 * 
 * @param {}
 *            cfg
 */
$package("chis.application.ohr.script")

$import("chis.script.BizTableFormView")

chis.application.ohr.script.OldpeopleRecordWriteOff = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.ohr.script.OldpeopleRecordWriteOff.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this)
}

Ext.extend(chis.application.ohr.script.OldpeopleRecordWriteOff, chis.script.BizTableFormView, {
	onReady : function() {
		chis.application.ohr.script.OldpeopleRecordWriteOff.superclass.onReady
				.call(this);
		var cancellationReason = this.form.getForm()
				.findField("cancellationReason");
		if (cancellationReason) {
			cancellationReason.on("select", function(field) {
						var value = field.getValue();
						this.setDeadReason(value);
					}, this);
		}
	},

	setDeadReason : function(value) {
		var items = this.schema.items;
		var form = this.form.getForm();
		if (value == 1) {// @@ 恢复原必填项。
			for (var i = 0; i < items.length; i++) {
				var field = form.findField(items[i].id);
				if (items[i].id == "deadReason"
						|| items[i].id == "deadDate") {
					field.allowBlank = false;
					field.enable();
					items[i]["not-null"] = true;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>"
									+ items[i].alias + ":</span>");
				}
			}
		} else {
			for (var i = 0; i < items.length; i++) {
				if (items[i].id == 'cancellationReason') {
					continue;
				}
				if (items[i].id == "deadReason" || items[i].id == "deadDate") {
					var field = form.findField(items[i].id);
					field.setValue();
				}
				var field = form.findField(items[i].id);
				if (field) {
					field.allowBlank = true;
					field.disable();
					items[i]["not-null"] = false;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update(items[i].alias + ":");
				}
			}
		}
		this.validate();
	},

	doSave : function() {
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		values["empiId"] = this.record.get("empiId");
		var title, msg;
		var value = this.form.getForm().findField("cancellationReason")
				.getValue();
		if (value == '1' || value == '2') {
			title = '档案注销';
			msg = '注销原因是迁出或死亡，将同时注销[' + this.record.get("personName")
					+ ']的所有相关档案，是否继续？”';
		} else {
			var title = '老年人档案注销';
			var msg = '[' + this.record.get("personName")
					+ ']的老年人档案将被注销，确定是否继续？';
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
							Ext.apply(this.data, values);
							this.saveToServer(values);
						}
					},
					scope : this
				});

	},

	getSaveRequest : function(saveData) {
		saveData["phrId"] = this.record.id;
		var cancellationReason = saveData.cancellationReason;
		if (cancellationReason == "1") {
			saveData.deadFlag = "y"
		}
		this.saveServiceId = "chis.oldPeopleRecordService";
		this.saveAction = "logoutOldPeopleRecord";
		if (cancellationReason == '1' || cancellationReason == '2') {
			this.saveServiceId = "chis.healthRecordService";
			this.saveAction = "logoutAllRecords";
		}
		return saveData;
	},

	onSave : function(entryName, op, json, data) {
		this.fireEvent("remove");
		this.getWin().hide();
	},

	onWinShow : function() {
		this.doNew();
		this.setDeadReason(0);
		this.validate();
	}
});
