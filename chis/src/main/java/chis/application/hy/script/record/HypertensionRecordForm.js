// 高血压档案创建面板
$package("chis.application.hy.script.record");

$import("chis.script.BizTableFormView",
		"chis.application.hy.script.HypertensionUtils",
		"chis.application.mpi.script.ExpertQuery", "chis.script.util.helper.Helper");

chis.application.hy.script.record.HypertensionRecordForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = false;
	cfg.showButtonOnTop = true;
	chis.application.hy.script.record.HypertensionRecordForm.superclass.constructor
			.apply(this, [cfg]);
	Ext.apply(this, chis.application.hy.script.HypertensionUtils);
	this.saveServiceId = "chis.hypertensionService";
	this.saveAction = "saveHypertensionRecord";
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionRecord";
	this.on("save", this.onSave, this);
};

Ext.extend(chis.application.hy.script.record.HypertensionRecordForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				
				this.initDataId = this.exContext.ids["MDC_HypertensionRecord.phrId"];
				chis.application.hy.script.record.HypertensionRecordForm.superclass.doNew
						.call(this);
			},
			
			doCheck:function(){
				this.fireEvent("addModule");
			},
			onSave : function(entryName, op, json, data) {
				this.initDataId = json.body.phrId;
				this.refreshEhrTopIcon();
				this.fireEvent("saveHyperRecord", entryName, op, json, data);
			},
			initFormData : function(data) {
				this.exContext.control = data["MDC_HypertensionRecord_actions"];
				if (this.op == "create") {
					this.initDataId = null;
					this.form.getForm().findField("manaDoctorId").disable();
					this.form.getForm().findField("createUser").enable();
				}
				chis.application.hy.script.record.HypertensionRecordForm.superclass.initFormData
						.call(this, data);
				this.form.getForm().findField("createDate").enable();
				var phrIdField = this.form.getForm().findField("phrId");
				if (!phrIdField || !phrIdField.getValue()) {
					phrIdField.setValue(this.exContext.ids.phrId);
				}
				this.onConfirmDateChange();
				this.onCalculateBMI();
//				this.onSmokeSelect();
				this.onFamilyHistroySelect();
//				this.onNRXJGLSelect();
//				this.onDrinkChange();
				this.onConstrictionChange();
				this.onDiastolicChange();
//				this.onBloodPressureInput();
				if(data && data.status && data.status.key=="1"){
					Ext.Msg.alert("友情提醒：","档案已注销，请先恢复档案！");
				}
			},
			onCalculateBMI : function() {
				var form = this.form.getForm();
				var bmi = form.findField("bmi");
				if (bmi) {
					var w = this.form.getForm().findField("weight").getValue();
					var h = this.form.getForm().findField("height").getValue();
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2);
					bmi.setValue(b);
				}
			},
			onReady : function() {
				chis.application.hy.script.record.HypertensionRecordForm.superclass.onReady
						.call(this);
				this.nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var form = this.form.getForm();
				var height = form.findField("height");
				height.on("blur", this.onHeightChange, this);
				height.on("keyup", this.onHeightChange, this);

				var weight = form.findField("weight");
				weight.on("blur", this.onWeightChange, this);
				weight.on("keyup", this.onWeightChange, this);

				var confirmDate = form.findField("confirmDate");
				confirmDate.on("select", this.onConfirmDateChange, this);
				confirmDate.on("blur", this.onConfirmDateChange, this);
				confirmDate.on("keyup", this.onConfirmDateChange, this);
				confirmDate.maxValue = this.nowDate;

				var constriction = form.findField("constriction");
				constriction.on("blur", this.onConstrictionChange, this);
				constriction.on("keyup", this.onConstrictionChange, this);
//				constriction.on("change", this.onBloodPressureInput, this);

				var diastolic = form.findField("diastolic");
				diastolic.on("blur", this.onDiastolicChange, this);
				diastolic.on("keyup", this.onDiastolicChange, this);
//				diastolic.on("change", this.onBloodPressureInput, this);

//				form.findField("smoke").on("select", this.onSmokeSelect, this);
//				form.findField("smoke").on("blur", this.onSmokeSelect, this);
//				form.findField("smoke").on("keyup", this.onSmokeSelect, this);
				
				form.findField("familyHistroy").on("select", this.onFamilyHistroySelect, this);
//				form.findField("SFNRXJGL").on("select", this.onNRXJGLSelect, this);

				form.findField("manaDoctorId").on("select",
						this.onManaDoctorIdSelect, this);
				
				form.findField("createUser").on("select",
						this.onCreateDoctorIdSelect, this);

				form.findField("createDate").on("select",
						this.onCreateDateChange, this);
				form.findField("createDate").on("blur",
						this.onCreateDateChange, this);
				form.findField("createDate").on("keyup",
						this.onCreateDateChange, this);

//				form.findField("drink").on("select", this.onDrinkChange, this);
//				form.findField("drink").on("blur", this.onDrinkChange, this);
//				form.findField("drink").on("keyup", this.onDrinkChange, this);

			},

			onHeightChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var height = field.getValue();
				if (!height) {
					return;
				}
				if (height >= 300 || height <= 0) {
					field.markInvalid("身高数值应在0到300之间！");
					return;
				}
				var weight = this.form.getForm().findField("weight").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					this.form.getForm().findField("bmi")
							.setValue((weight / temp).toFixed(2));
				}
			},

			onWeightChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var weight = field.getValue();
				if (!weight) {
					return;
				}
				if (weight > 500 || weight <= 0) {
					field.markInvalid("体重数值应在0到500之间！");
					return;
				}
				var height = this.form.getForm().findField("height").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					this.form.getForm().findField("bmi")
							.setValue((weight / temp).toFixed(2));
				}
			},

			onConstrictionChange : function(field) {
				if(!field){
					field = this.form.getForm().findField("constriction");
				}
				var constriction = field.getValue();
				var diastolicFld = this.form.getForm().findField("diastolic");
				var diastolic = diastolicFld.getValue();
				if (constriction) {
					diastolicFld.maxValue = constriction - 1;
				} else {
					diastolicFld.maxValue = 500;
				}
				diastolicFld.minValue = 50;
				if (diastolic) {
					field.minValue = diastolic + 1;
				} else {
					field.minValue = 50;
				}
				field.maxValue = 500;
				field.validate();
				diastolicFld.validate();
			},

