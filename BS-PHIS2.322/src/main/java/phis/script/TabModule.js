$package("phis.script")

$import("phis.script.SimpleModule")

phis.script.TabModule = function(cfg) {
	this.width = 1100
	this.height = 600
	this.activateId = 0;
	this.showButtonOnTop = true
	this.frame = false;
	phis.script.TabModule.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.script.TabModule, phis.script.SimpleModule, {
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
							// modify by yangl
						})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
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

			onSuperRefresh : function(entryName, op, json, rec) {
			},
			loadModuleCfg : function(id) {
				var result = phis.script.rmi.miniJsonRequestSync({
							url : 'app/loadModule',
							id : id
						})
				if (result.code != 200) {
					if (result.msg == "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				var m = result.json.body
				Ext.apply(m, m.properties)
				return m
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
		})
