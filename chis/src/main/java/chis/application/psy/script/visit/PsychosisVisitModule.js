$package("chis.application.psy.script.visit");

$import("chis.script.BizCombinedTabModule","chis.script.util.widgets.MyMessageTip");

chis.application.psy.script.visit.PsychosisVisitModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 175;
	chis.application.psy.script.visit.PsychosisVisitModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.psy.script.visit.PsychosisVisitModule,
		chis.script.BizCombinedTabModule, {
			groupActionsByTabType : function() {
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					if (type == "tab") {
						if(i == 1 && this.mainApp.exContext.psychosisType == 'paper'){
							continue;
						}
						if(i == 2 && this.mainApp.exContext.psychosisType != 'paper'){
							continue;
						}
						tabAction.push(action);
					} else {
						otherAction.push(action);
					}
				}
				return {
					"tab" : tabAction,
					"other" : otherAction[0]
				}
			},
			initPanel : function() {
				var panel = chis.application.psy.script.visit.PsychosisVisitModule.superclass.initPanel
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
				chis.application.psy.script.visit.PsychosisVisitModule.superclass.loadData
						.call(this);
			},
			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onSave, this);
					module.on("medicineSelect", this.onMedicineSelect, this);
					this.visitForm = module;
				}
				if (moduleId == this.actions[1].id) {
					this.visitMedicineList = module;
					Ext.apply(module.exContext, this.exContext);
					this.visitMedicineList.on("remove",this.onMedicineListRemove,this);
				}
				if (moduleId == this.actions[2].id) {
					this.healthGuidanceForm = module;
					Ext.apply(module.exContext, this.exContext);
				}
			},
			onMedicineSelect : function(medicine) {
				if (!medicine || medicine == '3' || medicine == '') {
					this.setTabItemDisabled(1, true);
					this.delMedicine = true;
				} else {
					this.setTabItemDisabled(1, false);
					this.delMedicine = false;
				}
			},
			onMedicineListRemove:function(entryName,op,json,rData){
				this.fireEvent("refreshModule", "P_01");
				this.fireEvent("refreshData", "all");
			},
			onLoadGridData : function(store) {
				this.medicine = false;
				if (store.getCount() == 0) {
					MyMessageTip.msg("提示信息", "没有计划,无需随访!",true);
					this.activeModule(0);
					this.tab.disable();
					return;
				}
				var index = this.list.selectedIndex;
				if (!index) {
					index = 0;
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				this.process(r, index);
			},

			onRowClick : function(grid, index, e) {
				if (!this.list) {
					this.activeModule(0);
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					this.activeModule(0);
					return;
				}
				this.list.selectedIndex = index;
				this.process(r, index);
			},

			process : function(r, index) {
				if (!r) {
					return;
				}
				this.exContext.args.planStatus = r.get("planStatus");
				this.exContext.args.planId = r.get("planId");
				this.exContext.args.selectedPlanId = r.get("planId");
				this.exContext.args.planDate = r.get("planDate");
				this.exContext.args.sn = r.get("sn");
				var planStatus = r.get("planStatus");
				this.exContext.args.visitId = r.get("visitId") || null;
				var beginDate = Date.parseDate(r.get("beginDate"), "Y-m-d");
				var endDate = Date.parseDate(r.get("endDate"), "Y-m-d");
				var nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				if (planStatus == "0") {
					if (nowDate < beginDate) {
						MyMessageTip.msg("提示", "未到随访时间",true);
						this.tab.disable();
						this.activeModule(0);
						return
					}
					this.tab.enable();
				} else if (planStatus == "3") {
					MyMessageTip.msg("提示", "该计划处于未访状态,无法进行操作!",true);
					this.tab.disable();
					this.activeModule(0);
					return;
				} else if (planStatus == "9") {
					MyMessageTip.msg("提示", "该计划已注销,无法进行操作！",true);
					this.tab.disable();
					this.activeModule(0);
					return
				} else {
					this.tab.enable();
				}
				// 随访日期可选择的最大最小日期
				this.exContext.args.minDate = beginDate;
				if (endDate >= nowDate) {
					this.exContext.args.maxDate = nowDate;
				} else {
					this.exContext.args.maxDate = endDate;
				}
				var body = {
					"empiId" : this.exContext.ids.empiId,
					"phrId" : this.exContext.ids.phrId,
					"EHR_HealthRecord.status" : this.exContext.ids["phrId.status"],
					"PSY_PsychosisRecord.status" : this.exContext.ids["PSY_PsychosisRecord.phrId.status"],
					"PSY_PsychosisRecord.manaUnitId" : this.mainApp.deptId,
					"PSY_PsychosisRecord.manaDoctorId" : this.mainApp.uid
				};
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.psychosisRecordPaperService",
							serviceAction : "getPsyPaperControl",
							method : "execute",
							body : body
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				Ext.apply(this.exContext.control, result.json.body._actions);
				this.activeModule(0);
				if (this.visitForm) {
					Ext.apply(this.visitForm.exContext, this.exContext);
				}
				if (this.medicine == 1 || this.medicine == 2) {
					this.tab.setActiveTab(1);
					this.visitMedicineList.phrId = this.exContext.args.phrId;
					this.visitMedicineList.visitId = this.exContext.args.visitId;
					this.visitMedicineList.doAdd();
				}
			},
			onSave : function(saveData) {
				var body = {};
				if (this.visitMedicineList) {
					body.medicineList = this.visitMedicineList
							.getMedicineList();
				} else {
					body.medicineList = [];
				}
				if(this.mainApp.exContext.psychosisType=='paper'){
					body.visitData = saveData.visitData;
					if(body.medicineList.length > 0){
						body.medicineList.concat(saveData.medicineList);
					}else{
						body.medicineList = saveData.medicineList;
					}
				}else{
					body.visitData = saveData;
				}
				body.visitData.empiId = this.exContext.ids.empiId;
				body.visitData.phrId = this.exContext.ids.phrId;
				
				var medicineRecordNum = body.medicineList.length;
				this.deleteMedicine = false;
				if(!saveData.delMedicine){
					saveData.delMedicine = this.delMedicine || false;
				}
				if (medicineRecordNum > 0 && saveData.delMedicine == true) {
					Ext.Msg.show({
								title : '消息提示',
								msg : '当前操作会引起服药数据删除,是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.deleteMedicine = true;
										if (this.visitMedicineList) {
											this.visitMedicineList
													.delAllMedicine();
										}
										this.saveToServer(body);
									} else {
										if (this.visitForm && this.mainApp.exContext.psychosisType=='paper') {
											this.visitForm
													.regainMedicineValue();
										}
										return
									}
								},
								scope : this
							});
				} else {
					this.saveToServer(body);
				}
			},

			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.isMedicine = false;
				if (this.visitForm) {
					this.visitOp = this.visitForm.op;
					this.isMedicine = this.visitForm.isMedicine;
				} else {
					this.visitOp = "";
				}
				this.saving = true;
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var saveCfg = {
					serviceId : "chis.psychosisVisitService",
					serviceAction : "savePsychosisVisitAndMedicine",
					method : "execute",
					visitOp : this.visitOp,
					body : {
						visitData : saveData.visitData,
						medicineList : saveData.medicineList,
						deleteMedicine : this.deleteMedicine,
						planId : this.exContext.args.planId,
						planDate : this.exContext.args.planDate,
						sn : this.exContext.args.sn
					}
				};
				util.rmi.jsonRequest(saveCfg,
					function(code,msg,json){
						this.panel.el.unmask();
						this.visitForm.saving = false;
						if (code > 300) {
							this.visitForm.processReturnMsg(code, msg, [saveData]);
							return
						}
						if (json.body) {
							if (saveData.visitData.visitEffect == 9) {
								this.fireEvent("refreshModule", "P_01");
								this.fireEvent("refreshData", "all");
							}
							Ext.apply(this.list.exContext, this.exContext);
							this.list.refresh();
							var medicine = json.body.medicine
									|| saveData.visitData.medicine;
							this.medicine = medicine;
							this.exContext.args.visitId = json.body.visitId;
						}
						this.visitForm.op = "update";
					},this);

			}

		});