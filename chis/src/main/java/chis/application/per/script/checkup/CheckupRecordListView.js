$package("chis.application.per.script.checkup");

$import("chis.script.BizSimpleListView", "chis.script.EHRView");

chis.application.per.script.checkup.CheckupRecordListView = function(cfg) {
	cfg.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.per.script.checkup.CheckupRecordListView.superclass.constructor.apply(
			this, [cfg]);
	this.serviceAction = "logoutCheckupRecord";
	this.on("save", this.checkupRegisterSave, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupRecordListView,
		chis.script.BizSimpleListView, {
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.statusPer",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});

				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				cfg.items = ["状态", "-", comb];
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

				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[7];
				if (btn) {
					if (status != "0") {
						btn.disable();
					} else {
						btn.enable();
					}
				}

				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["CheckupRecord"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule");
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					m.on("onEmpiReturn", this.onAddCheckupRecord, this);
					this.midiModules["CheckupRecord"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onAddCheckupRecord : function(data) {
				var empiId = data.empiId;
				var phrId = data.phrId;
				this.initDataId = null;
				this.showModule(empiId, phrId, this.initDataId);
			},
			showModule : function(empiId, phrId, checkupNo) {
				var module = this.midiModules["CheckupRecord_EHRView"];
//				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['J_01'],
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							});
					this.midiModules["CheckupRecord_EHRView"] = module;
					module.exContext.ids["checkupNo"] = checkupNo;
					module.exContext.args["selectCheckupNo"] = checkupNo;
					module.on("save", this.refreshData, this);
//				} else {
//					module.exContext.ids = {};
//					module.exContext.ids["empiId"] = empiId;
//					module.exContext.ids["checkupNo"] = checkupNo;
//					module.exContext.args["selectCheckupNo"] = checkupNo;
//					module.refresh();
//				}
				module.getWin().show();
			},
			refreshData : function(entryName, op, json, data) {
				this.refresh();
			},
			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected();
				this.initDataId = r.id;
				var formHis = r.get("fromHis");
				if (formHis && formHis == "1") {
					this.showHisModule(r.get("empiId"), this.initDataId);
				} else {
					this.showModule(r.get("empiId"), r.get("phrId"),
							this.initDataId);
				}
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			showHisModule : function(empiId, checkupNo) {
				var module = this.midiModules["CheckupRecordHis_EHRView"];
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['J_02'],
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							});
					module.exContext.ids["checkupNo"] = checkupNo;
					this.midiModules["CheckupRecordHis_EHRView"] = module;
				} else {
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = empiId;
					module.exContext.ids["checkupNo"] = checkupNo;
					module.refresh();
				}
				module.getWin().show();
			},
			doLogOut : function(item, e) {
				// var cmd = item.cmd;
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '体检记录作废[' + r.id + ']',
							msg : r.get("name") + '的体检记录['
									+ r.get("checkupType_text")
									+ ']将被作废，确定是否继续？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									Ext.Msg.show({
												title : '确认作废体检记录[' + r.id
														+ ']',
												msg : '记录作废后将无法操作，是否继续?',
												modal : true,
												width : 300,
												buttons : Ext.MessageBox.YESNO,
												multiline : false,
												fn : function(btn, text) {
													if (btn == "yes") {
														this
																.logOutRecord(r.data);
													}
												},
												scope : this
											});
								}
							},
							scope : this
						});
			},
			logOutRecord : function(saveData) {

				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}

				this.saving = true;

				this.grid.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							method:"execute",
							op : "update",
							schema : this.entryName,
							body : {
								checkupNo : saveData.checkupNo,
								empiId : saveData.empiId
							}
						}, function(code, msg, json) {
							this.grid.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.logOutRecord, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.fireEvent("save", this.entryName,
										"update", json, this.data);
							}
							this.refreshData();
						}, this);
			},
			checkupRegisterSave : function(entryName, op, json, data) {
				this.list.refresh();
			}

		});