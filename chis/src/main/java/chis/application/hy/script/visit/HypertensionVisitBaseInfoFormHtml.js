$package("chis.application.hy.script.visit");

$import("chis.script.BizHtmlFormView",
		"chis.application.hy.script.visit.HypertensionVisitBaseInfoHtmlTemplate");
$styleSheet("chis.css.HypertensionVisitBaseInfo");

chis.application.hy.script.visit.HypertensionVisitBaseInfoFormHtml = function(cfg) {
	chis.application.hy.script.visit.HypertensionVisitBaseInfoFormHtml.superclass.constructor.apply(this, [cfg]);
	Ext.apply(this,chis.application.hy.script.visit.HypertensionVisitBaseInfoHtmlTemplate);
	this.initDataId = this.exContext.args.visitId;
	this.serviceId = "chis.hypertensionVisitService";
	this.serviceAction = "saveHypertensionVisit";
	this.createFields = ["referralReason","visitDate", "nextDate", "visitDoctor", "drugNames1",
			"drugNames2", "drugNames3", "drugNames4"];
	// this.otherDisable = [
	// {fld:"currentSymptoms",type:"checkbox",control:[{key:"10",exp:'eq',field:["otherSymptoms"]}]},
	// {fld:"medicineBadEffect",type:"radio",control:[{key:"y",exp:'eq',field:["medicineBadEffectText"]}]}
	// ]
	this.on("aboutToSave", this.onAboutToSave, this);
	this.on("beforeLoadData", this.onBeforeLoadData, this);
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("beforePrint", this.onBeforePrint, this);
};
var thishyPanel = null;
Ext.extend(chis.application.hy.script.visit.HypertensionVisitBaseInfoFormHtml,
		chis.script.BizHtmlFormView, {
			onLoadData : function() {
				this.resetControlOtherFld();
				this.fieldValidate(this.schema)
			},
			initData : function(initValues) {
				if (this.readOnly) {
					return;
				}
				if (initValues) {
					if (!this.data) {
						this.data = {};
					}
					for (var item in initValues) {
						var field = this.form.getForm().findField(item);
						if (field) {
							field.setValue(initValues[item]);
						} else {
							this.data[item] = initValues[item];
						}
					}
				}
				this.medicine = null;
				this.nextDateDisable = initValues['nextDateDisable'];
				this.planId = initValues["planId"];
				this.visitId = initValues["visitId"];
				this.empiId = initValues["empiId"];
				this.phrId = initValues["phrId"];
				this.planDate = initValues["planDate"];
				this.beginDate = Date.parseDate(initValues["beginDate"]|| this.mainApp.serverDate, "Y-m-d");
				this.endDate = Date.parseDate(initValues["endDate"]|| this.mainApp.serverDate, "Y-m-d");
				this.sn = initValues["sn"];
				this.planStatus = initValues['planStatus'];
				// 设置随访时间选择范围 客户要求随访日期没有限制
//				var nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
//				var visitDateObj = this.visitDate;
//				if (this.planDate) {
//					visitDateObj.setValue(this.planDate);// 默认值为计划时期
//				}
//				if (!this.beginDate || !this.endDate) {
//					return;
//				}
//				if (nowDate < this.beginDate || nowDate > this.endDate) {
//					visitDateObj.setMinValue(this.beginDate);
//					visitDateObj.setMaxValue(this.endDate);
//				} else if (nowDate >= this.beginDate && nowDate <= this.endDate) {
//					visitDateObj.setMinValue(this.beginDate);
//					visitDateObj.setMaxValue(nowDate);
//				}
				// 设置下次预约时间范围  客户要求随访日期没有限制
//				var nextDateObj = this.nextDate;
//				if (nowDate > this.endDate) {
//					nextDateObj.setMinValue(nowDate);
//				} else {
//					var nextMinDate = new Date(this.endDate.getFullYear(),
//							this.endDate.getMonth(), this.endDate.getDate() + 1);
//					nextDateObj.setMinValue(nextMinDate);
//				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.hypertensionVisitService",
					serviceAction : "visitInitialize",
					method : "execute",
					// cnd: ["eq", ["$", "empiId"], ["s", this.empiId]],
					body : {
						lifeStyleSchema : "chis.application.hr.schemas.EHR_LifeStyle",
						fixGroupSchema : "chis.application.hy.schemas.MDC_HypertensionFixGroup",
						empiSchema : "chis.application.mpi.schemas.MPI_DemographicInfo",
						empiId : this.empiId,
						phrId : this.phrId,
						lastEndDate : initValues["endDate"],
						lastBeginDate : initValues["beginDate"],
						planDate : this.planDate,
						planId : this.planId,
						occurDate : initValues["beginDate"],
						businessType : 1
					}
				});
				if (result.json.body) {
					var groupAlarm = result.json.body.groupAlarm;
					var nonArrivalDate = this.exContext.args.nonArrivalDate;
					if (groupAlarm == 2 && this.planStatus != "9" && !nonArrivalDate) {
						Ext.Msg.alert('提示', "下一次随访之前需要进行评估，请做好相关准备工作。");
					}
					var data = result.json.body.lifeStyle;
					debugger;
					if (data) {
						var smokeCount = data.smokeCount;
						var drinkCount = data.drinkCount;
						this.setValueById("smokeCount", smokeCount);
						this.setValueById("drinkCount", drinkCount);
					}
					data = result.json.body.minStepInfo;
					if (data) {
						this.nextRemindDate = data.nextRemindDate;
						this.nextPlanId = data.nextPlanId;
					} else {
						delete this.mextRemindDate;
						delete this.nextPlanId;
					}
					data = result.json.body.fixGroup;
					if (data) {
						this.manaUnitId = data.manaUnitId;
						this.height = data.height;
						this.setRadioValue("riskLevel", data.riskLevel || '');
						this.clearCheckBoxValues("riskiness");
						this.setCheckBoxValues("riskiness", data.riskiness|| '');
						onRiskinessClick(data.riskiness);
						this.clearCheckBoxValues("targetHurt");
						this.setCheckBoxValues("targetHurt", data.targetHurt|| '');
						this.clearCheckBoxValues("complication");
						this.setCheckBoxValues("complication",data.complication || '');
					} else {
						delete this.manaUnitId;
						delete this.height;
						delete this.riskLevel;
						delete this.targetHurt;
						delete this.complication;
					}
					this.age = result.json.body.age;
					this.sex = result.json.body.sex;
					this.planMode = result.json.body.planMode;
					data = result.json.body.lastVisit;
					if (data) {
						var targetWeight = data.targetWeight;
						var targetHeartRate = data.targetHeartRate;
						var targetSmokeCount = data.targetSmokeCount;
						var targetDrinkCount = data.targetDrinkCount;
						var targetTrainMinute = data.targetTrainMinute;
						var targetSalt = data.targetSalt || "";
						this.setValueById("targetWeight", targetWeight);
						var targetBmiDom = document.getElementById("targetBmi"+ this.idPostfix);
						onWeightChange(targetWeight, targetBmiDom);
						this.setValueById("targetHeartRate", targetHeartRate|| '');
						this.setValueById("targetSmokeCount", targetSmokeCount|| '');
						this.setValueById("targetDrinkCount", targetDrinkCount|| '');
						this.setValueById("targetTrainMinute",targetTrainMinute);
						this.setValueById("targetSalt", targetSalt);
						this.lastVisitId = data.visitId;
					} else {
						this.lastVisitId = "0000000000000000";
					}
					if (this.sex && this.height) {
						var targetWeight;
						if (this.sex == 1) {
							targetWeight = this.height - 105;
							this.setValueById("targetWeight", targetWeight
											|| '');
						} else if (this.sex == 2) {
							targetWeight = this.height - 110;
							this.setValueById("targetWeight", targetWeight
											|| '');
						}
						var targetBmiDom = document.getElementById("targetBmi"
								+ this.idPostfix);
						onWeightChange(targetWeight, targetBmiDom);
					}
					if ((this.sex == 1 && this.age > 55) || (this.sex == 2 && this.age > 65)) {
						this.setCheckBoxValues("riskiness", "1");
					}
				}
				// 控制下次预约时间
				if (!this.visitId) {
					this.visitDoctor.setValue({
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname
							});
				}
				this.setNextDate();
				this.onLoadData();
				var noVisitReasonDom = document.getElementById("noVisitReason"+ this.idPostfix);
				onVisitEffectChange(1, noVisitReasonDom);
				var otherSymptomsDom = document.getElementById("otherSymptoms"+ this.idPostfix);
				otherSymptomsDom.disabled = true;
			},
			onBeforeLoadData : function(entryName, initDataId) {
				this.phrId = this.exContext.args.phrId;
				this.empiId = this.exContext.args.empiId;
				this.visitId = this.exContext.args.visitId;
				this.planDate = this.exContext.args.planDate;
				this.planId = this.exContext.args.planId;
				this.sn = this.exContext.args.sn;
				return true;
			},
			loadData : function() {
				this.doNew();
				if (this.loading) {
					return;
				}
				if (!this.schema) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId)) {
					return;
				}
				if (!this.exContext.args.visitId) {
					return;
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				this.loading = true;
				this.age = 0;
				this.sex = 0;
				util.rmi.jsonRequest({
							serviceId : "chis.hypertensionVisitService",
							serviceAction : "getVisitInfo",
							method : "execute",
							body : {
								pkey : this.exContext.args.visitId,
								planId : this.exContext.args.planId,
								empiId : this.exContext.args.empiId
							}
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask();
							}
							this.loading = false;
							if (code > 300) {
								if (code == 504) {
									msg = "对应的随访信息未找到!";
								}
								this.processReturnMsg(code, msg, this.loadData);
								return;
							}
							var visitEffect = "";
							this.medicine = "";
							var currentSymptoms = "";
							var body = json.body;
							if (body) {
								var visitInfo = body.visitInfo
								if (visitInfo) {
									this.clearCheckBoxValues("riskiness");
									this.clearCheckBoxValues("targetHurt");
									this.clearCheckBoxValues("complication");
									this.initFormData(visitInfo);
									this.fireEvent("loadData", this.entryName,visitInfo);
									visitEffect = visitInfo.visitEffect;
									var noVisitReasonDom = document.getElementById("noVisitReason"+this.idPostfix);
									var visitWayDom = thishyPanel.getElementById("div_visitWay"+this.idPostfix);
									var medicineDom = thishyPanel.getElementById("div_medicine"+this.idPostfix);
									onVisitEffectChange(visitEffect.key,noVisitReasonDom, visitWayDom,
											medicineDom,visitInfo.noVisitReason);
									var medicineBadEffect = visitInfo.medicineBadEffect;
									if (medicineBadEffect) {
										var medicineBadEffectTextDom = document.getElementById("medicineBadEffectText"
												+ this.idPostfix);
										onMedicineBadEffectChange(medicineBadEffect.key,medicineBadEffectTextDom);
									}
									currentSymptoms = visitInfo.currentSymptoms;
									if (currentSymptoms) {
										var otherSymptomsDom = document.getElementById("otherSymptoms"+this.idPostfix);
										onCurrentSymptomsClick(null,
												otherSymptomsDom,
												currentSymptoms.key)
									}
									if (visitInfo.medicine) {
										this.medicine = visitInfo.medicine;
									}
									weight = visitInfo.weight;
									currentSymptoms = visitInfo.currentSymptoms;
									this.exContext.visitId = visitInfo.visitId;
									this.hypertensionGroup = visitInfo.hypertensionGroup;
									var medicineBadEffectTextDom = document
											.getElementById("medicineBadEffectText"+ this.idPostfix);
									if (visitInfo.medicine) {
										onMedicineClick(visitInfo.medicine.key,
												medicineBadEffectTextDom)
									} else {
										onMedicineClick(null,
												medicineBadEffectTextDom)
									}
									if (visitInfo.medicineIds) {
										this.medicineIds = visitInfo.medicineIds;
									} else {
										delete this.medicineIds;
									}
								}
								delete this.manaUnitId;
//								delete this.height;
//								this.height = body.height;随访表里没有height
								this.manaUnitId = body.manaUnitId;
								this.age = body.age;
								this.sex = body.sex;
								this.planMode = body.planMode;
								if (body.lastVisit) {
									this.lastVisitId = body.lastVisit.visitId;
								}
							}
							if (this.op == 'create') {
								this.op = "update";
							}
						}, this);//
				// 控制下次预约时间
				this.setNextDate();
			},
			setNextDate : function() {
				// 控制预约时间
				var nextDate = this.nextDate;
				// 按随访结果时 下次预约时间范围在下一计划的开始结果时间之间
				if (this.planMode == "1" && nextDate) {
                    //alert(this.exContext.args.visitId)
//					if (this.nextDateDisable || this.exContext.args.visitId) {
//					} else {
//						if (this.exContext.args.endDate < this.mainApp.serverDate) {
//							var p = Date.parseDate(this.mainApp.serverDate,
//									"Y-m-d");
//							var nextMinDate = new Date(p.getFullYear(), p
//											.getMonth(), p.getDate() + 1);
//							nextDate.setValue(this.exContext.args.nextDate);
//						} else {
//							var p = Date.parseDate(this.exContext.args.endDate,
//									"Y-m-d");
//							var nextMinDate = new Date(p.getFullYear(), p
//											.getMonth(), p.getDate() + 1);
//							alert(this.exContext.args.nextDate)
//							nextDate.setValue(this.exContext.args.nextDate);
//						}
//					}
					if (!(this.nextDateDisable || this.exContext.args.visitId)) {
						nextDate.setValue(this.exContext.args.nextDate);
					}
				}
				// 控制nextDate时间
				if (this.planMode == "2") {// 按下次预约随访时
					nextDate.allowBlank = false;
					nextDate["not-null"] = true;
				}
			},

			getFormData : function() {
				var ac = util.Accredit;
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				var visitEffect = this.getRadioValue("visitEffect");
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						if (this.isCreateField(it.id)) {
							v = eval("this." + it.id + ".getValue()");
							var xtype = eval("this." + it.id + ".getXType()");
							if (xtype == "treeField") {
								var rawVal = eval("this."+it.id+".getRawValue()");
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (xtype == "datefield" && v != null && v != "") {
								v = v.format('Y-m-d');
							}
						} else {// 不是EXT创建的控件获取值
							if (it.tag == "text") {
								v = this.getValueById(it.id);
							} else if (it.tag == "radioGroup") {
								v = this.getRadioValue(it.id);
							} else if (it.tag == "checkBox") {
								v = this.getCheckBoxValues(it.id);
							} else if (it.tag == "selectgroup") {
								v = this.getSelectValues();
							} else if (it.id == "drugNames1" || it.id == "drugNames2" || it.id == "drugNames3"
									|| it.id == "drugNames4" || it.id == "visitDoctor") {
								v = eval("this." + it.id + ".getValue()");
							}
						}
						if (v) {
							values[it.id] = v;
						} else {
							values[it.id] = "";
						}
					}
				}
				return values;
			},
			// text类型的获取值
			getValueById : function(id) {
				var dom = document.getElementById(id + this.idPostfix);
				if (dom.value == dom.defaultValue) {
					return;
				}
				return dom.value
			},
			// radio类型的获取值
			getRadioValue : function(id) {
				var v = document.getElementsByName(id + this.idPostfix);
				var le = v.length;
				for (var i = 0; i < le; i++) {
					if (v[i].checked) {
						return v[i].value;
					}
				}
				return 0;
			},
			// checkBox类型的获取值
			getCheckBoxValues : function(id) {
				var v = document.getElementsByName(id + this.idPostfix);
				var le = v.length;
				var value = new Array();
				for (var i = 0; i < le; i++) {
					if (v[i].checked) {
						value.push(v[i].value);
					}
				}
				return value.toString();
			},
			// 取下拉框被选中的值,传入select的id
			getSelect : function(id) {
				var obj = document.getElementById(id + this.idPostfix); // selectid
				var index = obj.selectedIndex; // 选中索引
				var year = obj.options[index].value; // 选中值
				return year;
			},
			// text类型的赋值
			setValueById : function(id, v) {
				var dom = document.getElementById(id + this.idPostfix);
				if (!dom) {
					return;
				}
				if (v != null && (v != "" || v == "0")) {
					dom.value = v;
					dom.style.color = "#000";
				} else if (dom.defaultValue) {
					dom.value = dom.defaultValue
					dom.style.color = "#999";
				} else {
					dom.value = dom.defaultValue;
				}
			},
			// radio类型的赋值
			setRadioValue : function(id, v) {
				var dom = document.getElementsByName(id + this.idPostfix);
				if (!dom) {
					return;
				}
				var le = dom.length;
				for (var i = 0; i < le; i++) {
					if (v != null && dom[i].value == v.key) {
						dom[i].checked = true;
						break;
					}
				}
			},
			// 给下拉框赋值
			setSelectValue : function(id, v) {
				var dom = document.getElementById(id + this.idPostfix);
				if (!dom) {
					return;
				}
				var le = dom.options.length;
				for (var i = 0; i < le; i++) {
					if (dom.options[i].value == v) {
						dom.options[i].selected = true;
						break;
					}
				}
			},
			// checkBox类型的赋值
			setCheckBoxValues : function(id, v) {
				var dom = document.getElementsByName(id + this.idPostfix);
				if (!dom) {
					return;
				}
				var k = dom.length;
				// 下面代码是后台传值list或者string, 二选一 确定后台传值类型后 将另一个判断删掉
				if (typeof v == "object" && v.key) {// 如果值是list类型
					v = v.key;
				}
				var value = [];
				if (v.indexOf(",") > -1) {
					value = v.split(",");
				} else {
					value[0] = v;
				}
				var l = value.length;
				for (var i = 0; i < l; i++) {
					for (var j = 0; j < k; j++) {
						if (value[i] == dom[j].value) {
							dom[j].checked = true;
							break;
						}
					}
				}
			},
			setMedicineDisabled : function(flag) {
				var cflds = ["drugNames1", "drugNames2", "drugNames3","drugNames4"]
				for (var i = 1; i < 5; i++) {
					if (flag) {
						document.getElementById("everyDayTime"+i+ this.idPostfix).value = "";
						document.getElementById("oneDosage"+i+this.idPostfix).value = "";
						document.getElementById("medicineUnit"+i+this.idPostfix).value = "";
						eval("this.drugNames" + i + ".setValue()")
						eval("this.drugNames" + i + ".disable()")
					} else {
						eval("this.drugNames" + i + ".enable()")
					}
					var f1 = document.getElementById("everyDayTime"+i+this.idPostfix);
					f1.disabled = flag;
					this.removeClass(f1, "x-form-invalid");
					var f2 = document.getElementById("oneDosage" + i
							+ this.idPostfix);
					f2.disabled = flag;
					this.removeClass(f2, "x-form-invalid");
				}
			},
			setCheckBoxDisabled : function(id, flag) {
				var dom = document.getElementsByName(id + this.idPostfix);
				var k = dom.length;
				for (var i = 0; i < k; i++) {
					if (flag) {
						dom[i].checked = false;
					}
					dom[i].disabled = flag;
				}
			},
			setRadioDisabled : function(id, flag) {
				var dom = document.getElementsByName(id + this.idPostfix);
				var le = dom.length;
				for (var i = 0; i < le; i++) {
					dom[i].disabled = flag;
				}
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (this.isCreateField(it.id)) {
						var v = data[it.id]
						var f = eval("this." + it.id);
						// flag==true 表示要清空数据
						if (v != undefined || this.flag == true) {
							if (it.dic && v !== "" && v === 0) {// add by
								// yangl
								// 解决字典类型值为0(int)时无法设置的BUG
								v = "0";
							}
							// 判断点击“保存旁边的新建的时候，不要清空管辖机构的数据”
							if (this.flag == true && it.id == "manaUnitId") {
							} else {
								if (it.id == "drugNames1" || it.id == "drugNames2" || it.id == "drugNames3"
									|| it.id == "drugNames4") {
									eval("this." + it.id + ".selectData.YPMC=v");
								}
								f.setValue(v)
							}
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
								// this.setValueAgain(f, v, it);
							}
						}
						if (it.update == "false" && this.initDataId) {
							f.disable();
						}
					} else {
						var v = data[it.id]
						if (v != undefined || this.flag == true) {
							if (undefined == v) {
								v = ""
							}
							if (it.tag == "text") {
								this.setValueById(it.id, v)
							} else if (it.tag == "radioGroup") {
								if (this.flag == true) {
									// 清空
									var vv = document.getElementsByName(it.id + this.idPostfix);
									var le = vv.length;
									for (var j = 0; j < le; j++) {
										if (vv[j].checked) {
											vv[j].checked = false;
											break;
										}
									}
								} else {
									this.setRadioValue(it.id, v)
								}
							} else if (it.tag == "checkBox") {
								if (this.flag == true) {
									// 清空
									this.clearCheckBoxValues(it.id);
								} else {
									this.setCheckBoxValues(it.id, v)
								}
							} else if (it.tag == "selectgroup") {
								if (this.flag == true) {
									// 单选框
									var v2 = document.getElementsByName("educationCode"+this.idPostfix);
									var le2 = v2.length;
									for (var j2 = 0; j2 < le2; j2++) {
										if (v2[j2].checked) {
											v2[j2].checked = false;
										}
									}
								} else {
									this.setSelectValues(it.id, v)
								}
							}
						}
					}
					this.setKeyReadOnly(true)
				}
				this.flag = false;
			},
			clearCheckBoxValues : function(id) {
				var vvv = document.getElementsByName(id + this.idPostfix);
				var lee = vvv.length;
				for (var j1 = 0; j1 < lee; j1++) {
					if (vvv[j1].checked) {
						vvv[j1].checked = false;
					}
				}
			},
			onAboutToSave : function(entryName, op, saveData) {
				saveData["empiId"] = this.empiId;
				saveData["phrId"] = this.phrId;
				saveData["lastVisitId"] = this.lastVisitId;
				saveData["nextPlanId"] = this.nextPlanId;
				saveData["manaUnitId"] = this.manaUnitId;
				saveData["height"] = this.height;
				saveData["planDate"] = this.planDate;
				saveData["planId"] = this.planId;
				saveData["endDate"] = this.endDate;
				saveData["beginDate"] = this.beginDate;
				saveData["fixGroupDate"] = this.exContext.args.fixGroupDate
				saveData["sn"] = this.sn;
				if (this.planMode == "2" && saveData["visitEffect"] != "9") {
					var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
					if (!this.nextDateDisable && saveData["nextDate"] <= now) {
						MyMessageTip.msg("提示", "预约日期必须大于当前日期", true);
						return false;
					}
				}
				if (this.hypertensionGroup) {
					saveData["hypertensionGroup"] = this.hypertensionGroup.key;
				}
				var constrictionF = document.getElementById("constriction"+this.idPostfix);
				var constriction = constrictionF.value;
				var diastolicF = document.getElementById("diastolic"+this.idPostfix);
				var diastolic = diastolicF.value;
				if (constriction && diastolic && parseInt(constriction) <= parseInt(diastolic)) {
					this.addClass(constrictionF, "x-form-invalid");
					this.addClass(diastolicF, "x-form-invalid");
					MyMessageTip.msg("提示", "收缩压应大于舒张压", true);
					return false;
				}
				if (saveData["visitEffect"] && saveData["visitEffect"] != 1 && !saveData["noVisitReason"]) {
					document.getElementsByName("noVisitReason" + this.idPostfix)[0].focus();
					document.getElementsByName("noVisitReason" + this.idPostfix)[0].select();
					MyMessageTip.msg("提示", "(暂时失访/终止管理)原因不能为空", true);
					return false;
				}
//				if (saveData["medicine"] && saveData["medicine"] != 1
//						&& !saveData["medicineNot"]) {
//					document.getElementsByName("medicineNot" + this.idPostfix)[0]
//							.focus();
//					document.getElementsByName("medicineNot" + this.idPostfix)[0]
//							.select();
//					MyMessageTip.msg("提示", "不规律服药原因不能为空", true);
//					return false;
//				}
//				if (saveData["medicineNot"] && saveData["medicineNot"] == 99
//						&& !saveData["medicineOtherNot"]) {
//					document.getElementById("medicineOtherNot" + this.idPostfix).focus();
//					document
//							.getElementById("medicineOtherNot" + this.idPostfix)
//							.select();
//					MyMessageTip.msg("提示", "其他不规律服药原因不能为空", true);
//					return false;
//				}
				if (this.mainApp.exContext.hypertensionMode == 2 && !this.nextDate.getValue()) {
					MyMessageTip.msg("提示", "下次随访日期不能为空。", true);
					return false;
				}
				if(!saveData["visitDoctor"] || saveData["visitDoctor"] ==null){
				   MyMessageTip.msg("提示", "随访医生不能为空。", true);
				   document.getElementById("div_visitDoctor" + this.idPostfix).focus();
				   return false;
				}
				//yx客户要求健康处方建议、危险因素、并发症、靶器官损害、原并发症加重、危险分层不要，为不影响后台赋默认值
//				alert("健康处方建议:"+saveData["healthProposal"]+"危险因素:"+saveData["riskiness"]+
//				"并发症:"+saveData["complication"]+"靶器官损害:"+saveData["targetHurt"]+
//				"原并发症加重:"+saveData["complicationIncrease"]+"危险分层:"+saveData["riskLevel"]);
				saveData["healthProposal"]='';
				saveData["complication"]='';
				saveData["complicationIncrease"]='';
				saveData["riskiness"]='';
				saveData["targetHurt"]='';
				saveData["riskLevel"]='1';
				if (saveData["visitEffect"] != 2
						&& saveData["visitEffect"] != 9) {
					return this.htmlFormSaveValidate(this.schema);
				}
			},
			htmlFormSaveValidate : function(schema) {
				if (!schema) {
					schema = this.schema;
				}
				var validatePass = true;
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (this.isCreateField(it.id)) {
						var isLawful = true;
						isLawful = eval("this." + it.id + ".validate()");
						if (!isLawful) {
							validatePass = false;
							eval("this." + it.id + ".focus(true,200)");
							MyMessageTip.msg("提示", it.alias + "为必填项", true);
							this.createFldSaveValidate(it.id);
							break;
						}
						if (validatePass == false) {
							break;
						}
					} else {
						if (it.dic) {
							if (it['not-null'] == "1" || it['not-null'] == "true") {
								var dfv = this.getHtmlFldValue(it.id);
								var divId = "div_" + it.id + this.idPostfix;
								var div = document.getElementById(divId);
								if (div) {
									if (dfv && dfv.length > 0) {
										this.removeClass(div, "x-form-invalid");
										div.title = "";
									} else {
										this.addClass(div, "x-form-invalid");
										div.title = it.alias + "为必填项";
										validatePass = false;
										document.getElementsByName(it.id + this.idPostfix)[0].focus();
										MyMessageTip.msg("提示", div.title, true);
										break;
									}
								}
							}
						} else {
							var fld = document.getElementById(it.id + this.idPostfix);
							if (!fld) {
								continue;
							}
							if ((it['not-null'] == "1" || it['not-null'] == "true") && !it["pkey"]) {// 跳过主键必填验证
								if (fld.value == "" || (fld.value == fld.defaultValue && !fld.hidden)) {
									this.addClass(fld, "x-form-invalid");
									fld.title = it.alias + "为必填项";
									validatePass = false;
									if (!document.getElementById(it.id + this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id + this.idPostfix).focus();
									document.getElementById(it.id + this.idPostfix).select();
									//必填项
									MyMessageTip.msg("提示", fld.title, true);
									break;
								} else {
									this.removeClass(fld, "x-form-invalid");
									fld.title = it.alias
								}
							}
							var obj = fld;
							switch (it.type) {
								case "string" :
									var maxLength = it.length;
									var fv = fld.value;
									var fvLen = this.getStrSize(fv);
									if (fvLen > maxLength) {
										this.addClass(obj, "x-form-invalid")
										obj.title = it.alias + "中输入的字符串超出定义的最大长度（" + maxLength + "）";
										validatePass = false;
										if (!document.getElementById(it.id + this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id + this.idPostfix).focus();
										document.getElementById(it.id + this.idPostfix).select();
										MyMessageTip.msg("提示", obj.title, true);
									} else {
										this.removeClass(obj, "x-form-invalid");
										obj.title = it.alias
									}
									break;
								case 'int' :
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var fv = obj.value;
									if (fv == obj.defaultValue) {// 跳过注释文字验证
										continue;
									}
									var reg = new RegExp("^[0-9]*$");
									var fid = it.id;
									if (!reg.test(fv)) {
										this.addClass(obj, "x-form-invalid")
										obj.title = it.alias + "中输入了非整数 数字或字符"
										validatePass = false;
										if (!document.getElementById(it.id + this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id + this.idPostfix).focus();
										document.getElementById(it.id + this.idPostfix).select();
										MyMessageTip.msg("提示", obj.title, true);
										continue;
									} else {
										this.removeClass(obj, "x-form-invalid");
										obj.title = it.alias
									}
									if (typeof(minValue) != 'undefined') {
										if (parseInt(fv) < minValue) {
											this.addClass(obj,"x-form-invalid")
											obj.title = it.alias+ "中输入的值小于了定义的最小值（"+ minValue + "）";
											validatePass = false;
											if (!document.getElementById(it.id+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id+ this.idPostfix).focus();
											document.getElementById(it.id+ this.idPostfix).select();
											MyMessageTip.msg("提示", obj.title, true);
											continue;
										} else {
											this.removeClass(obj, "x-form-invalid");
											obj.title = it.alias
										}
									}
									if (typeof(maxValue) != 'undefined') {
										if (parseInt(fv) > maxValue) {
											this.addClass(obj,"x-form-invalid")
											obj.title = it.alias+ "中输入的值大于了定义的最大值（"+ maxValue + "）";
											validatePass = false;
											if (!document.getElementById(it.id+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id + this.idPostfix).focus();
											document.getElementById(it.id + this.idPostfix).select();
											MyMessageTip.msg("提示", obj.title, true);
											continue;
										} else {
											this.removeClass(obj, "x-form-invalid");
											obj.title = it.alias
										}
									}
									break;
								case "double" :
									var length = it.length;
									var precision = it.precision;
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var dd = 0;
									if (typeof(precision) != 'undefined') {
										dd = parseInt(precision);
									}
									var iNum = length - dd;
									var regStr = "(^[0-9]{0," + iNum + "}$)|(^[0-9]{0," + iNum
											+ "}(\\.[0-9]{0," + dd + "})?$)";
									if (dd == 0) {
										regStr = "(^[0-9]{0," + iNum + "}$)|(^[[0-9]*\\.[0-9]*]{0,"
												+ iNum + "}$)";
									}
									var reg = new RegExp(regStr);
									var fv = obj.value;
									if (fv == obj.defaultValue) {// 跳过注释文字验证
										continue;
									}
									if (!reg.test(fv)) {
										this.addClass(obj, "x-form-invalid")
										obj.title = it.alias + "中输入了非浮点型数据或字符"
										validatePass = false;
										if (!document.getElementById(it.id + this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id + this.idPostfix).focus();
										document.getElementById(it.id + this.idPostfix).select();
										MyMessageTip.msg("提示", obj.title, true);
										continue;
									} else {
										this.removeClass(obj, "x-form-invalid");
										obj.title = it.alias
									}
									if (typeof(minValue) != 'undefined') {
										if (parseInt(fv) < minValue) {
											this.addClass(obj,"x-form-invalid");
											obj.title = it.alias + "中输入的值小于了定义的最小值（" + minValue + "）"
											validatePass = false;
											if (!document.getElementById(it.id + this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id + this.idPostfix).focus();
											document.getElementById(it.id + this.idPostfix).select();
											MyMessageTip.msg("提示", obj.title, true);
											continue;
										} else {
											this.removeClass(obj, "x-form-invalid");
											obj.title = it.alias
										}
									}
									if (typeof(maxValue) != 'undefined') {
										if (parseInt(fv) > maxValue) {
											this.addClass(obj,"x-form-invalid");
											obj.title = it.alias
													+ "中输入的值大于了定义的最大值（"
													+ maxValue + "）"
											validatePass = false;
											if (!document.getElementById(it.id
													+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id + this.idPostfix).focus();
											document.getElementById(it.id + this.idPostfix).select();
											MyMessageTip.msg("提示", obj.title, true);
											continue;
										} else {
											this.removeClass(obj, "x-form-invalid");
											obj.title = it.alias
										}
									}
									break;
							}
							if (validatePass == false) {
								break;
							}
						}
					}
				}
				return validatePass;
			},
			saveToServer : function(saveData) {
				if (this.medicine
						&& (this.medicine.key == "1" || this.medicine.key == "2")
						&& (saveData.medicine == "3" || saveData.medicine == "4")) {
					Ext.Msg.show({
								title : '提示',
								msg : "当前操作会引起服药数据删除,是否继续?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "no") {
										return;
									}
									saveData.deleteMedicine = true;
									this.executeSaveAction(saveData);
								},
								scope : this
							});
				} else {
					this.executeSaveAction(saveData);
				}
			},

			executeSaveAction : function(saveData) {
				saveData.birthday=this.exContext.empiData.birthday;
				if (this.fireEvent("aboutToSave", this.entryName, this.op,
						saveData, this) == false) {
					this.form.el.unmask();
					return;
				}
				if (this.hypertensionGroup && this.hypertensionGroup.key) {
					saveData["hypertensionGroup"] = this.hypertensionGroup.key;
				}
				if (this.medicineIds) {
					saveData["medicineIds"] = this.medicineIds;
				}
				saveData.planId = this.planId;
				this.fireEvent("saveToServer", this.op, saveData, this);
			},

			visitIdChange : function(visitId) {
				this.exContext.visitId = visitId;
				this.visitId = visitId;
			},

			doImport : function() {
				var module = this.midiModules["HyperClinicImportList"];
				var list = this.list;
				var cfg = {};
				cfg.empiId = this.exContext.args.empiId;
				if (!module) {
					var cls = "chis.application.hy.script.visit.HypertensionClinicList";
					$require(cls, [function() {
										var m = eval("new " + cls + "(cfg)");
										m.on("import", this.onImport, this);
										this.midiModules["HyperClinicImportList"] = m;
										list = m.initPanel();
										list.border = false;
										list.frame = false;
										this.list = list;

										var win = m.getWin();
										win.add(list);
										win.show();
									}, this]);
				} else {
					Ext.apply(module, cfg);
					module.requestData.cnd = ['eq', ['$', 'empiId'],
							['s', this.exContext.args.empiId]];
					module.loadData();
					var win = module.getWin();
					win.add(list);
					win.show();
				}
			},

			onImport : function(record) {
				if (!record) {
					return;
				}
				var height = document.getElementById("height" + this.idPostfix).value = record
						.get("height");
				this.height = record.get("height");
				var weightF = document
						.getElementById("weight" + this.idPostfix).value = record
						.get("weight");
				var weightF = document
						.getElementById("weight" + this.idPostfix)
				onWeightChange(record.get("weight"), document
								.getElementById("bmi" + this.idPostfix));
				var constriction = document.getElementById("constriction"
						+ this.idPostfix).value = record.get("constriction");
				var diastolic = document.getElementById("diastolic"
						+ this.idPostfix).value = record.get("diastolic");
			},

			addFieldAfterRender : function() {
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 310,
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
				this.nextDate = new Ext.form.DateField({
							name : 'nextDate' + this.idPostfix,
							width : 310,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '下次随访日期',
							//minValue : curDate,
							// allowBlank:false,
							// invalidText : "必填字段",
							fieldLabel : "下次随访日期",
							renderTo : Ext.get("div_nextDate" + this.idPostfix)
						});
				this.visitDoctor = this.createDicField({
							"width" : 310,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user",
							"render" : "Tree",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true,
							"parentKey" : "%user.manageUnit.id",
							"defaultValue" : {
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname
							}
						});
				this.visitDoctor.render(Ext.get("div_visitDoctor"
						+ this.idPostfix));
				this.referralReason = this.createDicField({
							"width" : 310,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.reason",
							"render" : "LovCombo",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true,
							//"parentKey" : "%user.manageUnit.id",
							"defaultValue" : {
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname
							}
						});
				this.referralReason.render(Ext.get("div_referralReason"
						+ this.idPostfix));
				var cflds = ["drugNames1", "drugNames2", "drugNames3",
						"drugNames4"]
				var me = this;
				for (var i = 0, len = cflds.length; i < len; i++) {
					var fldId = cflds[i];
					this[fldId] = this.createLocalDicField({
								width : 420,
								id : 'drugNames' + i + this.idPostfix,
								afterSelect : function(t, record) {
									var id = t.container.id;
									var n = id.replace("div_drugNames", "")
											.replace(me.idPostfix, "");
									var medicineUnit = document
											.getElementById('medicineUnit' + n
													+ me.idPostfix);
									medicineUnit.value = record.data.JLDW;
									t.setValue(record.data.YPMC)
									var oneDosage = document
											.getElementById('oneDosage' + n
													+ me.idPostfix);
									oneDosage.value = record.data.YPJL;
								},
								afterClear : function(t) {
									var id = t.container.id;
									var n = id.replace("div_drugNames", "")
											.replace(me.idPostfix, "");
									var medicineUnit = document
											.getElementById('medicineUnit' + n
													+ me.idPostfix);
									medicineUnit.value = '';
									var oneDosage = document
											.getElementById('oneDosage' + n
													+ me.idPostfix);
									oneDosage.value = '';
									var everyDayTime = document
											.getElementById('everyDayTime' + n
													+ me.idPostfix);
									everyDayTime.value = '';
								}
							});
					// this[fldId].on("select", this.onDrugNamesSelect, this);
					// this[fldId].on("blur", this.onDrugNamesSelect, this)
					this[fldId]
							.render(Ext.get("div_" + fldId + this.idPostfix));
				}
				thishyPanel = this;

				var serverDate = this.mainApp.serverDate;
				this.visitDate.maxValue = Date.parseDate(serverDate, "Y-m-d");
				//this.nextDate.minValue = Date.parseDate(serverDate, "Y-m-d");
			},

			onDrugNamesSelect : function(f) {
				var v = f.getValue();
				var id = f.container.dom.id;
				var i = id.replace('drugNames', '').replace(
						'' + this.idPostfix, '');
				var f1 = document.getElementById('everyDayTime' + i + ''
						+ this.idPostfix);
				var f2 = document.getElementById('oneDosage' + i + ''
						+ this.idPostfix);
				if (v) {
					var v1 = this.getValueById('everyDayTime' + i);
					if (v1 && v > 0) {
						this.removeClass(f1, "x-form-invalid");
					} else {
						this.addClass(f1, "x-form-invalid");
					}
					var v2 = this.getValueById('oneDosage' + i);
					if (v2 && v > 0) {
						this.removeClass(f2, "x-form-invalid");
					} else {
						this.addClass(f2, "x-form-invalid");
					}
				} else {
					this.setValueById('everyDayTime' + i, "");
					this.removeClass(f1, "x-form-invalid");
					this.setValueById('oneDosage' + i, "");
					this.removeClass(f2, "x-form-invalid");
				}
			},

			validate : function() {
			},

			setBtnable : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns.item(0)) {
					return;
				}
				var rdStatus = this.exContext.ids.recordStatus;
				if (rdStatus && rdStatus == '1') {
					for (var i = 0; i < btns.getCount(); i++) {
						var btn = btns.item(i);
						if (btn) {
							btn.disable();
						}
					}
				}
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
								var fs = document.getElementsByName(it.id
										+ this.idPostfix);
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
				this.initData(this.exContext.args);
				this.fireEvent("doNew", this.form)
			},

			getHtmlFldValue : function(fldName) {
				var fldValue = "";
				var flds = document.getElementsByName(fldName + this.idPostfix);
				var vs = [];
				for (var i = 0, len = flds.length; i < len; i++) {
					var f = flds[i];
					if (f.type == "text" || f.type == "hidden") {
						vs.push(f.value || '');
					}
					if (f.type == "radio" || f.type == "checkbox") {
						if (f.checked) {
							vs.push(f.value);
						}
					}
				}
				fldValue = vs.join(',');
				return fldValue;
			},

			addFieldDataValidateFun : function(schema) {
				if (!schema) {
					schema = this.schema;
				}
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (!this.isCreateField(it.id)) {
						if (it.dic) {
							var notNull = false;
							if (it['not-null'] == "1"
									|| it['not-null'] == "true") {
								var dfs = document.getElementsByName(it.id
										+ this.idPostfix);
								if (!dfs) {
									continue;
								}
								notNull = true;
								var fv = this.getHtmlFldValue(it.id);
								var divId = "div_" + it.id + this.idPostfix;
								var fdiv = document.getElementById(divId);
								if (fv && fv.length > 0) {
									if (fdiv) {
										this
												.removeClass(fdiv,
														"x-form-invalid");
										fdiv.title = "";
									}
								} else {
									if (fdiv) {
										this.addClass(fdiv, "x-form-invalid");
										fdiv.title = it.alias + "为必选项!"
									}
								}
								var me = this;
								for (var di = 0, dlen = dfs.length; di < dlen; di++) {
									var itemFld = dfs[di];
									var handleFun = function(fldId, alias, obj,
											me) {
										return function() {
											me.dicFldValidateClick(fldId,
													alias, obj, me);
										}
									}
									this.addEvent(itemFld, "click", handleFun(
													it.id, it.alias, itemFld,
													me));
								}
							}
						} else {
							var fld = document.getElementById(it.id
									+ this.idPostfix);
							if (!fld) {
								continue;
							}
							var notNull = false;
							if (it['not-null'] == "1"
									|| it['not-null'] == "true") {
								notNull = true;
								if (fld.value == ""
										|| fld.value == fld.defaultValue) {
									this.addClass(fld, "x-form-invalid");
								} else {
									this.removeClass(fld, "x-form-invalid");
								}
							}
							var me = this;
							switch (it.type) {
								case "string" :
									var maxLength = it.length;
									var handleFun = function(maxLength,
											notNull, alias, obj, me) {
										return function() {
											me.validateString(maxLength,
													notNull, alias, obj, me);
										}
									}
									this.addEvent(fld, "change", handleFun(
													maxLength, notNull,
													it.alias, fld, me));
									break;
								case 'int' :
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var length = it.length;
									var handleFun = function(length, minValue,
											maxValue, notNull, fid, alias, obj,
											me) {
										return function() {
											me.validateInt(length, minValue,
													maxValue, notNull, fid,
													alias, obj, me);
										}
									}
									this.addEvent(fld, "change", handleFun(
													length, minValue, maxValue,
													notNull, it.id, it.alias,
													fld, me));
									break;
								case "double" :
									var length = it.length;
									var precision = it.precision;
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var handleFun = function(length, precision,
											minValue, maxValue, notNull, alias,
											obj, me) {
										return function() {
											me.validateDouble(length,
													precision, minValue,
													maxValue, notNull, alias,
													obj, me);
										}
									}
									this
											.addEvent(fld, "change", handleFun(
															length, precision,
															minValue, maxValue,
															notNull, it.alias,
															fld, me));
									break;
							}
						}
					}
				}
			},

			BPControl : function(fid, op, me) {// constriction diastolic
				return;
				var relative = "";
				if (fid == "constriction") {
					relative = "diastolic";
				} else {
					relative = "constriction";
				}
				var relObj = document.getElementById(relative + me.idPostfix);
				var rv = relObj.value;
				if (rv && rv != "") {
					var LBPid = fid + "_L" + me.idPostfix;
					var lo = document.getElementById(LBPid);
					var relo = document.getElementById(relative + "_L"
							+ me.idPostfix);
					if (op == "add") {
						me.addClass(ro, "x-form-invalid");
						me.addClass(relo, "x-form-invalid");
					} else {
						me.removeClass(lo, "x-form-invalid");
						me.removeClass(relo, "x-form-invalid");
					}
				} else {
					me.addClass(relObj, "x-form-invalid");
				}
			},

			initHTMLFormData : function(data, schema) {
				if (!schema) {
					schema = this.schema;
				}
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				// Ext.apply(this.data, data)
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (it.pkey) {
						this.initDataId = data[it.id];
					}
					if (this.isCreateField(it.id)) {
						var cfv = data[it.id]
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
						eval("this." + it.id + ".setValue(cfv);this." + it.id
								+ ".validate();");
					} else {
						if (it.dic) {
							if (!this.fireEvent("dicFldSetValue", it.id, data)) {
								continue;
							} else {
								var dfs = document.getElementsByName(it.id
										+ this.idPostfix);
								if (!dfs) {
									continue;
								}
								var dicFV = data[it.id];
								var fv = "";
								if (dicFV) {
									fv = dicFV.key;
								}
								if (!fv) {// yubo
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
							}
						} else {
							var f = document.getElementById(it.id
									+ this.idPostfix)
							if (f) {
								var v = data[it.id];
								if (!v) {
									v = f.defaultValue || "";
									if (f.defaultValue) {
										f.style.color = "#999";
									}
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
				this.setKeyReadOnly(true)
				// this.startValues = form.getValues(true);
				this.resetButtons(); // ** 用于页面按钮权限控制
				// this.focusFieldAfter(-1, 800);
			},
			getElementById : function(id) {
				return document.getElementById(id);
			},
			onBeforePrint : function(type, pages, ids_str) {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先保存记录。");
					return false;
				}
				pages.value = "chis.prints.htmlTemplate.hypertensionVisit";
				ids_str.value = "&phrId=" + this.exContext.ids.phrId
						+ "&empiId=" + this.exContext.ids.empiId;
			},
			doDeletevisit:function(){
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionVisitService",
							serviceAction : "deleteVisitPlan",
							method : "execute",
							planId : this.planId
					});
				if(result.json.deletecount==0){
					Ext.Msg.alert("提示", "本记录已经随访，不能删除！");
				}
				this.fireEvent("visitPlanDelete");
			}
		});

function onTextMustBlur(value, text) {
	if (!value || value == "") {
		thishyPanel.addClass(text, "x-form-invalid");
	} else {
		thishyPanel.removeClass(text, "x-form-invalid");
	}
}
function onVisitEffectChange(v, f, f1, f2, value) {
	var div = document.getElementById("div_noVisitReason" + thishyPanel.idPostfix);
	var divValue = thishyPanel.getHtmlFldValue("noVisitReason");
	if (v != 1) {
		if (divValue && divValue != "") {
			thishyPanel.removeClass(div, "x-form-invalid");
		} else {
			thishyPanel.removeClass(f1, "x-form-invalid");
			thishyPanel.removeClass(f2, "x-form-invalid");
			thishyPanel.addClass(div, "x-form-invalid");
		}
	} else {
		//清空原因
		document.getElementById("noVisitReason_1" + thishyPanel.idPostfix).checked=false;
		document.getElementById("noVisitReason_2" + thishyPanel.idPostfix).checked=false;
		document.getElementById("noVisitReason_3" + thishyPanel.idPostfix).checked=false;
		document.getElementById("noVisitReason_4" + thishyPanel.idPostfix).checked=false;
		thishyPanel.removeClass(div, "x-form-invalid");
		thishyPanel.fieldValidate();
	}
	var toRedObjects = getObjsByClass("classinput");
	for (var k in toRedObjects) {
		var obj = toRedObjects[k];
		if (!obj) {
			continue;
		}
		if (v != 1) {
			if (!obj.style) {
				continue;
			}
			obj.style.color = "black";
		} else {
			if (!obj.style) {
				continue;
			}
			obj.style.color = "red";
		}
	}
	var ZGYY = document.getElementById("ZGYY" + thishyPanel.idPostfix);
	if (v != 1) {
		ZGYY.style.color = "red";
	} else {
		ZGYY.style.color = "black";
	}
	thishyPanel.fireEvent("visitEffect", v)
}

function onVisitReasonChange(v, f) {
	var div = document
			.getElementById("div_noVisitReason" + thishyPanel.idPostfix);
	if (!v) {
		thishyPanel.addClass(div, "x-form-invalid");
	} else {
		thishyPanel.removeClass(div, "x-form-invalid");
	}
}

function onMedicineBadEffectChange(v, f) {
	if (v == "y") {
		f.style.color = "#000";
		f.disabled = false;
	} else {
		f.value = "";
		f.disabled = true;
	}
}

function onRiskinessClick(v) {
	var id = "riskiness";
	value = thishyPanel.getCheckBoxValues(id);
	if (value.indexOf(v) == -1) {
		return;
	}
	if (v == "12") {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, "12")
	} else if (value.indexOf("12") != -1) {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, v)
	}

}

