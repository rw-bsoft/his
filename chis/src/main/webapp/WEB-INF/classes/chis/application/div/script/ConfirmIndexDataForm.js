$package("chis.application.div.script");

$import("chis.script.BizTableFormView", "util.dictionary.TreeDicFactory",
		"org.ext.ux.layout.TableFormLayout");

chis.application.div.script.ConfirmIndexDataForm = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.autoLoadData = false;
	cfg.width = 1020;
	cfg.height = 140;
	cfg.butRule = false;
	cfg.saveServiceId = "chis.masterplateMaintainService";
	cfg.saveAction = "saveMasterplateDate";
	cfg.loadServiceId = "chis.masterplateMaintainService";
	cfg.loadAction = "loadMasterplateDate";
	this.showButtonOnTop = true;
	this.fldDefaultWidth = 120;
	chis.application.div.script.ConfirmIndexDataForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);

};

Ext.extend(chis.application.div.script.ConfirmIndexDataForm,
		chis.script.BizTableFormView, {
			initPanel : function() {
				var ret = util.rmi.miniJsonRequestSync({
							serviceId : "chis.masterplateMaintainService",
							serviceAction : "getMasterplate",
							method : "execute",
							body : this.masterplateId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.getDataForm);
					return;
				}
				var formFields = ret.json.body;
				this.formFields = formFields;
				this.schema = {
					"items" : formFields
				}
				var LRSJ = [];
				this.year = new Date().getFullYear();
				if (this.dateType == "1") {
					var LRSJcombox = util.dictionary.SimpleDicFactory
							.createDic({
										id : "chis.dictionary.years",
										defaultValue : {
											key : this.year,
											text : this.year + "年"
										},
										width : 360
									});
					LRSJcombox.name = 'LRSJ';
					LRSJcombox.fieldLabel = '年度';
					LRSJcombox.allowBlank = false;
					this.LRSJcomboxND = LRSJcombox;
					LRSJ = [{
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>年度:</span>"
							}, LRSJcombox]
				} else if (this.dateType == "2") {
					var LRSJcomboxND = util.dictionary.SimpleDicFactory
							.createDic({
										id : "chis.dictionary.years",
										defaultValue : {
											key : this.year,
											text : this.year + "年"
										},
										width : 120
									});
					LRSJcomboxND.name = 'LRSJND';
					LRSJcomboxND.fieldLabel = '年度';
					LRSJcomboxND.allowBlank = false;
					this.LRSJcomboxND = LRSJcomboxND;
					var LRSJcombox = util.dictionary.SimpleDicFactory
							.createDic({
										id : "chis.dictionary.season",
										width : 120
									});
					LRSJcombox.name = 'LRSJ';
					LRSJcombox.fieldLabel = '季度';
					LRSJcombox.allowBlank = false;
					LRSJ = [{
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>年度:</span>"
							}, LRSJcomboxND, {
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>季度:</span>"
							}, LRSJcombox]
				} else if (this.dateType == "3") {
					var LRSJcomboxND = util.dictionary.SimpleDicFactory
							.createDic({
										id : "chis.dictionary.years",
										defaultValue : {
											key : this.year,
											text : this.year + "年"
										},
										width : 120
									});
					LRSJcomboxND.name = 'LRSJND';
					LRSJcomboxND.fieldLabel = '年度';
					LRSJcomboxND.allowBlank = false;
					this.LRSJcomboxND = LRSJcomboxND;
					var LRSJcombox = util.dictionary.SimpleDicFactory
							.createDic({
										id : "chis.dictionary.month",
										width : 120
									});
					LRSJcombox.name = 'LRSJ';
					LRSJcombox.fieldLabel = '月度';
					LRSJcombox.allowBlank = false;
					LRSJ = [{
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>年度:</span>"
							}, LRSJcomboxND, {
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>月度:</span>"
							}, LRSJcombox]
				} else if (this.dateType == "4") {
					LRSJ = [{
								xtype : "panel",
								width : 120,
								html : "<span style='color:red'>日期:</span>"
							}, new Ext.form.DateField({
										name : 'LRSJ',
										altFormats : 'Y-m-d',
										format : 'Y-m-d',
										emptyText : '日期',
										allowBlank : false,
										width : 360
									})]
				}

				var config = {
					id : 'chis.@manageUnit',
					name : 'chis.@manageUnit',
					parentKey : this.mainApp.deptId,
					rootVisible : true,

					defaultValue : {
						key : this.mainApp.deptId,
						text : this.mainApp.dept
					},
					width : 360
				};
				if (this.masterplateId == "0000000000000005"
						|| this.masterplateId == "0000000000000006"
						|| this.masterplateId == "0000000000000007") {
					config.filter = ['le', ['len', ['$', 'item.getKey()']], '9'];
					config.onlySelectLeaf = false;
				} else {
					delete config.filter;
					config.onlySelectLeaf = true;
				}
				var unitCombox = util.dictionary.TreeDicFactory
						.createDic(config);
				unitCombox.name = 'manageUnit';
				unitCombox.fieldLabel = '管辖机构';
				unitCombox.allowBlank = false;
				this.unitCombox = unitCombox;

				var areaCombox = util.dictionary.TreeDicFactory.createDic({
							id : 'chis.dictionary.areaGrid',
							name : 'chis.dictionary.areaGrid',
							minChars : "4",
							width : 360
						});
				areaCombox.name = 'areaGrid';
				areaCombox.fieldLabel = '网格地址';
				areaCombox.allowBlank = false;
				this.areaCombox = areaCombox;

				config = {
					id : 'chis.dictionary.userDistinctAll',
					name : 'chis.dictionary.userDistinctAll',
					includeParentMinLen : "12",
					filterMin : "10",
					minChars : "4",
					filterMax : "18",
					onlySelectLeaf : true,
					parentKey : this.mainApp.deptId,
					rootVisible : true,
					defaultValue : {
						key : this.mainApp.uid,
						text : this.mainApp.uname
					},
					width : 360
				};
				if (this.masterplateId == "0000000000000008") {
					config.filter = [
							'if',
							['isNull', ['$', 'item.getProperty("manageUnit")']],
							['eq', ['s', '1'], ['s', '1']],
							[
									'gt',
									[
											'len',
											['$',
													'item.getProperty("manageUnit")']],
									'9']];
				} else {
					delete config.filter
				}
				var userCombox = util.dictionary.TreeDicFactory
						.createDic(config);
				userCombox.name = 'user';
				userCombox.fieldLabel = '医生';
				userCombox.allowBlank = false;
				this.userCombox = userCombox;

				var formItems = [];
				formItems.push({
							xtype : 'panel',
							layout : "table",
							colspan : 2,
							items : LRSJ
						});
				formItems.push({
							xtype : 'panel',
							layout : "table",
							colspan : 2,
							items : [{
										xtype : "panel",
										width : 120,
										html : "<span style='color:red'>管辖机构:</span>"
									}, unitCombox]
						});
				formItems.push({
							xtype : 'panel',
							layout : "table",
							colspan : 2,
							items : [{
										xtype : "panel",
										width : 120,
										html : "<span style='color:red'>网格地址:</span>"
									}, areaCombox]
						});
				formItems.push({
							xtype : 'panel',
							layout : "table",
							colspan : 2,
							items : [{
										xtype : "panel",
										width : 120,
										html : "<span style='color:red'>医生:</span>"
									}, userCombox]
						});
				this.height = 140;
				for (var i = 0; i < formFields.length; i++) {
					var field = formFields[i];
					var f = this.createField(field);
					formItems.push({
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 120,
											html : field.alias + ":"
										}, f]
							});
					if (i % 4 == 0) {
						this.height = this.height + 30;
					}
				}
				var actions = [];
				actions.push({
							iconCls : 'save',
							name : '保存',
							id : 'save',
							handler : this.doSave,
							scope : this
						});
				actions.push({
							iconCls : 'common_cancel',
							name : '取消',
							id : 'cancel',
							handler : this.doCancel,
							scope : this
						});
				this.actions = actions;
				var form = new Ext.FormPanel({
							labelWidth : 120,
							frame : true,
							defaultType : 'textfield',
							layout : 'tableform',
							autoScroll : true,
							defaults : {
								bodyStyle : 'padding-left:3px;padding-bottom:3px;'
							},
							layoutConfig : {
								columns : 4,
								tableAttrs : {
									border : 0
								}
							},
							items : formItems,
							tbar : [this.createButtons()]
						});
				this.form = form
				return form
			},
			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					return;
				}
				if (!this.validate()) {
					return;
				}
				Ext.apply(this.data, values);
				this.saveToServer(values)
			},
			getFormData : function() {
				var values = {};
				var items = this.formFields;
				var form = this.form.getForm();
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = this.formFields[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}
						if (v == null || v === "") {
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}
				var LRSJ = form.findField("LRSJ");
				values["LRSJ"] = LRSJ.getValue();
				var LRSJND = form.findField("LRSJND");
				if (LRSJND) {
					values["LRSJND"] = LRSJND.getValue();
				}
				var manageUnit = form.findField("manageUnit");
				values["manageUnit"] = manageUnit.getValue();
				var areaGrid = form.findField("areaGrid");
				values["areaGrid"] = areaGrid.getValue();
				var user = form.findField("user");
				values["user"] = user.getValue();
				values["masterplateId"] = this.masterplateId;
				if (this.op == "update") {
					values["recordId"] = this.data.recordId;
					values["dataIds"] = this.dataIds;
				}
				return values;
			},
			initFormData : function(data) {
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				Ext.apply(this.data, data)
				this.initDataId = this.data["recordId"]
				this.dataIds = this.data["dataIds"];
				var form = this.form.getForm()
				var items = this.formFields
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
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
				var LRSJ = form.findField("LRSJ")
				LRSJ.setValue(data["dateValue"]);
				var LRSJND = form.findField("LRSJND");
				if (LRSJND) {
					LRSJND.setValue(data["dateValue_nd"]);
				}
				var manageUnit = form.findField("manageUnit")
				manageUnit.setValue(data["manaUnitId"]);
				var areaGrid = form.findField("areaGrid")
				areaGrid.setValue(data["regionCode"]);
				var user = form.findField("user")
				user.setValue(data["manageDoctor"]);
				this.setKeyReadOnly(true)
				this.startValues = form.getValues(true);
				this.resetButtons(); // ** 用于页面按钮权限控制
				this.focusFieldAfter(-1, 800);
			},
			doNew : function() {
				chis.application.div.script.ConfirmIndexDataForm.superclass.doNew
						.call(this);
				this.unitCombox.setValue({
							key : this.mainApp.deptId,
							text : this.mainApp.dept
						});
				this.userCombox.setValue({
							key : this.mainApp.uid,
							text : this.mainApp.uname
						});
				this.LRSJcomboxND.setValue({
							key : this.year,
							text : this.year + "年"
						});
			},
			onWinShow : function() {
				this.win.setHeight(this.height);
				this.win.setWidth(this.width);
				this.saveServiceId = "chis.masterplateMaintainService";
				this.saveAction = "saveMasterplateDate";
				this.loadServiceId = "chis.masterplateMaintainService";
				this.loadAction = "loadMasterplateDate";
			},
			createField : function(it) {
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
				// Ext.apply(cfg,it);
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					scope : this
				};
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
				if (it['notNull'] == "1" || it['notNull'] == "true") {
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
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
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
				};
				return cfg;
			},
			doCancel : function() {
				var win = this.win;
				if (win) {
					this.win = null;
					win.close();
				}
			},
			getWin : function() {
				var win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			},
			createButtons : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {}
					btn.accessKey = f1 + i + this.buttonIndex, btn.cmd = action.id
					btn.text = action.name + "(F" + (i + 1 + this.buttonIndex)
							+ ")", btn.iconCls = action.iconCls || action.id
					btn.script = action.script
					btn.handler = action.handler;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn)
				}
				return buttons
			}
		});