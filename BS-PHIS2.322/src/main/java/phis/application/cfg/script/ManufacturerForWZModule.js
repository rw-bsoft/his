/**
 * 生产厂家维护--增加
 */
$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.ManufacturerForWZModule = function(cfg) {
	cfg.width = 900;
	cfg.height = 600;
	cfg.activateId = 0;
	cfg.modal = this.modal = true;
	phis.application.cfg.script.ManufacturerForWZModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("close", this.onClose, this)
}

Ext.extend(phis.application.cfg.script.ManufacturerForWZModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							deferredRender : false,
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : false,
							autoHeight : true,
							buttonAlign : "center",
							defaults : {
								frame : false,
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
				if (this.midiModules["baseMsgtab"]) {
					this.midiModules["baseMsgtab"].isNewOpen = 1;
				}
				if (this.midiModules["aliasMsgtab"]) {
					this.midiModules["aliasMsgtab"].isNewOpen = 1;
				}
				if (this.midiModules["cardtab"]) {
					this.midiModules["cardtab"].isNewOpen = 1;
				}
				if (this.midiModules["viewMsgtab"]) {
					this.midiModules["viewMsgtab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "baseMsgtab") {
					if (this.midiModules["baseMsgtab"]) {
						this.midiModules["baseMsgtab"].doNew();
						this.midiModules["baseMsgtab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
				}
				if (this.midiModules["aliasMsgtab"]) {
					this.moduleOperation("create",
							this.midiModules["aliasMsgtab"], null);
				}
				if (this.midiModules["cardtab"]) {
					this.moduleOperation("create", this.midiModules["cardtab"],
							null);
				}
				if (this.midiModules["viewMsgtab"]) {
					this.moduleOperation("create",
							this.midiModules["viewMsgtab"], null);
				}
			},
			loadData : function() {
				if (this.midiModules["baseMsgtab"]) {
					this.midiModules["baseMsgtab"].isNewOpen = 1;
				}
				if (this.midiModules["aliasMsgtab"]) {
					this.midiModules["aliasMsgtab"].isNewOpen = 1;
				}
				if (this.midiModules["cardtab"]) {
					this.midiModules["cardtab"].isNewOpen = 1;
				}
				if (this.midiModules["viewMsgtab"]) {
					this.midiModules["viewMsgtab"].isNewOpen = 1;
				}

				if (this.tab.activeTab.id == "baseMsgtab") {
					if (this.midiModules["baseMsgtab"]) {
						this.midiModules["baseMsgtab"].initDataId = this.initDataId;
						this.midiModules["baseMsgtab"].loadData();
						this.midiModules["baseMsgtab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
				}

				if (this.midiModules["aliasMsgtab"]) {
					this.moduleOperation("update",this.midiModules["aliasMsgtab"], null);
				}
				if (this.midiModules["cardtab"]) {
					this.moduleOperation("update", this.midiModules["cardtab"],null);
				}
				if (this.midiModules["viewMsgtab"]) {
					this.moduleOperation("update",this.midiModules["viewMsgtab"], null);
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,this.midiModules[newTab.id], null);
					}
					this.tab.doLayout();
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
					var m = eval("new " + cls + "(cfg)");
					m.setMainApp(this.mainApp);
					if (this.exContext) {
						m.exContext = this.exContext;
					}
					m.opener = this;
					this.midiModules[newTab.id] = m;
					var p = m.initPanel();
					m.on("save", this.onSuperRefresh, this);
					newTab.add(p);
					newTab.__inited = true;
					this.tab.doLayout();
					this.moduleOperation(this.op,this.midiModules[newTab.id], null);
					this.fireEvent("tabchange", tabPanel, newTab,curTab);
								}, this]);
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
				var form = this.midiModules["baseMsgtab"].form.getForm();
				var YHZH = form.findField("YHZH").getValue();
				var YZBM = form.findField("YZBM").getValue();
				var DHHM = form.findField("DHHM").getValue();
				var CZHM = form.findField("CZHM").getValue();
				if (YHZH) {
					var p1 = /^[+]{0,1}(\d){1,3}[ ]?([-]?(\d){1,12})+$/;
					var p2 = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
					if (!p1.test(YHZH) && !p2.test(YHZH)) {
						MyMessageTip.msg("提示", "银行账户输入格式不正确", true);
						return false;
					}
				}
				if (YZBM) {
					var p1 = /^[+]{0,1}(\d){1,3}[ ]?([-]?(\d){1,12})+$/;
					var p2 = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
					if (!p1.test(YZBM) && !p2.test(YZBM)) {
						MyMessageTip.msg("提示", "邮政编码输入格式不正确", true);
						return false;
					}
				}
				if (DHHM) {
					var p1 = /^[+]{0,1}(\d){1,3}[ ]?([-]?(\d){1,12})+$/;
					var p2 = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
					if (!p1.test(DHHM) && !p2.test(DHHM)) {
						MyMessageTip.msg("提示", "联系电话输入格式不正确", true);
						return false;
					}
				}
				if (CZHM) {
					var p1 = /^[+]{0,1}(\d){1,3}[ ]?([-]?(\d){1,12})+$/;
					var p2 = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
					if (!p1.test(CZHM) && !p2.test(CZHM)) {
						MyMessageTip.msg("提示", "传真号码输入格式不正确", true);
						return false;
					}
				}
				if (this.midiModules["aliasMsgtab"]) {
					var form = this.midiModules["baseMsgtab"].form.getForm();
					var CJMC = form.findField("CJMC").getValue();
					if (CJMC == "") {
						return
					} else {
						var store = this.midiModules["aliasMsgtab"].store;
						for (var i = 0; i < store.getCount(); i++) {
							var At = store.getAt(i);
							var CJBM = At.get("CJBM");
							var PYDM = At.get("PYDM");
							var WBDM = At.get("WBDM");
							var JXDM = At.get("JXDM");
							var QTDM = At.get("QTDM");
						}
					}
				}
				if (this.op == "update" && values["aliasMsgtab"]
						&& values["aliasMsgtab"].length == 0) {
					Ext.Msg.alert("提示", "别名不能为空");
					return;
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
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.fireEvent("save");
							this.getWin().hide();
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
					} else if (viewType == "priceList") {
						if (module.isNewOpen == 0) {
						} else {
							module.clear();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.patientList.clear();
							module.detailsList.clear();
							module.isNewOpen = 0;
						}
					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							module.patientList.clear();
							module.detailsList.clear();
							module.isNewOpen = 0;
						}
					}
				} else if (op == "update") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							var whatsthetime = function() {
								module.loadData();
							}
							whatsthetime.defer(500);
							module.isNewOpen = 0;
						}
					} else if (viewType == "priceList") {
						var cnds = "['eq',['$','CJXH'],['i',this.initDataId]]"
						if (module.isNewOpen == 0) {

						} else {
							if (this.initDataId) {
								module.requestData.cnd = eval(cnds);
							}
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.doRefresh(this.initDataId);
							module.isNewOpen = 0;
						}
					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							module.doRefresh(this.initDataId);
							module.isNewOpen = 0;
						}
					}
				} else if (op == "save") {
					if (viewType == "form") {
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.getForm();
						if (!form.isValid()) {
							return 0;
						}
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.pkey) {
								values[item.id] = this.initDataId;
							}
							var field = form.findField(item.id);
							if (field) {
								var value = field.getValue();
								if (value == null || value === "") {
									if (!item.pkey && item["not-null"]) {
										Ext.Msg
												.alert("提示", item.alias
																+ "不能为空");
										return 0;
									}
								}
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								values[item.id] = value;
							} else if (item.defaultValue && !item.virtual) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
					} else if (viewType == "priceList") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							values[module.acid]
									.push(module.store.getAt(i).data);
						}
					} else if (viewType == "editlist") {
						values[module.acid] = [];
						count = module.patientList.store.getCount();
						for (var i = 0; i < count; i++) {
							values[module.acid].push(module.patientList.store
									.getAt

									(i).data);
						}
					}
				}
				return 1;
			},
			onClose : function() {
				this.tab.activate(0);
			}
		});