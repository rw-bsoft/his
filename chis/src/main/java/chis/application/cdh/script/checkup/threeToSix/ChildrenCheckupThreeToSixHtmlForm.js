$package("chis.application.cdh.script.checkup.threeToSix")
$import(
		"chis.script.BizHtmlFormView",
		"chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixTemplate",
		"util.Accredit")
$styleSheet("chis.css.checkupInOne")

chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixHtmlForm = function(
		cfg) {
	cfg.idPostfix = cfg.idPostfix || "_cio36";
	this.nineCase = "36";
	this.tenCase = "48";
	this.elevenCase = "60";
	this.twelveCase = "72";
	chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixHtmlForm.superclass.constructor
			.apply(this, [cfg]);
	this.initServiceAction = "initChildCheckUp";
	this.on("beforePrint", this.onBeforePrint, this);
	this.on("doNew", this.onDoNew, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.onLoadData, this);
	Ext
			.apply(
					this,
					chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixTemplate);
	this.createFields = ["checkupDate", "nextCheckupDate", "checkDoctor"];
	this.disableFlds = ["checkupStage", "weightDevelopment",
			"heightDevelopment", "devEvaluation","otherGuide", "referralUnit",
			"referralReason", "this.checkDoctor", "pneumoniaCount",
			"diarrheaCount", "traumaCount", "otherCount"]
	// enable
	this.otherDisable = [{
				fld : "illnessType",
				type : "checkbox",
	        	mutex:true,
				control : [
				        {
				        	key:"0",
				        	exp:"eq",
				        	field:["mutex"],
				        	mutex:true
				        },
				        {
							key : "1",
							exp : 'eq',
							field : ["pneumoniaCount"]
						}, {
							key : "2",
							exp : 'eq',
							field : ["diarrheaCount"]
						}, {
							key : "3",
							exp : 'eq',
							field : ["traumaCount"]
						}, {
							key : "4",
							exp : 'eq',
							field : ["otherCount"]
						}]
			}, {
				fld : "referral",
				type : "radio",
				control : [{
							key : "y",
							exp : 'eq',
							field : ["referralUnit", "referralReason"]
						}]
			}, {
				fld : "guide",
				type : "checkbox",
				control : [{
							key : "5",
							exp : 'eq',
							field : ["otherGuide"]
						}]
			}];
}

