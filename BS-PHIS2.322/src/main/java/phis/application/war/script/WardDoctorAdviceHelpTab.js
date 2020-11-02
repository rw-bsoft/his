/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.TableForm", "phis.script.EditorList",
		"app.modules.common", "phis.script.TabModule");

phis.application.war.script.WardDoctorAdviceHelpTab = function(cfg) {
	phis.application.war.script.WardDoctorAdviceHelpTab.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.war.script.WardDoctorAdviceHelpTab,
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
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							border : false,
							width : this.width,
							// activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							// autoHeight : true,
							defaults : {
								border : false
								// autoHeight : true,
								//autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.on("bodyresize", this.onBodyResize, this);
				// tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onBodyResize : function(panel, width, height) {
				var tab = this.tab.getActiveTab();
				if (tab) {
					var m = this.midiModules[tab.id];
					if (m) {
						var t = m.tab.getActiveTab();
						if (t) {
							if (m.midiModules[t.id]) {
								m.afterQuickInputTabChange(m.midiModules[t.id]);
							}
						}
					}
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
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
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							m.opener = this;
							m.on("quickInput", function(tabId, record) {
										this.fireEvent("quickInput", tabId,
												record);
									}, this)
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			}
		});