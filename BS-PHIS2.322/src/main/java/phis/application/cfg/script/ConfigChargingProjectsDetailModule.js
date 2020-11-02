/**
 * 药品公用信息模版
 * 
 * @author tianj
 */
$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.ConfigChargingProjectsDetailModule = function(cfg) {
	cfg.width = 700;
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	this.modal = cfg.modal = true;
	phis.application.cfg.script.ConfigChargingProjectsDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("close", this.onClose, this)
}

Ext.extend(phis.application.cfg.script.ConfigChargingProjectsDetailModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if(ac.id=="cfgaliastab"){
						ac.cnds="['eq',['$','fyxh'],['d',this.initDataId]]";
					}
					tabItems.push({
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							autoHeight : true,
							buttonAlign : "center",
							defaults : {
								border : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems,
							buttons : [{
										xtype : "button",
										text : "确定",
										handler : this.doSave,
										scope : this
									}, {
										xtype : "button",
										text : "取消",
										handler : function() {
											this.getWin().hide()
										},
										scope : this
									}]
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return this.tab;
			},

			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (this.midiModules["cfgproptab"]) {
					this.midiModules["cfgproptab"].isNewOpen = 1;
				}
				if (this.midiModules["cfgaliastab"]) {
					this.midiModules["cfgaliastab"].isNewOpen = 1;
				}
				if (this.midiModules["cfglimittab"]) {
					this.midiModules["cfglimittab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "cfgproptab") {
					if (this.midiModules["cfgproptab"]) {
						this.midiModules["cfgproptab"].doNew();
						this.midiModules["cfgproptab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
					// this.tab.getActiveTab().getWin().center();
				}
				if (this.midiModules["cfgaliastab"]) {
					this.moduleOperation("create",
							this.midiModules["cfgaliastab"], null);
				}
				if (this.midiModules["cfglimittab"]) {
					this.moduleOperation("create",
							this.midiModules["cfglimittab"], null);
				}
			},
			loadData : function() {
				if (this.midiModules["cfgproptab"]) {
					this.midiModules["cfgproptab"].isNewOpen = 1;
				}
				if (this.midiModules["cfgaliastab"]) {
					this.midiModules["cfgaliastab"].isNewOpen = 1;
				}
				if (this.midiModules["cfglimittab"]) {
					this.midiModules["cfglimittab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "cfgproptab") {
					if (this.midiModules["cfgproptab"]) {
						this.midiModules["cfgproptab"].initDataId = this.initDataId;
						this.midiModules["cfgproptab"].loadData();
						this.midiModules["cfgproptab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
					this.tab.getWin().center();
				}
				if (this.midiModules["cfgaliastab"]) {
					this.moduleOperation("update",
							this.midiModules["cfgaliastab"], null);
				}
				if (this.midiModules["cfglimittab"]) {
					this.moduleOperation("update",
							this.midiModules["cfglimittab"], null);
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,
								this.midiModules[newTab.id], null);
					}
					return;
				}

				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var result = this.mainApp.taskManager.loadModuleCfg(ref);
					Ext.apply(cfg, result.json.body)
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$import(cls);
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)")
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							this.moduleOperation(this.op, m, null)
							m.on("save", this.onSuperFormRefresh, this);
							newTab.add(p);
							newTab.__inited = true;
								// this.moduleOperation(this.op, m);
							}, this])
				this.tab.doLayout();
			},
			doSave : function() {
				var values = {};
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					var module = this.midiModules[ac.id];
					if (module) {
						module.acid = ac.id;
						var k = this.moduleOperation("save", module, values);
						if (k == 0) {
							return;
						}
					}
				}
				if (this.op != "create"
						&& this.midiModules["cfgaliastab"]
						&& (!values["cfgaliastab"] || values["cfgaliastab"].length == 0)) {
					MyMessageTip.msg("提示", "费用别名不能为空！", true);
					return;
				}
				if ((!values["cfgaliastab"] || values["cfgaliastab"].length == 0)
						&& this.op == "create") {
					if (this.midiModules["cfgproptab"]) {
						var fymc = this.midiModules["cfgproptab"].form
								.getForm().findField("FYMC").getValue();
						values["cfgaliastab"] = [];
						var data = {
							'_opStatus' : 'create'
						};
						var body = {};
						body.codeType = [];
						body.codeType.push("py", "wb", "jx", "bh");
						body.value = fymc;
						var pyCode = phis.script.rmi.miniJsonRequestSync({
									serviceId : "codeGeneratorService",
									serviceAction : "getCode",
									method : "execute",
									body : body
								});
						data["FYXH"] = null;
						data["FYMC"] = fymc;
						data["PYDM"] = pyCode.json.body.py;
						data["WBDM"] = pyCode.json.body.wb;
						data["JXDM"] = pyCode.json.body.jx;
						data["BHDM"] = pyCode.json.body.bh;
						data["QTDM"] = null;
						data["BMFL"] = 1;
						values["cfgaliastab"].push(data);
					}
				}
				if (this.initDataId) {
					values["FYXH"] = this.initDataId;
				}

				this.tab.el.mask("正在执行操作...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.saveServiceAction,
							method : "execute",
							op : this.op,
							body : values
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code >= 300 && code != 613) {
								this.processReturnMsg(code, msg);
								return;
							} else if (code == 613) {// 提示不能重复
								MyMessageTip.msg("提示", msg, true);
								return false;
							}
							this.fireEvent("save");
							this.getWin().hide();
							// this.op = "update";
					}, this);
			},
			moduleOperation : function(op, module, values) {
				var viewType = module.viewType;
				if (op == "create") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.doNew();
							module.isNewOpen = 0;
						}
					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							module.clear();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.loadData();
							module.fillStore("create");
							module.isNewOpen = 0;
						}

					}
				} else if (op == "update") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.loadData();
							module.isNewOpen = 0;
						}

					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							if (module.cnds) {
								module.requestData.cnd = eval(module.cnds);
							}
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.fillStore("update");
							module.isNewOpen = 0;
						}
					}
				} else if (op == "save") {
					if (viewType == "form") {
						/*
						 * var i = module.doValidation(); if (i == 0) { return
						 * 0; }
						 */
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.getForm();

						var error;
						if(form.findField('XMLX').value == "11"){
							if(form.findField('JCDL').getValue().trim() =='' ){
								error="检查大类没有填写，请尽快填写，否则影响正常业务。";
							}
							/*if(form.findField('JCXL').getValue().trim() =='' ){
								error="检查小类没有填写，请尽快填写，否则影响正常业务。";
							}*/
						}
						if (error != undefined) {
							Ext.Msg.alert("提示",  error);
							return 0;
						}
						
						
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.pkey) {
								values['FYGB'] = this.exContext.FYGB;
								values[item.id] = this.initDataId;

							}
							var field = form.findField(item.id);
							if (field) {
								var value = field.getValue();
								if (!item.dic) {
									var error = field.getErrors(value)[0];
									if (error != undefined) {
										Ext.Msg.alert("提示", "【" + item.alias
														+ "】" + error);
										return 0;
									}
								}
								if (value != 'false'
										&& String(value) == "false") {
									value = '0';
								} else if (value != 'true'
										&& String(value) == "true") {
									value = '1';
								}
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								values[item.id] = value;
							} else if ((item.defaultValue||item.defaultValue==0) && !item.virtual) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
					} else if (viewType == "list") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							if (!module.store.getAt(i).data.FYMC) {
								continue;
							}
							values[module.acid]
									.push(module.store.getAt(i).data);
						}

					} else if (viewType == "editlist") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {

							if ((module.store.getAt(i).data["ZFBL"] != ''
									&& module.store.getAt(i).data["ZFBL"] != '0' && module.store
									.getAt(i).data["ZFBL"] != null)
									|| (module.store.getAt(i).data["FYXE"] != ''
											&& module.store.getAt(i).data["FYXE"] != '0' && module.store
											.getAt(i).data["FYXE"] != null)
									|| (module.store.getAt(i).data["CXBL"] != ''
											&& module.store.getAt(i).data["CXBL"] != '0' && module.store
											.getAt(i).data["CXBL"] != null)) {
								values[module.acid]
										.push(module.store.getAt(i).data);
							}

						}
					}
				}
				return 1;
			},
			onClose : function() {
				this.tab.activate(0);
			}
		});