/**
 * 老年人随访管理模块
 * 
 * @param {}
 *            cfg
 */
$package("chis.application.ohr.script")

$import("chis.script.BizCombinedTabModule")

chis.application.ohr.script.OldPeopleManageModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 178;
	chis.application.ohr.script.OldPeopleManageModule.superclass.constructor.apply(this, [cfg]);
	this.on("loadModule", this.onLoadModule, this)
}

Ext.extend(chis.application.ohr.script.OldPeopleManageModule, chis.script.BizCombinedTabModule, {
	initPanel : function() {
		var panel = chis.application.ohr.script.OldPeopleManageModule.superclass.initPanel
				.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.otherActions.id];
		this.list.on("loadData", this.onLoadGridData, this)
		this.grid = this.list.grid;
		this.grid.on("rowClick", this.onRowClick, this)
		return panel;
	},

	onRowClick : function(grid, index, e) {
		this.activeTabIndex = 0;
		var r = grid.store.getAt(index);
		this.process(r, index);
	},

	onLoadGridData : function(store) {
		if (store.getCount() == 0) {
			this.tab.setDisabled(true);
			this.activeModule(0);
			return;
		}
		// ** 控制按钮权限
		var result = util.rmi.miniJsonRequestSync({
					serviceId : this.saveServiceId,
					serviceAction : "loadControl",
					method: "execute",
					empiId : this.exContext.ids.empiId
				})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		this.exContext.control = result.json.body;
		
		//modify by yuhua
		var index = 0;
		if (this.exContext.args.selectedPlanId) {
			if(store.find("planId", this.exContext.args.selectedPlanId)>=0){
				index = store.find("planId", this.exContext.args.selectedPlanId);
			}
		}else if(this.exContext.args.visitId){
			if(store.find("visitId", this.exContext.args.visitId)>=0){
				index = store.find("visitId", this.exContext.args.visitId);
			}
		}
//		var index = store.find("visitId", this.exContext.args.visitId);
		if (this.exContext.args.visitId) {
			this.exContext.args.visitId = null; // 清空从第一个页面列表传过来的参数
		}
//		if (index == -1) {
//			index = 0
//		}
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
			this.activeModule(this.activeTabIndex);
			this.setTabFormEnable(true);
			this.reSetTab(true);
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
			this.activeModule(this.activeTabIndex);
			this.setTabFormEnable(false);
			this.reSetTab(false);
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
			this.activeModule(this.activeTabIndex);
			this.setTabFormEnable(false);
			Ext.Msg.alert("提示信息", "该计划已注销,无法进行操作!");
		} else {
			var startDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
			var currDate = Date.parseDate(this.mainApp.serverDate,
					"Y-m-d");
			if (startDate > currDate) {
				this.setTabFormEnable(false);
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
				this.activeModule(this.activeTabIndex);
				this.setTabFormEnable(true);
				this.reSetTab(false);
			}
		}
	},

	setTabFormEnable : function(status) {
		if (status) {
			if (this.tab.disabled) {
				this.tab.setDisabled(false);
			}
		} else {
			if (!this.tab.disabled) {
				this.tab.setDisabled(true);
			}
		}
	},

	loadData : function() {
		this.activeTabIndex = 0;
		this.list.requestData.empiId = this.exContext.ids.empiId;
		this.list.requestData.queryDate = Date.parseDate(
				this.mainApp.serverDate, "Y-m-d");
		this.list.loadData()
	},

	onLoadModule : function(moduleId, module) {
		module.on("save", this.saveAllData, this)
	},

	saveAllData : function() {
		var recordModule = this.midiModules[this.actions[0].id];
		var checkModule = this.midiModules[this.actions[1].id];
		var descModule = this.midiModules[this.actions[2].id];

		// ****随访基础数据****
		var recordBody = recordModule.getFormData();
		if (!recordBody || recordBody.length < 1) {
			Ext.Msg.alert("提示信息", "请填写老年人随访的相关信息!");
			return;
		}
		// ****体检数据****
		var checkBody = [];
		if (checkModule && this.tab.items.itemAt(1).__actived) {
			checkBody = checkModule.getIndexData()
		}
		// **中医辩体描述**
		var descBody = {};
		if (descModule && this.tab.items.itemAt(2).__actived) {
			descBody = descModule.getDescription()
		}
		var op;
		if (recordBody.visitId) {
			op = "update";
		} else {
			op = "create";
		}
		this.tab.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.serviceAction,
					method: "execute",
					op : op,
					body : {
						"visitRecord" : recordBody,
						"checkupList" : checkBody,
						"description" : descBody
					}
				}, function(code, msg, json) {
					this.tab.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.onSuperFormRefresh, null);
						return;
					}
					this.exContext.args.visitId = json.body.visitId;
					this.list.refresh();
					this.activeTabIndex = this.tab.items.indexOf(this.tab.activeTab);
					if(json.body["visitEffect"] == '9'){
						this.fireEvent("refreshEhrView", "B_07");
					}
					this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
				}, this);
	},

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