/**
 * 体弱儿随访, 随访列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.visit");
$import("chis.script.BizSimpleListView");
chis.application.cdh.script.debility.visit.DebilityChildrenVisitList = function(cfg) {
	cfg.listServiceId = "chis.debilityChildrenService";
	cfg.bbar = this.getBottomToolbar();
  cfg.pageSize = -1;
	cfg.disablePagingTbr = true;
	chis.application.cdh.script.debility.visit.DebilityChildrenVisitList.superclass.constructor
			.apply(this, [cfg]);
	this.selectFirst = true;
	
	this.current = 0;
	this.on("refresh", this.onRefresh, this);
};

Ext.extend(chis.application.cdh.script.debility.visit.DebilityChildrenVisitList,
		chis.script.BizSimpleListView, {

			onReady : function() {
				chis.application.cdh.script.debility.visit.DebilityChildrenVisitList.superclass.onReady
						.call(this);
				this.requestData.recordId = this.exContext.args.recordId;
				this.requestData.current = 0;
				this.requestData.serviceAction = "getCurYearVisitPlan";
			},

			loadData : function() {
				this.requestData.recordId = this.exContext.args.recordId;
				this.requestData.current = 0;
				this.current = 0;
				this.requestData.serviceAction = "getCurYearVisitPlan";
				chis.application.cdh.script.debility.visit.DebilityChildrenVisitList.superclass.loadData
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
					}
					buttons.push(btn)
				}
				return buttons
			},

			doPre : function() {
				this.current = this.current - 1;
				this.requestData.recordId = this.exContext.args.recordId;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getPreYearVisitPlan";
				this.refresh();
			},

			doNow : function() {
				this.current = 0;
				this.requestData.recordId = this.exContext.args.recordId;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getCurYearVisitPlan";
				this.refresh();
			},

			doNext : function() {
				this.current = this.current + 1;
				this.requestData.recordId = this.exContext.args.recordId;
				this.requestData.current = this.current;
				this.requestData.serviceAction = "getNextYearVisitPlan";
				this.refresh();
			},

			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			}
		});