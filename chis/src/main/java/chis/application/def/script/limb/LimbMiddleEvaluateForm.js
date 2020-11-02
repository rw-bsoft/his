$package("chis.application.def.script.limb");

$import("chis.script.BizTableFormView");

chis.application.def.script.limb.LimbMiddleEvaluateForm = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_LimbMiddleEvaluate"
	cfg.colCount = 3;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 140
	cfg.labelWidth = 100
	cfg.showButtonOnTop = true;
	chis.application.def.script.limb.LimbMiddleEvaluateForm.superclass.constructor
			.apply(this, [cfg]);
	this.loadServiceId = "chis.defLimbService"
	this.loadAction = "loadLimbMiddleEvaluate"
	this.saveServiceId = "chis.defLimbService"
	this.saveAction = "saveLimbMiddleEvaluate"
	this.on("loadData",this.onLoadData,this)
};

Ext.extend(chis.application.def.script.limb.LimbMiddleEvaluateForm, chis.script.BizTableFormView,
		{
			saveToServer : function(saveData) {
				saveData.evaluateId = this.exContext.r1.get("id")
				chis.application.def.script.limb.LimbMiddleEvaluateForm.superclass.saveToServer.call(this,
						saveData)
			},
			loadData : function() {
				var result = util.rmi.miniJsonRequestSync({
					serviceId:"chis.defLimbService",
					serviceAction:"loadLimbMiddleEvaluateData",
					method:"execute",
					body : {id: this.exContext.r1.get("id"),ids : this.exContext.ids}
				})
				if(result.json.body){
					this.initFormData(result.json.body)
				}else{
					this.doInitialize()
					this.initDataId = null
				}
				this.fireEvent("loadData")
			}
			,
			onLoadData:function(){
				if(this.exContext.r1.data.middleEvaluate){
//					this.form.getTopToolbar().items.item(0).enable()
					this.changeButtonState(false,0)
				}else{
//					this.form.getTopToolbar().items.item(0).disable()
					this.changeButtonState(true,0)
				}
			}
			,
			getLoadRequest : function() {
				return {
					ids : this.exContext.ids,
					r : this.exContext.r1.data
				}
			},
			doInitialize:function(){
				this.doNew()
				this.fireEvent("create")
			},
			onReady:function(){
				chis.application.def.script.limb.LimbMiddleEvaluateForm.superclass.onReady.call(this)
				
				var firstScore = this.form.getForm().findField("firstScore")
				firstScore.on("select", this.onCalculateScore, this)
				firstScore.on("blur", this.onCalculateScore, this)
				firstScore.on("keyup", this.onCalculateScore, this)
				this.firstScore = firstScore
				
				var secondScore = this.form.getForm().findField("secondScore")
				secondScore.on("select", this.onCalculateScore, this)
				secondScore.on("blur", this.onCalculateScore, this)
				secondScore.on("keyup", this.onCalculateScore, this)
				this.secondScore = secondScore
			},
			onCalculateScore:function(){
				var score = Number(this.secondScore.getValue()) -  Number(this.firstScore.getValue())
				this.form.getForm().findField("updateScore").setValue(score)
			}
		});