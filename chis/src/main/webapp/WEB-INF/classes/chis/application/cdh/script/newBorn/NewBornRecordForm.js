$package("chis.application.cdh.script.newBorn")
$import(
		// "chis.script.BizSimpleFormView", "app.modules.list.SimpleListView",
		// "chis.application.mpi.script.CombinationSelect",
		// "chis.script.BizTableFormView",
		"chis.application.mpi.script.ParentsQueryList",
		"chis.application.mpi.script.SubTableForm", "util.widgets.LookUpField",
		"chis.script.HtmlCommonMethod", "chis.script.BizHtmlFormView",
		"chis.application.cdh.script.newBorn.NewBornRecordFormTemPlate")
$styleSheet("chis.css.NewBornRecordForm");

chis.application.cdh.script.newBorn.NewBornRecordForm = function(cfg) {
	chis.application.cdh.script.newBorn.NewBornRecordForm.superclass.constructor
			.apply(this, [cfg]);
	Ext.apply(this,
			chis.application.cdh.script.newBorn.NewBornRecordFormTemPlate);
	Ext.apply(this, chis.script.HtmlCommonMethod)
	this.fldTofld = [{
				"name" : "face",
				"value" : "9",
				"id" : [{
							"contro" : "yes",
							"id" : "faceOther"
						}]
			}, {
				"name" : "bregmaStatus",
				"value" : "4",
				"id" : [{
							"contro" : "yes",
							"id" : "otherStatus1"
						}]
			}, {
				"name" : "eye",
				"value" : "99",
				"id" : [{
							"contro" : "yes",
							"id" : "eyeAbnormal"
						}]
			}, {
				"name" : "ear",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "earAbnormal"
						}]
			}, {
				"name" : "neck",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "neck1"
						}]
			}, {
				"name" : "nose",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "noseAbnormal"
						}]
			}, {
				"name" : "skin",
				"value" : "99",
				"id" : [{
							"contro" : "yes",
							"id" : "skinAbnormal"
						}]
			}, {
				"name" : "mouse",
				"value" : "14",
				"id" : [{
							"contro" : "yes",
							"id" : "mouseAbnormal"
						}]
			}, {
				"name" : "anal",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "analAbnormal"
						}]
			}, {
				"name" : "heartlung",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "heartLungAbnormal"
						}]
			}, {
				"name" : "umbilical",
				"value" : "9",
				"id" : [{
							"contro" : "yes",
							"id" : "umbilicalOther"
						}]
			}, {
				"name" : "spine",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "spineAbnormal"
						}]
			}, {
				"name" : "genitalia",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "genitaliaAbnormal"
						}]
			}, {
				"name" : "abdominal",
				"value" : "5",
				"id" : [{
							"contro" : "yes",
							"id" : "abdominalabnormal"
						}]
			}, {
				"name" : "referral",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "referralReason"
						}, {
							"contro" : "yes",
							"id" : "referralUnit"
						}]
			}, {
				"name" : "limbs",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "limbsAbnormal"
						}]
			}, {
				"name" : "pregnancyDisease",
				"value" : "3",
				"id" : [{
							"contro" : "yes",
							"id" : "otherDisease"
						}]
			}, {
				"name" : "malforMation",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "malforMationDescription"
						}]
			}, {
				"name" : "illnessScreening",
				"value" : "3",
				"id" : [{
							"contro" : "yes",
							"id" : "otherIllness"
						}]
			}];
	this.disableFlds = ["faceOther", "otherStatus", "eyeAbnormal",
			"earAbnormal", "neck1", "noseAbnormal", "skinAbnormal",
			"mouseAbnormal", "analAbnormal", "heartLungAbnormal",
			"genitaliaAbnormal", "abdominalabnormal", "spineAbnormal",
			"umbilicalOther", "referralReason", "referralUnit",
			"limbsAbnormal", "otherIllness", "otherDisease",
			"malforMationDescription", "otherStatus", "otherStatus1"];// 初始化不可用
	this.createFields = ["babyBirth", "fatherName", "motherName", "fatherJob",
			"motherJob", "visitDoctor", "fatherBirth", "motherBirth",
			"visitDate", "nextVisitDate"];
}

