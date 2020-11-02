/**
 * 产后42天表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatal42day")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.postnatal42day.Postnatal42dayRecordForm = function(cfg) {
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 180
	cfg.labelWidth = 130
	chis.application.mhc.script.postnatal42day.Postnatal42dayRecordForm.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this)
	this.on("loadNoData", this.onLoadNoData, this);

}
Ext.extend(chis.application.mhc.script.postnatal42day.Postnatal42dayRecordForm,
		chis.script.BizTableFormView, {

			onLoadNoData : function() {
				this.data["empiId"] = this.exContext.ids.empiId;
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "initPostnatal42dayRecord",
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
						if (data) {
							this.initFormData(data);
						}
						this.initDataId = null;
					}
				}, this)
				this.setWidgetStatus();

			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "pregnantId",
					"fieldValue" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
				};
			},

			onLoadData : function() {
				this.setWidgetStatus();
				var form = this.form.getForm();
				var constriction = form.findField("constriction");
				if (constriction) {
					this.onConstrictionChange(constriction);
				}

				var diastolic = form.findField("diastolic");
				if (diastolic) {
					this.onDiastolicChange(diastolic);
				}
			},

			onReady : function() {
				chis.application.mhc.script.postnatal42day.Postnatal42dayRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var breast = form.findField("breast")
				this.breast = breast
				breast.on("select", this.onBreastSelect, this)

				var vulva = form.findField("vulva")
				this.vulva = vulva
				vulva.on("select", this.onVulvaSelect, this)

				var vagina = form.findField("vagina")
				this.vagina = vagina
				vagina.on("select", this.onVaginaSelect, this)

				var uterus = form.findField("uterus")
				this.uterus = uterus
				uterus.on("select", this.onUterusSelect, this)

				var appendages = form.findField("appendages")
				this.appendages = appendages
				appendages.on("select", this.onAppendagesSelect, this)

				var cervix = form.findField("cervix")
				this.cervix = cervix
				cervix.on("select", this.onCervixSelect, this)

				var lochia = form.findField("lochia")
				this.lochia = lochia
				lochia.on("select", this.onLochiaSelect, this)

				var wound = form.findField("wound")
				this.wound = wound
				wound.on("select", this.onWoundSelect, this)

				var classification = form.findField("classification")
				this.classification = classification
				classification.on("select", this.onClassificationSelect, this)

				var suggestion = form.findField("suggestion")
				this.suggestion = suggestion
				suggestion.on("select", this.onSuggestionSelect, this)

				var treat = form.findField("treat")
				this.treat = treat
				treat.on("select", this.onTreatSelect, this)

				// chb 给舒张压、收缩压添加控制方法
				form.findField("constriction").on("blur",
						this.onConstrictionChange, this);
				form.findField("constriction").on("keyup",
						this.onConstrictionChange, this);
				form.findField("diastolic").on("blur", this.onDiastolicChange,
						this);
				form.findField("diastolic").on("keyup", this.onDiastolicChange,
						this);
			},

			onTreatSelect : function() {
				var form = this.form.getForm();
				if (this.treat.getValue() == "2") {
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

			onSuggestionSelect : function() {
				var form = this.form.getForm();

				var value = this.suggestion.getValue();
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("99") != -1) {
					form.findField("suggestionText").enable()
				} else {
					form.findField("suggestionText").disable()
					form.findField("suggestionText").setValue("")
				}
			},

			onClassificationSelect : function() {
				var form = this.form.getForm();
				if (this.classification.getValue() == "2") {
					form.findField("classificationText").enable()
				} else {
					form.findField("classificationText").disable()
					form.findField("classificationText").setValue("")
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

			onLochiaSelect : function() {
				var form = this.form.getForm();
				if (this.lochia.getValue() == "2") {
					form.findField("lochiaText").enable()
				} else {
					form.findField("lochiaText").disable()
					form.findField("lochiaText").setValue("")
				}
			},

			onCervixSelect : function() {
				var form = this.form.getForm();
				if (this.cervix.getValue() == "2") {
					form.findField("cervixText").enable()
				} else {
					form.findField("cervixText").disable()
					form.findField("cervixText").setValue("")
				}
			},

			onAppendagesSelect : function() {
				var form = this.form.getForm();
				if (this.appendages.getValue() == "2") {
					form.findField("appendagesText").enable()
				} else {
					form.findField("appendagesText").disable()
					form.findField("appendagesText").setValue("")
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

			onVaginaSelect : function() {
				var form = this.form.getForm();
				if (this.vagina.getValue() == "2") {
					form.findField("vaginaText").enable()
				} else {
					form.findField("vaginaText").disable()
					form.findField("vaginaText").setValue("")
				}
			},

			onVulvaSelect : function() {
				var form = this.form.getForm();
				if (this.vulva.getValue() == "2") {
					form.findField("vulvaText").enable()
				} else {
					form.findField("vulvaText").disable()
					form.findField("vulvaText").setValue("")
				}
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

			setWidgetStatus : function() {
				this.onBreastSelect();
				this.onVulvaSelect();
				this.onVaginaSelect();
				this.onUterusSelect();
				this.onCervixSelect();
				this.onAppendagesSelect();
				this.onLochiaSelect();
				this.onWoundSelect();
				this.onClassificationSelect();
				this.onSuggestionSelect();
				this.onTreatSelect();
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