/**
 * 体弱儿档案注销表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.record")
$import("chis.script.BizTableFormView")
chis.application.cdh.script.debility.record.DebilityChildrenRecordWriteOffForm = function(cfg) {
	cfg.height = 450
	cfg.colCount = 3;
	cfg.fldDefaultWidth = 145;
	chis.application.cdh.script.debility.record.DebilityChildrenRecordWriteOffForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.cdh.script.debility.record.DebilityChildrenRecordWriteOffForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.cdh.script.debility.record.DebilityChildrenRecordWriteOffForm.superclass.onReady
						.call(this);
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason");
				if (cancellationReason)
					cancellationReason.on("select", function(field) {
								var value = field.getValue();
								this.setDeadReason(value);
							}, this);

				var cancellationUser = this.form.getForm()
						.findField("cancellationUser");
				if (cancellationUser)
					cancellationUser.disable();

				var cancellationDate = this.form.getForm()
						.findField("cancellationDate");
				if (cancellationDate)
					cancellationDate.disable();
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

			saveToServer : function(saveData) {
				var values = this.exContext["chis.application.cdh.schemas.CDH_DebilityChildren"].data;
				Ext.apply(values, saveData);
				// 如果是死亡注销，则填写死亡时间
				if (values.cancellationReason == "1") {
					values.deadFlag = " y"
				}
				if (!this.fireEvent("beforeRemove", this.entryName, values)) {
					return;
				}
				var cancellationReason = values.cancellationReason;
				if (cancellationReason == '1' || cancellationReason == '2') { // **
					// 死亡或迁出注销所有档案
					Ext.Msg.show({
						title : '档案注销',
						msg : '死亡或者迁出将注销' + values.personName + '的所有档案,确定是否继续？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var req = {};
								req.serviceId = "chis.healthRecordService";
								req.serviceAction = "logoutAllRecords";
								req.body = values;
								this.logOut(req);
							}
						},
						scope : this
					});
				} else if (cancellationReason == '6') { // ** 作废注销单笔体弱儿档案
					Ext.Msg.show({
						title : '体弱儿档案注销',
						msg : values.personName + '的该条体弱儿档案将被作废且不能恢复,确定是否继续？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var req = {};
								req.serviceId = "chis.debilityChildrenService";
								req.serviceAction = "logOutDebilityChildrenByRecordId";
								req.body = values;
								this.logOut(req);
							}
						},
						scope : this
					});
				} else {
					Ext.Msg.show({
						title : '体弱儿档案注销',
						msg : values.personName + '的所有体弱儿档案将被注销,确定是否继续？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var req = {};
								req.serviceId = "chis.debilityChildrenService";
								req.serviceAction = "logOutDebilityChildrenByEmpiId";
								req.body = values;
								this.logOut(req);
							}
						},
						scope : this
					});
				}
			},

			logOut : function(req) {
				this.form.el.mask("正在注销数据...")
				util.rmi.jsonRequest({
							serviceId : req.serviceId,
							serviceAction : req.serviceAction,
							method:"execute",
							body : req.body
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code < 300) {
								this.doCancel();
								this.fireEvent("save")
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
			},

			onWinShow : function() {
				this.doNew()
			}
		});
