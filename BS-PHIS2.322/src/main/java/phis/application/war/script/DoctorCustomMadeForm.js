$package("phis.application.war.script");

$import("phis.script.TableForm", "org.ext.ux.layout.TableFormLayout","util.widgets.SpinnerField","phis.script.widgets.ModuleQueryField")

phis.application.war.script.DoctorCustomMadeForm = function(cfg) {
	cfg.colCount = 1;
	cfg.labelWidth = 340;
	cfg.fldDefaultWidth = 100;
	phis.application.war.script.DoctorCustomMadeForm.superclass.constructor.apply(this,
			[cfg]);
	this.valueChange = false;
	this.on("beforeclose", this.onFormBeforeclose, this);
	this.on("winShow", this.loadData, this)
}

Ext.extend(phis.application.war.script.DoctorCustomMadeForm,
		phis.script.TableForm, {
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
				var cfg = {
					labelAlign : "right",
					labelWidth : this.labelWidth || 80,
					iconCls : 'bogus',
					border : false,
					frame : true,
					autoHeight : true,
					width : 550,
					defaultType : 'textfield',
					shadow : true
				}
				this.initBars(cfg);
				this.form = new Ext.FormPanel(cfg)
				this.form.on("afterrender", this.onReady, this)
				var groups = {};
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f;
					if (it.xtype == "moduleQuery") {
						f = this.createModuleQueryField(it);
					} else if (it.xtype == "spinner") {
						f = this.getSpinnerField(it);
					} else {
						f = this.createField(it)
					}
					f.index = i;
					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					var gname = it.group
					if (gname) {
						if (!groups[gname]) {
							var cfg = {}
							cfg.defaultType = 'textfield', cfg.title = it.group
							cfg.autoWidth = true
							cfg.autoHeight = true
							cfg.collapsible = true
							var group = new Ext.form.FieldSet(cfg)
							groups[gname] = group
						}
					}
					groups[gname].add(f)
				}
				for (s in groups) {
					this.form.add(groups[s])
				}
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onFormBeforeclose : function() {
				var BCJG = this.form.form.findField("BCJG");
				var TZYS = this.form.form.findField("TZYS");
				var XSBL = this.form.form.findField("XSBL");
				var sig=0;
				if (this.startData.BCJG != BCJG.getValue()) {
					this.valueChange = true;
					sig=1;
				}else{
					this.valueChange = false;
				}
				this.startData.BCJG = BCJG.getValue();
				if(sig==0){
					if (this.startData.TZYS != TZYS.getValue()&& this.startData.TZYS.key != TZYS.getValue()) {
						this.valueChange = true;
						sig=1;
					}else{
						this.valueChange = false;
					}
				}
				this.startData.TZYS = TZYS.getValue();
				if(sig==0){
					if (this.startData.XSBL != XSBL.getValue()&& this.startData.XSBL.key != XSBL.getValue()) {
						this.valueChange = true;
					}else{
						this.valueChange = false;
					}
				}
				this.startData.XSBL = XSBL.getValue();
				if (this.valueChange) {
					Ext.Msg.show({
						title : '提示',
						msg : '当前页面已被修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.valueChange = false;
										this.saveToServer(this.startData);
										this.win.hide();
									} else {
										this.win.hide();
									}
								},
								scope : this
							})
				} else {
					this.win.hide();
				}
				return false;
			},
			createModuleQueryField : function(it) {
				var defaultWidth = this.fldDefaultWidth || 200
				var field = new phis.script.widgets.ModuleQueryField({
							id : "queryField",
							name : it.id,
							valueField : "key",
							displayField : "text",
							fieldLabel : it.alias,
							xtype : it.xtype || "textfield",
							vtype : it.vtype,
							width : defaultWidth,
							height : this.defaultHeight || it.height,
							value : it.defaultValue,
							enableKeyEvents : it.enableKeyEvents,
							validationEvent : it.validationEvent
						});
				field.on("queryClick", this.onQueryClick, this);
				field.setEditable(false);
				return field;
			},
			onQueryClick : function() {
				var XSBL = this.form.form.findField("XSBL");
				var val = XSBL.getValue();
				var value = this.changeXSBLValue(val);
				var radioForm = this.getRadioForm(value);
				var w = this.getRadioFormWin(radioForm);
				w.show();
			},
			getRadioFormWin : function(radioForm) {
				var w = new Ext.Window({
							id : "setWin",
							title : '缩放设置',
							collapsible : true,
							maximizable : false,
							width : 150,
							height : 240,
							closeAction : "close",
							layout : 'fit',
							plain : true,
							border : false,
							shadow : false,
							shim : true,
							constrain : true,
							modal : true,
							buttonAlign : 'right',
							items : radioForm,
							buttons : [{
								text : '确定',
										handler : this.saveSizeRadio
									}]
						});
				return w;
			},
			changeXSBLValue : function(value) {
				var setedValue;
				var numValue;
				if (value == "200") {
					setedValue = "twoHundred";
				} else if (value == "100") {
					setedValue = "oneHundred";
				} else if (value == "75") {
					setedValue = "seventyFive";
				} else if (value == "-2") {
					setedValue = "width";
				} else if (value == "-1") {
					setedValue = "page";
				} else {
					setedValue = "percent";
					numValue = value;
				}
				return [setedValue, numValue];
			},
			saveSizeRadio : function() {
				var radioForm = Ext.getCmp("radioForm");
				var vs = radioForm.form.getValues(false);
				var radioValue = vs.sizeRadio;
				var setedValue;
				var setedKey;
				if (radioValue == "twoHundred") {
					setedValue = "200";
					setedKey = "200";
				}
				if (radioValue == "oneHundred") {
					setedValue = "100";
					setedKey = "100";
				}
				if (radioValue == "seventyFive") {
					setedValue = "75";
					setedKey = "75";
				}
				if (radioValue == "width") {
					setedValue = "页宽";
					setedKey = "-2";
				}
				if (radioValue == "page") {
					setedValue = "整页";
					setedKey = "-1";
				}
				if (radioValue == "percent") {
					var spn = Ext.getCmp("perspn");
					setedValue = spn.getValue() == null ? null : spn.getValue()
							+ "";
					setedKey = setedValue;
				}
				var queryField = Ext.getCmp("queryField");
				var size = queryField.getValue();
				queryField.setValue({
							"key" : setedKey,
							"text" : setedValue
						});
				MyMessageTip.msg("提示", "该设置会在重新打开病历后生效!", true);
				var setWin = Ext.getCmp("setWin");
				if (setWin) {
					setWin.close();
				}
			},
			getRadioForm : function(XSBL) {
				var radioForm = new Ext.form.FormPanel({
							id : "radioForm",
							labelWidth : 55,
							baseCls : 'x-plain',
							layout : "fit",
							frame : true,
							items : [{
										id : "sizeRadioId",
										xtype : 'fieldset',
										title : '缩放至',
										autoHeight : true,
										defaultType : 'radio',
										items : [{
													id : "twoHundred",
													boxLabel : '200%',
													hideLabel : true,
													name : "sizeRadio",
													inputValue : "twoHundred"
												}, {
													id : "oneHundred",
													boxLabel : '100%',
													hideLabel : true,
													name : "sizeRadio",
													inputValue : "oneHundred"
												}, {
													id : "seventyFive",
													boxLabel : '75%',
													hideLabel : true,
													name : "sizeRadio",
													inputValue : "seventyFive"
												}, {
													id : "width",
													boxLabel : '页宽',
													hideLabel : true,
													name : "sizeRadio",
													inputValue : "width"
												}, {
													id : "page",
													boxLabel : '整页',
													hideLabel : true,
													name : "sizeRadio",
													inputValue : "page"
												},
												this.createCombinationField()]
									}]
						});
				if (XSBL[0]) {
					var field = Ext.getCmp(XSBL[0]);
					if (XSBL[0] == "percent") {
						field.setValue(true);
						var spn = Ext.getCmp("perspn");
						if (XSBL[1]) {
							spn.setValue(XSBL[1]);
						}
					} else {
						field.setValue(true);
					}
				} else {
					var field = Ext.getCmp("oneHundred");
					field.setValue(true);
				}
				return radioForm;
			},

			createCombinationField : function() {
				var radioField = new Ext.form.Radio({
							id : "percent",
							name : "sizeRadio",
							boxLabel : '百分比',
							hideLabel : true,
							inputValue : "percent"
						});
				radioField.on("check", this.checkRadioField, this);
				var spinner = new util.widgets.SpinnerField({
							id : "perspn",
							width : 60,
							minValue : 0,
							disabled : true,
							decimalPrecision : 0,
							allowDecimals : false,
							maxValue : 999
						});
				var field = new Ext.form.CompositeField({
							hideLabel : true,
							items : [radioField, spinner]
						});
				return field;
			},
			checkRadioField : function(field, flag) {
				var spn = Ext.getCmp("perspn");
				if (flag) {
					spn.setValue();
					spn.enable();
				} else {
					spn.setValue();
					spn.disable();
				}
			},

			getSpinnerField : function(it) {
				var defaultWidth = this.fldDefaultWidth || 200;
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					width : defaultWidth
				};
				if (it['not-null']) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it.fixed || it.fixed) {
					cfg.disabled = true
				}
				if (it.defaultValue) {
					cfg.value = it.defaultValue;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.minValue) {
					cfg.minValue = it.minValue;
				} else {
					cfg.minValue = 0;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				if (it.type == 'int') {
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				} else {
					cfg.decimalPrecision = it.precision || 2;
				}
				var numField = new util.widgets.SpinnerField(cfg);
				return numField;
			},
			loadData : function() {
				if (!this.mainApp.uid) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "doctorCustomMadeService",
							serviceAction : "loadPersonalSetInfo",
							schema : this.entryName,
							uid : this.mainApp.uid
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew();
								this.startData = json.body;
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							}else{
								this.startData={
									TZYS:0,
									BCJG:10,
									XSBL:100
								}
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)// jsonRequest
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "doctorCustomMadeService",
							serviceAction : "savePersonalSetInfo",
							op : this.op,
							schema : this.entryName,
							body : saveData,
							uid : this.mainApp.uid
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body);
								this.startData = this.data;
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.valueChange = false;
							this.op = "update"
						}, this)// jsonRequest
			},
