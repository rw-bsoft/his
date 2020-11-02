/**
 * 儿童档案列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.record")
$import("chis.script.BizSimpleListView",
		"chis.application.cdh.script.record.ChildrenHealthRecordWriteOffForm",
		"chis.script.util.helper.Helper")
chis.application.cdh.script.record.ChildrenHealthRecordList = function(cfg) {
	this.initCnd = cfg.cnds = ["ne", ["$", "a.status"], ["s", "1"]]
	chis.application.cdh.script.record.ChildrenHealthRecordList.superclass.constructor
			.apply(this, [cfg]);
	this.endManageServiceId = "chis.childrenHealthRecordService";
	this.endManageAction = "endManageCHR";
}

Ext.extend(chis.application.cdh.script.record.ChildrenHealthRecordList,
		chis.script.BizSimpleListView, {

			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["childrenHealthRecord"];
				if (!m) {
					$import("chis.application.cdh.script.base.ChildInfoForm")
					m = new chis.application.cdh.script.base.ChildInfoForm({
								entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
								title : "儿童基本信息查询",
								height : 450,
								width : 780,
								modal : true,
								mainApp : this.mainApp,
								isDeadRegist : false
							})
						
					m.on("save", this.onAddChildren, this);
					this.midiModules["childrenHealthRecord"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddChildren : function(entryName, op, json, data) {
				
				this.empiId = data.empiId;
				this.birthday = data.birthday;
				this.showModule(this.empiId);
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected();
				this.birthday = r.get("birthday");
				this.empiId = r.get("empiId");
				this.showModule(this.empiId);
			},

			doExport : function(item, e) {
				util.rmi.jsonRequest({
							serviceId : "chis.childrenRecordListExportService",
							serviceAction : "exportRecord",
							method : "execute"
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg)
								return;
							}
							window.open(json.body.data);
						}, this)
			},

			showModule : function(empiId) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.debilityChildrenService",
							serviceAction : "checkDebilityChildrenRecordExists",
							method : "execute",
							body : {
								"empiId" : empiId
							}
						});
				var childrenRecordExists = false;
				if (result.code == 200) {
					if (result.json) {
						childrenRecordExists = result.json.body.recordExists;
						
					}
				}
				$import("chis.script.EHRView");
				var module = this.midiModules["ChildrenHealthRecord_EHRView"];
				var age = this.getAge();
				if (!module) {
					var initModules = ['H_01', 'H_08', 'H_03', 'H_04'];
					if (childrenRecordExists) {
						if (age < 1) {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97',
									'H_09']
						} else if (age < 3) {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97',
									'H_98', 'H_09']
						} else {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97',
									'H_98', 'H_99', 'H_09'];
						}
					} else {
						if (age < 1) {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97']
						} else if (age < 3) {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97',
									'H_98']
						} else {
							initModules = ['H_01', 'H_02', 'H_03', 'H_97',
									'H_98', 'H_99'];
						}
					}
					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							})
					this.midiModules["ChildrenHealthRecord_EHRView"] = module;
					module.exContext.args["maxMonth"] = this.getMaxMonth();
					module.on("save", this.refreshList, this);
					module.getWin().show();
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.exContext.args["maxMonth"] = this.getMaxMonth();
					if (!childrenRecordExists
							&& module.activeModules["H_09"] == true) {
						module.activeModules["H_09"] = false;
						if (module.mainTab.find("mKey", "H_09")) {
							module.activeModules["H_09"] = false;
							module.mainTab.remove(module.mainTab.find("mKey",
									"H_09")[0]);
						}
					} else if (childrenRecordExists
							&& !module.activeModules["H_09"]) {
						module.activeModules["H_09"] = true;
						module.mainTab.add(module.getModuleCfg("H_09"));
					}
					if (age < 1) {
						if (module.activeModules["H_98"] == true) {
							module.activeModules["H_98"] = false;
							if (module.mainTab.find("mKey", "H_98")) {
								module.mainTab.remove(module.mainTab.find(
										"mKey", "H_98")[0]);
							}
						}
						if (module.activeModules["H_99"] == true) {
							module.activeModules["H_99"] = false;
							if (module.mainTab.find("mKey", "H_99")) {
								module.mainTab.remove(module.mainTab.find(
										"mKey", "H_99")[0]);
							}
						}
					} else if (age < 3) {
						if (!module.activeModules["H_98"]) {
							module.activeModules["H_98"] = true;
							module.mainTab.add(module.getModuleCfg("H_98"));
						}
						if (module.activeModules["H_99"] == true) {
							module.activeModules["H_99"] = false;
							if (module.mainTab.find("mKey", "H_99")) {
								module.mainTab.remove(module.mainTab.find(
										"mKey", "H_99")[0]);
							}
						}
					} else if (age >= 3) {
						if (!module.activeModules["H_98"]) {
							module.activeModules["H_98"] = true;
							module.mainTab.add(module.getModuleCfg("H_98"));
						}
						if (!module.activeModules["H_99"]) {
							module.activeModules["H_99"] = true;
							module.mainTab.add(module.getModuleCfg("H_99"));
						}
					}
					module.exContext.args = {} 
					module.refresh();
					module.getWin().show();
				}
			},

			refreshList : function(entryName, op, json, data) {
				
				if (entryName == this.entryName) {
					this.refresh();
				}
			},

			getMaxMonth : function() {
				var d = new Date(this.birthday);
				var d1 = new Date(this.mainApp.serverDate);
				var d2 = d1.getYear() - d.getYear();
				var d3 = d1.getMonth() - d.getMonth();
				if (d2 > 0) {
					if (d2 > 18) {
						var maxMonth = d2 * 12 + d3;
					} else {
						var maxMonth = 18;
					}
				} else {
					var maxMonth = 18;
				}
				return maxMonth;
			},

			onDblClick : function(grid, index, e) {
				var r = grid.getSelectionModel().getSelected();
				this.empiId = r.get("empiId");
				this.birthday = r.get("birthday");
				this.showModule(r.get("empiId"));
			},

			getAge : function() {
				this.currDate = Date
						.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth = Date.parseDate(this.birthday, "Y-m-d");
				return chis.script.util.helper.Helper.getAgeYear(birth, this.currDate);
			},

			getPagingToolbar : function(store) {
				var pagingToolbar = chis.application.cdh.script.record.ChildrenHealthRecordList.superclass.getPagingToolbar
						.call(this, store);
				var items = pagingToolbar.items;
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				items.insert(13, "lab", lab);
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true
						})
				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				items.insert(14, "comb", comb);
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
			onStoreLoadData : function(store, records, ops) {
				chis.application.cdh.script.record.ChildrenHealthRecordList.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},

			onRowClick : function(grid, index, e) {
				chis.application.cdh.script.record.ChildrenHealthRecordList.superclass.onRowClick
						.call(this, grid, index, e);
				var r = this.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.birthday = r.get("birthday");
				this.empiId = r.get("empiId");
				var endManageFlag = r.get("endManageFlag");
				var status = r.get("status");
				var age = this.getAge();
				var childrenEndManageAge = this.mainApp.exContext.childrenRegisterAge;
				var bts = this.grid.getTopToolbar().items;
				var embtn = bts.items[8];
				if (embtn) {
					if (age < childrenEndManageAge || endManageFlag == 'y' || status == '1') {
						embtn.disable();
					}  else {
						embtn.enable();
					}
				}
			},

			doEndManage : function() {// 儿童结案
				var r = this.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.birthday = r.get("birthday");
				this.empiId = r.get("empiId");
				var age = this.getAge();
				var childrenEndManageAge = this.mainApp.exContext.childrenRegisterAge;
				if (age < childrenEndManageAge) {
					Ext.Msg.alert("提示", "未到儿童档案结案年龄，不能进行结案操作！");
					return;
				} else {
					Ext.Msg.show({
								title : '确认结案',
								msg : '结案后档案将变为只读，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.processEndManage();
										this.grid.getTopToolbar().items[8]
												.disable()
									}
								},
								scope : this
							});
				}

			},

			processEndManage : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var endManageRequest = this.getEndManageRequest(r); // **
				// 获取删除条件数据
				if (!this.fireEvent("beforeRemove", this.entryName, r,
						endManageRequest)) {
					return;
				}
				this.mask("正在删除数据...");
				util.rmi.jsonRequest({
							serviceId : this.endManageServiceId,
							serviceAction : this.endManageAction,
							method:"execute",
							pkey : r.id,
							schema : this.entryName,
							body : endManageRequest
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.refresh();
								this.fireEvent("endManage", this.entryName,
										'update', json, r.data);
							} else {
								this.processReturnMsg(code, msg,
										this.doEndManage);
							}
						}, this);
			},

			getEndManageRequest : function(r) {
				return {
					phrId : r.get("phrId"),
					empiId : r.get("empiId")
				};
			}
		});