$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.RecordManageYearConfigForm")
$styleSheet("chis.css.diabetesConfig")
chis.application.conf.script.mdc.DiabetesConfigManageForm = function(cfg) {
	cfg.fldDefaultWidth = 160
	cfg.labelWidth = 120
	this.groupName = "管理年度及生成方式"
	chis.application.conf.script.mdc.DiabetesConfigManageForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this)
	this.on("save", this.onSave, this)
}
function IsNum(e) {
	var k = window.event ? e.keyCode : e.which;
	if (((k >= 48) && (k <= 57)) || k == 8 || k == 0 || k == 46) {
	} else {
		if (window.event) {
			window.event.returnValue = false;
		} else {
			e.preventDefault();
		}
	}
}
Ext.extend(chis.application.conf.script.mdc.DiabetesConfigManageForm,
		chis.application.conf.script.RecordManageYearConfigForm, {
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

					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					if (it.id == "configType") {
						var panel = new Ext.Panel({
									border : false,
									colspan : 3,
									html : this.getAddHtml(),
									frame : false,
									autoScroll : false,
									width : 800,
									height : 220
								});
						this.addPanel = panel;
						f = panel;
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
				Ext.apply(table, cfg);
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			getAddHtml : function() {
				this.addPanelId = "_" + this.generateMixed(6);
				var html = '<table width="670" style="border: #ccc solid 1px;text-align:center" class="configType">'
						+ '<tr><td width="150">病例种类</td><td width="130">血糖</td>'
						+ '<td width="280">血糖值（mmol/L）</td><td width="90">组别</td>'
						+ '</tr><tr><td rowspan="4"><table width="150"  class="configType_in">'
						+ '<tr><td>1型糖尿病</td></tr>'
						+ '<tr><td>2型糖尿病</td></tr>'
						+ '<tr><td>营养不良型</td></tr>'
						+ '</table></td><td>空腹血糖</td><td>'
						+ '<input type="text"  id="DBS_bloodNum11'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/>'
						+ ' ~ <input type="text" id="DBS_bloodNum12'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/></td>'
						+ '<td>一组</td></tr><tr>'
						+ '<td>餐后血糖</td>'
						+ '<td><input type="text" id="DBS_bloodNum21'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/>'
						+ '  ~ <input type="text" id="DBS_bloodNum22'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/></td>'
						+ '<td>一组</td></tr><tr>'
						+ '<td>空腹血糖</td>'
						+ '<td><input type="text" id="DBS_bloodNum31'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/>'
						+ '  ~ <input type="text" id="DBS_bloodNum32'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/></td>'
						+ '<td>二组</td></tr><tr>'
						+ '<td>餐后血糖</td>'
						+ '<td><input type="text" id="DBS_bloodNum41'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/>'
						+ '  ~ <input type="text" id="DBS_bloodNum42'
						+ this.addPanelId
						+ '" onkeypress="return IsNum(event)" style="width:80px;"/></td>'
						+ '<td>二组</td></tr><tr>'
						+ '<td rowspan="2"><table width="150" class="configType_in"><tr>'
						+ '<td>IFG</td></tr><tr>'
						+ '<td>IGT</td></tr><tr>'
						+ '<td>其他</td></tr>'
						+ '</table></td><td>&nbsp;</td><td>&nbsp;</td>'
						+ '<td>三组</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td>'
						+ '<td>三组</td></tr></table>';
				return html;
			},
			getFormData : function() {
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = this.data[it.id] || it.defaultValue
						if (v != null && typeof v == "object") {
							v = v.key
						}
						if (it.id == "configType") {
							var htmlData = this.getHtmlData();
							values[it.id] = htmlData;
							continue;
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if (it.dic) {
								values[it.id + "_text"] = f.getRawValue();
							}
						}
						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示信息", it.alias + "不能为空")
								return;
							}
						}
						if (typeof v == "object") {
							values[it.id] = v.key
						} else {
							values[it.id] = v
						}
						if (it.type == "date" && values[it.id]
								&& values[it.id] != it.defaultValue)
							values[it.id] = values[it.id].format("Y-m-d");
					}
				}
				return values;
			},
			getHtmlData : function() {
				var htmlData = {};
				var textFields = ["DBS_bloodNum11", "DBS_bloodNum12",
						"DBS_bloodNum21", "DBS_bloodNum22", "DBS_bloodNum31",
						"DBS_bloodNum32", "DBS_bloodNum41", "DBS_bloodNum42"];
				for (var i = 0; i < textFields.length; i++) {
					var tf = textFields[i];
					var tfDoc = document.getElementById(tf + this.addPanelId);
					if (tfDoc && tfDoc.value && tfDoc.value != "") {
						htmlData[tf] = tfDoc.value;
					}
				}
				return htmlData;
			},
			setHtmlData : function(data) {
				var textFields = ["DBS_bloodNum11", "DBS_bloodNum12",
						"DBS_bloodNum21", "DBS_bloodNum22", "DBS_bloodNum31",
						"DBS_bloodNum32", "DBS_bloodNum41", "DBS_bloodNum42"];
				for (var i = 0; i < textFields.length; i++) {
					var tf = textFields[i];
					var tfDoc = document.getElementById(tf + this.addPanelId);
					if (tfDoc && data) {
						tfDoc.value = data[tf];
					}
				}
				var manageType = this.form.getForm().findField("manageType");
				this.onManageTypeChange(manageType, null, null, true);
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
					if (it.id == "configType") {
						this.setHtmlData(data[it.id]);
						continue;
					}
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id];
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
			onBeforeSave : function(entry, op, data) {
				var planType1 = data.planType1;
				var planType2 = planType2;
				var planType3 = planType3;
				if (data.planMode == 1) {
					if (planType1 == planType2 || planType1 == planType3
							|| planType2 && planType2 != ""
							&& planType2 == planType3) {
						Ext.MessageBox.alert("提示", "计划类型重复！")
						return false;
					}
					if (planType1 == 11 || planType3 == 11 || planType2 == 11) {
						Ext.MessageBox.alert("提示", "按随访结果时，其他计划类型不能配置为2周1次！")
						return false;
					}
				}
				return this.checkHasBloodNum();
			},

			onSave : function(entryName, op, json, data) {
				var planMode = data.planMode;
				this.mainApp.exContext.diabetesMode = planMode;
			},

			changePlanMode : function(field, record, index, isloadData) {
				var value = field.getValue();
				var form = this.form.getForm();
				var items = this.schema.items
				var size = items.length;
				var manageType = this.form.getForm().findField("manageType");
				var manageTypeValue = manageType.getValue();
				if (value == '2') {
					for (var i = 0; i < size; i++) {
						var it = items[i];
						if (it.id == "configType") {
							this.disableConfigType(true);
							continue;
						}
						if (it.group != this.groupName) {
							var field = form.findField(it.id);
							if (!isloadData) {
								field.reset();
								field.disable();
							}
							field.allowBlank = true;
							it["not-null"] = false;
							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>"
									+ it.alias + ":</span>";
						}
					}
				} else {
					for (var i = 0; i < size; i++) {
						var it = items[i];
						if (it.id == "configType") {
							if (manageTypeValue == "2") {
								this.disableConfigType(false);
							} else {
								this.disableConfigType(true);
							}
							continue;
						}
						if (it.group != this.groupName) {
							var field = form.findField(it.id);
							if (manageTypeValue == "1"
									&& (it.id == "planType2" || it.id == "planType3")) {
								continue;
							}
							it["not-null"] = true;
							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>"
									+ it.alias + ":</span>";
							field.allowBlank = false;
							if (!isloadData) {
								field.enable();
							}
						}
					}
				}
				this.validate();
			},

			onReady : function() {
				chis.application.conf.script.mdc.DiabetesConfigManageForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("planType1").on("select", this.onPlanTypeChange,
						this);
				form.findField("planType1").on("change", this.onPlanTypeChange,
						this);
				form.findField("planType1").on("blur", this.onPlanTypeChange,
						this);
				form.findField("planType2").on("select", this.onPlanTypeChange,
						this);
				form.findField("planType2").on("change", this.onPlanTypeChange,
						this);
				form.findField("planType2").on("blur", this.onPlanTypeChange,
						this);
				form.findField("planType3").on("select", this.onPlanTypeChange,
						this);
				form.findField("planType3").on("change", this.onPlanTypeChange,
						this);
				form.findField("planType3").on("blur", this.onPlanTypeChange,
						this);
				form.findField("manageType").on("select",
						this.onManageTypeChange, this);
			},
			checkHasBloodNum : function() {
				var form = this.form.getForm();
				var manageType = form.findField("manageType");
				var manageTypeValue = manageType.getValue();
				if (manageTypeValue == "1") {
					return true;
				} else {
					var fields = ["DBS_bloodNum11", "DBS_bloodNum12",
							"DBS_bloodNum21", "DBS_bloodNum22",
							"DBS_bloodNum31", "DBS_bloodNum32",
							"DBS_bloodNum41", "DBS_bloodNum42"];
					var flag = true;
					for (var i = 0; i < fields.length; i++) {
						var sf = fields[i];
						var sfDoc = document.getElementById(sf
								+ this.addPanelId);
						var flag = this.hasClass(sfDoc, "x-form-invalid");
						if (!flag) {
							break;
						}
					}
					return flag;
				}
			},
			disableConfigType : function(flag) {
				var fields = ["DBS_bloodNum11", "DBS_bloodNum12",
						"DBS_bloodNum21", "DBS_bloodNum22", "DBS_bloodNum31",
						"DBS_bloodNum32", "DBS_bloodNum41", "DBS_bloodNum42"];
				for (var i = 0; i < fields.length; i++) {
					var sf = fields[i];
					var sfDoc = document.getElementById(sf + this.addPanelId);
					if (flag) {
						sfDoc.value = "";
						sfDoc.title = "";
						this.removeClass(sfDoc, "x-form-invalid");
					} else if (!sfDoc.value || sfDoc.value == "") {
						sfDoc.title = "血糖值为必填项！";
						this.addClass(sfDoc, "x-form-invalid");
					}
					sfDoc.disabled = flag;
				}
			},
			hasClass : function(obj, cls) {// 判断对象obj是否有cls样式
				return obj.className.match(new RegExp('(\\s|^)' + cls
						+ '(\\s|$)'));
			},
			addClass : function(obj, cls) {// 给对象obj增加cls样式
				if (!this.hasClass(obj, cls))
					obj.className += " " + cls;
			},
			removeClass : function(obj, cls) {// 移出对象obj的cls样式
				if (this.hasClass(obj, cls)) {
					var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
					obj.className = obj.className.replace(reg, ' ');
				}
			},
			onManageTypeChange : function(combo) {
				var nValue = combo.getValue();
				var form = this.form.getForm();
				var planType1 = form.findField("planType1");
				var planType2 = form.findField("planType2");
				var planType3 = form.findField("planType3");
				var planMode = form.findField("planMode");
				var planModeValue = planMode.getValue();
				if (planModeValue == "2") {
					this.disableConfigType(true);
					planType1.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'> 糖尿病一组:</span>";
					planType1.allowBlank = true;
					planType2.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>糖尿病二组:</span>";
					planType2.allowBlank = true;
					planType3.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>糖尿病三组:</span>";
					planType3.allowBlank = true;
					planType2.setValue();
					planType3.setValue();
					planType2.disable();
					planType3.disable();
					return;
				}
				if (nValue == "1") {
					this.disableConfigType(true);
					planType1.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>糖尿病一组:</span>";
					planType1.allowBlank = false;
					planType2.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>糖尿病二组:</span>";
					planType2.allowBlank = true;
					planType3.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>糖尿病三组:</span>";
					planType3.allowBlank = true;
					planType2.setValue();
					planType3.setValue();
					planType2.disable();
					planType3.disable();
				} else {
					this.disableConfigType(false);
					planType1.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>糖尿病一组:</span>";
					planType1.allowBlank = false;
					planType2.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>糖尿病二组:</span>";
					planType2.allowBlank = false;
					planType3.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>糖尿病三组:</span>";
					planType3.allowBlank = false;
					planType2.enable();
					planType3.enable();
				}
			},

			onPlanTypeChange : function(combo, record, index) {
				var nValue = combo.getValue();
				if (nValue == "01") {
					Ext.Msg.alert("提示", "糖尿病随访计划周期最小为2周1次");
					combo.setValue("02");
					return;
				}
			}

		});