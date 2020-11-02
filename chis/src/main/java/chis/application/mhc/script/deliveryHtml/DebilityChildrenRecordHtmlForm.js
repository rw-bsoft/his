$package("chis.application.mhc.script.deliveryHtml")
$import(
		"chis.script.BizHtmlFormView",
		"chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlFormTemplate",
		"chis.script.HtmlCommonMethod", "util.widgets.LookUpField")
$styleSheet("chis.css.DebilityChildrenRecordHtmlForm");

chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlForm = function(
		cfg) {
	chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlForm.superclass.constructor
			.apply(this, [cfg]);
	Ext
			.apply(
					this,
					chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlFormTemplate)
	Ext.apply(this, chis.script.HtmlCommonMethod);

	this.babyFlag = "";// 因为左边刷新，this.data会制空
	this.infoValue = {};
	this.createFields = ["babyBirth", "fatherBirth", "motherBirth",
			"motherJob", "fatherJob", "fatherName"];// ,"motherBirth","fatherBirth","motherJob","fatherJob"
	this.FieldToRed = ["babySexField", "babyBirthField"];// 字体变红的参数:id
	this.otherDisable = [{
				fld : "pregnancyDisease",
				type : "radio",
				control : [{
							key : "3",
							exp : 'eq',
							field : ["otherDisease"]
						}]
			}, {
				fld : "malforMation",
				type : "radio",
				control : [{
							key : "y",
							exp : 'eq',
							field : ["malforMationDescription"]
						}]
			}, {
				fld : "illnessScreening",
				type : "radio",
				control : [{
							key : "3",
							exp : 'eq',
							field : ["otherIllness"]
						}]
			}]
	this.disableFlds = ["otherIllness", "otherDisease",
			"malforMationDescription", "otherStatus"];// 初始化不可用
	this.checkBoxCount = 0;
	// this.mutualExclusionMy=[{name:"birthStatus",value:["1","2","3","4","6"],contro:"7",inputId:"otherStatus"}
	// ];

}
Ext
		.extend(
				chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlForm,
				chis.script.BizHtmlFormView, {
					getHTMLTemplate : function() {
						return this.getDebilityChildrenRecordHtmlFormTemplate();
					},// ext创建的控件
					addFieldAfterRender : function() {
						// 出生日期
						this.babyBirth = new Ext.form.DateField({
									name : 'babyBirth' + this.idPostfix,
									width : 90,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '出生日期',
									allowBlank : false,
									invalidText : "必填字段",
									// value:new Date(),
									renderTo : Ext.get("babyBirth"
											+ this.idPostfix)
								});

						// 母亲出生日期
						this.motherBirth = new Ext.form.DateField({
									name : 'motherBirth' + this.idPostfix,
									width : 90,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '出生日期',
									disabled : true,
									renderTo : Ext.get("motherBirth"
											+ this.idPostfix)
								});
						// 父亲出生日期
						this.fatherBirth = new Ext.form.DateField({
									name : 'fatherBirth' + this.idPostfix,
									width : 90,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '出生日期',
									renderTo : Ext.get("fatherBirth"
											+ this.idPostfix)
								});
						// 母亲职业
						this.motherJob = this.createDicField({
									"width" : 110,
									"defaultIndex" : 0,
									"id" : "chis.dictionary.jobtitle",
									"render" : "Tree",
									onlySelectLeaf : true,
									disabled : true,
									"selectOnFocus" : true
								});
						Ext.apply(this.motherJob, {
									name : "motherJob"
								})
						this.motherJob.fieldLabel = "母亲职业";
						this.motherJob.render(Ext.get("motherJob"
								+ this.idPostfix));
						// 父亲职业
						this.fatherJob = this.createDicField({
									"width" : 110,
									"defaultIndex" : 0,
									"id" : "chis.dictionary.jobtitle",
									"render" : "Tree",
									onlySelectLeaf : true,
									"selectOnFocus" : true
								});
						Ext.apply(this.fatherJob, {
									name : "fatherJob"
								})
						this.fatherJob.fieldLabel = "母亲职业";
						this.fatherJob.render(Ext.get("fatherJob"
								+ this.idPostfix));
						// 父亲名字
						this.fatherName = new util.widgets.LookUpFieldEx({
									width : 140
								})

						this.fatherName.render(Ext.get("fatherName"
								+ this.idPostfix));
						this.fatherName.on("lookup", this.findNo, this);
						this.fatherName.on("clear", this.clearFather, this);
						this.setRedLabel(this.FieldToRed, this);
						// 父母的信息不可用
						this.motherJob.disable();
						document.getElementById("motherName" + this.idPostfix).disabled = true;
						document.getElementById("motherPhone" + this.idPostfix).disabled = true;
						this.motherBirth.disable();
						this.babyBirth.setValue(new Date().format('Y-m-d'));

					},
					// 保存
					doSave : function() {
						if (!this.htmlFormSaveValidate(this.schema)) {
							return;
						}
						var values = this.getFormData();
						// alert(Ext.encode(values))
						this.saveToServer(values);
					},// 新建
					doCreate : function() {
						this.babyFlag = "";// 清缓存
						this.data = {};// 清缓存
						this.doNew(this.idPostfix);
						this.fireEvent("doCreate", this);
						this.initFormData(this.infoValue);
					},
					// 获取整个页面数据
					getFormData : function() {
						var ac = util.Accredit;
						var form = this.form.getForm();
						if (!this.schema) {

							return
						}
						var values = {};// 数据集合
						var items = this.schema.items;
						if (items) {
							var n = items.length;
							for (var i = 0; i < n; i++) {
								var it = items[i]
								if (this.op == "create"
										&& !ac.canCreate(it.acValue)) {
									continue;
								}
								var v = this.data[it.id] // ** modify by yzh
								// 2010-08-04
								if (v == undefined) {
									v = it.defaultValue
								}
								if (v != null && typeof v == "object") {
									v = v.key
								}
								var f = form.findField(it.id)
								if (f) {
									v = f.getValue();
									if (f.getXType() == "treeField") {

										var rawVal = f.getRawValue();
										if (rawVal == null || rawVal == "")
											v = "";
									}
									if (f.getXType() == "datefield"
											&& v != null && v != "") {
										v = v.format('Y-m-d');
										var today = new Date().format('Y-m-d');
										if (v > today) {

											return;
										}
									}

								} else {// 不是ext创建的控件
									if (it.tag == "text") {
										v = this.getValueById(it.id);
									} else if (it.tag == "radioGroup") {
										v = this.getRadioValue(it.id);
										// 修改前台传0过去，造成无法保存的问题
										if (v == 0) {
											v = "0";
										}
									} else if (it.tag == "checkBox") {
										v = this.getCheckBoxValues(it.id);

									}
								}
								// 处理手动用ext创建字段 字典
								if (it.dic) {
									if (it.id == "motherJob") {
										v = eval("this." + "motherJob"
												+ ".getValue()");
									}
									if (it.id == "fatherJob") {
										v = eval("this." + "fatherJob"
												+ ".getValue()");
									}
								}
								values[it.id] = v;
								//
								if (it.id == "babyBirth") {
									v = this.babyBirth.getValue();
									if (v) {
										v = v.format('Y-m-d');
										values["babyBirth"] = v;
									} else {
										return;
									}
								}
							}
						}
						// alert(Ext.encode(this.exContext))
						if (this.exContext.ids["MHC_PregnantRecord.pregnantId"]) {
							values["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
						}
						values["motherCardNo"] = this.exContext.empiData.idCard;
						values["babyId"] = this.babyFlag;
						if (this.motherBirth.getValue()) {
							values["motherBirth"] = this.motherBirth.getValue()
									.format('Y-m-d');
						}
						if (this.fatherBirth.getValue()) {
							values["fatherBirth"] = this.fatherBirth.getValue()
									.format('Y-m-d');
						}
						values["fatherName"] = this.fatherName.getValue();
						values["fatherEmpiId"] = this.data["fatherEmpiId"];
						return values;
					},
					//
					initFormData : function(data) {

						this.initFieldValue = {};
						if (data["babyId"]) {
							this.babyFlag = data["babyId"];
						}
						Ext.apply(this.data, data)
						this.initDataId = this.data[this.schema.pkey]
						var form = this.form.getForm()
						var items = this.schema.items
						var n = items.length
						for (var i = 0; i < n; i++) {
							var it = items[i]
							var f = form.findField(it.id)
							if (f) {
								var v = data[it.id]
								if (v != undefined || this.flag == true) {
									if (it.dic && v !== "" && v === 0) {// add
										// by
										v = "0";
									}
									f.setValue(v)
									if (it.dic && v != "0" && f.getValue() != v) {
										f.counter = 1;
									}
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
										if (it.controValue == v) {
											this.getObj(it.controId).disabled = false;
										}
										this.setRadioValue(it.id, v)
									} else if (it.tag == "checkBox") {
										this.setCheckBoxValues(it.id, v)
									} else if (it.tag == "selectgroup") {
										this.setSelectValues(it.id, v)
									}
								}
							}
							if (it.dic) {
								if (it.id == "motherJob") {
									var r = data["motherJob"];
									v = eval("this." + "motherJob"
											+ ".setValue(r)");
								}
								if (it.id == "fatherJob") {
									var r = data["fatherJob"];
									v = eval("this." + "fatherJob"
											+ ".setValue(r)");
								}
								if (it.id == "babySex" && data["babySex"]) {
									this
											.removeClass(
													document
															.getElementById("div_babySex"
																	+ this.idPostfix),
													"x-form-invalid");
								}
							}

							this.setKeyReadOnly(true)
						}
						if (data["babyBirth"]) {
							this.babyBirth.setValue(data["babyBirth"]);
						}
						if (data["motherBirth"]) {
							this.motherBirth.setValue(data["motherBirth"]);
						}
						if (data["fatherBirth"]) {
							this.fatherBirth.setValue(data["fatherBirth"]);
						}

						if (data["fatherName"]) {
							this.fatherName.setValue(data["fatherName"]);
						}
						this.motherJob.disable();
						document.getElementById("motherName" + this.idPostfix).disabled = true;
						document.getElementById("motherPhone" + this.idPostfix).disabled = true;
						this.motherBirth.disable();

					},
					// ****************************获取页面的数据以及页面控制的方法*************************

					// 不為空
					// *******************页面的控制的方法************************
					// 初始化页面
					doNew : function(Id) {
						var dom = document.getElementsByTagName("input");
						var l = dom.length;

						for (var i = 0; i < l; i++) {
							var d = dom[i];
							if (d.type == "text") {
								var arrt = dom[i].getAttribute("id");
								if (arrt.indexOf(Id) > 0) {
									d.value = "";
									document.getElementById(arrt).disabled = false;
								}
							} else if (d.type == "radio") {
								if (arrt.indexOf(Id) > 0) {
									d.checked = false;
								}
							} else if (d.type == "checkbox") {
								if (arrt.indexOf(Id) > 0) {
									d.checked = false;
								}
							}
						} //
						this.babyBirth.setValue("");
						this.fatherName.setValue("");
						// this.reMoveClass("babySex");
						this.initFieldDisable(this.disableFlds);
						this.addClass(document.getElementById("div_babySex"
										+ this.idPostfix), "x-form-invalid");
						this.babyBirth.setValue(new Date().format('Y-m-d'));
						this.afterDoNew();
						this.resetButtons();
					},

					// ******************去后台查询数据的方法****************************
					saveToServer : function(saveRequest) {
						if (!saveRequest) {
							return;
						}
						if (!this.fireEvent("beforeSave", this.entryName,
								this.op, saveRequest)) {

							return;
						}
						this.saving = true
						this.form.el.mask("正在保存数据...", "x-mask-loading")

						util.rmi.jsonRequest({
									serviceId : "chis.pregnantRecordService",
									serviceAction : "saveBabyVisitInfoToHtml",
									op : this.op,
									body : saveRequest,
									method : "execute"
								}, function(code, msg, json) {
									this.form.el.unmask()
									this.saving = false
									if (code > 300) {

										this.processReturnMsg(code, msg,
												this.saveToServer,
												[saveRequest], json.body);
										return
									}
									Ext.apply(this.data, saveRequest);
									if (json.body) {

										this.initFormData(json.body)
										this.fireEvent("save", this.entryName,
												this.op, json, this.data);
									}
									this.op = "update"
								}, this)// jsonRequest
					},
					findNo : function(field) {
						$import("chis.application.mpi.script.EMPIInfoModule")
						var expertQuery = this.midiModules["expertQuery"];
						if (!expertQuery) {
							expertQuery = new chis.application.mpi.script.EMPIInfoModule(
									{
										entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
										title : "个人基本信息查询",
										height : 450,
										modal : true,
										mainApp : this.mainApp
									});
							this.midiModules["expertQuery"] = expertQuery;
						}
						expertQuery.on("onEmpiReturn", function(r) {
									if (!r) {
										return;
									}

									var sex = r.sexCode;
									if (sex != "1") {
										Ext.Msg.alert('提示信息', "性别不符!");
										return;
									}
									var form = this.form.getForm();
									this.fatherName.setValue(r.personName);
									this.fatherJob.setValue(r.workCode);
									document.getElementById("fatherPhone"
											+ this.idPostfix).value = r.contactPhone;
									this.fatherBirth.setValue(r.birthday);
									this.data["fatherEmpiId"] = r.empiId;

								}, this);
						var win = expertQuery.getWin();
						win.setPosition(250, 100);
						win.show();
					},
					vtypeValidate : function(fieldId, vtype, length) {
						var _cfg = this;
						if ("babyIdCard" == fieldId) {
							var objCard = document.getElementById("babyIdCard"
									+ this.idPostfix);
							Ext.getDom("babyIdCard" + this.idPostfix).onchange = function() {
								// _cfg.onIdCardBlur("babyIdCard", _cfg);
								_cfg.onIdCardBlur("babyIdCard");
							};
						}
					},
					onReadyAffter : function() {
						var _cfg = this;
						var name = "birthStatus";
						var array = ["2", "3", "4", "6"];
						Ext.getDom("birthStatus_1" + this.idPostfix).onclick = function() {
							_cfg.cancleSelected(name, array);
						};
						var array2 = ["1", "3", "4", "6"];
						Ext.getDom("birthStatus_2" + this.idPostfix).onclick = function() {
							_cfg.cancleSelected(name, array2);
						};
						var array3 = ["1", "2", "4", "6"];
						Ext.getDom("birthStatus_3" + this.idPostfix).onclick = function() {
							_cfg.cancleSelected(name, array3);
						};
						var array4 = ["1", "2", "3"];
						Ext.getDom("birthStatus_4" + this.idPostfix).onclick = function() {
							_cfg.cancleSelected(name, array4);
						};
						var array6 = ["1", "2", "3"];
						Ext.getDom("birthStatus_6" + this.idPostfix).onclick = function() {
							_cfg.cancleSelected(name, array6);
						};
						Ext.getDom("birthStatus_7" + this.idPostfix).onclick = function() {
							_cfg.contro(name, "otherStatus");
						};
					},
					cancleSelected : function(name, array) {
						var dom = document.getElementsByName(name);
						var len = dom.length;
						var arrayLen = array.length;

						for (var j = 0; j < arrayLen; j++) {
							for (var i = 0; i < len; i++) {
								if (array[j] == dom[i].value) {
									dom[i].checked = false;
								}
							}
						}

					},
					contro : function(name, id) {
						var dom = document.getElementsByName(name);

						var obj = this.getObj(id);
						if (dom[6].checked) {
							if (typeof obj) {
								obj.disabled = false;
							}
						} else {
							obj.value = "";
							obj.disabled = true;
						}

					},
					clearFather : function() {
						this.data["fatherEmpiId"] = "";
						this.fatherJob.setValue();
						this.fatherBirth.setValue();
						this.setValueById("fatherPhone", "");
					}

				});