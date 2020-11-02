/**
 * 高血压随访整体模块
 * 
 * @author : ChenXianRui
 */
$package("chis.application.hq.script");

$import("chis.script.BizCombinedTabModule");

chis.application.hq.script.HighRiskVisitModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 170;
	this.width = 800;
	this.height = 600;
	chis.application.hq.script.HighRiskVisitModule.superclass.constructor
			.apply(this, [cfg]);
//	this.serviceId = "chis.hypertensionVisitService";
//	this.serviceAction = "saveHypertensionVisit";
//	this.entryName = "chis.application.hy.schemas.MDC_HypertensionVisit";
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.hq.script.HighRiskVisitModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.hq.script.HighRiskVisitModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this);
				return panel;
			},
			loadData : function() {
				this.list.firstLoad = true;
				this.exContext.args.selectedPlanId = this.exContext.args.selectedPlanId
						|| undefined;
				chis.application.hq.script.HighRiskVisitModule.superclass.loadData
						.call(this);
			}
			,onLoadModule : function(moduleId, module) {
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					this.visitForm = module;
					this.visitForm.on("savehighriskvist", this.list.refresh, this);
					this.visitForm.on("deletehighriskplan", this.list.refresh, this);
					this.visitForm.on("addhighriskplan", this.list.refresh, this);
				}
			}
			,onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.activeModule(0);
					this.visitForm.exContext.args = {};
					this.exContext.control = {"create":true,"update":true};
					this.visitForm.initDataId=null;
					this.visitForm.op ="create";
					this.visitForm.doNew();
					this.tab.enable();
					return;
				} else {
					this.tab.enable();
				}
				var index = this.list.selectedIndex;
				if (!index) {
					index = 0;
				}
				this.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
			}
			,onRowClick : function(grid, index, e) {
				if (!this.list) {
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.selectedIndex = index;
				this.process(r, index);
			}

			,process : function(r, index) {
				// ****获取下次随访记录,便于计算下次随访预约时间****
				var nextIndex = 0;
				if (!index) {
					nextIndex = 1;
				} else {
					nextIndex = index + 1;
				}
				var nextPlan = this.grid.store.getAt(nextIndex);
				var nextDateDisable = false;
				if (nextPlan) {
					var nextStatus = nextPlan.get("planStatus");
					if (nextStatus == '0') {
						this.exContext.args.nextMinDate = nextPlan.get("beginDate");
						this.exContext.args.nextMaxDate = nextPlan.get("endDate");
						this.exContext.args.nextPlanId = nextPlan.get("planId");
					} else {
						nextDateDisable = true;
					}
				}
				this.exContext.args.nextRemindDate = r.get("beginVisitDate");
				this.exContext.args.nextDateDisable = nextDateDisable;
				this.exContext.args.selectedPlanId = r.get("planId");
				this.exContext.args.empiId = r.get('empiId');
				this.exContext.args.phrId = r.get('recordId');
				this.exContext.args.planDate = r.get("planDate");
				this.exContext.args.beginDate = r.get("beginDate");
				this.exContext.args.endDate = r.get("endDate");
				this.exContext.args.planId = r.get("planId");
				this.exContext.args.visitId = r.get("visitId");
				this.exContext.args.sn = r.get("sn");
				this.exContext.args.planStatus = r.get("planStatus");
				this.exContext.args.fixGroupDate = r.get("fixGroupDate");
				Ext.apply(this.list.exContext, this.exContext);
                //初始化未随访记录的下次随访时间
				this.exContext.args.nextDate
				if(this.exContext.args.visitId.length==0 && nextPlan ){
				   this.exContext.args.nextDate=nextPlan.data.planDate
				}
				this.exContext.control = {"create":true,"update":true};
				this.initVisitFormData("create");
			}
			,initVisitFormData : function(op) {
				this.activeModule(0);
				var visitForm = this.midiModules[this.actions[0].id];
				if (!visitForm) {
					return;
				}
				Ext.apply(this.visitForm.exContext, this.exContext);
				visitForm.op = op;
				visitForm.initDataId = this.exContext.args.visitId;
				if (op == "create") {
					visitForm.doNew();
				}
				if (op == "update") {
					visitForm.loadData();
				}
			}
			,getLoadForm : function() {
				this.LoadForm = "VisitBaseForm";
				return this.LoadForm
			},

			groupActionsByTabType : function() {
				this.LoadForm = this.getLoadForm();
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					var refId = action.id;
					if (type == "tab") {
						if ((refId == "VisitBaseForm" || refId == "VisitPaperForm")
								&& refId != this.LoadForm) {
							// return
						} else {
							tabAction.push(action);
						}
					} else {
						otherAction.push(action);
					}
				}
				return {
					"tab" : tabAction,
					"other" : otherAction[0]
				}
			}
		});
