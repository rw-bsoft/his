$package("phis.application.fsb.script");

$import("phis.script.TabModule")

phis.application.fsb.script.FamilySickBedAdviceTab = function(cfg) {
	phis.application.fsb.script.FamilySickBedAdviceTab.superclass.constructor
			.apply(this, [cfg])
	this.on("tabchange", this.onMyTabChange, this);
}

Ext.extend(phis.application.fsb.script.FamilySickBedAdviceTab,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								frame : this.frame,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id + "_" + this.openBy
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							// frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							// autoHeight : true,
							defaults : {
								border : false
								// autoHeight : true,
								// autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onBeforeTabChange : function(tabPanel, newTab, curTab) {
				if (curTab && this.midiModules[curTab.id]) {
					var m = this.midiModules[curTab.id];
					var rs = m.store.getModifiedRecords();
					for (var i = 0; i < rs.length; i++) {
						if (rs[i].get("YPXH")) {
							if (confirm('当前医嘱数据已经修改，是否保存?')) {
								return this.opener.doSave()
							} else {
								m.store.rejectChanges();
								m.refresh();
								return true;
							}
						}
					}
					m.removeEmptyRecord();
				}
				return true;
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab) {
					this.newTableft = newTab;
				}
				if (newTab.__inited) {
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
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
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)");
							m.setMainApp(this.mainApp);
							m.openBy = this.openBy;
							m.opener = this.opener;
							this.mc = m;
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			},
			onMyTabChange : function(tabPanel, newTab, curTab) {
				this.moduleLoadData(newTab.id);
				var curModule = this.midiModules[newTab.id];
				var colModule = curModule.grid.getColumnModel();
				if (curModule.adviceType == "longtime") {
					// setDisabled()方法有样式控制'x-item-disabled'，因为浏览器版本对样式显示效果不同，所以暂且用show和hide解决
					this.opener.panel.getTopToolbar().items.items[4].show();
					this.opener.panel.getTopToolbar().items.items[5].show();
					this.opener.panel.getTopToolbar().items.items[6].show();
				} else {
					this.opener.panel.getTopToolbar().items.items[4].hide();
					this.opener.panel.getTopToolbar().items.items[5].hide();
					this.opener.panel.getTopToolbar().items.items[6].hide();
				}
				if (this.openBy == 'doctor') {
					this.opener.panel.getTopToolbar().items.items[7].hide();
					this.opener.panel.getTopToolbar().items.items[8].show();
					this.opener.panel.getTopToolbar().items.items[9].hide();
					this.opener.panel.getTopToolbar().items.items[13].hide();
				} else {
					this.opener.panel.getTopToolbar().items.items[7].show();
					this.opener.panel.getTopToolbar().items.items[8].hide();
					this.opener.panel.getTopToolbar().items.items[9].show();
					colModule.setHidden(colModule.getIndexById("YSTJ"), true);
				}
			},
			moduleLoadData : function(id) {
				var curModule = this.midiModules[id];
				if (curModule) {
					// alert(curModule.initDataId + " : " +
					// this.opener.initDataId);
					// if (curModule.initDataId !=
					// this.opener.initDataId) {
					curModule.exContext = this.exContext;
					curModule.initDataId = this.opener.initDataId;
					if (curModule.requestData) {
						var lsyz = curModule.adviceType == "longtime" ? 0 : 1;
						var cnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'eq',
														['$', 'a.ZYH'],
														[
																'd',
																this.opener.initDataId]],
												['eq', ['$', 'a.LSYZ'],
														['i', lsyz]]],
										['eq', ['$', 'a.LSBZ'], ['i', 0]]],
								['and', ['eq', ['$', 'a.YZPB'], ['i', 0]],
										['eq', ['$', 'a.ZFBZ'], ['i', 0]]]];
						if (this.openBy == 'nurse') {
							cnd = [
									'and',
									[
											'and',
											cnd,
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'a.YSBZ'],
																	['i', 1]],
															[
																	'eq',
																	['$',
																			'a.YSTJ '],
																	['i', 1]]],
													['eq', ['$', 'a.YSBZ'],
															['i', 0]]]],
									['eq', ['$', 'a.LSYZ'], ['i', lsyz]]]
						} else {
							cnd = ['and', cnd,
									['eq', ['$', 'a.YSBZ'], ['i', 1]]]
						}
						curModule.initCnd = cnd;
						curModule.requestData.cnd = cnd;
					}
					curModule.loadData();
					// }

				}
			}
		})
