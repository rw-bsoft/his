/**
 * 孕妇随访信息整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizCombinedModule2", "chis.script.util.helper.Helper")
chis.application.mhc.script.visit.PregnantVisitModule = function(cfg) {
	cfg.width = 880;
	cfg.autoLoadData = false
	chis.application.mhc.script.visit.PregnantVisitModule.superclass.constructor.apply(this,
			[cfg])
	this.itemWidth = 215
}
Ext.extend(chis.application.mhc.script.visit.PregnantVisitModule, chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.visit.PregnantVisitModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				var LoadForm=this.getLoadForm();
				var actions = this.actions;
				for ( var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					var refId=ac.id;
					if ((refId == "PregnantVisitForm" || refId == "PregnantVisitFormHtml") && refId !=LoadForm){
					}else {
						this.form = this.midiModules[this.actions[i].id];
						this.form.on("save", this.onFormSave, this);
					}
				}
				return panel;
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
				var week = r.get("extend1");
				var args = {
					"planId" : r.get("planId"),
					"status" : status,
					"planDate" : planDate,
					"pregnantWeeks" : week,
					"sn" : r.get("sn"),
					"remark" : ""
				};
				var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");

				// ** 获取孕周用于确认,随访页面胎位相关字段是否可用
				var fetalDisable = false;
				if (week < 18) {
					fetalDisable = true;
				}

				// **获取下次随访记录,便于计算下次随访预约时间
				var nextIndex = 0;
				if (!index) {
					nextIndex = 1;
				} else {
					nextIndex = index + 1;
				}
				var nextPlan = this.list.store.getAt(nextIndex);
				// ** 如果nextPlan 的planStatus非0 和 9 则下次预约时间和检查时间不可以编辑
				var nextDateDisable = false;
				if (nextPlan != null) {
					var nextStatus = nextPlan.get("planStatus")
					if (nextStatus != '0' && nextStatus != '9') {
						nextDateDisable = true;
					}
				}
				args.nextDateDisable = nextDateDisable

				// ** 获取检查日期的最大值和最小值
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var maxVisitDate;
				if (now > endDate) {
					maxVisitDate = endDate;
				} else {
					maxVisitDate = now;
				}
				this.exContext.args.minVisitDate = startDate;
				this.exContext.args.maxVisitDate = maxVisitDate;

				// ** 获取下次预约时间的最小值
				var minNextVisitDate;
				if (now > endDate) {
					minNextVisitDate = now;
				} else {
					minNextVisitDate = endDate;
				}
				this.exContext.args.minNextVisitDate = chis.script.util.helper.Helper
						.getOneDayAfterDate(minNextVisitDate);

				if (status == "1" || status == "2") {
					this.form.op = "update";
					this.form.initDataId = r.get("visitId");
					args.nextPlan = nextPlan || null;
					args.fetalDisable = fetalDisable;
					args.endDate = endDate;
					if (nextPlan) {
						var nextVisitDate = nextPlan.get("visitDate");
						if (nextVisitDate) {
							args.minNextVisitDate = null;
						}
					}
					Ext.apply(this.exContext.args, args);
					this.setButtonEnable(true);
					this.refreshExContextData(this.form, this.exContext);
					this.form.loadData();
				} else if (status == "3") {
					this.setButtonEnable(false);
					Ext.Msg.alert("提示信息", "该计划处于未访状态无法再编辑!");
				} else if (status == "0") {
					if (startDate > now) {
						this.setButtonEnable(false);
						Ext.Msg.alert("提示信息", "未到随访时间,无法进行随访!");
						return;
					} else {
						this.form.op = "create";
						this.form.initDataId = null;
						args.nextPlan = nextPlan || null;
						args.fetalDisable = fetalDisable;
						args.endDate = endDate;
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
				if(this.form.form.getTopToolbar()){
					var btns = this.form.form.getTopToolbar().items;
					for (var i = 0; i < btns.getCount(); i++) {
						var btn = btns.item(i);
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			},

			onFormSave : function(entryName, op, json, data) {
				this.form.riskStore = null;
				this.form.checkUpStore = null;
				this.form.description = null;
				this.exContext.args.selectVisitId = this.form.initDataId;
				this.list.refresh();
				var body = json.body;
				if (!body) {
					return;
				}
				var isEndManage = body.isEndManage;
				if (isEndManage) {
					this.fireEvent("refreshEhrView", "G_02");
				}
				this.fireEvent("save", entryName, op, json, data)
			},

			loadData : function() {
				this.form.lastMenstrualPeriod = null;
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			},
			getLoadForm : function() {
				this.LoadForm = "PregnantVisitForm";
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getSystemConfigValue",
							method : "execute",
							body : "PregnantVisitFormType"
						});
				if (result.code > 300) {
					alert("页面参数获取失败，默认为纸质页面！")
					return
				}
				if (result.json.body) {
					var value = result.json.body;
					if (value == "paper") {
						this.LoadForm = "PregnantVisitFormHtml";
					} else {
						this.LoadForm = "PregnantVisitForm";
					}
				}
				return this.LoadForm
			},

			/**
			 * 获取第二项
			 * 
			 * @return {}
			 */
			getSecondItem : function() {
				var firstModule = this.midiModules[this.actions[0].id];
				var buttonIndex = 0;
				if (firstModule) {
					buttonIndex = firstModule.actions.length;
				}
				var LoadForm=this.getLoadForm();
                if(LoadForm=="PregnantVisitFormHtml"){
                	return this.getCombinedModule(this.actions[2].id,
    						this.actions[2].ref, buttonIndex);
                }else{
                	return this.getCombinedModule(this.actions[1].id,
    						this.actions[1].ref, buttonIndex);
                }
			}
		});