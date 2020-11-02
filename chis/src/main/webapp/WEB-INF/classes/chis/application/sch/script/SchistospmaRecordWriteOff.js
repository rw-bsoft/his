$package("chis.application.sch.script")

$import("chis.script.BizTableFormView")

chis.application.sch.script.SchistospmaRecordWriteOff = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.sch.script.SchistospmaRecordWriteOff.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this)
}

Ext.extend(chis.application.sch.script.SchistospmaRecordWriteOff,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.sch.script.SchistospmaRecordWriteOff.superclass.onReady
						.call(this);
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason");
				if (cancellationReason) {
					cancellationReason.on("select", function(field) {
								var value = field.getValue();
								this.setDeadReason(value);
							}, this);
				}
				cancellationReason.on("blur", function(field) {
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

			onSave : function(entryName, op, json, data) {
				this.fireEvent("remove");
				this.getWin().hide();
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
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason").getValue();
				var msg;
				if (cancellationReason == '1' || cancellationReason == '2') {
					msg = "死亡或迁出将注销" + this.record.get("personName")
							+ "的所有档案，是否确定继续？";
				} else if (cancellationReason == '6') {
					msg = this.record.get("personName")
							+ "的该笔血吸虫档案将被作废且不能恢复，是否确定继续？";
				} else {
					msg = this.record.get("personName")
							+ "的所有血吸虫档案将被注销，是否确定继续？";
				}
				Ext.Msg.show({
							title : this.title,
							msg : msg,
							nodal : true,
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
				saveData["phrId"] = this.record.data.phrId;
				saveData["schisRecordId"] = this.record.id;
				var cancellationReason = saveData.cancellationReason;
				if (cancellationReason == "1") {
					saveData.deadFlag = "y"
				}
				this.saveServiceId = "chis.schistospmaService";
				this.saveAction = "logoutSchistospmaRecord";
				if (cancellationReason == '1' || cancellationReason == '2') {
					this.saveServiceId = "chis.healthRecordService";
					this.saveAction = "logoutAllRecords";
				}
				if (cancellationReason == '6') {
					this.saveServiceId = "chis.schistospmaService";
					this.saveAction = "singleRecordLogout";
				}
				return saveData;
			},

			onWinShow : function() {
				this.doNew();
				var deadReason = this.form.getForm().findField("deadReason");
				if (deadReason) {
					deadReason.reset();
					deadReason.disable();
					deadReason.allowBlank = true;
				}

				this.form.getForm().findField("cancellationDate")
						.setValue(this.mainApp.serverDate);
				this.form.getForm().findField("cancellationReason")
						.setValue(this.record.get("cancellationReason"));
				this.form.getForm().findField("cancellationUser").setValue({
							key : this.mainApp.uid,
							text : this.mainApp.uname
						});

				this.validate();
			}
		})