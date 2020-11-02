$package("chis.application.conf.script.mdc")
$import("chis.script.modules.form.FieldSetFormView")
chis.application.conf.script.mdc.HypertensionAssessmentConfigManageForm = function(
		cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	cfg.labelWidth = 150;
	cfg.colCount = 3
	chis.application.conf.script.mdc.HypertensionAssessmentConfigManageForm.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext
		.extend(
				chis.application.conf.script.mdc.HypertensionAssessmentConfigManageForm,
				chis.script.modules.form.FieldSetFormView, {

					doSave : function() {
						this.fireEvent("save", this);
					},
					planModeChange : function(value) {
						if (this.form) {
							var form = this.form.getForm();
							var assessType = form.findField("assessType");
							if (value == "2") {
								this.form.disable();
								assessType.setValue("1");
							} else {
								this.form.enable();
								assessType.setValue("2");
							}
						}
					},

					loadData : function() {
						this.form.el.mask("正在载入数据...", "x-mask-loading")
						util.rmi.jsonRequest({
									serviceId : this.loadServiceId,
									serviceAction : this.loadAction,
									method : "execute"
								}, function(code, msg, json) {
									this.form.el.unmask()
									if (code > 300) {
										this.processReturnMsg(code, msg);
										return
									}
									if (json.body) {
										this.hasLoad = true;
										this.initFormData(json.body);
									}
								}, this)
					},

					onReady : function() {
						chis.application.conf.script.mdc.HypertensionAssessmentConfigManageForm.superclass.onReady
								.call(this);
						var form = this.form.getForm();
						var assessType = form.findField("assessType");
						if (assessType) {
							assessType
									.on("change", this.assessTypeChange, this);
						}
					},
					assessTypeChange : function() {
						var form = this.form.getForm();
						var f = form.findField("assessType");
						var assessType = f.getValue();
						var recordWriteOff = form.findField("recordWriteOff");
						var newPatient = form.findField("newPatient");
						var notNormPatient = form.findField("notNormPatient");
						var oneGroup = form.findField("oneGroup");
						var oneGroupProportion = form
								.findField("oneGroupProportion");
						var twoGroup = form.findField("twoGroup");
						var twoGroupProportion = form
								.findField("twoGroupProportion");
						var threeGroup = form.findField("threeGroup");
						var threeGroupProportion = form
								.findField("threeGroupProportion");
						var assessDays = form.findField("assessDays");
						var assessHour1 = form.findField("assessHour1");
						var assessHour2 = form.findField("assessHour2");
						if (assessType == "1") {
							recordWriteOff.setValue();
							newPatient.setValue();
							notNormPatient.setValue();
							oneGroup.setValue();
							oneGroupProportion.setValue();
							twoGroup.setValue();
							twoGroupProportion.setValue();
							threeGroup.setValue();
							threeGroupProportion.setValue();
							assessDays.setValue();
							assessHour1.setValue();
							assessHour2.setValue();
							recordWriteOff.disable();
							newPatient.disable();
							notNormPatient.disable();
							oneGroup.disable();
							oneGroupProportion.disable();
							twoGroup.disable();
							twoGroupProportion.disable();
							threeGroup.disable();
							threeGroupProportion.disable();
							assessDays.disable();
							assessHour1.disable();
							assessHour2.disable();
						} else {
							if (!this.hasLoad) {
								recordWriteOff.setValue(true);
								newPatient.setValue(true);
								notNormPatient.setValue(true);
								oneGroup.setValue(true);
								oneGroupProportion.setValue(75);
								twoGroup.setValue(true);
								twoGroupProportion.setValue(75);
								threeGroup.setValue(true);
								threeGroupProportion.setValue(75);
								assessDays.setValue(5);
								assessHour1.setValue(16);
								assessHour2.setValue(6);
							}
							recordWriteOff.enable();
							newPatient.enable();
							notNormPatient.enable();
							oneGroup.enable();
							oneGroupProportion.enable();
							twoGroup.enable();
							twoGroupProportion.enable();
							threeGroup.enable();
							threeGroupProportion.enable();
							assessDays.enable();
							assessHour1.enable();
							assessHour2.enable();
						}
						this.hasLoad=false;
					},

					createField : function(it) {
						var ac = util.Accredit;
						var defaultWidth = this.fldDefaultWidth || 200
						var cfg = {
							name : it.id,
							xtype : it.xtype || "textfield",
							vtype : it.vtype,
							width : defaultWidth,
							value : it.defaultValue,
							enableKeyEvents : it.enableKeyEvents,
							validationEvent : it.validationEvent
						}
						if (it.xtype == "checkbox") {
							cfg.hideLabel = true;
							cfg.boxLabel = it.alias;
						} else if (it.xtype == "label") {
							cfg.text = it.alias;
							// cfg.style = "color:red"
							cfg.height = 80;
						} else {
							cfg.fieldLabel = it.alias;
						}
						cfg.listeners = {
							specialkey : this.onFieldSpecialkey,
							scope : this
						}
						if (it.inputType) {
							cfg.inputType = it.inputType
						}
						if (it['not-null']) {
							cfg.allowBlank = false
							cfg.invalidText = "必填字段"
							cfg.boxLabel = "<span style='color:red'>"
									+ cfg.boxLabel + "</span>"
						}
						if (it['showRed']) {
							cfg.boxLabel = "<span style='color:red'>"
									+ cfg.boxLabel + "</span>"
						}
						if (it.fixed || it.fixed) {
							cfg.disabled = true
						}
						if (it.pkey && it.generator == 'auto') {
							cfg.disabled = true
						}
						if (it.evalOnServer && ac.canRead(it.acValue)) {
							cfg.disabled = true
						}
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							cfg.disabled = true
						}
						if (this.op == "update" && !ac.canUpdate(it.acValue)) {
							cfg.disabled = true
						}
						if (it.dic) {
							it.dic.src = this.entryName + "." + it.id
							it.dic.defaultValue = it.defaultValue
							it.dic.width = defaultWidth
							var combox = this.createDicField(it.dic)
							Ext.apply(combox, cfg)
							combox.on("specialkey", this.onFieldSpecialkey,
									this)
							return combox;
						}
						if (it.length) {
							cfg.maxLength = it.length;
						}
						if (it.xtype) {
							return cfg;
						}
						switch (it.type) {
							case 'int' :
							case 'double' :
							case 'bigDecimal' :
								cfg.xtype = "numberfield"
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.minValue) {
									cfg.minValue = it.minValue;
								}
								if (it.maxValue) {
									cfg.maxValue = it.maxValue;
								}
								break;
							case 'date' :
								cfg.xtype = 'datefield'
								cfg.emptyText = "请选择日期"
								break;
							case 'text' :
								cfg.xtype = "htmleditor"
								cfg.enableSourceEdit = false
								cfg.enableLinks = false
								cfg.width = 300
								break;
						}
						return cfg;
					}

				});