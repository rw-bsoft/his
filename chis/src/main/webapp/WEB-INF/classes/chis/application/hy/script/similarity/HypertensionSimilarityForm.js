$package("chis.application.hy.script.similarity")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hy.script.similarity.HypertensionSimilarityForm = function(cfg) {
	cfg.colCount  = 3
	cfg.width=755;
	cfg.labelWidth=100;
	cfg.fldDefaultWidth = 150;
	cfg.autoFieldWidth = false;
	chis.application.hy.script.similarity.HypertensionSimilarityForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow",this.onWinShow,this)
	this.on("save",this.onSave,this);
}
Ext.extend(chis.application.hy.script.similarity.HypertensionSimilarityForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.hy.script.similarity.HypertensionSimilarityForm.superclass.onReady
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
				saveData.empiId = this.exContext.args.empiId || this.exContext.ids.empiId;
//				if ((this.fbsField.getValue() == "" && this.pbsField.getValue() == "")
//						&& this.visitEffect.getValue() == "1") {
//					Ext.MessageBox.alert("提示", "空腹血糖餐后血糖必须输入一个")
//					return
//				}
				chis.application.hy.script.similarity.HypertensionSimilarityForm.superclass.saveToServer.call(this,saveData)
				if(this.win){
					this.win.hide()
				}
			}
			,
			onSave : function(entryName, op, json, data){
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
			},
			onWinShow:function(){
//				var result = util.rmi.miniJsonRequestSync({
//					serviceId : "chis.hypertensionSimilarityService",
//					serviceAction : "initializeSimilarity",
//					method:"execute",
//					body : {
//						empiId : this.exContext.args.empiId
//					}
//				})
				this.doNew()
//				if(result.json.body){
//					this.initFormData(result.json.body)
//				}
			}
		});