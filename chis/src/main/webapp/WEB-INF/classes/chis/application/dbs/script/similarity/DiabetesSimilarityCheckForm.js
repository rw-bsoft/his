$package("chis.application.dbs.script.similarity")
$import("util.Accredit", "chis.script.BizTableFormView", "chis.script.EHRView",
		"chis.script.util.widgets.MyMessageTip")

chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm = function(
		cfg) {
	cfg.colCount = 3
	cfg.labelWidth = 140;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = true
	cfg.autoLoadData = true
	cfg.fldDefaultWidth = 122;
	cfg.width = 810;
	cfg.isAutoScroll=true;
	chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm,
		chis.script.BizTableFormView, {
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
					var it = items[i];
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = this.createField(it);
					if (it.id == "clinicSymptom1" || it.id == "clinicSymptom2") {
						f = new Ext.form.Checkbox({
									xtype : "checkbox",
									name : it.id,
									boxLabel : it.alias
								});
					}
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
			onReady : function() {
				chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm.superclass.onReady
						.call(this);
				var form = this.form.getForm()

				var weight = form.findField("weight")
				this.weight = weight
				if (weight) {
					weight.on("keyup", this.onWeightCheck, this)
				}

				var height = form.findField("height")
				this.height = height
				if (height) {
					height.on("keyup", this.onHeightCheck, this)
				}

				var fbsField = form.findField("fbs")
				this.fbsField = fbsField

				var pbsField = form.findField("pbs")
				this.pbsField = pbsField

				var fbs1 = form.findField("fbs1");
				var pbs1 = form.findField("pbs1");
				var fbs2 = form.findField("fbs2");
				var pbs2 = form.findField("pbs2");
				var clinicSymptom1 = form.findField("clinicSymptom1");
				var clinicSymptom2 = form.findField("clinicSymptom2");
				fbs1.on("blur", this.changeResult1, this);
				pbs1.on("blur", this.changeResult1, this);
				clinicSymptom1.on("check", this.changeResult1, this);
				fbs2.on("blur", this.changeResult2, this);
				pbs2.on("blur", this.changeResult2, this);
				clinicSymptom2.on("check", this.changeResult2, this);
			},
			changeResult1 : function(f) {
				var form = this.form.getForm();
				var fbs1F = form.findField("fbs1");
				var pbs1F = form.findField("pbs1");
				var clinicSymptom1 = form.findField("clinicSymptom1");
				var result1F = form.findField("result1");
				if ((fbs1F.getValue() == null || fbs1F.getValue() == "")
						&& (pbs1F.getValue() == null || pbs1F.getValue() == "")) {
					return;
				}
				var fbs1 = parseFloat(fbs1F.getValue() || 0);
				var pbs1 = parseFloat(pbs1F.getValue() || 0);
				if (fbs1 < 6.1 && pbs1 < 7.8) {
					result1F.setValue({
								key : "1",
								text : "正常"
							});
				} else if (fbs1 >= 6.1 && fbs1 <= 6.9 && pbs1 < 7.8) {
					result1F.setValue({
								key : "2",
								text : "IFG"
							});
				} else if (pbs1 >= 7.8 && pbs1 <= 11.0 && fbs1 <= 6.9) {
					result1F.setValue({
								key : "3",
								text : "IGT"
							});
				} else if (clinicSymptom1.getValue() == true) {
					result1F.setValue({
								key : "5",
								text : "糖尿病"
							});
				} else {
					result1F.setValue({
								key : "4",
								text : "疑似糖尿病"
							});
				}
			},
			changeResult2 : function(f) {
				var form = this.form.getForm();
				var fbs2F = form.findField("fbs2");
				var pbs2F = form.findField("pbs2");
				var clinicSymptom2 = form.findField("clinicSymptom2");
				var result2F = form.findField("result2");
				if ((fbs2F.getValue() == null || fbs2F.getValue() == "")
						&& (pbs2F.getValue() == null || pbs2F.getValue() == "")) {
					return;
				}
				var fbs2 = parseFloat(fbs2F.getValue() || 0);
				var pbs2 = parseFloat(pbs2F.getValue() || 0);
				if (fbs2 < 6.1 && pbs2 < 7.8) {
					result2F.setValue({
								key : "1",
								text : "正常"
							});
				} else if (fbs2 >= 6.1 && fbs2 <= 6.9 && pbs2 < 7.8) {
					result2F.setValue({
								key : "2",
								text : "IFG"
							});
				} else if (pbs2 >= 7.8 && pbs2 <= 11.0 && fbs2 <= 6.9) {
					result2F.setValue({
								key : "3",
								text : "IGT"
							});
				} else if (clinicSymptom2.getValue() == true) {
					result2F.setValue({
								key : "5",
								text : "糖尿病"
							});
				} else {
					result2F.setValue({
								key : "4",
								text : "疑似糖尿病"
							});
				}
			},
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
					// alert("身高输入非法")
					this.height.markInvalid("身高必须在130-250之间")
					this.height.focus()
					return
				}
				this.onCalculateBMI()
			},
			onWeightCheck : function() {
				if (this.weight.getValue() == "") {
					return
				}
				if (this.weight.getValue() > 140 || this.weight.getValue() < 20) {
					// alert("身高输入非法")
					this.weight.markInvalid("体重必须在20-140之间")
					this.weight.focus()
					return
				}
				this.onCalculateBMI()
			},
			onCalculateBMI : function() {
				var form = this.form.getForm()
				var bmi = form.findField("bmi")
				if (bmi) {
					var w = this.weight.getValue()
					var h = this.height.getValue()
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2)
					bmi.setValue(b)
				}
			},
			onWinShow : function() {
				this.loadData();
			},
			getLoadRequest : function() {
				return {
					"empiId" : this.exContext.ids["empiId"]
				}
			},
			onLoadData : function(entryName, body) {
				var bts = this.form.getTopToolbar();
				var saveBtn = bts.find("cmd", "save");
				var definiteDiagnosisBtn = bts.find("cmd", "definiteDiagnosis");
				var turnHighRiskBtn = bts.find("cmd", "turnHighRisk");
				var excludeBtn = bts.find("cmd", "exclude");
				if (body.diagnosisType && body.diagnosisType.key == "2") {
					this.changeButtonStatus(saveBtn[0], true);
					this.changeButtonStatus(definiteDiagnosisBtn[0], true);
					this.changeButtonStatus(turnHighRiskBtn[0], true);
					this.changeButtonStatus(excludeBtn[0], true);
				} else {
					this.changeButtonStatus(saveBtn[0], false);
					this.changeButtonStatus(definiteDiagnosisBtn[0], false);
					this.changeButtonStatus(turnHighRiskBtn[0], false);
					this.changeButtonStatus(excludeBtn[0], false);
				}
			},
			saveToServer : function(saveData) {
				saveData.empiId = this.exContext.args.empiId
						|| this.exContext.ids.empiId
				if ((this.fbsField.getValue() == "" && this.pbsField.getValue() == "")) {
					Ext.MessageBox.alert("提示", "空腹血糖餐后血糖必须输入一个")
					return
				}
				var form = this.form.getForm();
				var clinicSymptom1 = form.findField("clinicSymptom1");
				if (clinicSymptom1.getValue() == true) {
					saveData.clinicSymptom1 = "y";
				} else {
					saveData.clinicSymptom1 = "n";
				}
				var clinicSymptom2 = form.findField("clinicSymptom2");
				if (clinicSymptom2.getValue() == true) {
					saveData.clinicSymptom2 = "y";
				} else {
					saveData.clinicSymptom2 = "n";
				}
				if (this.diagnosisType && this.diagnosisType != "") {
					saveData.diagnosisType = this.diagnosisType;
				}
				chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm.superclass.saveToServer
						.call(this, saveData);
				this.fireEvent("chisSave");
			},
			initFormData : function(data) {
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i];
					if (it.id == "clinicSymptom1" || it.id == "clinicSymptom2") {
						var v = data[it.id];
						if (v) {
							v = v.key;
						}
						var f = form.findField(it.id)
						if (v == "y") {
							f.setValue(true);
						} else {
							f.setValue(false);
						}
						continue;
					}
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							if ((it.type == 'date' || it.xtype == 'datefield')
									&& typeof v == 'string' && v.length > 10) {
								v = v.substring(0, 10)
							}
							if ((it.type == 'datetime'
									|| it.type == 'timestamp' || it.xtype == 'datetimefield')
									&& typeof v == 'string' && v.length > 19) {
								v = v.substring(0, 19)
							}
							f.setValue(v)
						}
						if (this.initDataId) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.startValues = form.getValues(true);
				this.resetButtons(); // ** 用于页面按钮权限控制
				this.focusFieldAfter(-1, 800);
			},
			afterSaveData : function(entryName, op, json, data) {
				if ((data.result1 == "5" || data.result2 == "5")
						&& data.diabetesRecord == false) {
					Ext.apply(data, json.body);
					Ext.apply(data, this.data);
					Ext.Msg.show({
								title : '糖尿病建档',
								msg : '是否建立糖尿病档案',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.showEhrViewWin(['D_01', 'D_02',
												'D_03', 'D_05', 'D_04']);
									} else {
										this.DiabetesRecordSave();
									}
								},
								scope : this
							});
				} else {
					if (data.result2 != null && data.result2 != "1") {
						return;
					}
					if (data.result2 == null && data.result1 != null
							&& data.result1 != "1") {
						return;
					}
					if (data.result1 == null && data.result2 == null) {
						return;
					}
					Ext.Msg.show({
								title : '排除糖尿病',
								msg : '该次核实结果为正常，是否排除糖尿病！',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.diagnosisType = "3";
										this.DiabetesRecordSave();
										this
												.fireEvent("save", "", "", {},
														data);
										if (this.win) {
											this.win.hide();
										}
									}
								},
								scope : this
							});

				}
			},
			showEhrViewWin : function(visitModule) {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = visitModule;
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = this.activeTab || 0;
				cfg.needInitFirstPanel = true;
				this.empiId = this.exContext.args.empiId
				var module = this.midiModules["DiabetesRecordView_EHRView"
						+ visitModule[0]];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["DiabetesRecordView_EHRView"
							+ visitModule[0]] = module;
					module.exContext.ids["empiId"] = this.data.empiId;
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.data.empiId;
					module.refresh();
				}
				module.getWin().show();
			},
			DiabetesRecordSave : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesSimilarityService",
							serviceAction : "updateSimilarityType",
							method : "execute",
							similarityId : this.initDataId,
							diagnosisType : this.diagnosisType || "1"
						});
				this.doSave();
			},
			doDefiniteDiagnosis : function() {
				var form = this.form.getForm();
				var result1F = form.findField("result1");
				var result2F = form.findField("result2");
				var result1 = result1F.getValue();
				var result2 = result2F.getValue();
				if (result2 != null && result2 != "" && result2 != "5") {
					MyMessageTip.msg("提示", "结果为糖尿病才能确诊！", true);
					return;
				}
				if ((result2 == null || result2 == "") && result1 != null
						&& result1 != "" && result1 != "5") {
					MyMessageTip.msg("提示", "结果为糖尿病才能确诊！", true);
					return;
				}
				if ((result1 == null || result1 == "")
						&& (result2 == null || result2 == "")) {
					MyMessageTip.msg("提示", "结果为糖尿病才能确诊！", true);
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesSimilarityService",
							serviceAction : "getHasDiabetesRecord",
							method : "execute",
							empiId : this.data.empiId
									|| this.exContext.ids.empiId
						});
				var data = {
					diabetesRecord : result.json.diabetesRecord,
					result1 : "5"
				};
				this.diagnosisType = "1";
				if (result.json.diabetesRecord == true) {
					this.DiabetesRecordSave();
				} else {
					this.afterSaveData("", "", {}, data);
				}
				this.TurnHighRisk();
				this.fireEvent("save", "", "", {}, data);
				if (this.win) {
					this.win.hide();
				}
			},
			doExclude : function() {
				var form = this.form.getForm();
				var result1F = form.findField("result1");
				var result2F = form.findField("result2");
				var result1 = result1F.getValue();
				var result2 = result2F.getValue();
				if (result2 != null && result2 != "" && result2 != "1") {
					MyMessageTip.msg("提示", "结果为正常才能排除！", true);
					return;
				}
				if ((result2 == null || result2 == "") && result1 != null
						&& result1 != "" && result1 != "1") {
					MyMessageTip.msg("提示", "结果为正常才能排除！", true);
					return;
				}
				if ((result1 == null || result1 == "")
						&& (result2 == null || result2 == "")) {
					MyMessageTip.msg("提示", "结果为正常才能排除！", true);
					return;
				}
				this.diagnosisType = "3";
				this.DiabetesRecordSave();
				this.fireEvent("save", "", "", {}, data);
				if (this.win) {
					this.win.hide();
				}
			},
			doTurnHighRisk : function() {
				var form = this.form.getForm();
				var result1F = form.findField("result1");
				var result2F = form.findField("result2");
				var result1 = result1F.getValue();
				var result2 = result2F.getValue();
				if ((result2 != null && result2 != "")
						&& (result2 != "2" && result2 != "3")) {
					MyMessageTip.msg("提示", "结果是IFG、IGT才可转高危！", true);
					return;
				}
				if ((result2 == null || result2 == "")
						&& (result1 != null && result1 != "")
						&& (result1 != "2" && result1 != "3")) {
					MyMessageTip.msg("提示", "结果是IFG、IGT才可转高危！", true);
					return;
				}
				result = this.TurnHighRisk(1);
				if (result.json.code > 300) {
					if (result.json.hasHealthRecord) {
						Ext.Msg.show({
									title : '个人健康档案',
									msg : '该病人尚未建立个人健康档案，不能转高危人群管理，是否建立？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.showEhrViewWin(["B_01"]);
										}
									},
									scope : this
								});
					}
					return;
				}
				this.diagnosisType = "4";
				this.DiabetesRecordSave();
				this.fireEvent("save", "", "", {}, data);
				if (this.win) {
					this.win.hide();
				}
			},
			TurnHighRisk : function(type) {
				var form = this.form.getForm();
				var result1F = form.findField("result1");
				var result2F = form.findField("result2");
				var result1 = result1F.getValue();
				var result2 = result2F.getValue();
				var body = {};
				if (result2 != null && result2 != "") {
					body.fbs = form.findField("fbs2").getValue();
					body.pbs = form.findField("pbs2").getValue();
					body.result = form.findField("result2").getValue();
					body.checkUser = form.findField("checkUser2").getValue();
					body.checkDate = form.findField("checkDate2").getValue();
					var clinicSymptom2 = form.findField("clinicSymptom2");
					if (clinicSymptom2.getValue() == true) {
						body.clinicSymptom = "y";
					} else {
						body.clinicSymptom = "n";
					}
				} else if (result1 != null && result1 != "") {
					body.fbs = form.findField("fbs1").getValue();
					body.pbs = form.findField("pbs1").getValue();
					body.result = form.findField("result1").getValue();
					body.checkUser = form.findField("checkUser1").getValue();
					body.checkDate = form.findField("checkDate1").getValue();
					var clinicSymptom1 = form.findField("clinicSymptom1");
					if (clinicSymptom1.getValue() == true) {
						body.clinicSymptom = "y";
					} else {
						body.clinicSymptom = "n";
					}
				} else if (type != null) {
					MyMessageTip.msg("提示", "结果是IFG、IGT才可转高危！", true);
					return;
				}
				body.riskiness = form.findField("riskiness").getValue();
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesSimilarityService",
							serviceAction : "saveTurnHighRisk",
							method : "execute",
							similarityId : this.initDataId,
							empiId : this.data.empiId
									|| this.exContext.ids.empiId,
							body : body
						});
				return result
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent,
					labelSeparator : ":"
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					scope : this
				}
				if (it.onblur) {
					var func = eval("this." + it.onblur)
					if (typeof func == 'function') {
						Ext.apply(cfg.listeners, {
									blur : func
								})
					}
				}
				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = (it.editable == "true") ? true : false
				}
				if (it['not-null'] == "1" || it['not-null'] == "true"
						|| it.id == "height" || it.id == "weight") {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.regex = /(^\S+)/
					cfg.regexText = "前面不能有空格字符"
				}
				if (it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					cfg.disabled = true
				}
				if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}
					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					var combox = this.createDicField(it.dic)
					this.changeFieldCfg(it, cfg);
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.properties && it.properties.mode == "remote") {
					var f = this.createRemoteDicField(it);
					return f;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				if (typeof(it.minValue) != 'undefined') {
					cfg.minValue = it.minValue;
				}
				if (it.xtype) {
					if (it.xtype == "htmleditor") {
						cfg.height = it.height || 200;
					}
					if (it.xtype == "textarea") {
						cfg.height = it.height || 65
					}
					if (it.xtype == "datefield"
							&& (it.type == "datetime" || it.type == "timestamp")) {
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
					}
					this.changeFieldCfg(it, cfg);
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
						if (it.maxValue && typeof it.maxValue == 'string'
								&& it.maxValue.length > 10) {
							cfg.maxValue = it.maxValue.substring(0, 10);
						}
						if (it.minValue && typeof it.minValue == 'string'
								&& it.minValue.length > 10) {
							cfg.minValue = it.minValue.substring(0, 10);
						}
						break;
					case 'datetime' :
						cfg.xtype = 'datetimefield'
						cfg.emptyText = "请选择日期时间"
						cfg.format = 'Y-m-d H:i:s'
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						cfg.height = 180
						break;
				}
				this.changeFieldCfg(it, cfg);
				return cfg;
			}

		});