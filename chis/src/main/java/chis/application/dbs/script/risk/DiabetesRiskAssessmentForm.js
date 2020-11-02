$package("chis.application.dbs.script.risk")
$import("util.Accredit", "chis.script.BizTableFormView","chis.script.util.helper.Helper")

chis.application.dbs.script.risk.DiabetesRiskAssessmentForm = function(cfg) {
	cfg.colCount  = 4
	cfg.labelWidth = 120;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 110;
//	cfg.width = 780
	chis.application.dbs.script.risk.DiabetesRiskAssessmentForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(chis.application.dbs.script.risk.DiabetesRiskAssessmentForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.risk.DiabetesRiskAssessmentForm.superclass.onReady
						.call(this)
				var form = this.form.getForm()

				var weight = form.findField("weight")
				this.weight = weight
				if (weight) {
					weight.on("keyup", this.onWeightCheck, this)
					weight.on("blur", this.onWeightCheck, this)
				}

				var height = form.findField("height")
				this.height = height
				if (height) {
					height.on("keyup", this.onHeightCheck, this)
					height.on("blur", this.onHeightCheck, this)
				}
				
				var waistLine = form.findField("waistLine")
				this.waistLine = waistLine
				if (waistLine) {
					waistLine.on("keyup", this.onWaistLine, this)
					waistLine.on("blur", this.onWaistLine, this)
				}
				
				
				var estimateDate = form.findField("estimateDate")
				this.estimateDate = estimateDate
				if (estimateDate) {
					estimateDate.on("select", this.onEstimateDate, this)
				}
				
			},
			onEstimateDate:function(){
				var form = this.form.getForm()
				var month = chis.script.util.helper.Helper.getAgeMonths(Date.parseDate(this.exContext.args.birthday, "Y-m-d"), this.estimateDate.getValue());
				var age = month / 12
				var sexCode = this.exContext.args.sexCode
				var riskiness = {}
				if(age > 45){
					if(form.findField("riskiness").getValue() ==''){
							riskiness.key = '01';
							form.findField("riskiness").setValue(riskiness)
						}else{
							var riskinessArray = form.findField("riskiness").getValue().split(",")
							if(riskinessArray.indexOf('01') == -1){
								riskiness.key = form.findField("riskiness").getValue() +',01'
								form.findField("riskiness").setValue(riskiness)
							}
						}
				}else{
					var riskinessArray = form.findField("riskiness").getValue().split(",")
					if(riskinessArray.indexOf('01') != -1){
						riskiness.key = ""
						for(var i=0;i<riskinessArray.length;i++){
							if(riskinessArray[i] != '01'){
								riskiness.key += riskinessArray[i] + ","
							}
						}
						riskiness.key = riskiness.key.substring(0,riskiness.key.length-1)
						form.findField("riskiness").setValue(riskiness)
					}
				}
				form.findField("age").setValue(parseInt(age))
			}
			,
			saveToServer:function(saveData){
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				saveData.riskId = this.exContext.ids.riskId
				chis.application.dbs.script.risk.DiabetesRiskAssessmentForm.superclass.saveToServer.call(this,saveData)
			}
			,
			doRecipel:function(item,e){
				var module = this.createSimpleModule("DiabetesSimilarityHT","chis.application.dbs.DBS/DBS/D18-1-3")
				module.initPanel()
				this.refreshExContextData(module,this.exContext)
				module.getWin().show()
			}
			,
			doCreate:function(){
				this.fireEvent("create")
			}
			,
			onWaistLine:function(){
				var form = this.form.getForm()
				var sexCode = this.exContext.args.sexCode
				var bmi = form.findField("bmi").getValue()
				var riskiness = {}
				if(bmi>24 || (sexCode=='1' && form.findField("waistLine").getValue()>=90) || (sexCode=='2' && form.findField("waistLine").getValue()>=85)){
					if(form.findField("riskiness").getValue() ==''){
							riskiness.key = '02';
							form.findField("riskiness").setValue(riskiness)
						}else{
							var riskinessArray = form.findField("riskiness").getValue().split(",")
							if(riskinessArray.indexOf('02') == -1){
								riskiness.key = form.findField("riskiness").getValue() +',02'
								form.findField("riskiness").setValue(riskiness)
							}
						}
				}else{
					var riskinessArray = form.findField("riskiness").getValue().split(",")
					if(riskinessArray.indexOf('02') != -1){
						riskiness.key = ""
						for(var i=0;i<riskinessArray.length;i++){
							if(riskinessArray[i] != '02'){
								riskiness.key += riskinessArray[i] + ","
							}
						}
						riskiness.key = riskiness.key.substring(0,riskiness.key.length-1)
						form.findField("riskiness").setValue(riskiness)
					}
				}
			}
			,
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
					var riskiness = {}
					var sexCode = this.exContext.args.sexCode
					if(b >= 24 || ((sexCode=='1' && form.findField("waistLine").getValue()>=90) || (sexCode=='2' && form.findField("waistLine").getValue()>=85))){
						if(form.findField("riskiness").getValue() ==''){
							riskiness.key = '02';
							form.findField("riskiness").setValue(riskiness)
						}else{
							var riskinessArray = form.findField("riskiness").getValue().split(",")
							if(riskinessArray.indexOf('02') == -1){
								riskiness.key = form.findField("riskiness").getValue() +',02'
								form.findField("riskiness").setValue(riskiness)
							}
						}
					}else{
						var riskinessArray = form.findField("riskiness").getValue().split(",")
						if(riskinessArray.indexOf('02') != -1){
							riskiness.key = ""
							for(var i=0;i<riskinessArray.length;i++){
								if(riskinessArray[i] != '02'){
									riskiness.key += riskinessArray[i] + ","
								}
							}
							riskiness.key = riskiness.key.substring(0,riskiness.key.length-1)
							form.findField("riskiness").setValue(riskiness)
						}
					}
				}
			}
		});