/**
 * 健康检查表module
 * 
 * @author : tianj
 */
$package("chis.application.hc.script")

$import("chis.script.BizCombinedTabModule")

chis.application.hc.script.HealthCheckModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 300;
	// cfg.width = 1000;
	// cfg.height = 400;
	chis.application.hc.script.HealthCheckModule.superclass.constructor.apply(
			this, [cfg]);
	this.on("loadModule", this.onLoadModule, this);
	this.on("refreshInitedModule", this.onRefreshInitedModule, this);
}

Ext.extend(chis.application.hc.script.HealthCheckModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.hc.script.HealthCheckModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onListLoadData, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				return panel;
			},

			onRowClick : function(grid, index, e) {
				var isLast = false; // 判断是否是最后一条记录，如果是需要更新家庭档案收入来源状况
				if (index == grid.store.getCount() - 1) {
					isLast = true;
				}
				var r = grid.store.getAt(index);
				this.exContext.args.op = "update";
				this.process(r, isLast);
			},

			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onSave, this);
					module.on("create", this.onCreate, this);
					this.firstTabForm = module;
					//this.changeButton();
				}
			},

			onCreate : function() {
				this.reSetTab(false);
				this.exContext.control = {
							"create" : true,
							"update" : true
						};
				Ext.apply(this.firstTabForm.exContext.control, this.exContext.control);
				this.firstTabForm.resetButtons();
				//this.changeButton();
			},

			onSave : function(entryName, op, json, data) {
				this.list.refresh();
				this.exContext.args.healthCheck = json.body.healthCheck;
				this.exContext.args.op = "update";
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
				this.fireEvent("save", entryName, op, json, data)
			},

			onListLoadData : function(store, records, ops) {
				if(!this.exContext.control){
					this.exContext.control = {};
				}
				if (store.getCount() == 0) {
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						healthCheck : null,
						data : null
					}
					Ext.apply(this.exContext.args, exc);
					this.reSetTab(false);
					var result = util.rmi.miniJsonRequestSync({
								serviceId : this.loadServiceId,
								serviceAction : this.loadAction,
								method : "execute",
								body:{
									empiId : this.exContext.ids.empiId
								}
							})
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg);
						return
					}
					Ext.apply(this.exContext.control, result.json.body);
					if(this.exContext.control&&this.exContext.control.create==true){
						this.exContext.control.update=true;
					}
					this.activeModule(0);
					//this.firstTabForm.resetButtons(); 判断怎人医生重置按钮权限屏蔽
					//this.changeButton();
					return;
				}
				var index = store.find("healthCheck",
						this.exContext.args.healthCheck);
				if (index == -1 && this.exContext.args.dataSources) {
					index = store.find("checkWay",
							this.exContext.args.dataSources);
				}
				this.index = index;
				if (this.exContext.args.healthCheck) {
					this.exContext.args.healthCheck = null; // 清空从第一个页面列表传过来的参数
				}
				if (index == -1) {
					index = 0
				}
				var isLast = false; // 判断是否是最后一条记录，如果是需要更新家庭档案收入来源状况
				if (index == store.getCount() - 1) {
					isLast = true;
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				if (!r) {
					if (this.firstTabForm && this.exContext.args.op == "create") {
						this.firstTabForm.doNew();
					}
				}
				this.process(r, isLast);
			},

			process : function(r, isLast) {
				var healthCheck = null;
				var createUser = null;
				var data = null;
				if (r) {
					healthCheck = r.get("healthCheck");
					createUser = r.get("createUser")
					data = r.data;
					// if (!isLast) {
					// Ext.apply(data["_actions"],{"update":false});
					// }
				}
				// 控制新建按钮权限
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.loadServiceId,
							serviceAction : this.loadAction,
							method : "execute",
							body:{
								empiId : this.exContext.ids.empiId,
								healthCheck:healthCheck,
								createUser : createUser
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.exContext.control = result.json.body;
				//所有人都能做体检
				this.exContext.control.create="true";
				//所有人都能更新体检
				this.exContext.control.update="true";
				this.reSetTab(true);
				this.tab.items.each(function(item) {
							if (item.name == "HealthCheckForm") {
								var exc = {
									empiId : this.exContext.ids.empiId,
									phrId : this.exContext.ids.phrId,
									healthCheck : healthCheck,
									data : data
								}
								Ext.apply(this.exContext.args, exc);
								Ext.apply(item.exContext, this.exContext);
							} else {
								var exc = {
									healthCheck : healthCheck
								}
								Ext.apply(this.exContext.control,
										data["_actions"]);
								Ext.apply(this.exContext.args, exc);
							}
						}, this);
				this.activeModule(0);
				if (this.firstTabForm && this.exContext.args.op == "create") {
					this.firstTabForm.doNew();
					this.reSetTab(false);
				}
			},
			onRefreshInitedModule : function(newTab) {
				if (newTab.name == "HealthCheckForm") {
					//this.changeButton();
				}
			},
//			changeButton : function() {
//				var control = this.exContext.control;
//				var form = this.midiModules["HealthCheckForm"].form;
//				var btns = form.getTopToolbar();
//				if (!btns) {
//					return;
//				}
//				var saveBtn = btns.items.items[0];
//				var createBtn = btns.items.items[1];
//				saveBtn.enable();
//				createBtn.enable();
//				if (!control.update) {
//					saveBtn.disable();
//				}
//				if (!control.create) {
//					createBtn.disable();
//				}
//			},

			reSetTab : function(flag) {
				var items = this.tab.items
				for (var i = 1; i < items.length; i++) {
					if (flag) {
						items.itemAt(i).enable();
					} else {
						items.itemAt(i).disable();
					}
				}
			}
		});