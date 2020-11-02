$package("chis.application.dbs.script.visit")
$import("util.Accredit", "chis.script.BizCombinedTabModule",
		"chis.script.util.widgets.MyMessageTip")

chis.application.dbs.script.visit.DiabetesVisitModule = function(cfg) {
	this.autoLoadData = false
	this.hasNext = false; // 是否存在下一条被处理过的计划
	this.lodarForm = "";
	this.doQuery = "true";
	chis.application.dbs.script.visit.DiabetesVisitModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 160 // ** 第一个Item的宽度
	this.itemHeight = 432 // ** 第一个Item的高度
	this.nowDate = this.mainApp.serverDate;
	this.DiabetesMedicine = "chis.application.dbs.schemas.MDC_DiabetesMedicine";
	this.on("winShow", this.onWinShow, this);
	this.on("loadModule", this.onLoadModule, this);
}
Ext.extend(chis.application.dbs.script.visit.DiabetesVisitModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.dbs.script.visit.DiabetesVisitModule.superclass.initPanel.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.midiModules["DiabetesVisitList"] = this.list
				this.list.on("rowClick", this.onRowClick, this)
				this.list.on("loadData", this.onLoadData, this)
				this.list.on("refresh", this.onRefresh, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this);
				return panel;
			},
			getLoadForm : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getSystemConfigValue",
							method : "execute",
							body : "diabetesType"
						});
				if (result.code > 300) {
					alert("页面参数获取失败，默认为纸质页面！")
					this.lodarForm = "DiabetesVisitForm";
					return
				}
				if (result.json.body) {
					var value = result.json.body;
					if (value == "paper") {
						this.lodarForm = "DiabetesVisitFormPaper";
					} else {
						this.lodarForm = "DiabetesVisitForm";
					}
				}
				return this.lodarForm
			},
			onLoadModule : function(moduleId, module) {
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					module.on("loadDataByLocal", this.onLoadFormDataByLocal,this)
					module.on("save", this.onSave, this)
					module.on("visitPlanDelete",this.list.refresh,this)
					this.midiModules[this.lodarForm] = module
				}
				if (moduleId == this.actions[1].id) {
					this.midiModules["DiabetesVisitMedicineList"] = module
				}
				if (moduleId == this.actions[2].id) {
					this.midiModules["DiabetesComplicationListView"] = module
				}
				if (moduleId == this.actions[3].id) {
					this.midiModules["DiabetesRepeatVisitModule"] = module
					this.midiModules["DiabetesRepeatVisitModule"].loadData();
				}
				if (moduleId == this.actions[4].id) {
					this.midiModules["DiabetesVisitDescriptionForm"] = module
				}
				if (moduleId == this.actions[5].id) {
					this.midiModules["DiabetesVisitHealthTeachForm"] = module
					this.midiModules["DiabetesVisitHealthTeachForm"].loadData();
				}
			},
			onLoadFormDataByLocal : function(entryName, body, op) {
				this.exContext.control = body
				if (body.medicine) {
					if (body.medicine.key == "3" || body.medicine.key == "4") {
						this.tab.items.itemAt(1).disable()
					} else {
						this.tab.items.itemAt(1).enable()
					}
				}
				//下面这段控制了下次随访日期非最后条不能修改，完全鸡肋
//				if (this.result.json.body.isLastVisitedPlan
//						|| this.midiModules[this.lodarForm].op == "create") {
//					if (this.mainApp.exContext.diabetesType == "paper") {
//						this.midiModules[this.lodarForm].nextDate.enable();
//					} else {
//						this.midiModules[this.lodarForm].form.getForm()
//								.findField("nextDate").enable()
//					}
//				} else {
//					if (this.mainApp.exContext.diabetesType == "paper") {
//						this.midiModules[this.lodarForm].nextDate.disable();
//					} else {
//						this.midiModules[this.lodarForm].form.getForm()
//								.findField("nextDate").disable()
//					}
//				}
			},
			onMedicineFormSave : function(entryName, op, json, data) {
				this.midiModules["DiabetesVisitMedicineList"].onSave(entryName,op, json, data)
			},
			loadData : function() {
				this.clearAllActived();
				this.tab.setActiveTab(0);
				this.tab.enable();
				this.refreshExcontext();
				this.midiModules["DiabetesVisitList"].doNow();
			},
			onRefresh : function() {
				this.midiModules["DiabetesVisitList"].selectedIndex = 0;
			},
			onLoadData : function(store, records, opt) {
				this.clearAllActived();
				this.tab.setActiveTab(0);
				this.tab.enable();
				if (store.getCount() == 0) {
					this.tab.disable()
					return
				} else {
					this.tab.enable()
				}
				if (this.exContext.args.selectedPlanId) {
					if (store.find("planId", this.exContext.args.selectedPlanId) >= 0) {
						this.midiModules["DiabetesVisitList"].selectedIndex = store
								.find("planId",this.exContext.args.selectedPlanId);
					} else {
						this.midiModules["DiabetesVisitList"].selectedIndex = 0;
					}
				} else if (this.exContext.args.visitId) {
					if (store.find("visitId", this.exContext.args.visitId) >= 0) {
						this.midiModules["DiabetesVisitList"].selectedIndex = store
								.find("visitId", this.exContext.args.visitId);
					} else {
						this.midiModules["DiabetesVisitList"].selectedIndex = 0;
					}
				} else {
					this.midiModules["DiabetesVisitList"].selectedIndex = 0
				}
				if (this.midiModules["DiabetesVisitList"].selectedIndex > store
						.getCount()) {
					this.midiModules["DiabetesVisitList"].selectedIndex = 0
				}
				var r = store.getAt(this.midiModules["DiabetesVisitList"].selectedIndex)
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesVisitService",
							serviceAction : "initializeForm",
							method : "execute",
							body : r.data,
							empiData : this.exContext.empiData
						})
				this.result = result;
				this.process(r);
				this.midiModules[this.lodarForm].loadDataByLocal(result.json.body);
				// if (this.mainApp.exContext.diabetesType == "paper") {
				// var body = result.json.body;
				// var vmData = body[this.DiabetesMedicine + "_list"];
				// this.midiModules[this.lodarForm]
				// .setDBSVisitMedicineList(vmData);
				// }

				if (this.addMedicine) {
					this.tab.setActiveTab(1);
					// @@
					// 标识是否需要自动弹出添加药品对话框，这个标识将被传给药品列表模块，由该模块根据此标识进行弹出与否。
					this.midiModules["DiabetesVisitList"].needAddMedicine = true;
					this.midiModules["DiabetesVisitList"].phrId = this.exContext.args.phrId;
					this.midiModules["DiabetesVisitList"].visitId = this.exContext.args.visitId;
					this.addMedicine = false;
				}
			},
			onRowClick : function(grid, index, e) {
				this.clearAllActived()
				this.tab.setActiveTab(0)
				this.tab.enable()
				var r = grid.store.getAt(index)
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesVisitService",
							serviceAction : "initializeForm",
							method : "execute",
							body : r.data,
							empiData : this.exContext.empiData
						})
				this.result = result;
				this.process(r);
				this.midiModules[this.lodarForm].loadDataByLocal(result.json.body);
				if (this.mainApp.exContext.diabetesType == "paper") {
					var body = result.json.body;
					var vmData = body[this.DiabetesMedicine + "_list"];
					this.midiModules[this.lodarForm].setDBSVisitMedicineList(vmData);
				}
				if(!result.json.body["chis.application.dbs.schemas.MDC_DiabetesVisit_data"].visitId)
				{
					var nextr = grid.store.getAt(index+1)
					if(nextr){
				    	this.midiModules[this.lodarForm].nextDate.setValue(nextr.data.planDate);
					}
				    var idPostfix=this.midiModules[this.lodarForm].idPostfix;
				    document.getElementById("fbsTest_1"+idPostfix).checked=true;
				}
			},
			process : function(r) {
				this.refreshExcontext()
				this.tab.enable()
				if (!r) {
					return
				}
				this.exContext.args.r = r
				if (this.result && this.result.json.diabetesType) {
					this.midiModules[this.lodarForm].diabetesType = this.result.json.diabetesType;
				}
				//客户要求随访日期没有限制
				/*
				var minDate = (this.getDate(r.get("beginDate")))
				if (this.mainApp.exContext.diabetesType == "paper") {
					this.midiModules[this.lodarForm].visitDate
							.setMinValue(minDate);
					if (this.nowDate > r.get("endDate")) {
						this.midiModules[this.lodarForm].visitDate
								.setMaxValue(this.getDate(r.get("endDate")));
						this.midiModules[this.lodarForm].nextDate
								.setMinValue(Date.parseDate(this.nowDate,
										"Y-m-d").add(Date.DAY, 1));
					} else {
						this.midiModules[this.lodarForm].visitDate
								.setMaxValue(Date.parseDate(this.nowDate,
										"Y-m-d"));
						this.midiModules[this.lodarForm].nextDate
								.setMinValue(this.getDate(r.get("endDate"))
										.add(Date.DAY, 1));
					}
				} else {
					this.midiModules[this.lodarForm].visitDateField
							.setMinValue(minDate);
					if (this.nowDate > r.get("endDate")) {
						this.midiModules[this.lodarForm].visitDateField
								.setMaxValue(this.getDate(r.get("endDate")));
						this.midiModules[this.lodarForm].nextDateField
								.setMinValue(Date.parseDate(this.nowDate,
										"Y-m-d").add(Date.DAY, 1));
					} else {
						this.midiModules[this.lodarForm].visitDateField
								.setMaxValue(Date.parseDate(this.nowDate,
										"Y-m-d"));
						this.midiModules[this.lodarForm].nextDateField
								.setMinValue(this.getDate(r.get("endDate"))
										.add(Date.DAY, 1));
					}
				}
				*/
				//初始化下次计划随访日期
				var p = Date.parseDate(this.exContext.args.r.get('endDate'),"Y-m-d");
				var nextMinDate = new Date(p.getFullYear(),p.getMonth(),p.getDate()+1);
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesVisitService",
							serviceAction : "getStatus",
							method : "execute",
							body : r.data.recordId
						})
				if (result.code > 300) {
					this.status = "";
				}
				if (result.json.body && result.json.body.length > 0) {
					var status = result.json.body;
					this.status = status;
				}
				if (this.status == 1) {
					Ext.Msg.alert("提示", "该档案已经为注销！");
					this.tab.activate(0)
					this.tab.disable()
				}
				if (r.get("planStatus") == "0" || r.get("planStatus") == "1"
						|| r.get("planStatus") == "2" || r.get("planStatus") == "5") {
					if (r.get("visitId")) {
						this.tab.items.itemAt(1).enable()
						this.tab.items.itemAt(2).enable()
						this.tab.items.itemAt(3).enable()
						this.tab.items.itemAt(4).enable()
						if (this.midiModules["DiabetesVisitHealthTeachForm"]) {
							this.midiModules["DiabetesVisitHealthTeachForm"].loadData();
						}
					} else {
						var nowDate = Date.parseDate(this.nowDate, "Y-m-d")
						var startDate = Date.parseDate(r.get("beginDate"),"Y-m-d")
						var endDate = Date.parseDate(r.get("endDate"), "Y-m-d")
						//开始时间之前两个星期可以随访
						if (startDate - nowDate <= 1209600000) {
							if (endDate < nowDate) {
								this.midiModules[this.lodarForm].lateInput = "1"
							} else {
								this.midiModules[this.lodarForm].lateInput = "0"
							}
						} else if (startDate - nowDate > 1209600000) {
//							if (this.lodarForm == "DiabetesVisitFormPaper") {
//								MyMessageTip.msg("提示", "未到随访时间,无法进行随访!", true);
//							} else {
//								Ext.Msg.alert("提示", "未到随访时间,无法进行随访!");
//							}
//							this.tab.activate(0)
//							this.tab.disable()
//							return
						} else if (endDate < nowDate) {
							Ext.Msg.alert("提示", "超过随访时间,无法进行随访!");
							this.tab.activate(0)
							this.tab.disable()
							return
						}
						this.tab.items.itemAt(1).disable()
						this.tab.items.itemAt(2).disable()
						this.tab.items.itemAt(3).disable()
						this.tab.items.itemAt(4).disable()
					}
				} else if (r.get("planStatus") == "9") {
					Ext.Msg.alert("提示", "计划已经为注销，不能再进行随访");
					this.tab.activate(0)
					this.tab.disable()
				} else {
					Ext.Msg.alert("提示", "计划已经为未访，不能再进行随访");
					this.tab.activate(0)
					this.tab.disable()
				}
			},
			onSave : function(entryName, op, json, data) {
				var medicine = "";
				if (data.medicine) {
					medicine = data.medicine.key || data.medicine;
				} else {
					medicine = "";
				}
				this.medicine = medicine;
				if (medicine == 1 || medicine == 2) {
					this.addMedicine = true;
				} else {
					this.addMedicine = false;
				}
				if (data.visitEffect && data.visitEffect.key == '9') {
					this.fireEvent("refreshEhrView", "D_03")
				}
				this.exContext.args.selectedPlanId = data.planId
				this.loadData()
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
			},
			onWinShow : function() {
				this.loadData()
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : 1200,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								constrain : true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {this.fireEvent("winShow")}, this)
					win.on("close", function() {this.fireEvent("close", this)}, this)
					win.on("hide", function() {this.fireEvent("close", this)}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			},
			//actions分组
			groupActionsByTabType : function() {
				this.lodarForm = this.getLoadForm();
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					var refId = action.id;
					if (type == "tab") {
						if ((refId == "DiabetesVisitForm" || refId == "DiabetesVisitFormPaper")
								&& refId != this.lodarForm) {
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
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				// ** 模块是否已经激活过，即已经加载过数据，若为true则不再往下执行
				if (newTab.__actived) {
					if (newTab.name == 'DiabetesRepeatVisitModule') {
						this.midiModules["DiabetesRepeatVisitModule"]
								.loadData();
					}
					return;
				}
				// ** 模块是否已经初始化过，即是否已经构建过，若为true则刷新页面，fase则构建页面
				if (newTab.__inited) {
					// **面板刷新时可捕获此事件，改变面板。
					this.fireEvent("refreshInitedModule", newTab);
					this.refreshModule(newTab);
					return;
				}
				var p = this.getCombinedModule(newTab.name, newTab.exCfg.ref);
				newTab.add(p);
				newTab.__inited = true;
				this.tab.doLayout()
				var m = this.midiModules[newTab.name];
				// **面板加载完成，可捕获此事件，完善面板，如增加模块的事件捕捉
				this.fireEvent("loadModule", newTab.name, m);
				if (m.loadData && m.autoLoadData == false) {
					m.loadData()
					newTab.__actived = true;
				}
			}
		});