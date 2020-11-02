$package("chis.application.mhc.script.postnatal42day");

$import("chis.script.BizHtmlFormView",
		"chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlTemplate");
$styleSheet("chis.css.Postnatal42day")

chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlForm = function(
		cfg) {
	chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlForm.superclass.constructor
			.apply(this, [cfg]);
	Ext
			.apply(
					this,
					chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlTemplate);
	// this.loadServiceId = "chis.psychosisVisitService";
	// this.loadAction = "initalizeFirstVisitModule";
	this.createFields = ["checkDate", "checkDoctor"];
	this.disableFlds = ["breastText", "lochiaText", "uterusText", "woundText",
			"classificationText", "suggestionText"];
	this.otherDisable = [{
				fld : "breast",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["breastText"]
						}]
			}, {
				fld : "lochia",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["lochiaText"]
						}]
			}, {
				fld : "uterus",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["uterusText"]
						}]
			}, {
				fld : "wound",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["woundText"]
						}]
			}, {
				fld : "classification",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["classificationText"]
						}]
			}, {
				fld : "suggestion",
				type : "checkbox",
				control : [{
							key : "99",
							exp : 'eq',
							field : ["suggestionText"]
						}]
			}, {
				fld : "breast",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["breastText"]
						}]
			}, {
				fld : "treat",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["reason", "doccol"]
						}]
			}];
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("beforePrint", this.onBeforePrint, this);
}
var thisPanel = null;
Ext
		.extend(
				chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlForm,
				chis.script.BizHtmlFormView, {
					onReady : function() {
						chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlForm.superclass.onReady
								.call(this);
						thisPanel = this;
					},

					onBeforeSave : function(entryName, op, saveData) {
						saveData.empiId = this.exContext.ids.empiId;
						saveData.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
						saveData.recordId = this.initDataId;
						saveData.checkManaUnit = this.data.checkManaUnit||this.mainApp.deptId;
						var f1=document.getElementById("constriction"+this.idPostfix);
						var f2=document.getElementById("diastolic"+this.idPostfix);
						var v1 = f1.value;
						var v2 = f2.value;
						var d1 = f1.defaultValue;
						var d2 = f2.defaultValue;
						if (v1 == d1 || v2 == d2) {
							return;
						}
						if (v1.length <= v2.length && v1 <= v2) {
							f1.title = "收缩压必须大于舒张压。";
							f2.title = "舒张压必须小于收缩压。";
							thisPanel.addClass(f1, "x-form-invalid");
							thisPanel.addClass(f2, "x-form-invalid");
							MyMessageTip.msg("提示", "收缩压必须大于舒张压", true);
							return false;
						} else {
							f1.title = "收缩压(mmHg)";
							f2.title = "舒张压(mmHg)";
							thisPanel.removeClass(f1, "x-form-invalid");
							thisPanel.removeClass(f2, "x-form-invalid");
						}
						return this.htmlFormSaveValidate(this.schema);
					},

					getLoadRequest : function() {
						this.initDataId = null;
						return {
							"fieldName" : "pregnantId",
							"fieldValue" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
						};
					},

					onLoadData : function() {
						this.resetControlOtherFld();
						this.fieldValidate(this.schema);
					},

					onLoadNoData : function() {
						this.data["empiId"] = this.exContext.ids.empiId;
						this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
						util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "initPostnatal42dayRecord",
							method : "execute",
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
								this.checkDoctor.setValue({
											"key" : this.mainApp.uid,
											"text" : this.mainApp.uname
										})
							}
						}, this)
						this.resetControlOtherFld();
						this.fieldValidate(this.schema);
					},

					addFieldAfterRender : function() {
						var curDate = Date.parseDate(this.mainApp.serverDate,
								"Y-m-d");
						this.checkDate = new Ext.form.DateField({
									name : 'checkDate' + this.idPostfix,
									width : 200,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '随访日期',
									maxValue : curDate,
									allowBlank : false,
									invalidText : "必填字段",
									fieldLabel : "随访日期",
									renderTo : Ext.get("div_checkDate"
											+ this.idPostfix)
								});
						// 随访医生签名
						var cfg = {
							"width" : 200,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user",
							"render" : "Tree",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true,
							"defaultValue" : {
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname
							}
						}
						// var deptId = this.mainApp.deptId;
						// if (deptId.length > 9) {
						// cfg.parentKey = deptId;
						// }
						this.checkDoctor = this.createDicField(cfg);
						// this.checkDoctor.fieldLabel = "医生签字";
						// this.checkDoctor.allowBlank = false;
						// this.checkDoctor.invalidText = "必填字段";
						// this.checkDoctor.regex = /(^\S+)/
						// this.checkDoctor.regexText = "前面不能有空格字符";
						this.checkDoctor.tree.expandAll();
						this.checkDoctor.render(Ext.get("div_checkDoctor"
								+ this.idPostfix));
						this.checkDoctor.disable()
					},

					onBeforePrint : function(type, pages, ids_str) {
						if (!this.initDataId) {
							Ext.Msg.alert("提示", "请先保存记录。");
							return false;
						}
						pages.value = "chis.prints.htmlTemplate.postnatal42day";
						ids_str.value = "&recordId=" + this.initDataId
								+ "&empiId=" + this.exContext.ids.empiId;
					}
				})

function onXYChange(f1, f2) {
	var v1 = f1.value;
	var v2 = f2.value;
	var d1 = f1.defaultValue;
	var d2 = f2.defaultValue;
	if (v1 == d1 || v2 == d2) {
		return;
	}
	if (v1.length <= v2.length && v1 <= v2) {
		f1.title = "收缩压必须大于舒张压。";
		f2.title = "舒张压必须小于收缩压。";
		thisPanel.addClass(f1, "x-form-invalid");
		thisPanel.addClass(f2, "x-form-invalid");
	} else {
		f1.title = "收缩压(mmHg)";
		f2.title = "舒张压(mmHg)";
		thisPanel.removeClass(f1, "x-form-invalid");
		thisPanel.removeClass(f2, "x-form-invalid");
	}
}