//			onBloodPressureInput : function(field, newValue, oldValue) {
//				var constriction = this.form.getForm()
//						.findField("constriction").getValue();
//				var diastolic = this.form.getForm().findField("diastolic")
//						.getValue();
//				if (constriction && diastolic
//						&& this.isBPNormal(constriction, diastolic)) {
//					this.form.getForm().findField("afterMedicine").enable();
//					this.form.getForm().findField("afterMedicine")
//							.setValue("服药后");
//				} else {
//					this.form.getForm().findField("afterMedicine").disable();
//					this.form.getForm().findField("afterMedicine").setValue();
//				}
//			},
			onFamilyHistroySelect:function(combo, record, index)
			{
				var frm = this.form.getForm();
				var value = frm.findField("familyHistroy").getValue();
				if (value.indexOf('99')!=-1) {
					frm.findField("familyHistroyOther").enable();
				} else {
					frm.findField("familyHistroyOther").disable();
					frm.findField("familyHistroyOther").setValue("");
				}
				
			},
//			onNRXJGLSelect:function(combo, record, index)
//			{
//				var frm = this.form.getForm();
////				var value = frm.findField("SFNRXJGL").getValue();
//				if (value.indexOf('1')!=-1) {
//					frm.findField("XJGLDXBH").enable();
//				} else {
//					frm.findField("XJGLDXBH").disable();
//					frm.findField("XJGLDXBH").setValue("");
//				}
//				
//			},
			onDiastolicChange : function(field) {
				if(!field){
					field = this.form.getForm().findField("diastolic");
				}
				var diastolic = field.getValue();
				var constrictionFld = this.form.getForm()
						.findField("constriction");
				var constriction = constrictionFld.getValue();
				if (constriction) {
					field.maxValue = constriction - 1;
				} else {
					field.maxValue = 500;
				}
				field.minValue = 50;
				if (diastolic) {
					constrictionFld.minValue = diastolic + 1;
				} else {
					constrictionFld.minValue = 50;
				}
				constrictionFld.maxValue = 500;
				field.validate();
				constrictionFld.validate();
			},

			onManaDoctorIdSelect : function(combo, node) {
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
						});
				this.setManaUnit(result.json.manageUnit);
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("manaUnitId");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit);
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			},
			
			onCreateDoctorIdSelect : function(combo, node) {
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
						});
				this.setCreateUnit(result.json.manageUnit);
			},

			setCreateUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("createUnit");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit);
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			},

			onCreateDateChange : function(combo, record, index) {
				if (!this.nowDate) {
					return;
				}
				if (!combo.validate()) {
					return;
				}
				var thisYearBeginDate = Date.parseDate(this.nowDate.format("Y")
								+ "-01-01", "Y-m-d");
//				var thisYearEndDate = Date.parseDate(this.nowDate.format("Y")
//								+ "-12-31", "Y-m-d");
				combo.maxValue = this.nowDate;
				//combo.minValue = thisYearBeginDate;
				combo.validate();
			},

