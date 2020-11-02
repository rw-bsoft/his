$package("phis.application.cfg.script")

$import("phis.script.TabModule")

phis.application.cfg.script.MaterialInformationTabs = function(cfg) {
	this.width = 1100
	phis.application.cfg.script.MaterialInformationTabs.superclass.constructor
			.apply(this, [ cfg ])
}

Ext
		.extend(
				phis.application.cfg.script.MaterialInformationTabs,
				phis.script.TabModule,
				{
					initPanel : function() {
						if (this.tab) {
							return this.tab;
						}
						this.tbar = [ {
							xtype : "button",
							width : 80,
							cmd : "create",
							text : "新  增",
							iconCls : "new",
							handler : this.doCreate,
							scope : this
						}, '-', {
							xtype : "button",
							width : 80,
							text : "保  存",
							cmd : "save",
							iconCls : "save",
							handler : this.doSave,
							scope : this
						}, '-', {
							xtype : "button",
							width : 80,
							text : "退  出",
							cmd : "cancel",
							iconCls : "common_cancel",
							handler : this.doCancel,
							scope : this
						} ]
						var tabItems = []
						var actions = this.actions
						for ( var i = 0; i < actions.length; i++) {
							var ac = actions[i];
							tabItems.push({
								frame : this.frame,
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
							buttonAlign : "right",
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							// autoHeight : true,
							defaults : {
								border : false
							// autoHeight : true,
							// autoWidth : true
							},
							items : tabItems,
							tbar : {
								// buttonAlign : "right",
								items : this.tbar
							}
						})
						tab.on("tabchange", this.onTabChange, this);
						tab.on("beforetabchange", this.onBeforeTabChange, this);
						tab.activate(this.activateId)
						this.tab = tab
						return tab;
					},
					onTabChange : function(tabPanel, newTab, curTab) {
						if (newTab.id != "baseInfoTab") {
							this.tab.getTopToolbar().items.first().hide();
						} else {
							this.tab.getTopToolbar().items.first().show();
						}
						if (newTab.__inited) {
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
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
							var body = this.loadModuleCfg(ref);
							Ext.apply(cfg, body)
						}
						var cls = cfg.script
						if (!cls) {
							return;
						}

						if (!this.fireEvent("beforeload", cfg)) {
							return;
						}
						$require(cls, [
								function() {
									var m = eval("new " + cls + "(cfg)");
									this.module = m;
									m.setMainApp(this.mainApp);
									if (this.exContext) {
										m.exContext = this.exContext;
									}
									m.opener = this;
									this.midiModules[newTab.id] = m;
									if (newTab.id == "itemAliasTab") {
										m.requestData.cnd = [ 'eq',
												[ '$', 'WZXH' ],
												[ 'i', this.cfg.WZXH ] ];
									}
									var p = m.initPanel();
									if (m.refresh) {
										m.refresh();
									}
									m.on("save", this.onSuperRefresh, this)
									newTab.add(p);
									newTab.__inited = true
									this.tab.doLayout();
									this.fireEvent("tabchange", tabPanel,
											newTab, curTab);
								}, this ])
					},
					getWin : function() {
						var win = this.win;
						var closeAction = "hide";
						if (!win) {
							win = new Ext.Window({
								id : this.id,
								title : this.name,
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
								modal : this.modal || true
							// items : this.initPanel()
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
						return win;
					},
					doSave : function() {
						var data = {};
						var actions = this.actions
						data["itemAliasTab"] = [];
						for ( var i = 0; i < actions.length; i++) {
							var ac = actions[i];
							if (ac.type == "tab") {
								var module = this.midiModules[ac.id];
								if (module) {
									var ret = module.getSaveData();
									if (!ret)
										return;
									data[ac.id] = ret;
								}
							}
						}
						if (this.cfg.op == "create"
								&& (!this.midiModules["manufacturerTab"] || data["manufacturerTab"].length == 0)) {
							Ext.MessageBox.alert("提示", "该物品还未维护生产厂家！");
							return;
						}

						if (this.cfg.op == "create") {
							var record = {};
							record.WZBM = data["baseInfoTab"].WZMC;
							record.PYDM = data["baseInfoTab"].PYDM;
							record.WBDM = data["baseInfoTab"].WBDM;
							record.JXDM = data["baseInfoTab"].JXDM;
							record.QTDM = data["baseInfoTab"].QTDM;
							record._opStatus = "create";
							data["itemAliasTab"].push(record);
						}
						data.op = this.cfg.op;
						data.WZXH = this.cfg.WZXH;
						data.ZDXH = this.cfg.ZDXH;
						if (data["baseInfoTab"].WZMC.realLength() > 60) {
							MyMessageTip.msg("提示", "物资名称输入过长!", true);
							return;
						}

						if (data["baseInfoTab"].PYDM.length > 10) {
							MyMessageTip.msg("提示", "拼音代码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].WBDM.length > 10) {
							MyMessageTip.msg("提示", "五笔代码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].JXDM.length > 10) {
							MyMessageTip.msg("提示", "角形代码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].QTDM.length > 10) {
							MyMessageTip.msg("提示", "其它代码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].WZDW.realLength() > 10) {
							MyMessageTip.msg("提示", "物资单位输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].WZGG.realLength() > 40) {
							MyMessageTip.msg("提示", "物资规格输入过长!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].SXYJ) < 0) {
							MyMessageTip.msg("提示", "失效预警天数不能小于0!", true);
							return;
						}
						if ((data["baseInfoTab"].SXYJ + "").length > 4) {
							MyMessageTip.msg("提示", "失效预警天数输入过长!", true);
							return;
						}

						if (data["baseInfoTab"].KWBH.realLength() > 15) {
							MyMessageTip.msg("提示", "库位编号输入过长!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].GCSL) < 0) {
							MyMessageTip.msg("提示", "高储数量不能小于0!", true);
							return;
						}
						if ((data["baseInfoTab"].GCSL + "").length > 12) {
							MyMessageTip.msg("提示", "高储数量输入过长!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].DCSL) < 0) {
							MyMessageTip.msg("提示", "低储数量不能小于0!", true);
							return;
						}

						if (parseInt(data["baseInfoTab"].ZJNX) < 0) {
							MyMessageTip.msg("提示", "折旧年限不能小于0!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].ZGZL) < 0) {
							MyMessageTip.msg("提示", "总工作量不能小于0!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].JCZL) < 0) {
							MyMessageTip.msg("提示", "净残值率不能小于0!", true);
							return;
						}

						if ((data["baseInfoTab"].DCSL + "").length > 12) {
							MyMessageTip.msg("提示", "低储数量输入过长!", true);
							return;
						}
						if (parseInt(data["baseInfoTab"].DCSL) > parseInt(data["baseInfoTab"].GCSL)) {
							MyMessageTip.msg("提示", "低储数量不能大于高储数量!", true);
							return;
						}
						if ((data["baseInfoTab"].ZGZL + "").length > 12) {
							MyMessageTip.msg("提示", "总工作量输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].WZTM.realLength() > 30) {
							MyMessageTip.msg("提示", "物品条码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].ZDTM.realLength() > 30) {
							MyMessageTip.msg("提示", "自定条码输入过长!", true);
							return;
						}
						if (data["baseInfoTab"].GYBZ == 1) {
							MyMessageTip.msg("提示", "公共物资不能修改!", true);
							return;
						}
						this.tab.el.mask("正在保存数据...", "x-mask-loading")
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveData",
							body : data
						});
						if (ret.code > 300) {
							if (ret.code == 600) {
								MyMessageTip.msg("提示", "该物质已经存在!", true)
								this.tab.el.unmask();
							} else {
								this.processReturnMsg(ret.code, ret.msg,
										this.doLoadReport);
								this.tab.el.unmask();
							}
						} else {
							this.tab.el.unmask();
							MyMessageTip.msg("提示", "数据保存成功!", true)
							this.cfg.WZXH = ret.json.WZXH;
							this.cfg.op = "update";
							this.opener.refresh();
							this.refresh();
						}
					},
					doCreate : function() {
						if (this.midiModules["baseInfoTab"]
								.isDirty(this.cfg.op)) {
							Ext.MessageBox.show({
								title : '提示',
								msg : '数据发生变更，是否保存数据？',
								buttons : Ext.MessageBox.YESNOCANCEL,
								fn : this.messageTeate,
								icon : Ext.MessageBox.QUESTION,
								scope : this
							});
						} else {
							this.cfg.op = "create";
							this.cfg.WZXH = 0;
							this.refresh();
						}
					},
					doCancel : function() {
						if (this.cfg.op == "create") {
							if (this.midiModules["baseInfoTab"]
									.isDirty(this.cfg.op)) {
								Ext.MessageBox.show({
									title : '提示',
									msg : '数据发生变更，是否保存数据？',
									buttons : Ext.MessageBox.YESNOCANCEL,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.doSave();
										}
										if (btn == "no") {
											this.win.hide();
										}
									},
									icon : Ext.MessageBox.QUESTION,
									scope : this
								});
							} else {
								this.win.hide();
							}
						}
						if (this.cfg.op == "update") {
							if (this.midiModules["baseInfoTab"]
									.isDirty(this.cfg.op)
									|| (this.midiModules["manufacturerTab"] && this.midiModules["manufacturerTab"]
											.isDirty())
									|| (this.midiModules["itemAliasTab"] && this.midiModules["itemAliasTab"]
											.isDirty())) {
								Ext.MessageBox.show({
									title : '提示',
									msg : '数据发生变更，是否保存数据？',
									buttons : Ext.MessageBox.YESNOCANCEL,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.doSave();
										}
										if (btn == "no") {
											this.win.hide();
										}
									},
									icon : Ext.MessageBox.QUESTION,
									scope : this
								});
							} else {
								this.win.hide();
							}
						}
					},
					messageTeate : function(btn, text) {
						if (btn == "yes") {
							this.doSave();
							this.doCreate();
						}
						if (btn == "no") {
							this.refresh();
						}
					},
					refresh : function() {
						if (this.cfg.op == "create"
								&& this.midiModules["baseInfoTab"]) {
							this.midiModules["baseInfoTab"].doNew();
							this.midiModules["baseInfoTab"].initForm();
						}
						if (this.midiModules["manufacturerTab"]) {
							this.midiModules["manufacturerTab"].refresh();
						}
						if (this.midiModules["itemAliasTab"]) {
							this.midiModules["itemAliasTab"].refresh();
						}
					},
					disableZBLB : function() {
						if (this.midiModules["baseInfoTab"]) {
							this.midiModules["baseInfoTab"].updHSLB();
						}
					}
				})
