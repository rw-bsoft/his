$package("chis.application.hy.script.risk")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hy.script.risk.HypertensionRiskHTForm = function(cfg) {
	cfg.colCount  = 3
	cfg.autoLoadSchema = false
	chis.application.hy.script.risk.HypertensionRiskHTForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow",this.onWinShow,this)
}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskHTForm,
		chis.script.BizTableFormView, {
			saveToServer:function(saveData){
				saveData.riskAssessmentId = this.exContext.args.record.data.recordId
				chis.application.hy.script.risk.HypertensionRiskHTForm.superclass.saveToServer.call(this,saveData)
			}
			,
			onWinShow:function(){
				this.loadData()
			},
			getLoadRequest : function() {
				this.initDataId = null
				return {
					"fieldName" : "riskAssessmentId",
					"fieldValue" : this.exContext.args.record.data.recordId
				};
			}
			,
			doImport : function(){
				var EduRecipelQuery = this.midiModules["HERRecipelQuery"];
				if (!EduRecipelQuery) {
					$import("chis.application.her.script.util.HERQueryModule")
					EduRecipelQuery = new chis.application.her.script.util.HERQueryModule({
						title : "健康教育处方查询",
						autoLoadSchema : true,
						isCombined : true,
						autoLoadData : false,
						mutiSelect : false,
						queryCndsType : "1",
						entryName : "chis.application.her.schemas.HER_RecipelRecordQuery",
						buttonIndex : 3,
						height : 500,
						itemHeight : 92,
						mainApp : this.mainApp
					});
					EduRecipelQuery.on("recordSelected", function(records) {
						if (!records) {
							return;
						}
						var healthTeach = this.form.getForm().findField("healthTeach");
						if(healthTeach && records[0].data){
							healthTeach.setValue(records[0].get("recipelContent"));
						}
					}, this);
					this.midiModules["HERRecipelQuery"] = EduRecipelQuery;
				}
				var win = EduRecipelQuery.getWin();
				win.setPosition(250, 100);
				win.show();
			}
		});