$package("phis.script.report")
$import("app.desktop.Module",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync")
phis.script.report.Module = function(cfg) {
	cfg.titleStyle='font-weight:bold;height:10;padding:1px 1px 1px 6px;';
	phis.script.report.Module.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.script.report.Module,app.desktop.Module, {
	initPanel: function() {
		var arg=arguments[0];
		if (this.panel&&!arg)
			return this.panel;
		var panel=this.getPanel(arg);
		if(!arg){
			this.panel = panel;
		}else
		{
			this.winpanel = panel;
		}
		return panel;
	},
	getPanel:function(arg)
	{
		var me=this;
		var module=me.getModule('ddd','chis.application.fhr.FHR/FHR/B01',arg);
		var item=module.initPanel();
		var panel = new Ext.Panel({
			border : false,
			frame : true,
			layout : 'border',
			//html:'这是一个基本Module',
			items:[{
				layout : "fit",
				split : true,
				title : '11',
				region : 'center',
				items : item
			}],
			tbar:[{text:'弹出窗口',handler:function(){me.showWin();}}]
		});
		return panel;
	},
	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						//id : this.id,
						title : this.title || this.name||'',
						width : this.width||600,
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
		return win;
},
showWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						//id : 'win_'+this.id,
						title : this.title || this.name||'',
						width : this.width||600,
						height:this.height||450,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrain : true,
						resizable : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || true,
						items : this.initPanel(true)
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
		win.show();
	
	},
	getModule:function(id,url,arg,param)
	{
		var module = this.midiModules[id];
		if (arg||!module) {
			var cfg = {};
			if(param)
				Ext.apply(cfg,param);
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(url);
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			//delete cfg.id;
			cfg.exContext=this.exContext||{};
			//cfg.id=cfg.id+''+new Date();
			var cls = cfg.script;
			$import(cls);
			module=eval("new "+cls+"(cfg)");
			module.mainApp=this.mainApp;
			module.opener=this;
			if(arg){
			this.midiModules['win_'+id] = module;
			}else{
			this.midiModules[id] = module;
			}
		}
		//console.log(module);
		return module;
	},
maximize:function()
{
	var me=this;
	var win = this.midiWins[me.zid];
	var closeAction = "hide";
	if (!win) {
		win = new Ext.Window({
					//id : this.id,
					title : this.name||'',
					width : 700,
					height :500,
					iconCls : 'icon-grid',
					shim : true,
					layout : "fit",
					animCollapse : true,
					constrain : true,
					resizable : true,
					closeAction : closeAction,
					constrainHeader : true,
					minimizable : true,
					maximizable : true,
					maximized:true,
					shadow : false,
					modal : this.modal || true,
					items : me.getPanel()
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
//		var renderToEl = this.getRenderToEl();
//		if (renderToEl) {
//			win.render(renderToEl);
//		}
		this.midiWins[me.zid] = win;
	}
	if(arguments[0])
		win.show(arguments[0].getEl());
	else
		win.show();
},
getModuleToWin:function(id,url,param)
	{
		var module = this.midiModules[id];
		if (!module) {
			var cfg = {};
			if(param)
				Ext.apply(cfg,param);
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(url);
			
			if(moduleCfg.json&&moduleCfg.json.body)
				Ext.apply(cfg, moduleCfg.json.body);
			if(moduleCfg.json&&moduleCfg.json.body&&moduleCfg.json.body.properties)
				Ext.apply(cfg, moduleCfg.json.body.properties);
			//delete cfg.id;
			cfg.exContext=this.exContext||{};
			//cfg.autoInit=false;
			cfg.autoLoadSchema=false;
			//cfg.id=cfg.id+''+new Date();
			var cls = cfg.script;
			$import(cls);
			module=eval("new "+cls+"(cfg)");
			module.mainApp=this.mainApp;
			module.opener=this;
			module.closeId=id;
			this.midiModules[id] = module;
		}
		var win = this.midiWins[id];
		var closeAction = "hide";
		if (!win) {
			var winpanel=module.initPanel();
			//console.log(winpanel);
			win = new Ext.Window({
						//id : this.id,
						title : param.title || this.name||'',
						width : param.width||600,
						height : param.height||320,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrain : true,
						resizable : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || true,
						items : winpanel
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
			this.midiWins[id] = win;
		}
		//console.log(win);
		if(!module.autoLoadData){
			module.loadData();
		}
		win.show();
	}
})
