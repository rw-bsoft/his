$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicDisposalEntrySFModule = function(cfg) {
	this.openedBy = "chargeStation";
	cfg.modal = true;
	cfg.width = 1024;
	phis.application.cic.script.ClinicDisposalEntrySFModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	/**
	 * 监听快捷键 shortcutKeyFunc common.js有默认实现类
	 * 如有特殊需求要重写，需要重新定义监听的方法名称，否则会被common中的默认方法覆盖
	 */
	this.on("shortcutKey", this.shortcutKeyFunc, this);
}
Ext.extend(phis.application.cic.script.ClinicDisposalEntrySFModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
									}, this.getDisposalEntryTabList()],
							bbar : [{
										id : 'cflr',
										text : "处方录入",
										scope : this,
										iconCls : "forward",
										scale : "small",
										handler : this.goTab
									}]
						});
				this.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				this.panel.on("bodyresize", this.onBodyResize, this);
				return panel;
			},
			goTab : function(){
				this.fireEvent("doCflr",this);
				this.win.hide();
			},
			onBodyResize : function(panel, width, height) {
				var tab = this.quickInput.tab.getActiveTab();
				if (tab) {
					var m = this.quickInput.midiModules[tab.id];
					if (m) {
						m.grid.setHeight(height - 45);
					}
				}
			},
			getDisposalEntryList : function() {
				this.disposalEntryList = this.createModule(
						"getDisposalEntryList", this.refDisposalEntryList);
				this.disposalEntryList.opener = this;
				this.disposalEntryList.openedBy = this.openedBy;
				this.disposalEntryGrid = this.disposalEntryList.initPanel();
				// this.disposalEntryGrid.on("afterrender", this.onReady, this);
				this.disposalEntryList.on("loadData", function(store) {
							if (store.getCount() == 0) {
								this.disposalEntryList.doInsert();
							}
						}, this);
				if (this.exContext.ids.clinicId != 0) {
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
				module.grid.setHeight(height-80);
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
								if (store.getCount() > 0) {// 拿到最后一行的组号加1,重写分配组套过来的组号
									ypzh = (parseInt(store.getAt(store
											.getCount()
											- 1).get("YJZH")) + 1);
								}
								for (var i = 0; i < json.body.length; i++) {
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
//				r.set("YSDM", this.mainApp.uid);
//				r.set("YSDM_text", this.mainApp.uname);
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
				this.disposalEntryList.setZFBL(this.exContext.empiData.BRXZ, r);
			},
			onWinShow : function() {
				if (this.disposalEntryList) {
					this.disposalEntryList.setCountInfo();
					this.disposalEntryList.brid = this.exContext.ids.brid;
					this.disposalEntryList.brxz = this.exContext.empiData.BRXZ;
					this.disposalEntryList.brxm = this.exContext.empiData.personName
							|| this.exContext.ids.brxm;
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
							["eq", ["$", "BRID"],
									["s", this.exContext.ids.brid]],
							["eq", ["$", "JZXH"],
									["s", this.exContext.ids.clinicId]]];
					this.disposalEntryList.loadData();
				} else {
					this.disposalEntryList.requestData.cnd = ["in",
							["$", "a.YJXH"], this.yjxhs, "d"];
					this.disposalEntryList.loadData();
				}
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				if (this.disposalEntryList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.disposalEntryList.store.getCount(); i++) {
						if (this.disposalEntryList.store.getAt(i).get("YLXH")) {
							if (confirm('当前页面数据已经修改，是否保存?')) {
								this.disposalEntryList.needToClose = true;
								this.disposalEntryList.doSave();
								return false;
							} else {
								this.disposalEntryList.store.rejectChanges();
								return true;
							}
						}
					}
					this.disposalEntryList.store.rejectChanges();
				}
				return true;
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								maximized : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}
		});