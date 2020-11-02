$package("phis.application.cic.script");

$import("phis.script.common", "phis.script.TabModule");

phis.application.cic.script.ClinicDiagnosisEntryQuickInputTab = function(cfg) {
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, phis.script.common);
	phis.application.cic.script.ClinicDiagnosisEntryQuickInputTab.superclass.constructor
			.apply(this, [cfg]);

}

Ext.extend(phis.application.cic.script.ClinicDiagnosisEntryQuickInputTab,
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
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("bodyresize", this.onBodyResize, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onBodyResize : function() {
				var height = this.opener.opener.panel.getHeight();
				var tab = this.tab.getActiveTab();
				if (tab) {
					var m = this.midiModules[tab.id];
					if (m) {
						m.grid.setHeight(height - 70);
					}
				}
				// module.grid.setHeight(height - 70);
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
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
					// var result = phis.script.rmi.miniJsonRequestSync({
					// serviceId : "moduleConfigLocator",
					// id : ref
					// })
					// if (result.code == 200) {
					// Ext.apply(cfg, result.json.body)
					// }
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
					var m = eval("new " + cls + "(cfg)")
					this.midiModules[newTab.id] = m;
					// m.onDblClick = function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id, grid
					// .getStore().getAt(index));
					// }
					m.mainApp = this.mainApp;
					m.on("choose", function(record) {
								this.fireEvent("quickInput", newTab.id, record);
							}, this)
					var p = m.initPanel();
					// p.on("rowdblclick", function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id,
					// grid.getStore().getAt(index));
					// }, this)
					newTab.add(p);
					newTab.__inited = true;
					this.fireEvent("afterTabChange", m);
					this.tab.doLayout();
				}, this])
			}
		});