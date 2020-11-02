$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.list.TreeNavListView")

chis.application.conf.script.admin.UserManageListView = function(cfg) {
	cfg.initCnd = ['eq', ['s', '1'], ['s', '1']]
	chis.application.conf.script.admin.UserManageListView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.conf.script.admin.UserManageListView, chis.script.app.modules.list.TreeNavListView,
		{
			loadData : function() {
				var initCnd = ['like', ['$', 'manaUnitId'],
						['s', this.mainApp.deptId + '%']]
				this.requestData.cnd = initCnd
				chis.application.conf.script.admin.UserManageListView.superclass.loadData.call(this)
			},
			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			},
			doCreateUser : function() {
				var cfg = {}
				var exCfg = this.loadModuleCfg(this.refModule)
				Ext.apply(cfg, exCfg)
				cfg.isCombined = false
				cfg.autoLoadData = false
				cfg.autoLoadSchema = false
				cfg.mainApp = this.mainApp
				cfg.op = "create"
				var cls = cfg.script
				var module = this.midiModules["SystemUsersModule"]
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.midiModules["SystemUsersModule"] = module
					module.on("save", this.onSave, this)
					var panel = module.initPanel()
					this.panel = panel
					var win = module.getWin()
					win.add(panel)
					win.setPosition(250, 100);
					win.show()
				} else {
					Ext.apply(module, cfg)
					var win = module.getWin()
					win.setPosition(250, 100);
					win.show()
				}
			},
			doUpdateUser : function() {
				var r = this.getSelectedRecord()
				var cfg = {}
				var exCfg = this.loadModuleCfg(this.refModule)
				Ext.apply(cfg, exCfg)
				cfg.isCombined = false
				cfg.autoLoadData = false
				cfg.autoLoadSchema = false
				cfg.mainApp = this.mainApp
				cfg.logonName = r.get("logonName")
				cfg.userId = r.get("userId")
				cfg.op = "update"
				var cls = cfg.script
				var module = this.midiModules["SystemUsersModule"]
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.midiModules["SystemUsersModule"] = module
					module.on("save", this.onSave, this)
					var panel = module.initPanel()
					this.panel = panel
					var win = module.getWin()
					win.add(panel)
					win.setPosition(250, 100);
					win.show()
				} else {
					Ext.apply(module, cfg)
					var win = module.getWin()
					win.setPosition(250, 100);
					win.show()
				}
			},
			onDblClick : function() {
				this.doUpdateUser()
			},
			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id
						})

				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}

				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},
			onSave : function() {
				this.refresh()
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (this.checkIdUsed(r.get("logonName"),r.get("jobTitle"))) {// add by lis
															// 20100928
					Ext.Msg.alert("提示", "该用户正被使用，无法执行删除操作。")
					return;
				}

				Ext.Msg.show({
							title : '确认删除记录[' + r.id + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			checkIdUsed : function(id,jobTitle) {
				this.mask('用户验证中，请稍后...')
				var result = util.rmi.miniJsonRequestSync({
					serviceId : 'chis.systemUsersService',
					serviceAction:'checkUser',
					body : {
						userId : id,
						jobTitle : jobTitle
					} 
				})
				this.unmask()
				var userIdUsed = result.json.body.userIdUsed
				if(userIdUsed)
					return true
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
							serviceId : "chis.systemUsersService",
							pkey : r.id,
							schema : this.entryName,
							serviceAction:"removeUser"
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName, 'remove',
										json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			}
		})
