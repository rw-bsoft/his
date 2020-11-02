$package("phis.application.cfg.script")

/**
 * 病人性质module zhangyq 2012.5.25
 */
$import("phis.script.TabModule")

phis.application.cfg.script.ConfigPatientPropertiesTabModule = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.summaryable = true;
	this.oldTab = "";
	phis.application.cfg.script.ConfigPatientPropertiesTabModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigPatientPropertiesTabModule,
		phis.script.TabModule, {
			setBrxz : function(brxz) {
				this.brxz = brxz;
				var activeTab = this.tab.getActiveTab();
				var activeModule = this.midiModules[activeTab.id];
				if ("conceitProportionTab" == activeTab.id) {
					this.mainApp.BRXZ = brxz;
					activeModule.requestData.cnd = ['eq', ['$', 'BRXZ'],
							['s', brxz]];
					activeModule.loadData();
				} else {
					this.moduleLoad(activeModule, brxz);
				}
			},
			moduleLoad : function(activeModule, brxz) {
				var store = activeModule.grid.getStore();
				var n = store.getCount()
				var save = false;
				if (activeModule.xyyp.length != n
						&& activeModule.xyyp.length != 0) {
					save = true;
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						save = true;
						break;
					}
				}
				if (save) {
					Ext.Msg.show({
								title : '提示',
								msg : '是否保存已作的修改?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										if (this.doSave()) {
											this.xyyp = [];
											if (this.mainApp.BRXZ != brxz) {
												this.mainApp.BRXZ = brxz;
												this.requestData.cnd = ['eq',
														['$', 'BRXZ'],
														['s', brxz]];
												this.loadData();
											}
										}
									} else {
										this.xyyp = [];
										this.mainApp.BRXZ = brxz;
										this.requestData.cnd = ['eq',
												['$', 'BRXZ'], ['s', brxz]];
										this.loadData();
									}
								},
								scope : activeModule
							})
				} else {
					this.mainApp.BRXZ = brxz;
					activeModule.requestData.cnd = ['eq', ['$', 'BRXZ'],
							['s', brxz]];
					activeModule.loadData();
				}
			},
			loadZfbl : function(brxz) {
				this.mainApp.BRXZ = brxz;
				if (this.midiModules["conceitProportionTab"]) {
					this.midiModules["conceitProportionTab"].requestData.cnd = [
							'eq', ['$', 'BRXZ'], ['s', brxz]];
					this.midiModules["conceitProportionTab"].loadData();
				}
			},
			initPanel : function() {
				this.mainApp.BRXZ = 0;
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
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							// autoHeight : true,
							defaults : {
								border : false,
								// autoHeight : true,
								autoWidth : true
							},
							items : tabItems
						})
				//tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.beforetabchange, this);
				//tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			beforetabchange : function(tabPanel, newTab, curTab){
				if (this.oldTab) {
					if("conceitProportionTab" == this.oldTab.id){
						var oldModule = this.midiModules[this.oldTab.id];
						var cm = oldModule.grid.stopEditing();
					}
					var activeModule = this.midiModules[this.oldTab.id];
					var store = activeModule.grid.getStore();
					var n = store.getCount()
					var save = false;
					if (activeModule.xyyp) {
						if (activeModule.xyyp.length != n
								&& activeModule.xyyp.length != 0) {
							save = true;
						}
					}
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (r.dirty) {
							save = true;
							break;
						}
					}
					if (save) {
						var this_ = this;
						Ext.Msg.show({
									title : '提示',
									msg : '是否保存已作的修改?',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											this.doSave();
											this.xyyp = [];
											for (var i = 0; i < n; i++) {
												var r = store.getAt(i)
												if (r.dirty) {
													store.remove(r);
													i--;
													n--;
												}
											}
											this_.tab.setActiveTab(newTab);
										} else {
											this.xyyp = [];
											for (var i = 0; i < n; i++) {
												var r = store.getAt(i)
												if (r.dirty) {
													store.remove(r);
													i--;
													n--;
												}
											}
											this_.tab.setActiveTab(newTab);
										}
									},
									scope : activeModule
								})
								return false;
					}
					this.onTabChange(tabPanel, newTab, curTab);
					return true;
				}
				this.onTabChange(tabPanel, newTab, curTab);
				return true;
			},
			tabChanged : function(activeModule) {
				var store = activeModule.grid.getStore();
				var n = store.getCount()
				var save = false;
				if (activeModule.xyyp) {
					if (activeModule.xyyp.length != n
							&& activeModule.xyyp.length != 0) {
						save = true;
					}
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						save = true;
						break;
					}
				}
				if (save) {
					Ext.Msg.show({
								title : '提示',
								msg : '是否保存已作的修改?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.doSave();
										this.xyyp = [];
									} else {
										this.xyyp = [];
									}
								},
								scope : activeModule
							})
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.brxz) {
						this.mainApp.BRXZ = this.brxz;
						var newModule = this.midiModules[newTab.id];
						newModule.requestData.cnd = ['eq', ['$', 'BRXZ'],
								['s', this.brxz]];
						newModule.loadData();
					}
					this.oldTab = newTab;
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
					var res = this.mainApp.taskManager.loadModuleCfg(ref);
					Ext.apply(cfg, res.json.body)
					Ext.apply(cfg, res.json.body.properties)
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)");
									m.setMainApp(this.mainApp);
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									m.on("save", this.onSuperRefresh, this)
									if ("CFG040202" == m.id
											|| "CFG040203" == m.id) {
										m.afterOpen();
									}
									newTab.add(p);
									newTab.__inited = true
									this.oldTab = newTab;
									this.tab.doLayout();
								}, this])
			}
		})