function onCurrentSymptomsClick(v, f, value) {
	if (!value) {
		var id = "currentSymptoms";
		value = thishyPanel.getCheckBoxValues(id);
		if (value.indexOf(v) == -1) {
			if (f) {
				if (value.indexOf("10") != -1) {
					f.style.color = "#000";
					f.disabled = false;
				} else {
					f.value = "";
					f.disabled = true;
				}
			}
			return;
		}
		if (v == "1") {
			thishyPanel.clearCheckBoxValues(id);
			thishyPanel.setCheckBoxValues(id, "1")
			f.value = "";
			f.disabled = true;
			return;
		} else if (value.indexOf("1,") != -1) {
			thishyPanel.clearCheckBoxValues(id);
			thishyPanel.setCheckBoxValues(id, v)
		}
	}
	if (f) {
		if (value.indexOf("10") != -1) {
			f.style.color = "#000";
			f.disabled = false;
		} else {
			f.value = "";
			f.disabled = true;
		}
	}
}

function onTargetHurtClick(v) {
	var id = "targetHurt";
	var value = thishyPanel.getCheckBoxValues(id);
	if (value.indexOf(v) == -1) {
		return;
	}
	if (v == "10") {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, "10")
	} else if (value.indexOf("10") != -1) {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, v)
	}
}

