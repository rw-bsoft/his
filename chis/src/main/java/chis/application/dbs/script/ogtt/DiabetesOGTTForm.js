$package("chis.application.dbs.script.ogtt")
$import("util.Accredit", "chis.script.BizFieldSetFormView",
		"chis.script.util.helper.Helper")
chis.application.dbs.script.ogtt.DiabetesOGTTForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.showButtonOnTop = true;
	cfg.isAutoScroll = true;
	this.labelWidth = 120;
	chis.application.dbs.script.ogtt.DiabetesOGTTForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onChangeButton, this);
	this.on("beforeCreate", this.onChangeButton, this);
}
Ext.extend(chis.application.dbs.script.ogtt.DiabetesOGTTForm,
		chis.script.BizFieldSetFormView, {
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
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = this.createField(it);
					if (it.id == "riskFactors") {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = this.fldDefaultWidth || 200;
						it.dic.vertical = true;
						var cfg = {
							name : it.id,
							fieldLabel : it.alias,
							xtype : it.xtype || "textfield",
							vtype : it.vtype,
							width : this.fldDefaultWidth || 200,
							value : it.defaultValue,
							enableKeyEvents : it.enableKeyEvents,
							validationEvent : it.validationEvent,
							labelSeparator : ":"
						}
						$import("chis.script.util.dictionary.CheckboxDicFactory")
						f = chis.script.util.dictionary.CheckboxDicFactory
								.createDic(it.dic);
						Ext.apply(f, cfg)
					}
					if (it.id == "superDiagnose") {
						f = this.createCombinationField(it)
					}
					if (it.id == "clinicSymptom1" || it.id == "clinicSymptom2"
							|| it.id == "clinicSymptom3") {
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
			getCheckData : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesOGTTService",
							serviceAction : "checkDiabetesRecord",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId
							}
						})
				if (!result.json.body) {
					return {};
				} else {
					return result.json.body
				}
			},
			onChangeButton : function() {
				var checkData = this.getCheckData();
				var bts = this.form.getTopToolbar();
				var checkBtn = bts.find("cmd", "save");
				if (checkBtn && checkBtn[0]) {
					if (checkData
							&& (checkData.dbsCreate == "y" || checkData.status == "1")) {
						checkBtn[0].disable();
					} else {
						checkBtn[0].enable();
					}
				}
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm();
				form.reset();
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (!(arguments[0] == 1)) { // whether set defaultValue,
							// it will be setted when
							// there is no args.
							var dv = it.defaultValue;
							if (dv) {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof dv == 'string'
										&& dv.length > 10) {
									dv = dv.substring(0, 10);
								}
								f.setValue(dv);
							}
						}
						if (!it.update && !it.fixed && !it.evalOnServer) {
							f.enable();
						}
						f.validate();
					}
				}
				this.setKeyReadOnly(false)
				this.startValues = form.getValues(true);
				this.fireEvent("doNew", this.form)
				this.focusFieldAfter(-1, 800)
				this.afterDoNew();
			},
			afterSaveData : function(entryName, op, json, data) {
				if (data.result3 == "5"
						|| (data.result2 == "5" && (data.result3 == "" || data.result3 == null))
						|| (data.result1 == "5"
								&& (data.result2 == "" || data.result2 == null) && (data.result3 == "" || data.result3 == null))) {
					Ext.Msg.show({
								title : '糖尿病建档',
								msg : '是否建立糖尿病档案',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.openEhrView();
									}
								},
								scope : this
							});
				}
			},
			openEhrView : function() {
				this.fireEvent("addModule", "D_01");
			},
			createCombinationField : function(it) {
				this.checkbox = new Ext.form.Checkbox({
							xtype : "checkbox",
							name : it.id,
							hideLabel : true
						});
				this.checkbox.on("check", this.onDbsDiagnoseCheck, this);
				var label = new Ext.form.Label({
							xtype : "label",
							html : it.alias,
							width : 94
						});
				var dic = {};
				dic.id = "chis.dictionary.dbsDiagnose";
				dic.src = this.entryName + "." + "chis.dictionary.dbsDiagnose";
				var cfg = {
					name : it.id,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					hideLabel : true,
					width : this.fldDefaultWidth || 175,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent,
					disabled : true,
					labelSeparator : ":"
				}
				var diagnoseTextField = this.createDicField(dic);
				Ext.apply(diagnoseTextField, cfg);
				this.diagnoseTextField = diagnoseTextField;
				var field = new Ext.form.CompositeField({
							xtype : 'compositefield',
							name : it.id,
							anchor : '-20',
							hideLabel : true,
							index : it.index,
							boxMinWidth : 300,
							items : [this.checkbox, label, diagnoseTextField]
						});
				return field;
			},
			onReady : function() {
				var form = this.form.getForm();
				var fbs1 = form.findField("fbs1");
				var pbs1 = form.findField("pbs1");
				var fbs2 = form.findField("fbs2");
				var pbs2 = form.findField("pbs2");
				var fbs3 = form.findField("fbs3");
				var pbs3 = form.findField("pbs3");
				var clinicSymptom1 = form.findField("clinicSymptom1");
				var clinicSymptom2 = form.findField("clinicSymptom2");
				var clinicSymptom3 = form.findField("clinicSymptom3");
				fbs1.on("blur", this.changeResult1, this);
				pbs1.on("blur", this.changeResult1, this);
				clinicSymptom1.on("check", this.changeResult1, this);
				fbs2.on("blur", this.changeResult2, this);
				pbs2.on("blur", this.changeResult2, this);
				clinicSymptom2.on("check", this.changeResult2, this);
				fbs3.on("blur", this.changeResult3, this);
				pbs3.on("blur", this.changeResult3, this);
				clinicSymptom3.on("check", this.changeResult3, this);
				chis.application.dbs.script.ogtt.DiabetesOGTTForm.superclass.onReady
						.call(this);
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
			changeResult3 : function(f) {
				var form = this.form.getForm();
				var fbs3F = form.findField("fbs3");
				var pbs3F = form.findField("pbs3");
				var clinicSymptom3 = form.findField("clinicSymptom3");
				var result3F = form.findField("result3");
				if ((fbs3F.getValue() == null || fbs3F.getValue() == "")
						&& (pbs3F.getValue() == null || pbs3F.getValue() == "")) {
					return;
				}
				var fbs3 = parseFloat(fbs3F.getValue() || 0);
				var pbs3 = parseFloat(pbs3F.getValue() || 0);
				if (fbs3 < 6.1 && pbs3 < 7.8) {
					result3F.setValue({
								key : "1",
								text : "正常"
							});
				} else if (fbs3 >= 6.1 && fbs3 <= 6.9 && pbs3 < 7.8) {
					result3F.setValue({
								key : "2",
								text : "IFG"
							});
				} else if (pbs3 >= 7.8 && pbs3 <= 11.0 && fbs3 <= 6.9) {
					result3F.setValue({
								key : "3",
								text : "IGT"
							});
				} else if (clinicSymptom3.getValue() == true) {
					result3F.setValue({
								key : "5",
								text : "糖尿病"
							});
				} else {
					result3F.setValue({
								key : "4",
								text : "疑似糖尿病"
							});
				}
			},
			onDbsDiagnoseCheck : function(box, flag) {
				if (this.checkbox.getValue() == true) {
					this.diagnoseTextField.enable();
				} else {
					this.diagnoseTextField.setValue();
					this.diagnoseTextField.disable();
				}
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values.empiId = this.exContext.ids.empiId;
				values.phrId = this.exContext.ids.phrId
				if (this.checkbox.getValue() == true) {
					values.superDiagnose = "y";
					values.superDiagnoseText = this.diagnoseTextField
							.getValue();
				} else {
					values.superDiagnose = "n";
					values.superDiagnoseText = "";
				}
				var form = this.form.getForm();
				var clinicSymptom1 = form.findField("clinicSymptom1");
				if (clinicSymptom1.getValue() == true) {
					values.clinicSymptom1 = "y";
				} else {
					values.clinicSymptom1 = "n";
				}
				var clinicSymptom2 = form.findField("clinicSymptom2");
				if (clinicSymptom2.getValue() == true) {
					values.clinicSymptom2 = "y";
				} else {
					values.clinicSymptom2 = "n";
				}
				var clinicSymptom3 = form.findField("clinicSymptom3");
				if (clinicSymptom3.getValue() == true) {
					values.clinicSymptom3 = "y";
				} else {
					values.clinicSymptom3 = "n";
				}
				values.planId = this.planId;
				values.planDate = this.planDate;
				Ext.apply(this.data, values);
				this.saveToServer(values)
			},
			initRiskFactors : function(riskFactors) {
				var form = this.form.getForm()
				var riskFactorsF = form.findField("riskFactors");
				if (riskFactorsF) {
					riskFactorsF.setValue(riskFactors);
				}
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
					if (it.id == "superDiagnose") {
						var v = data[it.id];
						if (v) {
							v = v.key;
						}
						if (v == "y") {
							this.checkbox.setValue(true);
						} else {
							this.checkbox.setValue(false);
						}
						continue;
					}
					if (it.id == "superDiagnoseText") {
						var v = data[it.id];
						this.diagnoseTextField.setValue(v);
						continue;
					}
					if (it.id == "clinicSymptom1" || it.id == "clinicSymptom2"
							|| it.id == "clinicSymptom3") {
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
			}
		});