Ext.extend(chis.application.cdh.script.newBorn.NewBornRecordForm,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getNewBornRecordFormTemPlate();
			},
			addFieldAfterRender : function() {
				// 出生日期
				this.babyBirth = new Ext.form.DateField({
							name : 'babyBirth' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '出生日期',
							// allowBlank : false,
							// invalidText : "必填字段",
							renderTo : Ext.get("babyBirth" + this.idPostfix)
						});
				// 母亲出生日期
				this.motherBirth = new Ext.form.DateField({
							name : 'motherBirth' + this.idPostfix,
							width : 130,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '出生日期',
							renderTo : Ext.get("motherBirth" + this.idPostfix)
						});
				// 父亲名字
				this.fatherName = new util.widgets.LookUpFieldEx({})

				this.fatherName.render(Ext.get("fatherName" + this.idPostfix));
				this.fatherName.on("lookup", this.findNo, this);
				this.fatherName.on("clear", this.clearFather, this);

				// 母亲名字
				this.motherName = new util.widgets.LookUpFieldEx({})

				this.motherName.render(Ext.get("motherName" + this.idPostfix));
				this.motherName.on("lookup", this.findNo, this);
				this.motherName.on("clear", this.clearMother, this);
				// 父亲出生日期
				this.fatherBirth = new Ext.form.DateField({
							name : 'fatherBirth' + this.idPostfix,
							width : 130,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '出生日期',
							renderTo : Ext.get("fatherBirth" + this.idPostfix)
						});
				// 母亲职业
				this.motherJob = this.createDicField({
							"width" : 90,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.jobtitle",
							"render" : "Tree",
							onlySelectLeaf : true,
							"selectOnFocus" : true
						});
				Ext.apply(this.motherJob, {
							name : "motherJob"
						})
				this.motherJob.fieldLabel = "母亲职业";
				this.motherJob.render(Ext.get("motherJob" + this.idPostfix));
				// 父亲职业
				this.fatherJob = this.createDicField({
							"width" : 90,
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
				this.fatherJob.render(Ext.get("fatherJob" + this.idPostfix));
				// 随访日期
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");

				this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "本次随访日期",
							value : curDate,
							disabled : true,// 禁止该控件
							renderTo : Ext.get("visitDate" + this.idPostfix)
						});
				this.nextVisitDate = new Ext.form.DateField({
							name : 'nextVisitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							minValue : curDate,
							// allowBlank : false,
							// invalidText : "必填字段",
							fieldLabel : "下次随访日期",
							renderTo : Ext
									.get("nextVisitDate" + this.idPostfix)
						});
				// 随访医生签名
				this.visitDoctor = this.createDicField({
							"width" : 150,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user01",
							"render" : "Tree",
							onlySelectLeaf : true,
							"selectOnFocus" : true,
							parentKey : this.mainApp.topUnitId
						});
				Ext.apply(this.visitDoctor, {
							name : "visitDoctor"
						})
				this.visitDoctor.fieldLabel = "随访医生";
				this.visitDoctor.render("visitDoctor" + this.idPostfix);
				var v = {};
				v["key"] = this.mainApp.uid
				v["text"] = this.mainApp.uname
				this.visitDoctor.setValue(v);
				this.visitDoctor.disable(), // 禁止该控件
				this.radioToInput(this.fldTofld);
			},
			// 保存
			doSave : function() {
				if (!this.htmlFormSaveValidate(this.schema)) {
					return;
				}

				var values = this.getFormData();
				// alert(Ext.encode(values))
				this.saveToServer(values);

			},
			doCreate : function() {
				this.doNew(this.idPostfix);
				if (this.data) {
					this.data = {};
				}
				this.fireEvent("doCreate", this);

			},
			// 调入
			doImportIn : function() {

				var empiId = this.data["empiId"];
				var data = {};
				data["empiId"] = empiId;
				// 先获取页面是否填写了身份证号和出生证号
				var babyIdCard = this.getValueById("babyIdCard");
				var certificateNo = this.getValueById("certificateNo");
				var v = {};
				if (!babyIdCard || !certificateNo) {
					v["babyIdCard"] = babyIdCard;
					v["certificateNo"] = certificateNo;

				} else {
					v = this.getCardInfo(data);// 获取 儿童身份证号、出生证号、母亲的身份证号
				}
				var moduleName = "ToMHC";
				var module = this.createCombinedModule(moduleName,
						this.refModule);
				module.moduleName = moduleName;
				module.actionName = "apply";
				module.on("importIn1", this.importIn1, this);
				module.initDataId = v;
				this.module = module;
				this.showWin(module);
				this.module.loadData();
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
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
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
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
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
								v = this.getRadioValueMy(it.id);
								// 修改前台传0过去，造成无法保存的问题
								if (v == 0) {
									v = "0";
								}
							} else if (it.tag == "checkBox") {
								v = this.getCheckBoxValuesMy(it.id);
							}
						}
						var value = {}
						value[it.id] = v;
						if (values["jbxx"] && "jbxx" == it.lb) {
							Ext.apply(values["jbxx"], value);
						} else if (values["fsjl"] && "fsjl" == it.lb) {
							Ext.apply(values["fsjl"], value);
						} else {
							values[it.lb] = value;
						}
					}
				}
				values["fsjl"]["empiId"] = this.data["empiId"];
				values["jbxx"]["empiId"] = this.data["empiId"];
				values["jbxx"]["babyName"] = this.data["babyName"];

				values["fsjl"]["visitId"] = this.data["visitId"];
				values["jbxx"]["babyId"] = this.data["babyId"];
				values["fsjl"]["babyId"] = this.data["babyId"];
				values["fsjl"]["visitDoctor"] = this.visitDoctor.getValue();
				values["jbxx"]["visitDoctor"] = this.visitDoctor.getValue();
				if (this.nextVisitDate.getValue()) {
					values["fsjl"]["nextVisitDate"] = this.nextVisitDate
							.getValue().format('Y-m-d');
				}
				if (this.visitDate.getValue()) {
					values["fsjl"]["visitDate"] = this.visitDate.getValue()
							.format('Y-m-d');
				}
				values["jbxx"]["motherJob"] = this.motherJob.getValue();
				values["jbxx"]["fatherJob"] = this.fatherJob.getValue();
				values["jbxx"]["motherBirth"] = this.motherBirth.getValue();
				values["jbxx"]["fatherBirth"] = this.fatherBirth.getValue()
				values["jbxx"]["fatherName"] = this.fatherName.getValue();
				values["jbxx"]["motherName"] = this.motherName.getValue();
				values["jbxx"]["fatherEmpiId"] = this.data["fatherEmpiId"];
				values["jbxx"]["motherEmpiId"] = this.data["motherEmpiId"];
				return values;
			},
			initFormData : function(data) {
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
							if (it.dic && v !== "" && v === 0) {// add by
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
									var strId = it.controId;
									var s = strId.indexOf(",");

									if (s == -1) {
										this.getObj(strId).disabled = false;
									} else {
										this.getObj(strId.substring(0, s)).disabled = false;// s处不要
										this.getObj(strId.substring(s + 1,
												strId.length)).disabled = false;
									}
								}
								this.setRadioValueMy(it.id, v)
							} else if (it.tag == "checkBox") {
								this.setCheckBoxValuesMy(it.id, v)
							} else if (it.tag == "selectgroup") {
								this.setSelectValues(it.id, v)
							}
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
				if (data["visitDate"]) {
					this.visitDate.setValue(data["visitDate"]);
				}
				if (data["nextVisitDate"]) {
					this.nextVisitDate.setValue(data["nextVisitDate"]);
				}
				if (data["visitDoctor"]) {
					this.visitDoctor.setValue(data["visitDoctor"]);
				}
				if (data["motherJob"]) {
					this.motherJob.setValue(data["motherJob"]);
				}
				if (data["fatherJob"]) {
					this.fatherJob.setValue(data["fatherJob"]);
				}
				if (data["fatherName"]) {
					this.fatherName.setValue(data["fatherName"]);
				}
				if (data["motherName"]) {
					this.motherName.setValue(data["motherName"]);
				}
				// =======出生情况=========
				if (data["otherStatus"]) {
					document.getElementById("otherStatus" + this.idPostfix).disabled = false;
				}
			},
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

				this.nextVisitDate.setValue("");
				this.fatherName.setValue("");
				this.motherName.setValue("");
				var v = {};
				v["key"] = this.mainApp.uid
				v["text"] = this.mainApp.uname
				this.visitDoctor.setValue(v);
				this.visitDoctor.disabled = true, // 禁止该控件
				this.initFieldDisable(this.disableFlds);
				this.visitDate.setValue(new Date().format('Y-m-d'));
			},
			// **************************其他页面的处理方法**************************
			importIn1 : function(data) {
				this.initFormData(data);
				this.module.getWin().hide();
			},
			changeManaUnit : function(combo, node) {
				this.visitDoctor.text = node.text;
				this.visitDoctor.key = node.id;
			},
			saveToServer : function(saveRequest) {
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "saveChildVistRecordAndInfo",
							op : this.op,
							body : saveRequest,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								return
							}
							Ext.apply(this.data, saveRequest);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data);
							}
							this.op = "update"
						}, this)
			},
			// ******************去后台查询数据的方法****************************
			getCardInfo : function(data) {
				if (!data) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "getInfoNumber",
							method : "execute",
							body : data
						});
				var body = result.json.body;
				if (body) {
					return body;
				}
			},
			findNo : function(field) {
				var v = field.container.id;
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
					var form = this.form.getForm();
					var sex = r.sexCode;
					if (v.indexOf("fatherName") != -1) {
						if (sex != "1") {
							Ext.Msg.alert('提示信息', "性别不符!");
							return;
						}
						this.fatherName.setValue(r.personName);
						this.fatherJob.setValue(r.workCode);
						document.getElementById("fatherPhone" + this.idPostfix).value = r.contactPhone;
						this.fatherBirth.setValue(r.birthday);
						this.data["fatherEmpiId"] = r.empiId;
					}
					if (v.indexOf("motherName") != -1) {
						if (sex != "2") {
							Ext.Msg.alert('提示信息', "性别不符!");
							return;
						}
						this.motherName.setValue(r.personName);
						this.motherJob.setValue(r.workCode);
						document.getElementById("motherPhone" + this.idPostfix).value = r.contactPhone;
						this.motherBirth.setValue(r.birthday);
						this.data["motherEmpiId"] = r.empiId;
					}
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
				var dom = document.getElementsByName(name + this.idPostfix);
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
				var dom = document.getElementsByName(name + this.idPostfix);
				var obj = this.getObj(id);
				if (dom[6].checked) {
					obj.disabled = false;
				} else {
					obj.value = "";
					obj.disabled = true;
				}
			},// 新生儿随访打印
			doPrintNewBorn : function() {
				this.visitId = this.data["visitId"];
				if (!this.visitId) {
					this.visitId = "23333333";// 当this.visitId 为空的时候，随便设置值
				}
				var url = "resources/chis.prints.template.ChildVisitInfoAndRecord.print?type="
						+ 1 + "&visitId=" + this.visitId
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
			},
			clearFather : function() {
				this.data["fatherEmpiId"] = "";
				this.fatherJob.setValue();
				this.fatherBirth.setValue();
				this.setValueById("fatherPhone", "");
			},
			clearMother : function() {
				this.data["motherEmpiId"] = "";
				this.motherJob.setValue();
				this.motherBirth.setValue();
				this.setValueById("motherPhone", "");
			}

		});