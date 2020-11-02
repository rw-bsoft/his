// 高血压档案管理主页面
$package("chis.application.hy.script.record");

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.application.hy.script.record.HypertensionRecordWriteOffView");

chis.application.hy.script.record.HypertensionRecordListView = function(cfg) {
	//this.initCnd = cfg.cnds || ['eq', ['$', 'a.status'], ['s', '0']];
	this.initCnd = ['in', ['$', 'a.status'], ['0', '2']]
	//this.needOwnerBar = true;
	chis.application.hy.script.record.HypertensionRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.height = 470;
	this.width = 1010;
	this.confirmWriteOffServiceAction = "checkLogoutHypertensionRecord";
	this.activateId = "MDC_HypertensionRecord";
	this.checkServiceAction = "ifHypertensionRecordExist";
	this.businessType = "1";
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.hy.script.record.HypertensionRecordListView,
		chis.script.BizSimpleListView, {
			init : function() {
				this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						});
				this.requestData = {
					serviceId : this.serviceId,
					serviceAction : "listHypertensionRecord",
					method : "execute",
					schema : this.entryName,
					cnd : this.initCnd,
					pageSize : this.pageSize || 25,
					pageNo : 1
				};
				if (this.serverParams) {
					Ext.apply(this.requestData, this.serverParams);
				}
				if (this.autoLoadSchema) {
					this.getSchema();
				}
			},

			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				var yearCheckItems = this.getYearCheckItems();
				cfg.items = [lab, comb, yearCheckItems];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]];
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd && navCnd.length > 0) {
						cnd.push(navCnd);
					}
					if (queryCnd && queryCnd.length > 0) {
						cnd.push(queryCnd);
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},

			doCreateDoc : function() {
				var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								modal : true,
								mainApp : this.mainApp
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["EMPI.ExpertQuery"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(empi) {
				this.empiId = empi["empiId"];
				this.recordStatus = 0;
				this.activeTab = 0;
				this.showEhrViewWin();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.activeTab = 0;
				this.showEhrViewWin();
			},

			showEhrViewWin : function() {
				var cfg = {};
				cfg.closeNav = true;
				var visitModule = ['C_01', 'C_02', 'C_03', 'C_05', 'C_04','C_08'];
				// if (this.mainApp.exContext.hypertensionType == 'paper') {
				// visitModule = ['C_01', 'C_02', 'C_03_HTML', 'C_05', 'C_04'];
				// }
				cfg.initModules = visitModule;
				cfg.mainApp = this.mainApp;
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["HypertensionRecordListView_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["HypertensionRecordListView_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.exContext.ids.recordStatus = this.recordStatus;
				module.getWin().show();
			},

			onRecordCreated : function() {
				this.refresh();
			},

			onRowClick : function() {
				var r = this.getSelectedRecord();
				var toolBar = this.grid.getTopToolbar();
				if (!r) {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == "createDoc") {
							continue;
						}
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						btn[0].disable();
					}
					return;
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == "createDoc"
								|| this.actions[i].id == "confirmWriteOff"
								|| this.actions[i].id == "writeOff") {
							continue;
						}
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						btn[0].enable();
					}
				}
				var status = r.get("status");
				var cwoBtn = toolBar.find("cmd", "confirmWriteOff");// @@ 注销核实
				var woBtn = toolBar.find("cmd", "writeOff");// @@ 注销核实
				var vBtn = toolBar.find("cmd", "visit");// @@ 随访
				if (status == 0) {
					if (cwoBtn && cwoBtn.length > 0) {
						cwoBtn[0].disable();// @@ 注销核实
					}
					if (woBtn && woBtn.length > 0) {
						woBtn[0].enable();// @@ 注销
					}
					if (vBtn && vBtn.length > 0) {
						vBtn[0].enable();// @@ 随访
					}
				} else if (status == 2) {
					if (cwoBtn && cwoBtn.length > 0) {
						cwoBtn[0].enable();// @@ 注销核实
					}
					if (woBtn && woBtn.length > 0) {
						woBtn[0].disable();// @@ 注销
					}
					if (vBtn && vBtn.length > 0) {
						vBtn[0].disable();// @@ 随访
					}
				} else if (status == 1) {
					if (cwoBtn && cwoBtn.length > 0) {
						cwoBtn[0].disable();// @@ 注销核实
					}
					if (woBtn && woBtn.length > 0) {
						woBtn[0].disable();// @@ 注销
					}
					if (vBtn && vBtn.length > 0) {
						vBtn[0].enable();// @@ 随访
					}
				}
			},

			onDblClick : function() {
				this.doModify();
			},

			doVisit : function(item, e) {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "checkUpGroupRecord",
							method : "execute",
							empiId : this.empiId
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				if (result.json.haveRecord) {
					this.activeTab = 2;
				} else {
					this.activeTab = 1;
				}
				this.showEhrViewWin();
			},

			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
                if(this.mainApp.jobId=='chis.14'|| this.mainApp.jobId=='chis.system'||
                	record.get("manaDoctorId")==this.mainApp.uid){
                }else{
                	alert("只有防保科长或现责任医生能注销档案！");
                	return;
                }
				var writeOff = this.midiModules["MDC.DocWriteOff"];
				if (!writeOff) {
					writeOff = new chis.application.hy.script.record.HypertensionRecordWriteOffView(
							{
								record : record,
								mainApp : this.mainApp
							});
					writeOff.on("writeOff", this.onWriteOff, this);
					this.midiModules["MDC.DocWriteOff"] = writeOff;
				} else {
					writeOff.record = record;
				}
				writeOff.getWin().show();
			},

			onWriteOff : function() {
				this.refresh();
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.hy.script.record.HypertensionRecordListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				// this.grid.fireEvent("rowclick", this);
				this.onRowClick();

				var girdcount = 0;
				store.each(function(r) {
					var needVisit = r.get("needDoVisit");
					if (needVisit) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
			},

			doConfirmWriteOff : function() {
				Ext.apply(this.data, this.exContext);
				var record = this.getSelectedRecord();
				var status = record.get("status");
				if (status != "2") {
					Ext.Msg.alert("提示", "档案未注销，不需要核实！");
					return;
				}
				Ext.Msg.show({
							title : '注销核实[' + record.id + ']',
							msg : record.get("personName")
									+ '的档案将注销，是否确定要注销该档案?',
							modal : true,
							width : 300,
							buttons : {
								ok : '确定注销',
								no : '恢复',
								cancel : "取消"
							},
							// Ext.MessageBox.YESNO
							multiline : false,
							fn : function(btn, text) {
								if (btn == "no") {
									this.revertHypertensionRecord();
									return;
								}
								if (btn == "cancel") {
									return;
								}
								Ext.Msg.show({
											title : '注销核实[' + record.id + ']',
											msg : '档案注销后将无法操作，是否继续?',
											modal : true,
											width : 300,
											buttons : Ext.MessageBox.YESNO,
											multiline : false,
											fn : function(btn, text) {
												if (btn == "no") {
													return;
												}
												this
														.checkHypertensionRecordLogout();
											},
											scope : this
										});
							},
							scope : this
						});
				this.on("confirmWriteOff", this.onConfirmWriteOff, this);
			},
			revertHypertensionRecord : function() {
				var r = this.getSelectedRecord();
				var saveData = r.data;
				this.mask("正在执行操作...");
				util.rmi.jsonRequest({
							serviceId : "chis.hypertensionService",
							op : "update",
							method : "execute",
							schema : this.entryName,
							serviceAction : "revertHypertensionRecord",
							body : saveData
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.revertHypertensionRecord,
										[saveData]);
								return
							}
							this.fireEvent("confirmWriteOff");
						}, this);// jsonRequest

			},
			checkHypertensionRecordLogout : function() {
				var r = this.getSelectedRecord();
				var saveData = r.data;
				var cancellationReason = saveData["cancellationReason"];
				if (cancellationReason == "1" || cancellationReason == "2") {
					Ext.Msg.show({
						title : '确认注销[' + saveData.personName + ']的所有档案',
						msg : "注销原因是迁出或死亡，将同时注销该人所有相关档案，是否继续？",
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								this.logoutRecordService = "healthRecordService";
								this.logoutRecordAction = "logoutAllRecords";
								this.saveDataToServer(saveData);
							}
						},
						scope : this
					});
				} else {
					this.logoutRecordService = "hypertensionService";
					this.logoutRecordAction = "checkHypertensionRecordLogout";
					this.saveDataToServer(saveData);
				}
			},
			saveDataToServer : function(saveData) {
				saveData["status"] = 1;
				this.mask("正在执行操作...");
				util.rmi.jsonRequest({
							serviceId : this.logoutRecordService,
							op : "update",
							schema : this.entryName,
							serviceAction : this.logoutRecordAction,
							body : saveData
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveDataToServer, [saveData]);
								return
							}
							this.fireEvent("confirmWriteOff");
						}, this);// jsonRequest
			},
			onConfirmWriteOff : function() {
				this.refresh();
				this.lastSelected = null;
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd];
				if (module) {
					var win = module.getWin();
					if (xy) {
						win.setPosition(xy[0], xy[1]);
					}
					win.setTitle(module.title);
					win.show();
					// if (!win.hidden) {
					switch (cmd) {
						case "create" :
							module.doNew();
							module.setEmpiId(this.empiId);
							module.exContext = this.exContext;
							break;
						case "read" :
						case "update" :
							module.loadData();
					}
					// }
				}
			},

			loadModule : function(cls, entryName, item, r) {
				if (this.loading) {
					return
				}
				var cmd = item.cmd;
				var cfg = {};
				cfg.title = this.title + '-' + item.text;
				cfg.entryName = entryName;
				cfg.superGridEntryName = entryName;
				cfg.exContext.ids["empiId"] = this.empiId;
				cfg.op = cmd;
				cfg.showButtonOnTop = true;
				cfg.exContext = {};
				if (cmd != 'create') {
					cfg.exContext.ids["phrId"] = r.id;
					cfg.exContext[entryName] = r;
				}

				var id = this.refId;
				var moduleCfg = this.loadModuleCfg(id);
				var actions = moduleCfg.actions;
				if (moduleCfg.script) {
					cls = moduleCfg.script;
				}
				cfg.actions = actions;
				Ext.apply(cfg.exContext, this.exContext);
				if (this.serviceId) {
					cfg.saveServiceId = this.serviceId;
				}
				if (this.serviceAction) {
					cfg.serviceAction = this.serviceAction;
				}
				var m = this.midiModules[cmd];
				if (!m) {
					this.loading = true;
					// $require(cls, [function() {
					this.loading = false;
					cfg.autoLoadData = false;
					var module = eval("new " + cls + "(cfg)");
					module.on("save", this.onSave, this);
					module.on("close", this.active, this);
					module.opener = this;
					module.setMainApp(this.mainApp);
					this.midiModules[cmd] = module;
					this.fireEvent("loadModule", module);
					this.openModule(cmd, r, [100, 0]);
					module.on("recordCreated", this.onRecordCreated, this);
					module.on("writeOff", this.onWriteOff, this);
					// }, this])
				} else {
					Ext.apply(m, cfg);
					this.openModule(cmd, r);
				}
			},

			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id
						});
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id]);
					}
					return null;
				}
				return result.json.body;
			},

			onWinShow : function() {
			}
		});
