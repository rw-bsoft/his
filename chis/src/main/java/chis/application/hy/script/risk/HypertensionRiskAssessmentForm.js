$package("chis.application.hy.script.risk")
$import("util.Accredit", "chis.script.BizFieldSetFormView",
		"chis.script.util.helper.Helper")

chis.application.hy.script.risk.HypertensionRiskAssessmentForm = function(cfg) {
	cfg.colCount = 4
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.autoLoadData = true;
	cfg.isAutoScroll = true;
	chis.application.hy.script.risk.HypertensionRiskAssessmentForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("loadData", this.onLoadData, this);
	this.on("doNew", this.onDoNew, this);
	this.on("save", this.onSave, this);
}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskAssessmentForm,
		chis.script.BizFieldSetFormView, {
			onReady : function() {
				chis.application.hy.script.risk.HypertensionRiskAssessmentForm.superclass.onReady
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
				var dataSource = form.findField("dataSource");
				this.dataSource = dataSource;
				if (dataSource) {
					dataSource.on("keyup", this.onDataSource, this);
					dataSource.on("select", this.onDataSource, this)
				}
				var effectCase = form.findField("effectCase");
				this.effectCase = effectCase;
				if (effectCase) {
					effectCase.on("keyup", this.onEffectCase, this);
					effectCase.on("select", this.onEffectCase, this)
				}

				var constriction = form.findField("constriction")
				this.constriction = constriction
				if (constriction) {
					constriction.on("keyup", this.onHYCheck, this)
					constriction.on("blur", this.onHYCheck, this)
				}
				var diastolic = form.findField("diastolic")
				this.diastolic = diastolic
				if (diastolic) {
					diastolic.on("keyup", this.onHYCheck, this)
					diastolic.on("blur", this.onHYCheck, this)
				}
			},
			onHYCheck : function() {
				var c = this.constriction.getValue();
				var d = this.diastolic.getValue();
				var form = this.form.getForm();
				var riskiness = {}
				if (c && c != 0) {
					if (c >= 120 && c <= 139) {
						this.addRiskness("03");
						return;
					}

				}
				if (d && d != 0) {
					if (d >= 80 && d <= 89) {
						this.addRiskness("03");
						return;
					}
				}
				this.removeRiskiness("03")
			},
			onEffectCase : function() {
				var value = this.effectCase.getValue();
				var form = this.form.getForm();
				var stopDate = form.findField("stopDate");
				var effect = form.findField("effect");
				if (value == "3") {
					stopDate.enable();
					effect.enable();
				} else {
					stopDate.disable();
					effect.disable();
				}
			},
			onDataSource : function() {
				var value = this.dataSource.getValue();
				var form = this.form.getForm();
				var otherDataSource = form.findField("otherDataSource");
				if (value == "9") {
					otherDataSource.enable();
				} else {
					otherDataSource.disable();
				}

			},
			getLoadRequest : function() {
				if (this.initDataId) {
					return {
						pkey : this.initDataId
					};
				} else {
					return {
						"fieldName" : "empiId",
						"fieldValue" : this.exContext.ids["empiId"]
					};
				}
			},
			getControlData : function() {
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getActionControl",
					method : "execute",
					body : {
						"empiId" : this.exContext.ids.empiId,
						"schema" : "chis.application.hy.schemas.MDC_HypertensionRisk"
					}
				})
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				if (result.json.body) {
					var control = result.json.body["_actions"];
					if (control) {
						Ext.apply(this.exContext.control, control);
					} else {
						this.exContext.control = {};
					}
				}
			},
			onDoNew : function() {
				this.getControlData();
			},
			onLoadNoData : function() {
				this.initDataId = null;
				this.onEstimateDate();
				this.initRiskiness();
			},
			initRiskiness : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionRiskService",
							serviceAction : "initializeHypertensionRiskAssessment",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId
							}
						});
				var data = result.json.body;
				if (data) {
					var XY = data.XY;
					var QFTLYD = data.QFTLYD;
					var GXYJZS = data.GXYJZS;
					var TNBHZ = data.TNBHZ;
					if (GXYJZS == true) {
						this.addRiskness("04")
					} else {
						this.removeRiskiness("04")
					}
					if (TNBHZ == true) {
						this.addRiskness("07")
					} else {
						this.removeRiskiness("07")
					}
					if (QFTLYD == true) {
						this.addRiskness("08")
					} else {
						this.removeRiskiness("08")
					}
					if (XY == true) {
						this.addRiskness("09")
					} else {
						this.removeRiskiness("09")
					}
				}
			},
			onLoadData : function(entryName, body) {
				var bts = this.form.getTopToolbar();
				var checkBtn = bts.find("cmd", "save");
				if (checkBtn && checkBtn[0]) {
					if (body && (body.status == "1" || body.status.key == "1")) {
						checkBtn[0].disable();
						return;
					}
					if (body && body.statusCase && body.statusCase.key != "1") {
						checkBtn[0].disable();
						MyMessageTip.msg("提示", "核实情况为高危确诊才允许生成档案！", true)
						return;
					}
					if (body && body.createFlag && body.createFlag.key != "1") {
						this.initRiskiness();
					}
					checkBtn[0].enable();
				}
			},
			addRiskness : function(key) {
				var form = this.form.getForm();
				var riskiness = {};
				if (form.findField("riskiness").getValue() == '') {
					riskiness.key = key;
					form.findField("riskiness").setValue(riskiness)
				} else {
					var riskinessArray = form.findField("riskiness").getValue()
							.split(",")
					if (riskinessArray.indexOf(key) == -1) {
						riskiness.key = form.findField("riskiness").getValue()
								+ ',' + key
						form.findField("riskiness").setValue(riskiness)
					}
				}
			},
			removeRiskiness : function(key) {
				var form = this.form.getForm();
				var riskiness = {};
				var riskinessArray = form.findField("riskiness").getValue()
						.split(",")
				if (riskinessArray.indexOf(key) != -1) {
					riskiness.key = ""
					for (var i = 0; i < riskinessArray.length; i++) {
						if (riskinessArray[i] != key) {
							riskiness.key += riskinessArray[i] + ","
						}
					}
					riskiness.key = riskiness.key.substring(0,
							riskiness.key.length - 1)
					form.findField("riskiness").setValue(riskiness)
				}
			},
			onEstimateDate : function() {
				var form = this.form.getForm()
				var month = chis.script.util.helper.Helper.getAgeMonths(Date
								.parseDate(this.exContext.empiData.birthday,
										"Y-m-d"), new Date());
				var age = month / 12
				var sexCode = this.exContext.args.sexCode
				var riskiness = {}
				if (age > 45) {
					this.addRiskness("01")
				} else {
					this.removeRiskiness("01")
				}
				form.findField("age").setValue(parseInt(age))
			},
			doNew : function() {
				this.initDataId = null;
				chis.application.hy.script.risk.HypertensionRiskAssessmentForm.superclass.doNew
						.call(this)
			},
			saveToServer : function(saveData) {
				saveData.empiId = this.exContext.ids.empiId;
				saveData.phrId = this.exContext.ids.phrId;
				saveData.riskId = this.initDataId;
				chis.application.hy.script.risk.HypertensionRiskAssessmentForm.superclass.saveToServer
						.call(this, saveData)
			},
			onSave : function(entryName, op, json, data) {
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				this.fireEvent("refreshEhrView", "C_07");
				this.refreshEhrTopIcon();
			},
			doRecipel : function(item, e) {
				var module = this.createSimpleModule(
						"HypertensionSimilarityHT",
						"chis.application.hy.HY/HY/C18-1-3")
				module.initPanel()
				this.refreshExContextData(module, this.exContext)
				module.getWin().show()
			},
			doCreate : function() {
				this.fireEvent("create")
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
					var riskiness = {}
					var sexCode = this.exContext.args.sexCode
					if (b >= 24) {
						this.addRiskness("02")
					} else {
						this.removeRiskiness("02")
					}
				}
			}
		});