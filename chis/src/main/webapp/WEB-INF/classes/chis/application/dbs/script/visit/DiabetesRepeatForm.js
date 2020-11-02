$package("chis.application.dbs.script.inquire")
$import("util.Accredit", "chis.script.BizTableFormView",
		"app.modules.list.SimpleListView")

chis.application.dbs.script.visit.DiabetesRepeatForm = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesRepeatVisit"
	cfg.saveServiceId = "chis.diabetesService"
	cfg.saveAction = "saveDiabetesRepeatVisit"
	chis.application.dbs.script.visit.DiabetesRepeatForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.dbs.script.visit.DiabetesRepeatForm,
		chis.script.BizTableFormView, {
			doCreate : function() {
				chis.application.dbs.script.visit.DiabetesRepeatForm.superclass.doCreate
						.call(this);
				this.fireEvent("create");
				//this.form.getForm().findField('visitDate').setValue(new Date());
				//this.form.getForm().findField('visitDate').setValue(this.exContext.args.r.data.visitDate);
			},
			getSaveRequest : function(saveData) {
				saveData.empiId = this.exContext.ids.empiId
				return saveData;
			},
			saveToServer : function(saveData) {
				//空腹血糖和餐后血糖必填一项
				if((saveData.fbs==""&&saveData.pbs=="")||(saveData.fbs.length==0&&saveData.pbs.length==0))
				{
					this.form.getForm().findField('fbs').focus();
					Ext.MessageBox.alert("提示", "空腹血糖和餐后血糖必填一项");
					return;
				}
				var saveRequest = this.getSaveRequest(saveData); // **
																	// 获取保存条件数据
				chis.application.dbs.script.visit.DiabetesRepeatForm.superclass.saveToServer
						.call(this, saveRequest)
			},
			loadData : function() {
				this.initDataId = this.exContext.r.get("recordId");
				chis.application.dbs.script.visit.DiabetesRepeatForm.superclass.loadData
						.call(this)
			}
		});