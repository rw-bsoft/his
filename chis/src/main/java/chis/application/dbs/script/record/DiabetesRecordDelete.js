// 高血压档案注销窗口。
$package("chis.application.dbs.script.record");

$import("chis.script.BizTableFormView");

chis.application.dbs.script.record.DiabetesRecordDelete = function(cfg) {
	if (!cfg) {
		cfg = {};
	}
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	this.title = "糖尿病刪除：仅用于建错档的情况";
	cfg.entryName = "chis.application.wl.schemas.MDC_Deletemdc";
	cfg.height = 450;
	cfg.enableCnd = false;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false
	cfg.showButtonOnTop = true;
	cfg.colCount = 3;
	cfg.fldDefaultWidth = 145;
	chis.application.dbs.script.record.DiabetesRecordDelete.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.dbs.script.record.DiabetesRecordDelete,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.record.DiabetesRecordDelete.superclass.onReady
						.call(this);
//				var frm = this.form.getForm();
//				var cancellationReason = frm.findField("cancellationReason");
//				if (cancellationReason)
//					cancellationReason.on("select", function(field) {
//								var value = field.getValue();
//								this.setDeadReason(value);
//							}, this);
//				var cancellationUser = frm.findField("cancellationUser");
//				if (cancellationUser) {
//					cancellationUser.disable();
//				}
//
//				var cancellationDate = frm.findField("cancellationDate");
//				if (cancellationDate) {
//					cancellationDate.disable();
//				}
			},

//			setDeadReason : function(value) {
//				var frm = this.form.getForm();
//				var deadReason = frm.findField("deadReason");
//				var deadDate = frm.findField("deadDate");
//				if (value == "1") {
//					deadReason.enable();
//					deadReason.allowBlank = false;
//
//					deadDate.enable();
//					deadDate.allowBlank = false;
//				} else {
//					deadReason.reset();
//					deadReason.disable();
//					deadReason.allowBlank = true;
//
//					deadDate.reset();
//					deadDate.disable();
//					deadDate.allowBlank = true;
//				}
//				deadReason.validate();
//				deadDate.validate();
//			},

			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values["phrId"] = this.record.id;
				values["empiId"] = this.record.data.empiId;
				values["recordType"]="2";//糖尿病
				values["oldCreateUser"]=this.record.data.createUser;
				values["oldCreateUnit"]=this.record.data.createUnit;
				values["oldCreateDate"]=this.record.data.createDate;
//				values["personName"] = this.record.data.personName;
				Ext.apply(this.data, values);
				var req = {};
				req.serviceId = "chis.diabetesService";
				req.serviceAction = "deleteDiabetesRecord";
				req.body = values;
				this.deletemdc(req);
				// 如果是死亡注销，则填写死亡时间
//				if (values.cancellationReason == 1) {
//					values.deadFlag = 'y';
//				}
//				Ext.Msg.show({
//							title : '档案删除',
//							msg : '档案删除后将无法操作，是否继续?',
//							modal : true,
//							width : 300,
//							buttons : Ext.MessageBox.OKCANCEL,
//							multiline : false,
//							fn : function(btn, text) {
//								if (btn == "ok") {
//									this.saveToServer(values);
//								}
//							},
//							scope : this
//						});
			},
//			saveToServer : function(saveData) {
//				var cancellationReason = saveData.cancellationReason;
//				if (cancellationReason == '1' || cancellationReason == '2') {
//					// 注销原因 为 死亡 或 迁出 调用健康档案注销全部档案
//					Ext.Msg.show({
//								title : '确认注销[' + saveData.personName
//										+ ']的所有档案',
//								msg : saveData.personName
//										+ '已死亡或迁出，将同时注销该人所有相关档案，是否继续？',
//								modal : true,
//								width : 300,
//								buttons : Ext.MessageBox.YESNO,
//								multiline : false,
//								fn : function(btn, text) {
//									if (btn == "yes") {
//										var req = {};
//										req.serviceId = "chis.healthRecordService";
//										req.serviceAction = "logoutAllRecords";
//										req.body = saveData;
//										this.logOut(req);
//									}
//								},
//								scope : this
//							});
//				} else {// 子档注销
//					var req = {};
//					req.serviceId = "chis.hypertensionService";
//					req.serviceAction = "logoutHypertensionRecord";
//					req.body = saveData;
//					this.logOut(req);
//				}
//			},
			deletemdc : function(req) {
				this.form.el.mask("正在提交请求，请稍候...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : req.serviceId,
							serviceAction : req.serviceAction,
							method:"execute",
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
			onWinShow : function() {
				this.doNew();
				if (!this.form) {
					this.initPanel();
				}
//				var deadReason = this.form.getForm().findField("deadReason");
//
//				deadReason.reset();
//				deadReason.disable();
//				deadReason.allowBlank = true;
//
//				this.form.getForm().findField("cancellationDate")
//						.setValue(this.mainApp.serverDate);
//				this.form.getForm().findField("cancellationReason")
//						.setValue(this.record.get("cancellationReason"));
//				this.form.getForm().findField("cancellationUser").setValue({
//							key : this.mainApp.uid,
//							text : this.mainApp.uname
//						});
				this.validate();
			}
		});
