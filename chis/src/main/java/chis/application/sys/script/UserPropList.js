$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.list.SimpleListView")

chis.application.conf.script.admin.UserPropList = function(cfg) {
	cfg.createCls = "chis.application.conf.script.admin.UserPropForm"
	cfg.updateCls = "chis.application.conf.script.admin.UserPropForm"
	this.disablePagingTbr = true
	this.title = "用户属性"
	this.actions = [{
				id : "create",
				name : "新增",
				iconCls : "add"
			}, {
				id : "update",
				name : "查看"
			}, {
				id : "remove",
				name : "删除"
			}]
	chis.application.conf.script.admin.UserPropList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.admin.UserPropList, chis.script.app.modules.list.SimpleListView, {
			doAction : function(item, e) {
				if (item.cmd == 'remove') {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.systemUserService",
								serviceAction : "checkUser",
								body : {
									userId : r.get("userId"),
									jobId : r.get("jobId"),
									manaUnitId : r.get("manaUnitId")
								}
							})
					if (result.json.body.userIdUsed == true) {
						Ext.Msg.alert("消息", "该角色正在被使用，当前操作无法执行");
						return
					}
				}
				if (item.cmd == 'update') {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					var userId = r.get("userId");
					var manaUnitId = r.get("manaUnitId");
					var jobId = r.get("jobId");
					if (userId == this.mainApp.uid
							&& manaUnitId == this.mainApp.deptId
							&& jobId == this.mainApp.jobtitleId) {
						Ext.Msg.alert("提示", "当前登录角色不允许修改!");
						return;
					}
				}

				chis.application.conf.script.admin.UserPropList.superclass.doAction.call(this, item,
						e)
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.store.remove(r)
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition((screen.width - this.width) / 2,
								(screen.height - this.height) / 2)
					}
					win.setTitle(module.title)
					win.show()
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								var userId = this.getUserId()
								module.doNew(userId)
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},

			// 获取表单中的 工号
			getUserId : function() {
				var form = this.opener.form.getForm()
				var f = form.findField(this.opener.formModule.schema.pkey)
				if (f) {
					return f.getValue()
				}
				return ""
			}

		})