$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.CaseHistoryControlLList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.listServiceId = "caseHistoryControlService";
	cfg.removeServiceId = "caseHistoryControlService";
	cfg.removeAction = "removeDoctorRoles";
	cfg.serverParams = {
		serviceAction : "listDoctorRoles"
	}
	phis.application.cfg.script.CaseHistoryControlLList.superclass.constructor.apply(
			this, [cfg])
	this.removeByFiled="JSMC";
	this.on("beforeRemove",this.beforeRemove,this);
}

Ext.extend(phis.application.cfg.script.CaseHistoryControlLList,
		phis.script.SimpleList, {
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					win.setTitle(module.title)
					win.show()
					if (this.winState) {
						if (this.winState == 'center') {
							win.center();
						} else {
							xy = this.winState;
							win.setPosition(this.xy[0] || xy[0], this.xy[1]
											|| xy[1])
						}
					} else {
						var default_xy = win.el.getAlignToXY(win.container,
								'c-c');
						win.setPagePosition(default_xy[0], default_xy[1] - 100);
					}
					module.on("beforeSave",this.onBeforeSave,this);
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doNew()
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			onDblClick : function() {
			},
			onBeforeSave : function(entryName, op, saveData) {
				if(op=="update"){
					return true;
				}
				var jsmc=saveData.JSMC;
				var store=this.store;
				var flag=true;
				store.each(function(r){
					if(jsmc==r.get("JSMC")){
						flag=false;
					}
				},this);
				return flag;
			},
			beforeRemove:function(entryName,record){
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "caseHistoryControlService",
							serviceAction : "checkDocRoleUsed",
							method : "execute",
							pkey : record.id
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.beforeRemove);
					return
				}
				var flag=true;
				if (r.json.flag!=null) {
					flag = r.json.flag
				}
				return flag;
			},
			processRemove : function(r) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					MyMessageTip.msg("提示", "该医疗角色已被用户使用！", true);
					return;
				}
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.removeServiceId,
							serviceAction : this.removeAction,
							method : "execute",
							pkey : r.id,
							schema : this.entryName
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			}
		});