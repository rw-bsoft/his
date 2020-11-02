$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicDisposalEntryModule = function(cfg) {
	this.openedBy = "doctorStation";
	cfg.modal = true;
	cfg.width = 1024;
	phis.application.cic.script.ClinicDisposalEntryModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.ClinicDisposalEntryModule,
		phis.script.SimpleModule, {
			keyManageFunc : function(keyCode, keyName) {
				this.disposalEntryList
						.doAction(this.disposalEntryList.btnAccessKeys[keyCode]);
			},
			initPanel : function() {
				// 处置编辑
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										items : this.getDisposalEntryList()
									}, this.getDisposalEntryTabList()]
						});
				this.on("beforeclose", this.beforeClose, this);
				this.panel = panel;
				this.panel.on("bodyresize", this.onBodyResize, this);
				return panel;
			},
			onBodyResize : function(panel, width, height) {
				var tab = this.quickInput.tab.getActiveTab();
				if (tab) {
					var m = this.quickInput.midiModules[tab.id];
					if (m) {
						m.grid.setHeight(height - 50);
					}
				}
				if (this.bclLocked) {
					this.disposalEntryList.grid.stopEditing();
				}
			},
			getDisposalEntryList : function() {
				this.disposalEntryList = this.createModule(
						"getDisposalEntryList", this.refDisposalEntryList);
				this.disposalEntryList.opener = this;
				this.disposalEntryList.openedBy = this.openedBy;
				this.disposalEntryList.autoLoadData = false;
				this.disposalEntryGrid = this.disposalEntryList.initPanel();
				// this.disposalEntryGrid.on("afterrender", this.onReady, this);
				this.disposalEntryList.on("loadData", function(store) {
							this.opener.loading = false;// add by yangl
							// 电子病历中触发的事件会调用两次打开
							if (store.getCount() == 0) {
								if (!this.bclLocked) {
									this.disposalEntryList.doInsertAfter();
								}
							} else {
								if (!this.bclLocked) {
									this.disposalEntryList.doInsertAfter(null,
											null, true);
								}
							}
						}, this);
				if (this.exContext.ids.clinicId != 0) {
					this.disposalEntryList.jzxh = this.exContext.ids.clinicId;
					this.disposalEntryList.on("doSave", function() {
								this.fireEvent("doSave", "3");
							}, this)
				} else {
					this.disposalEntryList.on("doSave", function(body) {
								this.fireEvent("doSave", body);
							}, this);
					this.disposalEntryList.on("afterRemove", function(body) {
								this.fireEvent("doRemove", body);
							}, this);
				}
				return this.disposalEntryGrid;
			},
			getDisposalEntryTabList : function() {
				this.quickInput = this.midiModules['disposalEntryTabList'];
				if (!this.quickInput) {
					this.disposalEntryTabList = this.createModule(
							"getDisposalEntryTabList",
							this.refDisposalEntryTabList);
					this.disposalEntryTabList.on("afterTabChange",
							this.afterQuickInputTabChange, this);
					this.quickInput = this.disposalEntryTabList;
				}
				this.quickInput.exContext = this.exContext;
				this.quickInput.on("quickInput", this.quickInputClinic, this);
				var quickInputPanel = new Ext.Panel({
							layout : "fit",
							border : false,
							region : 'east',
							width : 250,
							collapsible : true,
							collapsed : true,
							bufferResize : 200,
							animCollapse : false,
							titleCollapse : true,
							floatable : false,
							items : this.quickInput.initPanel()
						});
				quickInputPanel.on("expand", this.onQuickInputExpend, this);
				this.quickInputPanel = quickInputPanel;
				return quickInputPanel;
			},
			afterQuickInputTabChange : function(module) {
				var height = this.panel.getHeight();
				module.grid.setHeight(height - 50);
			},
			onQuickInputExpend : function() {
				var tab = this.quickInput.tab.getActiveTab();
				if (!tab) {
					this.quickInput.tab.setActiveTab(0);
				}
			},
			quickInputClinic : function(tabId, record) {
				var serviceAction = "loadCostInfo";
				var body = {};
				if (tabId == "disposalPersonalSet") {
					body.ZTBH = record.get("ZTBH");
					serviceAction = "loadPersonalSet"
				} else if (tabId == "disposalAll") {
					body.FYXH = record.get("FYXH");
					serviceAction = "loadclinicAll"
				} else {
					body.JLBH = record.get("JLBH");
				}
				phis.script.rmi.jsonRequest({
							serviceId : "clinicDisposalEntryService",
							serviceAction : serviceAction,
							body : body
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var o = this.disposalEntryList
									.getStoreFields(this.disposalEntryList.schema.items)
							var store = this.disposalEntryList.grid.getStore();
							var Record = Ext.data.Record.create(o.fields)
							// 组套,调用组套调入功能
							if (tabId == 'disposalPersonalSet') {
								this.disposalEntryList.removeEmptyRecord();
								var ypzh = 1;
								var lastYpzh = -1;
								var lastFyks = -1;
								if (store.getCount() > 0) {// 拿到最后一行的组号加1,重写分配组套过来的组号
									ypzh = (parseInt(store.getAt(store
											.getCount()
											- 1).get("YJZH")) + 1);
								}
								for (var i = 0; i < json.body.length; i++) {
									if (json.body[i].errorMsg) {
										MyMessageTip
												.msg(
														"提示",
														'项目【'
																+ json.body[i].XMMC
																+ '】'
																+ json.body[i].errorMsg,
														true);
										continue;
									}
									if (json.body[i].YPZH != lastYpzh) {
										if (lastYpzh != -1) {
											ypzh++;
										}
										lastYpzh = json.body[i].YPZH;
										json.body[i].YJZH = ypzh;
									} else {
										json.body[i].YJZH = ypzh;
									}
									var r = new Record();
									store.add(r);
									this.quickInputClickBack(r, json.body[i]);
								}
							} else {// 常用项和全部
								// 判断是否重复
								var r = this.disposalEntryList
										.getSelectedRecord();
								if (r
										&& (r.get("YLXH") == null
												|| r.get("YLXH") == "" || r
												.get("YLXH") == 0)) {
									json.body.YJZH = r.get("YJZH");
								} else {
									this.disposalEntryList.doInsertAfter(null,
											null, false, true)
									r = this.disposalEntryList
											.getSelectedRecord();
									json.body.YJZH = r.get("YJZH");
								}
								var row = this.disposalEntryList.store.indexOf(r);
								for (var i = 0; i < store.getCount(); i++) {
									var record = store.getAt(i)
									if (record.get("YJZH") == json.body.YJZH) {
										if (record.get("YLXH") == json.body.FYXH) {
											MyMessageTip
													.msg(
															"提示",
															"【"
																	+ record
																			.get("FYMC")
																	+ "】在该组中已存在，禁止重复录入!",
															true);
											return;
										}
										//根据病人性质及收费项目对照情况进行判定提醒
										if((!row || i != row) && this.exContext.empiData.BRXZ==2000 && json.body.YYZBM!=undefined && record.get("YYZBM")!=undefined && json.body.YYZBM.replace(/\s+/g,"").length!=record.get("YYZBM").replace(/\s+/g,"").length){
												MyMessageTip.msg("提示", "医保病人的同一组处置中只能开自费项目或医保项目！", true);
												return;
										}
									}
								}
								this.quickInputClickBack(r, json.body);
							}
						}, this);
			},
			quickInputClickBack : function(r, data) {
				r.set("YJZH", data.YJZH);
				r.set("FYMC", data.XMMC);
				r.set("FYDW", data.FYDW);
				r.set("YLXH", data.FYXH);
				if (data.XMLX != null && data.XMLX != ''
						&& data.XMLX != undefined) {
					r.set("XMLX", data.XMLX);
				} else {
					r.set("XMLX", 0);
				}
				r.set("JGID", this.mainApp['phisApp'].deptId);
				r.set("YLSL", data.XMSL);
				r.set("YLDJ", data.FYDJ);
				r.set("HJJE", parseFloat(data.FYDJ * data.XMSL).toFixed(2));
				r.set("FYGB", data.FYGB);
				r.set("YJZX", 0);
				r.set("YSDM", this.mainApp.uid);
				r.set("YSDM_text", this.mainApp.uname);
				if (data.FYKS) {
					var store = this.disposalEntryList.grid.getStore();
					if (r.get("ZXKS") == null || r.get("ZXKS") == "") {
						for (var i = 0; i < store.getCount(); i++) {
							var rowItem = store.getAt(i)
							if (r.get("YJZH") == rowItem.get("YJZH")) {
								rowItem.set("ZXKS", data.FYKS);
								rowItem.set("ZXKS_text", data.FYKS_text);
							}
						}
					}
				}
				r.set("DZBL", 1);
				r.set("YYZBM", data.YYZBM);
				this.disposalEntryList.setZFBL(this.exContext.empiData.BRXZ, r);
			},
			onWinShow : function() {
				// ** add by yangl 门诊电子病历一次点击事件会触发多次
				if (this.loading)
					return
				this.loading = true;
				// *********
				if (this.disposalEntryList) {
					this.disposalEntryList.setCountInfo();
					this.disposalEntryList.brid = this.exContext.ids.brid;
					this.disposalEntryList.brxz = this.exContext.empiData.BRXZ;
					this.disposalEntryList.brxm = this.exContext.empiData.personName;
					if (this.exContext.ids.clinicId) {
						this.disposalEntryList.clinicId = this.exContext.ids.clinicId;
					} else {
						this.disposalEntryList.clinicId = 0;
					}
					if (this.exContext.ids.djly) {
						this.disposalEntryList.djly = this.exContext.ids.djly;
					} else {
						this.disposalEntryList.djly = 1;
					}
				}
				if (this.exContext.ids.clinicId > 0) {
					this.disposalEntryList.requestData.cnd = [
							"and",
							[
									"and",
									["eq", ["$", "BRID"],
											["d", this.exContext.ids.brid]],
									["eq", ["$", "JZXH"],
											["d", this.exContext.ids.clinicId]]],
							['or', ['isNull', ['$', 'FJLB']],
									['ne', ['$', 'FJLB'], ['d', '2']]]];
					this.disposalEntryList.loadData();
				} else {
					this.disposalEntryList.requestData.cnd = ["in",
							["$", "a.YJXH"], this.yjxhs];
					this.disposalEntryList.loadData();
				}
			},
			beforeClose : function() {
				this.disposalEntryList.needToClose = true;
				this.beforeclose();
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				if (this.disposalEntryList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.disposalEntryList.store.getCount(); i++) {
						if (this.disposalEntryList.store.getAt(i).get("YLXH")) {
							if (confirm('当前页面数据已经修改，是否保存?')) {
								return this.disposalEntryList.doSave();
							} else {
								this.disposalEntryList.store.rejectChanges();
								return true;
							}
						}
					}
					this.disposalEntryList.store.rejectChanges();
				}
				return true;
			}
		});