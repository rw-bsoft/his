/**
 * 孕妇户籍地址迁移表单确认页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.mhc")
$import("chis.application.mov.script.mhc.MHCMoveUtilForm", "util.widgets.LookUpField")
chis.application.mov.script.mhc.MHCMoveConfirmForm = function(cfg) {
	chis.application.mov.script.mhc.MHCMoveConfirmForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("save", this.onSave, this)
};
Ext.extend(chis.application.mov.script.mhc.MHCMoveConfirmForm, chis.application.mov.script.mhc.MHCMoveUtilForm,
		{

			initFormData : function(body) {
				chis.application.mov.script.mhc.MHCMoveConfirmForm.superclass.initFormData
						.call(this, body);
				var residenceCode = this.form.getForm().findField("targetResidenceCode");
				residenceCode.disable();
			},

			doConfirm : function() {
				this.saveAction = "saveMHCMoveConfirmRecord";
				this.doSave();
			},

			doReject : function() {
				this.saveAction = "saveMHCMoveRejectRecord";
				this.doSave();
			},

			onSave : function(entryName, op, json, data) {
				this.doCancel();
				this.fireEvent("afterSave");
			}
		})