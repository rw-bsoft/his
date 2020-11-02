$package("chis.application.tr.script.highRisk")

$import("chis.script.BizFieldSetFormView","util.widgets.LookUpField","chis.script.util.widgets.MyMessageTip")

chis.application.tr.script.highRisk.TumourHighRiskVisitForm = function(cfg) {
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount = 8
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 30;
	chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.tumourHighRiskVisitService";
	this.saveAction = "saveTHRVisit";
	this.JKCFRecords = {};
	this.formId = "_ZLSF";
};
var healthGuidance_ctx = null;
function onImageClick() {
	healthGuidance_ctx.openCardLayoutWin()
}
Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskVisitForm,
		chis.script.BizFieldSetFormView, {
			initFormData : function(data) {
				chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.initFormData
						.call(this, data);
				var JKCFRecords = data.JKCFRecords;
				if (!JKCFRecords) {
					return;
				}
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200

				var items = schema.items
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					this.forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* colCount
					table.layoutConfig.forceWidth = this.forceViewWidth
				}
				var groups = {};
				var otherItems = [];
				var items = schema.items
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					if (it.id == "healthTeach") {
						var panel = new Ext.Panel({
							id : "healthTeach" + this.formId,
							border : false,
							colspan : 8,
							html : '<table style="vertical-align:middle;"><tr>'
									+ '<th width="18%">健康教育：</th><td width="77%">'
									+ '<div id="div_JKJY'
									+ this.formId
									+ '"/></td>'
									+ '<td><p  style="float:right;margin:0 8px 0 0;"><a title="引入健康处方">'
									+ '<img id="importHER" onclick="onImageClick();" src="resources/chis/resources/app/biz/images/jkchufang.png"/>'
									+ '</a></p></td></tr>' + '</table>',
							frame : false,
							autoScroll : true
						});
						this.addPanel = panel;
						var gname = it.group
						if (!gname) {
							gname = "_default"
						}
						if (!groups[gname])
							groups[gname] = [];
						groups[gname].push(panel)
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					var gname = it.group
					if (!gname) {
						gname = "_default"
					}
					if (!groups[gname])
						groups[gname] = [];
					groups[gname].push(f)
				}
				for (s in groups) {
					var border = true;
					var collapsible = true;
					var title = "<font size='2'>" + s + "</font>";
					if (s == "_default") {
						border = false;
						collapsible = false;
						title = null;
					}
					var group = groups[s];
					if (group.length > 0) {
						var fs = new Ext.form.FieldSet({
									border : border,
									collapsible : collapsible,
									width : this.fldDefaultWidth || 100,
									autoHeight : true,
									anchor : "100%",
									colspan : this.colCount,
									bodyStyle : 'overflow-x:hidden; overflow-y:auto',
									style : {
										marginBottom : '5px'
									},
									items : {
										layout : 'tableform',
										layoutConfig : {
											columns : colCount,
											tableAttrs : {
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										items : group
									}
								})
						if (title) {
							fs.title = title;
						}
						table.items.push(fs)
					}
				}
				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			initFormSet : function() {
				this.setFieldDisable(true, "fixGroup", true);
				this.setFieldDisable(true, "BMI", true);
				this.setFieldDisable(true, "lostVisitReason", true);
				this.setFieldDisable(true, "terminatedReason", true);
				this.setFieldDisable(true, "deadDate", true);
				this.setFieldDisable(true, "deadReason", true);
				this.setDisableFieldsOfBS();
				this.setDisableFieldsOfAS();
				this.setDisableFieldsOfRSS();
				this.setDisableFieldsOfSOC();
				this.setDisableFieldsOfSS();
				this.setDisableFieldsOfGS();
				this.hideOrShowSymptom();
			},
			doNew : function() {
				chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.doNew
						.call(this);
				this.initData(this.exContext.args);
			},
			initData : function(initValues) {
				if (initValues && initValues.planId) {
					if (!this.data) {
						this.data = {};
					}
					for (var item in initValues) {
						var field = this.form.getForm().findField(item);
						if (!field) {
							continue;
						}
						if (field) {
							field.setValue(initValues[item]);
						} else {
							this.data[item] = initValues[item];
						}
					}
					this.nextDateDisable = initValues['nextDateDisable'];
					this.planId = initValues["planId"];
					this.visitId = initValues["visitId"];
					this.empiId = initValues["empiId"];
					this.THRID = initValues["THRID"];

					this.planDate = initValues["planDate"];
					this.beginDate = Date.parseDate(initValues["beginDate"],
							"Y-m-d");
					this.endDate = Date.parseDate(initValues["endDate"],
							"Y-m-d");
					this.planStatus = initValues['planStatus'];
					// 设置随访时间选择范围
					var nowDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					var visitDateObj = this.form.getForm()
							.findField("visitDate");
					// visitDateObj.setValue(this.planDate);// 默认值为计划时期
					if (nowDate < this.beginDate || nowDate > this.endDate) {
						visitDateObj.setMinValue(this.beginDate);
						visitDateObj.setMaxValue(this.endDate);
					} else if (nowDate >= this.beginDate
							&& nowDate <= this.endDate) {
						visitDateObj.setMinValue(this.beginDate);
						visitDateObj.setMaxValue(nowDate);
					}

					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.tumourHighRiskVisitService",
								serviceAction : "visitInitialize",
								method : "execute",
								body : {
									THRID : this.THRID,
									empiId : this.empiId,
									lastEndDate : initValues["endDate"],
									lastBeginDate : initValues["beginDate"],
									planDate : this.planDate,
									planId : this.planId,
									occurDate : initValues["beginDate"],
									businessType : 15
								}
							});
					var body = result.json.body;
					if (body) {
						this.planMode = body.planMode;
						var frm = this.form.getForm();
						frm.findField("year").setValue(body.year);
						var thrData = body.thrRecord;
						if (thrData) {
							this.highRiskType = thrData.highRiskType.key;
							this.exContext.args.highRiskType=this.highRiskType;
							this.exContext.args.highRiskType_text = thrData.highRiskType.text;
							var highRiskTypeFld = frm.findField("highRiskType");
							if (highRiskTypeFld) {
								highRiskTypeFld.disable();
								highRiskTypeFld.setValue(thrData.highRiskType);
							}
							var fixGroupFld = frm.findField("fixGroup");
							if (fixGroupFld) {
								fixGroupFld.setValue(thrData.managerGroup)
							}
						}
					}
					var visitEffectField = this.form.getForm()
							.findField("visitEffect");
					this.onVisitEffectChange(visitEffectField);
				}
				// 控制下次预约时间
				this.setNextDate();
			},
			setNextDate : function() {
				// 控制预约时间
				var form = this.form.getForm();
				var nextDate = form.findField("nextDate");
				// 按随访结果时 下次预约时间范围在下一计划的开始结果时间之间
				if (this.planMode == "1" && nextDate) {
					var beginDate = Date.parseDate(this.exContext.args.nextBeginDate,
							"Y-m-d");
					if(beginDate){
						var nextMinDate = new Date(beginDate.getFullYear(), beginDate
										.getMonth(), beginDate.getDate() + 1);
						nextDate.setMinValue(nextMinDate);
					}
					var endDate = Date.parseDate(this.exContext.args.nextEndDate,
							"Y-m-d");
					nextDate.setMaxValue(endDate);
				}
				// 控制nextDate时间
				if (this.planMode == "2") {// 按下次预约随访时
					nextDate.allowBlank = false;
					nextDate["not-null"] = true;
					var pd = Date.parseDate(this.planDate, "Y-m-d");
					var minND = new Date(pd.getFullYear(), pd.getMonth(), pd
									.getDate()
									+ 1);
					nextDate.setMinValue(minND);
					if (Ext.getCmp(nextDate.id).getEl()) {
						Ext
								.getCmp(nextDate.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'>下次预约时间:</span>");
					}
				}
			},
			loadData : function() {
				this.JKCFRecord = null;
				this.initDataId = this.exContext.args.visitId;
				chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.loadData
						.call(this);
			},
			getLoadRequest : function() {
				var body = {};
				body.wayId = this.exContext.args.visitId;
				return body;
			},
			openCardLayoutWin : function() {
				var module = this.createCombinedModule(
						"refRecipeImportModule_ZLSF",
						this.refRecipeImportModule);
				module.exContext = this.exContext;
				module.on("importRecipe", this.onImportRecipe, this);
				module.fromId = "ZLSF";
				this.recipeImportModule = module;
				module.JKCFRecords = this.JKCFRecords;
				var win = module.getWin();
				win.setHeight(500);
				win.setWidth(1000);
				win.show();
				return;
			},
			createAllJKJYHTML : function(JKCFRecords) {
				this.JKCFRecords = {};
				var html = '<table cellspacing="0" id="the-table' + this.formId+ '">';
				for (var i = 0; i < JKCFRecords.length; i++) {
					var r = JKCFRecords[i];
					var healthTeach = r.healthTeach;
					var diagnoseId = parseInt(r.diagnoseId);
					this.JKCFRecords[diagnoseId] = r;
					html += '<tr style="height:40px;border-bottom:1px solid #ccd3dc;"><td>'
							+ '<pre id="JKCF'
							+ this.formId
							+ diagnoseId
							+ '" style="white-space:pre-wrap;word-wrap:break-word;">'
							+ healthTeach + '</pre></td></tr>'
				}
				html += '</table>';
				return html;
			},
			onImportRecipe : function(JKCFRecords) {
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			getSaveRequest : function(savaData) {
				savaData.planId = this.planId;
				savaData.visitId = this.visitId;
				savaData.beginDate = this.beginDate;
				savaData.nextPlanId = this.exContext.args.nextPlanId;
				var form = this.form.getForm();
				savaData.guideDate = form.findField("guideDate").getValue();
				savaData.guideUser = form.findField("guideUser").getValue();
				savaData.wayId = this.exContext.args.visitId;
				savaData.examineUnit = this.mainApp.dept;
				savaData.empiId = this.exContext.ids.empiId;
				savaData.phrId = this.exContext.ids.phrId;
				savaData.THRID = this.THRID
						|| this.exContext.ids["MDC_TumourHighRisk.THRID"];
				savaData.guideWay = "04";
				if (this.JKCFRecords) {
					savaData.JKCF = this.JKCFRecords;
				}
				if(this.checkResultIds){
					savaData.checkResultIds=this.checkResultIds;
				}
				if(savaData.checkResult){
					savaData.visitNorm = '1';
				}
				return savaData;
			},
			hideOrShowSymptom : function() {
				var sexCode = this.exContext.empiData.sexCode;
				var gsFldSet = this.form.items.item(1);// 获取妇科症状fieldSet
				if (gsFldSet) {
					if (sexCode == "2" && this.highRiskType == "6") {
						this.setFieldNotNull(true, "gynecologySymptom");
						gsFldSet.show();
					} else {
						this.setFieldNotNull(false, "gynecologySymptom");
						gsFldSet.hide();
					}
				}
				var asFldSet = this.form.items.item(2);// 获取肛肠症状fieldSet
				if (asFldSet) {
					if (this.highRiskType == "1") {
						this.setFieldNotNull(true, "anorectalSymptom");
						asFldSet.show();
					} else {
						this.setFieldNotNull(false, "anorectalSymptom");
						asFldSet.hide();
					}
				}
				var rssFldSet = this.form.items.item(3);// 获取呼吸系统症状fieldSet
				if (rssFldSet) {
					if (this.highRiskType == "4") {
						this.setFieldNotNull(true, "respiratorySystemSymptom");
						rssFldSet.show();
					} else {
						this.setFieldNotNull(false, "respiratorySystemSymptom");
						rssFldSet.hide();
					}
				}
				var socFldSet = this.form.items.item(4);// 获取胸部症状fieldSet
				if (socFldSet) {
					if (this.highRiskType == "5") {
						this.setFieldNotNull(true, "symptomsOfChest");
						socFldSet.show();
					} else {
						this.setFieldNotNull(false, "symptomsOfChest");
						socFldSet.hide();
					}
				}
				var ssFldSet = this.form.items.item(5);// 获取胃部症状fieldSet
				if (ssFldSet) {
					if (this.highRiskType == "2") {
						this.setFieldNotNull(true, "stomachSymptoms");
						ssFldSet.show();
					} else {
						this.setFieldNotNull(false, "stomachSymptoms");
						ssFldSet.hide();
					}
				}
			},
			setFieldNotNull : function(notNull, fieldId) {
				var items = this.schema.items;
				var frm = this.form.getForm();
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == fieldId) {
						var field = frm.findField(fieldId);
						if (!field) {
							continue;
						}
						if (notNull) {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update("<span style='color:red'>"
												+ items[i].alias + ":</span>");
							}
						} else {
							field.allowBlank = true;
							items[i]["not-null"] = false;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update(items[i].alias + ":");
							}
						}
						field.validate();
						break;
					}
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
				healthGuidance_ctx = this;
				var frm = this.form.getForm();
				var highRiskTypeFld = frm.findField("highRiskType");
				if (highRiskTypeFld) {
					highRiskTypeFld.on("change", this.onHighRiskTypeChange,
							this);
				}
				var visitEffectFld = frm.findField("visitEffect");
				if (visitEffectFld) {
					visitEffectFld.on("change", this.onVisitEffectChange, this);
				}
				var terminatedReasonFld = frm.findField("terminatedReason");
				if (terminatedReasonFld) {
					terminatedReasonFld.on("change",
							this.onTerminatedReasonChange, this);
				}

				// 妇科症状
				var gynecologySymptomFld = frm.findField("gynecologySymptom");
				if (gynecologySymptomFld) {
					var items = gynecologySymptomFld.items;
					for (var i = 0, len = items.length; i < len; i++) {
						var box = items[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onGynecologySymptomFldItemCheck(
										checkedBox, checked);
							},
							scope : this
						}
					}
					gynecologySymptomFld.on("change",
							this.onGynecologySymptomFldChange, this);
					// this.setDisableFieldsOfGS();
				}
				// 肛肠症状
				var anorectalSymptomFld = frm.findField("anorectalSymptom");
				if (anorectalSymptomFld) {
					var items = anorectalSymptomFld.items;
					for (var i = 0, len = items.length; i < len; i++) {
						var box = items[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onAnorectalSymptomFldItemCheck(checkedBox,
										checked);
							},
							scope : this
						}
					}
					anorectalSymptomFld.on("change",
							this.onAnorectalSymptomFldChange, this);
					// this.setDisableFieldsOfAS();
				}
				// 呼吸系统症状
				var respiratorySystemSymptomFld = frm
						.findField("respiratorySystemSymptom");
				if (respiratorySystemSymptomFld) {
					var items = respiratorySystemSymptomFld.items;
					for (var i = 0, len = items.length; i < len; i++) {
						var box = items[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onRespiratorySystemSymptomFldItemCheck(
										checkedBox, checked);
							},
							scope : this
						}
					}
					respiratorySystemSymptomFld.on("change",
							this.onRespiratorySystemSymptomFldChange, this);
					// this.setDisableFieldsOfRSS();
				}
				// 胸部症状
				var symptomsOfChestFld = frm.findField("symptomsOfChest");
				if (symptomsOfChestFld) {
					var items = symptomsOfChestFld.items;
					for (var i = 0, len = items.length; i < len; i++) {
						var box = items[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onSymptomsOfChestFldItemCheck(checkedBox,
										checked);
							},
							scope : this
						}
					}
					symptomsOfChestFld.on("change",
							this.onSymptomsOfChestFldChange, this);
					// this.setDisableFieldsOfSOC();
				}
				// 胃部症状
				var stomachSymptomsFld = frm.findField("stomachSymptoms");
				if (stomachSymptomsFld) {
					var items = stomachSymptomsFld.items;
					for (var i = 0, len = items.length; i < len; i++) {
						var box = items[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onStomachSymptomsFldItemCheck(checkedBox,
										checked);
							},
							scope : this
						}
					}
					stomachSymptomsFld.on("change",
							this.onStomachSymptomsFldChange, this);
					// this.setDisableFieldsOfSS();
				}
				// 全身症状
				var bodySymptomFld = frm.findField("bodySymptom");
				if (bodySymptomFld) {
					var bsItems = bodySymptomFld.items;
					for (var i = 0, len = bsItems.length; i < len; i++) {
						var box = bsItems[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onBodySymptomFldItemCheck(checkedBox,
										checked);
							},
							scope : this
						}
					}
					bodySymptomFld.on("change", this.onBodySymptomFldChange,
							this);
					// this.setDisableFieldsOfBS();
				}

