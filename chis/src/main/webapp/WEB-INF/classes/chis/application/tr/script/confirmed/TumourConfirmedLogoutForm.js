$package("chis.application.tr.script.confirmed")

$import("chis.script.BizTableFormView")

chis.application.tr.script.confirmed.TumourConfirmedLogoutForm = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.entryName = "chis.application.pub.schemas.EHR_WriteOff";
	cfg.saveServiceId = "chis.tumourConfirmedService";
	cfg.saveAction = "logoutTumourConfirmedRecord";
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.tr.script.confirmed.TumourConfirmedLogoutForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedLogoutForm,
		chis.script.BizTableFormView, {
			onWinShow : function() {
				this.doNew();
				this.setDeadReason(0);
				this.validate();
			},

			onReady : function() {
				chis.application.tr.script.confirmed.TumourConfirmedLogoutForm.superclass.onReady
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

			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values["TCID"] = this.TCID;
				values["empiId"] = this.empiId;
				values["phrId"] = this.phrId;
				values["highRiskType"]=this.highRiskType;
				var cancellationReason = values.cancellationReason;
				if(cancellationReason=="1"){
					values["deadFlag"]="y";
				}
				Ext.Msg.show({
							title : '档案注销',
							msg : '[' + this.personName
									+ ']的档案注销后将无法操作，确定是否继续？',
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
			saveToServer : function(saveData) {
				var cancellationReason = saveData.cancellationReason;
				if (cancellationReason == '1' || cancellationReason == '2') {
					// 注销原因 为 死亡 或 迁出 调用健康档案注销全部档案
					Ext.Msg.show({
								title : '确认注销[' + this.personName + ']的所有档案',
								msg : this.personName
										+ '已死亡或迁出，将同时注销该人所有相关档案，是否继续？',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										var req = {};
										req.serviceId = "chis.healthRecordService";
										req.serviceAction = "logoutAllRecords";
										req.body = saveData;
										this.logOut(req);
									}
								},
								scope : this
							});
				} else {// 子档注销
					var req = {};
					req.serviceId = this.saveServiceId;
					req.serviceAction = this.saveAction;
					req.body = saveData;
					this.logOut(req);
				}
			},
			logOut : function(req) {
				this.form.el.mask("正在提交请求，请稍候...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : req.serviceId,
							serviceAction : req.serviceAction,
							method : "execute",
							body : req.body
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code < 300) {
								this.doCancel();
								this.fireEvent("writeOff", this.entryName,
										this.op, json, this.data);
							} else {
								this.processReturnMsg(code, msg);
							}
							this.getWin().hide();
							this.fireEvent("writeOff", this.entryName, this.op,
									json, this.data);
						}, this);
			},
			doCancel : function() {
				this.getWin().hide();
			},
			getSaveRequest : function(saveData) {
				saveData.phrId = this.phrId;
				if (saveData.cancellationReason == "1") {
					saveData.deadFlag = "y"
				}
				return saveData;
			},

			onSave : function() {
				this.fireEvent("remove");
				this.getWin().hide();
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
						if (items[i].id == "deadReason"
								|| items[i].id == "deadDate") {
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
			}
		});