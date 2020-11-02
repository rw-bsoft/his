$package("chis.application.per.script.checkup");

$import("chis.script.BizTableFormView");

chis.application.per.script.checkup.CheckupRevokeForm = function(cfg){
	cfg.colCount = 3;
	cfg.fldDefaultWidth = 158;
	chis.application.per.script.checkup.CheckupRevokeForm.superclass.constructor.apply(this,[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onSave, this);
	this.saveAction = "checkRevoke";
};

Ext.extend(chis.application.per.script.checkup.CheckupRevokeForm,chis.script.BizTableFormView,{
	onWinShow : function() {
		this.doNew();
	},
	onBeforeSave : function(entryName, op, saveData) {
		saveData["checkupNo"] = this.checkupNo;
		saveData["empiId"] = this.empiId;
	},
	onSave : function(entryName, op, json, body) {
		this.fireEvent("recordSave", entryName, op, json, body);
		this.doCancel();
	}
});