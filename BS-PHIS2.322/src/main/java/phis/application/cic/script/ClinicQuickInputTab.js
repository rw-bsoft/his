$package("phis.application.cic.script");

$import("phis.script.common", "phis.script.TabModule");

phis.application.cic.script.ClinicQuickInputTab = function(cfg) {
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, app.modules.common);
	phis.application.cic.script.ClinicQuickInputTab.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cic.script.ClinicQuickInputTab,
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
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "bottom",
							defaults : {
								border : false
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

			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
					this.fireEvent("afterTabChange",
							this.midiModules[newTab.id]);
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
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							m.on("quickInput", this.onQuickInput, this)
							m.on("afterTabChange",
									this.afterQuickInputTabChange, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("afterTabChange", m);
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			},
			afterQuickInputTabChange : function(module) {
				var height = this.opener.panel.getHeight();
				if (module.grid) {
					module.grid.setHeight(height - 70);
				}
			},
			onQuickInput : function(newTab, grid) {
				this.fireEvent("quickInput", newTab, grid);
			},
			onSuperRefresh : function(entryName, op, json, rec) {
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			}
		});