//				var superviseMyselfFld = frm.findField("superviseMyself");
//				if (superviseMyselfFld) {
//					superviseMyselfFld.on("change",
//							this.onSuperviseMyselfFldChange, this);
//					this.setFieldDisable(true, "checkResult");
//				}

				var familyHistoryFld = frm.findField("familyHistory");
				if (familyHistoryFld) {
					familyHistoryFld.on("change",
							this.onFamilyHistoryFldChange, this);
					this.setFieldDisable(true, "part")
					this.setFieldDisable(true, "relationship")
				}

				// this.setFieldDisable(true,"BMI");
				var heightFld = frm.findField("height");
				if (heightFld) {
					heightFld.on("blur", this.onHeightFldChange, this);
					heightFld.on("keyup", this.onHeightFldChange, this);
				}
				var weightFld = frm.findField("weight");
				if (weightFld) {
					weightFld.on("blur", this.onWeightFldChange, this);
					weightFld.on("keyup", this.onWeightFldChange, this);
				}

				// 计算随访分组
				var checkResultFld = frm.findField("checkResult");
				if (checkResultFld) {
					checkResultFld.on("blur", this.onCheckResultBlur, this);
				}
				var transferTreatmentFld = frm.findField("transferTreatment");
				if (transferTreatmentFld) {
					transferTreatmentFld.on("change",
							this.onTransferTreatmemtChange, this);
				}
				
				//检查结果
				var checkResultFld = frm.findField("checkResult");
				if(checkResultFld){
					checkResultFld.on("lookup", this.doAddCheckRusult, this);
					checkResultFld.on("clear", this.clearCheckRusult, this);
				}
				chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.onReady
						.call(this);
			},
			onHighRiskTypeChange : function() {
				var frm = this.form.getForm();
				var highRiskTypeFld = frm.findField("highRiskType");
				if (highRiskTypeFld) {
					this.highRiskType = highRiskTypeFld.getValue();
					var sexCode = this.exContext.empiData.sexCode;
					if (this.highRiskType == "6" && sexCode != "2") {
						highRiskTypeFld.setValue();
						Ext.Msg.alert("提示", "性别不符合，女性才可选择“宫颈”高危类别！");
						return;
					}
					this.hideOrShowSymptom();
				}
			},
			onVisitEffectChange : function() {
				var frm = this.form.getForm();
				var visitEffectFld = frm.findField("visitEffect");
				if (!visitEffectFld) {
					return;
				}
				var visitEffectVal = visitEffectFld.getValue();
				var items = this.schema.items;
				if (!this.notNullSchemaItems) {
					this.notNullSchemaItems = [];
					for (var i = 0; i < items.length; i++) {
						if (items[i]["not-null"] || items[i]["not-null"] == "1") {
							this.notNullSchemaItems.push(items[i].id);
						}
					}
				}
				if (visitEffectVal == '1' || visitEffectVal == '3') {// @@ 恢复原必填项。
					for (var i = 0; i < items.length; i++) {
						if (items[i].id == "nextDate" && this.planMode != 2) {
							continue;
						}
						var field = frm.findField(items[i].id);
						if (items[i].id == "nextDate" && this.planMode == 2) {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update("<span style='color:red'>"
												+ items[i].alias + ":</span>");
							}
							continue;
						}
						if (field
								&& this.notNullSchemaItems.indexOf(items[i].id) > -1) {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update("<span style='color:red'>"
												+ items[i].alias + ":</span>");
							}
							field.validate();
						}
					}
					this.setFieldDisable(true, "lostVisitReason");
					this.setFieldNotNull(false, "lostVisitReason");
					this.setFieldDisable(true, "terminatedReason");
					this.setFieldNotNull(false, "terminatedReason");
					this.setFieldDisable(true, "deadDate");
					this.setFieldNotNull(false, "deadDate");
					this.setFieldDisable(true, "deadReason");
					this.setFieldNotNull(false, "deadReason");
					return;
				}

				if (visitEffectVal == '2' || visitEffectVal == '9') {// @@
					// 去除必填项
					for (var i = 0; i < items.length; i++) {
						if (items.evalOnServer || items[i].id == "visitDoctor") {
							continue;
						}

						var field = frm.findField(items[i].id);
						if (items[i].id == "nextDate" && this.planMode == 2) {
							field.allowBlank = false;
							items[i]["not-null"] = true;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update("<span style='color:red'>"
												+ items[i].alias + ":</span>");
							}
							continue;
						}

						if (field) {
							field.allowBlank = true;
							items[i]["not-null"] = false;
							if (Ext.getCmp(field.id).getEl()) {
								Ext.getCmp(field.id).getEl().up('.x-form-item')
										.child('.x-form-item-label')
										.update(items[i].alias + ":");
							}
							field.validate();
						}
					}
					if (visitEffectVal == '2') {
						this.setFieldDisable(false, "lostVisitReason");
						this.setFieldNotNull(true, "lostVisitReason");
						this.setFieldDisable(true, "terminatedReason");
						this.setFieldNotNull(false, "terminatedReason");
						this.setFieldDisable(true, "deadDate");
						this.setFieldDisable(true, "deadReason");
					}
					if (visitEffectVal == '9') {
						this.setFieldDisable(true, "lostVisitReason");
						this.setFieldNotNull(false, "lostVisitReason");
						this.setFieldDisable(false, "terminatedReason");
						this.setFieldNotNull(true, "terminatedReason");
					}
					// this.validate();
					return
				}
			},
			onTerminatedReasonChange : function() {
				var frm = this.form.getForm();
				var terminatedReasonFld = frm.findField("terminatedReason");
				if (!terminatedReasonFld) {
					return;
				}
				var trVal = terminatedReasonFld.getValue();
				if (trVal == "1") {
					this.setFieldDisable(false, "deadDate");
					this.setFieldDisable(false, "deadReason");
					this.setFieldNotNull(true, "deadDate");
					this.setFieldNotNull(true, "deadReason");
				} else {
					this.setFieldDisable(true, "deadDate");
					this.setFieldDisable(true, "deadReason");
					this.setFieldNotNull(false, "deadDate");
					this.setFieldNotNull(false, "deadReason");
				}
			},
			onGynecologySymptomFldItemCheck : function(checkedBox, checked) {
				var sexCode = this.exContext.empiData.sexCode;
				if (sexCode != "2") {
					return;
				}
				var frm = this.form.getForm();
				var boxGroup = frm.findField("gynecologySymptom");
				if (!boxGroup) {
					return;
				}
				var gsVal = boxGroup.getValue();
				if (gsVal == "") {
					boxGroup.setValue('01');
				}
				if (gsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = gsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onGynecologySymptomFldChange : function(boxGroup, checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("gynecologySymptom");
				}
				var gsVal = boxGroup.getValue();
				if (gsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "irregularVaginalBleeding");
				} else {
					this.setFieldDisable(true, "irregularVaginalBleeding");
				}
				if (gsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "intermittentBackache");
				} else {
					this.setFieldDisable(true, "intermittentBackache");
				}
				if (gsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "vaginalDischarge");
				} else {
					this.setFieldDisable(true, "vaginalDischarge");
				}
				if (gsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "urgentUrinationAnusDrop");
				} else {
					this.setFieldDisable(true, "urgentUrinationAnusDrop");
				}
				if (gsVal.indexOf("06") != -1) {
					this.setFieldDisable(false, "lowerAbdomenPain");
				} else {
					this.setFieldDisable(true, "lowerAbdomenPain");
				}
				if (gsVal.indexOf("07") != -1) {
					this.setFieldDisable(false, "edemaOfLowerLimbs");
				} else {
					this.setFieldDisable(true, "edemaOfLowerLimbs");
				}
			},
			setDisableFieldsOfGS : function() {
				this.setFieldDisable(true, "irregularVaginalBleeding");
				this.setFieldDisable(true, "intermittentBackache");
				this.setFieldDisable(true, "vaginalDischarge");
				this.setFieldDisable(true, "urgentUrinationAnusDrop");
				this.setFieldDisable(true, "lowerAbdomenPain");
				this.setFieldDisable(true, "edemaOfLowerLimbs");
			},
			onAnorectalSymptomFldItemCheck : function(checkedBox, checked) {
				var frm = this.form.getForm();
				var boxGroup = frm.findField("anorectalSymptom");
				if (!boxGroup) {
					return;
				}
				var gsVal = boxGroup.getValue();
				if (gsVal == "") {
					boxGroup.setValue('01');
				}
				if (gsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = gsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onAnorectalSymptomFldChange : function(boxGroup, checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("anorectalSymptom");
				}
				var gsVal = boxGroup.getValue();
				if (gsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "frequent");
				} else {
					this.setFieldDisable(true, "frequent");
				}
				if (gsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "constipation");
				} else {
					this.setFieldDisable(true, "constipation");
				}
				if (gsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "tenesmus");
				} else {
					this.setFieldDisable(true, "tenesmus");
				}
				if (gsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "DCAA");
				} else {
					this.setFieldDisable(true, "DCAA");
				}
				if (gsVal.indexOf("06") != -1) {
					this.setFieldDisable(false, "brightRedStool");
				} else {
					this.setFieldDisable(true, "brightRedStool");
				}
				if (gsVal.indexOf("07") != -1) {
					this.setFieldDisable(false, "jamSamplesThen");
				} else {
					this.setFieldDisable(true, "jamSamplesThen");
				}
				if (gsVal.indexOf("08") != -1) {
					this.setFieldDisable(false, "mucousStool");
				} else {
					this.setFieldDisable(true, "mucousStool");
				}
				if (gsVal.indexOf("09") != -1) {
					this.setFieldDisable(false, "shitAttenuateOrVariant");
				} else {
					this.setFieldDisable(true, "shitAttenuateOrVariant");
				}
				if (gsVal.indexOf("10") != -1) {
					this.setFieldDisable(false, "unKnownCausesAnemia");
				} else {
					this.setFieldDisable(true, "unKnownCausesAnemia");
				}
				if (gsVal.indexOf("11") != -1) {
					this.setFieldDisable(false, "abdominalFixedSitePain");
				} else {
					this.setFieldDisable(true, "abdominalFixedSitePain");
				}
			},
			setDisableFieldsOfAS : function() {
				this.setFieldDisable(true, "frequent");
				this.setFieldDisable(true, "constipation");
				this.setFieldDisable(true, "tenesmus");
				this.setFieldDisable(true, "DCAA");
				this.setFieldDisable(true, "brightRedStool");
				this.setFieldDisable(true, "jamSamplesThen");
				this.setFieldDisable(true, "mucousStool");
				this.setFieldDisable(true, "shitAttenuateOrVariant");
				this.setFieldDisable(true, "unKnownCausesAnemia");
				this.setFieldDisable(true, "abdominalFixedSitePain");
			},
			onRespiratorySystemSymptomFldItemCheck : function(checkedBox,
					checked) {
				var frm = this.form.getForm();
				var boxGroup = frm.findField("respiratorySystemSymptom");
				if (!boxGroup) {
					return;
				}
				var gsVal = boxGroup.getValue();
				if (gsVal == "") {
					boxGroup.setValue('01');
				}
				if (gsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = gsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onRespiratorySystemSymptomFldChange : function(boxGroup,
					checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("respiratorySystemSymptom");
				}
				var gsVal = boxGroup.getValue();
				if (gsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "cough");
				} else {
					this.setFieldDisable(true, "cough");
				}
				if (gsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "expectoration");
				} else {
					this.setFieldDisable(true, "expectoration");
				}
				if (gsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "bloodySputum");
				} else {
					this.setFieldDisable(true, "bloodySputum");
				}
				if (gsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "pectoralgia1");
				} else {
					this.setFieldDisable(true, "pectoralgia1");
				}
				if (gsVal.indexOf("06") != -1) {
					this.setFieldDisable(false, "chestCongestion");
				} else {
					this.setFieldDisable(true, "chestCongestion");
				}
			},
			setDisableFieldsOfRSS : function() {
				this.setFieldDisable(true, "cough");
				this.setFieldDisable(true, "expectoration");
				this.setFieldDisable(true, "bloodySputum");
				this.setFieldDisable(true, "pectoralgia1");
				this.setFieldDisable(true, "chestCongestion");
			},
			onSymptomsOfChestFldItemCheck : function(checkedBox, checked) {
				var frm = this.form.getForm();
				var boxGroup = frm.findField("symptomsOfChest");
				if (!boxGroup) {
					return;
				}
				var gsVal = boxGroup.getValue();
				if (gsVal == "") {
					boxGroup.setValue('01');
				}
				if (gsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = gsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onSymptomsOfChestFldChange : function(boxGroup, checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("symptomsOfChest");
				}
				var gsVal = boxGroup.getValue();
				if (gsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "pectoralgia2");
				} else {
					this.setFieldDisable(true, "pectoralgia2");
				}
				if (gsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "breastLump");
				} else {
					this.setFieldDisable(true, "breastLump");
				}
				if (gsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "breastPartFixedPain");
				} else {
					this.setFieldDisable(true, "breastPartFixedPain");
				}
				if (gsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "nippleErosion");
				} else {
					this.setFieldDisable(true, "nippleErosion");
				}
				if (gsVal.indexOf("06") != -1) {
					this.setFieldDisable(false, "nippleDischarge");
				} else {
					this.setFieldDisable(true, "nippleDischarge");
				}
				if (gsVal.indexOf("07") != -1) {
					this.setFieldDisable(false, "bilateralBreastAsymmetry");
				} else {
					this.setFieldDisable(true, "bilateralBreastAsymmetry");
				}
			},
			setDisableFieldsOfSOC : function() {
				this.setFieldDisable(true, "pectoralgia2");
				this.setFieldDisable(true, "breastLump");
				this.setFieldDisable(true, "breastPartFixedPain");
				this.setFieldDisable(true, "nippleErosion");
				this.setFieldDisable(true, "nippleDischarge");
				this.setFieldDisable(true, "bilateralBreastAsymmetry");
			},
			onStomachSymptomsFldItemCheck : function(checkedBox, checked) {
				var frm = this.form.getForm();
				var boxGroup = frm.findField("stomachSymptoms");
				if (!boxGroup) {
					return;
				}
				var gsVal = boxGroup.getValue();
				if (gsVal == "") {
					boxGroup.setValue('01');
				}
				if (gsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = gsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onStomachSymptomsFldChange : function(boxGroup, checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("stomachSymptoms");
				}
				var gsVal = boxGroup.getValue();
				if (gsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "belching");
				} else {
					this.setFieldDisable(true, "belching");
				}
				if (gsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "acidRegurgitation");
				} else {
					this.setFieldDisable(true, "acidRegurgitation");
				}
				if (gsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "epigastricPain");
				} else {
					this.setFieldDisable(true, "epigastricPain");
				}
				if (gsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "haematemesisMelena");
				} else {
					this.setFieldDisable(true, "haematemesisMelena");
				}
			},
			setDisableFieldsOfSS : function() {
				this.setFieldDisable(true, "belching");
				this.setFieldDisable(true, "acidRegurgitation");
				this.setFieldDisable(true, "epigastricPain");
				this.setFieldDisable(true, "haematemesisMelena");
			},
			onBodySymptomFldItemCheck : function(checkedBox, checked) {
				var frm = this.form.getForm();
				var boxGroup = frm.findField("bodySymptom");
				if (!boxGroup) {
					return;
				}
				var bsVal = boxGroup.getValue();
				if (bsVal == "") {
					boxGroup.setValue('01');
				}
				if (bsVal.indexOf("01") != -1) {
					if (checkedBox.inputValue == "01" && checked) {
						boxGroup.setValue("01")
					} else {
						var valueArray = bsVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "01") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onBodySymptomFldChange : function(boxGroup, checkedBoxs) {
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("bodySymptom");
				}
				var bsVal = boxGroup.getValue();
				if (bsVal.indexOf("02") != -1) {
					this.setFieldDisable(false, "weak");
				} else {
					this.setFieldDisable(true, "weak");
				}
				if (bsVal.indexOf("03") != -1) {
					this.setFieldDisable(false, "anorexia");
				} else {
					this.setFieldDisable(true, "anorexia");
				}
				if (bsVal.indexOf("04") != -1) {
					this.setFieldDisable(false, "hepatalgia");
				} else {
					this.setFieldDisable(true, "hepatalgia");
				}
				if (bsVal.indexOf("05") != -1) {
					this.setFieldDisable(false, "emaciation");
				} else {
					this.setFieldDisable(true, "emaciation");
				}
				if (bsVal.indexOf("06") != -1) {
					this.setFieldDisable(false, "fever");
				} else {
					this.setFieldDisable(true, "fever");
				}
			},
			setDisableFieldsOfBS : function() {
				this.setFieldDisable(true, "weak");
				this.setFieldDisable(true, "anorexia");
				this.setFieldDisable(true, "hepatalgia");
				this.setFieldDisable(true, "emaciation");
				this.setFieldDisable(true, "fever");
			},
			onSuperviseMyselfFldChange : function(radioGroup, checkedRadio) {
				if (!radioGroup) {
					var frm = this.form.getForm();
					radioGroup = frm.findField("superviseMyself");
				}
				var smVal = radioGroup.getValue();
				if (smVal == 'y') {
					this.setFieldDisable(false, "checkResult");
				} else {
					this.setFieldDisable(true, "checkResult");
				}
			},
			onFamilyHistoryFldChange : function(radioGroup, checkedRadio) {
				if (!radioGroup) {
					var frm = this.form.getForm();
					radioGroup = frm.findField("familyHistory");
				}
				var smVal = radioGroup.getValue();
				if (smVal == '1') {
					this.setFieldDisable(false, "part");
					this.setFieldDisable(false, "relationship");
				} else {
					this.setFieldDisable(true, "part");
					this.setFieldDisable(true, "relationship");
				}
			},
			onCalculateBMI : function() {
				var frm = this.form.getForm();
				var bmi = frm.findField("bmi");
				if (bmi) {
					var w = frm.findField("weight").getValue();
					var h = frm.findField("height").getValue();
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2);
					bmi.setValue(b);
				}
			},
			onHeightFldChange : function(field) {
				var frm = this.form.getForm();
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
				var weight = frm.findField("weight").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					frm.findField("BMI").setValue((weight / temp).toFixed(2));
				}
			},
			onWeightFldChange : function(field) {
				var frm = this.form.getForm();
				if (!field.validate()) {
					MyMessageTip.msg("提示", "没有取到计划ID！请重试", true);
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
				var height = frm.findField("height").getValue();
				if (height && weight) {
					var temp = height * height / 10000;
					frm.findField("BMI").setValue((weight / temp).toFixed(2));
				}
			},
			onCheckResultBlur : function() {
				var frm = this.form.getForm();
				var checkResultFld = frm.findField("checkResult");
				if (checkResultFld) {
					var crv = checkResultFld.getValue();
					var fixGroupFld = frm.findField("fixGroup");
					var ttVal = frm.findField("transferTreatment").getValue();
					if (crv.indexOf("阳性") != -1 || ttVal == '2') {
						if (fixGroupFld) {
							fixGroupFld.setValue({
										key : "2",
										text : "高危组"
									});
						}
					} else {
						if (fixGroupFld) {
							fixGroupFld.setValue({
										key : "1",
										text : "常规组"
									});
						}
					}
				}
			},
			onTransferTreatmemtChange : function(radioGroup, checkedRadio) {
				var frm = this.form.getForm();
				if (!radioGroup) {
					radioGroup = frm.findField("transferTreatment");
				}
				var ttVal = radioGroup.getValue();
				var fixGroupFld = frm.findField("fixGroup");
				var crv = frm.findField("checkResult").getValue();
				if (ttVal == '2' || crv.indexOf("阳性") != -1) {// 转诊
					if (fixGroupFld) {
						fixGroupFld.setValue({
									key : "2",
									text : "高危组"
								});
					}
				} else {
					if (fixGroupFld) {
						fixGroupFld.setValue({
									key : "1",
									text : "常规组"
								});
					}
				}
			},
			doAddCheckRusult : function(field){
				if (!field.disabled) {
					var planId = this.exContext.args.planId;
					if(!planId || planId == ''){
						
						return;
					}
					var frm = this.form.getForm();
					var visitEffectFld = frm.findField("visitEffect");
					if(visitEffectFld){
						this.exContext.args.visitEffect = visitEffectFld.getValue() || '';
					}
					var thrvCheckResultModule = this.midiModules["thrvCheckResultModule"];
					if (!thrvCheckResultModule) {
						$import("chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList");
						thrvCheckResultModule = new chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList({
							title : "随访检查项目结果",
							autoLoadSchema : true,
							isCombined : false,
							autoLoadData : true,
							mutiSelect : true,
							modal : true,
							queryCndsType : "1",
							height:300,
							width:780,
							exContext : this.exContext,
							entryName : "chis.application.tr.schemas.MDC_TumourScreeningCheckResult"
						});
						thrvCheckResultModule.initPanel();
						this.midiModules["thrvCheckResultModule"] = thrvCheckResultModule;
					}
					thrvCheckResultModule.setMainApp(this.mainApp)
					thrvCheckResultModule.exContext = this.exContext;
					thrvCheckResultModule.on("select",function(records){
						var recordIdArr = [],checkResultArr=[],crKeys=[];
						for(var i=0,len=records.length;i<len;i++){
							var r = records[i];
							var recordId = r.get("recordId");
							recordIdArr.push(recordId);
							var checkItem = r.get("checkItem");
							var checkResult = r.get("checkResult_text");
							checkResultArr.push(checkItem+":"+checkResult);
							var crv = r.get("checkResult");
							crKeys.push(crv);
						}
						var frm = this.form.getForm();
						var visitEffectFld = frm.findField("visitEffect");
						var crvs = crKeys.join(',');
						if(crvs.indexOf('4') > -1){
							if(visitEffectFld){
								visitEffectFld.setValue(3);
								visitEffectFld.disable();
							}
						}else{
							if(visitEffectFld){
								visitEffectFld.setValue(1);
								visitEffectFld.enable();
							}
						}
						var checkResultIdsFld = frm.findField("checkResultIds");
						this.checkResultIds = recordIdArr.join(',');
						if(checkResultIdsFld){
							checkResultIdsFld.setValue(this.checkResultIds);
						}
						var checkResultFld = frm.findField("checkResult");
						if(checkResultFld){
							checkResultFld.setValue(checkResultArr.join('|'));
						}
					},this);
					var win = thrvCheckResultModule.getWin();
					win.setPosition(260, 240);
					win.show();
					thrvCheckResultModule.loadData();
				}
			},
			clearCheckRusult : function(){
				var frm = this.form.getForm();
				var checkResultFld = frm.findField("checkResult");
				var checkResultIdsFld = frm.findField("checkResultIds");
				if(checkResultIdsFld){
					checkResultIdsFld.setValue();
				}
				if (checkResultFld) {
					checkResultFld.setValue();
				}
			},
			afterSaveData : function(entryName, op, json, data) {
				Ext.apply(data, json.body);
				var JKCFRecords = data.JKCFRecords;
				if (!JKCFRecords) {
					return;
				}
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			doPrintRecipe : function() {
				if (!this.exContext.args.visitId) {
					return;
				}
				var url = "resources/chis.prints.template.HealthRecipelManage.print?type="
						+ 1
						+ "&wayId="
						+ this.exContext.args.visitId
						+ "&guideWay=04";
				url += "&temp=" + new Date().getTime()
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			}

		});