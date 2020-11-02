$package("phis.application.cfg.script")
/**
 * 收费项目维护的医疗项目 caijy 2012.05
 */
$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cfg.script.ConfigProjectsDetailForm = function(cfg) {
	// cfg.entryName=this.entryName="GY_YLMX_DR";
	// cfg.schema=this.schema="GY_SFXM_XG";
	cfg.serviceId = "configDeptCostService";
	cfg.loadServiceActionId = "fromLoadItemDetails";
	cfg.width = 770;
	phis.application.cfg.script.ConfigProjectsDetailForm.superclass.constructor.apply(
			this, [cfg]);

}
Ext.extend(phis.application.cfg.script.ConfigProjectsDetailForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {

				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.actions = [/*
								 * { id : "create", name : "新建", iconCls :
								 * "create", scale : 'large' },
								 */{
					id : "save",
					name : "保存",
					iconCls : "save",
					notReadOnly : true
						// scale : 'large'
					}, {
					id : "cancel",
					name : "关闭",
					iconCls : "common_cancel",
					notReadOnly : true
						// scale : 'large'
					}]
				this.form = new Ext.FormPanel({
							labelWidth : 65, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1024,
							autoHeight : true,
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										title : '代码属性',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('DMSX')
									}, {
										xtype : 'fieldset',
										title : '其他属性',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('QT')
									}, {
										xtype : 'fieldset',
										title : '机构属性',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JG')
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				if (!this.isCombined) {
					this.addPanelToWin();
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
				return this.form
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync("phis.application.cfg.schemas.GY_YLMX_DR")
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]

					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					if (!it.layout || it.layout != para) {
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			expand : function() {
				this.win.center();
			},
			doSave : function() {
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

				Ext.apply(this.data, this.exContext)

				if (items) {
					var n = items.length
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
							v = f.getValue()
							if (f.getXType() == "checkbox") {
								if (v == true) {
									v = "1";
								} else {
									v = "0";
								}
							}
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
						values[it.id] = v;
					}
				}

				Ext.apply(this.data, values);
				this.saveToServer(values)
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.loadServiceActionId,
					schema : this.entryName,
					pkey : this.initDataId,
					method : "execute",
					body : this.initDataBody,
					action : this.op, // 按钮事件
					module : this._mId
						// 增加module的id
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
						this.doNew()
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
					}
					if (this.op == 'create') {
						this.op = "update"
					}
				}, this)// jsonRequest
			},
			createButtons : function() {
				if (this.op == 'read') {
					return []
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
					btn.accessKey = f1 + i, btn.cmd = action.id
					btn.text = action.name + "(F" + (i + 1) + ")", btn.iconCls = action.iconCls
							|| action.id
					btn.script = action.script
					btn.handler = this.doAction;

					// ** add by yzh **
					btn.notReadOnly = action.notReadOnly

					if (action.notReadOnly)
						btn.disabled = false
					else
						btn.disabled = this.exContext.readOnly || false

					btn.scope = this;
					// btn.scale = "large";
					// btn.iconAlign = "top";
					buttons.push(btn, '')
				}
				return buttons
			}
		});
