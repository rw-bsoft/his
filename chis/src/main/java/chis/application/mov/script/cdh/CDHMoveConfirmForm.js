/**
 * 儿童户籍地址迁移表单确认页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.cdh")
$import("chis.application.mov.script.cdh.CDHMoveUtilForm", "util.widgets.LookUpField")
chis.application.mov.script.cdh.CDHMoveConfirmForm = function(cfg) {
	chis.application.mov.script.cdh.CDHMoveConfirmForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("save", this.onSave, this)
};
Ext.extend(chis.application.mov.script.cdh.CDHMoveConfirmForm, chis.application.mov.script.cdh.CDHMoveUtilForm,
		{
	
			doConfirm : function() {
				this.saveAction = "saveCDHMoveConfirmRecord";
				this.doSave();
			},

			doReject : function() {
				this.saveAction = "saveCDHMoveRejectRecord";
				this.doSave();
			},

			onSave : function(entryName, op, json, data) {
				this.doCancel();
				this.fireEvent("afterSave");
			}

		})