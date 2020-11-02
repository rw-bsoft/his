$package("chis.application.dbs.script.inquire")
$import("util.Accredit", "chis.script.BizTableFormView",
		"app.modules.list.SimpleListView")

chis.application.dbs.script.inquire.DiabetesInquireForm = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesInquire"
	cfg.saveServiceId = "chis.diabetesService"
	cfg.saveAction = "saveDiabetesInquire"
	chis.application.dbs.script.inquire.DiabetesInquireForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.dbs.script.inquire.DiabetesInquireForm,
		chis.script.BizTableFormView, {
			doCreate : function() {
				chis.application.dbs.script.inquire.DiabetesInquireForm.superclass.doCreate
						.call(this);
				this.fireEvent("create")
			},
			getSaveRequest : function(saveData) {
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				return saveData;
			},
			saveToServer : function(saveData) {

				var saveRequest = this.getSaveRequest(saveData); // **
																	// 获取保存条件数据
				chis.application.dbs.script.inquire.DiabetesInquireForm.superclass.saveToServer
						.call(this, saveRequest)
			},
			loadData : function() {
				this.initDataId = this.exContext.r.get("inquireId")
				chis.application.dbs.script.inquire.DiabetesInquireForm.superclass.loadData
						.call(this)
			}
		});