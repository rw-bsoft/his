$package("phis.application.sas.script");

$import("phis.script.TabModule");

phis.application.sas.script.BusinessDocumentDetailTabModule = function(cfg) {
	phis.application.sas.script.BusinessDocumentDetailTabModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sas.script.BusinessDocumentDetailTabModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					if (this.mainApp['phis'].treasuryId == null
							|| this.mainApp['phis'].treasuryId == ""
							|| this.mainApp['phis'].treasuryId == undefined) {
						Ext.Msg.alert("提示", "未设置登录库房,请先设置");
						return null;
					}
					if (this.mainApp['phis'].treasuryEjkf != 0) {
						Ext.MessageBox.alert("提示", "该库房不是一级库房!");
						return;
					}
					if (this.mainApp['phis'].treasuryCsbz != 1) {
						Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
						return;
					}
				}
				if (this.tab) {
					return this.tab;
				}
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
					tabPosition : this.tabPosition || "top",
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
				$require(cls, [ function() {
					var m = eval("new " + cls + "(cfg)");
					m.setMainApp(this.mainApp);
					if (this.exContext) {
						m.exContext = this.exContext;
					}
					m.opener = this;
					this.midiModules[newTab.id] = m;
					var p = m.initPanel();
					if(newTab.id=="Plan"){
						m.doRefresh();
					}
					m.on("save", this.onSuperRefresh, this)
					newTab.add(p);
					newTab.__inited = true
					this.tab.doLayout();
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
				}, this ])
			}
		});