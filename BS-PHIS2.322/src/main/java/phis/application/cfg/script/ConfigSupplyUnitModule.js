/**
 * 供货单位信息维护模版
 * 
 * @author gaof
 */
$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.ConfigSupplyUnitModule = function(cfg) {
	cfg.width = 800;
	cfg.activateId = 0;
	cfg.modal = true;

	phis.application.cfg.script.ConfigSupplyUnitModule.superclass.constructor.apply(
			this, [cfg]);
	this.on("close", this.onClose, this);
	this.serviceId="configSupplyUnitService";
}

Ext.extend(phis.application.cfg.script.ConfigSupplyUnitModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
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
							tbar : [{
										xtype : "button",
										width : 80,
										cmd : "save",
										text : "确定",
										iconCls : "save",
										handler : this.doSave,
										scope : this
									}, '-', {
										xtype : "button",
                                        width : 80,
                                        cmd : "cancel",
										text : "取消",
                                        iconCls : "common_cancel",
										handler : function() {
											this.getWin().hide();
										},
										scope : this
									}]
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId);
				this.tab = tab;
				return this.tab;
			},
			doNew : function() {
				this.op = "create";
				if (this.data) {
					this.data = {}
				}
				if (this.midiModules["baseinfomationtab"]) {
					this.midiModules["baseinfomationtab"].isNewOpen = 1;
				}
				if (this.midiModules["certificateinftab"]) {
					this.midiModules["certificateinftab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "baseinfomationtab") {
					if (this.midiModules["baseinfomationtab"]) {
						this.midiModules["baseinfomationtab"].doNew();
						this.midiModules["baseinfomationtab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
				}
				if (this.midiModules["certificateinftab"]) {
					this.moduleOperation("create",
							this.midiModules["certificateinftab"], null);
				}

			},
			loadData : function() {
				if (this.midiModules["baseinfomationtab"]) {
					this.midiModules["baseinfomationtab"].isNewOpen = 1;
				}
				if (this.midiModules["certificateinftab"]) {
					this.midiModules["certificateinftab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "baseinfomationtab") {
					if (this.midiModules["baseinfomationtab"]) {
						this.midiModules["baseinfomationtab"].initDataId = this.initDataId;
						this.midiModules["baseinfomationtab"].loadData();
						this.midiModules["baseinfomationtab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
				}
				if (this.midiModules["certificateinftab"]) {
					this.moduleOperation("update",
							this.midiModules["certificateinftab"], null);
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,
								this.midiModules[newTab.id], null);
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
					m.on("save", this.onSuperRefresh, this)
					newTab.add(p);
					newTab.__inited = true
					this.tab.doLayout();
					
					this.moduleOperation(this.op,this.midiModules[newTab.id], null);
					
					this.fireEvent("tabchange", tabPanel, newTab,
							curTab);
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

				var data = {};
				data["DWXH"] = values.DWXH;
				if (this.op == "update") {
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "getJGID",
								body : data
							}, function(code, msg, json) {
								if (code >= 300) {
									MyMessageTip.msg("提示", msg, true);
								} else {

									if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId
											&& json.jgid == this.mainApp.topUnitId) {
										MyMessageTip.msg("提示", '公共供货单位不允许修改!',
												true);
										return
									}

									if (values.DWZT != 1) {
										MyMessageTip.msg("提示", "已注销的供货单位不能修改",
												true);
										return;
									}

									this.tab.el.mask("正在执行操作...");
									phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : "saveSupplyUnitInfomations",
										op : this.op,
										body : values
									}, function(code, msg, json) {
										this.tab.el.unmask();
										if (code >= 300) {
											this.processReturnMsg(code, msg);
											return;
										}
										if (json.count > 0) {
											Ext.Msg.alert("提示", "单位名称不能重复");
											return;
										}
										if (json.countZJXX > 0) {
											Ext.Msg.alert("提示", "证件编号不能重复");
											return;
										}
										// Ext.Msg.alert("提示", "保存成功");
										this.fireEvent("save");
										this.getWin().hide();
											// this.op = "update";
										}, this);
								}
							}, this)
				} else if (this.op == "create") {
					this.tab.el.mask("正在执行操作...");
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "saveSupplyUnitInfomations",
								op : this.op,
								body : values
							}, function(code, msg, json) {
								this.tab.el.unmask();
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}
								if (json.count > 0) {
									Ext.Msg.alert("提示", "单位名称不能重复");
									return;
								}
								if (json.countZJXX > 0) {
									Ext.Msg.alert("提示", "证件编号不能重复");
									return;
								}
								// Ext.Msg.alert("提示", "保存成功");
								this.fireEvent("save");
								this.getWin().hide();
								// this.op = "update";
						}, this);
				}

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
						var cnds = "['eq',['$','DXXH'],['i',this.initDataId]]";
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.cnd = eval(cnds);
							module.loadData();
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
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								if (item.id == "DWMC" && value.length > 60) {
									Ext.Msg.alert("提示", "单位名称长度不能超过60");
									return 0;
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
					} else if (viewType == "list") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							if (module.store.getAt(i).data["ZJLX"] == null
									|| module.store.getAt(i).data["ZJLX"] == '') {
								Ext.Msg.alert("提示", "证件类型不能为空");
								return 0;
							}
							if (module.store.getAt(i).data["ZJBH"] == null
									|| module.store.getAt(i).data["ZJBH"] == '') {
								Ext.Msg.alert("提示", "证件编号不能为空");
								return 0;
							}
							if (module.store.getAt(i).data["FZRQ"] == null
									|| module.store.getAt(i).data["FZRQ"] == '') {
								Ext.Msg.alert("提示", "发证日期不能为空");
								return 0;
							}
							if (module.store.getAt(i).data["SXRQ"] == null
									|| module.store.getAt(i).data["FZRQ"] == '') {
								Ext.Msg.alert("提示", "失效日期不能为空");
								return 0;
							}
							if (module.store.getAt(i).data["ZJZT"] == 1
									&& module.store.getAt(i).data["SXRQ"] < new Date()
											.format("Y-m-d")) {
								Ext.Msg.alert("提示", "失效日期不能小于当前日期");
								return 0;
							}
							if (module.store.getAt(i).data["FZRQ"] > module.store
									.getAt(i).data["SXRQ"]) {
								Ext.MessageBox.alert("提示", "发证日期不能大于失效时间");
								return 0;
							}
							for (var j = 0; j < count; j++) {
								if (i == j) {
									continue;
								}
								if (module.store.getAt(i).data["ZJLX"] == module.store
										.getAt(j).data["ZJLX"]
										&& module.store.getAt(i).data["ZJBH"] == module.store
												.getAt(j).data["ZJBH"]) {
									Ext.Msg.alert("提示", "证件号码重复，请核对");
									return 0;
								}
								if (module.store.getAt(i).data["ZJLX"] == module.store
										.getAt(j).data["ZJLX"]
										&& module.store.getAt(i).data["FZRQ"] <= module.store
												.getAt(j).data["FZRQ"]
										&& module.store.getAt(i).data["SXRQ"] > module.store
												.getAt(j).data["FZRQ"]) {
									Ext.Msg.alert("提示",
											"同类型证件,较晚证件的发证日期需>=较早证件的失效日期");
									return 0;
								}
							}
							values[module.acid]
									.push(module.store.getAt(i).data);
						}

					}
				}
				return 1;
			},
			onClose : function() {
				this.tab.activate(0);
			}
		});