$package("chis.application.dbs.script.visit")
$import("util.Accredit", "app.modules.form.TableFormView",
		"chis.script.BizSimpleListView")

chis.application.dbs.script.visit.DiabetesVisitList = function(cfg) {
	this.entryName = "chis.application.pub.schemas.PUB_VisitPlan"
	this.current = 0
	this.selectedIndex = 0
	cfg.listServiceId = "chis.diabetesVisitService";
	cfg.bbar = this.getPagingToolbar()
	cfg.disablePagingTbr = true;
	chis.application.dbs.script.visit.DiabetesVisitList.superclass.constructor.apply(this, [cfg])
	this.nowDate = this.mainApp.serverDate

}
Ext.extend(chis.application.dbs.script.visit.DiabetesVisitList, chis.script.BizSimpleListView, {
	getPagingToolbar : function() {
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
		var buttons = []
		if (!actions) {
			return buttons
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {
				text : action.name,
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				enableToggle : (action.toggle == "true"),
				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons
	},
	onRowClick : function(grid, index, e) {
		// 加载已经随访的记录
		this.selectedIndex = index
		this.fireEvent("rowClick", grid, index, e, this)
	},
	onStoreBeforeLoad : function(store, op) {
		// 覆盖父类方法
//		var r = this.getSelectedRecord()
//		var n = this.store.indexOf(r)
//		if (n > -1) {
//			this.selectedIndex = n
//		}
	},
	onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store)
		if (!this.selectedIndex) {
			this.selectRow(0)
		} else {
			this.selectRow(this.selectedIndex);
		}
		var girdcount = 0;
//		store.each(function(r) {
//			var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
//			var serverDate = Date.parseDate(this.nowDate, "Y-m-d");
//			if (endDate < serverDate && r.get("planStatus") == "0") {
//				this.grid.getView().getRow(girdcount).style.backgroundColor = '#FF1493';
//			}
//			girdcount += 1;
//		}, this);
	},
	onReady : function() {
		chis.application.dbs.script.visit.DiabetesVisitList.superclass.onReady.call(this)
		this.requestData.empiId = this.exContext.ids.empiId;
		this.requestData.current = 0;
		this.requestData.yearType = 2;
		this.requestData.serviceAction = "getCurYearVisitPlan";
		var columns = this.grid.getColumnModel().getColumnsBy(function(c){
		  	c.sortable = false
		});
	},
	doPre : function() {
		this.current = this.current - 1;
		this.requestData.current = this.current;
		this.requestData.empiId = this.exContext.ids.empiId;
		this.requestData.serviceAction = "getPreYearVisitPlan";
		this.requestData.yearType = 1
		this.refresh();
	},
	doNow : function() {
		this.current = 0;
		this.requestData.current = this.current;
		this.requestData.empiId = this.exContext.ids.empiId;
		this.requestData.serviceAction = "getCurYearVisitPlan";
		this.requestData.yearType = 2
		this.refresh();
	},
	doNext : function() {
		this.current = this.current + 1;
		this.requestData.current = this.current;
		this.requestData.empiId = this.exContext.ids.empiId;
		this.requestData.serviceAction = "getNextYearVisitPlan";
		this.requestData.yearType = 3
		this.refresh();
	}
});