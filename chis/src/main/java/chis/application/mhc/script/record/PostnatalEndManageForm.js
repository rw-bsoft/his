/**
 * 孕妇终止管理表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.record.PostnatalEndManageForm = function(cfg) {
	cfg.fldDefaultWidth = 123
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.record.PostnatalEndManageForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(chis.application.mhc.script.record.PostnatalEndManageForm, chis.script.BizTableFormView,
		{
      
			saveToServer : function(saveData) {
				var values = this.exContext["chis.application.mhc.schemas.MHC_PregnantRecord"].data;
				saveData.empiId = values.empiId;
				saveData.pregnantId = values.pregnantId;
				Ext.Msg.show({
							title : '孕妇档案终止妊娠',
							msg : values.personName
									+ '的孕妇档案将被终止妊娠且不能恢复，确定是否继续？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var req = {};
									req.serviceId = this.saveServiceId;
									req.serviceAction = this.saveAction;
									req.body = saveData;
									this.logOut(req);
								}
							},
							scope : this
						});
			},

			logOut : function(req) {
				this.form.el.mask("正在提交数据...")
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