Ext
		.extend(
				chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixHtmlForm,
				chis.script.BizHtmlFormView, {
					getHTMLTemplate : function() {
						return this.getCheckupThreeToSixHTML();
					},
					addFieldAfterRender : function() {
						var curDate = new Date();
						this.checkupDate = new Ext.form.DateField({
									name : 'checkupDate',
									defaultValue : curDate,
									width : 200,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '随访日期',
									allowBlank : false,
									invalidText : "必填字段",
									regex : /(^\S+)/,
									regexText : "前面不能有空格字符",
									renderTo : Ext.get("div_checkupDate"
											+ this.idPostfix)
								});
						this.checkupDate.setValue(curDate);
						var cfg = {
							"width" : 200,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user",
							"render" : "Tree",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true
						}
						var deptId = this.mainApp.deptId;
						cfg.parentKey = deptId;
						this.checkDoctor = this.createDicField(cfg);
						this.checkDoctor.allowBlank = true;
						this.checkDoctor.fieldLabel = "随访医生";
						this.checkDoctor.tree.expandAll();
						this.checkDoctor.render(Ext.get("div_checkDoctor"
								+ this.idPostfix));
						this.checkDoctor.setValue({
									key : this.mainApp.uid,
									text : this.mainApp.uname
								});
						this.checkDoctor.disable();

						this.nextCheckupDate = new Ext.form.DateField({
									name : 'nextCheckupDate',
									width : 200,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '下次随访日期',
									allowBlank : true,
									renderTo : Ext.get("div_nextCheckupDate"
											+ this.idPostfix)
								});
					},
					onBeforeCreate : function() {
						if (!this.exContext.args[this.checkupType + "_param"].checkupDate) {
							return;
						}
						this.data["phrId"] = this.exContext.ids["CDH_HealthCard.phrId"];
						this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
						util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method : "execute",
							schema : this.entryName,
							body : {
								"phrId" : this.exContext.ids["CDH_HealthCard.phrId"],
								"empiId" : this.exContext.ids.empiId,
								"birthday" : this.exContext.empiData.birthday,
								"needInit" : this.exContext.args[this.checkupType
										+ "_param"].needInit,
								"checkupDate" : this.exContext.args[this.checkupType
										+ "_param"].checkupDate,
								"nextCheckupDate" : this.exContext.args[this.checkupType
										+ "_param"].nextCheckupDate,
								"checkupStage" : this.exContext.args[this.checkupType
										+ "_param"].checkupStage
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.body) {
								var data = json.body;
								this.initFormData(data);
							}
						}, this)
					},

					onDoNew : function() {
						var checkupDate = this.checkupDate;
						if (checkupDate) {
							checkupDate
									.setMinValue(this.exContext.args[this.checkupType
											+ "_param"].minVisitDate);
							checkupDate
									.setMaxValue(this.exContext.args[this.checkupType
											+ "_param"].maxVisitDate);
						}

						var notNextPlan = this.exContext.args.notNextPlan;
						if (notNextPlan) {
							var res = util.rmi.miniJsonRequestSync({
								serviceId : this.saveServiceId,
								serviceAction : "getNextPlanVisited",
								method : "execute",
								body : {
									"checkupStage" : this.exContext.args[this.checkupType
											+ "_param"].checkupStage,
									"recordId" : this.exContext.ids["CDH_HealthCard.phrId"]
								}
							})
							if (res.code == 200) {
								var nextPlanVisted = res.nextPlanVisted;
								if (nextPlanVisted) {
									this.exContext.args.minNextVisitDate = null;
								}
								this.valNextVisitDate();
							}
						} else {
							this.valNextVisitDate();
						}
						this.setFieldEnable();
						this.addFieldDataValidateFun(this.schema);
						this.checkDoctor.setValue({
									key : this.mainApp.uid,
									text : this.mainApp.uname
								});
						this.checkupDate.setValue(new Date());
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
									eval("this." + it.id + ".enable()");
								} else {
									if (it.dic) {
										var fs = document
												.getElementsByName(it.id);
										if (fs && fs.length > 0) {
											for (var j = 0, len = fs.length; j < len; j++) {
												var f = fs[j];
												if (f.type == "checkbox"
														|| f.type == "radio") {
													if (f.checked) {
														f.checked = false;
													}
													f.disabled = false;
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
											f.disabled = false;
										}
									}
								}
							}
						}
						this.initFieldDisable();
						this.changeHtmlField();
						this.fieldValidate(this.schema);
						this.fireEvent("doNew", this.form);
						if (this.initDataId) {
							this.fireEvent("beforeUpdate", this); // **
							// 在数据加载之前做一些初始化操作
						} else {
							this.fireEvent("beforeCreate", this); // **
																	// 在页面新建时做一些初始化操作
						}
					},
					initFieldDisable : function() {// 初始化时设置不可用字段
						var len = this.disableFlds.length;
						var frmEl = this.form.getEl();
						for (var i = 0; i < len; i++) {
							var fn = this.disableFlds[i];
							var fldId = fn + this.idPostfix;
							if (fn.indexOf('.') != -1) {
								eval(fn + ".disable()");
							} else {
								var fld = document.getElementById(fldId);
								if (fld) {
									// fld.style.display = "none";
									fld.disabled = true;
								} else {
									var flds = document.getElementsByName(fn);
									for (var j = 0; j < flds.length; j++) {
										var f = flds[j];
										if (frmEl.contains(f)) {
											f.disabled = true;
										}
									}
								}
							}
						}
					},
					valNextVisitDate : function() {
						var nextTime = this.nextCheckupDate;
						if (nextTime) {
							var minNextVisitDate = this.exContext.args[this.checkupType
									+ "_param"].minNextVisitDate;
							if (minNextVisitDate) {
								nextTime.setMinValue(minNextVisitDate);
								nextTime.enable();
							} else {
								nextTime.disable();
							}
						}
					},
					changeHtmlField : function() {
						var checkupStage = this.exContext.args[this.checkupType
								+ "_param"].checkupStage;
						var hearingFs = document.getElementsByName("hearing");
						if (checkupStage > this.nineCase) {
							for (var i = 0; i < hearingFs.length; i++) {
								var hearingF = hearingFs[i];
								hearingF.checked = false;
								hearingF.disabled = true;
							}
						} else {
							for (var i = 0; i < hearingFs.length; i++) {
								var hearingF = hearingFs[i];
								hearingF.checked = false;
								hearingF.disabled = false;
							}
						}
						var eyesightFs = document.getElementsByName("eyesight");
						if (checkupStage <= this.nineCase) {
							for (var i = 0; i < eyesightFs.length; i++) {
								var eyesightF = eyesightFs[i];
								eyesightF.checked = false;
								eyesightF.disabled = true;
							}
						} else {
							for (var i = 0; i < eyesightFs.length; i++) {
								var eyesightF = eyesightFs[i];
								eyesightF.checked = false;
								eyesightF.disabled = false;
							}
						}

						var tzbqhbhFs = document.getElementsByName("tzbqhbhs");
						var zyqbrcfjFs = document.getElementsByName("zyqbrcfj");
						var jcyssbszFs = document.getElementsByName("jcyssbsz");
						if (checkupStage == this.nineCase) {
							for (var i = 0; i < tzbqhbhFs.length; i++) {
								var tzbqhbhF = tzbqhbhFs[i];
								tzbqhbhF.checked = false;
								tzbqhbhF.disabled = false;
							}
							for (var i = 0; i < zyqbrcfjFs.length; i++) {
								var zyqbrcfjF = zyqbrcfjFs[i];
								zyqbrcfjF.checked = false;
								zyqbrcfjF.disabled = false;
							}
							for (var i = 0; i < jcyssbszFs.length; i++) {
								var jcyssbszF = jcyssbszFs[i];
								jcyssbszF.checked = false;
								jcyssbszF.disabled = false;
							}
						} else {
							for (var i = 0; i < tzbqhbhFs.length; i++) {
								var tzbqhbhF = tzbqhbhFs[i];
								tzbqhbhF.checked = false;
								tzbqhbhF.disabled = true;
							}
							for (var i = 0; i < zyqbrcfjFs.length; i++) {
								var zyqbrcfjF = zyqbrcfjFs[i];
								zyqbrcfjF.checked = false;
								zyqbrcfjF.disabled = true;
							}
							for (var i = 0; i < jcyssbszFs.length; i++) {
								var jcyssbszF = jcyssbszFs[i];
								jcyssbszF.checked = false;
								jcyssbszF.disabled = true;
							}
						}

					},
					loadData : function() {
						var checkupStageF = document
								.getElementById("checkupStage" + this.idPostfix)
						if (checkupStageF) {
							checkupStageF.value = this.exContext.args[this.checkupType
									+ "_param"].checkupStage;
						}
						if (this.checkDoctor) {
							this.checkDoctor.setValue({
										key : this.mainApp.uid,
										text : this.mainApp.uname
									});
						}
						var initDataId = this.exContext.args[this.checkupType
								+ "_param"].checkupId
						if (initDataId) {
							this.initDataId = initDataId;
						} else {
							this.initDataId = null;
						}
						chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixHtmlForm.superclass.loadData
								.call(this);
					},
					onBeforeSave : function(entryName, op, saveData) {

						saveData.empiId = this.exContext.ids.empiId;
						saveData.phrId = this.exContext.ids.phrId;
						saveData.manaUnitId = this.mainApp.deptId;
						saveData.checkupId = this.initDataId
								|| this.exContext.args[this.checkupType
										+ "_param"].checkupId;
						saveData.checkupStage = this.exContext.args[this.checkupType
								+ "_param"].checkupStage;
						var isReturn = this.htmlFormSaveValidate();
						return isReturn;
					},

					saveToServer : function(saveData) {
						if (!this.fireEvent("beforeSave", this.entryName,
								this.op, saveData)) {
							return;
						}
						if (this.initDataId == null) {
							this.op = "create";
						}
						this.saveData = saveData
						this.saving = true
						this.form.el.mask("正在保存数据...", "x-mask-loading")
						util.rmi.jsonRequest({
									serviceId : this.saveServiceId,
									op : this.op,
									schema : this.entryName,
									method : "execute",
									body : this.saveData,
									serviceAction : this.saveAction
								}, function(code, msg, json) {
									this.form.el.unmask()
									this.saving = false
									if (code != 200) {
										this.processReturnMsg(code, msg);
										return
									}
									Ext.apply(this.data, json.body);
									if (json.body) {
										this.initFormData(json.body)
										this.fireEvent("save", this.entryName,
												this.op, json, this.data)
									}
									this.op = "update"
									var isWeek = json.isWeek;
									if (!isWeek) {
										return;
									}
									if (json.recordCount == 0) {
										Ext.Msg.show({
											title : '体弱儿童建档',
											msg : '是否需要建立体弱儿童档案',
											modal : true,
											width : 300,
											buttons : Ext.MessageBox.OKCANCEL,
											multiline : false,
											fn : function(btn, text) {
												if (btn == "ok") {
													var data = {
														"empiId" : this.data.empiId,
														"phrId" : this.data.phrId,
														"debilityReason" : json.debilityReason
													}
													this.fireEvent(
															"activeDebility",
															data);
												}
											},
											scope : this
										});
									} else {
										Ext.MessageBox.alert("消息",
												"有未结案的体弱儿档案，请更新体弱儿档案！")
									}
								}, this)// jsonRequest
					},
					onLoadData : function(entryName, body) {
						this.setFieldEnable();
						this.addFieldDataValidateFun(this.schema);
					},
					setFieldEnable : function() {
						var referral = this.getHtmlFldValue("referral");
						var guide = this.getHtmlFldValue("guide");
						var referralUnitF = document
								.getElementById("referralUnit" + this.idPostfix);
						var referralReasonF = document
								.getElementById("referralReason"
										+ this.idPostfix);

						var illnessType = this.getHtmlFldValue("illnessType");
						var pneumoniaCount = document
								.getElementById("pneumoniaCount"
										+ this.idPostfix);
						var diarrheaCount = document
								.getElementById("diarrheaCount"
										+ this.idPostfix);
						var traumaCount = document.getElementById("traumaCount"
								+ this.idPostfix);
						var otherCount = document.getElementById("otherCount"
								+ this.idPostfix);
						
						if (illnessType.indexOf('1') != -1) {
							pneumoniaCount.disabled = false;
						}else
						{
							pneumoniaCount.disabled = true;
						}
						if (illnessType.indexOf('2') != -1) {
							diarrheaCount.disabled = false;
						}else
						{
							diarrheaCount.disabled = true;
						}
						if (illnessType.indexOf('3') != -1) {
							traumaCount.disabled = false;
						}else
						{
							traumaCount.disabled = true;
						}
						if (illnessType.indexOf('4') != -1) {
							otherCount.disabled = false;
						}else
						{
							otherCount.disabled = true;
						}
						
						var otherGuideF = document.getElementById("otherGuide"
								+ this.idPostfix);
						if (referral == "y") {
							referralUnitF.disabled = false;
							referralReasonF.disabled = false;
						} else {
							referralUnitF.disabled = true;
							referralReasonF.disabled = true;
						}
						if (guide.indexOf('5') != -1) {
							otherGuideF.disabled = false;
						} else {
							otherGuideF.disabled = true;
						}
					},
					initHTMLFormData : function(data, schema) {
						this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
						// Ext.apply(this.data, data)
						var items = schema.items
						var n = items.length;
						for (var i = 0; i < n; i++) {
							var it = items[i]
							if (it.id == "checkupStage") {
								var f = document.getElementById(it.id
										+ this.idPostfix);
								if (f) {
									var v = data[it.id];
									if (!v) {
										v = "";
									}
									f.value = v.key;
									if (it['not-null'] == "1"
											|| it['not-null'] == "true") {
										if (data[it.id] && data[it.id] != "") {
											this.removeClass(f,
													"x-form-invalid");
										}
									}
								}
								continue;
							}
							if (it.pkey) {
								this.initDataId = data[it.id];
							}
							if (it.display
									&& (it.display == "1" || it.display == 0 || it.hidden)) {
								continue;
							}
							if (this.isCreateField(it.id)) {
								var cfv = data[it.id]
								if (!cfv && it.defaultValue) {
									cfv = it.defaultValue;
								}
								if (!cfv) {
									cfv = "";
								}
								if (it.type == "date") {
									if (typeof cfv != "string") {
										cfv = Ext.util.Format
												.date(cfv, 'Y-m-d');
									} else {
										cfv = cfv.substring(0, 10);
									}
								}
								eval("this." + it.id + ".setValue(cfv);this."
										+ it.id + ".validate();");
							} else {
								if (it.dic) {
									if (!this.fireEvent("dicFldSetValue",
											it.id, data)) {
										continue;
									} else {
										var dfs = document
												.getElementsByName(it.id);
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
										if (!fv) {// yubo
											continue;
										}
										var dvs = fv.split(",");
										for (var j = 0, len = dvs.length; j < len; j++) {
											var f = document
													.getElementById(it.id + "_"
															+ dvs[j]
															+ this.idPostfix);
											if (f) {
												f.checked = true;
											}
										}
									}
								} else {
									var f = document.getElementById(it.id
											+ this.idPostfix)
									if (f) {
										var v = data[it.id];
										if (!v && !it.defaultValue) {
											v = f.defaultValue || "";
											if (f.defaultValue) {
												f.style.color = "#999";
											}
										} else if (!v) {
											v = it.defaultValue;
											f.style.color = "#000";// 不是注释文字，改黑色字体
										} else {
											f.style.color = "#000";// 不是注释文字，改黑色字体
										}
										f.value = v;
										if (it['not-null'] == "1"
												|| it['not-null'] == "true") {
											if (data[it.id]
													&& data[it.id] != "") {
												this.removeClass(f,
														"x-form-invalid");
											}
										}
									}
								}
							}
						}
						this.setKeyReadOnly(true)
						// this.startValues = form.getValues(true);
						this.resetButtons(); // ** 用于页面按钮权限控制
						// this.focusFieldAfter(-1, 800);
					}
				});
