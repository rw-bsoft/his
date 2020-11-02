$package("chis.application.cdh.script.checkup.oneToTwo")
$import(
		"chis.script.BizHtmlFormView",
		"chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoTemplate",
		"util.Accredit")
$styleSheet("chis.css.checkupInOne")

chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoHtmlForm = function(
		cfg) {
	cfg.idPostfix = cfg.idPostfix || "_cio12";
	this.fiveCase = "12";
	this.sixCase = "18";
	this.sevenCase = "24";
	this.eightCase = "30";
	chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoHtmlForm.superclass.constructor
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
					chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoTemplate);
	this.createFields = ["checkupDate", "nextCheckupDate", "checkDoctor"];
	this.disableFlds = ["checkupStage", "weightDevelopment",
			"heightDevelopment", "otherGuide", "referralUnit",
			"referralReason", "this.checkDoctor", "bregmaTransverse",
			"bregmaLongitudinal"]
	// enable
	this.otherDisable = [{
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
			}, {
				fld : "bregmaClose",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							mustField : ["bregmaTransverse",
									"bregmaLongitudinal"],
							field : ["bregmaTransverse", "bregmaLongitudinal"]
						}]
			}];
}

Ext
		.extend(
				chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoHtmlForm,
				chis.script.BizHtmlFormView, {
					getHTMLTemplate : function() {
						return this.getCheckupOneToTwoHTML();
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
						if (checkupStage == this.fiveCase
								|| checkupStage == this.sevenCase) {
							for (var i = 0; i < hearingFs.length; i++) {
								var hearingF = hearingFs[i];
								hearingF.checked = false;
								hearingF.disabled = false;
							}
						} else {
							for (var i = 0; i < hearingFs.length; i++) {
								var hearingF = hearingFs[i];
								hearingF.checked = false;
								hearingF.disabled = true;
							}
						}

						var gaitFs = document.getElementsByName("gait");
						if (checkupStage == this.fiveCase) {
							for (var i = 0; i < gaitFs.length; i++) {
								var gaitF = gaitFs[i];
								gaitF.checked = false;
								gaitF.disabled = true;
							}
						} else {
							for (var i = 0; i < gaitFs.length; i++) {
								var gaitF = gaitFs[i];
								gaitF.checked = false;
								gaitF.disabled = false;
							}
						}

						var kyglbtzFs = document.getElementsByName("kyglbtz");
						if (checkupStage == this.eightCase) {
							for (var i = 0; i < kyglbtzFs.length; i++) {
								var kyglbtzF = kyglbtzFs[i];
								kyglbtzF.checked = false;
								kyglbtzF.disabled = true;
							}
						} else {
							for (var i = 0; i < kyglbtzFs.length; i++) {
								var kyglbtzF = kyglbtzFs[i];
								kyglbtzF.checked = false;
								kyglbtzF.disabled = false;
							}
						}

						var hgbFs = document.getElementsByName("hgb");
						if (checkupStage == this.fiveCase
								|| checkupStage == this.sevenCase) {
							for (var i = 0; i < hgbFs.length; i++) {
								var hgbF = hgbFs[i];
								hgbF.checked = false;
								hgbF.disabled = true;
							}
						} else {
							for (var i = 0; i < hgbFs.length; i++) {
								var hgbF = hgbFs[i];
								hgbF.checked = false;
								hgbF.disabled = false;
							}
						}

						var fywssFs = document.getElementsByName("fywss");
						if (checkupStage == this.eightCase) {
							for (var i = 0; i < fywssFs.length; i++) {
								var fywssF = fywssFs[i];
								fywssF.checked = false;
								fywssF.disabled = true;
							}
						} else {
							for (var i = 0; i < fywssFs.length; i++) {
								var fywssF = fywssFs[i];
								fywssF.checked = false;
								fywssF.disabled = false;
							}
						}

						var developmentFs = document
								.getElementsByName("development");
						if (checkupStage == this.eightCase) {
							for (var i = 0; i < developmentFs.length; i++) {
								var developmentF = developmentFs[i];
								developmentF.checked = false;
								developmentF.disabled = true;
							}
						} else {
							for (var i = 0; i < developmentFs.length; i++) {
								var developmentF = developmentFs[i];
								developmentF.checked = false;
								developmentF.disabled = false;
							}
						}

						var bregmaCloseFs = document
								.getElementsByName("bregmaClose");
						if (checkupStage == this.eightCase) {
							for (var i = 0; i < bregmaCloseFs.length; i++) {
								var bregmaCloseF = bregmaCloseFs[i];
								bregmaCloseF.checked = false;
								bregmaCloseF.disabled = true;
							}
						} else {
							for (var i = 0; i < bregmaCloseFs.length; i++) {
								var bregmaCloseF = bregmaCloseFs[i];
								bregmaCloseF.checked = false;
								bregmaCloseF.disabled = false;
							}
						}

						var yhhfyFs = document.getElementsByName("yhhfy");
						if (checkupStage == this.fiveCase) {
							for (var i = 0; i < yhhfyFs.length; i++) {
								var yhhfyF = yhhfyFs[i];
								yhhfyF.checked = false;
								yhhfyF.disabled = false;
							}
						} else {
							for (var i = 0; i < yhhfyFs.length; i++) {
								var yhhfyF = yhhfyFs[i];
								yhhfyF.checked = false;
								yhhfyF.disabled = true;
							}
						}
						var zrwtFs = document.getElementsByName("zrwt");
						if (checkupStage == this.fiveCase) {
							for (var i = 0; i < zrwtFs.length; i++) {
								var zrwtF = zrwtFs[i];
								zrwtF.checked = false;
								zrwtF.disabled = false;
							}
						} else {
							for (var i = 0; i < zrwtFs.length; i++) {
								var zrwtF = zrwtFs[i];
								zrwtF.checked = false;
								zrwtF.disabled = true;
							}
						}
						var wcdzFs = document.getElementsByName("wcdz");
						if (checkupStage == this.sevenCase) {
							for (var i = 0; i < wcdzFs.length; i++) {
								var wcdzF = wcdzFs[i];
								wcdzF.checked = false;
								wcdzF.disabled = false;
							}
						} else {
							for (var i = 0; i < wcdzFs.length; i++) {
								var wcdzF = wcdzFs[i];
								wcdzF.checked = false;
								wcdzF.disabled = true;
							}
						}
						var mfshFs = document.getElementsByName("mfsh");
						if (checkupStage == this.sevenCase) {
							for (var i = 0; i < mfshFs.length; i++) {
								var mfshF = mfshFs[i];
								mfshF.checked = false;
								mfshF.disabled = false;
							}
						} else {
							for (var i = 0; i < mfshFs.length; i++) {
								var mfshF = mfshFs[i];
								mfshF.checked = false;
								mfshF.disabled = true;
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
						chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoHtmlForm.superclass.loadData
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
						this.fireEvent("doNew", this.form)
						if (this.initDataId) {
							this.fireEvent("beforeUpdate", this); // **
							// 在数据加载之前做一些初始化操作
						} else {
							this.fireEvent("beforeCreate", this); // **
																	// 在页面新建时做一些初始化操作
						}
					},
					setFieldEnable : function() {
						var referral = this.getHtmlFldValue("referral");
						var guide = this.getHtmlFldValue("guide");
						var bregmaClose = this.getHtmlFldValue("bregmaClose");
						var referralUnitF = document
								.getElementById("referralUnit" + this.idPostfix);
						var referralReasonF = document
								.getElementById("referralReason"
										+ this.idPostfix);
						var otherGuideF = document.getElementById("otherGuide"
								+ this.idPostfix);
						var bregmaTransverseF = document
								.getElementById("bregmaTransverse"
										+ this.idPostfix);
						var bregmaLongitudinalF = document
								.getElementById("bregmaLongitudinal"
										+ this.idPostfix);
						if (referral == "y") {
							referralUnitF.disabled = false;
							referralReasonF.disabled = false;
						} else {
							referralUnitF.disabled = true;
							referralReasonF.disabled = true;
						}
						if (bregmaClose == "2") {
							bregmaTransverseF.disabled = false;
							bregmaLongitudinalF.disabled = false;
						} else {
							bregmaTransverseF.disabled = true;
							bregmaLongitudinalF.disabled = true;
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
