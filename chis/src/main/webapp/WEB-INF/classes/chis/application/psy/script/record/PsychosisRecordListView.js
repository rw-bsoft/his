$package("chis.application.psy.script.record");

$import("chis.script.BizSimpleListView");

chis.application.psy.script.record.PsychosisRecordListView = function(cfg) {
	cfg.initCnd = cfg.initCnd || ["eq", ["$", "a.status"], ['s', '0']];
	this.entryName = "chis.application.psy.schemas.PSY_PsychosisRecord"
	this.needOwnerBar=true;
	chis.application.psy.script.record.PsychosisRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.exContext.control = {};
	this.businessType="10";
};

Ext.extend(chis.application.psy.script.record.PsychosisRecordListView,
		chis.script.BizSimpleListView, {
			// loadData : function(){
			// this.requestData.serviceId = this.serviceId ||
			// "chis.psychosisRecordService";
			// this.requestData.serviceAction = "findPsyRecordPageList";
			// this.requestData.initCnd = this.initCnd;
			// chis.application.psy.script.record.PsychosisRecordListView.superclass.loadData.call(this);
			// },

			onStoreLoadData : function(store, records, ops) {
				chis.application.psy.script.record.PsychosisRecordListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				// this.onRowClick();
			},

			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["PsychosisRecordListView_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule");
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["PsychosisRecordListView_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(data) {
				this.empiId = data.empiId;
				if (data.op && data.op == "update") {
					/*var manaDoctorId = data.manaDoctorId;
					if (manaDoctorId != this.mainApp.uid && !this.mainApp.fd
							&& this.mainApp.uid != 'system') {
						Ext.MessageBox.alert("提示", "非该精神病档案责任医生，没有权限查看！");
						return;
					}
					if (this.mainApp.fd && manaDoctorId != this.mainApp.fd
							&& manaDoctorId != this.mainApp.uid) {
						Ext.MessageBox
								.alert("提示", "非该精神病档案责任医生或相关医生助理，没有权限查看！");
						return;
					}*/
				}
				// 显示EHRView
				this.showEHRViewMule();
			},

			showEHRViewMule : function() {
				var cfg = {};
				cfg.initModules = ['P_01', 'P_02', 'P_03', 'P_04'];
				cfg.closeNav = true;
				cfg.mainApp = this.mainApp;
				cfg.empiId = this.empiId;
				var module = this.midiModules["PsychosisRecordListView_EHRView"];
				if (!module) {
					$import("chis.script.EHRView");
					module = new chis.script.EHRView(cfg);
					module.on("save", this.onSave, this);
					this.midiModules["PsychosisRecordListView_EHRView"] = module;
				} else {
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.getWin().show();
			},

			onSave : function() {
				this.refresh();
			},

			doModify : function() {
				if (this.store.getCount() == 0) {
					return
				}
				var r = this.grid.getSelectionModel().getSelected();
				var data = {};
				data.empiId = r.get("empiId");
				data.manaDoctorId = r.get("manaDoctorId");
				data.op = "update";
				this.onEmpiSelected(data);
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var status = r.get("status");
				var bts = this.grid.getTopToolbar().items;
				if (bts.items.length >= 8) {
					if (status == 2) {
						bts.items[7].disable();
						bts.items[8].enable();
					} else if (status == 1) {
						bts.items[7].disable();
						bts.items[8].enable();
					} else {
						bts.items[7].enable();
						bts.items[8].enable();
					}
				}
			},

			doPsychosisRecordLogout : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected();
				if (this.store.getCount() == 0) {
					return
				}
				this.phrId = r.get("phrId");

				var cfg = {};
				cfg.title = "精神病档案注销";
				cfg.phrId = r.get("phrId");
				cfg.empiId = r.get("empiId");
				cfg.personName = r.get("personName");
				cfg.autoLoadSchema = false;
				cfg.autoLoadData = true;
				cfg.isCombined = true;
				cfg.showButtonOnTop = true;
				cfg.colCount = 3;
				cfg.autoFieldWidth = false;
				cfg.fldDefaultWidth = 145;
				cfg.actions = [{
							id : "save",
							name : "确定"
						}, {
							id : "cancel",
							name : "取消",
							iconCls : "common_cancel"
						}];
				var module = this.midiModules["PsychosisRecordLogoutFormView"];
				if (!module) {
					$import("chis.application.psy.script.record.PsychosisRecordLogoutFormView");
					module = new chis.application.psy.script.record.PsychosisRecordLogoutFormView(cfg);
					module.on("save", this.onSave, this);
					this.midiModules["PsychosisRecordLogoutFormView"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.on("writeOff", this.onWriteOffToRefreshList, this);
				module.getWin().show();
			},

			doCheck : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if (this.store.getCount() == 0) {
					return
				}
				Ext.Msg.show({
					title : '确认注销记录[' + r.id + ']',
					msg : r.get("personName") + '的档案将注销，是否确定要注销该档案?',
					modal : true,
					width : 300,
					buttons : {
						ok : '确定注销',
						no : '恢复',
						cancel : "取消"
					},
					multiline : false,
					fn : function(btn, text) {
						if (btn == "no") {
							this.checkRecordLogout();
						}
						if (btn == "cancel") {
							return
						}

						if (btn == "ok") {
							Ext.Msg.show({
										title : '注销核实[' + r.id + ']',
										msg : '档案注销后将无法操作，是否继续?',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.YESNO,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "no") {
												return;
											}
											if (btn == "yes") {
												this
														.checkPsychosisRecordLogout();
											}

										},
										scope : this
									});
						}
					},
					scope : this
				});
			},

			checkRecordLogout : function() {
				var r = this.getSelectedRecord();
				var saveData = r.data;
				this.mask("正在执行操作...");
				util.rmi.jsonRequest({
							serviceId : "chis.psychosisRecordService",
							method : "execute",
							op : "update",
							schema : this.entryName,
							serviceAction : "revertPsychosisRecord",
							body : saveData
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										checkeRecordLogout, [saveData]);
								return
							}
							this.refresh();
						}, this);// jsonRequest
			},

			checkPsychosisRecordLogout : function() {
				var r = this.getSelectedRecord();
				var saveData = r.data;
				var cancellationReason = saveData["cancellationReason"];
				if (cancellationReason == "1" || cancellationReason == "2") {
					Ext.Msg.show({
								title : '确认注销[' + saveData.personName
										+ ']的所有档案',
								msg : "注销原因是迁出或死亡，将同时注销该人所有相关档案，是否继续？",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										// 核实注销全档
										this.saveAllDataToServer(saveData);
									}
								},
								scope : this
							});
				} else {
					// 核实注销子档
					this.saveSubDataToServer(saveData);
				}
			},

			saveSubDataToServer : function(saveData) {
				saveData["status"] = 1;
				this.mask("正在执行操作...");
				util.rmi.jsonRequest({
							serviceId : "chis.psychosisRecordService",
							method : "execute",
							op : "update",
							schema : this.entryName,
							serviceAction : "checkPsychosisRecordLogout",
							body : saveData
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.refresh();
							}
						}, this);// jsonRequest
			},

			saveAllDataToServer : function(saveData) {
				saveData["status"] = 1;
				this.mask("正在执行操作...");
				util.rmi.jsonRequest({
							serviceId : "chis.healthRecordService",
							serviceAction : "logoutAllRecords",
							method : "execute",
							op : "update",
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.refresh();
							}
						}, this);// jsonRequest
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
				comb.setWidth(100);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				var yearCheckItems=this.getYearCheckItems();
				cfg.items = [lab, comb,yearCheckItems];
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
					if (navCnd) {
						cnd.push(navCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},

			onWriteOffToRefreshList : function(entryName, op, json, data) {
				this.refresh();
			}

		});