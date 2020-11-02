/**
 * 孕妇产后访视表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatalVisit")
$import("chis.script.BizHtmlFormView", "chis.script.util.helper.Helper",
		"chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlTemplate")
$styleSheet("chis.css.PostnatalVisitInfo")

chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm = function(
		cfg) {
	// cfg.colCount = 2;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 135;
	cfg.labelWidth = 130;
	chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm.superclass.constructor
			.apply(this, [cfg])
	Ext
			.apply(
					this,
					chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlTemplate);
	this.createFields = ["visitDate", "birthDay", "checkDoctor",
			"nextVisitDate"];
	this.disableFlds = ["postnatalDays", "breastText", "lochiaText",
			"uterusText", "woundText", "classificationText", "otherSuggestion"];
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
							field : ["otherSuggestion"]
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
				fld : "referral",
				type : "radio",
				control : [{
							key : "y",
							exp : 'eq',
							field : ["reason", "doccol"]
						}]
			}];
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadData, this);
	this.on("doNew", this.beforeCreate, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("beforePrint", this.onBeforePrint, this);
}
Ext.extend(
		chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm,
		chis.script.BizHtmlFormView, {
			onReady : function() {
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm.superclass.onReady
						.call(this);
				thisPanel = this;
			},

			onLoadData : function() {
				this.resetControlOtherFld();
				this.fieldValidate(this.schema);
			},

			onBeforeSave : function() {
				var f1 = document.getElementById("constriction"
						+ this.idPostfix);
				var f2 = document.getElementById("diastolic" + this.idPostfix);
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

			getSaveRequest : function(savaData) {
				savaData.checkManaUnit = this.data.checkManaUnit
				savaData.empiId = this.exContext.ids.empiId;
				savaData.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				savaData.visitId = this.initDataId;
				return savaData;
			},

			beforeCreate : function() {
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "initPostnatalVisitInfo",
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
						this.data["checkManaUnit"] = data.checkManaUnit.key
					}
					this.data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
					this.checkDoctor.setValue({
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname
							})
					this.resetControlOtherFld();
					this.nextVisitDate
							.setMinValue(chis.script.util.helper.Helper
									.getOneDayAfterDate(d));
				}, this)
			},

			loadData : function() {
				this.doNew();
				var datas = this.exContext.args.formDatas
				if (!datas) {
					return;
				}
				this.initFormData(datas);
				var createDate = datas["createDate"];
				var d = Date.parseDate(createDate.substr(0, 10), "Y-m-d")
				this.nextVisitDate.setMinValue(chis.script.util.helper.Helper
						.getOneDayAfterDate(d));
				this.nextVisitDate.validate();
				this.fireEvent("loadData");
			},

			initFormData : function(data) {
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm.superclass.initFormData
						.call(this, data)
				this.changeBirthDay(this.birthDay);
			},

			addFieldAfterRender : function() {
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							maxValue : curDate,
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "随访日期",
							renderTo : Ext
									.get("div_visitDate" + this.idPostfix)
						});
				this.visitDate.on("keyup", this.onBirthDayChange, this)
				this.visitDate.on("select", this.onBirthDayChange, this)
				this.visitDate.on("blur", this.onBirthDayChange, this)
				this.birthDay = new Ext.form.DateField({
							name : 'birthDay' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '出生日期',
							maxValue : curDate,
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "出生日期",
							renderTo : Ext.get("div_birthDay" + this.idPostfix)
						});
				this.birthDay.on("keyup", this.changeBirthDay, this)
				this.birthDay.on("select", this.changeBirthDay, this)
				this.birthDay.on("blur", this.changeBirthDay, this)
				this.nextVisitDate = new Ext.form.DateField({
							name : 'nextVisitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '下次随访日期',
							minValue : curDate,
							// allowBlank : false,
							// invalidText : "必填字段",
							fieldLabel : "下次随访日期",
							renderTo : Ext.get("div_nextVisitDate"
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
				this.checkDoctor = this.createDicField(cfg);
				this.checkDoctor.tree.expandAll();
				this.checkDoctor.render(Ext.get("div_checkDoctor"
						+ this.idPostfix));
				this.checkDoctor.disable()
			},

			changeBirthDay : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					this.visitDate.setMinValue(null);
					return;
				}
				var visitValue = this.visitDate.getValue();
				if (visitValue < constriction) {
					this.visitDate.setValue(null);
				}
				this.visitDate.setMinValue(constriction);
				this.onBirthDayChange();
			},

			onBirthDayChange : function() {
				var postnatalDays = document.getElementById("postnatalDays"
						+ this.idPostfix)
				if (!this.birthDay.validate()) {
					return
				}
				if (!this.visitDate.validate()) {
					return
				}
				var date = this.birthDay.getValue().format('Y-m-d')
				var visitDate = this.visitDate.getValue().format('Y-m-d')
				postnatalDays.value = this.daysBetween(date, visitDate);
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

			onBeforePrint : function(type, pages, ids_str) {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先保存记录。");
					return false;
				}
				pages.value = "chis.prints.htmlTemplate.postnatalVisit";
				ids_str.value = "&recordId=" + this.initDataId + "&empiId="
						+ this.exContext.ids.empiId;
			}
		});

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