function onComplicationClick(v) {
	var id = "complication";
	var value = thishyPanel.getCheckBoxValues(id);
	if (value.indexOf(v) == -1) {
		return;
	}
	if (v == "16") {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, "16")
	} else if (value.indexOf("16") != -1) {
		thishyPanel.clearCheckBoxValues(id);
		thishyPanel.setCheckBoxValues(id, v)
	}
}

function onWeightChange(v, f) {
	debugger;
	if (thishyPanel.height && v) {
		var temp = thishyPanel.height * thishyPanel.height / 10000;
		var bmi = (v / temp).toFixed(2);
		f.style.color = "#000";
		f.value = bmi;
	} else {
		f.value = f.defaultValue;
		f.style.color = "#999";
	}
	thishyPanel.fieldValidate(this.schema)
}

function onMedicineClick(v, f1) {
	var id = "medicineBadEffect";
	var flag = true;
	if (v == 3) {
		thishyPanel.setRadioValue(id, {
					key : "n"
				});
		thishyPanel.setRadioDisabled(id, true)
		f1.value = "";
		f1.disabled = true;
	} else if (v == 1 || v == 2) {
		thishyPanel.setRadioDisabled(id, false)
		var value = thishyPanel.getRadioValue(id);
		if (value == "y") {
			f1.disabled = false;
		} else {
			f1.disabled = true;
		}
		flag = false;
	} else {
		thishyPanel.setRadioDisabled(id, false)
	}
	var BGLYY = document.getElementById("BGLYY" + thishyPanel.idPostfix);
	var medicineNotDiv = document.getElementById("div_medicineNot"+ thishyPanel.idPostfix);
	var medicineNot = document.getElementsByName("medicineNot"+ thishyPanel.idPostfix);
	var medicineNotValue = thishyPanel.getHtmlFldValue("medicineNot");
	var medicineOtherNot = document.getElementById("medicineOtherNot"+ thishyPanel.idPostfix);
	if (v == 1 || v == "") {
		thishyPanel.removeClass(medicineNotDiv, "x-form-invalid");
		medicineOtherNot.disabled = true;
		medicineOtherNot.value = "";
		thishyPanel.removeClass(medicineOtherNot, "x-form-invalid");
		thishyPanel.setHtmlFldValue("medicineNot" + thishyPanel.idPostfix, -1);
		BGLYY.style.color = "black";
		thishyPanel.setRadioDisabled("medicineNot", true);
	} else {
		BGLYY.style.color = "black";
		thishyPanel.setRadioDisabled("medicineNot", false);
		if (!medicineNotValue || medicineNotValue == "") {
			thishyPanel.addClass(medicineNotDiv, "x-form-invalid");
		} else if (medicineNotValue == "99") {
			medicineOtherNot.disabled = false;
			if (!medicineOtherNot.value || medicineOtherNot.value == "") {
				thishyPanel.addClass(medicineOtherNot, "x-form-invalid");
			}
		} else {
			medicineOtherNot.disabled = true;
			medicineOtherNot.value = "";
			thishyPanel.removeClass(medicineOtherNot, "x-form-invalid");
		}
	}
	thishyPanel.setMedicineDisabled(flag);
	thishyPanel.fireEvent("medicineSelectChange", v, thishyPanel);
}

