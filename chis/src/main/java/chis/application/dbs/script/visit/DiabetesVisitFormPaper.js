$package("chis.application.dbs.script.visit")
$import("chis.script.BizHtmlFormView",
		"chis.application.dbs.script.visit.DiabetesVisitPaperTemplate",
		"chis.script.util.widgets.MyMessageTip")
$styleSheet("chis.css.DiabetesVisitPaper")

chis.application.dbs.script.visit.DiabetesVisitFormPaper = function(cfg) {
	cfg.width = 850
	cfg.autoLoadData = false;
	cfg.isAutoScroll = true;
	chis.application.dbs.script.visit.DiabetesVisitFormPaper.superclass.constructor.apply(this, [cfg])
	Ext.apply(this,chis.application.dbs.script.visit.DiabetesVisitPaperTemplate);
//	this.createFields = ["referralReason","visitDate", "testDate", "nextDate", "visitDoctor"];
	this.createFields = ["referralReason","visitDate", "nextDate", "visitDoctor"];
	this.disableFlds = ["noVisitReason_1", "noVisitReason_2",
			"noVisitReason_3", "noVisitReason_4", "otherSymptoms", "bmi",
			"mbBmi", "this.medicineName_1", "medicineFrequency_1",
			"medicineDosage_1", "this.medicineName_2", "medicineFrequency_2",
			"medicineDosage_2", "this.medicineName_3", "medicineFrequency_3",
			"medicineDosage_3", "this.medicineName_4", "medicineFrequency_4",
			"medicineDosage_4", 
			//"medicineOtherNot", "medicineNot_1",
			//"medicineNot_2", "medicineNot_3", "medicineNot_4",
			//"medicineNot_99",
			"diabetesType_y", "diabetesType_n"];
	this.otherDisable = [{
				fld : "symptoms",
				type : "checkbox",
				control : [{
							key : "99",
							field : ["otherSymptoms"]
						}]
			},{
		fld : "visitEffect",
		type : "radio",
		control : [{
			key : '1',
			exp : 'eq',
			mustField : ["this.visitDate", "constriction", "diastolic",
					"weight", "targetWeight", "smokeCount", "targetSmokeCount",
					"drinkCount", "targetDrinkCount", "trainTimesWeek",
					"trainMinute", "targetTrainTimesWeek", "targetTrainMinute",
					"food", "targetFood", "medicine", "visitWay"],
//			redLabels : ["SFRQ", "SFFS", "XY", "TZ", "TZZS", "RXYL", "RYJL",
//					"YD", "ZS", "FYYCX"],
			oppositeRL : ["ZGYY"],
			oppositeMF : ["noVisitReason", "noVisitReason_1",
					"noVisitReason_2", "noVisitReason_3", "noVisitReason_4"]
		}]
	}, {
		fld : "medicine",
		type : "radio",
		control : [{
			key : '3',
			exp : 'ne',
			field : ["this.medicineName_1", "this.medicineName_2",
					"this.medicineName_3", "this.medicineName_4"],
			disField : ["medicineFrequency_1", "medicineDosage_1",
					"medicineUnit_1", "medicineFrequency_2",
					"medicineDosage_2", "medicineUnit_2",
					"medicineFrequency_3", "medicineDosage_3",
					"medicineUnit_3", "medicineFrequency_4",
					"medicineDosage_4", "medicineUnit_4"]
		}
//		, {
//			key : '1',
//			exp : 'eq',
//			disField : ["medicineNot", "medicineOtherNot", "medicineNot_1",
//					"medicineNot_2", "medicineNot_3", "medicineNot_4",
//					"medicineNot_99"],
//			oppositeRL : ["BGLYY"],
//			oppositeMF : ["medicineNot", "medicineOtherNot", "medicineNot_1",
//					"medicineNot_2", "medicineNot_3", "medicineNot_4",
//					"medicineNot_99"]
//		}
		]
	}, 
//		{
//		fld : "medicineNot",
//		type : "radio",
//		control : [{
//					key : '99',
//					exp : 'ne',
//					oppositeMF : ["medicineOtherNot"]
//				}]
//	}, 
		{
		fld : "healthProposal",
		type : "checkbox",
		control : [{
					key : '8',
					exp : 'eq',
					field : ["otherHealthProposal"]
				}]
	}];
	this.mutualExclusion = [{
				fld : "symptoms",
				key : "1",
				other : ["otherSymptoms"]
			}];
	var nowDate = this.mainApp.serverDate
	this.nowDate = nowDate
	this.planMode = this.mainApp.exContext.diabetesMode
	this.on("loadDataByLocal", this.onLoadDataByLocal, this);
	this.on("beforePrint", this.onBeforePrint, this);
}
var thisdbsPanel = null;
Ext.extend(chis.application.dbs.script.visit.DiabetesVisitFormPaper,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getDBSVisitTemplate();
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				for (var s = 0, sLen = this.schemas.length; s < sLen; s++) {
					var items = this.schemas[s].items;
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.isCreateField(it.id)) {
							// 对自创建手动创建的字段在新建里赋初值，赋值后返回true，否则字段会设置为空值
							var isSet = this.setMyFieldValue(it.id);
							if (!isSet) {
								eval("this." + it.id + ".setValue()");
							}
						} else {
							if (it.dic) {
								var fs = document.getElementsByName(it.id);
								if (fs && fs.length > 0) {
									for (var j = 0, len = fs.length; j < len; j++) {
										var f = fs[j];
										if (f.type == "checkbox"
												|| f.type == "radio") {
											if (f.checked) {
												f.checked = false;
											}
										}
									}
								}
							} else {
								var f = document.getElementById(it.id
										+ this.idPostfix)
								if (f) {
									f.value = f.defaultValue || '';
									if (f.defaultValue) {
										f.style.color = "#999";// 填充注释文字，设灰色
									}
								}
							}
						}
					}
				}
				this.fieldValidate(this.schema);
				this.fireEvent("doNew", this.form)
			},
			initHTMLFormData : function(data, schema) {
				if (!schema) {
					schema = this.schema;
				}
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				// Ext.apply(this.data, data)
				var items = schema.items;
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i];
					if (it.display
							&& (it.display == "1" || it.display == 0 || it.hidden)) {
						continue;
					}
					if (it.pkey) {
						this.initDataId = data[it.id];
					}
					if (this.isCreateField(it.id)) {
						var cfv = data[it.id];
						if (!cfv && it.defaultValue) {
							cfv = it.defaultValue;
						}
						if (!cfv) {
							cfv = "";
						}

						if (it.type == "date") {
							if (typeof cfv != "string") {
								cfv = Ext.util.Format.date(cfv, 'Y-m-d');
							} else {
								cfv = cfv.substring(0, 10);
							}
						}
						if (!cfv || cfv == '') {
							cfv = this[it.id].defaultValue;
						}
						eval("this." + it.id + ".setValue(cfv);");
						eval("this." + it.id + ".validate();");
					} else {
						if (it.dic) {
							if (!this.fireEvent("dicFldSetValue", it.id, data)) {
								continue;
							} else {
								var dfs = document.getElementsByName(it.id);
								if (!dfs) {
									continue;
								}
								var dicFV = data[it.id];
								var fv = "";
								if (it.defaultValue) {
									fv = it.defaultValue.key;
								}
								if (dicFV) {
									fv = dicFV.key;
								}
								if (!fv) {
									continue;
								}
								var dvs = fv.split(",");
								for (var j = 0, len = dvs.length; j < len; j++) {
									var f = document.getElementById(it.id + "_"
											+ dvs[j] + this.idPostfix);
									if (f) {
										f.checked = true;
									}
								}
								if (dvs.length > 0) {
									var div = document.getElementById("div_"
											+ it.id + this.idPostfix);
									if (div) {
										this.removeClass(div, "x-form-invalid");
									}
								}
							}
						} else {
							var f = document.getElementById(it.id+ this.idPostfix)
							if (f) {
								var v = data[it.id];
								if (!v && !(v == 0) && !it.defaultValue) {
									v = f.defaultValue || "";
									if (f.defaultValue) {
										f.style.color = "#999";
									}
								} else if (!v && !(v == 0)) {
									v = it.defaultValue;
									f.style.color = "#000";// 不是注释文字，改黑色字体
								} else {
									f.style.color = "#000";// 不是注释文字，改黑色字体
								}
								f.value = v;
								if (it['not-null'] == "1"
										|| it['not-null'] == "true") {
									if (data[it.id] && data[it.id] != "") {
										this.removeClass(f, "x-form-invalid");
									}
								}
							}
						}
					}
				}
				this.onDiabetesTypeSelect();
				this.setKeyReadOnly(true)
				// this.startValues = form.getValues(true);
				this.resetButtons(); // ** 用于页面按钮权限控制
				// this.focusFieldAfter(-1, 800);
			},
			addFieldAfterRender : function() {
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				// 随访日期
				this.visitDate = new Ext.form.DateField({
							name : 'visitDate',
							width : 350,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_visitDate" + this.idPostfix)
						});
				// 检查日期
				this.testDate = new Ext.form.DateField({
							name : 'testDate',
							width : 150,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '检查日期',
							maxValue : curDate,
							fieldLabel : "检查日期",
							renderTo : Ext.get("div_testDate" + this.idPostfix)
						});

				// 下次随访日期
				var ndCfg = {
					name : 'nextDate',
					width : 350,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					fieldLabel : "下次随访日期",
					renderTo : Ext.get("div_nextDate" + this.idPostfix)
				};
				if (this.planMode == 2) {
					ndCfg.allowBlank = false;
					ndCfg.invalidText = "必填字段";
				}
				this.nextDate = new Ext.form.DateField(ndCfg);
				// 随访医生
				this.visitDoctor = this.createDicField({
							"width" : 350,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user",
							"render" : "Tree",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true,
							"parentKey" : "%user.manageUnit.id",
							"defaultValue" : {
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname + "--"+ this.mainApp.jobtitle
							}
						});
				this.visitDoctor.render("div_visitDoctor" + this.idPostfix);
				//转诊原因
				this.referralReason = this.createDicField({
							"width" : 350,
							"id" : "chis.dictionary.reason01",
							"render" : "LovCombo",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true
						});
				this.referralReason.render("div_referralReason" + this.idPostfix);
				// 用药情况
				var me = this;
				for (var i = 1; i < 5; i++) {
					var fldId = "medicineName_" + i;
					this[fldId] = this.createLocalDicField({
								width : 480,
								id : fldId + this.idPostfix,
								name : fldId,
								afterSelect : function(t, record) {
									var id = t.container.id;
									var last_idx = id.lastIndexOf('_');
									var idx = id.substring(last_idx - 1,last_idx);
									var tx = document.getElementById('medicineUnit_'+ idx + me.idPostfix);
									tx.value = record.data.JLDW;
									var mfFld = document.getElementById("medicineFrequency_"+ idx + me.idPostfix);
									if (mfFld) {
										mfFld.disabled = false;
										me.addClass(mfFld, "x-form-invalid");
									}
									var mdFld = document.getElementById("medicineDosage_"+ idx + me.idPostfix);
									if (mdFld) {
										mdFld.value = record.data.YPJL;
										mdFld.disabled = false;
										if (mdFld.value == "") {
											me.addClass(mdFld,"x-form-invalid");
										}
									}
								},
								afterClear : function(t) {
									var id = t.container.id;
									var last_idx = id.lastIndexOf('_');
									var idx = id.substring(last_idx - 1,last_idx);
									var tx = document.getElementById('medicineUnit_'+ idx + me.idPostfix);
									tx.value = "";
									var mfFld = document.getElementById("medicineFrequency_"+ idx + me.idPostfix);
									if (mfFld) {
										mfFld.value = "";
										mfFld.disabled = true;
										me.removeClass(mfFld, "x-form-invalid");
									}
									var mdFld = document.getElementById("medicineDosage_"+ idx + me.idPostfix);
									if (mdFld) {
										mdFld.value = "";
										mdFld.disabled = true;
										me.removeClass(mdFld, "x-form-invalid");
									}
								}
							});
					this[fldId].addClass("input_btline");
					this[fldId].render(Ext.get("div_" + fldId + this.idPostfix));
				}
				thisdbsPanel=this;
				var serverDate = this.mainApp.serverDate;
				this.visitDate.maxValue = Date.parseDate(serverDate, "Y-m-d");
			},
			onReadyAffter : function() {
				var me = this;
				for (var i = 1; i < 5; i++) {
					var mfFld = document.getElementById("medicineFrequency_"+ i + me.idPostfix);
					if (mfFld) {
						var handleFun = function(maxLength, notNull, alias,obj, me) {
							return function() {
								me.validateString(maxLength, notNull, alias,obj, me);
							}
						}
						this.addEvent(mfFld, "change", handleFun(20, true,"每日次数", mfFld, me));
					}
					var mdFld = document.getElementById("medicineDosage_" + i
							+ me.idPostfix);
					if (mdFld) {
						var length = 10;
						var precision = 2;
						var maxValue = undefined;
						var minValue = undefined;
						var notNull = true;
						var alias = "每次剂量"
						var handleFun = function(length, precision, minValue,
								maxValue, notNull, alias, obj, me) {
							return function() {
								me.validateDouble(length, precision, minValue,
										maxValue, notNull, alias, obj, me);
							}
						}
						this.addEvent(mdFld, "change", handleFun(length,
										precision, minValue, maxValue, notNull,
										alias, mdFld, me));
					}
				}
				// 计算BMI
				var height = me.height;
				var weightFld = document.getElementById("weight" + me.idPostfix);
				if (weightFld) {
					var handleFun = function(weightObj, bmiFldName, height, me) {
						return function() {
							me.onWeightChangeCalculateBMI(weightObj,bmiFldName, height, me);
						}
					}
					this.addEvent(weightFld, "change", handleFun(weightFld,"bmi", height, me));
				}
				var targetWeightFld = document.getElementById("targetWeight"
						+ me.idPostfix);
				if (targetWeightFld) {
					var handleFun = function(weightObj, bmiFldName, height, me) {
						return function() {
							me.onWeightChangeCalculateBMI(weightObj,bmiFldName, height, me);
						}
					}
					this.addEvent(targetWeightFld, "change", handleFun(targetWeightFld, "mbBmi", height, me));
				}
				// **空腹血糖，餐后血糖 至少填一个
				var fbsField = document.getElementById("fbs" + me.idPostfix);
				if (fbsField) {
					var handleFun = function(me) {
						return function() {
							me.fbsAndpbsHasOne(me);
						}
					}
					this.addEvent(fbsField, "change", handleFun(me));
				}
				var pbsField = document.getElementById("pbs" + me.idPostfix);
				if (pbsField) {
					var handleFun = function(me) {
						return function() {
							me.fbsAndpbsHasOne(me);
						}
					}
					this.addEvent(pbsField, "change", handleFun(me));
				}
				this.fbsAndpbsHasOne(me);
				var diabetesTypes = document.getElementsByName("diabetesType");
				var frmEl = this.form.getEl();
				for (var i = 0; i < diabetesTypes.length; i++) {
					var diabetesType = diabetesTypes[i];
					if (!frmEl.contains(diabetesType)) {
						continue;
					}
					if (diabetesType) {
						var handleFun = function(me) {
							return function() {
								me.onDiabetesTypeSelect(me);
							}
						}
						this.addEvent(diabetesType, "click", handleFun(me));
					}
				}
			},
			onDiabetesTypeSelect : function() {
				var diabetesTypeValue = this.getHtmlFldValue("diabetesType");
				if (!diabetesTypeValue || diabetesTypeValue == "") {
					this.setHtmlFldValue("diabetesChange", "n");
				}
				if (this.diabetesType && this.diabetesType == diabetesTypeValue) {
					this.setHtmlFldValue("diabetesChange", "n");
				} else {
					this.setHtmlFldValue("diabetesChange", "y");
				}
			},
			// 空腹血糖，餐后血糖 至少填一个
			fbsAndpbsHasOne : function(me) {
				var fbsField = document.getElementById("fbs" + me.idPostfix);
				var pbsField = document.getElementById("pbs" + me.idPostfix);
				var fbsLab = document.getElementById("KFXT" + me.idPostfix);
				var pbsTLab = document.getElementById("CHXT" + me.idPostfix);
				var visitEffect = me.getHtmlFldValue("visitEffect");
				var fbs = fbsField.value || '';
				var pbs = pbsField.value || '';
				var tms = "空腹血糖餐后血糖必须输入一项";
				if (visitEffect == "1" || visitEffect == '') {
					if (fbs == "" && pbs == "") {
						me.addClass(fbsField, "x-form-invalid")
						//fbsLab.style.color = "#FF0000";
						fbsField.title = tms;
						me.addClass(pbsField, "x-form-invalid")
						//pbsTLab.style.color = "#FF0000";
						pbsField.title = tms;
					} else if (fbs && fbs != '') {
						me.removeClass(fbsField, "x-form-invalid")
						fbsField.title = "";
						me.removeClass(pbsField, "x-form-invalid")
						//pbsTLab.style.color = "#000000";
						pbsField.title = "";
					} else if (pbs && pbs != '') {
						me.removeClass(fbsField, "x-form-invalid")
						//fbsLab.style.color = "#000000";
						fbsField.title = "";
						me.removeClass(pbsField, "x-form-invalid")
						pbsField.title = "";
					} else {
						me.removeClass(fbsField, "x-form-invalid")
						//fbsLab.style.color = "#000000";
						fbsField.title = "";
						me.removeClass(pbsField, "x-form-invalid")
						me.removeClass(pbsField, "x-form-invalid")
						//pbsTLab.style.color = "#000000";
						pbsField.title = "";
					}
				} else {
					me.removeClass(fbsField, "x-form-invalid")
					//fbsLab.style.color = "#000000";
					fbsField.title = "";
					me.removeClass(pbsField, "x-form-invalid")
					//pbsTLab.style.color = "#000000";
					pbsField.title = "";
				}
			},
			onWeightChangeCalculateBMI : function(weightObj, bmiFldName,height, me) {
				var bmi = document.getElementById(bmiFldName + me.idPostfix);
				if (bmi) {
					var w = weightObj.value;
					var h = height || me.height;
					if (w == "" || h == "") {
						bmi.value = "";
						return
					}
					var b = (w/(h*h/10000)).toFixed(2);
					bmi.value = b;
				}
			},
			setDBSVisitMedicineList : function(vmData) {
				var fldNum = this.DBSVisitMedicineList.length;
				if (vmData.length == 0) {
					for (var i = 0, len = 4; i < len; i++) {
						for (var j = 0; j < fldNum; j++) {
							var fn = this.DBSVisitMedicineList[j];
							var fv = "";
							if (fn.type == "div") {
								this[fn.id + "_" + (i + 1)].selectData.YPMC = fv;
								this[fn.id + "_" + (i + 1)].setValue(fv);
							} else {
								var f = document.getElementById(fn.id + "_"
										+ (i + 1) + this.idPostfix);
								if (f) {
									if (fn.id == "days") {
										fv = "1";
									}
									f.value = fv || '';
								}
							}
						}
					}
				} else {
					for (var i = 0, len = vmData.length; i < len; i++) {
						if (i == 4) {
							break;
						}
						for (var j = 0; j < fldNum; j++) {
							var fn = this.DBSVisitMedicineList[j];
							var fv = vmData[i][fn.id];
							if (fn.type == "div") {
								this[fn.id + "_" + (i + 1)].selectData.YPMC = fv;
								this[fn.id + "_" + (i + 1)].setValue(fv);
							} else {
								var f = document.getElementById(fn.id + "_"+ (i + 1) + this.idPostfix);
								if (f) {
									f.value = fv || '';
								}
							}
						}
					}
				}
			},
			getDBSVisitMedicineList : function() {
				var vmList = [];
				var fldNum = this.DBSVisitMedicineList.length;
				for (var i = 0, len = 4; i < len; i++) {
					var vmData = {};
					vmData["phrId"] = this.exContext.ids.phrId;
					vmData["visitId"] = this.exContext.args.r.get("visitId");
					var isUsable = true;
					for (var j = 0; j < fldNum; j++) {
						var fn = this.DBSVisitMedicineList[j];
						if (fn.type == "div") {
							vmData[fn.id] = this[fn.id + "_" + (i + 1)].getValue();
							if (vmData[fn.id] == '') {
								isUsable = false;
								break;
							}
						} else {
							var f = document.getElementById(fn.id+"_"+(i+1)+this.idPostfix);
							if (f) {
								vmData[fn.id] = f.value || '';
							}
						}
					}
					if (isUsable) {
						vmList.push(vmData);
					}
				}
				return vmList;
			},
			onLoadDataByLocal : function(entryName, data, op) {
				this.height = data.height;
				this.resetControlOtherFld();
				var me = this;
				var targetWeightFld = document.getElementById("targetWeight"+ me.idPostfix);
				if (targetWeightFld) {
					this.onWeightChangeCalculateBMI(targetWeightFld, "mbBmi",this.height, me);
				}
				this.fbsAndpbsHasOne(me);
//				var medicineNot = "";
//				if (data["medicineNot"]) {
//					medicineNot = data["medicineNot"].key
//							|| data["medicineNot"];
//				}
//				var medicineOtherNot = "";
//				if (data["medicineOtherNot"]) {
//					medicineOtherNot = data["medicineOtherNot"];
//				}
//				this.setHtmlFldValue("medicineNot", medicineNot);
//				this.setHtmlFldValue("medicineOtherNot", medicineOtherNot);
			},
			doSave : function() {
				// if(this.saving){
				// return
				// }
				this.visitEffect = this.getHtmlFldValue("visitEffect");
				if (this.visitEffect == "1") {
					if (!this.htmlFormSaveValidate()) {
						return;
					}
				} else {
					var val = this.getHtmlFldValue("noVisitReason");
					var div = document.getElementById("div_noVisitReason"+ this.idPostfix)
					if (!val || val == "") {
						div.title = "原因为必填项";
						if (document.getElementsByName("noVisitReason")[0]) {
							document.getElementsByName("noVisitReason")[0].focus();
						}
						MyMessageTip.msg("提示", div.title, true);
						return;
					}
				}
				var values = this.getFormData();
				Ext.apply(this.data, values);
//				if (values["medicine"] && values["medicine"] != 1
//						&& !values["medicineNot"]) {
//					document.getElementsByName("medicineNot")[0].focus();
//					document.getElementsByName("medicineNot")[0].select();
//					MyMessageTip.msg("提示", "不规律服药原因不能为空", true);
//					return;
//				}
//				if (values["medicineNot"] && values["medicineNot"] == 99
//						&& !values["medicineOtherNot"]) {
//					document
//							.getElementById("medicineOtherNot" + this.idPostfix)
//							.focus();
//					document
//							.getElementById("medicineOtherNot" + this.idPostfix)
//							.select();
//					MyMessageTip.msg("提示", "其他不规律服药原因不能为空", true);
//					return;
//				}
				this.saveToServer(values)
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,saveData)) {
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				saveData["empiId"] = this.exContext.ids.empiId
				saveData["phrId"] = this.exContext.ids.phrId
				saveData["visitId"] = this.exContext.args.r.get("visitId")
				saveData["planId"] = this.exContext.args.r.get("planId")
				saveData["planDate"] = this.exContext.args.r.get("planDate")
				saveData["endDate"] = this.exContext.args.r.get("endDate")
				saveData["lateInput"] = this.lateInput
				saveData["height"] = this.height
				saveData["visitUnit"] = this.mainApp.deptId
				if (this.getHtmlFldValue("visitEffect") != '9') {
					var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
					var planD = this.getDate(this.visitDate.getValue(), "Y-m-d");
					var nextDate;
					if (this.nextDate.getValue) {
						nextDate = Date.parseDate(this.nextDate.getValue(),"Y-m-d");
					}
					if (nextDate && planD >= now) {
						if (nextDate <= planD) {
							MyMessageTip.msg("提示", "预约日期必须大于计划日期", true)
							return
						}
					} else {
						if (nextDate && nextDate <= now) {
							MyMessageTip.msg("提示", "预约日期必须大于当前日期", true)
							return
						}
					}
				}
				var fbsField = document.getElementById("fbs" + this.idPostfix)
				var pbsField = document.getElementById("pbs" + this.idPostfix)
				var fbsVal = fbsField.value;
				var pbsVal = pbsField.value;
				if (fbsVal == "" && pbsVal == "" && this.visitEffect == "1") {
					fbsField.focus();
					this.addClass(fbsField, "x-form-invalid")
					document.getElementById("KFXT" + this.idPostfix).style.color = "#FF0000";
					MyMessageTip.msg("提示", "空腹血糖餐后血糖必须输入一个", true)
					return
				}
				this.saving = true
				var medicine = this.getHtmlFldValue("medicine");
				if ((medicine == "3" || medicine == "4" || medicine == "")
						&& (medicine == '1' || medicine == '2') && this.op == "update") {
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
				saveData.diabetesFormType = this.mainApp.exContext.diabetesType;
				saveData.vmList = this.getDBSVisitMedicineList();
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
								this.processReturnMsg(code, msg,this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								if (json.body.needReferral == true && (this.referralReason.getValue() == null || this.referralReason.getValue() == "")) {
									Ext.Msg.show({
										title : '温馨提示',
										msg : '鉴于你当前的情况，我们建议您转诊！',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.OK,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "ok") {
												this.referralReason.focus();
												return;
											}
										},
										scope:this
									});
									return;
								}
								this.initFormData(json.body)
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data)
							this.op = "update"
						}, this)
			},

			onBeforePrint : function(type, pages, ids_str) {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先保存记录。");
					return false;
				}
				pages.value = "chis.prints.htmlTemplate.diabetesVisit";
				ids_str.value = "&phrId="+this.exContext.ids.phrId+"&empiId="+this.exContext.ids.empiId;
			},
			doDeletevisit:function(){
				if(!this.exContext.args &&!this.exContext.args.r && !this.exContext.args.r.data){
					Ext.Msg.alert("提示","请选择一条随访计划!");
					return;
				}
				var result=util.rmi.miniJsonRequestSync({
							serviceId:"chis.diabetesVisitService",
							serviceAction:"deleteVisitPlan",
							method:"execute",
							planId:this.exContext.args.r.data["planId"]
						});
				if(result.json.deletecount==0){
					Ext.Msg.alert("提示","本记录已经随访，不能删除！");
				}
				this.fireEvent("visitPlanDelete");
			}
		})
		
