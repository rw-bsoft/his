$package("chis.application.psy.script.record");

$import("util.Accredit", "chis.script.BizTableFormView");

chis.application.psy.script.record.PsychosisFirstVisitForm = function(cfg) {
	cfg.colCount = "4";
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 140;
	cfg.labelWidth = 100;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	chis.application.psy.script.record.PsychosisFirstVisitForm.superclass.constructor.apply(
			this, [cfg]);
	this.entryName = "chis.application.psy.schemas.PSY_PsychosisFirstVisit";
	this.on("loadDataByLocal", this.onLoadDataByLocal, this);
//	this.on("loadData",this.onLoadData,this);
};

Ext.extend(chis.application.psy.script.record.PsychosisFirstVisitForm,
		chis.script.BizTableFormView, {
			
			onLoadDataByLocal : function(entryName, data, op) {
				if (data && data.medicine && data.adverseReactions
						&& data.adverseReactionsText) {
					this.medicine_key = data.medicine.key;
					this.medicine_text = data.medicine.text;
					this.adverseReactions_key = data.adverseReactions.key;
					this.adverseReactions_text = data.adverseReactions.text;
					this.adverseReactionsText = data.adverseReactionsText;
					this.visitId = data.visitId;
					this.nextDate = data.nextDate;
				}
				
				this.setWidgetStatus();
			},

			doNew : function() {
				chis.application.psy.script.record.PsychosisFirstVisitForm.superclass.doNew
						.call(this);
				this.setWidgetStatus();
				var visitDate = this.form.getForm().findField("visitDate");
				visitDate.disable();
				this.form.getForm().findField("lastHospitalizationTime")
						.disable();
				var dangerousGrade = this.form.getForm()
						.findField("dangerousGrade");
				var dangerousGradeValue = {
					key : 0,
					text : '0级'
				};
				dangerousGrade.setValue(dangerousGradeValue);
			},

			setWidgetStatus : function() {
				this.onSymptom();
				this.onHealing();
				this.onAdverseReactionsSelect();
				this.onReferral();
				this.onMedicineSelect();
				this.onIsLabCheckupSelect();
				
				this.setNextDateValidate();
				this.onFamilySocialImpactFldChange();
			},
			
			setNextDateValidate : function(){
				var nextDate = this.form.getForm().findField("nextDate");
				var ndv = nextDate.getValue();
				if(!ndv){
					var nowServerDate = this.mainApp.serverDate;
					var nowDate = Date.parseDate(nowServerDate, 'Y-m-d');
					var nextMinDate = new Date(nowDate.getFullYear(), nowDate
									.getMonth(), nowDate.getDate() + 1);
					nextDate.setMinValue(nextMinDate);
					nextDate.validate();
				}else{
					nextDate.setMinValue();
					nextDate.validate();
				}
			},
			setFieldDisable : function(disable, fieldId, hasValue) {
				var frm = this.form.getForm();
				var fld = frm.findField(fieldId);
				if (fld) {
					if (disable) {
						if (!hasValue) {
							fld.setValue();
						}
						fld.disable();
					} else {
						fld.enable();
					}
				}
			},
			onReady : function() {
				var frm = this.form.getForm();
				
				var symptomField = frm.findField("symptom");
				symptomField.on("select", this.onSymptom, this);
				this.symptomField = symptomField;
				this.symptomField.editable = false;

				var healingField = frm.findField("healing");
				healingField.on("select", this.onHealing, this);
				this.healingField = healingField;
				this.healingField.editable = false;

				var medicineField = frm.findField("medicine");
				this.medicineField = medicineField;
				if (medicineField) {
					medicineField.on("select", this.onMedicineSelect, this);
					medicineField.on("keyup", this.onMedicineSelect, this);
					medicineField.on("blur", this.onMedicineSelect, this);
				}

				var adverseReactionsField = frm.findField("adverseReactions");
				this.adverseReactionsField = adverseReactionsField;
				if (adverseReactionsField) {
					adverseReactionsField.on("select",
							this.onAdverseReactionsSelect, this);
					adverseReactionsField.on("keyup",
							this.onAdverseReactionsSelect, this);
					adverseReactionsField.on("blur",
							this.onAdverseReactionsSelect, this);
				}

				var referral = frm.findField("referral");
				this.referral = referral;
				referral.on("select", this.onReferral, this);
				referral.on("blur", this.onReferral, this);
				referral.on("keyup", this.onReferral, this);

				// var visitTypeField =
				// frm.findField("visitType");
				// this.visitTypeField = visitTypeField;
				// if (visitTypeField) {
				// visitTypeField.on("select", this.onVisitTypeSelect, this);
				// visitTypeField.on("keyup", this.onVisitTypeSelect, this);
				// visitTypeField.on("blur", this.onVisitTypeSelect, this);
				// }

				var visitDate = frm.findField("visitDate");
				this.visitDate = visitDate;
				this.visitDate.disable();
				
				var hospitalization = frm.findField("hospitalization");
				if (hospitalization) {
					hospitalization.on("select", this.onHospitalizationSelect,
							this);
					hospitalization.on("keyup", this.onHospitalizationSelect,
							this);
					hospitalization.on("blur", this.onHospitalizationSelect,
							this);
				}

				var riskFactor = frm.findField("riskFactor");
				if (riskFactor) {
					riskFactor.on("select", this.onRiskFactorSelect, this);
					riskFactor.on("keyup", this.onRiskFactorSelect, this);
					riskFactor.on("blur", this.onRiskFactorSelect, this);
				}
				
				var insight = frm.findField("insight");
				if(insight){
					insight.on("select", this.onInsight, this);
					insight.on("keyup", this.onInsight, this);
					insight.on("blur", this.onInsight, this);
				}
				
				var social = frm.findField("social");
				if(social){
					social.on("select", this.onSocial, this);
					social.on("keyup", this.onSocial, this);
					social.on("blur", this.onSocial, this);
				}
				
				var isLabCheckup = frm.findField("isLabCheckup");
				if(isLabCheckup){
					isLabCheckup.on("select", this.onIsLabCheckupSelect, this);
					isLabCheckup.on("keyup", this.onIsLabCheckupSelect, this);
					isLabCheckup.on("blur", this.onIsLabCheckupSelect, this);
				}
				
				var fsiFld = frm.findField("familySocialImpact");
				if(fsiFld){
					var fsiItems = fsiFld.items;
					for (var i = 0, len = fsiItems.length; i < len; i++) {
						var box = fsiItems[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onFamilySocialImpactFldItemCheck(checkedBox, checked);
							},
							scope : this
						}
					}
					fsiFld.on("change",this.onFamilySocialImpactFldChange, this);
				}
				
				chis.application.psy.script.record.PsychosisFirstVisitForm.superclass.onReady
						.call(this);
			},

			onSymptom : function() {
				var v = this.symptomField.getValue();
				var symptomTextF = this.form.getForm().findField("symptomText");
				var vArray = v.split(",");
				if (vArray.indexOf("99") != -1) {
					symptomTextF.enable();
				} else {
					symptomTextF.setValue();
					symptomTextF.disable();
				}
			},

			onHealing : function() {
				var v = this.healingField.getValue();
				var healingTextF = this.form.getForm().findField("healingText");
				var vArray = v.split(",");
				if (vArray.indexOf("5") != -1) {
					healingTextF.enable();
				} else {
					healingTextF.setValue();
					healingTextF.disable();
				}
			},

			onMedicineSelect : function() {
				var frm = this.form.getForm();
				var adverseReactions = frm.findField("adverseReactions");
				if (this.medicineField.getValue() == "3"
						|| this.medicineField.getValue() == "") {
					adverseReactions.disable();
					adverseReactions.setValue("");
				} else {
					adverseReactions.enable();
				}
				this.onAdverseReactionsSelect();
				this.fireEvent("medicineSelect", this.medicineField.getValue(),this.visitId);
			},
			regainMedicineValue : function() {
				var medicine = {};
				medicine.key = this.medicine_key || "";
				medicine.text = this.medicine_text || "";
				this.medicineField.setValue(medicine);
				var adverseReactions = {};
				adverseReactions.key = this.adverseReactions_key || "";
				adverseReactions.text = this.adverseReactions_text || "";
				this.adverseReactions.setValue(adverseReactions);
				var adverseReactionsText = this.form.getForm()
						.findField("adverseReactionsText");
				adverseReactionsText.setValue(this.adverseReactionsText || "");
				this.fireEvent("medicineSelect", this.medicine_key);
			},

			onAdverseReactionsSelect : function() {
				var adverseReactionsText = this.form.getForm()
						.findField("adverseReactionsText");
				if (this.adverseReactionsField.getValue() == "y") {
					adverseReactionsText.enable();
				} else {
					adverseReactionsText.disable();
					adverseReactionsText.setValue("");
				}
				this.setVisitType();
			},

			onReferral : function() {
				var reason = this.form.getForm().findField("reason");
				var doccol = this.form.getForm().findField("doccol");
				if (this.referral.getValue() == "y") {
					reason.enable();
					doccol.enable();
				} else {
					reason.disable();
					reason.setValue("");
					doccol.disable();
					doccol.setValue("");
				}
			},

			// onVisitTypeSelect : function() {
			// var lostReason = this.form.getForm().findField("lostReason");
			// if (this.visitTypeField.getValue() == "0") {
			// lostReason.enable();
			// lostReason.addClass("x-form-invalid");
			// } else {
			// lostReason.disable();
			// lostReason.removeClass("x-form-invalid");
			// }
			// },

			onHospitalizationSelect : function() {
				var hospitalization = this.form.getForm()
						.findField("hospitalization");
				var lastHospitalizationTime = this.form.getForm()
						.findField("lastHospitalizationTime");
				if (hospitalization.getValue() == ""
						|| hospitalization.getValue() == '0') {
					lastHospitalizationTime.disable();
				} else {
					lastHospitalizationTime.enable();
				}
			},

			onRiskFactorSelect : function() {
				var riskFactor = this.form.getForm().findField("riskFactor");
				var dangerousGrade = this.form.getForm()
						.findField("dangerousGrade");
				if (dangerousGrade && riskFactor) {
					var riskFactorValue = riskFactor.getValue() || "0";
					var dangerousGradeValue = {
						key : riskFactorValue,
						text : riskFactorValue + "级"
					};
					dangerousGrade.setValue(dangerousGradeValue);
				}
				this.setVisitType();
			},
			
			onInsight : function(){
				this.setVisitType();
			},
			
			onSocial : function(){
				this.setVisitType();
			},
			
			onIsLabCheckupSelect : function(){
				var frm = this.form.getForm();
				var isLabCheckup = frm.findField("isLabCheckup");
				var labCheckup = frm.findField("labCheckup");
				if(isLabCheckup.getValue() == '2'){
					labCheckup.enable();
				}else{
					labCheckup.setValue();
					labCheckup.disable();
				}
			},

			getFirstVisitData : function() {
				this.setVisitType();
				var values = chis.application.psy.script.record.PsychosisFirstVisitForm.superclass.getFormData
						.call(this);
				return values;
			},

			// 随访分类
			setVisitType : function() {
				var form = this.form.getForm();
				var dangerousGrade = form.findField("dangerousGrade")
						.getValue();
				var insight = form.findField("insight").getValue();
				var adverseReactions = form.findField("adverseReactions")
						.getValue();
				var social = form.findField("social").getValue();
				var visitType = {};
				// 转归为 2 暂时失访 --> 未访到 9 终止管理时，注销档案不正生成随访计划
				// 危险性为3～5级 或 自知力缺乏 或 有药物不良反应-->不稳定
				if (dangerousGrade > '2' || insight == '3'
						|| adverseReactions == 'y') {
					visitType = {
						key : '1',
						text : '不稳定'
					};
					form.findField("visitType").setValue(visitType);
					return;
				}
				// 若危险性为1～2级 或 自知力不全 或 社交能力较差 -->基本稳定
				if (dangerousGrade == '1' || dangerousGrade == '2'
						|| insight == '2' || social == '3') {
					visitType = {
						key : '2',
						text : '基本稳定'
					};
					form.findField("visitType").setValue(visitType);
					return;
				}
				// 危险性为0级，且 自知力完全，社会能力处于一般 或 良好，无药物不良反应 -->稳定
				if (dangerousGrade == '0' && insight == '1'
						&& (social == '1' || social == '2')
						&& adverseReactions == 'n') {
					visitType = {
						key : '3',
						text : '稳定'
					};
					form.findField("visitType").setValue(visitType);
					return;
				}
				// 没有任何状况，默认为 -稳定
				visitType = {
					key : '3',
					text : '稳定'
				};
				form.findField("visitType").setValue(visitType);
				return;
			},
			
			onFamilySocialImpactFldItemCheck : function(checkedBox, checked){
				var frm = this.form.getForm();
				var boxGroup = frm.findField("familySocialImpact");
				if (!boxGroup) {
					return;
				}
				var fsiVal = boxGroup.getValue();
				if (fsiVal == "") {
					boxGroup.setValue('0');
				}
				if (fsiVal.indexOf("0") != -1) {
					if (checkedBox.inputValue == "0" && checked) {
						boxGroup.setValue("0")
					} else {
						var valueArray = fsiVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "0") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onFamilySocialImpactFldChange : function(boxGroup, checkedBoxs){
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("familySocialImpact");
				}
				var fsiVal = boxGroup.getValue();
				if(fsiVal.indexOf('1') != -1){
					this.setFieldDisable(false, "lightAffray");
				}else{
					this.setFieldDisable(true, "lightAffray");
				}
				if(fsiVal.indexOf('2') != -1){
					this.setFieldDisable(false, "causeTrouble");
				}else{
					this.setFieldDisable(true, "causeTrouble");
				}
				if(fsiVal.indexOf('3') != -1){
					this.setFieldDisable(false, "causeAccident");
				}else{
					this.setFieldDisable(true, "causeAccident");
				}
				if(fsiVal.indexOf('4') != -1){
					this.setFieldDisable(false, "selfHurt");
				}else{
					this.setFieldDisable(true, "selfHurt");
				}
				if(fsiVal.indexOf('5') != -1){
					this.setFieldDisable(false, "suicide");
				}else{
					this.setFieldDisable(true, "suicide");
				}
			},
			setFSIControlFldsDisable : function(){
				this.setFieldDisable(true, "lightAffray");
				this.setFieldDisable(true, "causeTrouble");
				this.setFieldDisable(true, "causeAccident");
				this.setFieldDisable(true, "selfHurt");
				this.setFieldDisable(true, "suicide");
			},
			doSave : function(saveData) {
				this.fireEvent("firstVisitFormSave");
			}
			

		});