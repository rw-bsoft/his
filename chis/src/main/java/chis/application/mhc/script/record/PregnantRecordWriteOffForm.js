/**
 * 孕妇档案注销页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.record.PregnantRecordWriteOffForm = function(cfg) {
	cfg.fldDefaultWidth = 145
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = true;
	chis.application.mhc.script.record.PregnantRecordWriteOffForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.mhc.script.record.PregnantRecordWriteOffForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				this.doNew();
				chis.application.mhc.script.record.PregnantRecordWriteOffForm.superclass.onReady
						.call(this);
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason");
				if (cancellationReason)
					cancellationReason.on("select", function(field) {
								var value = field.getValue();
								this.setDeadReason(value);
							}, this);
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
				var values = this.exContext["chis.application.mhc.schemas.MHC_PregnantRecord"].data;
				Ext.apply(values, saveData);
				// 如果是死亡注销，则填写死亡标志
				var cancellationReason = values.cancellationReason;
				if (cancellationReason == "1") {
					values.deadFlag = "y"
				}
				Ext.Msg.show({
					title : '档案注销',
					msg : '档案注销后将无法操作，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							if (cancellationReason == "1"
									|| cancellationReason == "2") { // **
								// 死亡或迁出注销所有档案
								Ext.Msg.show({
									title : '确认注销[' + values.personName
											+ ']的所有档案',
									msg : values.personName
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
											req.body = values;
											this.logOut(req);
										}
									},
									scope : this
								})
							} else {
								Ext.Msg.show({
									title : '孕妇档案注销',
									msg : values.personName
											+ '的孕妇档案将被注销且不能操作，确定是否继续？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											var req = {};
											req.serviceId = "chis.pregnantRecordService";
											req.serviceAction = "logOutPregnantRecord";
											req.body = values;
											this.logOut(req);
										}
									},
									scope : this
								});

							}
						}
					},
					scope : this
				});
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
				this.doNew();
			}
		});