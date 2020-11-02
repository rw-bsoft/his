$package("chis.application.psy.script.record");

$import("chis.script.BizTabModule","chis.script.util.widgets.MyMessageTip");

chis.application.psy.script.record.PsychosisRecordModule = function(cfg) {
	// this.entryName = "PSY_PsychosisRecord";
	// alert($encode(cfg.mainApp.exContext))
	cfg.autoLoadData = false;
	chis.application.psy.script.record.PsychosisRecordModule.superclass.constructor
			.apply(this, [cfg]);

	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.psy.script.record.PsychosisRecordModule,
		chis.script.BizTabModule, {
			filterActions : function(actions){
				if(!actions){
					actions = this.actions;
				}
				var as = [];
				if(this.mainApp.exContext.psychosisType == 'paper'){
					as.push(actions[2]);
					as.push(actions[3]);
				}else{
					as.push(actions[0]);
					as.push(actions[1]);
				}
				this.actions = as
				return this.actions;
			},
			initPanel : function() {
				var tab = chis.application.psy.script.record.PsychosisRecordModule.superclass.initPanel
						.call(this);
				this.tab = tab;
				return this.tab;
			},

			onAddModule : function() {
				var module = "B_10";
				if (this.mainApp.exContext.healthCheckType == 'paper') {
					module = "B_10_HTML";
				}
				this.fireEvent("addModule", module,true);
				this.fireEvent("activeModule", module, {
							dataSources : "5"
						});
			},

			onLoadModule : function(moduleName, module) {
				Ext.apply(module.exContext,this.exContext);
				if (moduleName == this.actions[0].id) {
					this.psyRecordForm = module;
					this.psyRecordForm.on("save", this.onSave, this);
					this.psyRecordForm.on("addModule", this.onAddModule, this);
				}
				if (moduleName == this.actions[1].id) {
					this.firstVisitModule = module;
					this.firstVisitModule.on("firstVisitSave",this.onFirstVisitSave,this);
				}
			},
			loadData : function() {
				this.activeModule(0);
				delete this.saving;
			},
			onFirstVisitSave : function(body){
				if(this.mainApp.exContext.psychosisType == 'paper'){
					if(!this.psyRecordForm.htmlFormSaveValidate()){
						return;
					}
					body.recordData = this.psyRecordForm.getSaveData();
					body.recordData.empiId = this.exContext.ids.empiId;
					body.recordData.phrId = this.exContext.ids.phrId;
					this.saveToServer(body);
				}else{
					if(!this.psyRecordForm.validate()){
						MyMessageTip.msg("提示","精神病档案的信息填写不完整或不正确!",true);
						return
					}else{
						body.recordData = this.psyRecordForm.getFormData();
						body.recordData.empiId = this.exContext.ids.empiId;
						body.recordData.phrId = this.exContext.ids.phrId;
					}
					this.saveToServer(body);
				}
				this.refreshEhrTopIcon();
			},
			onSave : function(saveData) {
				if(this.saving){
					return
				}
				if(this.mainApp.exContext.psychosisType == 'paper'){
					var body = {};
					body.recordData = saveData;
					body.recordData.empiId = this.exContext.ids.empiId;
					body.recordData.phrId = this.exContext.ids.phrId;
					if (!this.psyRecordForm) {
						return;
					}
					if (this.psyRecordForm.op == "create") {
						if (!this.firstVisitModule) {
							MyMessageTip.msg("提示", "请填写首次随访的信息",true);
							this.tab.activate(1);
							return
						} else {
							if(!this.firstVisitModule.checkFldValidate()){
								return;
							}
							var fvData = this.firstVisitModule.getSaveData();
							body.firstVisitData = fvData.firstVisitData;
							body.medicineList = fvData.medicineList;
							this.saveToServer(body);
						}
					}else{
						if (!this.firstVisitModule) {
							body.firstVisitData = null;
							body.medicineList = null;
						}else{
							if(!this.firstVisitModule.checkFldValidate()){
								return;
							}
							var fvData = this.firstVisitModule.getSaveData();
							body.firstVisitData = fvData.firstVisitData;
							body.medicineList = fvData.medicineList;
						}
						this.saveToServer(body);
					}
				}else{
					var body = {};
					var data = {};
					body.recordData = saveData;
					body.recordData.empiId = this.exContext.ids.empiId;
					if (!this.psyRecordForm) {
						return;
					}
					if (this.psyRecordForm.op == "create") {
						if (!this.firstVisitModule) {
							MyMessageTip.msg("提示", "请填写首次随访的信息",true);
							this.tab.activate(1);
							return
						} else {
							if (this.firstVisitModule && !this.firstVisitModule.validate()) {
								MyMessageTip.msg("提示", "首次随访的信息填写不完整",true);
								this.tab.activate(1);
								return
							}
							data = this.firstVisitModule.getFirstVisitData();
							body.firstVisitData = data;
							body.firstVisitData.empiId = this.exContext.ids.empiId;
							body.firstVisitData.phrId = this.exContext.ids.phrId;
							body.firstVisitData.type = '0';
							var medicineList = this.firstVisitModule.getMedicineList();
							body.medicineList = medicineList;
						}
					} else {
						if (!this.firstVisitModule) {
							body.firstVisitData = null;
							body.medicineList = null;
						} else {
							data = this.firstVisitModule.getFirstVisitData();
							if (data == null) {
								return
							}
							body.firstVisitData = data;
							var medicineList = this.firstVisitModule
									.getMedicineList();
							body.medicineList = medicineList;
						}
					}
					// this.saveToServer(body);
					if (data.deleteMedicine == true) {
						Ext.Msg.show({
									title : '消息提示',
									msg : '当前操作会引起服药数据删除,是否继续?',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											if (this.firstVisitModule) {
												this.firstVisitModule
														.deleteListAllMedicine();
											}
											this.saveToServer(body);
										} else {
											if (this.firstVisitModule) {
												this.firstVisitModule
														.regainFormMedicineValue();
											}
											return
										}
									},
									scope : this
								});
					} else {
						this.saveToServer(body);
					}
				}
				this.refreshEhrTopIcon();
			},

			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,saveData)) {
					return;
				}
				this.saving = true;
				this.psyRecordForm.saving = true;
				// var psyRecordFrom = this.midiModules[this.actions[0].id];
				if (this.firstVisitModule) {
					if(this.mainApp.exContext.psychosisType != 'paper'){
						this.firstVisitModule.op = this.firstVisitModule.form.op;
					}
					this.firstVisitModule.saving = true;
				}
				this.tab.el.mask("正在保存数据...", "x-mask-loading");
				debugger;
				util.rmi.jsonRequest({
							serviceId : "chis.psychosisRecordService",
							serviceAction : "savePsychosisRecordAndFirstVisit",
							method : "execute",
							recordOp : this.psyRecordForm.op || this.psyRecordForm.myOp || 'update',
							visitOp : this.firstVisitModule == undefined
									? ""
									: this.firstVisitModule.op,
							firstVisitData : saveData.firstVisitData,
							recordData : saveData.recordData,
							medicineList : saveData.medicineList,
							psychosisType : this.mainApp.exContext.psychosisType
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code > 300) {
								this.saving = false;
								this.psyRecordForm.saving = false;
								if(this.firstVisitModule){
									this.firstVisitModule.saving = false;
								}
								this.psyRecordForm.processReturnMsg(code, msg,
										this.psyRecordForm.saveToServer,
										[saveData]);
								return
							}
							Ext.apply(this.psyRecordForm.data, saveData);
							if (json.body) {
								json.body.psyRcordForm.op = "update";
								var psyRecordData = {};
								psyRecordData.body = json.body.psyRcordForm;
								// 重置随访tab状态
								this.fireEvent("save",
										this.psyRecordForm.entryName,
										this.psyRecordForm.op, psyRecordData,
										this.psyRecordForm.data);
								this.psyRecordForm.op = "update";
								if(this.mainApp.exContext.psychosisType == 'paper'){
									var control = json.body['PSY_PsychosisRecord_control'];
									if(control){
										this.exContext.control = control
										this.psyRecordForm.exContext.control = control;
									}
									this.psyRecordForm.initFormData(json.body.recordHTMLData);
									if (this.firstVisitModule) {
										this.firstVisitModule.op = "update";
										this.firstVisitModule.form.op = "update";
										var fvData = json.body.fvHTMLData;
										if(fvData){
											this.firstVisitModule.initFormData(fvData)
										}
									}
								}else{
									this.fireEvent("refreshData", "P_02");
									this.psyRecordForm.initFormData(json.body.psyRcordForm);
									if (this.firstVisitModule) {
										this.firstVisitModule.op = "update";
										this.firstVisitModule.form.op = "update";
										this.firstVisitModule.form.initFormData(json.body.firstVistForm);
									}
								}
								this.saving = false;
								this.psyRecordForm.saving = false;
								if(this.firstVisitModule){
									this.firstVisitModule.saving = false;
								}
							}
						}, this);// jsonRequest
			}

		});