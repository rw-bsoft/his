$package("chis.application.psy.script.visit");

$import("chis.script.BizSimpleListView");

chis.application.psy.script.visit.PsychosisVisitPlanList = function(cfg) {
	chis.application.psy.script.visit.PsychosisVisitPlanList.superclass.constructor
			.apply(this, [cfg]);
	this.entryName = 'chis.application.pub.schemas.PUB_VisitPlan'
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
	this.isCombined = true;

	this.bbar = this.getBottomToolbar();

	this.current = 0;

	this.selectedPlanId = 0;
};

Ext.extend(chis.application.psy.script.visit.PsychosisVisitPlanList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				this.selectedPlanId = this.exContext.args.selectedPlanId || 0;
				this.requestData.empiId = this.exContext.ids.empiId;
				this.requestData.serviceId = this.serviceId;
				if (this.firstLoad) {
					this.requestData.current = 0;
					this.requestData.serviceAction = "getCurYearVisitPlan";
					this.firstLoad = false;
				}
				chis.application.psy.script.visit.PsychosisVisitPlanList.superclass.loadData
						.call(this);
			},

			getBottomToolbar : function() {
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
				var f1 = 112;
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
						handler : this.doAction,
						scope : this
					};
					buttons.push(btn);
				}
				return buttons;
			},

			selectFirstRow : function() {
				if (!this.grid.hidden) {
					this.grid.el.focus();
				}
				if (this.grid && this.selectFirst) {
					var sm = this.grid.getSelectionModel();
					sm.selectFirstRow();
					this.grid.fireEvent("rowclick", this);
				}
			},

			// @@ 选中参数中传递的指定编号的行，以planId指定。
			selectSpecifiedRow : function() {
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get("planId") == this.selectedPlanId) {
						this.grid.getSelectionModel().selectRecords([r]);
						this.grid.fireEvent("rowclick", this, i);
						return;
					}
				}
				this.selectedPlanId = null;
				this.selectFirstRow();
			},

			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store);
				if (records.length == 0) {
					this.fireEvent("noVisitPlan");
					return;
				}
				if (this.win) {
					this.win.doLayout();
				}
				if (this.selectedPlanId) {// selectedPlanId
					this.selectSpecifiedRow();
				} else {
					this.selectFirstRow();
				}
				var girdcount = 0;
				store.each(function(r) {
					var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
					var serverDate = null;
					if (this.nowDate) {
						serverDate=Date.parseDate(this.nowDate, "Y-m-d");
					}
					if (endDate < serverDate && r.get("planStatus") == "0") {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#FF1493';
					}
					girdcount += 1;
				}, this);
			},

			onRefresh : function() {
				if (this.requestData.empiId == this.empiId) {
					var r = this.getSelectedRecord();
					if (r) {
						this.selectedPlanId = r.get("planId");
					}
				}
				this.requestData.empiId = this.empiId;
			},

			doPre : function() {
				this.selectedIndex = 0;
				this.current = this.current - 1;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getPreYearVisitPlan";
				this.loadData();
			},

			doNow : function() {
				this.selectedIndex = 0;
				this.current = 0;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getCurYearVisitPlan";
				this.loadData();
			},

			doNext : function() {
				this.selectedIndex = 0;
				this.current = this.current + 1;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getNextYearVisitPlan";
				this.loadData();
			}
		});