$package("chis.application.hy.script.similarity")
$import("util.Accredit", "chis.script.BizTableFormView", "chis.script.EHRView",
		"chis.script.util.widgets.MyMessageTip")

chis.application.hy.script.similarity.HypertensionSimilarityCheckForm = function(
		cfg) {
	cfg.colCount = 3
	cfg.labelWidth = 120;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	chis.application.hy.script.similarity.HypertensionSimilarityCheckForm.superclass.constructor
			.apply(this, [cfg])
	this.on("doNew", this.onDoNew, this)
}
Ext.extend(
		chis.application.hy.script.similarity.HypertensionSimilarityCheckForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				this.empiId = this.exContext.args.empiId
						|| this.exContext.ids.empiId;
				chis.application.hy.script.similarity.HypertensionSimilarityCheckForm.superclass.onReady
						.call(this)
				var form = this.form.getForm()

				var constriction = form.findField("constriction")
				this.constriction = constriction
				if (constriction) {
					constriction.on("keyup", this.onConstrictionCheck, this)
				}

				var diastolic = form.findField("diastolic")
				this.diastolic = diastolic
				if (diastolic) {
					diastolic.on("keyup", this.onDiastolicCheck, this)
				}

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
			onConstrictionCheck : function() {
				if (this.constriction.getValue() == ""
						|| this.diastolic.getValue() == "") {
					return
				}
				var hypertensionLevel = this
						.decideHypertensionGrade(this.constriction.getValue(),
								this.diastolic.getValue())
				this.form.getForm().findField("hypertensionLevel")
						.setValue(hypertensionLevel)
			},
			onDiastolicCheck : function() {
				if (this.constriction.getValue() == ""
						|| this.diastolic.getValue() == "") {
					return
				}
				var hypertensionLevel = this
						.decideHypertensionGrade(this.constriction.getValue(),
								this.diastolic.getValue())
				this.form.getForm().findField("hypertensionLevel")
						.setValue(hypertensionLevel)
			},
			decideHypertensionGrade : function(constriction, diastolic) {
				if (constriction >= 180 || diastolic >= 110) {
					return {
						key : "3",
						text : "3级血压（重度）"
					}; // @@ 3级（重度）
				}

				if ((constriction >= 160 && constriction <= 179)
						|| (diastolic >= 100 && diastolic <= 109)) {
					return {
						key : "2",
						text : "2级血压（中度）"
					}; // @@ 2级（中度）
				}
				if ((constriction >= 140 && constriction <= 159)
						|| (diastolic >= 90 && diastolic <= 99)) {
					return {
						key : "1",
						text : "1级血压（轻度）"
					}; // @@ 1级（轻度）
				}
				if (constriction < 120 && diastolic < 80) {
					return {
						key : "4",
						text : "理想血压"
					}; // @@ 理想血压
				}
				if (constriction < 130 && diastolic < 85) {
					return {
						key : "5",
						text : "正常血压"
					}; // @@ 正常血压
				}
				if ((constriction >= 130 && constriction <= 139 && diastolic < 90)
						|| (diastolic >= 85 && diastolic <= 89 && constriction < 140)) {
					return {
						key : "6",
						text : "正常高值"
					}; // @@ 正常高值
				}
				if (constriction >= 140 && diastolic < 90) {
					return {
						key : "7",
						text : "单纯收缩性高血压"
					}; // @@ 单纯收缩性高血压
				}
			},
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
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
			},
			onDoNew : function() {
				var diagnosisType = this.exContext.args.diagnosisType;
				if (!diagnosisType || diagnosisType == 2) {
					this.exContext.control = {
						"create" : true,
						"update" : true
					}
				} else {
					this.exContext.control = {
						"create" : false,
						"update" : false
					}
				}
			},
			doConfirm : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionSimilarityService",
							serviceAction : "saveConfirmHypertensionSimilarity",
							method : "execute",
							body : {
								empiId : this.exContext.args.empiId
										|| this.exContext.ids.empiId,
								similarityId : this.exContext.args.similarityId,
								phrId : this.exContext.args.phrId
							}
						})
				if (result.code == 200) {
					this.fireEvent("save", this.entryName, this.op, null,
							result.json.body);
					if (!result.json.body.hasHyRecord) {
						this.fireEvent("chisSave", "需要创建高血压档案！");
					}
				}
			},
			doEliminate : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionSimilarityService",
							serviceAction : "saveEliminateHypertensionSimilarity",
							method : "execute",
							body : {
								empiId : this.exContext.args.empiId
										|| this.exContext.ids.empiId,
								similarityId : this.exContext.args.similarityId,
								phrId : this.exContext.args.phrId
							}
						})
				if (result.code == 200) {
					this.fireEvent("save", this.entryName, this.op, null,
							result.json.body)
					this.fireEvent("chisSave")
				}
			},
			doTurnHighRisk : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionSimilarityService",
							serviceAction : "ifHypertensionRiskExist",
							method : "execute",
							empiId : this.exContext.args.empiId
									|| this.exContext.ids.empiId
						})
				var hasRecord = false;
				var hrData;
				var isExistHealthRecord = false;
				var isExistHyRecord = false
				if (result.json.body) {
					hasRecord = result.json.body.isExistHR;
					hrData = result.json.body.hrData;
					isExistHyRecord = result.json.body.isExistHyRecord;
					isExistHealthRecord = result.json.body.isExistHealthRecord;
				}
				if (isExistHyRecord) {
					MyMessageTip.msg("提示", "该病人已建高血压档案，无需再转！", true, 6);
					return;
				}
				if (hasRecord) {
					if (hrData && hrData.effectCase == "1") {
						MyMessageTip.msg("提示", "该病人已建高危人群档案，无需再转！", true, 6);
						return;
					}
				}
				if (!isExistHealthRecord) {
					Ext.Msg.show({
								title : '个人健康档案',
								msg : '该病人尚未建立个人健康档案，不能转高危人群管理，是否建立？',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.showEhrViewWin(["B_01"]);
									}
								},
								scope : this
							});
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionSimilarityService",
							serviceAction : "saveHighRiskHypertensionSimilarity",
							method : "execute",
							body : {
								empiId : this.exContext.args.empiId
										|| this.exContext.ids.empiId,
								similarityId : this.exContext.args.similarityId,
								phrId : this.exContext.args.phrId
							}
						})
				if (result.code == 200) {
					this.fireEvent("save", this.entryName, this.op, null,
							result.json.body)
					this.fireEvent("chisSave");
					if (!hasRecord) {
						Ext.Msg.show({
									title : '高血压高危档案',
									msg : '是否建立高血压高危人群档案？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.showEhrViewWin(['C_06']);
										}
									},
									scope : this
								});
					}
				}
			},
			saveToServer : function(saveData) {
				saveData.empiId = this.exContext.args.empiId
						|| this.exContext.ids.empiId;
				saveData.similarityId = this.exContext.args.similarityId;
				saveData.phrId = this.exContext.args.phrId
				chis.application.hy.script.similarity.HypertensionSimilarityCheckForm.superclass.saveToServer
						.call(this, saveData)
			},
			showEhrViewWin : function(visitModule) {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = visitModule;
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = this.activeTab || 0;
				cfg.needInitFirstPanel = true;
				this.empiId = this.exContext.args.empiId
						|| this.exContext.ids.empiId
				var module = this.midiModules["HypertensionSView_EHRView"
						+ visitModule[0]];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["HypertensionSView_EHRView"
							+ visitModule[0]] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("clearModuleData", this.onClearModuleData, this);
					module.on("save", this.onHealthRecordSave, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.getWin().show();
			},
			onHealthRecordSave : function(entryName, op, json, data) {
				if (entryName == "chis.application.hr.schemas.EHR_HealthRecord") {
					Ext.Msg.show({
						title : '转高危',
						msg : '是否继续转高危操作,并且建立高血压高危人群档案？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.hypertensionSimilarityService",
									serviceAction : "saveHighRiskHypertensionSimilarity",
									method : "execute",
									body : {
										empiId : this.exContext.args.empiId
												|| this.exContext.ids.empiId,
										similarityId : this.exContext.args.similarityId,
										phrId : this.exContext.args.phrId
									}
								})
								if (result.code == 200) {
									this.fireEvent("save", this.entryName,
											this.op, null, result.json.body)
									this.fireEvent("chisSave");
									var module = this.midiModules["HypertensionSView_EHRViewB_01"];
									module.onAddModule("C_06", false);
									module.onActiveModule("C_06", {});
								}
							}
						},
						scope : this
					});
				}
			},
			onClearModuleData : function(exContext) {
				Ext.apply(this.exContext.args, this.cachaData)
			}
		});