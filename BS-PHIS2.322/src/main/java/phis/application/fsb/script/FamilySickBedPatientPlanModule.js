$package("phis.application.fsb.script")
$import("phis.script.SimpleModule")

phis.application.fsb.script.FamilySickBedPatientPlanModule = function(cfg) {
	this.serviceId = "familySickBedManageService"
	phis.application.fsb.script.FamilySickBedPatientPlanModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(phis.application.fsb.script.FamilySickBedPatientPlanModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							// tbar : this.createButtons(),
							items : [{
										layout : "border",
										border : false,
										region : 'center',
										items : [{
													layout : "fit",
													border : false,
													region : 'north',
													height : 70,
													items : this
															.getPatientForm()
												}, {
													layout : "fit",
													border : false,
													region : 'center',
													items : this
															.getPatientPlanList()
												}]
									}, {
										title : "计划模版",
										layout : "fit",
										border : false,
										region : 'east',
										collapsible : true,
										collapsed : true,
										bufferResize : 200,
										animCollapse : false,
										titleCollapse : true,
										items : this.getAssistant()
									}]
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			getPatientForm : function() {
				this.form = this.createModule("refPatientPlanForm",
						this.refPatientPlanForm);
				this.form.opener = this;
				var grid = this.form.initPanel();
				return grid;
			},
			getPatientPlanList : function() {
				this.list = this.createModule("refPatientPlanList",
						this.refPatientPlanList);
				this.list.opener = this;
				this.list.on("loadData", function() {
							if (this.list.store.getCount() == 0) {
								this.list.doInsert();
							}
						}, this);
				var grid = this.list.initPanel();
				return grid;
			},
			getAssistant : function() {
				this.quickInput = this.midiModules['refPatientPlanAssistant'];
				if (!this.quickInput) {
					var module = this.createModule("refPatientPlanAssistant",
							this.refPatientPlanAssistant);
					module.on('quickInput', this.quickInputPlan, this);
					this.quickInput = module;
				}
				return this.quickInput.initPanel();
			},
			quickInputPlan : function(record) {
				// 根据组套编号，获取明细
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "loadPlanDetails",
							body : {
								'ZTBH' : record.get("ZTBH")
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var plans = json.body;
							if (!plans)
								return;
							var o = this.list
									.getStoreFields(this.list.schema.items)
							var store = this.list.grid.getStore();
							var Record = Ext.data.Record.create(o.fields)
							this.list.removeEmptyRecord();
							var ypzh = 0;
							var lastYpzh = -1;
							if (store.getCount() > 0) {
								ypzh = parseInt(store.getAt(store.getCount()
										- 1).get("YPZH"));
							}
							for (var i = 0; i < plans.length; i++) { // 插入组套信息
								var data = plans[i];
								if (data.YPZH != lastYpzh) {
									lastYpzh = data.YPZH;
									data.YPZH = ++ypzh;
								} else {
									data.YPZH = ypzh;
								}
								data.ZYH = this.initDataId;
								data.JGID = this.mainApp.deptId
								delete data.JLBH;
								var r = new Record(data);
								store.add(r);
							}
						}, this);

			},
			onWinShow : function() {
				if (this.initDataId) {
					this.form.initDataId = this.initDataId;
					this.form.loadData();
					this.list.initCnd = ['eq', ['$', 'a.ZYH'],
							['l', this.initDataId]];
					this.list.requestData.cnd = ['eq', ['$', 'a.ZYH'],
							['l', this.initDataId]];
					this.list.loadData();
				}
			}
		})
