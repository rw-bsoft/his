/**
 * @include "BizListView.js" chenhuabin
 */
$package("chis.application.dr.script")

$import("app.modules.list.BizSimpleListView")

chis.application.dr.script.BizListView = function(cfg) {
	this.msgBoxWidth=300;
	chis.application.dr.script.BizListView.superclass.constructor.apply(this, [cfg]);
	this.showButtonOnTop = true;
	this.isCombined = true;
}
Ext.extend(chis.application.dr.script.BizListView, app.modules.list.SimpleListView, {
			init : function() {
				if (this.solrCore) {
					this.serverParams = {
						solrCore : this.solrCore
					};
				}
				Ext.apply(this,this.properties);
				chis.application.dr.script.BizListView.superclass.init.call(this);

			},
			processRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var removeRequest = this.getRemoveRequest(r); // 获取删除条件数据
				if (!this.fireEvent("beforeRemove", this.entryName,
						removeRequest)) {
					return;
				}
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
							method:"execute",
							serviceId : this.removeServiceId,
							action : this.removeAction || "remove",
							schema : this.entryName,
							body : removeRequest
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.store.remove(r);
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
							} else {
								this.processReturnMsg(code, msg, this.doRemove);
							}
						}, this);
			},

			/**
			 * 获取删除操作的请求数据
			 * 
			 * @param {}
			 *            r
			 * @return {}
			 */
			getRemoveRequest : function(r) {
				return {
					pkey : r.id,
					mpiId : r.get("mpiId")
				};
			},
			mask : function(msg) {
				if (this.grid && this.grid.el) {
					this.grid.el.mask(msg, "x-mask-loading")
				}
			},
			unmask : function() {
				if (this.grid && this.grid.el) {
					this.grid.el.unmask()
				}
			},
//			loadRemote : function(ref, btn) {
//				if (this.loading) {
//					return
//				}
//				var r = this.getSelectedRecord()
//				var cmd = btn.cmd
//				if (cmd == "update" || cmd == "read") {
//					if (r == null) {
//						return
//					}
//				}
//				var cfg = {}
//				cfg._mId = this.grid._mId // 增加module的id
//				cfg.title = this.title + '-' + btn.text
//				cfg.op = cmd
//				cfg.openWinInTaskbar = false
//				cfg.autoLoadData = false;
//				cfg.exContext = {}
//				Ext.apply(cfg.exContext, this.exContext)
//
//				if (cmd != 'create' && r != null) {
//					cfg.initDataId = r.id
//					cfg.exContext[this.entryName] = r
//				} else {
//					cfg.initDataId = null;
//				}
//				var module = this.midiModules[cmd]
//				if (module) {
//					Ext.apply(module, cfg)
//					this.openModule(cmd, r)
//				} else {
//					this.loading = true
//					this.mainApp.taskManager.destroyInstance(ref)
//					this.mainApp.taskManager.loadInstance(ref, cfg, function(m,
//									from) {
//								this.loading = false
//								m.on("save", this.onSave, this)
//								m.on("close", this.active, this)
//								this.midiModules[cmd] = m
//								if (from == "local") {
//									Ext.apply(m, cfg)
//								}
//								this.fireEvent("loadModule", m)
//								this.openModule(cmd, r, 100, 50)
//							}, this)
//				}
//			},
			active : function() {
				if (!this.store || this.store.getCount() == 0) {
					return;
				}
				if (!this.selectedIndex || this.selectedIndex >= this.store.getCount()) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
				}
			},
			getIWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								resizable : false,
								header : false,
								frame : false,
								closable : false,
								shim : true,
								layout : "fit",
								animCollapse : true,
								shadow : false,
								modal : true
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					this.win = win
				}
				return win;
			},
			
		openModule:function(cmd,r,xy){
		var module = this.midiModules[cmd]
		if(module){
			var win = module.getWin()
			if(!win && cmd == "printList")
			{
				module.loadData();
				return;
			}
			if(xy){
				win.setPosition(xy[0], xy[1])
			}
			win.setTitle(module.title)
			win.show()
			this.fireEvent("openModule", module)
			if(!win.hidden){
				switch(cmd){
					case "create":
						module.doCreate()
						break;
					case "read":
					case "update":
					module.loadData()
					break;
					case "printList":
						module.loadData()
				}
			}
		}
	},
			doSelect : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				/*
				this.loadEHRViewRoles();
				if (this.sys_ehrViewPath == null || this.sys_ehrViewPath === "") {
					if(this.roles == null){
						Ext.Msg.show({
						title:"提示",
						msg:"未获得权限，请检查电子健康档案路径配置。"	
						});

						return;
					}
					if (this.roles.length > 1) {
						this.showSelectView(record);
						return;
					} else if (this.roles.length == 1) {
						var id = this.roles[0]["key"];
						this.loadEHRViewPath(id);
						if (this.sys_ehrViewPath == null
								|| this.sys_ehrViewPath === "") {
							return;
						}
					} else if (this.roles.length == 0) {
						return;
					}
				}
				var url = this.sys_ehrViewPath + "-" + record.data.mpiId;
				*/
				//案例 http://172.31.167.105:8081/ehrview/EhrLogonService?user=system&pwd=123&idcard=320423194011152421
				var url = "http://172.31.167.105:8081/ehrview/EhrLogonService?user=system&pwd=123&idcard="+record.data.idCard;
				this.fireEvent("onSelect", record);
				window.open(url);

			// this.getWin().hide();
				},
				showSelectView : function(r){
				var cfg = {};
				cfg.exContext = {};
				cfg.openWinInTaskbar = false;
				cfg.autoLoadData = false;
				cfg.roles = this.roles;
				var model;
				var cls = "dr.referralMgr.view.DRSelectRolesView";
				$import(cls);
				$require(cls, [function() {
						model = eval("new " + cls + "(cfg)");
						model.setMainApp(this.mainApp);
						model.on("selectRole",function(id){
							this.selectRoleId = id;
							this.loadEHRViewPath(id);
							if(this.sys_ehrViewPath == null ||
										this.sys_ehrViewPath === ""){
								return;
							}
							window.open(this.sys_ehrViewPath + "-"
										+ r.data.mpiId);
							},this)
						model.getWin().show();
					}, this])
			},

		loadEHRViewPath : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							method : "execute",
							serviceId : "dr.drResourceApplyControllor",
							action : "loadEHRViewPath",
//							deptId:this.mainApp.deptId,
							roleId : id
						})
				if (result.code == 200) {
					this.sys_ehrViewPath = result.json.body.sys_ehrViewPath
							|| "";
				} else {
					this.processReturnMsg(result.code, result.msg);
				}
			},
			loadEHRViewRoles : function() {
				var result = util.rmi.miniJsonRequestSync({
							method : "execute",
							serviceId : "dr.drResourceApplyControllor",
							action : "loadEHRViewRoles"
						})
				if(result.code == 200){
								var body = result.body;
								var roles = body.roles;
								if (roles != null && roles.length > 0) {
									this.roles = roles;
									var toolbar = this.grid.getTopToolbar();
									var btns = toolbar.find("cmd", "ehrView");
									if (btns.length > 0) {
										var btn = btns[0];
										btn.enable();
									}
								}
				}
			}
		});