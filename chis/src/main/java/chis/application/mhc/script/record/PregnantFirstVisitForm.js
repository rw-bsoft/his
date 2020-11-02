/**
 * 孕妇首次随访表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField","chis.script.util.helper.Helper")
chis.application.mhc.script.record.PregnantFirstVisitForm = function(cfg) {
	cfg.fldDefaultWidth = 160
	cfg.labelWidth = 90;
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.record.PregnantFirstVisitForm.superclass.constructor.apply(
			this, [cfg])
	this.on("loadData", this.onLoadData, this);
	this.on("beforeCreate", this.onBeforeCreate, this)
}
Ext.extend(chis.application.mhc.script.record.PregnantFirstVisitForm, chis.script.BizTableFormView,
		{

			loadData : function() {
				this.initDataId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				chis.application.mhc.script.record.PregnantFirstVisitForm.superclass.loadData
						.call(this);
			},

			onBeforeCreate : function() {
				this.form.getForm().findField("diagnosisDate").enable();
				this.fireEvent("firstVisitData");
			},

			doNew : function() {
				chis.application.mhc.script.record.PregnantFirstVisitForm.superclass.doNew
						.call(this);
				this.data["highRisknesses"] = [];
				this.data["empiId"] = this.exContext.empiData.empiId;
				this.data["highRisknessesChanged"] = false;
				this.riskStore = null;
				var module = this.midiModules["HighRiskModule"];
				if (module) {
					delete module.exContext.args.initRisknesses;
				}
				
				var visitPrecontractTime = this.form.getForm().findField("visitPrecontractTime");
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				visitPrecontractTime.setMinValue(now);
				visitPrecontractTime.validate();
			},

			onReady : function() {
				chis.application.mhc.script.record.PregnantFirstVisitForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var morningSickness = form.findField("morningSickness");
				if (morningSickness) {
					morningSickness.on("select", this.setFieldStatus, this);
				}

				var headache = form.findField("headache");
				if (headache) {
					headache.on("select", this.setFieldStatus, this);
				}

				var edema = form.findField("edema");
				if (edema) {
					edema.on("select", this.setFieldStatus, this);
				}

				var bleeding = form.findField("bleeding");
				if (bleeding) {
					bleeding.on("select", this.setFieldStatus, this);
				}

				var fever = form.findField("fever");
				if (fever) {
					fever.on("select", this.setFieldStatus, this);
				}

				var otherSympton = form.findField("otherSympton");
				if (otherSympton) {
					otherSympton.on("valid", this.setHappenWeeks, this);
				}

//				var category = form.findField("category");
//				if (category) {
//					category.on("select", this.setGeneralComment, this);
//				}

				var selfFeelSymptom = form.findField("selfFeelSymptom");
				if (selfFeelSymptom) {
					selfFeelSymptom.on("select", this.setSymptomDesc, this);
				}

				var highRiskScore = form.findField("highRiskScore");
				if (highRiskScore) {
					highRiskScore.on("lookup", this.getHighRisk, this);
				}

				var weight = form.findField("weight");
				if (weight) {
					weight.on("valid", this.getBMI, this);
				}

				var sbp = form.findField("sbp");
				if (sbp) {
					sbp.on("blur", this.onSbpChange, this);
					sbp.on("keyup", this.onSbpChange, this);
				}

				var dbp = form.findField("dbp");
				if (dbp) {
					dbp.on("blur", this.onDbpChange, this);
					dbp.on("keyup", this.onDbpChange, this);
				}

				var generalComment = form.findField("generalComment");
				if (generalComment) {
					generalComment.on("select", this.setCommentText, this);
				}

				var suggestion = form.findField("suggestion");
				if (suggestion) {
					suggestion.on("select", this.onSuggestionSelect, this);
				}

				var referral = form.findField("referral")
				if (referral) {
					referral.on("select", this.onReferralSelect, this)
				}

				var visitDoctorCode = form.findField("visitDoctorCode");
				if (visitDoctorCode) {
					visitDoctorCode.on("select", this.changeManaUnit, this);
				}
			},

			onSbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					return;
				}
				if (constriction > 500 || constriction < 10) {
					field.markInvalid("收缩压必须在10到500之间！");
					return;
				}
				var diastolicFld = this.form.getForm().findField("dbp");
				var diastolic = diastolicFld.getValue();
				if (constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					diastolicFld.clearInvalid();
				}

			},

			onDbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var diastolic = field.getValue();
				if (!diastolic) {
					return;
				}
				if (diastolic > 500 || diastolic < 10) {
					field.markInvalid("舒张压必须在10到500之间！");
					return;
				}
				var constrictionFld = this.form.getForm().findField("sbp");
				var constriction = constrictionFld.getValue();
				if (constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					constrictionFld.clearInvalid();
				}
			},

			setCommentText : function(field) {
				var value = field.getValue();
				var disable = true;
				if (value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "commentText");
			},

			onSuggestionSelect : function(field) {
				var value = field.getValue();
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("99") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherSuggestion");
			},

			onReferralSelect : function(field) {
				var value = field.getValue();
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "reason");
				this.changeFieldState(disable, "doccol");
			},

			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("visitUnitCode");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				combox.setValue(manageUnit)
				combox.disable();
			},

			onLoadData : function(entryName, body) {
				var visitPrecontractTime = this.form.getForm().findField("visitPrecontractTime");
				var createDate =body["createDate"];
				var d = Date.parseDate(createDate.substr(0,10), "Y-m-d")
				visitPrecontractTime.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(d));
				visitPrecontractTime.validate();
				
				var morningSickness = body["morningSickness"]
				if (morningSickness && morningSickness.key) {
					var disable = false;
					if (morningSickness.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "morningSicknessWeek");
				}

				var headache = body["headache"]
				if (headache && headache.key) {
					var disable = false;
					if (headache.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "headacheWeek");
				}

				var edema = body["edema"]
				if (edema && edema.key) {
					var disable = false;
					if (edema.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "edemaWeek");
				}

				var bleeding = body["bleeding"]
				if (bleeding && bleeding.key) {
					var disable = false;
					if (bleeding.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "bleedingWeek");
				}

				var fever = body["fever"]
				if (fever && fever.key) {
					var disable = false;
					if (fever.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "feverWeek");
				}

				var selfFeelSymptom = body["selfFeelSymptom"]
				if (selfFeelSymptom && selfFeelSymptom.key) {
					var disable = true;
					if (selfFeelSymptom.key == "2") {
						disable = false;
					}
					this.changeFieldState(disable, "symptomDesc");
				}

//				var category = body["category"]
//				if (category) {
//					var disable = true;
//					if (category.key == "2") {
//						disable = false;
//					} else {
//						this.changeFieldState(disable, "commentText");
//					}
//					this.changeFieldState(disable, "generalComment");
//				}

				var otherSympton = body["otherSympton"];
				var disable = true;
				if (otherSympton && otherSympton != "") {
					disable = false;
				}
				this.changeFieldState(disable, "gestationalWeeks");

				var generalComment = body["generalComment"];
				if (generalComment && generalComment.key) {
					var disable = true;
					if (generalComment.key == "2") {
						disable = false;
					}
					this.changeFieldState(disable, "commentText");
				}

				var suggestion = body["suggestion"];
				if (suggestion && suggestion.key) {
					var disable = true;
					var valueArray = suggestion.key.split(",");
					if (valueArray.indexOf("99") != -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherSuggestion");
				}

				var referral = body["referral"];
				if (referral && referral.key) {
					var disable = true;
					if (referral.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "reason");
					this.changeFieldState(disable, "doccol");
				}
			},

			getHighRisk : function(field) {
				if (!this.riskStore && this.op == "create") {
					this.fireEvent("openHighRiskForm", this.op, this);
				} else {
					this.openHighRisknessForm();
				}
			},

			openHighRisknessForm : function(risknesses) {
				var module = this.createCombinedModule("HighRiskModule",
						this.refHighRiskModule);
				module.on("moduleClose", this.onModuleClose, this);
				module.__actived = this.riskStore == null ? false : true;
				var args = {
					"visitId" : "0000000000000000"
				};
				if (this.riskStore) {
					args.initRisknesses = null;
				} else if (this.op == "create") {
					args.initRisknesses = risknesses;
				}
				Ext.apply(this.exContext.args, args);
				this.refreshExContextData(module, this.exContext);
				module.getWin().show();
			},

			onModuleClose : function(records, store) {

				var highRiskScore = records.highRiskScore;
				var highRiskLevel = records.highRiskLevel;
				var highRisknesses = records.highRisknesses;

				this.riskStore = store;
				var form = this.form.getForm();

				var scoreField = form.findField("highRiskScore");
				scoreField.setValue(highRiskScore);

				var levelField = form.findField("highRiskLevel");
				levelField.setValue(highRiskLevel);

				this.data["highRisknesses"] = highRisknesses;
				this.data["highRisknessesChanged"] = true;
			},

			getBMI : function(field) {
				var wight = field.getValue();
				if (wight) {
					var bmi = this.form.getForm().findField("BMI");
					this.fireEvent("changeBMI", wight, bmi);
				}
			},

			setFieldStatus : function(field) {
				var value = field.getValue();
				var disable = false;
				if (value == "n") {
					disable = true;
				}
				var fieldName = field.getName();
				var changeFiled;
				if (fieldName == "morningSickness") {
					changeFiled = "morningSicknessWeek";
				} else if (fieldName == "headache") {
					changeFiled = "headacheWeek";
				} else if (fieldName == "edema") {
					changeFiled = "edemaWeek";
				} else if (fieldName == "bleeding") {
					changeFiled = "bleedingWeek";
				} else if (fieldName == "fever") {
					changeFiled = "feverWeek";
				}
				this.changeFieldState(disable, changeFiled);
			},

//			setGeneralComment : function(field) {
//				var value = field.getValue();
//				var disable = true;
//				if (value && value == "2") {
//					disable = false;
//				} else {
//					this.changeFieldState(disable, "commentText");
//				}
//				this.changeFieldState(disable, "generalComment");
//
//			},

			setSymptomDesc : function(field) {
				var value = field.getValue();
				var disable = true;
				if (value && value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "symptomDesc");
			},

			setHappenWeeks : function(field) {
				var value = field.getValue();
				var disable = true;
				if (value && value != "") {
					disable = false;
				}
				this.changeFieldState(disable, "gestationalWeeks");
			},

			doSave : function() {
				this.fireEvent("recordSave");
			},

			getSaveData : function() {
				var values = this.getFormData();
				if (!this.data["highRisknessesChanged"] && !this.initDataId) {
					// Ext.Msg.alert("提示信息","还未进行高危评定,无法保存随访记录!");
					this.getHighRisk()
					return;
				}
				values["highRisknesses"] = this.data["highRisknesses"]
				values["highRisknessesChanged"] = this.data["highRisknessesChanged"]
				return values;
			}

		});