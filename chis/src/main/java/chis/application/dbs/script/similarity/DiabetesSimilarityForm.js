$package("chis.application.dbs.script.similarity")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.dbs.script.similarity.DiabetesSimilarityForm = function(cfg) {
	cfg.colCount  = 3
	cfg.labelWidth = 140;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.width = 800
	chis.application.dbs.script.similarity.DiabetesSimilarityForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow",this.onWinShow,this)
}
Ext.extend(chis.application.dbs.script.similarity.DiabetesSimilarityForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.similarity.DiabetesSimilarityForm.superclass.onReady
				.call(this)
				var form = this.form.getForm()

				var weight = form.findField("weight")
				this.weight = weight
				if (weight) {
					weight.on("keyup", this.onWeightCheck, this)
				}

				var height = form.findField("height")
				this.height = height
				if (height) {
					height.on("keyup", this.onHeightCheck, this)
				}
				
				var fbsField = form.findField("fbs")
				this.fbsField = fbsField

				var pbsField = form.findField("pbs")
				this.pbsField = pbsField
			},
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
					// alert("身高输入非法")
					this.height.markInvalid("身高必须在130-250之间")
					this.height.focus()
					return
				}
				this.onCalculateBMI()
			},
			onWeightCheck : function() {
				if (this.weight.getValue() == "") {
					return
				}
				if (this.weight.getValue() > 140 || this.weight.getValue() < 20) {
					// alert("身高输入非法")
					this.weight.markInvalid("体重必须在20-140之间")
					this.weight.focus()
					return
				}
				this.onCalculateBMI()
			},
			onCalculateBMI : function() {
				var form = this.form.getForm()
				var bmi = form.findField("bmi")
				if (bmi) {
					var w = this.weight.getValue()
					var h = this.height.getValue()
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2)
					bmi.setValue(b)
				}
			}
			,
			saveToServer:function(saveData){
				saveData.empiId = this.exContext.args.empiId
				if ((this.fbsField.getValue() == "" && this.pbsField.getValue() == "")) {
					Ext.MessageBox.alert("提示", "空腹血糖餐后血糖必须输入一个")
					return
				}
				chis.application.dbs.script.similarity.DiabetesSimilarityForm.superclass.saveToServer.call(this,saveData)
				this.win.hide()
			}
			,
			onWinShow:function(){
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.diabetesSimilarityService",
					serviceAction : "initializeSimilarity",
					method:"execute",
					body : {
						empiId : this.exContext.args.empiId
					}
				})
				this.doNew()
				if(result.json.body){
					this.initFormData(result.json.body)
				}
			}
		});