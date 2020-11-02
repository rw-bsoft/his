/**
 * 老年人随访计划列表
 * @param {} cfg
 */
$package("chis.application.ohr.script")

$import("chis.script.BizSimpleListView")

chis.application.ohr.script.OldPeopleVisitPlanList = function(cfg) {
	cfg.bbar = this.getBbar();
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	chis.application.ohr.script.OldPeopleVisitPlanList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.ohr.script.OldPeopleVisitPlanList, chis.script.BizSimpleListView, {
	
	init : function(){
		this.serverParams = {"queryDate" : Date.parseDate(
				this.mainApp.serverDate, "Y-m-d")};
		chis.application.ohr.script.OldPeopleVisitPlanList.superclass.init.call(this);
	},
	
	getBbar : function() {
		var actions = [{
					id : "pre",
					name : "上一年"
				}, {
					id : "now",
					name : "本年度"
				}, {
					id : "next",
					name : "下一年"
				}];
		var bbar = this.createBottomBar(actions);
		return bbar;
	},

	createBottomBar : function(actions) {
		var buttons = [];
		if (!actions) {
			return buttons;
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {
				accessKey : f1 + i,
				text : action.name,
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				enableToggle : (action.toggle == "true"),
				script : action.script,
				handler : this.doGoToYear,
				scope : this
			};
			buttons.push(btn);
		}
		return buttons;
	},

	doGoToYear : function(item, e) {
		this.selectIndex = 0;
		var queryDate = this.requestData.queryDate;
		if (item.cmd == "now") {
			this.requestData.queryDate = Date.parseDate(
					this.mainApp.serverDate, "Y-m-d");
		} else if (item.cmd == "pre") {
			this.requestData.queryDate = new Date(queryDate
					.setFullYear((queryDate.getFullYear() - 1)));
		} else if (item.cmd == "next") {
			this.requestData.queryDate = new Date(queryDate
					.setFullYear((queryDate.getFullYear() + 1)));
		}
		this.refresh();
	},

	loadData : function() {
		this.store.removeAll();
		this.grid.el.mask("正在查询...");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "loadPlanVisitRecords",
					method: "execute",
					entryName : this.entryName,
					body : this.requestData
				}, function(code, msg, json) {
					this.grid.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.saveToServer, this.requestData);
						return;
					}
					if (json.body) {
						this.store.removeAll();
						var result = json.body;
						for (var i = 0; i < result.length; i++) {
							var plan = result[i];
							var record = new Ext.data.Record(plan,
									plan.planId);
							this.store.add(record);
						}
						if (this.selectedPlanId) {
							this.selectSpecifiedRow();
						} else if (!this.selectIndex) {
							this.selectFirstRow();
						} else if (this.selectIndex == -1) {
							this.selectRow(this.store.getCount() - 1);
						} else {
							this.selectRow(this.selectIndex);
						}
					}
					this.fireEvent("loadData", this.store);
				}, this)
	},
	
	selectSpecifiedRow : function() {
		for (var i = 0; i < this.store.getCount(); i++) {
			var r = this.store.getAt(i);
			if (r.get("planId") == this.selectedPlanId) {
				this.grid.getSelectionModel().selectRecords([r]);
				this.grid.fireEvent("rowclick", this);
				return;
			}
		}
		this.selectedPlanId = null;
	}
});