function ondbsWeightChange(v, f) {
	if (thisdbsPanel.height && v) {
		var temp = thisdbsPanel.height * thisdbsPanel.height / 10000;
		var bmi = (v / temp).toFixed(2);
		f.style.color = "#000";
		f.value = bmi;
	} else {
		f.value = f.defaultValue;
		f.style.color = "#999";
	}
	thisdbsPanel.fieldValidate(this.schema)
};
function onpulsationchange(pulsation){
	var pulsation_1= document.getElementById("pulsation_1"+thisdbsPanel.idPostfix);
	var pulsation_2= document.getElementById("pulsation_2"+thisdbsPanel.idPostfix);
	var pulsation_3= document.getElementById("pulsation_3"+thisdbsPanel.idPostfix);
	var pulsation_4= document.getElementById("pulsation_4"+thisdbsPanel.idPostfix);
	var pulsation_5= document.getElementById("pulsation_5"+thisdbsPanel.idPostfix);
	var pulsation_6= document.getElementById("pulsation_6"+thisdbsPanel.idPostfix);
	var pulsation_7= document.getElementById("pulsation_7"+thisdbsPanel.idPostfix);
	if(pulsation=="1" && pulsation_1.checked==true ){
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_3"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_4"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_6"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_7"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="2" && pulsation_2.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_3"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_4"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_6"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_7"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="3" && pulsation_3.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_6"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="4" && pulsation_4.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_7"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="5" && pulsation_5.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_3"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_4"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_6"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_7"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="6" && pulsation_6.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_3"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
	}
	if(pulsation=="7" && pulsation_7.checked==true ){
		document.getElementById("pulsation_1"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_2"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_4"+thisdbsPanel.idPostfix).checked=false;
		document.getElementById("pulsation_5"+thisdbsPanel.idPostfix).checked=false;
	}
}