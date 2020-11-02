/**
 * 儿童询问整天模块页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.inquire")
$import("util.Accredit", "chis.script.BizCombinedModule2")
chis.application.cdh.script.inquire.ChildrenInquireModule = function(cfg) {
	cfg.width = 880;
	cfg.autoLoadData = false;
	chis.application.cdh.script.inquire.ChildrenInquireModule.superclass.constructor.apply(
			this, [cfg])
	this.currentDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
}
Ext.extend(chis.application.cdh.script.inquire.ChildrenInquireModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.cdh.script.inquire.ChildrenInquireModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				return panel;
			},
			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.form.resetButtons();
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
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			process : function(r, n) {
				if (!r) {
					return;
				}
				var status = r.get("planStatus");
				var extend1 = r.get("extend1");
				this.form.planId = r.get("planId");
				var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
				var planDate = Date.parseDate(r.get("planDate"), "Y-m-d");
				if (n && n > 0) {
					var preInqId = this.list.store.getAt(n - 1).get("visitId");
				}
				var minVisitDate = startDate;
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var maxVisitDate;
				if (now > endDate) {
					maxVisitDate = endDate;
				} else {
					maxVisitDate = now;
				}
				this.exContext.args.minVisitDate = minVisitDate;
				this.exContext.args.maxVisitDate = maxVisitDate;
				this.refreshExContextData(this.form, this.exContext);
				if (status == "1" || status == "2") {
//					this.form.form.setDisabled(false);
					this.form.form.el.unmask()
					this.form.op = "update";
					this.form.initDataId = r.get("visitId");
					this.form.planBeginDate = startDate;
					this.form.preInqId = preInqId || null;
					this.form.extend1 = extend1;
					this.form.loadData();
				} else if (status == "9") {
//					this.form.form.setDisabled(true);
					this.form.form.el.mask()
					Ext.Msg.alert("提示信息", "该计划处于注销状态无法编辑!");
				} else {
//					this.form.form.setDisabled(false);
					this.form.form.el.unmask()
					this.form.op = "create";
					this.form.initDataId = null;
					this.form.planDate = planDate;
					this.form.planBeginDate = startDate;
					this.form.preInqId = preInqId || null;
					this.form.extend1 = extend1;
					this.form.doNew();
				}
			},

			onFormSave : function(entryName, op, json, data) {
				var planId = data.planId;
				var r = this.list.store.getById(planId);
				r.set("planStatus", "1");
				r.set("visitDate", data.inquireDate);
				r.set("visitId", data.inquireId);
				r.commit();
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
			},
			refreshWhenTabChange:function(){
				this.loadData();
			},

			loadData : function() {
				this.exContext.args.businessType = "5";
				this.exContext.args.checkupType = null;
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			}
		});