// 转组信息显示窗口。
$package("chis.application.hy.script.fixgroup");

$import("chis.script.BizFieldSetFormView",
		"chis.application.hy.script.HypertensionUtils");

chis.application.hy.script.fixgroup.HypertensionGroupForm = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.labelAlign = "left";
	cfg.labelWidth = 140;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.entryNames = "MDC_HypertensionFixGroup";
	chis.application.hy.script.fixgroup.HypertensionGroupForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("aboutToSave", this.onAboutToSave, this);
	this.on("afterCreate", this.onAfterCreate, this);
	this.on("doNew", this.onDoNew, this);
	this.sex = this.exContext.empiData.sexCode;
	var status= this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
	if(status=='1'){
   		Ext.Msg.alert("友情提醒：","高血压档案已注销，请先恢复高血压档案！");
   		return;
    }
	Ext.apply(this, chis.application.hy.script.HypertensionUtils);
};

Ext.extend(chis.application.hy.script.fixgroup.HypertensionGroupForm,
		chis.script.BizFieldSetFormView, {
			onDoNew : function() {
				
				this.data.empiId = this.exContext.ids.empiId;
				this.data.phrId = this.exContext.ids.phrId;
			},

			doAdd : function() {
				var status= this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
				if(status=='1'){
   					Ext.Msg.alert("友情提醒：","高血压档案已注销，请先恢复高血压档案！");
   					return;
				}
				this.fireEvent("add", this);
				//this.form.getForm().findField("fixDate").enable();
			},

			loadInitData : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "initializeCreateGroup",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId,
								autoCreate : this.autoCreate
							}
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadInitData);
					return;
				}
				if (this.form.getTopToolbar()) {
					var bts = this.form.getTopToolbar().items;
					bts.items[0].enable();
				}
				this.doNew();
				this.initFormData(result.json.body);
				this.validate();
				this.sn = result.json.body.sn;

				if (this.autoCreate) {
					this.initRiskness = result.json.body.riskiness;
					this.initTargetHurt = result.json.body.targetHurt;
					this.form.getForm().findField("riskiness")
							.setValue(this.initRiskness);
					this.form.getForm().findField("targetHurt")
							.setValue(this.initTargetHurt);
				}
				return result.json.body;
			},
			doSave : function() {
				var status= this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
				if(status=='1'){
   					Ext.Msg.alert("友情提醒：","高血压档案已注销，请先恢复高血压档案！");
   					return;
				}
				this.data.phrId = this.exContext.ids.phrId;
				chis.application.hy.script.fixgroup.HypertensionGroupForm.superclass.doSave
						.call(this);
			},

			// onSave : function(entryName,op,json,data){
			// this.fireEvent("add", op,json,data);
			// },

			saveToServer : function(saveData) {
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				saveData.sn = this.sn;
				var flag1 = this.checkEqual(this.initRiskness,
						saveData.riskiness);
				var flag2 = this.checkEqual(this.initTargetHurt,
						saveData.targetHurt);
				if (this.autoCreate && (!flag1 || !flag2)) {
					var msg = "";
					if (!flag1) {
						var riskness = this.form.getForm()
								.findField("riskiness").getRawValue();
						msg = "危险因素跟随访预期不一致<br/>随访预期：" + this.initRiskness.text
								+ "<br/>评估结果：" + riskness + "<br/>要继续保存吗？";
					} else if (!flag2) {
						var targetHurt = this.form.getForm()
								.findField("targetHurt").getRawValue();
						msg = "靶器官伤害跟随访预期不一致<br/>随访预期："
								+ this.initTargetHurt.text + "<br/>评估结果："
								+ targetHurt + "<br/>要继续保存吗？";
					}
					Ext.Msg.show({
								title : "提示",
								msg : msg,
								modal : true,
								icon : Ext.MessageBox.QUESTION,
								buttons : Ext.Msg.YESNO,
								fn : function(btn, text) {
									if (btn == "no") {
										return;
									}
									this.save(saveData);
								},
								scope : this
							});
				} else {
					this.save(saveData);
				}
			},

			checkEqual : function(value1, value2) {
				if (!value2 || !value1) {
					return false;
				}
				var array1 = value1.key.split(",");
				var array2 = value2.split(",");
				if (array1.length != array2.length) {
					return false;
				}
				for (var i = 0; i < array2.length; i++) {
					for (var j = 0; j < array1.length; j++) {
						if (array1[i] == array2[j]) {
							break;
						}
					}
					if (j == array1.length) {
						return false;
					}
				}
				return true;
			},

			save : function(saveData) {
				this
						.fireEvent("aboutToSave", this.entryName, this.op,
								saveData);
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : 'saveHypertensionFixGroup',
							method : "execute",
							schema : this.entryName,
							body : saveData,
							op : this.op
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg, this.save,
										[saveData]);
								return;
							}
							this.data.planId = json.body.planId;
							this.op = "update";
							if (this.form.getTopToolbar()) {
								var btns = this.form.getTopToolbar().items;
								if (btns) {
									btns.item(0).disable();
								}
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.fireEvent("afterCreate", this.entryName,
									this.op, json, this.data);
						}, this);// jsonRequest
			},

			onAfterCreate : function(entryName, op, json) {
				this.op = "update";
				this.autoCreate = false;
				var form = this.form.getForm();
				// @@ 管理分组
				var hypertensionGroup = json.body.hypertensionGroup;
				if (hypertensionGroup) {
					var hyperGroupText = json.body.hypertensionGroup_text;
					var group = {
						key : hypertensionGroup,
						text : hyperGroupText
					};
					form.findField("hypertensionGroup").setValue(group);
				}
				// @@ 危险分层
				var riskLevel = json.body.riskLevel;
				if (riskLevel) {
					var riskLevelText = json.body.riskLevel_text;
					var risk = {
						key : riskLevel,
						text : riskLevelText
					};
					form.findField("riskLevel").setValue(risk);
				}
				// @@ 血压级别
				var hypertensionLevel = json.body.hypertensionLevel;
				if (hypertensionLevel) {
					var hypertensionLevelText = json.body.hypertensionLevel_text;
					var hl = {
						key : hypertensionLevel,
						text : hypertensionLevelText
					};
					form.findField("hypertensionLevel").setValue(hl);
				}
			},

			onAboutToSave : function(entryName, op, saveData) {

			},

			onReady : function() {
				chis.application.hy.script.fixgroup.HypertensionGroupForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				// form.findField("constriction").on("blur",this.onConstrictionChange,
				// this);
				form.findField("constriction").on("keyup",
						this.onConstrictionChange, this);
				form.findField("constriction").on("change",
						this.onBloodPressureInput, this);

				// form.findField("diastolic").on("blur",this.onDiastolicChange,
				// this);
				form.findField("diastolic").on("keyup", this.onDiastolicChange,
						this);
				form.findField("diastolic").on("change",
						this.onBloodPressureInput, this);

				//form.findField("TC").on("blur", this.onTCChange, this);
				//form.findField("TC").on("keyup", this.onTCChange, this);
				//form.findField("TC").allowBlank = false;
				//form.findField("TC").validate();

				form.findField("LDL").on("blur", this.onLDLChange, this);
				form.findField("LDL").on("keyup", this.onLDLChange, this);

				form.findField("HDL").on("blur", this.onHDLChange, this);
				form.findField("HDL").on("keyup", this.onHDLChange, this);

				form.findField("height").on("blur", this.onHeightChange, this);
				form.findField("height").on("keyup", this.onHeightChange, this);

				form.findField("weight").on("blur", this.onWeightChange, this);
				form.findField("weight").on("keyup", this.onWeightChange, this);

				form.findField("waistLine").on("blur", this.onWaistLineChange,
						this);
				form.findField("waistLine").on("keyup", this.onWaistLineChange,
						this);

				form.findField("microalbuminuria").on("blur",
						this.onMicroalbuminuriaChange, this);
				form.findField("microalbuminuria").on("keyup",
						this.onMicroalbuminuriaChange, this);

				form.findField("buminuriaSerum").on("change",
						this.onBuminuriaSerumChange, this);
				form.findField("buminuriaSerum").on("keyup",
						this.onBuminuriaSerumChange, this);

				form.findField("fbs").on("blur", this.onFbsChange, this);
				form.findField("fbs").on("change", this.onFbsChange, this);

				var IGT = form.findField("IGT")
				IGT.on("blur", this.onIGTChange, this);
				IGT.on("keyup", this.onIGTChange, this);

				var sokolowLyons = form.findField("SokolowLyons");
				sokolowLyons.on("blur", this.onSokolowLyonsChange, this);
				sokolowLyons.on("keyup", this.onSokolowLyonsChange, this);

				var cornell = form.findField("Cornell");
				cornell.on("blur", this.onCornellChange, this);
				cornell.on("keyup", this.onCornellChange, this);

				var lvmi = form.findField("LVMI");
				lvmi.on("blur", this.onLvmiChange, this);
				lvmi.on("keyup", this.onLvmiChange, this);

				var carotidUltrasound = form.findField("carotidUltrasound");
				carotidUltrasound.on("blur", this.onCarotidUltrasound, this);
				carotidUltrasound.on("keyup", this.onCarotidUltrasound, this);

				var arteryGruelTypeMottling = form
						.findField("arteryGruelTypeMottling");
				arteryGruelTypeMottling.on("blur",
						this.onArteryGruelTypeMottling, this);
				arteryGruelTypeMottling.on("keyup",
						this.onArteryGruelTypeMottling, this);

				var sphygmus = form.findField("sphygmus");
				sphygmus.on("blur", this.onSphygmusChange, this);
				sphygmus.on("keyup", this.onSphygmusChange, this);

				var ankleOrArmBPI = form.findField("ankleOrArmBPI");
				ankleOrArmBPI.on("blur", this.onAnkleOrArmBPI, this);
				ankleOrArmBPI.on("keyup", this.onAnkleOrArmBPI, this);

				var GFRDecreased = form.findField("GFRDecreased");
				GFRDecreased.on("blur", this.onGFRDecreased, this);
				GFRDecreased.on("keyup", this.onGFRDecreased, this);

				var proteinuria = form.findField("proteinuria");
				proteinuria.on("blur", this.onProteinuria, this);
				proteinuria.on("keyup", this.onProteinuria, this);

				form.findField("serumCreatinine").on("blur",
						this.onSerumCreatinineChange, this);
				form.findField("serumCreatinine").on("keyup",
						this.onSerumCreatinineChange, this);

				var complication = form.findField("complication");
				if (complication) {
					complication.on("select", this.onComboBoxSelect3, this);
				}
				var riskiness = form.findField("riskiness");
				if (riskiness) {
					riskiness.on("select", this.onComboBoxSelect1, this);
				}
				var targetHurt = form.findField("targetHurt");
				if (targetHurt) {
					targetHurt.on("select", this.onComboBoxSelect2, this);
				}

				var pbs = form.findField("pbs");
				if (pbs) {
					pbs.on("blur", this.onPbsChange, this);
					pbs.on("change", this.onPbsChange, this);
				}

				var ghp = form.findField("ghp");
				if (ghp) {
					ghp.on("blur", this.onGhpChange, this);
				}
			},

			onConstrictionChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					return;
				}
				if (constriction > 500 || constriction < 50) {
					field.markInvalid("收缩压必须在0到500之间！");
					return;
				}
				var diastolicFld = this.form.getForm().findField("diastolic");
				var diastolic = diastolicFld.getValue();
				if (constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					diastolicFld.clearInvalid();
				}
				// this.addBPUnnormalToRiskness(constriction, diastolic);
			},

			onDiastolicChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var diastolic = field.getValue();
				if (!diastolic) {
					return;
				}
				if (diastolic > 500 || diastolic < 50) {
					field.markInvalid("舒张压必须在0到500之间！");
					return;
				}
				var constrictionFld = this.form.getForm()
						.findField("constriction");
				var constriction = constrictionFld.getValue();
				if (constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					constrictionFld.clearInvalid();
				}
				// this.addBPUnnormalToRiskness(constriction, diastolic);
			},

			/*
			 * addBPUnnormalToRiskness : function(constriction, diastolic) { var
			 * risknessField = this.form.getForm().findField("riskiness"); if
			 * (!this.isBPNormal(constriction, diastolic)) { if
			 * (risknessField.getValue() != 0) {
			 * risknessField.setValue(risknessField.getValue() + ",8"); } else {
			 * risknessField.setValue("8"); } } else { var riskValues =
			 * risknessField.getValue().split(","); var newValue = "";
			 * riskValues.remove("8"); for (var i = 0; i < riskValues.length;
			 * i++) { newValue += riskValues[i] + ","; } if (newValue == "") {
			 * risknessField.setValue({ key : 0, text : "无" }); } else {
			 * risknessField.setValue(newValue.substring(0, newValue.length)); } } },
			 */

			onBloodPressureInput : function(field, newValue, oldValue) {
				// var constrictionFld =
				// this.form.getForm().findField("constriction");
				// var constriction = constrictionFld.getValue();
				// var diastolicFld =
				// this.form.getForm().findField("diastolic");
				// var diastolic = diastolicFld.getValue();
				// if (constriction && diastolic &&
				// this.isBPNormal(constriction, diastolic)) {
				// Ext.Msg.alert('提示', '非高血压，无需建档，继续保存将不生成随访计划。');
				// }
			},

			onTCChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("TC");
				}
				field.validate();
				var tc = field.getValue();
				var ldlF = this.form.getForm().findField("LDL");
				var hdlF = this.form.getForm().findField("HDL");
				var riskCombo = this.form.getForm().findField("riskiness");
				var value = riskCombo.getValue();
				var valueArray = value.split(",");
				if (!tc) {
					if (!ldlF.getValue() && !hdlF.getValue()) {
						// ldlF.allowBlank = false;
						// ldlF.validate();
						// hdlF.allowBlank = false;
						// hdlF.validate();
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
					return;
				}
				// ldlF.allowBlank = true;
				// ldlF.validate();
				// hdlF.allowBlank = true;
				// hdlF.validate();
				if (tc >= 5.7) {
					if (valueArray.indexOf("0") != -1) {
						riskCombo.setValue(4);
					} else if (valueArray.indexOf("4") == -1) {
						value += ",4";
						riskCombo.setValue(value);
					}
				} else {
					var ldl = this.form.getForm().findField("LDL").getValue();
					var hdl = this.form.getForm().findField("HDL").getValue();
					if ((!ldl || ldl <= 3.3) && (!hdl || hdl >= 1.0)
							&& valueArray.indexOf("4") != -1) {
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
				}
			},

			onLDLChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("LDL");
				}
				field.validate();
				var ldl = field.getValue();
				var tcF = this.form.getForm().findField("TC");
				var hdlF = this.form.getForm().findField("HDL");
				var riskCombo = this.form.getForm().findField("riskiness");
				var value = riskCombo.getValue();
				var valueArray = value.split(",");
				if (!ldl) {
					if (!tcF.getValue() && !hdlF.getValue()) {
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
					return;
				}
				if (ldl > 3.3) {
					if (valueArray.indexOf("0") != -1) {
						riskCombo.setValue(4);
					} else if (valueArray.indexOf("4") == -1) {
						value += ",4";
						riskCombo.setValue(value);
					}
				} else {
					var tc = this.form.getForm().findField("TC").getValue();
					var hdl = this.form.getForm().findField("HDL").getValue();
					if ((!tc || tc < 5.7) && (!hdl || hdl >= 1.0)
							&& valueArray.indexOf("4") != -1) {
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
				}
			},

			onHDLChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("HDL");
				}
				field.validate();
				var hdl = field.getValue();
				var tcF = this.form.getForm().findField("TC");
				var ldlF = this.form.getForm().findField("LDL");
				var riskCombo = this.form.getForm().findField("riskiness");
				var value = riskCombo.getValue();
				var valueArray = value.split(",");
				if (!hdl) {
					if (!tcF.getValue() && !ldlF.getValue()) {
						// tcF.allowBlank = false;
						// tcF.validate();
						// ldlF.allowBlank = false;
						// ldlF.validate();
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
					return;
				}
				// tcF.allowBlank = true;
				// tcF.validate();
				// ldlF.allowBlank = true;
				// ldlF.validate();
				if (hdl < 1.0) {
					if (valueArray.indexOf("0") != -1) {
						riskCombo.setValue(4);
					} else if (valueArray.indexOf("4") == -1) {
						value += ",4";
						riskCombo.setValue(value);
					}
				} else {
					var tc = this.form.getForm().findField("TC").getValue();
					var ldl = this.form.getForm().findField("LDL").getValue();
					if ((!tc || tc < 5.7) && (!ldl || ldl <= 3.3)
							&& valueArray.indexOf("4") != -1) {
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								4));
					}
				}
			},

			onHeightChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("height");
				}
				if (!field.validate()) {
					return;
				}
				var height = field.getValue();
				if (!height) {
					return;
				}
				var weight = this.form.getForm().findField("weight").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					var bmi = (weight / temp).toFixed(2);
					this.form.getForm().findField("bmi").setValue(bmi);
					this.checkBMI(bmi);
				}
			},

			onWeightChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("weight");
				}
				if (!field.validate()) {
					return;
				}
				var weight = field.getValue();
				if (!weight) {
					return;
				}
				var height = this.form.getForm().findField("height").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					var bmi = (weight / temp).toFixed(2);
					this.form.getForm().findField("bmi").setValue(bmi);
					this.checkBMI(bmi);
				}
			},

			onWaistLineChange : function(field) {
				if (!field) {
					field = this.form.getForm().findField("onwaistLine");
				}
				field.validate();
				var bmi = this.form.getForm().findField("bmi").getValue();
				this.checkBMI(bmi);
			},

			onFbsChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("fbs");
				}
				field.validate();
				var fbsVal = field.getValue();
				var igtVal = form.findField("IGT").getValue();

				this.changeRiskiness(fbsVal, igtVal);

				if (fbsVal >= 7.0) {
					this.changeComplication("60",
							"空腹血糖 ：≥7.0mmol/L( 126mg/dL)", "add");
				} else {
					this.changeComplication("60",
							"空腹血糖 ：≥7.0mmol/L( 126mg/dL)", "del");
				}
			},

			onIGTChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("IGT");
				}
				field.validate();
				var igtVal = field.getValue();
				var fbsVal = form.findField("fbs").getValue();

				this.changeRiskiness(fbsVal, igtVal);
			},

			changeRiskiness : function(fbsVal, igtVal) {
				var form = this.form.getForm();
				var riskCombo = form.findField("riskiness");
				var value = riskCombo.getValue();
				var valueArray = value.split(",");
				if (!igtVal && !fbsVal) {
					riskCombo.setValue(this.removeItemFromArray(valueArray, 3));
				}
				if ((igtVal >= 7.8 && igtVal <= 11.0)
						|| (fbsVal >= 6.1 && fbsVal <= 6.9)) {
					if (valueArray.indexOf("3") == -1) {
						value += ",3";
						riskCombo.setValue(value);
					}
				} else {
					riskCombo.setValue(this.removeItemFromArray(valueArray, 3));
				}
			},

			onSokolowLyonsChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("SokolowLyons");
				}
				field.validate();
				var slv = field.getValue();
				var cornellVal = form.findField("Cornell").getValue();
				var lvmiVal = form.findField("LVMI").getValue();
				this.changeTargetHurt1(slv, cornellVal, lvmiVal);
			},

			onCornellChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("Cornell");
				}
				field.validate();
				var slv = form.findField("SokolowLyons").getValue();
				var cornellVal = field.getValue();
				var lvmiVal = form.findField("LVMI").getValue();
				this.changeTargetHurt1(slv, cornellVal, lvmiVal);
			},

			onLvmiChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("LVMI");
				}
				field.validate();
				var slv = form.findField("SokolowLyons").getValue();
				var cornellVal = form.findField("Cornell").getValue();
				var lvmiVal = field.getValue();

				this.changeTargetHurt1(slv, cornellVal, lvmiVal);
			},

			changeTargetHurt1 : function(slv, cornell, lvmi) {
				var form = this.form.getForm();
				var targetHurtCombo = form.findField("targetHurt");
				var value = targetHurtCombo.getValue();
				var valueArray = value.split(",");
				if (!slv && !cornell && !lvmi) {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 1));
				}

				var sexCode = this.exContext.empiData.sexCode;
				var lvmiUnusual = false;
				if (sexCode == '1' && lvmi >= 125) {
					lvmiUnusual = true;
				}
				if (sexCode == '2' && lvmi >= 120) {
					lvmiUnusual = true;
				}
				if (slv > 38 || cornell > 2440 || lvmiUnusual) {
					if (valueArray.indexOf("1") == -1
							&& valueArray.indexOf("10") == -1) {
						value += ",1";
						targetHurtCombo.setValue(value);
					} else if (valueArray.indexOf("10") > -1) {
						value = "1";
						targetHurtCombo.setValue(value);
					}
				} else {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 1));
				}
			},

			onCarotidUltrasound : function(field) {
				if (!field) {
					field = form.findField("carotidUltrasound");
				}
				field.validate();
				var form = this.form.getForm();
				var imtVal = field.getValue();
				var agtmVal = form.findField("arteryGruelTypeMottling")
						.getValue();

				this.changeTargetHurt2(imtVal, agtmVal);
			},

			onArteryGruelTypeMottling : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("arteryGruelTypeMottling");
				}
				field.validate();
				var imtVal = form.findField("carotidUltrasound").getValue();
				var agtmVal = field.getValue();

				this.changeTargetHurt2(imtVal, agtmVal);
			},

			changeTargetHurt2 : function(imtVal, agtmVal) {
				var form = this.form.getForm();
				var targetHurtCombo = form.findField("targetHurt");
				var value = targetHurtCombo.getValue();
				var valueArray = value.split(",");
				if (!imtVal && !agtmVal) {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 2));
				}

				if (imtVal > 0.9 || agtmVal == 'y') {
					if (valueArray.indexOf("2") == -1
							&& valueArray.indexOf("10") == -1) {
						value += ",2";
						targetHurtCombo.setValue(value);
					} else if (valueArray.indexOf("10") > -1) {
						value = "2";
						targetHurtCombo.setValue(value);
					}
				} else {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 2));
				}
			},

			onSphygmusChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("sphygmus");
				}
				field.validate();
				var sphygmusVal = field.getValue();
				var targetHurtCombo = form.findField("targetHurt");
				var value = targetHurtCombo.getValue();
				var valueArray = value.split(",");
				if (!sphygmusVal) {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 3));
				}

				if (sphygmusVal > 12) {
					if (valueArray.indexOf("3") == -1
							&& valueArray.indexOf("10") == -1) {
						value += ",3";
						targetHurtCombo.setValue(value);
					} else if (valueArray.indexOf("10") > -1) {
						value = "3";
						targetHurtCombo.setValue(value);
					}
				} else {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 3));
				}
			},

			onAnkleOrArmBPI : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("ankleOrArmBPI");
				}
				field.validate();
				var ankleOrArmBPIVal = field.getValue();
				var targetHurtCombo = form.findField("targetHurt");
				var value = targetHurtCombo.getValue();
				var valueArray = value.split(",");
				if (!ankleOrArmBPIVal && ankleOrArmBPIVal != 0) {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 4));
				}

				if (ankleOrArmBPIVal < 0.9 && ankleOrArmBPIVal !== '') {
					if (valueArray.indexOf("4") == -1
							&& valueArray.indexOf("10") == -1) {
						value += ",4";
						targetHurtCombo.setValue(value);
					} else if (valueArray.indexOf("10") > -1) {
						value = "4";
						targetHurtCombo.setValue(value);
					}
				} else {
					targetHurtCombo.setValue(this.removeItemFromArray(
							valueArray, 4));
				}
			},

			onGFRDecreased : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("GFRDecreased");
				}
				field.validate();
				var sc = form.findField("serumCreatinine").getValue();
				var GFRDecreasedVal = field.getValue();
				this.changeTargetHurt5(sc, GFRDecreasedVal);
			},

			onProteinuria : function(field) {
				if (!field) {
					var form = this.form.getForm();
					field = form.findField("proteinuria");
				}
				field.validate();
				var proteinuria = field.getValue();
				if (proteinuria > 300) {
					this.changeComplication("33", "蛋白尿 ＞300mg/24h", "add");
				} else {
					this.changeComplication("33", "蛋白尿 ＞300mg/24h", "del");
				}
			},

			onSerumCreatinineChange : function(field, newValue) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("serumCreatinine");
				}
				field.validate();
				var sc = field.getValue();
				var GFRDecreasedVal = form.findField("GFRDecreased").getValue();
				this.changeTargetHurt5(sc, GFRDecreasedVal);

				if ((sc > 133 && this.sex == 1) || (sc > 124 && this.sex == 2)) {
					this.changeComplication("32",
							"血肌酐[男性＞133mol/L(1.5mg/dL) 女性＞124mol/L(1.4mg/dL)]",
							"add");
				} else {
					this.changeComplication("32",
							"血肌酐[男性＞133mol/L(1.5mg/dL) 女性＞124mol/L(1.4mg/dL)]",
							"del");
				}
			},

			changeTargetHurt5 : function(sc, GFRDecreasedVal) {
				var targetCombo = this.form.getForm().findField("targetHurt");
				var value = targetCombo.getValue();
				var valueArray = value.split(",");
				if (!sc) {
					targetCombo.setValue(this
							.removeItemFromArray(valueArray, 5));
				}
				if (((sc >= 115 && sc <= 133 && this.sex == 1) || (sc >= 107
						&& sc <= 124 && this.sex == 2))
						|| GFRDecreasedVal == 'y') {
					if (valueArray.indexOf("10") != -1) {
						targetCombo.setValue(5);
					} else if (valueArray.indexOf("5") == -1) {
						value += ",5";
						targetCombo.setValue(value);
					}
				} else {
					if (valueArray.indexOf("5") != -1) {
						targetCombo.setValue(this.removeItemFromArray(
								valueArray, 5));
					}
				}
			},

			onMicroalbuminuriaChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("microalbuminuria");
				}
				field.validate();
				var bs = form.findField("buminuriaSerum").getValue();
				var mb = field.getValue();
				this.changeTargetHurt6(bs, mb);
			},

			onBuminuriaSerumChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("buminuriaSerum");
				}
				field.validate();
				var bs = field.getValue();
				var mb = form.findField("microalbuminuria").getValue();

				this.changeTargetHurt6(bs, mb);
			},

			changeTargetHurt6 : function(bs, mb) {
				var targetCombo = this.form.getForm().findField("targetHurt");
				var value = targetCombo.getValue();
				var valueArray = value.split(",");
				if (!bs && !mb) {
					targetCombo.setValue(this
							.removeItemFromArray(valueArray, 6));
					return;
				}
				if ((bs >= 22 && this.sex == 1) || (bs >= 31 && this.sex == 2)
						|| (mb >= 30 && mb <= 300)) {
					if (valueArray.indexOf("10") != -1) {
						targetCombo.setValue(6);
					} else if (valueArray.indexOf("6") == -1) {
						value += ",6";
						targetCombo.setValue(value);
					}
				} else {
					targetCombo.setValue(this
							.removeItemFromArray(valueArray, 6));
				}
			},

			onComboBoxSelect1 : function(combo, record, index) {
				var value = combo.getValue();
				var thisKey = record.data.key;
				var thisChecked = record.data.checked;
				var values = value.split(",");
				var newValue = [];
				for (var i = 0; i < values.length; i++) {
					var v = values[i];
					if (v == thisKey && thisKey == "12") {
						newValue = [];
						newValue.push("12")
						break;
					} else if (v != "12") {
						newValue.push(v)
					}
				}
				combo.clearValue();
				combo.setValue(newValue.join(","))
			},
			onComboBoxSelect2 : function(combo, record, index) {
				var value = combo.getValue();
				var thisKey = record.data.key;
				var thisChecked = record.data.checked;
				var values = value.split(",");
				var newValue = [];
				for (var i = 0; i < values.length; i++) {
					var v = values[i];
					if (v == thisKey && thisKey == "10") {
						newValue = [];
						newValue.push("10")
						break;
					} else if (v != "10") {
						newValue.push(v)
					}
				}
				combo.clearValue();
				combo.setValue(newValue.join(","))
			},
			onComboBoxSelect3 : function(combo, record, index) {
				var value = combo.getValue();
				var thisKey = record.data.key;
				var thisChecked = record.data.checked;
				var values = value.split(",");
				var newValue = [];
				for (var i = 0; i < values.length; i++) {
					var v = values[i];
					if (v == thisKey && thisKey == "16") {
						newValue = [];
						newValue.push("16")
						break;
					} else if (v != "16") {
						newValue.push(v)
					}
				}
				combo.clearValue();
				combo.setValue(newValue.join(","))
			},

			onPbsChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("pbs");
				}
				field.validate();
				var pbsVal = field.getValue();
				if (pbsVal >= 11.1) {
					this.changeComplication("61",
							"餐后血糖 ：≥11.1mmol/L( 200mg/dL）", "add");
				} else {
					this.changeComplication("61",
							"餐后血糖 ：≥11.1mmol/L( 200mg/dL）", "del");
				}
			},

			onGhpChange : function(field) {
				var form = this.form.getForm();
				if (!field) {
					field = form.findField("ghp");
				}
				field.validate();
				var ghpVal = field.getValue();
				if (ghpVal >= 6.5) {
					this
							.changeComplication("62", "糖化血红蛋白：（ HbA1c）≥6.5%",
									"add");
				} else {
					this
							.changeComplication("62", "糖化血红蛋白：（ HbA1c）≥6.5%",
									"del");
				}
			},

			changeComplication : function(value, text, op) {
				var form = this.form.getForm();
				var complication = form.findField("complication");
				var oVal = complication.getValue();
				var oTxt = complication.getRawValue();
				var cVal = "";
				var cTxt = "";
				if (op == "add") {
					if (oVal.indexOf(value) == -1) {
						if (oVal == "16") {
							complication.clearValue();
							cVal = value;
							cTxt = text;
						} else {
							cVal = oVal + "," + value;
							cTxt = oTxt + "," + text;
						}
						var nVal = {
							'key' : cVal,
							'text' : cTxt
						};
						complication.setValue(nVal);
					}
				}
				if (op == 'del') {
					if (oVal.indexOf(value) != -1) {
						var valArray = oVal.split(",");
						for (var i = 0; i < valArray.length; i++) {
							if (value == valArray[i]) {
								delete valArray[i];
								break;
							}
						}
						if (valArray.length != 0) {
							cVal = valArray.join(',');
						} else {
							cVal = "";
						}
						var txtArray = oTxt.split(',');
						for (var j = 0; j < txtArray.length; j++) {
							if (text == txtArray[j]) {
								delete txtArray[j];
								break;
							}
						}
						if (txtArray.length != 0) {
							cTxt = txtArray.join(",");
						} else {
							cTxt = "";
						}
						var nVal = {
							'key' : cVal,
							'text' : cTxt
						};
						complication.setValue(nVal);
					}
				}
			},

			validate : function() {
				var form = this.form.getForm();
				this.onConstrictionChange(form.findField("constriction"));
				this.onDiastolicChange(form.findField("diastolic"));
				this.onTCChange(form.findField("TC"));
				this.onLDLChange(form.findField("LDL"));
				this.onHDLChange(form.findField("HDL"));
				this.onWaistLineChange(form.findField("waistLine"));
				this.onFbsChange(form.findField("fbs"));
				this.onIGTChange(form.findField("IGT"));
				this.onSokolowLyonsChange(form.findField("SokolowLyons"));
				this.onCornellChange(form.findField("Cornell"));
				this.onLvmiChange(form.findField("LVMI"));
				this.onCarotidUltrasound(form.findField("carotidUltrasound"));
				this.onArteryGruelTypeMottling(form
						.findField("arteryGruelTypeMottling"));
				this.onSphygmusChange(form.findField("sphygmus"));
				this.onAnkleOrArmBPI(form.findField("ankleOrArmBPI"));
				this.onSerumCreatinineChange(form.findField("serumCreatinine"));
				this.onGFRDecreased(form.findField("GFRDecreased"));
				this.onProteinuria(form.findField("proteinuria"));
				this.onSerumCreatinineChange(form.findField("serumCreatinine"));
				this.onMicroalbuminuriaChange(form
						.findField("microalbuminuria"));
				this.onBuminuriaSerumChange(form.findField("buminuriaSerum"));
				this.onPbsChange(form.findField("pbs"));
				this.onGhpChange(form.findField("ghp"));

				if (false == form.isValid()) {
					return false;
				}
				/*
				 * var count = 0; if
				 * (form.findField("serumCreatinine").getValue()) { count++; }
				 * if (form.findField("microalbuminuria").getValue() ||
				 * form.findField("buminuriaSerum").getValue()) { count++; } if
				 * (count < 2) { Ext.Msg .alert("提示", "左心室肥厚<br/>动脉壁增厚<br/>血清肌酐<br/>尿白蛋白数（白蛋白/肌酐比）<br/>必须至少有两项。");
				 * return false; }
				 */
				return true;
			},

			removeItemFromArray : function(valueArray, item) {
				var newValue = "";
				for (var i = 0; i < valueArray.length; i++) {
					if (valueArray[i] != item) {
						newValue += "," + valueArray[i];
					}
				}
				if (newValue == "") {
					return "0";
				} else {
					return newValue.substring(1, newValue.length);
				}
			},

			checkBMI : function(bmi) {
				if (!bmi) {
					return;
				}
				var riskCombo = this.form.getForm().findField("riskiness");
				var value = riskCombo.getValue();
				var valueArray = value.split(",");
				var waistLine = this.form.getForm().findField("waistLine")
						.getValue();
				if (bmi >= 28 || (waistLine >= 90 && this.sex == 1)
						|| (waistLine >= 85 && this.sex == 2)) {
					if (valueArray.indexOf("12") != -1) {
						riskCombo.setValue(6);
					} else if (valueArray.indexOf("6") == -1) {
						value += ",6";
						riskCombo.setValue(value);
					}
				} else {
					if (valueArray.indexOf("6") != -1) {
						riskCombo.setValue(this.removeItemFromArray(valueArray,
								6));
					}
				}
			},

			getFormDataDic : function() {
				var ac = util.Accredit;
				var form = this.form.getForm();
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext.empiData);
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id];
						if (v == undefined) {
							v = it.defaultValue;
						}
						if (v != null && typeof v == "object") {
							v = v.key;
						}
						var f = form.findField(it.id);
						if (f) {
							v = f.getValue();
							// edit by chenxr
							if (f.getXType() == "mycombox") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "") {
									v = {
										key : "",
										text : ""
									};
								} else {
									v = {
										key : f.getValue(),
										text : rawVal
									};
								}
							}
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "") {
									v = {
										key : "",
										text : ""
									};
								} else {
									v = {
										key : f.getValue(),
										text : rawVal
									};
								}
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}
						if (v == null || v === "") {
							if (!it.pkey
									&& (it['not-null'] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}
				return values;
			},

			initFormData : function(data) {
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							if (it.id == "complication") {
								f.clearValue();
								f.setValue(v);
							} else {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof v == 'string'
										&& v.length > 10) {
									v = v.substring(0, 10)
								} else if (it.type == 'datetime') {
									v = v.substring(0, 16)
								}

								f.setValue(v)
							}
						}

						if (this.initDataId) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.resetButtons(); // ** 用于页面按钮权限控制
				this.focusFieldAfter(-1, 800)
			},
			doLook : function(){
				Ext.Msg.alert("友情提醒：","先分组评估再做随访，如先做了一条随访请把分组日期推算到下次随访时间，不然计划不能正常生成！");
			}

		});