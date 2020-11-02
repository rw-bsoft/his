$package("chis.application.hr.script")
$import("chis.script.BizHtmlFormView", "app.modules.list.SimpleListView",
		"chis.application.mpi.script.CombinationSelect",
		"chis.script.BizSimpleFormView", "chis.script.BizTableFormView",
		"chis.application.hr.script.BasicPersonalInformationTemplate",
		"chis.application.mpi.script.ParentsQueryList",
		"chis.application.mpi.script.SubTableForm")
$styleSheet("chis.css.BasicPersionalInformation");

chis.application.hr.script.BasicPersonalInformationForm = function(cfg) {
	cfg.width = 1000
	cfg.autoLoadData = false;
	var _ctrs = this;
	cfg.idPostfix = cfg.idPostfix || "_bpif01";
	this.queryServiceId = "chis.empiService";
	this.queryServiceActioin = "advancedSearch";
	this.height = (window.screen.height / 2) + 180;
	this.queryInfo = {};
	chis.application.hr.script.BasicPersonalInformationForm.superclass.constructor
			.apply(this, [cfg]);
	Ext.apply(this,
					chis.application.hr.script.BasicPersonalInformationTemplate);
	this.createFields = ["birthday", "confirmdate_gxy", "confirmdate_tnb",
			"confirmdate_gxb", "confirmdate_exzl", "confirmdate_zxjsjb",
			"confirmdate_gzjb", "confirmdate_zyb",
			"confirmdate_qt", "confirmdate_mxzsxfjb", "confirmdate_nzz",
			"confirmdate_jhb",
			"confirmdate_qtfdcrb", "startdate_ss0", "startdate_ss1",
			"startdate_ws0", "startdate_ws1", "startdate_sx0", "startdate_sx1",
			"deadDate", "nationCode", "manaDoctorId", "manaUnitId",
			"regionCode"];
	this.disableFlds = ["this.confirmdate_gxy", "this.confirmdate_tnb",
			"this.confirmdate_gxb", "this.confirmdate_exzl",
			"this.confirmdate_zxjsjb", "this.confirmdate_gzjb",
			"this.confirmdate_zyb",
			"this.confirmdate_qt", "this.confirmdate_mxzsxfjb",
			"this.confirmdate_nzz", "this.confirmdate_jhb",
			"this.confirmdate_qtfdcrb", "this.startdate_ss0",
			"this.startdate_ss1", "this.startdate_ws0", "this.startdate_ws1",
			"this.startdate_sx0", "this.startdate_sx1", "this.deadDate",
			"this.manaUnitId", "insuranceCode1", "a_qt1", "diseasetext_zyb",
			"diseasetext_qtfdcrb", "diseasetext_qt", "diseasetext_ss0",
			"diseasetext_ss1", "diseasetext_ws0", "diseasetext_ws1",
			"diseasetext_sx0", "diseasetext_sx1", "qt_fq1", "qt_mq1",
			"qt_xdjm1", "qt_zn1", "diseasetextYCBS", "cjqk_qtcj1",
			"deadReason", "diseasetext_check_jb_0202",
			"diseasetext_check_jb_0203", "diseasetext_check_jb_0204",
			"diseasetext_check_jb_0205", "diseasetext_check_jb_0206",
			"diseasetext_check_jb_0207", "diseasetext_check_jb_0208",
			"diseasetext_check_jb_0209", "diseasetext_check_jb_0210",
			 "diseasetext_check_jb_0212",
			"diseasetext_check_jb_0298", "diseasetext_check_jb_0299"];
	this.otherDisable = [{
				fld : "insuranceCode",
				type : "checkbox",
				control : [{
							key : "99",
							exp : 'eq',
							field : ["insuranceCode1"]
						}]
			}, {
				fld : "deadFlag",
				type : "radio",
				control : [{
							key : "y",
							exp : 'eq',
							field : ["deadReason", "this.deadDate"],
							mustField : ["deadReason", "this.deadDate"],
							redLabels : ["SWRQ", "SWYY"]
						}]
			}, {
				fld : "diseasetext_check_gm",
				type : "checkbox",
				control : [{
							key : "0109",
							exp : 'eq',
							field : ["a_qt1"]
						}]
			}, {
				fld : "diseasetext_radio_jb",
				type : "radio",
				control : [{
					key : "02",
					exp : 'eq',
					field : ["diseasetext_check_jb_0202",
							"diseasetext_check_jb_0203",
							"diseasetext_check_jb_0204",
							"diseasetext_check_jb_0205",
							"diseasetext_check_jb_0206",
							"diseasetext_check_jb_0207",
							"diseasetext_check_jb_0208",
							"diseasetext_check_jb_0209",
							"diseasetext_check_jb_0210",
							"diseasetext_check_jb_0212",
							"diseasetext_check_jb_0298",
							"diseasetext_check_jb_0299"]
				}]
			}, {
				fld : "diseasetext_check_jb",
				type : "checkbox",
				control : [{
							key : "0202",
							exp : 'eq',
							field : ["this.confirmdate_gxy"]
						}, {
							key : "0203",
							exp : 'eq',
							field : ["this.confirmdate_tnb"]
						}, {
							key : "0204",
							exp : 'eq',
							field : ["this.confirmdate_gxb"]
						}, {
							key : "0205",
							exp : 'eq',
							field : ["this.confirmdate_mxzsxfjb"]
						}, {
							key : "0206",
							exp : 'eq',
							field : ["this.confirmdate_exzl"]
						}, {
							key : "0207",
							exp : 'eq',
							field : ["this.confirmdate_nzz"]
						}, {
							key : "0208",
							exp : 'eq',
							field : ["this.confirmdate_zxjsjb"]
						}, {
							key : "0209",
							exp : 'eq',
							field : ["this.confirmdate_jhb"]
						}, {
							key : "0210",
							exp : 'eq',
							field : ["this.confirmdate_gzjb"]
						}, {
							key : "0212",
							exp : 'eq',
							field : ["diseasetext_zyb", "this.confirmdate_zyb"]
						}, {
							key : "0298",
							exp : 'eq',
							field : ["diseasetext_qtfdcrb",
									"this.confirmdate_qtfdcrb"]
						}, {
							key : "0299",
							exp : 'eq',
							field : ["diseasetext_qt", "this.confirmdate_qt"]
						}]
			}, {
				fld : "diseasetext_ss",
				type : "radio",
				control : [{
					key : "0302",
					exp : 'eq',
					field : ["diseasetext_ss0", "this.startdate_ss0",
							"diseasetext_ss1", "this.startdate_ss1"]
				}]
			}, {
				fld : "diseasetext_ws",
				type : "radio",
				control : [{
					key : "0602",
					exp : 'eq',
					field : ["diseasetext_ws0", "this.startdate_ws0",
							"diseasetext_ws1", "this.startdate_ws1"]
				}]
			}, {
				fld : "diseasetext_sx",
				type : "radio",
				control : [{
					key : "0402",
					exp : 'eq',
					field : ["diseasetext_sx0", "this.startdate_sx0",
							"diseasetext_sx1", "this.startdate_sx1"]
				}]
			}, {
				fld : "diseasetext_check_fq",
				type : "checkbox",
				control : [{
							key : "0799",
							exp : 'eq',
							field : ["qt_fq1"]
						}]
			}, {
				fld : "diseasetextCheckMQ",
				type : "checkbox",
				control : [{
							key : "0899",
							exp : 'eq',
							field : ["qt_mq1"]
						}]
			}, {
				fld : "diseasetextCheckXDJM",
				type : "checkbox",
				control : [{
							key : "0999",
							exp : 'eq',
							field : ["qt_xdjm1"]
						}]
			}, {
				fld : "diseasetextCheckZN",
				type : "checkbox",
				control : [{
							key : "1099",
							exp : 'eq',
							field : ["qt_zn1"]
						}]
			}, {
				fld : "diseasetextRedioYCBS",
				type : "radio",
				control : [{
							key : "0502",
							exp : 'eq',
							field : ["diseasetextYCBS"]
						}]
			}, {
				fld : "diseasetextCheckCJ",
				type : "checkbox",
				control : [{
							key : "1199",
							exp : 'eq',
							field : ["cjqk_qtcj1"]
						}]
			}];
	this.mutualExclusion = [{
				fld : "diseasetext_check_gm",
				key : "0101",
				other : ["a_qt1"]
			}, {
				fld : "diseasetext_check_bl",
				key : "1201",
				other : []
			}, {
				fld : "diseasetext_check_fq",
				key : "0701",
				other : ["qt_fq1"]
			}, {
				fld : "diseasetextCheckMQ",
				key : "0801",
				other : ["qt_mq1"]
			}, {
				fld : "diseasetextCheckXDJM",
				key : "0901",
				other : ["qt_xdjm1"]
			}, {
				fld : "diseasetextCheckZN",
				key : "1001",
				other : ["qt_zn1"]
			}, {
				fld : "diseasetextCheckCJ",
				key : "1101",
				other : ["cjqk_qtcj1"]
			}]
}