function getstyle(sname) {
	for (var i = 0; i < document.styleSheets.length; i++) {
		var rules;
		if (document.styleSheets[i].cssRules) {
			rules = document.styleSheets[i].cssRules;
		} else {
			rules = document.styleSheets[i].rules;
		}
		for (var j = 0; j < rules.length; j++) {
			if (rules[j].selectorText == sname) {
				// 属性的作用是对一个选择的地址进行替换.意思应该是获取RULES[J]的CLASSNAME.有说错的地方欢迎指正
				return rules[j].style;
			}
		}
	}
}
function onMedicineNotClick(value) {
	var medicineOtherNot = document.getElementById("medicineOtherNot"+ thishyPanel.idPostfix);
	var medicineNotDiv = document.getElementById("div_medicineNot"+ thishyPanel.idPostfix);
	if (value == "") {
		thishyPanel.addClass(medicineNotDiv, "x-form-invalid");
	} else {
		thishyPanel.removeClass(medicineNotDiv, "x-form-invalid");
	}
	if (value == "99") {
		if (!medicineOtherNot.value || medicineOtherNot.value == "") {
			thishyPanel.addClass(medicineOtherNot, "x-form-invalid");
		}
		medicineOtherNot.disabled = false;
	} else {
		thishyPanel.removeClass(medicineOtherNot, "x-form-invalid");
		medicineOtherNot.value = "";
		medicineOtherNot.disabled = true;
	}
}
function getObjsByClass(clsName) {
	// 依据样式名称获取DOM对象
	var tags = this.tags || document.getElementsByTagName("*");
	var list = [];
	for (var k in tags) {
		var tag = tags[k];
		if (tag.className == clsName) {
			list.push(tag);
		}
	}
	return list;
}

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
		thishyPanel.addClass(f1, "x-form-invalid");
		thishyPanel.addClass(f2, "x-form-invalid");
	} else {
		f1.title = "收缩压(mmHg)";
		f2.title = "舒张压(mmHg)";
		thishyPanel.removeClass(f1, "x-form-invalid");
		thishyPanel.removeClass(f2, "x-form-invalid");
	}
}