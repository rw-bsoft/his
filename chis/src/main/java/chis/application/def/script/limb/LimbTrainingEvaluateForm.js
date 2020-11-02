$package("chis.application.def.script.limb");

$import("chis.script.BizTableFormView");

chis.application.def.script.limb.LimbTrainingEvaluateForm = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_LimbTrainingEvaluate"
	cfg.colCount = 3;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	cfg.labelWidth = 80
	cfg.showButtonOnTop = true;
	chis.application.def.script.limb.LimbTrainingEvaluateForm.superclass.constructor.apply(this,
			[cfg]);
	this.saveServiceId = "chis.defLimbService"
	this.saveAction = "saveLimbTrainingEvaluate"
	this.loadServiceId = "chis.defLimbService"
	this.loadAction = "loadLimbTrainingEvaluateData"
};

Ext.extend(chis.application.def.script.limb.LimbTrainingEvaluateForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.def.script.limb.LimbTrainingEvaluateForm.superclass.onReady
						.call(this)

				var turnOver = this.form.getForm().findField("turnOver")
				turnOver.on("select", this.onCalculateScore, this)
				turnOver.on("blur", this.onCalculateScore, this)
				turnOver.on("keyup", this.onCalculateScore, this)
				this.turnOver = turnOver

				var sit = this.form.getForm().findField("sit")
				sit.on("select", this.onCalculateScore, this)
				sit.on("blur", this.onCalculateScore, this)
				sit.on("keyup", this.onCalculateScore, this)
				this.sit = sit

				var stand = this.form.getForm().findField("stand")
				stand.on("select", this.onCalculateScore, this)
				stand.on("blur", this.onCalculateScore, this)
				stand.on("keyup", this.onCalculateScore, this)
				this.stand = stand

				var shift = this.form.getForm().findField("shift")
				shift.on("select", this.onCalculateScore, this)
				shift.on("blur", this.onCalculateScore, this)
				shift.on("keyup", this.onCalculateScore, this)
				this.shift = shift

				var walk = this.form.getForm().findField("walk")
				walk.on("select", this.onCalculateScore, this)
				walk.on("blur", this.onCalculateScore, this)
				walk.on("keyup", this.onCalculateScore, this)
				this.walk = walk

				var walkupStairs = this.form.getForm()
						.findField("walkupStairs")
				walkupStairs.on("select", this.onCalculateScore, this)
				walkupStairs.on("blur", this.onCalculateScore, this)
				walkupStairs.on("keyup", this.onCalculateScore, this)
				this.walkupStairs = walkupStairs

				var takeFood = this.form.getForm().findField("takeFood")
				takeFood.on("select", this.onCalculateScore, this)
				takeFood.on("blur", this.onCalculateScore, this)
				takeFood.on("keyup", this.onCalculateScore, this)
				this.takeFood = takeFood

				var undress = this.form.getForm().findField("undress")
				undress.on("select", this.onCalculateScore, this)
				undress.on("blur", this.onCalculateScore, this)
				undress.on("keyup", this.onCalculateScore, this)
				this.undress = undress

				var wash = this.form.getForm().findField("wash")
				wash.on("select", this.onCalculateScore, this)
				wash.on("blur", this.onCalculateScore, this)
				wash.on("keyup", this.onCalculateScore, this)
				this.wash = wash

				var toilet = this.form.getForm().findField("toilet")
				toilet.on("select", this.onCalculateScore, this)
				toilet.on("blur", this.onCalculateScore, this)
				toilet.on("keyup", this.onCalculateScore, this)
				this.toilet = toilet

				var exchangeActivity = this.form.getForm()
						.findField("exchangeActivity")
				exchangeActivity.on("select", this.onCalculateScore, this)
				exchangeActivity.on("blur", this.onCalculateScore, this)
				exchangeActivity.on("keyup", this.onCalculateScore, this)
				this.exchangeActivity = exchangeActivity

				var houseWork = this.form.getForm().findField("houseWork")
				houseWork.on("select", this.onCalculateScore, this)
				houseWork.on("blur", this.onCalculateScore, this)
				houseWork.on("keyup", this.onCalculateScore, this)
				this.houseWork = houseWork

				var groupActivity = this.form.getForm()
						.findField("groupActivity")
				groupActivity.on("select", this.onCalculateScore, this)
				groupActivity.on("blur", this.onCalculateScore, this)
				groupActivity.on("keyup", this.onCalculateScore, this)
				this.groupActivity = groupActivity
			},
			onCalculateScore : function() {
				var score = Number(this.turnOver.getValue())
						+ Number(this.sit.getValue())
						+ Number(this.stand.getValue())
						+ Number(this.shift.getValue())
						+ Number(this.walk.getValue())
						+ Number(this.walkupStairs.getValue())
						+ Number(this.takeFood.getValue())
						+ Number(this.undress.getValue())
						+ Number(this.wash.getValue())
						+ Number(this.toilet.getValue())
						+ Number(this.exchangeActivity.getValue())
						+ Number(this.houseWork.getValue())
						+ Number(this.groupActivity.getValue())
				this.form.getForm().findField("score").setValue(score)
			},
			saveToServer : function(saveData) {
				this.onCalculateScore()
				saveData.defId = this.exContext.r.get("id")
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				chis.application.def.script.limb.LimbTrainingEvaluateForm.superclass.saveToServer.call(this,saveData)
			},
			doCreate : function() {
				this.fireEvent("create")
			},
			loadData : function() {
				if (this.exContext.r1.get("id")) {
					this.initDataId = this.exContext.r1.get("id")
					chis.application.def.script.limb.LimbTrainingEvaluateForm.superclass.loadData.call(this)
				} else {
					this.initDataId = null
					this.doInitialize()
				}
			},
			getLoadRequest : function() {
				return {
					ids : this.exContext.ids,
					r : this.exContext.r1.data
				}
			},
			doInitialize : function() {
				this.doNew()
				
			}
		});