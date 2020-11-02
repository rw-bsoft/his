$package("chis.application.def.script.limb");

$import("chis.script.BizTableFormView");

chis.application.def.script.limb.LimbTrainingRecordForm = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_LimbTrainingRecord"
	cfg.colCount = 3;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 140
	cfg.labelWidth = 100
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false
	cfg.width = 780
	chis.application.def.script.limb.LimbTrainingRecordForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow",this.onWinShow,this)
	this.saveServiceId = "chis.defLimbService"
	this.saveAction = "saveLimbTrainingRecord"
	this.loadServiceId = "chis.defLimbService"
	this.loadAction = "loadLimbTrainingRecord"
};

Ext.extend(chis.application.def.script.limb.LimbTrainingRecordForm, chis.script.BizTableFormView,
		{
			saveToServer : function(saveData) {
				saveData.planId = this.exContext.r2.get("id")
				saveData.empiId = this.exContext.ids.empiId
				chis.application.def.script.limb.LimbTrainingRecordForm.superclass.saveToServer.call(this,saveData)

			},
			onWinShow:function(){
				if(this.op=="create"){
					this.doNew()
				}else{
					this.loadData()
				}
			},
			getLoadRequest : function() {
				return {
					initDataId:this.initDataId
				}
			}
		});