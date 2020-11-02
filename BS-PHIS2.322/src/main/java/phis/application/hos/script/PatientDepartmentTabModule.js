$package("phis.application.hos.script")

$import("phis.script.TabModule")

phis.application.hos.script.PatientDepartmentTabModule = function(cfg) {
	this.width = 1100
	this.height = 600
	this.activateId = 0;// 默认打开的标签页
	this.showButtonOnTop = true
	this.frame = false;
	phis.application.hos.script.PatientDepartmentTabModule.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow",this.onWinShow,this);
}

Ext.extend(phis.application.hos.script.PatientDepartmentTabModule,
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
			onWinShow:function(){
				var m = this.midiModules["accountsDetail"];
				if (m) {
					m.doLoadReport();
				}
				m = this.midiModules["deliveryDetail"];
				if (m) {
					m.doLoadReport();
				}
				m = this.midiModules["refundDetail"];
				if (m) {
					m.doLoadReport();
				}
			},
			setParaData : function() {
				var m = this.midiModules["accountsDetail"];
				if (m) {
					m.jzrq = this.jzrq
					m.jzbs = this.jzbs;
				}
				m = this.midiModules["deliveryDetail"];
				if (m) {
					m.jzrq = this.jzrq
					m.jzbs = this.jzbs;
				}
				m = this.midiModules["refundDetail"];
				if (m) {
					m.jzrq = this.jzrq
					m.jzbs = this.jzbs;
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
					var body = this.mainApp.taskManager.loadModuleCfg(ref).json.body;
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
							m.jzrq = this.jzrq
							m.jzbs = this.jzbs;
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							m.doLoadReport();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			},
			closeCurrentTab : function() {
				if (this.win) {
					this.win.hide();
				}
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
								modal : this.modal || true,
								items : this.initPanel()
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
				}else{
					this.setParaData();
				}
				return win;
			}
		});