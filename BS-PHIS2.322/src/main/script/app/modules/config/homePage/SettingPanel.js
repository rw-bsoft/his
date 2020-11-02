$package("app.modules.config.homePage")

$styleSheet("index.indexPage")
$import("app.desktop.Module", "app.modules.common",
		"util.rmi.miniJsonRequestSync")

app.modules.config.homePage.SettingPanel = function(cfg) {
	this.pfix = ".png"
	this.serviceId = "settingService"
	this.pModules = {}
	Ext.apply(this, app.modules.common)
	app.modules.config.homePage.SettingPanel.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(app.modules.config.homePage.SettingPanel, app.desktop.Module, {
	init : function() {
		this.addEvents({
					"loadData" : true
				})
	},

	initPanel : function() {
		var leftData = [{
					id : 'app',
					title : '角色切换',
					iconCls : 'app'
				}, {
					id : 'psw',
					title : '密码修改',
					iconCls : 'lock'
				}, {
					id : 'pic',
					title : '头像修改',
					iconCls : 'pic'
				}, {
					id : 'imc',
					title : '输入法切换',
					iconCls : 'input'
				}]
		var leftView = this.getAppView(leftData)
		leftView.on("click", this.onLeftViewClick, this)

		var topView = this.getRoleView([])
		topView.on("click", this.onTopViewClick, this)
		this.topView = topView

		var panel = new Ext.Panel({
			width : 527,
			height : 253,
			border : false,
			frame : false,
			headerStyle : this.initBackground("setting_header.png",
					"height:25px;border:0px;padding-top:10px"),
			bodyStyle : this.initBackground("setting_body.png",
					"height:214px;padding:0px 20px"),
			tools : [{
						id : 'sclosed',
						handler : function() {
							this.win.hide()
						},
						scope : this
					}],
			layout : 'table',
			layoutConfig : {
				columns : 2
			},
			defaults : {
				bodyStyle : 'background:none;padding-left:2px;overflow-y:hidden;overflow-x:auto;',
				border : false,
				frame : false
			},
			items : [{
						bodyStyle : 'background:none;padding:0 20px 0 10px',
						height : 200,
						width : 190,
						items : leftView
					}, {
						bodyStyle : 'background: none;padding:10px 10px 0 0',
						width : 300,
						height : 200,
						items : topView
					}]
		});
		this.panel = panel;
		return panel
	},

	getAppTpl : function() {
		var home = ClassLoader.stylesheetHome + "index/images"
		var tpl = this.appTpl
		if (!tpl) {
			tpl = new Ext.XTemplate('<tpl for=".">', '<ul>', '<li><img src="'
							+ home + '/{iconCls}' + this.pfix
							+ '" width="32" height="32" /></li>',
					'<li>{title}</li>', '</ul>', '</tpl>')
			this.appTpl = tpl;
		}
		return tpl;
	},

	getAppView : function(data, exCfg) {
		var store = new Ext.data.Store({
					reader : new Ext.data.JsonReader({}, ["id", 'title',
									'iconCls']),
					data : data
				})
		var cfg = {
			store : store,
			tpl : this.getAppTpl(),
			cls : 'setbody',
			itemSelector : 'ul',
			overClass : 'mbselect',
			singleSelect : true
		}
		if (exCfg) {
			Ext.apply(cfg, exCfg)
		}
		return new Ext.DataView(cfg)
	},

	getRoleTpl : function() {
		var home = ClassLoader.stylesheetHome + "index/images"
		var tpl = this.roleTpl
		if (!tpl) {
			tpl = new Ext.XTemplate('<tpl for=".">', '<ul>', '<li>',
					'<img src="' + home + '/{iconCls}' + this.pfix
							+ '" width="32" height="32" />',
					'<span>{deptTitle}<br><b>{roleTitle}</b></span>', '</li>',
					'</ul>', '</tpl>')
			this.roleTpl = tpl;
		}
		return tpl;
	},

	getRoleView : function(data, exCfg) {
		var store = new Ext.data.Store({
					reader : new Ext.data.JsonReader({}, ["id", 'deptTitle',
									'roleTitle', 'iconCls']),
					data : data
				})
		var cfg = {
			store : store,
			tpl : this.getRoleTpl(),
			cls : 'rolebody',
			itemSelector : 'ul',
			overClass : 'utselectd',
			singleSelect : true,
			autoScroll : true,
			width : 300,
			height : 190
		}
		if (exCfg) {
			Ext.apply(cfg, exCfg)
		}
		return new Ext.DataView(cfg)
	},

	onLeftViewClick : function(view, index, node, e) {
		var id = view.store.getAt(index).data.id
		switch (id) {
			case "app" :
				this.doSwitchApp()
				break
			case "psw" :
				this.doChangePsw()
				break
			case "pic" :
				this.doSettingPic()
				break;
			case "imc" :
				this.doSettingImc()
				break;
		}
	},

	doSwitchApp : function() {
		var topData = this.loadData("loadRoles")
		if (topData.length == 0) {
			return
		}
		this.topView.getStore().loadData(topData)
	},

	loadRoles : function() {
		this.doSwitchApp()
	},

	doChangePsw : function() {
		var m = this.pModules['psw']
		if (!m) {
			$import("app.modules.config.homePage.PasswordChanger")
			m = new app.modules.config.homePage.PasswordChanger({});
			m.setMainApp(this.mainApp)
			this.pModules['psw'] = m
			var p = m.initPanel()
			if (!p) {
				return
			}
		}
		var win = m.getWin()
		if (win) {
			win.show()
		}
	},
	// 设置输入法
	doSettingImc : function() {
		var m = this.createModule("imc", "phis.application.cfg.CFG/CFG/CFG91");
		var win = m.getWin()
		if (win) {
			win.show()
		}
	},
	doSettingPic : function() {
		var cls = "app.modules.config.homePage.ImageChangePanel"
		var m = this.pModules['pic']
		if (!m) {
			$import(cls)
			m = eval("new " + cls + "({})")
			m.setMainApp(this.mainApp)
			this.pModules['pic'] = m
		}
		var win = m.getWin()
		if (win) {
			win.show()
		}
	},

	onTopViewClick : function(view, index, node, e) {
		var urt = view.store.getAt(index).data.id
		var token = this.loadData("loadUsertoken", urt)
		$logon.doChangeUserRoleToken(urt, token)
		$logon.doLoadAppDefines(urt, 2)

	},

	loadData : function(cmd, domain) {
		var cfg = {
			serviceId : this.serviceId,
			method : cmd
		}
		if (domain) {
			cfg.body = [domain]
		}
		var re = util.rmi.miniJsonRequestSync(cfg)
		if (re.code != 200) {
			if (re.msg == "NotLogon") {
				this.mainApp.logon(this.loadData, this, [cmd, domain])
			}
			return null;
		}
		return re.json.body
	},

	initBackground : function(id, css) {
		var bg = "background:url(" + ClassLoader.stylesheetHome
				+ "index/images/" + id + ")"
		if (css) {
			bg = bg + ";" + css
		}
		return bg
	},

	messageDialog : function(title, msg, icon) {
		Ext.MessageBox.show({
					title : title,
					msg : msg,
					buttons : Ext.MessageBox.OK,
					icon : icon
				})
	},

	initDataView : function() {
		this.topView.getStore().loadData([])
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						frame : false,
						border : false,
						closable : false,
						autoWidth : true,
						// width: 527,
						autoHeight : true,
						resizable : false,
						layout : "fit",
						closeAction : 'hide',
						shadow : false,
						plain : true,
						items : this.initPanel(),
						modal : true
					})
			win.on("show", function() {
						this.initDataView()
						this.fireEvent("winShow")
					}, this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		return win;
	},
	createModule : function(moduleName, moduleId, exCfg) {
		var item = this.midiModules[moduleName]
		if (!item) {
			var moduleCfg = null;;
			var res = this.mainApp.taskManager.loadModuleCfg(moduleId);
			if (!res.code) {
				moduleCfg = res;
			} else if (res.code != 200) {
				Ext.MessageBox.alert("错误", res.msg)
				return
			}
			if (!moduleCfg) {
				moduleCfg = res.json.body;
			}
			var cfg = {
				showButtonOnTop : true,
				border : false,
				frame : false,
				autoLoadSchema : false,
				isCombined : true,
				exContext : {}
			};
			Ext.apply(moduleCfg, moduleCfg.properties);
			Ext.apply(cfg, exCfg);
			Ext.apply(cfg, moduleCfg);
			var cls = moduleCfg.script;
			if (!cls) {
				return;
			}
			if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
				return;
			}
			$import(cls);
			item = eval("new " + cls + "(cfg)");
			item.setMainApp(this.mainApp);
			this.midiModules[moduleName] = item;
		}
		return item;
	}
})