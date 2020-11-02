$package("chis.application.conf.script.admin")

$import("app.desktop.Module")

chis.application.conf.script.admin.UserManageModule = function(cfg) {
	this.width = 600
	this.height = 400
	Ext.apply(this, app.modules.common)
	chis.application.conf.script.admin.UserManageModule.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.conf.script.admin.UserManageModule, app.desktop.Module, {
	initPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : 600,
					height : 400,
					items : [{
								layout : "fit",
								split : true,
								collapsed : false,
								title : '',
								region : 'north',
								// height : 190,
								autoHeight : true,
								items : this.getUserForm()
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								height : 210,
								// autoHeight : true,
								items : this.getProfileList()
							}],
					buttonAlign : "center",
					buttons : [{
								text : "确定",
								iconCls : "save",
								handler : this.doSave,
								scope : this
							}, {
								text : "取消",
								iconCls : "common_cancel",
								handler : function() {
									this.win.hide()
								},
								scope : this
							}]
				});
		this.panel = panel
		return panel;

	},
	doAddJobTitle : function() {
		this.midiModules[this.refForm].doAdd()
	},
	doSave : function(entryName, op, json, data) {
		if (!this.midiModules[this.refForm].validate()) {
			Ext.Msg.alert("提示消息",'请输入用户信息')
			return
		}
		var count = this.midiModules[this.refList].store.getCount()

		if (count == 0) {
			Ext.Msg.alert("提示消息","必须给该用户建立一个角色")
			return
		}

		if (this.midiModules[this.refForm].form.getForm()
				.findField("logonName").getValue() == 'system') {
			Ext.Msg.alert("提示消息","用户名已经存在")
			return
		}
		this.grid.el.mask("正在保存数据...");
		
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.systemUsersService",
					schema : "SystemUsers",
					serviceAction : "checkLogonName",
					logonName : this.midiModules[this.refForm].form.getForm()
							.findField("logonName").getValue()
				})
		if (result.code > 300) {
			this.grid.el.unmask();
			this.processReturnMsg(result.code, result.msg);
			return;
		}

		if (this.midiModules[this.refForm].op == "create"
				&& result.json.count > 0) {
			Ext.Msg.alert("提示消息","用户名已经存在")
			this.grid.el.unmask();
			return
		}
		this.setManaUnitId()

		var jobTitle = ""
		var vo = "";
		var vt = "";
		for (var i = 0; i < count; i++) {
			if (jobTitle == "") {
				jobTitle = this.midiModules[this.refList].store.getAt(i)
						.get("jobTitle")
			} else {
				jobTitle += ","
						+ this.midiModules[this.refList].store.getAt(i)
								.get("jobTitle")
			}

			vo = this.midiModules[this.refList].store.getAt(i).get("jobTitle")

			for (var j = i + 1; j < count; j++) {
				vt = this.midiModules[this.refList].store.getAt(j)
						.get("jobTitle");
				if ((vo == "05" || vo == "01") && (vt == "05" || vt == "01")) {
					Ext.Msg.alert("提示消息","同一用户不能同时为团队长和责任医生!")
					this.grid.el.unmask();
					return
				}
			}
		}

		this.midiModules[this.refForm].jobTitle = jobTitle

		var formData = this.midiModules[this.refForm].getFormData()
		var listData = this.midiModules[this.refList].getListData()
		util.rmi.jsonRequest({
					serviceId : "chis.systemUsersService",
					schema : "SystemUsersProfile",
					op : this.op,
					formData : formData,
					gridData : listData,
					serviceAction : "saveSystemUsersProfile"
				}, function(code, msg, json) {
					this.grid.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					this.loadData()
					this.fireEvent("save");
					if (this.op == 'create') {
						this.op = 'create'
					}
					if (this.op == 'update') {
						this.op = 'update'
					}

					// if (this.op == 'create') {
				// this.op = "update"
				// }
			}, this)

		// this.midiModules[this.refForm].doSave()
	},
	getUserForm : function() {
		var cfg = {}
		var exCfg = this.loadModuleCfg(this.refForm)
		Ext.apply(cfg, exCfg)
		cfg.autoLoadSchema = false
		cfg.autoLoadData = false;
		cfg.showButtonOnTop = false
		cfg.isCombined = true
		cfg.mainApp = this.mainApp
		var cls = cfg.script
		var module = this.midiModules[this.refForm]
		if (!module) {
			$import(cls)
			module = eval("new " + cls + "(cfg)");
			module.on("save", this.onSave, this)
			this.midiModules[this.refForm] = module
			var form = module.initPanel()
			this.form = form
			return form;
		}

	},
	getProfileList : function() {
		var cfg = {}
		var exCfg = this.loadModuleCfg(this.refList)
		Ext.apply(cfg, exCfg)
		cfg.autoLoadSchema = false
		cfg.autoLoadData = false;
		cfg.showButtonOnTop = true
		cfg.isCombined = true
		cfg.mainApp = this.mainApp
		cfg.formData = this.formData
		cfg.logonName = this.logonName
		cfg.userId = this.userId
		var cls = cfg.script
		var module = this.midiModules[this.refList]
		if (!module) {
			$import(cls)
			module = eval("new " + cls + "(cfg)");
			this.midiModules[this.refList] = module
			// module.on("remove", this.onRemove, this)
			module.on("save", this.onProfileSave, this);
			var grid = module.initPanel()
			this.grid = grid
			return grid;
		}

	},
	setManaUnitId : function() {
		var n = this.midiModules[this.refList].store.getCount()
		var length = this.midiModules[this.refList].store.getAt(0)
				.get("manaUnitId").length
		for (var i = 0; i < n; i++) {
			if (length >= this.midiModules[this.refList].store.getAt(i)
					.get("manaUnitId").length) {
				this.midiModules[this.refForm].manaUnitId = this.midiModules[this.refList].store
						.getAt(i).get("manaUnitId")
			}
		}
	},
	onSave : function(entryName, op, json, data) {

		this.midiModules[this.refForm].form.getForm().findField("logonName")
				.disable()
		this.midiModules[this.refList].requestData.cnd = ['eq',
				['$', 'userId'], ['s', data.userId]]
		this.midiModules[this.refList].userId = data.userId
		this.midiModules[this.refList].logonName = data.logonName
		this.midiModules[this.refList].doSave()
		this.fireEvent("save", entryName, op, json, data)
	},

	onProfileSave : function() {
		this.win.hide();
	},

	onRemove : function(jobTitle, flag) {
		if (flag) {
			var count = this.midiModules[this.refList].store.getCount()
			var jobTitle = ""
			for (var i = 0; i < count; i++) {
				if (jobTitle == "") {
					jobTitle = this.midiModules[this.refList].store.getAt(i)
							.get("jobTitle")
				} else {
					jobTitle += ","
							+ this.midiModules[this.refList].store.getAt(i)
									.get("jobTitle")
				}
			}
			this.midiModules[this.refForm].jobTitle = jobTitle
			this.midiModules[this.refForm].doSave()
		}

	},
	loadData : function() {
		if (this.op == "create") {
			this.midiModules[this.refForm].initDataId = ""
			this.userId = ""
			this.logonName = ""
			this.midiModules[this.refForm].doNew()
			this.midiModules[this.refList].requestData.cnd = ['eq',
					['$', 'userId'], ['s', this.userId]]
			this.midiModules[this.refList].loadData()
		} else {
			this.midiModules[this.refList].userId = this.userId
			this.midiModules[this.refList].logonName = this.logonName
			this.midiModules[this.refList].requestData.cnd = ['eq',
					['$', 'userId'], ['s', this.userId]]
			this.midiModules[this.refList].loadData()

			this.midiModules[this.refForm].userId = this.userId
			this.midiModules[this.refForm].logonName = this.logonName
			this.midiModules[this.refForm].initDataId = this.userId
			this.midiModules[this.refForm].loadData()
		}

	},
	loadModuleCfg : function(id) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "moduleConfigLocator",
					id : id
				})
		if (result.code != 200) {
			if (result.msg = "NotLogon") {
				this.mainApp.logon(this.loadModuleCfg, this, [id])
			}
			return null;
		}
		return result.json.body;
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						autoHeight : true,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						constrain : true,
						layout : "fit",
						plain : true,
						autoScroll : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						buttonAlign : 'center',
						modal : true,
						item : this.initPanel()
					})
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win;

	},
	onWinShow : function() {
		this.win.doLayout()
		this.loadData()
	}
})