// getWin : function() {
// var win = this.win
// var closeAction = this.closeAction || "close";
// if (!win) {
// win = new Ext.Window({
// id : this.id,
// title : this.title,
// width : this.width,
// iconCls : 'icon-grid',
// shim : true,
// layout : "fit",
// animCollapse : true,
// closeAction : closeAction,
// constrainHeader : true,
// constrain : true,
// minimizable : true,
// maximizable : true,
// shadow : false,
// modal : this.modal || true
// })
// var renderToEl = this.getRenderToEl()
// if (renderToEl) {
// win.render(renderToEl)
// }
// win.on("show", function() {
// this.fireEvent("winShow")
// }, this)
// win.on("add", function() {
// this.win.doLayout()
// }, this)
// win.on("beforeclose", function() {
// return this.fireEvent("beforeclose", this);
// }, this);
// // win.on("beforehide", function() {
// // return this.fireEvent("beforeclose", this);
// // }, this);
// win.on("close", function() {
// this.fireEvent("close", this)
// }, this)
// win.on("hide", function() { // ** add by yzh 2010-06-24 **
// this.fireEvent("hide", this)
// }, this)
// this.win = win
// }
//
// return win;
// },
			onEsc : function() {
				this.win.close()
			},
			doCancel : function() {
				var win = this.getWin();
				if (win){
					win.close();
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
// win.on("beforehide", function() {
// return this.fireEvent("beforeclose", this);
// }, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}

		});