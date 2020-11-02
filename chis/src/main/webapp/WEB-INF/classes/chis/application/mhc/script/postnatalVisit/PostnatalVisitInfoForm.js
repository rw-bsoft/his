/**
 * 孕妇产后访视表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatalVisit")
$import("chis.script.BizTableFormView","chis.script.util.helper.Helper")
chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm = function(cfg) {
	// cfg.colCount = 2;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 135;
	cfg.labelWidth = 130;
	chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCreate", this.beforeCreate, this);

}
Ext.extend(chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm,
		chis.script.BizTableFormView, {

			beforeCreate : function() {
				this.data["empiId"] = this.exContext.ids.empiId;
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "initPostnatalVisitInfo",
					method:"execute",
					body : {
						"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
					}
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					if (json.body) {
						var data = json.body;
						this.initFormData(data);
						this.initDataId = null;
					}
				}, this)
			},

			loadData : function() {
				this.doNew();
				var datas = this.exContext.args.formDatas
				if (!datas) {
					return;
				}
				this.initFormData(datas);
				
				var nextVisitDate = this.form.getForm().findField("nextVisitDate");
				var createDate =datas["createDate"] ;
				var d = Date.parseDate(createDate.substr(0,10), "Y-m-d")
				nextVisitDate.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(d));
				nextVisitDate.validate();
			},

			initFormData : function(data) {
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm.superclass.initFormData
						.call(this, data)
				var form = this.form.getForm();
				var birthDay = form.findField("birthDay");
				this.changeBirthDay(birthDay);
				var constriction = form.findField("constriction");
				this.onConstrictionChange(constriction);
				var diastolic = form.findField("diastolic");
				this.onDiastolicChange(diastolic);
				this.onBreastSelect();
				this.onLochiaSelect();
				this.onUterusSelect();
				this.onWoundSelect();
				this.onClassificationSelect();
				this.onSuggestionSelect();
				this.onReferralSelect();
			},

			onReady : function() {
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var referral = form.findField("referral")
				this.referral = referral
				referral.on("select", this.onReferralSelect, this)
				referral.on("blur", this.onReferralSelect, this)

				var birthDay = form.findField("birthDay")
				this.birthDay = birthDay
				birthDay.on("keyup", this.changeBirthDay, this)
				birthDay.on("select", this.changeBirthDay, this)
				birthDay.on("blur", this.changeBirthDay, this)

				var visitDate = form.findField("visitDate")
				this.visitDate = visitDate
				visitDate.on("keyup", this.onBirthDayChange, this)
				visitDate.on("select", this.onBirthDayChange, this)
				visitDate.on("blur", this.onBirthDayChange, this)

				this.nowDate = this.mainApp.serverDate

				// chb 给舒张压、收缩压添加控制方法
				form.findField("constriction").on("blur",
						this.onConstrictionChange, this);
				form.findField("constriction").on("keyup",
						this.onConstrictionChange, this);
				form.findField("diastolic").on("blur", this.onDiastolicChange,
						this);
				form.findField("diastolic").on("keyup", this.onDiastolicChange,
						this);

				var breast = form.findField("breast")
				this.breast = breast
				breast.on("select", this.onBreastSelect, this)

				var lochia = form.findField("lochia")
				this.lochia = lochia
				lochia.on("select", this.onLochiaSelect, this)

				var uterus = form.findField("uterus")
				this.uterus = uterus
				uterus.on("select", this.onUterusSelect, this)

				var wound = form.findField("wound")
				this.wound = wound
				wound.on("select", this.onWoundSelect, this)

				var classification = form.findField("classification");
				this.classification = classification;
				classification.on("select", this.onClassificationSelect, this);

				var suggestion = form.findField("suggestion");
				this.suggestion = suggestion;
				suggestion.on("select", this.onSuggestionSelect, this);

			},

			changeBirthDay : function(field) {
				if (!field.validate()) {
					return;
				}
				var visitDate = this.form.getForm().findField("visitDate");
				var constriction = field.getValue();
				if (!constriction) {
					visitDate.setMinValue(null);
					return;
				}
				var visitValue = visitDate.getValue();
				if (visitValue < constriction) {
					visitDate.setValue(null);
				}
				visitDate.setMinValue(constriction);
				this.onBirthDayChange();
			},

			onBirthDayChange : function() {
				var postnatalDays = this.form.getForm()
						.findField("postnatalDays")
				if (!this.birthDay.validate()) {
					return
				}
				if (!this.visitDate.validate()) {
					return
				}
				var date = this.birthDay.getValue().format('Y-m-d')
				var visitDate = this.visitDate.getValue().format('Y-m-d')
				if (date > this.nowDate) {
					postnatalDays.setValue("")
					this.birthDay.markInvalid("时间不能大于当前时间")
					this.birthDay.focus();
					return
				}
				postnatalDays.setValue(this.daysBetween(date, visitDate))
			},

			onBreastSelect : function() {
				var form = this.form.getForm();
				if (this.breast.getValue() == "2") {
					form.findField("breastText").enable()
				} else {
					form.findField("breastText").disable()
					form.findField("breastText").setValue("")
				}
			},

			onLochiaSelect : function() {
				var form = this.form.getForm();
				if (this.lochia.getValue() == "2") {
					form.findField("lochiaText").enable()
				} else {
					form.findField("lochiaText").disable()
					form.findField("lochiaText").setValue("")
				}
			},

			onUterusSelect : function() {
				var form = this.form.getForm();
				if (this.uterus.getValue() == "2") {
					form.findField("uterusText").enable()
				} else {
					form.findField("uterusText").disable()
					form.findField("uterusText").setValue("")
				}
			},

			onWoundSelect : function() {
				var form = this.form.getForm();
				if (this.wound.getValue() == "2") {
					form.findField("woundText").enable()
				} else {
					form.findField("woundText").disable()
					form.findField("woundText").setValue("")
				}
			},

			onClassificationSelect : function() {
				var value = this.classification.getValue();
				var disable = true;
				if (value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "classificationText");
			},

			onSuggestionSelect : function() {
				var value = this.suggestion.getValue();
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("99") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherSuggestion");
			},

			daysBetween : function(DateOne, DateTwo) {
				var OneMonth = DateOne.substring(5, DateOne.lastIndexOf('-'));
				var OneDay = DateOne.substring(DateOne.length, DateOne
								.lastIndexOf('-')
								+ 1);
				var OneYear = DateOne.substring(0, DateOne.indexOf('-'));

				var TwoMonth = DateTwo.substring(5, DateTwo.lastIndexOf('-'));
				var TwoDay = DateTwo.substring(DateTwo.length, DateTwo
								.lastIndexOf('-')
								+ 1);
				var TwoYear = DateTwo.substring(0, DateTwo.indexOf('-'));

				var cha = ((Date.parse(OneMonth + '/' + OneDay + '/' + OneYear) - Date
						.parse(TwoMonth + '/' + TwoDay + '/' + TwoYear)) / 86400000);
				return Math.abs(cha);
			},

			onReferralSelect : function() {
				var form = this.form.getForm()
				if (this.referral.getValue() == "y") {
					form.findField("reason").enable()
					form.findField("doccol").enable()
					form.findField("referralNum").enable();
				} else {
					form.findField("reason").disable()
					form.findField("reason").setValue("")
					form.findField("doccol").disable()
					form.findField("doccol").setValue("")
					form.findField("referralNum").disable();
					form.findField("referralNum").setValue("");
				}
			},

			doCreate : function() {
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm.superclass.doCreate
						.call(this);
				this.fireEvent("create");
				
				var nextVisitDate = this.form.getForm().findField("nextVisitDate");
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				nextVisitDate.setMinValue(now);
				nextVisitDate.validate();
			},

			onConstrictionChange : function(field) {
				var constriction = field.getValue();
				var diastolicFld = this.form.getForm().findField("diastolic");
				var diastolic = diastolicFld.getValue();
				if (constriction) {
					diastolicFld.maxValue = constriction - 1;
				} else {
					diastolicFld.maxValue = 500;
				}
				diastolicFld.minValue = 10;
				if (diastolic) {
					field.minValue = diastolic + 1;
				} else {
					field.minValue = 10;
				}
				field.maxValue = 500;
				field.validate();
				diastolicFld.validate();
				if (diastolic && diastolic && constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
				}
			},

			onDiastolicChange : function(field) {
				var diastolic = field.getValue();
				var constrictionFld = this.form.getForm()
						.findField("constriction");
				var constriction = constrictionFld.getValue();
				if (constriction) {
					field.maxValue = constriction - 1;
				} else {
					field.maxValue = 500;
				}
				field.minValue = 10;
				if (diastolic) {
					constrictionFld.minValue = diastolic + 1;
				} else {
					constrictionFld.minValue = 10;
				}
				constrictionFld.maxValue = 500;
				field.validate();
				constrictionFld.validate();
				if (diastolic && diastolic && constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
				}
			}
		});