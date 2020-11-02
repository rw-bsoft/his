$package("phis.application.fsb.script")
$import("phis.script.TabModule", "phis.script.rmi.miniJsonRequestSync")

phis.application.fsb.script.FsbDetailsTabModule = function(cfg) {
	this.width = 600
	this.height = 500
	this.showButtonOnTop = true
	this.requestData = {};
	phis.application.fsb.script.FsbDetailsTabModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.fsb.script.FsbDetailsTabModule, phis.script.TabModule, {
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
		tab.on("afterrender", this.onReady, this);
		tab.activate(this.activateId)
		this.tab = tab
		return tab;
	},
	onReady : function() {
		// this.doWhole();
//		this.win.title = "明细项目【"+this.data.ZYGB_text+"】";
		this.on("winShow", this.onWinShow, this);
	},
	onWinShow : function() {
		this.win.setTitle("明细项目【"+this.data.ZYGB_text+"】");
		this.refresh();
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
//							if(this.requestData.cnd){
							 	this.refresh();
//							 }
						}, this])
	},
	refresh : function() {
		if (this.midiModules['sfxm']) {
			this.midiModules['sfxm'].list.data = this.data;
			this.midiModules['sfxm'].list.clear();
			this.midiModules['sfxm'].DetailsList.clear();
			this.midiModules['sfxm'].list.refresh();
		}
		if (this.midiModules['mxxm']) {
			this.midiModules['mxxm'].list.data = this.data;
			this.midiModules['mxxm'].list.clear();
			this.midiModules['mxxm'].DetailsList.clear();
			this.midiModules['mxxm'].list.refresh();
		}
	},
	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : "明细项目【"+this.data.ZYGB_text+"】",
						width : 1024,
						height : 500,
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
		}
		win.instance = this;
		return win;
	}
})
