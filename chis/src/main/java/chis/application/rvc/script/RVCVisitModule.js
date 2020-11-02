$package("chis.application.rvc.script");

$import("chis.script.BizCombinedModule2");

chis.application.rvc.script.RVCVisitModule = function(cfg) {
	chis.application.rvc.script.RVCVisitModule.superclass.constructor.apply(
			this, [cfg]);
	this.itemWidth = 180;
};

Ext.extend(chis.application.rvc.script.RVCVisitModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.rvc.script.RVCVisitModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				return panel;
			},

			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.form.form.el.mask();
					return;
				}
				// ** 控制按钮权限
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "loadControl",
							method : "execute",
							empiId : this.exContext.ids.empiId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.exContext.control = result.json.body;

				var index = 0;
				if (this.exContext.args.selectedPlanId) {
					if (store
							.find("planId", this.exContext.args.selectedPlanId) >= 0) {
						index = store.find("planId",
								this.exContext.args.selectedPlanId);
					}
				} else if (this.exContext.args.visitId) {
					if (store.find("visitId", this.exContext.args.visitId) >= 0) {
						index = store.find("visitId",
								this.exContext.args.visitId);
					}
				}
				if (this.exContext.args.visitId) {
					this.exContext.args.visitId = null; // 清空从第一个页面列表传过来的参数
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				this.process(r, index);
			},

			process : function(r, index) {
				if (!r) {
					return;
				}
				// ****获取下次随访记录,便于计算下次随访预约时间****
				var nextIndex = 0;
				if (!index) {
					nextIndex = 1;
				} else {
					nextIndex = index + 1;
				}
				var nextPlan = this.grid.store.getAt(nextIndex);
				// 如果nextPlan 的planStatus非0 和 9 则随访时间和检查时间不可以编辑
				var nextDateDisable = false;
				if (nextPlan != null) {
					var nextStatus = nextPlan.get("planStatus");
					if (nextStatus != '0' && nextStatus != '9') {
						nextDateDisable = true;
					}
				}
				var planId = r.get("planId");
				var planDate = r.get("planDate");
				var status = r.get("planStatus");
				var beginDate = r.get("beginDate");
				var endDate = r.get("endDate");
				if (status == "1" || status == "2") {
					var visitId = r.get("visitId");
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						nextDateDisable : nextDateDisable,
						initDataId : visitId,
						planId : planId,
						planDate : planDate,
						beginDate : beginDate,
						endDate : endDate,
						op : "update"
					}
					Ext.apply(this.exContext.args, exc);
					Ext.apply(this.form.exContext, this.exContext);
					this.form.loadData();
					this.setFormEnable(true);
				} else if (status == "3") {
					var visitId = r.get("visitId");
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						nextDateDisable : nextDateDisable,
						initDataId : visitId,
						planId : planId,
						planDate : planDate,
						beginDate : beginDate,
						endDate : endDate,
						op : "update"
					}
					Ext.apply(this.exContext.args, exc);
					Ext.apply(this.form.exContext, this.exContext);
					this.form.loadData();
					this.setFormEnable(true);
					Ext.Msg.alert("提示信息", "该计划处于未访状态,无法进行操作!");
				} else if (status == "9") {
					var visitId = r.get("visitId");
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						nextDateDisable : nextDateDisable,
						initDataId : visitId,
						planId : planId,
						planDate : planDate,
						beginDate : beginDate,
						endDate : endDate,
						op : "update"
					}
					Ext.apply(this.exContext.args, exc);
					Ext.apply(this.form.exContext, this.exContext);
					this.form.loadData();
					this.setFormEnable(false);
					Ext.Msg.alert("提示信息", "该计划已注销,无法进行操作!");
				} else {
					var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
					var currDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					if (startDate > currDate) {
						this.setFormEnable(false);
						Ext.Msg.alert("提示信息", "未到随访时间,无法进行随访!");
					} else {
						var exc = {
							initDataId : null,
							empiId : this.exContext.ids.empiId,
							phrId : this.exContext.ids.phrId,
							nextDateDisable : nextDateDisable,
							planId : planId,
							planDate : planDate,
							beginDate : beginDate,
							endDate : endDate,
							op : "create"
						}
						Ext.apply(this.exContext.args, exc);
						Ext.apply(this.form.exContext, this.exContext);
						this.form.loadData();
						this.setFormEnable(true);
					}
				}
			},

			setFormEnable : function(status) {
				if (status) {
					this.form.form.el.unmask();
				} else {
					this.form.form.el.mask();
				}
			},

			loadData : function() {
				this.list.requestData.empiId = this.exContext.ids.empiId;
				this.list.requestData.queryDate = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				this.list.loadData()
			},

			onFormSave : function() {
				this.list.refresh();
			}
		});