//			onSmokeSelect : function(combo, record, index) {
//				var frm = this.form.getForm();
//				var value = frm.findField("smoke").getValue();
//				if (value == 1 || value == 2) {
//					frm.findField("smokeCount").enable();
//				} else {
//					frm.findField("smokeCount").disable();
//					frm.findField("smokeCount").setValue("");
//				}
//			},

//			onDrinkChange : function(combo, record, index) {
//				var frm = this.form.getForm();
//				var value = frm.findField("drink").getValue();
//				var dtf = frm.findField("drinkTypeCode");
//				var dc = frm.findField("drinkCount");
//				if (value == "1" || value == "") {
//					dtf.disable();
//					dtf.setValue();
//					dc.disable();
//					dc.setValue();
//				} else {
//					dtf.enable();
//					dc.enable();
//				}
//			},

			onConfirmDateChange : function() {
				var field = this.form.getForm().findField("confirmDate");
				if (!field.validate()) {
					return;
				}
				if (!field.getValue() || !this.nowDate) {
					return;
				}
				var years = this.calculateYears(field.getValue());
				this.form.getForm().findField("deaseAge").setValue(years);
				field.validate();
			},

			calculateYears : function(confirmDate) {
				var month = chis.script.util.helper.Helper.getAgeMonths(confirmDate,
						this.nowDate);
				if (month < 1) {
					var days = chis.script.util.helper.Helper.getPeriod(confirmDate,
							this.nowDate);
					return days + "天";
				}
				if (month >= 12) {
					var years = parseInt(month / 12);
					var remainMonth = month - years * 12;
					if (remainMonth == 0) {
						return years + "年";
					}
					return years + "年" + remainMonth + "月";
				}
				return month + "月";
			},
			
			setButton : function(m, flag) {
				if (this.empiId && this.phrId
						&& this.manaDoctorId != this.mainApp.uid
						&& this.mainApp.jobId != "system") {
					if (this.phrId) {
						Ext.Msg.alert("提示", "该病人责任医生非本人，不能新增接诊记录");
					}
					flag = false;
				}
				var btns;
				var btn;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar();
				} else {
					btns = this.form.buttons;
				}
				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			
			validate : function() {
				if (false == this.form.getForm().isValid()) {
					return false;
				}
				var constrictionFld = this.form.getForm()
						.findField("constriction");
				var diastolicFld = this.form.getForm().findField("diastolic");
				var constriction = constrictionFld.getValue();
				var diastolic = diastolicFld.getValue();
				if (constriction < diastolic) {
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					return false;
				}
				return true;
			}
		});