/**
 * 儿童体格检查整体模块
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup")
$import("chis.script.BizCombinedTabModule", "chis.script.util.helper.Helper")
chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil = function(cfg) {
	cfg.height = 400
	cfg.autoLoadData = false
	chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil.superclass.constructor
			.apply(this, [cfg])
	this.itemWidth = 235
	this.on("loadModule", this.onLoadModule, this)
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil,
		chis.script.BizCombinedTabModule, {

			initPanel : function() {
				var panel = chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				return panel;
			},

			onTabChange : function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				// ** 模块是否已经激活过，即已经加载过数据，若为true则不再往下执行
				if (newTab.__actived) {
					return;
				}
				// ** 模块是否已经初始化过，即是否已经构建过，若为true则刷新页面，fase则构建页面
				if (newTab.__inited) {
					this.refreshModule(newTab);
					return;
				}

				var p = this.getCombinedModule(newTab.name, newTab.exCfg.ref);
				newTab.add(p);
				newTab.__inited = true;
				this.tab.doLayout()
				var m = this.midiModules[newTab.name];
				// **面板加载完成，可捕获此事件，完善面板，如增加模块的事件捕捉
				this.fireEvent("loadModule", newTab.name, m);
				if (newTab.name == 'ChildrenCheckupDescriptionForm') {
					m.changeFieldByAge();
				}
				if (m.loadData && m.autoLoadData == false) {
					m.loadData()
					newTab.__actived = true;
				}
			},
			refreshModule : function(newTab) {
				var m = this.midiModules[newTab.name]
				if (m) {
					if (this.exContext) {
						delete m.exContext;
						m.exContext = {};
						this.refreshExContextData(m, this.exContext);
					}
					if (newTab.name == 'ChildrenCheckupDescriptionForm') {
						m.changeFieldByAge();
					}
					if (m.loadData) {
						m.loadData();
						newTab.__actived = true;
					} else if (m.refresh) {
						m.refresh();
						newTab.__actived = true;
					}

				}
			},

			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onSave, this)
					module.on("activeDebility", this.onActiveDebility, this)
				}
			},

			onActiveDebility : function(data) {
				var exc = {
					"data" : data
				};
				this.fireEvent("activeModule", "H_09", exc);
			},

			onSave : function(entryName, op, json, data) {
				var checkupId = data.checkupId;
				var checkupStage = data.checkupStage.key;
				var exc = {
					"checkupId" : checkupId,
					"checkupStage" : checkupStage
				};
				Ext.apply(this.exContext.args, exc);
				this.changeSubItemDisabled(false, this.actions[0].id);
				var index = this.list.store.find("extend1", checkupStage);
				var r = this.list.store.getAt(index);
				if (!r) {
					return
				}
				r.set("planStatus", "1");
				r.set("visitDate", data.checkupDate);
				r.set("visitId", checkupId);
				r.commit();
			},

			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.changeSubItemDisabled(true, this.actions[0].id);
					this.exContext.args[this.checkupType + "_param"] = {};
					this.activeModule(0);
					this.tab.setDisabled(true);
					return;
				}
				var index = 0;
				var selectRecord = this.exContext.args.selectedPlanId;
				if (selectRecord) {
					index = this.list.store.find("planId", selectRecord);
				}
				this.list.selectedIndex = index;
				var r = store.getAt(index);
				this.process(r, index);
			},
			changeField : function() {
			},

			process : function(r, n) {
				if (!r) {
					return;
				}
				var status = r.get("planStatus");
				if (status == "9") {
					this.tab.setDisabled(true);
					Ext.Msg.alert("提示信息", "该计划处于注销状态无法编辑!");
				} else {
					this.tab.setDisabled(false);
				}
				var exc = {};
				var visitId = r.get("visitId");
				var extend1 = r.get("extend1");
				var checkupId;
				this.exContext.args.checkupStage = extend1;
				// ** 获取随访日期的最大值和最小值
				var minVisitDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var maxVisitDate;
				if (now > endDate) {
					maxVisitDate = endDate;
				} else {
					maxVisitDate = now;
				}
				exc.minVisitDate = minVisitDate;
				exc.maxVisitDate = maxVisitDate;

				// ** 获取下次随访时间的最小值
				exc.minNextVisitDate = chis.script.util.helper.Helper
						.getOneDayAfterDate(endDate);

				if (nextPlan) {
					exc.notNextPlan = false;
				} else {
					exc.notNextPlan = true;
				}

				var nextPlan = this.grid.store.getAt(n + 1);
				if (visitId) {
					checkupId = visitId;
					if (nextPlan) {
						var nextVisitDate = nextPlan.get("visitDate");
						if (nextVisitDate) {
							exc.minNextVisitDate = null;
						}
					}
					var param = {
						"checkupId" : checkupId,
						"checkupStage" : extend1
					};
					Ext.apply(exc, param);
					this.changeSubItemDisabled(false, this.actions[0].id);
				} else {
					checkupId = null;
					var planDate = Date.parseDate(r.get("planDate"), "Y-m-d");
					var nextCheckupDate;
					if (nextPlan) {
						nextCheckupDate = Date.parseDate(nextPlan
										.get("planDate"), "Y-m-d");
					}
					var needInit = false;
					if (extend1 == 1) {
						needInit = true;
					}
					var param = {
						"checkupId" : null,
						"needInit" : needInit,
						"checkupStage" : extend1,
						"checkupDate" : planDate,
						"nextCheckupDate" : nextCheckupDate
					};
					Ext.apply(exc, param);
					this.changeSubItemDisabled(true, this.actions[0].id);
				}
				this.exContext.args[this.checkupType + "_param"] = {};
				Ext
						.apply(
								this.exContext.args[this.checkupType + "_param"],
								exc);
				this.activeModule(0);
				this.changeField();
			},

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : 1200,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
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
			refreshWhenTabChange : function() {
				this.loadData();
			},
			onWinShow : function() {
				this.loadData()
			}
		})