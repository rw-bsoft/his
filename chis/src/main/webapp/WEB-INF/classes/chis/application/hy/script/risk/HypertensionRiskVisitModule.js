/**
 * 孕妇随访信息整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.hy.script.risk")
$import("chis.script.BizCombinedModule2", "chis.script.util.helper.Helper")
chis.application.hy.script.risk.HypertensionRiskVisitModule = function(cfg) {
	cfg.width = 880;
	cfg.autoLoadData = false
	chis.application.hy.script.risk.HypertensionRiskVisitModule.superclass.constructor
			.apply(this, [cfg])
	this.itemWidth = 215
	this.on("winShow", this.onWinShow, this)
	this.nowDate = this.mainApp.serverDate
}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskVisitModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.hy.script.risk.HypertensionRiskVisitModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("addModule", this.onAddModule, this);
				return panel;
			},
			onAddModule : function(module) {
				this.fireEvent("addModule", module);
				this.fireEvent("activeModule", module, {});
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.form.setButtonEnable(false);
					return;
				} else {
					this.form.setButtonEnable(true);
				}
				var index = 0;
				// ** 从工作列表进入，需根据计划编号选中记录
				var selectPlanRecord = this.exContext.args.selectedPlanId;
				if (selectPlanRecord) {
					index = this.list.store.find("planId", selectPlanRecord);
				}
				// ** 从孕妇随访计划列表进入，需根据随访编号编号选中记录
				var selectVisitRecord = this.exContext.args.selectVisitId;
				if (selectVisitRecord) {
					index = this.list.store.find("visitId", selectVisitRecord);
				}
				this.list.selectedIndex = index < 0 ? 0 : index;
				var r = store.getAt(this.list.selectedIndex);
				this.proccess(r, this.list.selectedIndex);
			},

			onRowClick : function(grid, index, e) {
				var r = grid.getStore().getAt(index)
				this.list.selectedIndex = index
				this.proccess(r, index);
			},

			proccess : function(r, index) {
				if (!r) {
					return;
				}
				var planDate = Date.parseDate(r.get("planDate"), "Y-m-d")
				var status = r.get("planStatus");
				var args = {
					"planId" : r.get("planId"),
					"status" : status,
					"planDate" : planDate,
					"sn" : r.get("sn")
				};
				var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
				//
				// // ** 获取检查日期的最大值和最小值
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				if (now > endDate) {
					this.form.form.getForm().findField("visitDate")
							.setMaxValue(Date.parseDate(this.nowDate, "Y-m-d"));
					this.form.form.getForm().findField("visitDate")
							.setMinValue(new Date(r.get("beginDate")));
				} else {
					this.form.form.getForm().findField("visitDate")
							.setMaxValue(Date.parseDate(this.nowDate, "Y-m-d"));
					this.form.form.getForm().findField("visitDate")
							.setMinValue(new Date(r.get("beginDate")));
				}

				if (status == "1" || status == "2") {
					this.form.op = "update";
					this.form.initDataId = r.get("visitId");
					Ext.apply(this.exContext.args, args);
					this.setButtonEnable(true);
					this.refreshExContextData(this.form, this.exContext);
					this.form.loadData();
					// } else if (status == "3") {
					// this.setButtonEnable(false);
					// Ext.Msg.alert("提示信息", "该计划处于未访状态无法再编辑!");
				} else if (status == "0") {
					if (startDate > now) {
						this.setButtonEnable(false);
						Ext.Msg.alert("提示信息", "未到随访时间,无法进行随访!");
						return;
					} else {
						this.form.op = "create";
						this.form.initDataId = null;
						Ext.apply(this.exContext.args, args);
						this.setButtonEnable(true);
						this.refreshExContextData(this.form, this.exContext);
						this.form.doNew();
					}
				} else if (status == "9") {
					this.setButtonEnable(false);
					Ext.Msg.alert("提示信息", "该计划处于注销状态无法编辑!");
				} else if (status == "8") {
					this.setButtonEnable(false);
					Ext.Msg.alert("提示信息", "该计划处于结案状态无法编辑!");
				}
			},

			setButtonEnable : function(status) {
				if (!this.form.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.form.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (status) {
						btn.enable()
					} else {
						btn.disable()
					}
				}
			},

			onFormSave : function(entryName, op, json, data) {
				this.exContext.args.selectVisitId = this.form.initDataId;
				this.list.refresh();
				var body = json.body;
				if (!body) {
					return;
				}
				this.fireEvent("save", entryName, op, json, data);
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				this.fireEvent("refreshEhrView", "C_06");
				this.refreshEhrTopIcon();
			},

			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
				if (this.form) {// add by chenxr 权限控制没有起做用，高危记录 核实情况 为确认时随访才可操作
					this.refreshExContextData(this.form, this.exContext);
					this.form.resetButtons();
				}
			},
			onWinShow : function() {
				this.loadData()
			}
		});