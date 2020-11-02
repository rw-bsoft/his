$package("chis.application.cvd.script")
$import("chis.script.BizCombinedTabModule");
chis.application.cvd.script.CVDRegisterModule = function(cfg) {
	cfg.itemWidth = 180;
	this.autoLoadData = false;
	this.activateId = 0;
	chis.application.cvd.script.CVDRegisterModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.cvd.script.CVDRegisterModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				debugger;
				var panel = chis.application.cvd.script.CVDRegisterModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				//this.grid.on("rowClick", this.onRowClick, this);
				//this.on("loadModule", this.onLoadModule, this);
				return panel;
			},
//			onRowClick : function(grid, index, e) {
//				debugger;
//				// this.clearAllActived()
//				this.tab.setActiveTab(0)
//				this.tab.enable()
//				var r = grid.store.getAt(index)
//
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "chis.diabetesVisitService",
//							serviceAction : "initializeForm",
//							method : "execute",
//							body : r.data,
//							empiData : this.exContext.empiData
//						})
//				this.result = result;
//				//this.process(r);
//				this.midiModules[this.lodarForm].loadDataByLocal(result.json.body);
//			}
//			,
			onLoadModule : function(moduleId, module) {
				debugger;
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					this.resisterForm = module;
//					module.on("save", this.onResisterFormSaveToServer, this);
//					module.on("doCreate", function() {
//								this.resisterForm.initDataId = null;
//								this.changeSubItemDisabled(true,
//										this.actions[0].id);
//								this.activeModule(0);
//							}, this)
//					module.on("remove", this.onRemove, this);
				} else if (moduleId == this.actions[1].id) {
					this.apraisalForm = this.midiModules[this.actions[1].id];
				}
			},
//			onResisterFormSaveToServer : function(entryName, op, json, data) {
//				var resBody = json.body;
//				this.exContext.args.selectInquireId = resBody.inquireId;
//				this.list.loadData();
//				this.changeSubItemDisabled(false, this.actions[0].id);
//				this.fireEvent("save", this);
//			},
			onLoadGridData : function(store) {
				debugger;
				this.exContext.control = this.exContext.control || {};
				Ext.apply(this.exContext.control, this.getFrmControl());
				if (store.getCount() == 0) {
					//this.reSetTab(false);
						this.resisterForm.initDataId  = null;
					this.activeModule(0);
					return;
				} else {
					this.reSetTab(true);
				}
				var index = this.list.selectedIndex;
				var selectRecordId = this.exContext.args.selectRecordId;
				if (selectRecordId) {
					index = this.list.store.find("recordId", selectRecordId);
				}
				this.list.selectedIndex = index < 0 ? 0 : index;
				var r = store.getAt(this.list.selectedIndex);
				if (r) {
					this.process(r, index);
				}
			},
//			onRowClick : function(grid, index, e) {
//				if (!this.list) {
//					return;
//				}
//				var r = this.list.grid.getSelectionModel().getSelected();
//				if (!r) {
//					return;
//				}
//				this.selectedIndex = index;
//				this.process(r, index);
//			},
			process : function(r, index) {
				this.exContext.args.recordId = r.get("recordId");
				//this.resisterForm.initDataId = r.get("recordId");
				//this.onBeforeLoadData(r.data);
				this.changeSubItemDisabled(false, this.actions[0].id);
				this.activeModule(0);
				
			},
			getFrmControl : function() {
				var body = {};
				body.phrId = this.exContext.ids.phrId;
				body.empiId = this.exContext.ids.empiId;
				this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.cvdService",
							serviceAction : "getCVDControl",
							method : "execute",
							body : body
						});
				this.panel.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				debugger;
				return result.json.body;
			},
//			onBeforeLoadData : function(body) {
//				this.data = {}
//				this.data.url = ""
//				if (body.diabetes == "y" && body.tc > 0) {
//					this.data.url = "1"
//				} else if (body.diabetes == "y"
//						&& (body.tc == null || body.tc == 0)) {
//					this.data.url = "2"
//				} else if (body.diabetes == "n" && body.tc > 0) {
//					this.data.url = "3"
//				} else if (body.diabetes == "n"
//						&& (body.tc == null || body.tc == 0)) {
//					this.data.url = "4"
//				}
//				this.exContext.args.data = this.data;
//			},
//			onRemove : function() {
//				this.loadData();
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