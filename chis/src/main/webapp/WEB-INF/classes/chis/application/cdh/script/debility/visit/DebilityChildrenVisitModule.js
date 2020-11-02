/**
 * 体弱儿随访, 随访整体（计划和随访）窗口
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.visit");
$import("chis.script.BizCombinedTabModule", "chis.script.util.helper.Helper");
chis.application.cdh.script.debility.visit.DebilityChildrenVisitModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.cdh.script.debility.visit.DebilityChildrenVisitModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemWidth = 180;
	this.on("loadModule", this.onLoadModule, this)
};

Ext.extend(chis.application.cdh.script.debility.visit.DebilityChildrenVisitModule,
		chis.script.BizCombinedTabModule, {

			initPanel : function() {
				var panel = chis.application.cdh.script.debility.visit.DebilityChildrenVisitModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.grid.on("rowclick", this.onRowClick, this);
				this.list.on("loadData", this.onLoadGridData, this)
				return panel;
			},

			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onSave, this);
				}
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.tab.setDisabled(true);
					this.activeModule(0);
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

			onRowClick : function(grid, index, e) {
				if (!this.list) {
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.exContext.args.selectedPlanId = r.get("planId")
				this.process(r, index);
			},

			process : function(r, index) {
				if (!r) {
					return;
				}
				var status = r.get("planStatus");
				if (status == "9") {
					this.tab.setDisabled(true);
					Ext.Msg.alert("提示信息", "该计划处于注销状态无法编辑!");
				} else if (status == "8") {
					this.tab.setDisabled(true);
					Ext.Msg.alert("提示信息", "该计划处于结案状态无法编辑!");
				} else {
					this.tab.setDisabled(false);
				}
				this.changeSubItemDisabled(false, this.actions[0].id);
				var planId = r.get("planId");
				var visitId = r.get("visitId");
				var nextPlan = this.list.grid.store.getAt(index + 1);
				var exc = {};
				var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
				// ** 获取随诊日期的最大值和最小值
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var maxVisitDate;
				if (now > endDate) {
					maxVisitDate = endDate;
				} else {
					maxVisitDate = now;
				}
				exc.minVisitDate = startDate;
				exc.maxVisitDate = maxVisitDate;
				// ** 获取复诊日期的最小值
				var minNextVisitDate;
				if (now > endDate) {
					minNextVisitDate = now;
				} else {
					minNextVisitDate = endDate;
				}
				exc.minNextVisitDate = chis.script.util.helper.Helper
						.getOneDayAfterDate(minNextVisitDate);

				if (nextPlan) {
					exc.notNextPlan = false;
				} else {
					exc.notNextPlan = true;
				}
				if (visitId) {
					if (nextPlan) {
						var nextVisitDate = nextPlan.get("visitDate");
						if (nextVisitDate) {
							exc.minNextVisitDate = null;
						}
					}
					var parma = {
						"visitId" : visitId,
						"planId" : planId
					};
					Ext.apply(exc, parma);
					this.changeSubItemDisabled(false, this.actions[0].id);
				} else {
					var planDate = r.get("planDate");
					var parma = {
						"visitId" : null,
						"planDate" : planDate,
						"planId" : planId
					};
					Ext.apply(exc, parma);
					this.changeSubItemDisabled(true, this.actions[0].id);
				}
				Ext.apply(this.exContext.args, exc);
				this.activeModule(0);
				this.fireEvent("controlButton")
			},

			onSave : function(entryName, op, json, data) {
				var visitId = data.visitId;
				this.exContext.args.visitId = visitId;
				this.changeSubItemDisabled(false, this.actions[0].id);
				var planId = data.planId;
				var r = this.list.store.getById(planId);
				r.set("planStatus", "1");
				r.set("visitDate", data.visitDate);
				r.set("visitId", visitId);
				r.commit();
				this.list.refresh()
			}

		});