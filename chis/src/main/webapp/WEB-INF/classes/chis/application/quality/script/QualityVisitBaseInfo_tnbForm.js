	$package("chis.application.dbs.script.visit")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.quality.script.QualityVisitBaseInfo_tnbForm = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesVisit"
	cfg.autoLoadSchema = false;
	cfg.isCombined = true;
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.autoWidth = true;
	cfg.colCount = 4;
	cfg.labelWidth = 88;
	cfg.fldDefaultWidth = 110;
	cfg.autoFieldWidth = false;
	chis.application.quality.script.QualityVisitBaseInfo_tnbForm.superclass.constructor
			.apply(this, [cfg])
	var nowDate = this.mainApp.serverDate
	this.nowDate = nowDate
	// this.loadServiceId = "chis.diabetesVisitService"
	// this.loadAction = "loadVisitData"
	this.planMode = this.mainApp.exContext.diabetesMode

}
Ext.extend(chis.application.quality.script.QualityVisitBaseInfo_tnbForm,
		chis.script.BizTableFormView, {
		saveToServer : function(saveData) {
			if (!this.fireEvent("beforeSave", this.entryName, this.op,
					saveData)) {
				return;
			}
			if (this.initDataId == null) {
				this.op = "create";
			}
			saveData["empiId"] = this.exContext.ids.empiId
			saveData["phrId"] = this.exContext.ids.phrId
			// saveData["nextPlanId"] = this.nextPlan.planId
			// saveData["nextPlan"] = this.nextPlan
			saveData["visitId"] = this.exContext.args.r.get("visitId")
			saveData["planId"] = this.exContext.args.r.get("planId")
			saveData["planDate"] = this.exContext.args.r.get("planDate")
			saveData["endDate"] = this.exContext.args.r.get("endDate")
			saveData["lateInput"] = this.lateInput
			saveData["height"] = this.height
	
			if (this.planMode == 2
					&& this.form.getForm().findField("visitEffect")
							.getValue() != '9') {
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var planD = Date.parseDate(this.planDate, "Y-m-d");
				if (planD >= now) {
					if (saveData["nextDate"] <= planD) {
						Ext.MessageBox.alert("提示", "预约日期必须大于计划日期")
						return
					}
				} else {
					if (saveData["nextDate"] <= now) {
						Ext.MessageBox.alert("提示", "预约日期必须大于当前日期")
						return
					}
				}
			}
			if ((this.fbsField.getValue() == "" && this.pbsField.getValue() == "")
					&& this.visitEffect.getValue() == "1") {
				Ext.MessageBox.alert("提示", "空腹血糖餐后血糖必须输入一个")
				return
			}
			this.saving = true
			if ((this.medicineField.getValue() == "3"
					|| this.medicineField.getValue() == "4" || this.medicineField
					.getValue() == "")
					&& (this.medicineValue == '1' || this.medicineValue == '2')
					&& this.op == "update") {
				Ext.Msg.show({
							title : '消息提示',
							msg : '当前操作会引起服药数据删除,是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									saveData.medicineChange = true
									this.process(saveData)
									return
								} else {
									this.saving = false
								}
							},
							scope : this
						})
			} else {
				this.process(saveData)
			}
		},
		process : function(saveData) {
			var result = util.rmi.miniJsonRequestSync({
						serviceId : "chis.diabetesVisitService",
						serviceAction : "checkNeedChangeGroup",
						method : "execute",
						op : this.op,
						body : saveData
					});
			if (result.code > 300) {
				return
			}
			if (result.json.body) {
				var needChangeGroup = result.json.body.needChangeGroup;
				var diabetesGroupName = result.json.body.diabetesGroupName;
				if (needChangeGroup) {
					Ext.Msg.show({
						title : '消息提示',
						msg : '当前操作会将病人转为' + diabetesGroupName + '管理,是否继续?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								Ext.apply(saveData, result.json.body);
							}
							this.saveFormData(saveData);
						},
						scope : this
					})
				} else {
					Ext.apply(saveData, result.json.body);
					this.saveFormData(saveData);
				}
			} else {
				this.saveFormData(saveData);
			}
		},
		saveFormData : function(saveData) {
			this.form.el.mask("正在保存数据...", "x-mask-loading")
			util.rmi.jsonRequest({
						serviceId : "chis.diabetesVisitService",
						op : this.op,
						schema : this.entryName,
						serviceAction : "saveDiabetesVisit",
						method : "execute",
						body : saveData
					}, function(code, msg, json) {
						this.form.el.unmask()
						this.saving = false
						if (code > 300) {
							this.processReturnMsg(code, msg,
									this.saveFormData, [saveData]);
							return
						}
						Ext.apply(this.data, saveData);
						if (json.body) {alert(json.body.needReferral);
							if (json.body.needReferral == true) {
								Ext.MessageBox.alert("温馨提示",
										"鉴于你当前的情况，我们建议您转诊！")
							}
							this.initFormData(json.body)
						}
						this.fireEvent("save", this.entryName, this.op,
								json, this.data)
						this.op = "update"
					}, this)// jsonRequest
		},
		doNew : function() {
			chis.application.quality.script.QualityVisitBaseInfo_tnbForm.superclass.doNew
					.call(this)
		},
		resetButton : function(data) {
			if (!this.form.getTopToolbar()) {
				return;
			}
			var btns = this.form.getTopToolbar().items;
			if (!btns) {
				return;
			}
			var n = btns.getCount();
			for (var i = 0; i < n; i++) {
				var btn = btns.item(i)
				var obj = data["_actions"]
				if (obj) {
					var status
					if (this.initDataId) {
						status = obj["update"]
					} else {
						status = obj["create"]
					}
					if (status) {
						btn.enable()
					} else {
						btn.disable()
					}
				}
			}
		},
		initFormData : function(data) {
	
			this.onVisitDateSelect()
//			this.form.getForm().findField("visitDate")
//					.setValue(this.exContext.args.r.data.planDate)
			chis.application.quality.script.QualityVisitBaseInfo_tnbForm.superclass.initFormData
					.call(this, data)
			this.nextRemindDate = data.nextRemindDate;
			this.height = data.height;
			this.diabetesGroup = data.diabetesGroups
			this.onCalculateBMI()
			this.onVisitEffect()
			this.onMedicineSelect()
			this.onSymptoms()
			this.resetButton(data)
			if (data.medicine) {
				this.medicineValue = data.medicine.key
			}
	
		},
		onReady : function() {
			chis.application.quality.script.QualityVisitBaseInfo_tnbForm.superclass.onReady
					.call(this)
	
			var form = this.form.getForm()
			var visitEffect = form.findField("visitEffect")
			if (visitEffect) {
				visitEffect.on("select", this.onVisitEffect, this)
				visitEffect.on("blur", this.onVisitEffect, this)
			}
			this.visitEffect = visitEffect
	
			var medicineType = form.findField("medicineType")
			if (medicineType) {
				medicineType.on("select", this.onMedicineType, this)
				medicineType.on("blur", this.onMedicineType, this)
			}
	
			var symptoms = form.findField("symptoms")
			if (symptoms) {
				symptoms.on("select", this.onSymptoms, this)
			}
	
			var mindStatus = form.findField("mindStatus")
			if (mindStatus) {
				mindStatus.on("select", this.onMindStatus, this)
				mindStatus.on("blur", this.onMindStatus, this)
			}
	
			var weight = form.findField("weight")
			this.weight = weight
			if (weight) {
				weight.on("keyup", this.onWeightCheck, this)
			}
	
			var constriction = form.findField("constriction")
			this.constriction = constriction
			if (constriction) {
				constriction.on("keyup", this.onConstriction, this)
			}
	
			var diastolic = form.findField("diastolic")
			this.diastolic = diastolic
			if (diastolic) {
				diastolic.on("keyup", this.onDiastolic, this)
			}
	
			var visitDateField = form.findField("visitDate")
			this.visitDateField = visitDateField
	
			if (visitDateField) {
				visitDateField.on("select", this.onVisitDateSelect, this)
				// visitDateField.on("blur", this.onVisitDateSelect, this)
			}
	
			var nextDateField = form.findField("nextDate")
			this.nextDateField = nextDateField
	
			var testDateField = form.findField("testDate")
			this.testDateField = testDateField
			this.testDateField.setMaxValue(this.testDateField
					.parseDate(this.nowDate))
			this.testDateField.validate()
	
			var medicineField = form.findField("medicine")
			this.medicineField = medicineField
			if (medicineField) {
				medicineField.on("select", this.onMedicineSelect, this)
				medicineField.on("keyup", this.onMedicineSelect, this)
				medicineField.on("blur", this.onMedicineSelect, this)
			}
	
			var fbsField = form.findField("fbs")
			this.fbsField = fbsField
			if (fbsField) {
				fbsField.on("keyup", this.onFbs, this)
				fbsField.on("blur", this.onFbs, this)
			}
	
			var pbsField = form.findField("pbs")
			this.pbsField = pbsField
			if (pbsField) {
				pbsField.on("keyup", this.onPbs, this)
				pbsField.on("blur", this.onPbs, this)
			}
	
			var drinkTypeCodeField = form.findField("drinkTypeCode")
			if (drinkTypeCodeField) {
				drinkTypeCodeField.on("select", this.onDrinkTypeCode, this)
			}
	
			var drinkCountField = form.findField("drinkCount")
			if (drinkCountField) {
				drinkCountField.on("keyup", this.onDrinkCountChange, this)
			}
	
			var visitDoctor = form.findField("visitDoctor");
			if (visitDoctor) {
				visitDoctor.on("select", this.onVisitDoctorSelect, this);
			}
	
		},
		onDrinkTypeCode : function(combo, record, index) {
			var value = combo.getValue();
			var valueArray = value.split(",");
			if (valueArray.indexOf("10") != -1) {
				combo.clearValue();
				if (record.data.key == 10) {
					combo.setValue({
								key : 10,
								text : "不饮酒"
							});
					this.form.getForm().findField("drinkCount").setValue(0);
				} else {
					combo.setValue(record.data.key);
					this.form.getForm().findField("drinkCount").setValue();
				}
			}
			if (value == "") {
				combo.setValue({
							key : 10,
							text : "不饮酒"
						});
				this.form.getForm().findField("drinkCount").setValue(0);
			}
		},
		onDrinkCountChange : function(field) {
			field.validate();
			var combo = this.form.getForm().findField("drinkTypeCode");
			if (field.getValue() == 0) {
				combo.setValue({
							key : 10,
							text : "不饮酒"
						});
			} else {
				if (combo.getValue() == 10) {
					combo.setValue();
				}
				combo.allowBlank = false;
				combo.validate();
			}
		},
		onPbs : function() {
			if (this.visitEffect.getValue() != '1') {
				this.fbsField.allowBlank = true
				this.pbsField.allowBlank = true
				Ext.getCmp(this.fbsField.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update("空腹血糖:");
				Ext.getCmp(this.pbsField.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update("餐后血糖:");
				this.validate()
			} else {
				if (this.pbsField.getValue() == "") {
					this.fbsField.allowBlank = false
					Ext.getCmp(this.fbsField.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + "空腹血糖"
									+ ":</span>");
				} else {
					this.fbsField.allowBlank = true
					Ext.getCmp(this.fbsField.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update("空腹血糖:");
				}
				this.validate()
			}
		},
		onFbs : function() {
			if (this.visitEffect.getValue() != '1') {
				this.fbsField.allowBlank = true
				this.pbsField.allowBlank = true
				Ext.getCmp(this.fbsField.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update("空腹血糖:");
				Ext.getCmp(this.pbsField.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update("餐后血糖:");
				this.validate()
			} else {
				if (this.fbsField.getValue() == "") {
					this.pbsField.allowBlank = false
					Ext.getCmp(this.pbsField.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + "餐后血糖"
									+ ":</span>");
				} else {
					this.pbsField.allowBlank = true
					Ext.getCmp(this.pbsField.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update("餐后血糖:");
				}
				this.validate()
			}
		},
		onMedicineSelect : function() {
			if (this.medicineField.getValue() == "3"
					|| this.medicineField.getValue() == "4"
					|| this.medicineField.getValue() == "") {
				this.form.getForm().findField("adverseReactions").disable()
				this.form.getForm().findField("adverseReactions")
						.setValue("")
			} else {
				this.form.getForm().findField("adverseReactions").enable()
			}
		},
		onVisitDateSelect : function() {
			if (this.planMode == 1) {
				if (!this.visitDateField.getValue()
						|| !this.visitDateField.validate()) {
					return
				}
				var remindField = this.form.getForm().findField("nextDate")
				var visitDate = this.visitDateField.getValue()
				var nowDate = this.nowDate
				if( this.formLoadData){
					var endDate = this.formLoadData["endDate"];
					if (nowDate > endDate) {
						remindField.setValue(nowDate);
					} else {
						remindField.setValue(endDate);
					}	
				
				}
			
			}
		},
		onConstriction : function() {
			var constriction = this.constriction.getValue();
			if (constriction == "") {
				return
			}
			if (constriction > 500 || constriction < 10) {
				this.constriction.markInvalid("收缩压必须在10到500之间！");
				return;
			}
			var diastolic = this.diastolic.getValue();
			if (constriction <= diastolic) {
				this.constriction.markInvalid("收缩压应该大于舒张压！");
				return;
			}
		},
	
		onDiastolic : function() {
			var diastolic = this.diastolic.getValue();
			if (diastolic == "") {
				return
			}
			if (diastolic > 500 || diastolic < 10) {
				this.diastolic.markInvalid("舒张压必须在10到500之间！");
				return;
			}
			var constriction = this.constriction.getValue();
			if (constriction <= diastolic) {
				this.diastolic.markInvalid("舒张压应该小于收缩压！");
				return;
			}
		},
		onMindStatus : function() {
			var form = this.form.getForm()
			var mindStatus = form.findField("mindStatus")
			if (mindStatus) {
				if (mindStatus.getValue().indexOf("11") >= 0) {
					mindStatus.setValue("11")
				}
			}
		},
		onSymptoms : function(combo, record, index) {
			var combo = this.form.getForm().findField("symptoms")
			var v = combo.getValue();
			var otherSymptomsF = this.form.getForm()
					.findField("otherSymptoms");
			var vArray = v.split(",")
			if (!record) {
				if (vArray.indexOf("99") != -1) {
					otherSymptomsF.enable();
				} else {
					otherSymptomsF.disable();
				}
				return
			}
			if (vArray.indexOf("1") != -1) {
				combo.clearValue();
				otherSymptomsF.setValue();
				if (record.data.key == 99) {
					combo.setValue(99);
					otherSymptomsF.enable();
				} else {
					combo.setValue(record.data);
					otherSymptomsF.disable()
				}
			} else {
				if (vArray.indexOf("99") != -1) {
					otherSymptomsF.enable();
				} else {
					otherSymptomsF.setValue();
					otherSymptomsF.disable();
				}
			}
	
		},
		onWeightCheck : function(f, e) {
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
				var h = this.height
				if (w == "" || h == "") {
					return
				}
				var b = (w / (h * h / 10000)).toFixed(2)
				bmi.setValue(b)
				var loseWeight = {}
				var f = this.form.getForm().findField("loseWeight")
				if (bmi.getValue() > 24) {
					loseWeight.key = "2"
					loseWeight.text = "需要"
				} else {
					loseWeight.key = "1"
					loseWeight.text = "不需要"
				}
				f.setValue(loseWeight)
			}
		},
		onVisitEffect : function(flag) {
			this.setNoVisitReasonStatus(this.visitEffect.getValue())
			this.onFbs()
			this.onPbs()
		},
		forbidOperate : function(flag) {
			var value = this.visitEffect.getValue();
			if (value == 9) {
				this.fireEvent("forbidOperate");
			}
		},
		onMedicineType : function(combo, record, index) {
			var form = this.form.getForm()
			var medicineType = form.findField("medicineType")
			var otherType = form.findField("otherType")
			if (medicineType.getValue().indexOf("5") >= 0) {
				if (medicineType) {
					medicineType.setValue("5")
				}
	
				if (otherType) {
					otherType.enable()
				}
			} else {
				if (otherType) {
					otherType.disable()
					otherType.setValue("")
				}
			}
		},
		setNoVisitReasonStatus : function(v) {
			var items = this.schema.items;
			var form = this.form.getForm();
			var f = form.findField("noVisitReason");
			var nextDate = form.findField("nextDate");
	
			if (!this.notNullSchemaItems) {
				this.notNullSchemaItems = [];
				for (var i = 0; i < items.length; i++) {
					if (items[i]["not-null"] || items[i]["not-null"] == "1") {
						this.notNullSchemaItems.push(items[i].id);
					}
				}
			}
			if (v == 1) {// @@ 恢复原必填项。
				Ext.getCmp(f.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update(f.fieldLabel
								+ ":");
				f.disable();
				f.setValue();
				f.allowBlank = true;
	
				for (var i = 0; i < items.length; i++) {
					var field = form.findField(items[i].id);
					if (items[i].id == "noVisitReason") {
						items[i]["not-null"] = false;
					}
	
					if (items[i].id == "nextDate" && this.planMode != 2) {
						field.allowBlank = true;
						items[i]["not-null"] = false;
						Ext.getCmp(field.id).getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update(items[i].alias + ":");
						continue;
					}
	
					if (field
							&& this.notNullSchemaItems.indexOf(items[i].id) > -1) {
						field.allowBlank = false;
						items[i]["not-null"] = true;
						Ext.getCmp(field.id).getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'>"
										+ items[i].alias + ":</span>");
					}
				}
				this.validate();
				return;
			}
	
			if (v == 2) {// @@ 去除必填项，激活失访原因文本框
				Ext.getCmp(f.id).getEl().up('.x-form-item')
						.child('.x-form-item-label')
						.update("<span style='color:red'>" + f.fieldLabel
								+ ":</span>");
				f.allowBlank = false;
				f.enable();
				for (var i = 0; i < items.length; i++) {
					if (items.evalOnServer) {
						continue;
					}
	
					var field = form.findField(items[i].id);
	
					if (field) {
						if ((items[i].id == "nextDate" && this.planMode == 2)
								|| items[i].id == "noVisitReason") {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							Ext.getCmp(field.id).getEl().up('.x-form-item')
									.child('.x-form-item-label')
									.update("<span style='color:red'>"
											+ items[i].alias + ":</span>");
							continue;
						}
	
						field.allowBlank = true;
						items[i]["not-null"] = false;
						Ext.getCmp(field.id).getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update(items[i].alias + ":");
					}
				}
				this.validate();
				return
			}
	
			if (v == 9) {// @@ 去除必填项，激活失访原因文本框
				Ext.getCmp(f.id).getEl().up('.x-form-item')
						.child('.x-form-item-label')
						.update("<span style='color:red'>" + f.fieldLabel
								+ ":</span>");
				f.allowBlank = false;
				f.enable();
	
				for (var i = 0; i < items.length; i++) {
					if (items.evalOnServer) {
						continue;
					}
					var field = form.findField(items[i].id);
	
					if (field) {
						if (items[i].id == "noVisitReason") {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							Ext.getCmp(field.id).getEl().up('.x-form-item')
									.child('.x-form-item-label')
									.update("<span style='color:red'>"
											+ items[i].alias + ":</span>");
							continue;
						}
						field.allowBlank = true;
						items[i]["not-null"] = false;
						Ext.getCmp(field.id).getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update(items[i].alias + ":");
					}
				}
				this.validate();
			}
		},
	
		onVisitDoctorSelect : function(combo, node) {
			if (!node.attributes['key']) {
				return
			}
			var result = util.rmi.miniJsonRequestSync({
						serviceId : "chis.publicService",
						serviceAction : "getManageUnit",
						method : "execute",
						body : {
							manaUnitId : node.attributes["manageUnit"]
						}
					})
			var manageUnit = result.json.manageUnit;
			this.data.visitUnit = manageUnit.key;
		},
	
		doTurn : function() {
			Ext.Msg.show({
						title : '血糖值换算',
						msg : '换算公式mg/dl÷18=mmol/L,确认是否需要进行换算?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var fbsField = this.form.getForm()
										.findField("fbs")
								var pbsField = this.form.getForm()
										.findField("pbs")
								var unitField = this.form.getForm()
										.findField("unit")
								fbsField
										.setValue((fbsField.getValue() / 18)
												.toFixed(2))
								pbsField
										.setValue((pbsField.getValue() / 18)
												.toFixed(2))
								var unit = {}
								unit.key = "1"
								unit.text = "mmol/L"
								unitField.setValue(unit)
							}
						},
						scope : this
					});
		},
		loadData : function() {
			// don't remove this method
		},doSave: function(){
			var values = this.getFormData();
			if(!values){
				return;
			}
		//	 Ext.apply(this.data,values); 
			 this.fireEvent("formSave", values);//返回 保存数据
			//this.saveToServer(values)
		},
		partLoat:function(data){
			this.doNew();
			this.formLoadData=data;
			this.initFormData(data);
		}
   });

