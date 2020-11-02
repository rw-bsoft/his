$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.list.SimpleListView", "util.dictionary.DictionaryLoader","chis.application.conf.script.admin.UserManaUnitReplaceFormView")

chis.application.conf.script.admin.UserManageProfileListView = function(cfg) {
	this.entryName = "SystemUsersProfile"
	this.disablePagingTbr = true
	this.removeServiceId = "chis.simpleRemove"
	chis.application.conf.script.admin.UserManageProfileListView.superclass.constructor.apply(this,
			[cfg])
	this.on("loadData",this.onLoadData,this)
}
Ext.extend(chis.application.conf.script.admin.UserManageProfileListView,
		chis.script.app.modules.list.SimpleListView, {
			getListData : function() {
				var array = []
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i)
					array.push(this.clone(r.data))
				}
				return array;
			},
			clone:function( obj ){
				var result={};
				for(var i in obj) {
					result[i]   =   obj[i] 
				} 
				return result 
			},
			onLoadData:function(){
				this.listData = this.getListData();
				//alert(Ext.encodethis.listData)
			},
			getSavedUserProfile:function(profileId){
				if(this.listData && profileId){
					for(var i =0;i<this.listData.length ; i++){
						var r = this.listData[i];
						if(r["id"]==profileId)
							return r;
					}
				}
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (this.store.getCount() == 1) {
					Ext.Msg.alert("提示","用户必须拥有一个角色，此角色不允许删除")
					return
				}
				if (this.mainApp.jobtitleId != 'system') {
					if (r.get("jobTitle") == "03" || r.get("jobTitle") == "09"
							|| r.get("jobTitle") == "10"
							|| r.get("jobTitle") == "11"
							|| r.get("jobTitle") == "12"
							|| r.get("jobTitle") == "13") {
						Ext.Msg.alert("提示","不能维护该角色")
						return
					}
				}
				if (!r.get("id")) {
					var rowIndex = this.store.indexOf(r)
					this.store.remove(r)
					rowIndex = rowIndex >= this.store.getCount()
							? rowIndex - 1
							: rowIndex;
					this.grid.getSelectionModel().selectRow(rowIndex);
					this.store.commitChanges();
					// this.fireEvent("remove", r.get("jobTitle"),false)
				} else {
					this.processRemove()
					// this.fireEvent("remove", r.get("jobTitle"), true)
				}
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.removeRecord = r ;
				var profileId = r.get("id")
				var oldRec = this.getSavedUserProfile(profileId);
				
				//检查是否有管辖档案.
				this.mask("正在查询,请稍后...")
				util.rmi.jsonRequest({
						serviceId : "chis.systemUsersService",
						serviceAction : "checkUser",
						body : {
							userId : oldRec["logonName"],
							jobTitle : oldRec["jobTitle"]
						}
					}, function(code, msg, json) {
						this.unmask()
						if (code != 200) {
							this.processReturnMsg(code, msg);
							return;
						}
						var body = json.body;
						if (body.userIdUsed == true) {
							replaceFormView = new chis.application.conf.script.admin.UserManaUnitReplaceFormView({
									mainApp :this.mainApp,
									manaUnitId:oldRec["manaUnitId"],
									logonName:oldRec["logonName"],
									jobTitle :oldRec["jobTitle"]
								});
							replaceFormView.form= undefined;
							replaceFormView.win= undefined;
							replaceFormView.on("save",this.removeRole,this)
							replaceFormView.initPanel()
							replaceFormView.getWin().show();
						} else{
							this.removeRole();
						}
				}, this)
			},
			
			removeRole:function(entryName, op, json, data){
				//删除角色.
				this.store.remove(this.removeRecord)
				var userJobTitle = this.getUserJobTitle()
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
						serviceId : "chis.systemUsersService",
						schema : "SystemUsersProfile",
						serviceAction : "removeSystemUsersProfile",
						body : {
							oldRecord:this.removeRecord.data,
							replace:data,
							userJobTitle:userJobTitle
						}
					}, function(code, msg, json) {
					this.unmask()
					if (code < 300) {
						
						this.fireEvent("remove", this.entryName, 'remove',
								json, this.removeRecord.data)
					} else {
						this.processReturnMsg(code, msg, this.doRemove)
					}
				}, this)
			},
			getUserJobTitle:function(){
				var array = []
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i)
					array.push(r.get("jobTitle"))
				}
				return array.join(",");
			}
			,
			doAdd : function() {
				var cfg = {}
				var exCfg = this.loadModuleCfg(this.refForm)
				Ext.apply(cfg, exCfg)
				cfg.isCombined = false
				cfg.autoLoadData = false
				cfg.autoLoadSchema = false
				cfg.showButtonOnTop = true
				cfg.mainApp = this.mainApp
				cfg.op = "create"
				var cls = cfg.script
				var module = this.midiModules[this.refForm]
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.midiModules[this.refForm] = module
					module.on("add", this.onAdd, this)
					module.on("modify", this.onModify, this)
					var panel = module.initPanel()
					this.panel = panel
					var win = module.getWin()
					this.win = win
					win.add(panel)
					win.setPosition(330, 200);
					win.show()
				} else {
					Ext.apply(module, cfg)
					var win = module.getWin()
					this.win = win
					win.setPosition(330, 200);
					win.show()
				}
			},
			onAdd : function(data) {
				var r = {}
				r["manaUnitId"] = data.manaUnitId.key
				r["manaUnitId_text"] = data.manaUnitId.text
				r["regionCode"] = data.regionCode.key
				r["regionCode_text"] = data.regionCode.text
				r["jobTitle"] = data.jobTitle.key
				r["jobTitle_text"] = data.jobTitle.text

				var record = new Ext.data.Record(r)

				for (var i = 0; i < this.grid.getStore().getCount(); i++) {
					var r = this.grid.getStore().getAt(i);
					var vt = r.get("jobTitle");
					var vk = data.jobTitle.key
					if (vt == vk) {
						Ext.Msg.alert("提示消息", "角色已经存在")
						return
					}
					if ((vk == "05" && vt == "01")
							|| (vk == "01" && vt == "05")) {
						Ext.Msg.alert("提示消息", "同一用户不能同时为团队长和责任医生!")
						return
					}

				}

				this.midiModules[this.refForm].win.hide()
				this.grid.getStore().add(record)
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			doModify : function() {
				var record = this.getSelectedRecord()
				if (record == null) {
					return
				}
				var oldRec = this.getSavedUserProfile(record.get("id"));
				var cfg = {}
				var exCfg = this.loadModuleCfg(this.refForm)
				Ext.apply(cfg, exCfg)
				cfg.isCombined = false
				cfg.autoLoadData = false
				cfg.autoLoadSchema = false
				cfg.showButtonOnTop = true
				cfg.mainApp = this.mainApp
				cfg.op = "update"
				cfg.record = record
				cfg.oldRec= oldRec ;
				var cls = cfg.script
				var module = this.midiModules[this.refForm]
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.midiModules[this.refForm] = module
					module.on("modify", this.onModify, this)
					module.on("add", this.onAdd, this)
					var panel = module.initPanel()
					this.panel = panel
					var win = module.getWin()
					this.win = win
					win.add(panel)
					win.setPosition(330, 200);
					win.show()
				} else {
					Ext.apply(module, cfg)
					var win = module.getWin()
					this.win = win
					win.setPosition(330, 200);
					win.show()
				}
			},
			onModify : function(data) {
				var r = this.getSelectedRecord()

				var count = this.grid.getStore().getCount();

				for (var i = 0; i < count; i++) {
					var record = this.grid.getStore().getAt(i)
					var vt = record.get("jobTitle");
					var vk = data.jobTitle.key;
					var vg = r.get("jobTitle");
					if (vt == vk && vt != vg) {
						Ext.Msg.alert("提示消息", "角色已经存在")
						return
					}

					if ((vk == "05" && vt == "01")
							|| (vk == "01" && vt == "05")) {
						if (vg == vk) {
							Ext.Msg.alert("提示消息", "同一用户不能同时为团队长和责任医生!")
							return
						}
					}
				}

				r.set("manaUnitId", data.manaUnitId.key)
				r.set("manaUnitId_text", data.manaUnitId.text)
				r.set("regionCode", data.regionCode.key)
				r.set("regionCode_text", data.regionCode.text)
				r.set("jobTitle", data.jobTitle.key)
				r.set("jobTitle_text", data.jobTitle.text)

				r.set("newDoctor", data.newDoctor)
				r.set("newManaUnitId", data.newManaUnitId)
				r.set("oldJobTitle", data.oldJobTitle)

				r.commit()
				this.midiModules[this.refForm].win.hide()

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
				return result.json.body;
			}
		})