Ext.extend(chis.application.hr.script.BasicPersonalInformationForm,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getBasicInformationHTML();
			},
			afterSaveData : function() {
				this.refreshEhrTopIcon();
			},
			addFieldAfterRender : function() {
				for (var i = 0; i < this.createFields.length; i++) {
					var fid = this.createFields[i];
					if (fid == "nationCode") {
						var cfg = {
							"width" : 200,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.ethnic",
							"render" : "Simple",
							"defaultValue" : {
								key : "01",
								text : "汉族"
							},
							"selectOnFocus" : true
						}
						this.nationCode = this.createDicField(cfg);
						this.nationCode.allowBlank = false;
						this.nationCode.fieldLabel = "民族";
						this.nationCode.render(Ext.get("div_nationCode"
								+ this.idPostfix));
						this.nationCode.validate();
						continue;
					}
					if (fid == "manaDoctorId") {
						this.manaDoctorId = this.createDicField({
									"width" : 250,
									"defaultIndex" : 0,
									"id" : "chis.dictionary.user01",
									"render" : "Tree",
									onlySelectLeaf : true,
									"selectOnFocus" : true,
									parentKey : this.mainApp.topUnitId

								});
						this.manaDoctorId.name = "manaDoctorId";
						this.manaDoctorId.allowBlank = false;
						this.manaDoctorId.fieldLabel = "责任医生";
						this.manaDoctorId.render(Ext.get("div_manaDoctorId"
								+ this.idPostfix));
						this.manaDoctorId.on("select", this.changeManaUnit,
								this);
						this.manaDoctorId.validate();
						continue;
					}
					if (fid == "manaUnitId") {
						this.manaUnitId = this.createDicField({
							"width" : 250,
							"defaultIndex" : 0,
							"id" : "chis.@manageUnit",
							"render" : "Tree",
							onlySelectLeaf : true,
							"selectOnFocus" : true,
							"readOnly" : true
								// 只读
							});
						this.manaUnitId.name = "manaUnitId";
						this.manaUnitId.fieldLabel = "管辖机构";
						this.manaUnitId.disable();
						this.manaUnitId.render(Ext.get("div_manaUnitId"
								+ this.idPostfix));
						this.manaUnitId.validate();
						continue;
					}
					if (fid == "regionCode") {
						if ("tree" == this.mainApp.exContext.areaGridShowType) {
							this.regionCode = this.createDicField({
										"width" : 250,
										"defaultIndex" : 0,
										"id" : "chis.dictionary.areaGrid",
										"render" : "Tree",
										onlySelectLeaf : true,
										"selectOnFocus" : true
									});
							this.regionCode.name = "regionCode";
							this.regionCode.allowBlank = false;
							this.regionCode.selectOnFocus = true;
							this.regionCode.fieldLabel = "网格地址：";
							this.regionCode.render(Ext.get("div_regionCode"
									+ this.idPostfix));
							this.regionCode.on("select", this.changeManaUnit1,
									this);

						} else if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							var it = {
								id : "regionCode"
							}
							it.otherConfig = {
								'not-null' : 'true',
								width : 250,
								allowBlank : false,
								invalidText : "必填字段"
							};
							// it.afterSelect=this.afterSelect;
							this.regionCode = this.createAreaGridField(it);
							this.regionCode.render(Ext.get("div_regionCode"
									+ this.idPostfix));
							this.regionCode.on("afterSelect",
									this.getRegionInfo, this);
						} else {
							var _ctr = this;
							this.regionCode = new Ext.form.TriggerField({
										name : "regionCode",
										onTriggerClick : function() {
											_ctr.onRegionCodeClick()
										},
										allowBlank : false,
										triggerClass : 'x-form-search-trigger', // 按钮样式
										fieldLabel : '网格地址：',
										width : 250,
										renderTo : Ext.get("div_regionCode"
												+ this.idPostfix)
									});
							this.regionCode.regex = /(^\S+)/;
							this.regionCode.regexText = "前面不能有空格字符";
						}
						this.regionCode.validate();
						continue;
					}
					if (fid == "birthday") {
						var field = new Ext.form.DateField({
									name : fid,
									width : 150,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '请选择日期',
									allowBlank : false,
									renderTo : Ext.get("div_" + fid
											+ this.idPostfix)
								});
						this.birthday = field;
						this.birthday.validate();
						continue;
					}
					var field = new Ext.form.DateField({
								name : fid,
								width : 100,
								altFormats : 'Y-m-d',
								format : 'Y-m-d',
								emptyText : '请选择日期',
								allowBlank : true,
								renderTo : Ext.get("div_" + fid
										+ this.idPostfix)
							});
					eval("this." + fid + "=field;");
				}
				var me = this;
				this.birthday.on("change", this.queryInfoFilled, this);
			},
			validateString : function(maxLength, notNull, alias, obj, me) {// 处理string类型字段的校验
				var fv = obj.value;
				var fvLen = me.getStrSize(fv);
				if (fvLen > maxLength) {
					// obj.value="";
					me.addClass(obj, "x-form-invalid")
					obj.title = alias + "中输入的字符串超出定义的最大长度（" + maxLength + "）";
					return;
				} else {
					obj.title = alias;
					me.removeClass(obj, "x-form-invalid");
				}
				if (notNull) {
					if (fv == "") {
						obj.title = alias + "为必填项";
						me.addClass(obj, "x-form-invalid");
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
					}
				}
				if (obj.id == "idCard" + this.idPostfix) {
					me.onIdCardBlur(obj, me);
				}
				if (obj.id == "cardNo" + this.idPostfix) {
					me.onCardNoFilled(obj, me);
				}
				if (obj.id == "personName" + this.idPostfix) {
					me.queryInfoFilled();
				}
			},
			dicFldValidateClick : function(fldId, alias, obj, me) {
				var fv = me.getHtmlFldValue(fldId);
				var divId = "div_" + fldId + this.idPostfix;
				var fdiv = document.getElementById(divId);
				if (fv && fv.length > 0) {
					if (fdiv) {
						this.removeClass(fdiv, "x-form-invalid");
						fdiv.title = "";
					}
				} else {
					if (fdiv) {
						this.addClass(fdiv, "x-form-invalid");
						fdiv.title = alias + "为必选项!"
					}
				}
				if (obj.name == "sexCode") {
					me.queryInfoFilled();
				}
			},
			onCardNoFilled : function(obj, me) {
				var value = obj.value;
				if (value.trim().length == 0) {
					return;
				}
				if (this.queryInfo) {
					if (this.queryInfo["cardNo"] == value) {
						return;
					}
				}
				this.queryInfo["cardNo"] = value;
				var queryData = {
					cardNo : value
				};
				this.queryBy = "cardNo";
				this.doQueryy(queryData);

			},
			queryInfoFilled : function() {
				var sexCodeValue = this.getHtmlFldValue("sexCode");
				if (sexCodeValue == "") {
					return;
				}
				var personName = document.getElementById("personName"
						+ this.idPostfix);
				var personNameValue = personName.value;
				if (personNameValue.trim().length == 0) {
					return;
				}
				var birthday = this.birthday;
				var birthdayValue = birthday.getValue();
				if (birthdayValue) {
					birthdayValue = birthdayValue.format('Y-m-d')
				}
				if (!birthdayValue) {
					return;
				}
				if (this.queryInfo) {
					if (this.queryInfo["personName"] == personNameValue
							&& this.queryInfo["sexCode"] == sexCodeValue
							&& this.queryInfo["birthday"] == birthdayValue
									.toString()) {

						return;
					}
					this.queryInfo["personName"] = personNameValue;
					this.queryInfo["sexCode"] = sexCodeValue;
					this.queryInfo["birthday"] = birthdayValue.toString();
				}
				var queryData = {};
				this.queryBy = "baseInfo";
				queryData["personName"] = personNameValue;
				queryData["sexCode"] = sexCodeValue;
				queryData["birthday"] = birthdayValue;
				this.doQueryy(queryData);
			},
			getQueryInfoSnap : function() {
				var form = this.form.getForm();
				var snap = {};
				snap["idCard"] = document.getElementById("idCard"
						+ this.idPostfix).value;
				snap["personName"] = document.getElementById("personName"
						+ this.idPostfix).value;
				snap["sexCode"] = this.getHtmlFldValue("sexCode");
				var s = this.birthday.getValue();
				if (s) {
					snap["birthday"] = s.format('Y-m-d');
				}
				return snap;
			},
			needsQuery : function() {
				var snap = this.getQueryInfoSnap();
				if (!this.snap) {
					return true;
				}
				if (snap["idCard"] != ""
						&& snap["idCard"] != this.snap["idCard"]) {
					return true;
				}
				if (snap["personName"] != this.snap["personName"]) {
					return true;
				}
				if ((snap["birthday"] + "") != (this.snap["birthday"] + "")) {
					return true
				}
				if (snap["sexCode"] != this.snap["sexCode"]) {
					return true;
				}
				return false;
			},
			doQueryy : function(queryData) {
				if (!this.needsQuery()) {
					return;
				}
				this.idCard = queryData.idCard;
				queryData["queryBy"] = this.queryBy;
				this.queried = true;
				this.snap = this.getQueryInfoSnap();
				this.form.el.mask("正在查询数据...", "x-mask-loading");
				util.rmi.jsonRequest({
					serviceId : this.queryServiceId,
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
					serviceAction : this.queryServiceActioin,
					method : "execute",
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code == 403) {
						this.processReturnMsg(result.code, result.msg);
						return;
					}
					if (code == 900) {
						if (json.body && json.body.length > 0) {
							var url = json.body[0]["url"];
							this.getWin().close();
							window.open(url);
							return;
						}
					}
					if (code == 750) {
						nameField = document.getElementById("personName"
								+ this.idPostfix);
						Ext.Msg.alert("提示", "身份证号码与名字不匹配!");
						nameField.value = "";
						return;
					}
					var data = json["body"];
					if (!data || data.length == 0)
						return;
					this.dataSource = json.dataSource || "chis";
					if (data.length == 1) {
						// 如果数据是从pix服务器取得,只作为默认值填入。
						if (this.dataSource == "pix") {
							var data1 = {};
							data1["empiId"] = empiId
							this.loadData(data1);
							this.regionCode.enable();// 可编辑
							this.manaDoctorId.setValue("");
							this.manaDoctorId.enable();
							return;
						}
						var empiId = data[0]["empiId"];
						var score = data[0]["score"];
						if (score == 1.0) {// 代表是身份证等查询的，可以确定的
							this.serviceAction = "updatePerson"
							this.fireEvent("gotEmpi", empiId)
							var data1 = {};
							data1["empiId"] = empiId
							this.loadData(data1);
							this.regionCode.enable();// 可编辑
							this.manaDoctorId.setValue("");
							this.manaDoctorId.enable();
						} else {
							this.showDataInSelectView(data)
						}
					} else {
						this.showDataInSelectView(data)
					}
				}, this)
			},
			showDataInSelectView : function(data) {
				var idCardF = document
						.getElementById("idCard" + this.idPostfix);
				var idCard = idCardF.value;
				var IdCardValidate = this.hasClass(idCardF, "x-form-invalid");
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					// 如果身份证填写了人没查询到结果，然后使用基本信息查询到的结果列表中，
					// 带有身份证号的记录将被过滤掉。
					if (this.queryBy == "baseInfo" && idCard.length > 0
							&& IdCardValidate) {
						if (r.idCard && r.idCard.length > 0) {
							continue;
						}
					}
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				if (records.length == 0) {
					return;
				}
				var empiIdSelectView = this.midiModules["grjbxx"];
				if (!empiIdSelectView) {
					var empiIdSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人记录",
								width : 500,
								height : 300
							});
					empiIdSelectView.on("onSelect", function(r) {
								var data1 = {}
								data1["empiId"] = r.get("empiId");
								data["regionCode"] = data[0]["empiId"];
								if (this.dataSource == "pix") {
									this.loadData(data1);
									this.regionCode.enable();// 可编辑
									this.regionCode.setValue("");
									this.manaDoctorId.enable();
									return;
								}
								this.loadData(data1);
							}, this);
				}
				empiIdSelectView.getWin().show();
				var task = new Ext.util.DelayedTask(function() {
							empiIdSelectView.setRecords(records);
						}, this);
				task.delay(100);
			},
			loadData : function(data) {
				if (!data) {
					data = {
						empiId : this.exContext.empiData.empiId
					}
				}
				if (this.id != "family" && data["empiId"]) {
					this.op = "update";
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.loadAction,
							body : data,
							op : this.op,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							var resBody = json.body;
							if (code > 300) {
								this.processReturnMsg(code, msg);
								this.fireEvent("exception", code, msg, resBody);
								return;
							}
							Ext.apply(this.data, resBody);
							if (resBody) {
								if ("family" == this.id
										&& resBody.masterFlag
										&& ("y" == resBody.masterFlag || "y" == resBody.masterFlag.key)) {
									var dom = document
											.getElementsByName("masterFlag");
									var domlength = dom.length;
									for (var i = 0; i < domlength; i++) {
										dom[i].disabled = true;
										if (i == 1) {
											dom[i].checked = "checked";
										}
									}
									var div_masterFlag = document
											.getElementById("div_masterFlag"
													+ this.idPostfix);
									this.removeClass(div_masterFlag,
											"x-form-invalid");
									Ext.Msg.alert("提示", "同一个家庭不能存在两个户主，请先核对")
									this.doQk();
									return;
								}
								if (this.id == "familyc") {
									resBody["masterFlag"] = "n";
								}
								// 当为树状的时候，返回的数据需要组装成：{regionCode：{"key":,"text":}}
								if ("form" == this.mainApp.exContext.areaGridShowType
										&& resBody["regionCode"]) {
									resBody["regionCodeCode"] = resBody["regionCode"].key;
									resBody["regionCode"] = resBody["regionCode"].text;
								}
								this.qkhc();
								this.initFormData(resBody);
								this.snap = this.getQueryInfoSnap();// 防止重复查询，
								if ("update" == this.op) {
									if (!resBody["manaDoctorId"]) {
										this.manaDoctorId.enable();
									} else {
										this.manaDoctorId.disable();
									}
									this.birthday.disable();
									var idCardDom = document
											.getElementById("idCard"
													+ this.idPostfix);
									if (idCardDom && idCardDom.value
											&& idCardDom.value.trim() != "") {
										idCardDom.disabled = true;
									}
									var dom = document
											.getElementsByName("sexCode");
									var domlength = dom.length;
									for (var i = 0; i < domlength; i++) {
										dom[i].disabled = true;
									}
									// 判断按钮不可用，当病人为死亡的情况下
									if ("y" == resBody.deadFlag) {
										var btns1 = this.form.getTopToolbar().items;
										for (var i = 0; i < btns1.length; i++) {
											btns1.item(i).disable();
										}
									}
									if (resBody.regionCode) {
										this.regionCode.disable();
									} else {
										this.flagF = true;
									}
									// this.isDead();
								}
								this.setFieldEnable();
								this.addFieldDataValidateFun();
								if (resBody["empiId"]) {
									this.disableUpdate(resBody["phrId"]);
								}
								//重置网格地址是否可以修改
//								debugger;
//                                if(resBody.manaDoctorId.key==this.mainApp.uid ||
//                                	(this.mainApp.jobId=="chis.14"
//                                	&& this.mainApp.deptId.indexOf("320111017")==-1
//								    && this.mainApp.deptId.indexOf("320111018")==-1
//								    && this.mainApp.deptId.indexOf("320111019")==-1
//								    && this.mainApp.deptId.indexOf("320111020")==-1
//								    && this.mainApp.deptId.indexOf("320111021")==-1
//								    && this.mainApp.deptId.indexOf("320111022")==-1
//								 	)
//                                ){
//                                	this.regionCode.enable();
//                                	this.manaDoctorId.enable();
//                                }
								var btnss = this.form.topToolbar.items.items;
								if (this.mainApp.jobId != 'chis.system'
								    && this.mainApp.jobId != 'chis.14'
								    && this.mainApp.jobId != 'chis.01'
										&& this.mainApp.jobId != 'phis.system'
										&& resBody.manaDoctorId.key != this.mainApp.uid) {
									var btnss = this.form.topToolbar.items.items;
									for (var ii = 0; ii < btnss.length; ii++) {
										if (btnss[ii].cmd == 'save')
											btnss[ii].disable();
									}
								}
								//yx 修复先点了不是自己管理的居民信息再点自己管理的居民信息，保存按钮是灰的bug
								if (this.mainApp.jobId == 'chis.system'
										|| this.mainApp.jobId == 'phis.system'
										|| resBody.manaDoctorId.key == this.mainApp.uid) {
									var btnss = this.form.topToolbar.items.items;
									for (var ii = 0; ii < btnss.length; ii++) {
										if (btnss[ii].cmd == 'save')
											btnss[ii].enable();
									}
								}
								if (this.mainApp.jobId == 'chis.20') {
								var doctorid=this.mainApp.rds
								if(doctorid.indexOf(resBody.manaDoctorId.key)>-1){
									var btnss = this.form.topToolbar.items.items;
									for (var ii = 0; ii < btnss.length; ii++) {
										if (btnss[ii].cmd == 'save')
											btnss[ii].enable();
									}	
								}
								}
							}
						}, this)
			},
			setFieldEnable : function() {
				var len = this.otherDisable.length;
				var me = this;
				for (var i = 0; i < len; i++) {
					var od = this.otherDisable[i];
					var cArr = od.control;
					var type = od.type;
					if (type == "text") {
						var fObj = document.getElementById(od.fld
								+ this.idPostfix);
						if (fObj) {
							me.textOnChange(fObj, cArr, me);
						}
					}
					if (type == "checkbox") {
						for (var j = 0, cLen = cArr.length; j < cLen; j++) {
							var co = cArr[j];
							var key = co.key;
							var fId = od.fld + "_" + key + this.idPostfix;
							var fObj = document.getElementById(fId);
							if (!fObj) {
								continue;
							}
							me.checkOnClick(fObj, co, me);
						}
					}
					if (type == "radio") {
						var fldName = od.fld;
						var fldes = document.getElementsByName(fldName);
						me.radioOnClick(fldName, cArr, me);
					}
				}
			},
			onIdCardBlur : function(fld, me) {
				var idCard = fld.value;
				var data = {};
				data["idCard"] = idCard;
				if (idCard.trim().length == 0) {
					return;
				}
				idCard = idCard.replace(/(^\s*)|(\s*$)/g, "");
				fld.value = idCard;
				if (me.hasClass(fld, "x-form-invalid")) {
					return;
				}
				fld.value = idCard.toUpperCase();
				var info = me.getInfo(idCard);
				if (!info) {
					return;
				}
				var sex = info[1];
				var birthday = info[0];
				me.birthday.setValue(birthday);
				me.setHtmlFldValue("sexCode", sex);
				me.doQuery(data);
			},
			doQuery : function(data) {
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.loadAction,
							body : data,
							op : this.op,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							var resBody = json.body;
							if (code > 300) {
								this.processReturnMsg(code, msg);
								this.fireEvent("exception", code, msg, resBody);
								return;
							}
							Ext.apply(this.data, resBody);
							if (resBody) {
								if (data["idCard"]) {
									this.op = "update";
									if (resBody["regionCode"]) {// 20141215
										this.flagF = true;
									}
								}
								if ("family" == this.id
										&& resBody.masterFlag
										&& ("y" == resBody.masterFlag || "y" == resBody.masterFlag.key)) {
									var dom = document
											.getElementsByName("masterFlag");
									var domlength = dom.length;
									for (var i = 0; i < domlength; i++) {
										dom[i].disabled = true;
										if (i == 1) {
											dom[i].checked = "checked";
										}
									}
									var div_masterFlag = document
											.getElementById("div_masterFlag"
													+ this.idPostfix);
									this.removeClass(div_masterFlag,
											"x-form-invalid");
									Ext.Msg.alert("提示", "同一个家庭不能存在两个户主，请先核对")
									this.doQk();
									return;
								}
								if (this.id == "familyc") {
									resBody["masterFlag"] = "n";
								}
								if ("form" == this.mainApp.exContext.areaGridShowType
										&& resBody["regionCode"]) {
									resBody["regionCodeCode"] = resBody["regionCode"].key;
									resBody["regionCode"] = resBody["regionCode"].text;
								}
								this.initFormData(resBody);
								if (resBody["empiId"]) {
									this.disableUpdate(resBody["phrId"]);
								}
								//重置网格地址是否可以修改
								 if(resBody.manaDoctorId.key==this.mainApp.uid ||
                                this.mainApp.jobId=="chis.14"){
                                	this.regionCode.enable();
                                }

							}
						}, this)
			},
			initHTMLFormData : function(data, schema) {
				if ("family" == this.id
						&& data.masterFlag
						&& ("y" == data.masterFlag || "y" == data.masterFlag.key)) {
					var dom = document.getElementsByName("masterFlag");
					var domlength = dom.length;
					for (var i = 0; i < domlength; i++) {
						dom[i].disabled = true;
						if (i == 1) {
							dom[i].checked = "checked";
						}
					}
					var div_masterFlag = document
							.getElementById("div_masterFlag" + this.idPostfix);
					this.removeClass(div_masterFlag, "x-form-invalid");
					Ext.Msg.alert("提示", "同一个家庭不能存在两个户主，请先核对");
					this.doQk();
					return;
				}
				if (!schema) {
					schema = this.schema;
				}
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				// Ext.apply(this.data, data)
				var items = schema.items
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
						if (it.type == "date" && cfv != "") {
							if (typeof cfv != "string") {
								cfv = Ext.util.Format.date(cfv, 'Y-m-d');
							} else {
								cfv = cfv.substring(0, 10);
							}
						}
						if (!cfv || cfv == '') {
							cfv = this[it.id].defaultValue;
						}
						if ("pycode" == this.mainApp.exContext.areaGridShowType
								&& it.id == 'regionCode') {
							var text = '';
							var key = '';
							if (cfv && cfv != '') {
								text = cfv.text;
								key = cfv.key;
							}
							eval("this." + it.id + ".setValue('" + text
									+ "');this." + it.id + ".validate();this."
									+ it.id + ".selectData.regionCode='" + key
									+ "'");
						} else {
							eval("this." + it.id + ".setValue(cfv);this."
									+ it.id + ".validate();");
						}
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
								if (dvs.length > 0) {
									var div = document.getElementById("div_"
											+ it.id + this.idPostfix);
									if (div) {
										this.removeClass(div, "x-form-invalid");
									}
								}
							}
						} else {
							var f = document.getElementById(it.id
									+ this.idPostfix)
							if (f) {
								var v = data[it.id];
								if (!v && !(v == 0) && !it.defaultValue) {
									v = f.defaultValue || "";
									if (f.defaultValue) {
										// f.style.color = "#999";
									}
								} else if (!v && !(v == 0)) {
									v = it.defaultValue;
									// f.style.color = "#000";// 不是注释文字，改黑色字体
								} else {
									// f.style.color = "#000";// 不是注释文字，改黑色字体
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
				this.resetButtons();
			},
			disableUpdate : function(phrId) {
				var doc = document.getElementById("idCard" + this.idPostfix);
				if (doc && doc.value && doc.value.trim() != "") {
					doc.disabled = true;
				}
				var docs = document.getElementsByName("sexCode");
				for (var i = 0; i < docs.length; i++) {
					docs[i].disabled = true;
				}
				this.birthday.disable();
				this.manaUnitId.disable();
				if (phrId) {
					this.regionCode.disable();
					this.manaDoctorId.disable();
				} else {
					this.regionCode.enable();
					this.manaDoctorId.enable();
				}
				 
			},
			setFamilyValue : function(data) {
				this.familyData = data;
				if (data["cookAirTool"]) {
					this.setHtmlFldValue("shhjCheckCFPFSS",
							data["cookAirTool"].key);
				}
				if (data["fuelType"]) {
					this.setHtmlFldValue("shhjCheckRLLX", data["fuelType"].key);
				}
				if (data["waterSourceCode"]) {
					this.setHtmlFldValue("shhjCheckYS",
							data["waterSourceCode"].key);
				}
				if (data["washroom"]) {
					this.setHtmlFldValue("shhjCheckCS", data["washroom"].key);
				}
				if (data["livestockColumn"]) {
					this.setHtmlFldValue("shhjCheckQCL",
							data["livestockColumn"].key);
				}
				this.manaDoctorId.setValue(data["manaDoctorId"]);
				this.manaUnitId.setValue(data["manaUnitId"]);
				var regionCodeValue = "";
				if ("form" == this.mainApp.exContext.areaGridShowType
						&& data["regionCode"]) {
					this.data["regionCodeCode"] = data["regionCode"].key;
					this.data["regionCode"] = data["regionCode"].text;
					regionCodeValue = data["regionCode"].text;
				}
				if ("pycode" == this.mainApp.exContext.areaGridShowType
						&& data["regionCode"]) {
					this.data["regionCodeCode"] = data["regionCode"].key;
					this.data["regionCode"] = data["regionCode"].text;
					this.regionCode.selectData = {
						"regionCode" : data["regionCode"].key,
						"regionName" : data["regionCode"].text
					}
					regionCodeValue = data["regionCode"].text;
				}
				if ("tree" == this.mainApp.exContext.areaGridShowType
						&& data["regionCode"]) {
					this.data["regionCodeCode"] = data["regionCode"].key;
					this.data["regionCode"] = data["regionCode"].text;
					regionCodeValue = data["regionCode"]
				}
				this.regionCode.setValue(regionCodeValue);
				this.manaUnitId.disable();
				var dom = document.getElementsByName("masterFlag");
				var frmEl = this.form.getEl();
				var domlength = dom.length;
				for (var i = 0; i < domlength; i++) {
					dom[i].disabled = true;
					if (!frmEl.contains(dom[i])) {
						continue;
					}
					if (dom[i].value == "n") {
						dom[i].checked = "checked";
					}
				}
				var div_masterFlag = document.getElementById("div_masterFlag"
						+ this.idPostfix);
				this.removeClass(div_masterFlag, "x-form-invalid");
				this.data["familyId"] = data["familyId"];
				this.regionCode.disable();
				this.manaDoctorId.disable();
			},
			getInfo : function(id) {
				// 根据身份证取 省份,生日，性别
				id = this.checkIdcard(id);
				var fid = id.substring(0, 16), lid = id.substring(17);
				var fdiv = document.getElementById("idCard" + this.idPostfix)
				if (isNaN(fid) || (isNaN(lid) && (lid != "x"))) {
					// Ext.Msg.alert("提示", id);
					if (fdiv) {
						this.addClass(fdiv, "x-form-invalid");
						fdiv.title = id;
					}
					return;
				}
				if (fdiv) {
					this.removeClass(fdiv, "x-form-invalid");
					fdiv.title = "";
				}
				var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2";
				var birthday = new Date(id.slice(6, 10), id.slice(10, 12) - 1,
						id.slice(12, 14));

				return [birthday, sex];
			},
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {

					return "身份证号共有 15 码或18位";
				}
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					return "身份证除最后一位外，必须为数字！";
				}
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy)) {
					return "身份证输入错误！";
				}
				for (var i = 0, ret = 0; i < 17; i++) {
					ret += Ai.charAt(i) * Wi[i];
				}
				Ai += arrVerifyCode[ret %= 11];
				return pId.length == 18 && pId.toLowerCase() != Ai
						? "身份证输入错误！"
						: Ai;
			},
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			},
			onRegionCodeClick : function() {
				if ("family" == this.id) {
					return
				}
				if ("update" == this.op && !this.flagF) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
			},
			onQd : function(data) {
				// 当全网格化的时候并且选择是户主的时候，去数据库查询是否已经存在户主，如果存在的，就不允许保存
				// 应该提示：家庭中已经存在户主:
				if ("full" == this.mainApp.exContext.areaGridType) {
					var data_value = {};
					var r = this.getHtmlFldValue("masterFlag");
					if ("y" == r) {
						data_value["regionCode"] = data.regionCode;
						var ownerName = this.getOwnerName(data_value);
						if (ownerName) {
							Ext.Msg.alert("提示", "家庭中已经存在户主: " + ownerName);
							this.setHtmlFldValue("masterFlag", "n");
						}
					}
				}
				this.regionCode.setValue(data.regionCode_text);
				this.data.regionCodeCode = data.regionCode;
				this.data.regionCode = data.regionCode_text;
				this.getRegionInfo(this.regionCode);
			},
			changeManaUnit1 : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				this.regionCode.setValue({
							key : node.attributes["key"],
							text : node.attributes["text"]
						});
				this.regionCode_value = node.attributes["text"];
				// 当全网格化的时候并且选择是户主的时候，去数据库查询是否已经存在户主，如果存在的，就不允许保存
				// 应该提示：家庭中已经存在户主:
				if ("full" == this.mainApp.exContext.areaGridType) {
					var data_value = {};
					var r = this.getHtmlFldValue("masterFlag");
					if ("y" == r) {
						data_value["regionCode"] = node.attributes["key"];
						var ownerName = this.getOwnerName(data_value);
						if (ownerName) {
							Ext.Msg.alert("提示", "家庭中已经存在户主: " + ownerName);
							this.setHtmlFldValue("masterFlag", "n");
						}
					}
				}
				this.data.regionCodeCode = node.attributes["key"];
				this.data.regionCode = node.attributes["text"];
				this.getRegionInfo(this.regionCode, node);
			},
			getRegionInfo : function(field, node) {
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.isFamily = true;
					if (field.selectData) {
						this.data.regionCodeCode = field.selectData.regionCode;
						this.data.regionCode = field.selectData.regionName;
					}
				} else if ("tree" == this.mainApp.exContext.areaGridShowType) {
					this.isFamily = node.attributes["isFamily"] == "1"
							? true
							: false;
					this.data.regionCodeCode = field.getValue();
					this.data.regionCode = field.getRawValue();
				} else {
					this.isFamily = true;
				}
				if (!this.data.regionCodeCode) {
					return;
				}
				this.form.el.mask("正在查询,请稍后..");
				util.rmi.jsonRequest({
							serviceId : "chis.healthRecordService",
							serviceAction : "findInfoByRegionCode",
							method : "execute",
							schema : this.entryName,
							body : {
								regionCode : this.data.regionCodeCode
							}
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.getFamilyMess, [null]);
								return;
							}
							var body = json.body;
							if (!body) {
								return;
							}
							if ("full" == this.mainApp.exContext.areaGridType) {
								// ** 设置家庭对应信息 **
								var familyId = body.familyId;
								if (familyId) {
									this.data["familyId"] = familyId;
								} else {
									this.data["familyId"] = null;
								}
							}
							// ** 设置网格地址对应信息 **
							var manaDoctor = body.manaDoctor;
							this.manaDoctorId.setValue(manaDoctor);
							var manageUnits = json.body.manageUnits;
							if (manageUnits && manageUnits.length == 1) {
								this.setManaUnit(manageUnits[0]);
							} else {
								// 该责任医生可能归属的管辖机构编码集合
								this.manageUnits = manageUnits
								this.setManaUnit("");
							}
						}, this)
			},

			getOwnerName : function(data) {
				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "getOwnerNameByRegionCode",
							method : "execute",
							body : data,
							op : this.op
						}, this);
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				var resBody = result.json.body;
				if ("1" == resBody.flag) {
					return resBody.ownerName;
				}
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						});
				// this.manaUnitId=result.json.manageUnit;
				this.setManaUnit(result.json.manageUnit)
			},
			setManaUnit : function(manageUnit) {
				var combox = this.manaUnitId;
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.setValue({
								key : "",
								text : ""
							});
					return;
				}
				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit);
					combox.disable();
				}
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (this.queryInfo) {
					this.queryInfo = {}
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
								var fs = document.getElementsByName(it.id);
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
									f.disabled = false;
								}
							}
						}
					}
				}
				this.initFieldDisable();
				var cfDiv = document.getElementById("deadReason"
						+ this.idPostfix)
				this.removeClass(cfDiv, "x-form-invalid");
				this.setRedLabel(["SWRQ", "SWYY"], false, this);
				this.deadDate.allowBlank = true;
				this.deadDate.validate();
				if (this.id == "familyc") {
					this.disableMasterFlag();
				}
				if (this.familyData) {
					this.setFamilyValue(this.familyData)
				}
				this.fieldValidate(this.schema);
				this.fireEvent("doNew", this.form);
				if (this.initDataId) {
					this.fireEvent("beforeUpdate", this); // **
					// 在数据加载之前做一些初始化操作
				} else {
					this.fireEvent("beforeCreate", this); // ** 在页面新建时做一些初始化操作
				}
			},
			doQk : function() {
				this.data = {}
				this.flag = true
				this.initFormData(this.data);
				this.op = "create";
				this.doNew();
				//yx 非责任医生先点查看，再点新建，保存按钮灰掉不恢复bug
				var btnss = this.form.topToolbar.items.items;
				for (var ii = 0; ii < btnss.length; ii++) {
										if (btnss[ii].cmd == 'save')
											btnss[ii].enable();
									}
				var personFocuse = document.getElementById("personName"
						+ this.idPostfix);
				personFocuse.focus();
			},
			doPrintCheck : function() {
				// alert("健康检查打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
				if (this.data) {
					this.empiIdCheck = this.data.empiId;
				} else {
					return;
				}
				var url = "resources/chis.prints.template.info.print?type=" + 1
						+ "&empiId=" + this.empiIdCheck
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
			doClose : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			doSave : function() {
				this.data.definePhrid=document.getElementById("definePhrid"
						+ this.idPostfix).value;
				var values = this.getFormData();
				if (!values) {
					return;
				}
				// 当没有户主的档案区创建家庭档案的时候
				if (("familyc" == this.id || "family" == this.id) && this.data) {
					values["familyId"] = this.data.familyId;
					if (this.data2 && !values["familyId"]) {
						values["familyId"] = this.data2.familyId || '';
					}
				}
				if (this.data && this.data.empiId) {
					values["empiId"] = this.data.empiId;
				}
				if (this.data && this.data.phrId) {
					values["phrId"] = this.data.phrId;
				}
				if (this.data && this.data.middleId) {
					values["middleId"] = this.data.middleId;
				}
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (this.data && this.data.regionCodeCode) {
						values["regionCode"] = this.data.regionCodeCode;
					}
					if (this.data && this.data.regionCode) {
						values["regionCode_text"] = this.data.regionCode;
					}
				}
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					var regionCodeValue = this.regionCode.getAreaCodeValue();
					var regionCodeText = this.regionCode.getValue();
					values["regionCode"] = regionCodeValue;
				}
				var idCardDom = document.getElementById("idCard"
						+ this.idPostfix);
				if (idCardDom && idCardDom.value && idCardDom.value != "") {
					var id = this.checkIdcard(idCardDom.value);
					var fid = id.substring(0, 16), lid = id.substring(17);
					if (isNaN(fid) || (isNaN(lid) && (lid != "x"))) {
						// Ext.Msg.alert("提示", id);
						if (idCardDom) {
							this.addClass(idCardDom, "x-form-invalid");
							idCardDom.title = id;
						}
						MyMessageTip.msg("提示", id, true);
						idCardDom.focus();
						return;
					}
				}
				Ext.apply(this.data, values);
				if (!this.htmlFormSaveValidate()) {
					return;
				}
				this.onBeforeSave1(values);
			},
			onBeforeSave1 : function(data) {
				var _cfg = this;
				if (data["cardNo"] && this.data && this.data.empiId) {// 通过卡号调出信息：更新保存
					data["cardNo"] = "";
					this.saveToServer(data);
				} else if (data["cardNo"] && "create" == this.op) {// 有卡号情况下，新建保存
					Ext.Msg.show({
								title : '提示',
								msg : '是否将新卡纳入管理?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										_cfg.showCardForm(data);
									} else {
										// 移除卡号
										data["cardNo"] = "";
										_cfg.saveToServer(data);
									}
								},
								scope : this
							});
				} else {// 新建保存
					this.saveToServer(data);
				}
			},
			showCardForm : function(data) {
				var cardNo = data["cardNo"];
				this.cardNo = cardNo;
				var createView = this.midiModules["cardForm"];
				if (!createView) {
					$import("chis.application.cdh.script.base.CardForm");
					createView = new chis.application.cdh.script.base.CardForm(
							{
								cardNo : cardNo,
								autoLoadData : false,
								autoLoadSchema : false,
								entryName : "chis.application.mpi.schemas.MPI_Card",
								isCombined : false,
								mainApp : this.mainApp,
								title : "新建卡"
							});
					this.midiModules["cardForm"] = createView;
					createView.on("saveCard", function(values) {
						if (values) {
							this.card = values;
							this.fireEvent("saveData");
							var cardinfo = {};
							cardinfo["cardTypeCode"] = values["cardTypeCode"];
							cardinfo["cardNo"] = values["cardNo"];
							var card = [];
							card.push(cardinfo);
							data["cards"] = card;
							util.rmi.jsonRequest({// 判断是否存在同类型的相同的卡号
								serviceId : "chis.basicPersonalInformationService",
								serviceAction : "selectCardNo",
								method : "execute",
								body : values
							}, function(code, msg, json) {
								if (json.body) {
									if (json.body.flag == 1) {
										Ext.Msg.alert("提示", " 该类型的卡号已存在!");
										return;
									}
								}
							}, this)// jsonRequest
							this.saveToServer(data);
						}
					}, this);
					createView.on("close", function() {
								this.cardChecked = false;
							}, this);
				}
				createView.cardNo = cardNo;
				var form = createView.initPanel();
				var win = createView.getWin();
				win.add(form)
				win.minimizable = false;
				win.maximizable = false;
				win.show();
			},
			saveToServer : function(saveData) {
				//alert(saveData.definePhrid)
				// 判断，如果创建家庭档案的时候，该信息本来就是别的家庭成员的，就要提示
				if ("familyc" == this.id || "family" == this.id) {
					var msgV = this.checkInfo(saveData);
					if ("该人员已经有家庭档案!" == msgV) {// 本身是户主除外
						if(this.ownerName){
							if(this.familyAddr){
								Ext.Msg.alert("提示", "该人员已经有户主为： "+this.ownerName+"，家庭地址为："+this.familyAddr+" 的家庭档案");
							}else{
								Ext.Msg.alert("提示", "该人员已经有户主为 "+this.ownerName+" 的家庭档案");
							}
							this.ownerName=null;
						}else{
							Ext.Msg.alert("提示", msgV);
						}
						return;
					}
				}
				// 判断 如果添加成员的时候，该成员是本身也是户主，就要返回提示
				if ("family" == this.id && "y" == saveData["masterFlag"]) {
					Ext.Msg.alert("提示", "同一个家庭不能存在两个户主，请先核对")
					return;
				}
				if ("familyc" == this.id) {
					saveData["masterFlag"] = "y";
					saveData["flagFamilyId"] = "1";
				}
				if (!this.htmlFormSaveValidate()) {
					return;
				}
				var saveRequest = this.getSaveRequest(saveData); // **
				// 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							schema : this.Schema,
							op : this.op,
							body : saveRequest,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code == 500
									&& (msg == "The contact should be provided, when card and certificate is null." || msg == "IdCard and cardNo must have one")) {
								Ext.Msg.alert("提示", "保存失败，卡号或身份证号必须至少填写其中一个。");
								return;
							}
							this.saving = false;
							var resBody = json.body;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData], resBody);
								this
										.fireEvent("exception", code, msg,
												saveData);
								return;
							}

							if (this.id == "familyc") {
								// 向入口为家庭健康档案管理那个模块传递相关信息：基本信息（家庭健康档案）
								var ll = {};
								if (resBody["manaDoctorId"]) {
									ll["manaDoctorId"] = resBody["manaDoctorId"]["key"];
								} else {
									ll["manaDoctorId"] = saveData["manaDoctorId"];
								}
								ll["empiId"] = resBody["empiId"];
								if (ll) {
									this.getDoctor(ll);
								}
								this.fireEvent("imot1");
								this.fireEvent("save1", this.entryName,
										this.op, json, this.data);
							}
							Ext.apply(saveData, resBody);
							if (resBody) {
								if ("form" == this.mainApp.exContext.areaGridShowType
										&& resBody["regionCode"]) {
									saveData["regionCodeCode"] = resBody["regionCode"].key;
									saveData["regionCode"] = resBody["regionCode"].text;
								}
								this.initFormData(saveData);
								this.regionCode.disable();// 不可编辑
								this.manaDoctorId.disable();
							}
							// 当死亡标志为“是”，判断是否需要注销个人所有的档案
							if (!this.DeadFlag1) {
								this.checkDeadFlag(saveData);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.fireEvent("chisSave");
							this.afterSaveData(this.entryName, this.op, json,
									this.data);
							this.op = "update";
							if ("familyc" == this.id || "family" == this.id) {
								if (saveData["deadFlag"] != "y") {
									this.doClose()
								}
							}
						}, this)
			},
			checkDeadFlag : function(saveData) {
				var deadFlag = saveData["deadFlag"];
				var phrId = saveData["phrId"];
				var r = document.getElementById("deadReason" + this.idPostfix).value;
				if (deadFlag == 'y' && phrId != null) {
					Ext.Msg.show({
						title : '提示',
						msg : '死亡标志为"是"将注销用户所有档案 ，是否继续',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.basicPersonalInformationService",
									serviceAction : "logoutAllRecords",
									method : "execute",
									body : saveData

								}, this)
								this.DeadFlag1 = true;
								// 按钮不可用
								var btns1 = this.form.getTopToolbar().items;
								for (var i = 0; i < btns1.length; i++) {
									btns1.item(i).disable();
								}
								var task = new Ext.util.DelayedTask(function() {
											this.fireEvent("save2");
										}, this);
								task.delay(800);
								if ("familyc" == this.id || "family" == this.id) {
									this.doClose();
								}
							} else {
								var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.basicPersonalInformationService",
									serviceAction : "cleanDieInfo",
									method : "execute",
									body : saveData

								}, this)
								var task = new Ext.util.DelayedTask(function() {
											this.fireEvent("save2");
										}, this);
								task.delay(800);

								if ("familyc" == this.id || "family" == this.id) {
									this.doClose();
								}

							}
						},
						scope : this
					});
				}
			},
			getDoctor : function(data) {
				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "getDoctorById",
							method : "execute",
							body : data,
							op : this.op
						});
				var resBody = result.json.body;
				if (resBody) {
					var l = this.data;
					Ext.apply(l, resBody);
					this.fireEvent("chuanDi", l);
				}
			},
			checkInfo : function(data) {
				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.basicPersonalInformationService",
							serviceAction : "getInfo",
							method : "execute",
							body : data,
							op : this.op
						}, this)
				if (result.code != 200) {
					if(result.json && result.json.ownerName ){
						this.ownerName=result.json.ownerName;
						this.familyAddr=result.json.familyAddr
					}
					return result.msg;
				}
			},
			disableMasterFlag : function() {
				var mass = document.getElementsByName("masterFlag");
				var frmEl = this.form.getEl();
				for (var i = 0; i < mass.length; i++) {
					var mas = mass[i];
					mas.disabled = true;
					if (!frmEl.contains(mas)) {
						continue;
					}
					if (mas.value == "y") {
						mas.checked = "checked";
					}
				}
				var div_masterFlag = document.getElementById("div_masterFlag"
						+ this.idPostfix);
				this.removeClass(div_masterFlag, "x-form-invalid");
			},
			getHTMLFormData : function(schema) {
				if (!schema) {
					schema = this.schema;
				}
				// 取表单数据
				if (!schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = schema.items;
				var n = items.length
				var frmEl = this.form.getEl();
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (this.op == "create" && !ac.canCreate(it.acValue)) {
						continue;
					}
					// 从内存中取
					var v = this.data[it.id];
					if (v == undefined) {
						v = it.defaultValue;
					}
					if (v != null && typeof v == "object") {
						v = v.key;
					}
					// 从页面上取
					if (this.isCreateField(it.id)) {
						v = eval("this." + it.id + ".getValue()");
						values[it.id] = v;
					} else {
						if (it.dic) {
							var fs = document.getElementsByName(it.id);
							if (!fs) {
								continue;
							}
							var vs = [];
							if (fs && fs.length > 0) {
								for (var j = 0, len = fs.length; j < len; j++) {
									var f = fs[j];
									if (frmEl.contains(f)) {
										if (f.type == "checkbox"
												|| f.type == "radio") {
											if (f.checked) {
												vs.push(f.value);
											}
										} else if (f.type == "hidden") {
											vs.push(f.value || '');
										}
									}
								}
							}
							if (vs.length > 1) {
								v = vs.join(',') || ''
							} else {
								v = vs[0] || ''
							}
							values[it.id] = v;
						} else {
							var f = document.getElementById(it.id
									+ this.idPostfix)
							if (f) {
								v = f.value || f.defaultValue || '';
								if (v == f.defaultValue && f.type != "hidden") {
									v = '';
								}
								values[it.id] = v;
							}
						}
					}
					if (it.id == 'regionCode') {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							v = this.regionCode.getAreaCodeValue();
						}
					}
					if (it.id == 'regionCode_text') {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							v = this.regionCode.getValue();
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if ("regionCode" == it.id) {
							v = this.data.regionCodeCode;
						}
						if ("regionCode_text" == it.id) {
							v = this.data.regionCode;
						}
					}
					if (v == null || v === "") {
						if (!(it.pkey)
								&& (it["not-null"] == "1" || it['not-null'] == "true")
								&& !it.ref) {
							if (eval("this." + it.id)) {
								eval("this." + it.id + ".focus(true,200)");
							} else if (it.dic) {
								var divId = "div_" + it.id + this.idPostfix;
								var div = document.getElementById(divId);
								if (document.getElementsByName(it.id)[0]) {
									document.getElementsByName(it.id)[0]
											.focus();
								}
							} else {
								if (document.getElementById(it.id
										+ this.idPostfix)) {
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
								}
							}
							MyMessageTip.msg("提示", it.alias + "为必填项", true);
							return;
						}
					}
				}
				return values;
			},
		doChangeIdCard:function(){
			var idcard=document.getElementById("idCard"+ this.idPostfix).value;
			var check=this.checkIdcard(idcard);
			if(idcard!=check){
				Ext.MessageBox.prompt("提示","请输入身份证号",function(btn,value) {
					if(btn=="ok"){
						value=value.toUpperCase();
						if(value!=this.checkIdcard(value)){
							MyMessageTip.msg("提示", "输入的身份证号不正确！", true);
						}else{
							var body={};
							body.empiId=this.data.empiId;
							body.idCard=value;
							var result = util.rmi.miniJsonRequestSync({
								serviceId : this.saveServiceId,
								serviceAction : "changeIdCard",
								method:"execute",
								body : body
							})
							if(result.code>300){
								MyMessageTip.msg("提示", result.msg, true);
							}else{
								document.getElementById("idCard"+ this.idPostfix).value=value;
							}
						}
					}
   				},this,false,idcard);
			}else{
				MyMessageTip.msg("提示", "原身份证号正确，不需要变更！", true);
			}
		},
		qkhc : function() {
			                   //医疗付款方式
			                   document.getElementById("insuranceCode_01"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_02"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_04"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_05"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_06"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_07"+ this.idPostfix).checked=false;
							   document.getElementById("insuranceCode_99"+ this.idPostfix).checked=false;

                               //药物过敏史
							   document.getElementById("diseasetext_check_gm_0101"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_gm_0102"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_gm_0103"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_gm_0104"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_gm_0109"+ this.idPostfix).checked=false;
                               //暴 露 史
							   document.getElementById("diseasetext_check_bl_1201"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_bl_1202"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_bl_1203"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_bl_1204"+ this.idPostfix).checked=false;
                               //疾病
							   document.getElementById("diseasetext_radio_jb_0201"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_radio_jb_02"+ this.idPostfix).checked=false;
                               document.getElementById("diseasetext_check_jb_0202"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0203"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0204"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0205"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0206"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0207"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0208"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0209"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0210"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0212"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0298"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_jb_0299"+ this.idPostfix).checked=false;
							   //家族史
							   //父亲
						       document.getElementById("diseasetext_check_fq_0701"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0702"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0703"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0704"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0705"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0706"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0707"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0708"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0709"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0710"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0711"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetext_check_fq_0799"+ this.idPostfix).checked=false;
							   //母亲
							   document.getElementById("diseasetextCheckMQ_0801"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0802"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0803"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0804"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0805"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0806"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0807"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0808"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0809"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0810"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0811"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckMQ_0899"+ this.idPostfix).checked=false;
							   //兄弟姐妹
							   document.getElementById("diseasetextCheckXDJM_0901"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0902"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0903"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0904"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0905"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0906"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0907"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0908"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0909"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0910"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0911"+ this.idPostfix).checked=false;
							   document.getElementById("diseasetextCheckXDJM_0999"+ this.idPostfix).checked=false;
							   //子 女
							    document.getElementById("diseasetextCheckZN_1001"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1002"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckZN_1003"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckZN_1004"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckZN_1005"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckZN_1006"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckZN_1007"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1008"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1009"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1010"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1011"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckZN_1099"+ this.idPostfix).checked=false;
							    //残疾情况
							    document.getElementById("diseasetextCheckCJ_1101"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckCJ_1102"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckCJ_1103"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckCJ_1104"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckCJ_1105"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckCJ_1106"+ this.idPostfix).checked=false;
								document.getElementById("diseasetextCheckCJ_1107"+ this.idPostfix).checked=false;
							    document.getElementById("diseasetextCheckCJ_1199"+ this.idPostfix).checked=false;
		}
	});