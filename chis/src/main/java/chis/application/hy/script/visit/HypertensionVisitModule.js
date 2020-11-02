/**
 * 高血压随访整体模块
 * 
 * @author : ChenXianRui
 */
$package("chis.application.hy.script.visit");

$import("chis.script.BizCombinedTabModule");

chis.application.hy.script.visit.HypertensionVisitModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 170;
	this.width = 800;
	this.height = 600;
	chis.application.hy.script.visit.HypertensionVisitModule.superclass.constructor
			.apply(this, [cfg]);
	this.serviceId = "chis.hypertensionVisitService";
	this.serviceAction = "saveHypertensionVisit";
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionVisit";
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.hy.script.visit.HypertensionVisitModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.hy.script.visit.HypertensionVisitModule.superclass.initPanel
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
				// this.selectedIndex = this.list.selectIndex || 0;
				this.exContext.args.selectedPlanId = this.exContext.args.selectedPlanId
						|| undefined;
				chis.application.hy.script.visit.HypertensionVisitModule.superclass.loadData
						.call(this);
			},
			onLoadModule : function(moduleId, module) {
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					this.visitForm = module;
					module.on("beforeLoadData",this.onVisitFormBeforeLoadData,this);
					module.on("loadData",this.onVisitFormLoadData, this);
					module.on("saveToServer",this.onVisitFormSaveToServer,this);
					module.on("visitRecordRefreshed",this.onVisitRecordRefreshed, this);
					module.on("medicineSelectChange",this.onMedicineSelectChange, this);
					module.on("visitEffect",this.onVisitEffect, this);
					module.on("visitPlanDelete",this.list.refresh,this)
				}
				if (moduleId == this.actions[1].id) {
					module.notNeedAddMedicine = this.notNeedAddMedicine;
					module.on("addItem", this.onAddItem, this);
					module.on("changeMedicine", this.onChangeMedicine, this);
					this.visitMedicineList = module;
				}
				if (moduleId == this.actions[2].id) {
					this.visitDescriptionForm = module;
				}
				if (moduleId == this.actions[3].id) {
					this.visitHealthTeachForm = module;
				}
			},
			onChangeMedicine : function() {
				this.onRowClick(this.grid, this.selectedIndex || 0);
			},
			onAddItem : function() {
				this.clearAllActived();
			},
			onVisitFormBeforeLoadData : function() {
				if (this.list.store.getCount() == 0) {
					return false;
				}
				return true;
			},
			onVisitFormLoadData : function(entryName, data) {
				if (data.medicine) {
					this.medicine = data.medicine.key;
				}
			},
			onVisitFormBeforeSave : function(entryName, op, saveData) {
				var rs = this.onActiveHyperVisDesForm();
				return rs;
			},
			onVisitFormSaveToServer : function(op, saveData) {
				var medicineList = [];
				if (this.visitMedicineList) {
					medicineList = this.visitMedicineList.getMedicineList();
				}
				saveData.medicineList = medicineList;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionVisitService",
							serviceAction : "checkNeedChangeGroup",
							method : "execute",
							op : op,
							body : saveData
						});
				if (result.code > 300) {
					return
				}
				if (result.json.body && result.json.body.errorMsg
						&& result.json.body.errorCode == "588") {
					MyMessageTip.msg("提示", result.json.body.errorMsg, true);
					return
				}
				if (result.json.body) {
					var needChangeGroup = result.json.body.needChangeGroup;
					var hypertensionGroupName = result.json.body.hypertensionGroupName;
					if (needChangeGroup) {
						Ext.Msg.show({
									title : '消息提示',
									msg : '当前操作会将病人转为' + hypertensionGroupName
											+ '管理,是否继续?',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											Ext.apply(saveData,
													result.json.body);
										}
										this.doVisitFormSave(op, saveData);
									},
									scope : this
								})
					} else {
						Ext.apply(saveData, result.json.body);
						this.doVisitFormSave(op, saveData);
					}
				} else {
					this.doVisitFormSave(op, saveData);
				}
			},
			doVisitFormSave : function(op, saveData) {
				this.visitForm.form.el.mask("正在提交请求，请稍候...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							schema : this.entryName,
							method : "execute",
							body : saveData,
							op : op,
							serviceAction : this.serviceAction
						}, function(code, msg, json) {
							this.visitForm.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return;
							}
							Ext.apply(saveData, json.body);
							if (saveData.visitEffect == "9") {
								this.fireEvent("writeOff");
								this.fireEvent("refreshModule", "C_01");
								this.fireEvent("refreshData", "all");
							}
							if (json.body) {
								if (json.body.needReferral == 1 && (this.visitForm.referralReason.getValue() == null || this.visitForm.referralReason.getValue() == "")) {
//									MyMessageTip.msg("温馨提示",
//											"鉴于你当前的情况，我们建议您转诊！", true);
									Ext.Msg.show({
										title : '温馨提示',
										msg : '鉴于你当前的情况，我们建议您转诊！',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.OK,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "ok") {
												this.visitForm.referralReason.focus();
												return;
											}
										},
										scope : this
									});
									return;
								}
								this.visitForm.initFormData(json.body);
								this.fireEvent("save", this.entryName, op,
										json, saveData);
								this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
								// this.fireEvent("visitRecordRefreshed",
								// saveData, json.body);
								if (saveData.medicine) {
									this.medicine = saveData.medicine;
									this.onVisitRecordRefreshed(saveData,
											json.body);
								}
								this.exContext.args.selectedPlanId = this.exContext.args.planId;
								this.list.loadData();
								// if (op == "create") {
								this.visitForm.visitIdChange(json.body.visitId);
								var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.systemCommonManageService",
									serviceAction : "getSystemConfigValue",
									method : "execute",
									body : "hypertensionType"
								});
								if (result.code > 300) {
									alert("页面参数获取失败，默认为纸质页面！")
									return
								}
								if (result.json.body) {
									var value = result.json.body;
									if (value != "paper") {
										this.exContext.args.visitId = json.body.visitId
									}
									// }

								}
							}
							this.visitForm.op = "update";
						}, this);// jsonRequest
			},
			// onVisitFormSave : function(entryName,op,json,data){
			// this.medicine = data.medicine;
			// this.list.loadData();
			// this.visitId = json.body.visitId;
			// if(this.visitId){
			// this.exContext.args.visitId = this.visitId;
			// }
			// this.onActiveHyperVisDesForm();
			// },
			onVisitRecordRefreshed : function(data, json) {
//				this.groupAlarm = json.groupAlarm;
//				if (this.groupAlarm == 2) {
//					Ext.Msg.alert('提示', "下一次随访之前需要进行评估，请做好相关准备工作。");
//				}
				var medicine = "";
				if (data.medicine) {
					medicine = data.medicine.key || data.medicine;
				} else {
					medicine = "";
				}
				this.medicine = medicine;
				if (medicine == 1 || medicine == 2) {
					this.addMedicine=true;
					this.setTabItemDisabled(1, false);
					// 基础表单加载时，如果用药依从性为“规律”或“间断”会打开药品增加页面*现去掉改为保存时提示
//					this.onActiveHyperVisDesForm();
				} else {
					this.addMedicine=false;
					this.setTabItemDisabled(1, true);
					this.setTabItemDisabled(2, false);
					this.setTabItemDisabled(3, false);
				}
			},
			onActiveHyperVisDesForm : function(recordNum) {
				if (recordNum) {
					if (recordNum > 0) {
						this.setTabItemDisabled(2, false);
						this.setTabItemDisabled(3, false);
					} else {
						this.setTabItemDisabled(2, true);
						this.setTabItemDisabled(3, true);
					}
				} else {
					if (this.visitForm) {
						if (this.medicine == "1" || this.medicine == "2") {
							var recordData = util.rmi.miniJsonRequestSync({
										serviceId : 'chis.hypertensionVisitService',
										serviceAction : 'checkHasMedicine',
										method : "execute",
										body : {
											visitId : this.exContext.args.visitId,
											phrId : this.exContext.args.phrId
										}
									});
							if (recordData.code == 200) {
								if (recordData.json.body) {
									var hasMedicine = recordData.json.body.hasMedicine;
									if (hasMedicine) {
										this.setTabItemDisabled(2, false);
										this.setTabItemDisabled(3, false);
									} else {
										this.setTabItemDisabled(2, true);
										this.setTabItemDisabled(3, true);
									}
									this.tab.setActiveTab(1);
									// @@
									// 标识是否需要自动弹出添加药品对话框，这个标识将被传给药品列表模块，由该模块根据此标识进行弹出与否。
									this.visitMedicineList.needAddMedicine = false;
									this.visitMedicineList.phrId = this.exContext.args.phrId;
									this.visitMedicineList.visitId = this.exContext.args.visitId;
									this.visitMedicineList.openAddWin();
									return true;
								}
								return false;
							} else {
								this.setTabItemDisabled(2, true);
								this.setTabItemDisabled(3, true);
								if (recordData.code > 300) {
									this.processReturnMsg(recordData.code,
											recordData.msg,
											this.onActiveHyperVisDesForm);
									return false;
								}
								return false;
							}
						}
					}
				}
			},
			onMedicineSelectChange : function(medicine) {
				this.medicine = medicine;
				// if (medicine == '1' || medicine == '2') {
				// this.setTabItemDisabled(1, false);
				// } else {
				// this.setTabItemDisabled(1, true);
				// }
				// this.setTabItemDisabled(2, true);
				// this.setTabItemDisabled(3, true);
			},

			onVisitEffect : function(veVal) {
				if (veVal != "1") {// 转归 终止管理或失访
					this.notNeedAddMedicine = true;
					// if(this.visitMedicineList){
					// this.visitMedicineList.notNeedAddMedicine = true;
					// }
				}
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					if (this.exContext.ids.status == '0') {
						Ext.Msg.alert("提示信息", "没有计划,无需随访!");
					}
					this.activeModule(0);
					this.visitForm.exContext.args = {};
					this.tab.disable();
					this.exContext.control = this.getFrmControl(null, null);
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
				// this.process(r, index);
				if (this.addMedicine) {
					this.tab.setActiveTab(1);
					// @@
					// 标识是否需要自动弹出添加药品对话框，这个标识将被传给药品列表模块，由该模块根据此标识进行弹出与否。
					this.visitMedicineList.needAddMedicine = false;
					this.visitMedicineList.phrId = this.exContext.args.phrId;
					this.visitMedicineList.visitId = this.exContext.args.visitId;
					// this.visitMedicineList.openAddWin();
					this.addMedicine = false;
				}
			},

			onRowClick : function(grid, index, e) {
				if (!this.list) {
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.selectedIndex = index;
				this.process(r, index);
			},

			process : function(r, index) {
				if (this.planId !== r.get("planId")) {
					// this.exContext.args.activeTabIndex = 0;//????
					this.exContext.args.neeAddMedicine = false;// 控制是否要弹出增加服药窗口
				}
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
						this.exContext.args.nextMinDate = nextPlan
								.get("beginDate");
						this.exContext.args.nextMaxDate = nextPlan
								.get("endDate");
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
				this.exContext.control = this.getFrmControl(r.get("planId"), r.get("visitId"));
				var status = r.get("planStatus");
				if (status == "1" || status == "2") {// 已访、失访
					// 第一个可用其他不可用
					this.setTabItemDisabled(0, false);
					if (status == "1") {
						this.setTabItemDisabled(2, false);
						this.setTabItemDisabled(3, false);
					}
					this.initVisitFormData("update");
					return;
				} else if (status == "3") {// 未访
					Ext.Msg.alert("提示", "本次计划未访，无法进行补录！");
					this.changeSubItemDisabled(true);
					this.initVisitFormData("update");
					return;
				} else if (status == "9") {
					Ext.Msg.alert("提示", "该计划已注销,无法进行操作！");
					this.changeSubItemDisabled(true);
					this.initVisitFormData("update");
					return;
				} else {// 过访,应访
					var beginDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
					// var endDate = Date.parseDate(r.get("planId"),"Y-m-d");
					var nowDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
							
					//开始时间之前两个星期可以随访
					if (beginDate-nowDate <= 1209600000 ) {
						this.beginVisitDate = r.get("beginVisitDate");
						this.setTabItemDisabled(0, false);
						this.changeSubItemDisabled(true, this.actions[0].id);
						this.initVisitFormData("create");
						return;
					} else if (beginDate-nowDate > 1209600000 ) {
						//this.changeSubItemDisabled(true);
						//Ext.Msg.alert("提示", "未到随访时间,无法进行随访!");
						//this.exContext.args.nonArrivalDate = true;
						//this.initVisitFormData("update");
						this.beginVisitDate = r.get("beginVisitDate");
						this.setTabItemDisabled(0, false);
						this.changeSubItemDisabled(true, this.actions[0].id);
						this.initVisitFormData("create");
						return;
					} else {
						this.beginVisitDate = r.get("beginVisitDate");
						this.changeSubItemDisabled(true, this.actions[0].id);
						this.initVisitFormData("create");
						return;
					}
				}
			},

			initVisitFormData : function(op) {
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
			},

			getFrmControl : function(planId, visitId) {
				var body = {};
				body.phrId = this.exContext.ids.phrId;
				body.empiId = this.exContext.ids.empiId;
				body.planId = planId || null;
				body.visitId = visitId || null;
				this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionVisitService",
							serviceAction : "getHyperVisitControl",
							method : "execute",
							body : body
						});
				this.panel.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json.body;
			},

			getLoadForm : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getSystemConfigValue",
							method : "execute",
							body : "hypertensionType"
						});
				if (result.code > 300) {
					alert("页面参数获取失败，默认为纸质页面！")
					this.LoadForm = "VisitPaperForm";
					return
				}
				if (result.json.body) {
					var value = result.json.body;
					if (value == "paper") {
						this.LoadForm = "VisitPaperForm";
					} else {
						this.LoadForm = "VisitBaseForm";
					}
				}
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
