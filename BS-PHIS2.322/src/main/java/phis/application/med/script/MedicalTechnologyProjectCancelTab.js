$package("phis.application.med.script")
$import("phis.script.TabModule")
phis.application.med.script.MedicalTechnologyProjectCancelTab = function(cfg) {
	phis.application.med.script.MedicalTechnologyProjectCancelTab.superclass.constructor
			.apply(this, [ cfg ])
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(phis.application.med.script.MedicalTechnologyProjectCancelTab,
		phis.script.TabModule, {
			initPanel : function() {
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
					activeTab : 1,
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
			onBeforeTabChange : function(tabPanel, newTab, curTab) {
				return true;
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				this.newTabqj = newTab;
				/**
				 * 2013-06-20 gejj start
				 * 修改bug1775医技管理--医技项目执行：已收费的记录，【修改】按钮需不可用。 在页面切换时，每次都将修改按钮启用
				 */
				this.opener.panel.getTopToolbar().items.items[3]
						.setDisabled(false);
				/** 2013-06-20 gejj end */
				if (newTab.id == "zyTab") {
					var toolBar = this.opener.panel.getTopToolbar();
					var btn = toolBar.find("cmd", "CQHM");
					btn[0].setText("住院号码:");
				} else if (newTab.id == "mzTab") {
					var toolBar = this.opener.panel.getTopToolbar();
					var btn = toolBar.find("cmd", "CQHM");
					btn[0].setText("卡号:");
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
				$require(cls, [ function() {
					cfg.mainApp = this.mainApp
					var m = eval("new " + cls + "(cfg)");
					m.openBy = this.openBy;
					if (this.exContext) {
						m.exContext = this.exContext;
					}
					this.midiModules[newTab.id] = m;
					var p = m.initPanel();
					m.on("save", this.onSuperRefresh, this)
					m.opener = this;
					newTab.add(p);
					newTab.__inited = true
					this.tab.doLayout();
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
				}, this ])
			},
			onMyTabChange : function(tabPanel, newTab, curTab) {
				this.moduleLoadData(newTab.id);
			},
			moduleLoadData : function(id) {
				var curModule = this.midiModules[id];
				if (curModule) {
					curModule.strdate = this.strdate;
					curModule.enddate = this.enddate;
					curModule.carno = this.carno;
					curModule.loadData(this.strdate, this.enddate, this.carno);
				}
			},
			doCancel : function() {
				var cancelModule = this.midiModules[this.newTabqj.id];
				cancelModule.doCancel();
			}
		})
