/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule", "util.dictionary.DictionaryLoader");

phis.application.war.script.WardDoctorAdviceModule = function(cfg) {
	cfg.width = "1024";
	cfg.modal = true;
	cfg.listServiceId = "wardPatientManageService";
	this.serviceId = "wardPatientManageService";
	// this.exContext = {};
	phis.application.war.script.WardDoctorAdviceModule.superclass.constructor
			.apply(this, [cfg]);

	this.on("beforeclose", this.beforeClose, this);
	this.on("close", this.onClose, this);
}

Ext.extend(phis.application.war.script.WardDoctorAdviceModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.complexPanel) {
					return this.complexPanel;
				}
				if (this.openBy == "nurse") {
					if (this.exContext.systemParams) {
						Ext.apply(this.exContext.systemParams, this
										.loadSystemParams({
													commons : ['MZYP', 'JSYP'],
													privates : ['XSFJJJ_YS',
															'XSFJJJ_HS',
															'ZYYSQY',
															'QYKJYWGL',
															'QYKJYYY',
															'KJYSYTS',
															'QYZYKJYWSP']
												}));
					} else {
						this.exContext.systemParams = this.loadSystemParams({
									commons : ['MZYP', 'JSYP'],
									privates : ['XSFJJJ_YS', 'XSFJJJ_HS',
											'ZYYSQY', 'QYKJYWGL', 'QYKJYYY',
											'KJYSYTS', 'QYZYKJYWSP']
								});
					}

				}
				var panel = new Ext.Panel({
							// frame : true,
							layout : 'border',
							tbar : new Ext.Toolbar({
										enableOverflow : true,
										items : [this.createMyButtons()]
									}),
							items : [{
										layout : "fit",
										border : false,
										region : 'north',
										height : 76,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										region : 'center',
										items : this.getTab()
									}]
						});
				this.panel = panel;
				this.complexPanel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							// height : 500,
							items : [{
										layout : "fit",
										region : 'center',
										items : this.panel
									}, this.getAssistant()]
						});
				// this.complexPanel.on("bodyresize", this.onBodyResize,
				// this);
				this.complexPanel.on("afterrender", this.onReady, this)
				return this.complexPanel;
			},
			changeMode : function() {

			},
			createMyButtons : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}

					// ** add by yzh **
					var btnFlag;
					if (action.notReadOnly)
						btnFlag = false
					else
						btnFlag = this.exContext.readOnly || false

					var btn = {
						accessKey : f1 + i,
						text : action.name + "&nbsp;",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						disabled : btnFlag,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			onReady : function() {
				this.initDataId = this.exContext.brxx.get("ZYH");
				if (this.openBy == "nurse") {
					if (this.exContext.systemParams.ZYYSQY !== "1") {
//						this.panel.getTopToolbar().items.item(12).hide()
					}
					this.on("beforeWinShow", this.onWinShow, this);
				} else {
					this.onWinShow();
					this.complexPanel.on("destroy", this.onClose, this);
				}
			},
			onWinShow : function() {
				if (this.form && this.tabModule) {
					if (this.initDataId) {
						this.form.initDataId = this.initDataId;
						this.form.loadData();
						this.tabModule.initDataId = this.initDataId;
						this.tabModule.exContext = this.exContext;
						this.tabModule.tab.setActiveTab(0);
						var tab = this.tabModule.tab.getActiveTab();
						if (tab) {
							this.tabModule.moduleLoadData(tab.id);
						}
					}
				}
				if (this.quickInputPanel) {
					this.quickInputPanel.collapse();
				}
			},
			beforeClose : function() {
				var curTab = this.tabModule.tab.getActiveTab();
				if (curTab && this.tabModule.midiModules[curTab.id]) {
					var m = this.tabModule.midiModules[curTab.id];
					var rs = m.store.getModifiedRecords();
					var exrs = m.exList.store.getModifiedRecords();
					if (m.removeRecords.length > 0) {
						return this.confirmSave(m);
					}
					for (var i = 0; i < rs.length; i++) {
						if (rs[i].get("YPXH")
								|| (rs[i].get("JFBZ") == 3 && rs[i].get("YZMC"))) {
							return this.confirmSave(m);
						}
					}
					for (var i = 0; i < exrs.length; i++) {
						if (exrs[i].get("YPXH")) {
							return this.confirmSave(m);
						}
					}
					// m.removeEmptyRecord();
				}
				return true;
			},
			confirmSave : function(m) {
				if (confirm('当前医嘱数据已经修改，是否保存?')) {
					return this.doSave()
				} else {
					m.store.rejectChanges();
					m.removeRecords = [];
					m.exList.store.rejectChanges();
					m.exList.removeRecords = [];
					return true;
				}
			},
			getForm : function() {
				var module = this.createModule("wardDoctorAdviceForm",
						this.refWardDoctorAdviceForm);
				if (module) {
					this.form = module;
					module.opener = this;
					module.openBy = this.openBy;
					return module.initPanel();
				}
			},
			getTab : function() {
				var module = this.createModule("wardDoctorAdviceTab",
						this.refWardDoctorAdviceTab);
				if (module) {
					module.exContext = this.exContext;
					module.opener = this;
					module.openBy = this.openBy;
					module.on("tabchange", this.onTabchangeLR, this)
					this.tabModule = module;
					return module.initPanel();
				}
			},
			getAssistant : function() {
				this.quickInput = this.midiModules['wardQuickInput'];
				if (!this.quickInput) {
					var module = this.createModule("wardQuickInput",
							this.refWardQuickInputTab);
					module.on("tabchange", this.onTabchangeLR, this)
					// module.on("afterTabChange",
					// this.afterQuickInputTabChange,
					// this);
					this.quickInput = module;
				}
				this.quickInput.exContext = this.exContext;
				this.quickInput.on("quickInput", this.quickInputAdvice, this);
				var quickInputPanel = new Ext.Panel({
							layout : "fit",
							border : false,
							// split : true,
							region : 'east',
							width : 260,
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
			onTabchangeLR : function() {
				if (this.quickInput) {
					if (this.tabModule.newTableft) {
						if (this.tabModule.newTableft.id == "EmergencyMedicationTab"
								|| this.tabModule.newTableft.id == "DischargeMedicationTab") {
							this.quickInput.tab.activate(0);
						}
					}
				}
			},
			// afterQuickInputTabChange : function(module) {
			// var height = this.complexPanel.getHeight();
			// module.grid.setHeight(height - 50);
			// },
			onQuickInputExpend : function() {
				var tab = this.quickInput.tab.getActiveTab();
				if (!tab) {
					this.quickInput.tab.setActiveTab(0);
					// this.quickInput.midiModules[tab.id].tab.setActiveTab(0);
				} else {
					this.quickInput.tab.setActiveTab(0);
					tab = this.quickInput.tab.getActiveTab();
					this.quickInput.midiModules[tab.id].tab.setActiveTab(0);
					var n_tab = this.quickInput.midiModules[tab.id].tab
							.getActiveTab();
					this.quickInput.midiModules[tab.id].midiModules[n_tab.id].cndField
							.setValue("");
				}
			},
			quickInputAdvice : function(tabId, record) {
				if (tabId == "medicinePersonalSet"
						|| tabId == "clinicPersonalSet" || tabId == "characterPersonalSet") {
					this.doLoadAdviceSet(record.data);
				} else if (tabId == "medicineCommon" || tabId == "medicineAll") {
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "loadMedicineInfo",
								body : {
									tabId : tabId,
									YFXX : this.exContext.yfxx,
									YPXH : record.get("YPXH"),
									ZTLB : record.get("ZTLB"),
									JLBH : record.get("JLBH"),
									CFLX : record.get("CFLX"),
									wardId : this.mainApp['phis'].wardId
								}
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}
								// 1.判断当前是否有选中行，若没有，跳过2判断
								// 2.Grid当前选中行是否是新数据（没有调入过药品信息）
								// 3.如果是新数据，直接插入，否则按是否自动新组功能插入一条新记录
								if (!json.body) {
									MyMessageTip.msg("提示", '暂无库存!', true);
									return;
								}
								if (json.body.errorMsg) {
									MyMessageTip.msg("提示", json.body.errorMsg,
											true);
									return;
								}
								var tab = this.tabModule.tab.getActiveTab();
								var list = this.tabModule.midiModules[tab.id];
								var sypc = list.grid.getColumnModel()
										.getColumnById("SYPC").editor;
								var ypyf = list.grid.getColumnModel()
										.getColumnById("YPYF").editor;
								var sypc_r = sypc.findRecord("key",
										json.body.SYPC);
								var ypyf_r = ypyf.findRecord("key",
										json.body.GYTJ);
								if (tabId == "medicineCommon" && !sypc_r) {
									MyMessageTip.msg("提示", '药品【'
													+ json.body.YPMC
													+ '】的频次信息错误，终止调入!', true);
									return;
								}
								var r = list.getSelectedRecord();
								var cell = list.grid.getSelectionModel()
										.getSelectedCell();
								if (r
										&& (r.get("YPXH") == null
												|| r.get("YPXH") == "" || r
												.get("YPXH") == 0)) {
									if (r.get("YPLX") === 0) {
										if (cell[0] == (list.store.getCount() - 1)) {
											list.doNewGroup(null, null, true);
											r = list.getSelectedRecord();
										} else {
											MyMessageTip.msg("提示",
													"当前组不能录入药品医嘱!", true);
											return;
										}
									}
									if (r.get("YPYF") == null
											|| r.get("YPYF") == ""
											|| r.get("YPYF") == 0) {

										if (tabId == 'medicineAll') {
											r.set("YPYF", json.body.GYFF);
											r.set("YPYF_text",
													json.body.GYFF_text);
										} else {
											r.set("YPYF", json.body.GYTJ);
											r.set("YPYF_text",
													json.body.GYTJ_text);
										}
									}
									if (list.setRecordIntoList(json.body, r,
											cell[0])) {
										list.ypyfSelect(ypyf, ypyf_r, -1);// 附加项目
										list.grid.startEditing(cell[0], 5);
									} else {
										return;
									}
								} else {
									// 判断最后一行是否是药品
									var lastRecord = list.store
											.getAt(list.store.getCount() - 1);
									if (lastRecord
											&& lastRecord.get("YPLX") == 0) {
										list.doNewGroup(null, null, true);
									} else {
										list.doInsertAfter(null, null, false,
												true);
									}
									r = list.getSelectedRecord();
									cell = list.grid.getSelectionModel()
											.getSelectedCell();
									json.body.YZZH_SHOW = r.get("YZZH_SHOW")
									json.body.YZZH = r.get("YZZH")
									if (r.get("YPYF") == null
											|| r.get("YPYF") == ""
											|| r.get("YPYF") == 0) {

										if (tabId == 'medicineAll') {
											r.set("YPYF", json.body.GYFF);
											r.set("YPYF_text",
													json.body.GYFF_text);
										} else {
											r.set("YPYF", json.body.GYTJ);
											r.set("YPYF_text",
													json.body.GYTJ_text);
										}
									}
									if (list.setRecordIntoList(json.body, r,
											cell[0])) {
										list.ypyfSelect(ypyf, ypyf_r, -1);// 附加项目
										list.grid.startEditing(cell[0], 5);
									} else {
										return;
									}
									// this.setMedQuantity(r);
								}
								if (tabId == "medicineCommon") {
									if (!r.get("SYPC")) {
										r.set("SYPC", json.body.SYPC);
										r.set("SYPC_text", json.body.SYPC_text);
										r.set("MRCS", sypc_r.get("MRCS"));
										r.set("MZCS", 0);
										r.set("SRCS", sypc_r.get("MRCS"));
										r.set("YZZXSJ", sypc_r.get("ZXSJ"));
									}
								}
								var YZMC = json.body.YPMC
										+ (json.body.YFGG ? "/"
												+ json.body.YFGG : "")
										+ (json.body.YFDW ? "/"
												+ json.body.YFDW : "")
								r.modified["YZMC"] = YZMC;
								r.set("YZMC", YZMC);
								r.set("FYGB", json.body.FYGB);
								// var yfxx =
								// list.getYfsb(json.body.YPLX)
								r.set("YFSB", json.body.YFSB);
								r.set("YFSB_text", json.body.YFSB_text);
								list.onRowClick();

								// 调用需要异步的校验
								list.loop = new asynLoop(
										[list.checkAntibacterials], [json.body,
												r], list);
								list.loop.over = function() {
									if (list.grid.activeEditor != null) {
										list.grid.activeEditor.completeEdit();
									}
								}
								list.loop.start();
							}, this);
				} else if (tabId == "clinicAll" || tabId == "clinicCommon") {
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "loadClinicInfo",
								body : {
									FYXH : record.get("FYXH"),
									JLBH : record.get("JLBH"),
									tabId : tabId
								}
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}

								// 1.判断当前是否有选中行，若没有，跳过2判断
								// 2.Grid当前选中行是否是新数据（没有调入过药品信息）
								// 3.如果是新数据，直接插入，否则按是否自动新组功能插入一条新记录
								var tab = this.tabModule.tab.getActiveTab();
								var list = this.tabModule.midiModules[tab.id];
								var sypc = list.grid.getColumnModel()
										.getColumnById("SYPC").editor;
								var sypc_r = sypc.findRecord("key",
										json.body.SYPC);
								if (tabId == "clinicCommon" && !sypc_r) {
									MyMessageTip.msg("提示", '费用【'
													+ json.body.FYMC
													+ '】的频次信息错误，终止调入!', true);
									return;
								}
								var r = list.getSelectedRecord();
								var cell = list.grid.getSelectionModel()
										.getSelectedCell();
								if (r
										&& (r.get("YPXH") == null
												|| r.get("YPXH") == "" || r
												.get("YPXH") == 0)) {
									if (r.get("YPLX") > 0) {
										if (cell[0] == (list.store.getCount() - 1)) {
											list.doNewGroup(null, null, true);
											r = list.getSelectedRecord();
										} else {
											MyMessageTip.msg("提示",
													"当前组不能录入费用医嘱!", true);
											return;
										}
									}
									if (tabId == "clinicCommon") {
										r.set("YCSL", json.body.XMSL);
										if (!r.get("SYPC")) {
											r.set("SYPC", json.body.SYPC);
											r.set("SYPC_text", sypc_r
															.get("text"));
											r.set("MRCS", sypc_r.get("MRCS"));
											r.set("MZCS", 0);
											r.set("SRCS", 0);
											r.set("YZZXSJ", sypc_r.get("ZXSJ"));
										}
									}
									if (list.setRecordIntoList(json.body, r,
											cell[0])) {
										list.grid.startEditing(cell[0], 8);
									} else {
										return;
									}
								} else {
									// 判断最后一行是否是费用
									var lastRecord = list.store
											.getAt(list.store.getCount() - 1);
									if (lastRecord
											&& lastRecord.get("YPLX") > 0) {
										list.doNewGroup(null, null, true);
									} else {
										list.doInsertAfter(null, null, false,
												true);
									}
									r = list.getSelectedRecord();
									cell = list.grid.getSelectionModel()
											.getSelectedCell();
									json.body.YZZH_SHOW = r.get("YZZH_SHOW")
									json.body.YZZH = r.get("YZZH")
									if (tabId == "clinicCommon") {
										r.set("YCSL", json.body.XMSL);
										if (!r.get("SYPC")) {
											r.set("SYPC", json.body.SYPC);
											r.set("SYPC_text", sypc_r
															.get("text"));
											r.set("MRCS", sypc_r.get("MRCS"));
											r.set("MZCS", 0);
											r.set("SRCS", 0);
											r.set("YZZXSJ", sypc_r.get("ZXSJ"));
										}
									}
									if (list.setRecordIntoList(json.body, r,
											cell[0])) {
										list.grid.startEditing(cell[0], 8);
									} else {
										return;
									}
								}
								var YZMC = json.body.FYMC
										+ (json.body.FYDW ? "/"
												+ json.body.FYDW : "")
								r.modified["YZMC"] = YZMC;
								r.set("YZMC", YZMC);
								r.set("FYGB", json.body.FYGB);
								list.onRowClick();
							}, this);
				}else if (tabId == "characterCommon") {//add by lizhi 2017-12-04文字医嘱
					phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "loadCharacterInfo",
							body : {
								tabId : tabId,
								JLBH : record.get("JLBH")
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							// 1.判断当前是否有选中行，若没有，跳过2判断
							// 2.Grid当前选中行是否是新数据（没有调入过药品信息）
							// 3.如果是新数据，直接插入，否则按是否自动新组功能插入一条新记录
							if (json.body.errorMsg) {
								MyMessageTip.msg("提示", json.body.errorMsg,
										true);
								return;
							}
							var tab = this.tabModule.tab.getActiveTab();
							var list = this.tabModule.midiModules[tab.id];
							var r = list.getSelectedRecord();
							r.set("XMLX", 1);
							r.set("YPLX", 0);
							r.set("JFBZ", 3);
							r.set("BZXX", "(不计费医嘱)");
							r.set("YPXH", 0);
							if (tabId == "characterCommon") {
								r.set("YCSL", 1);
								r.set("YPDJ", 0);
								if (!r.get("SYPC")) {
									r.set("SYPC", json.body.SYPC);
									r.set("SYPC_text", json.body.SYPC_text);
									r.set("MRCS", 0);
									r.set("MZCS", 0);
									r.set("SRCS", 0);
								}
							}
							var YZMC = json.body.XMMC;
							r.modified["YZMC"] = YZMC;
							r.set("YZMC", YZMC);
							var cell = list.grid.getSelectionModel()
									.getSelectedCell();
							if (r
									&& (r.get("YPXH") == null
											|| r.get("YPXH") == "" || r
											.get("YPXH") == 0)) {
								if (r.get("YPLX") === 0) {
									if (cell[0] == (list.store.getCount() - 1)) {
										list.doNewGroup(null, null, true);
										r = list.getSelectedRecord();
									} else {
										MyMessageTip.msg("提示",
												"当前组不能录入文字医嘱!", true);
										return;
									}
								}
							} else {
								// 判断最后一行是否是药品
								var lastRecord = list.store
										.getAt(list.store.getCount() - 1);
								if (lastRecord
										&& lastRecord.get("YPLX") == 0) {
									list.doNewGroup(null, null, true);
								} else {
									list.doInsertAfter(null, null, false,
											true);
								}
							}
							list.onRowClick();
						}, this);
				}
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doInsert : function() {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doInsert();
				}
			},
			doNewGroup : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doInsertAfter(item, e, true);
				}
			},
			doGroupSet : function() {
				this.panel.el.mask("组套加载中...");
				var module = this.createModule("wardAdviceSetModule",
						this.refWardDoctorAdviceSet);
				if (module) {
					if (this.adviceSetWin) {
						this.adviceSetWin.show();
						this.panel.el.unmask();
						return;
					}
					module.opener = this;
					module.on("loadSet", this.doLoadAdviceSet, this);
					this.adviceSetWin = module.getWin();
					// this.adviceSetWin.add(module.initPanel());
					this.adviceSetWin.setHeight(400);
					this.adviceSetWin.setWidth(700);
					this.adviceSetWin.show();
				}
				this.panel.el.unmask();
			},
			doLoadAdviceSet : function(body) {
				body.BRXZ = this.exContext.brxx.get("BRXZ")
				body.BRID = this.exContext.brxx.get("BRID")
				body.ZYH = this.initDataId;
				body.YFXX = this.exContext.yfxx;
				body.wardId = this.mainApp['phis'].wardId;
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "loadAdviceSet",
							body : body
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var curTab = this.tabModule.tab.getActiveTab();
							var m = this.tabModule.midiModules[curTab.id];
							m.removeEmptyRecord();// 清空无效记录
							var sypc = m.grid.getColumnModel()
									.getColumnById("SYPC").editor;
							var ypyf = m.grid.getColumnModel()
									.getColumnById("YPYF").editor;
							// 组套调入
							var items = m.schema.items
							var o = m.getStoreFields(items)
							var store = m.grid.getStore();
							var Record = Ext.data.Record.create(o.fields)
							var factory = util.dictionary.DictionaryLoader
							this.ypzh = 1;
							this.lastYpzh = -1;
							if (store.getCount() > 0) {
								this.ypzh = parseInt(store.getAt(store
										.getCount()
										- 1).get("YZZH_SHOW"))
										+ 1;
							}
							var defaultData = {
								'_opStatus' : 'create'
							}
							for (var i = 0; i < items.length; i++) {
								var it = items[i]
								var v = null
								if (it.defaultValue) {
									v = it.defaultValue
									defaultData[it.id] = v
									var dic = it.dic
									if (dic) {
										defaultData[it.id] = v.key;
										var o = factory.load(dic)
										if (o) {
											var di = o.wraper[v.key];
											if (di) {
												defaultData[it.id + "_text"] = di.text
											}
										}
									}
								}
								if (it.type && it.type == "int") {
									defaultData[it.id] = (defaultData[it.id] == "0"
											|| defaultData[it.id] == "" || defaultData[it.id] == undefined)
											? 0
											: parseInt(defaultData[it.id]);
								}

							}
							if (body.ZTLB < 4) {// 药品信息
								// 调用需要异步的校验
								m.loop = new asynLoop([this.addMedAdviceSet], [
												json.body, defaultData], this);
								m.loop.iterator = function(rs, index) {
									var medInfo = rs[0][index];
									if (!medInfo)
										return null;
									return [medInfo, rs[1], rs[2]]
								}
								m.loop.over = function() {
									this.ypzh = 1;
									if (m.grid.activeEditor != null) {
										m.grid.activeEditor.completeEdit();
									}
									var r = m.getSelectedRecord();
									var cell = m.grid.getSelectionModel()
											.getSelectedCell();
									m.grid.startEditing(cell[0], 5);
								}
								m.loop.start();
								return;
							} else if (body.ZTLB == 5) {// add by lizhi 2017-12-04文字医嘱
								for (var i = 0; i < json.body.length; i++) {
									var wzInfo = json.body[i];
									if (wzInfo.errorMsg) {
										MyMessageTip.msg("提示", '文字医嘱【'
														+ wzInfo.XMMC + '】'
														+ wzInfo.errorMsg,
												true);
										continue;
									}
									Ext.applyIf(wzInfo, defaultData);
									if (wzInfo.YPZH != this.lastYpzh) {
										if (this.lastYpzh != -1) {
											this.ypzh++;
										}
										this.lastYpzh = wzInfo.YPZH;
										lastYzsj = Date.getServerDateTime();
										wzInfo.YZZH_SHOW = this.ypzh;
										wzInfo.KSSJ = lastYzsj;
									} else {
										wzInfo.YZZH_SHOW = this.ypzh;
										wzInfo.KSSJ = lastYzsj;
									}
									var r = new Record(wzInfo);
									r.set("YPXH", 0);
									r.set("XMLX", 1);
									r.set("JFBZ", 3);
									r.set("YCSL", 1);
									r.set("YPDJ", 0);
									r.set("YPDW", null);
									r.set("BZXX", "(不计费医嘱)");
									r.set("ZYH", this.initDataId);
									r.set("JGID",
											this.mainApp['phisApp'].deptId);
									r.set("YPLX", 0);
									if (this.openBy == "nurse") {
										r.set("YSGH", this.exContext.brxx
														.get("ZSYS"));
										r.set("YSGH_text", this.exContext.brxx
														.get("ZSYS_text"));
									} else {
										r.set("YSGH", this.mainApp.uid);
										r.set("YSGH_text", this.mainApp.uname);
									}
									r.set("YSBZ", this.openBy == "nurse"
													? 0
													: 1);
									r.set("YZMC",wzInfo.XMMC);
									r.modified["YZMC"] = r.get("YZMC");
									store.add(r);
								}
							} else {
								// 项目

								for (var i = 0; i < json.body.length; i++) {
									var cicInfo = json.body[i];
									if (cicInfo.errorMsg) {
										MyMessageTip.msg("提示", '项目【'
														+ cicInfo.XMMC + '】'
														+ cicInfo.errorMsg,
												true);
										continue;
									}
									Ext.applyIf(cicInfo, defaultData);
									if (cicInfo.SYPC) {
										var sypc_r = sypc.findRecord("key",
												cicInfo.SYPC);
										if (!sypc_r) {
											MyMessageTip.msg("提示", '项目【'
															+ cicInfo.XMMC
															+ '】的频次信息错误，终止调入!',
													true);
											continue
										}
									}
									var zfbl = cicInfo.payProportion.ZFBL;
									if (zfbl > 0) {
										// MyMessageTip.msg("提示",
										// cicInfo.XMMC +
										// "的自负比例是" + zfbl
										// + "!", true);
										cicInfo.ZFPB = 1;
									}
									if (cicInfo.YPZH != this.lastYpzh) {
										if (this.lastYpzh != -1) {
											this.ypzh++;
										}
										this.lastYpzh = cicInfo.YPZH;
										lastYzsj = Date.getServerDateTime();
										cicInfo.YZZH_SHOW = this.ypzh;
										cicInfo.KSSJ = lastYzsj;
									} else {
										cicInfo.YZZH_SHOW = this.ypzh;
										cicInfo.KSSJ = lastYzsj;
									}
									var r = new Record(cicInfo);
									r.set("XMLX", cicInfo.XMLX || 4);
									if (cicInfo.SYPC) {
										r.set("SYPC_text", sypc_r.get("text"));
										r.set("MRCS", sypc_r.get("MRCS"));
										r.set("MZCS", 0);
										// r.set("SRCS",
										// sypc_r.get("MRCS"));
										r.set("YZZXSJ", sypc_r.get("ZXSJ"));
									}
									r.set("YPXH", cicInfo.FYXH);
									r
											.set(
													"YZMC",
													cicInfo.XMMC
															+ (cicInfo.FYDW
																	? "/"
																			+ cicInfo.FYDW
																	: ""));
									r.set("YCSL", cicInfo.XMSL);
									r.set("YPDJ", cicInfo.FYDJ);
									r.set("YPDW", null);
									r.set("ZYH", this.initDataId);
									r.set("JGID",
											this.mainApp['phisApp'].deptId);
									r.set("YJZX", cicInfo.YJZX);
									r.set("YPLX", 0);
									r.set("ZXKS", cicInfo.FYKS
													|| this.exContext.brxx
															.get("BRKS"))
									if (this.openBy == "nurse") {
										r.set("YSGH", this.exContext.brxx
														.get("ZSYS"));
										r.set("YSGH_text", this.exContext.brxx
														.get("ZSYS_text"));
									} else {
										r.set("YSGH", this.mainApp.uid);
										r.set("YSGH_text", this.mainApp.uname);
									}
									r.set("YSBZ", this.openBy == "nurse"
													? 0
													: 1);
									r.modified["YZMC"] = r.get("YZMC");

									store.add(r);
								}
							}
							if (m.store.getCount() > 0) {
								m.grid.getSelectionModel().select(
										m.store.getCount() - 1, 3);
							}
						}, this);

			},
			addMedAdviceSet : function(medInfo, defaultData) {
				var curTab = this.tabModule.tab.getActiveTab();
				var m = this.tabModule.midiModules[curTab.id];
				var items = m.schema.items
				var o = m.getStoreFields(items)
				var store = m.grid.getStore();
				var Record = Ext.data.Record.create(o.fields)
				var sypc = m.grid.getColumnModel().getColumnById("SYPC").editor;
				var ypyf = m.grid.getColumnModel().getColumnById("YPYF").editor;
				var sign = 0;
				Ext.applyIf(medInfo, defaultData);
				if (medInfo.errorMsg) {
					MyMessageTip.msg("提示", medInfo.errorMsg, true);
					m.loop.next();
					return;
				}
				if (!m.checkDoctorPermission(medInfo)) {// 判断药品权限
					m.loop.next();
					return;
				}
				if (medInfo.isAllergy) {
					var errorMsg = "病人【" + this.exContext.brxx.get("BRXM")
							+ "】对药物【" + data.YPMC + "】过敏";
					if (body.BLFY && body.BLFY.length > 0) {
						errorMsg += ",\n不良反应为:" + (body.BLFY || '')
					}
					MyMessageTip.msg("禁止录入", errorMsg, true);
					m.loop.next();
					return;
				}
				var sypc_r = sypc.findRecord("key", medInfo.SYPC);
				var ypyf_r = ypyf.findRecord("key", medInfo.GYTJ);

				if (!sypc_r) {
					MyMessageTip.msg("提示", '药品【' + medInfo.YPMC
									+ '】的频次信息错误，终止调入!', true);
					m.loop.next();
					return;
				}
				if (medInfo.PSPB > 0) {
					MyMessageTip.msg("注意", '药品【' + medInfo.YPMC
									+ '】是皮试药品，需要做皮试处理!', true);
				}
				var zfbl = medInfo.payProportion.ZFBL;
				if (zfbl > 0) {
					// MyMessageTip.msg("提示",
					// medInfo.YPMC +
					// "的自负比例是" + zfbl
					// + "!", true);
					medInfo.ZFPB = 1;
				}
				if (medInfo.YPZH != this.lastYpzh) {
					if (this.lastYpzh != -1) {
						this.ypzh++;
					}
					this.lastYpzh = medInfo.YPZH;
					this.lastYzsj = Date.getServerDateTime();
					medInfo.YZZH_SHOW = this.ypzh;
					medInfo.KSSJ = this.lastYzsj;
					sign = 1;
				} else {
					medInfo.YZZH_SHOW = this.ypzh;
					medInfo.KSSJ = this.lastYzsj;
				}
				medInfo.YZMC = medInfo.YPMC
						+ (medInfo.YFGG ? "/" + medInfo.YFGG : "")
						+ (medInfo.YFDW ? "/" + medInfo.YFDW : "");
				medInfo.YPDJ = medInfo.LSJG;
				medInfo.YCSL = parseFloat(medInfo.YCJL / medInfo.YPJL)
						.toFixed(2)
				var r = new Record(medInfo);

				r.set("YJXH", 0);
				r.set("XMLX", 1);
				r.set("MRCS", sypc_r.get("MRCS"));
				r.set("MZCS", 0);
				r.set("SRCS", sypc_r.get("MRCS"));
				r.set("YZZXSJ", sypc_r.get("ZXSJ"));
				r.set("YPYF", medInfo.GYTJ);
				r.set("YPYF_text", medInfo.GYTJ_text);
				r.set("ZYH", this.initDataId);
				r.set("JGID", this.mainApp['phisApp'].deptId);
				if (this.openBy == "nurse") {
					r.set("YSGH", this.exContext.brxx.get("ZSYS"));
					r.set("YSGH_text", this.exContext.brxx.get("ZSYS_text"));
				} else {
					r.set("YSGH", this.mainApp.uid);
					r.set("YSGH_text", this.mainApp.uname);
				}
				// var yfxx =
				// m.getYfsb(medInfo.YPLX)
				r.set("YFSB", medInfo.YFSB);
				r.set("YFSB_text", medInfo.YFSB_text);
				r.set("YSBZ", this.openBy == "nurse" ? 0 : 1);
				// r.modified["YZMC"] = r.get("YZMC");
				if (m.adviceType == "DischargeMedication") {
					m.setMedQuantity(r);
				}
				store.add(r);
				m.grid.getSelectionModel().select(store.getCount() - 1, 2);
				if (sign) {
					m.ypyfSelect(ypyf, ypyf_r, -1, r);
				}
				m.checkAntibacterials(medInfo, r);
				// m.loop.next();
			},
			// 单停
			doSingleStop : function() {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doSingleStop();
				}
			},
			doAllStop : function() {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doAllStop();
				}
			},
			// 赋空
			doAssignedEmpty : function() {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doAssignedEmpty();
				}
			},
			doRemove : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doRemove(item, e);
				}
			},
			doRemoveGroup : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.doRemoveGroup(item, e);
				}
			},
			doSave : function(item, e, noMess) {
				var success = true;
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					this.panel.el.mask("正在保存数据...", "x-mask-loading")
					list.patinetData = {
						BRKS : this.form.form.getForm().findField("BRKS")
								.getValue(),
						BRBQ : (this.openBy == 'doctor' ? this.exContext.brxx
								.get("BRBQ") : this.mainApp['phis'].wardId),
						BRCH : this.form.form.getForm().findField("BRCH")
								.getValue()
					}
					success = list.doSave();
					if (success && !noMess) {
						MyMessageTip.msg("提示", "保存成功!", true);;
					}
					this.panel.el.unmask();
				}
				return success;
			},
			doRefresh : function() {
				if (this.beforeClose()) {
					var tab = this.tabModule.tab.getActiveTab();
					this.form.loadData();
					if (tab) {
						this.tabModule.moduleLoadData(tab.id);
					}
				}
			},
			needSave : function() {
				var curTab = this.tabModule.tab.getActiveTab();
				if (curTab && this.tabModule.midiModules[curTab.id]) {
					var m = this.tabModule.midiModules[curTab.id];
					var rs = m.store.getModifiedRecords();
					var exrs = m.exList.store.getModifiedRecords();
					if (m.removeRecords.length > 0) {
						return true;
					}
					for (var i = 0; i < rs.length; i++) {
						if (rs[i].get("YPXH")
								|| (rs[i].get("JFBZ") == 3 && rs[i].get("YZMC"))) {
							return true;
						}
					}
					for (var i = 0; i < exrs.length; i++) {
						if (exrs[i].get("YPXH")) {
							return true;
						}
					}
				}
				return false;
			},
			// 医嘱执行
			doConfirm : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					list.alterReview(this.panel);
				}
				// list.removeEmptyRecord();
				if (!this.needSave() || this.doSave(item, e, true)) {
					var module = this.createModule("wardAdviceExecuteModule",
							this.refWardAdviceExecuteModule);
					if (module) {
						module.initDataId = this.initDataId;
						if (this.confirmWin) {
							this.confirmWin.show();
							return;
						}
						module.on("doSave", this.doRefresh, this);
						module.opener = this;
						this.confirmWin = module.getWin();
						// this.confirmWin.add(module.initPanel());
						this.confirmWin.setHeight(600);
						this.confirmWin.setWidth(1024);
						// this.confirmWin.maximize();
						this.confirmWin.show();
					}
				}
			},
			// 医嘱提交
			doSubmit : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					if (!this.needSave() || this.doSave(item, e, true)) {
						if (this.openBy == "doctor") {
							list.docSubmit(this.panel);
						} else {
							list.alterReview(this.panel);
							var module = this.createModule(
									"wardAdviceSubmitModule",
									this.refWardAdviceSubmitModule);
							if (module) {
								module.initDataId = this.initDataId;
								if (this.submitWin) {
									this.submitWin.show();
									module.doRefresh();
									return;
								}
								module.on("doSave", this.doRefresh, this);
								module.opener = this;
								for (var i = 0; i < module.actions.length; i++) {
									var action = module.actions[i];
									if (action.id == "close") {
										action.properties.hide = false;
										break;
									}
								}
								this.submitWin = module.getWin();
								this.submitWin.add(module.initPanel());
								this.submitWin.setHeight(600);
								this.submitWin.setWidth(1024);
								this.submitWin.show();
								module.doRefresh();
							}
						}
					}
				}
			},
			// 复核
			doReview : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					if (!this.needSave() || this.doSave(item, e, true)) {
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						list.doReview(this.panel);
					}
				}
			},
			//单个复核
			doSingleReview : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					var cell = list.grid.getSelectionModel().getSelectedCell();
					if (!cell)
						return;
					if (!this.needSave() || this.doSave(item, e, true)) {
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						list.grid.getSelectionModel().select(cell[0], cell[1]);
						list.doSingleReview(this.panel);
					}
				}
			},
			// 取消复核
			doUnReview : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					var cell = list.grid.getSelectionModel().getSelectedCell();
					if (!cell)
						return;
					if (!this.needSave() || this.doSave(item, e, true)) {
						list.grid.getSelectionModel().select(cell[0], cell[1]);
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						if (!list.doUnReview(this.panel)) {
							this.panel.el.unmask();
						}
					}
				}
			},
			// 医生站 提交
			doDocSubmit : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					if (!this.needSave() || this.doSave(item, e, true)) {
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						list.docSubmit(this.panel);
					}
				}
			},
			// 病区 退回
			doGoback : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					var cell = list.grid.getSelectionModel().getSelectedCell();
					if (!cell)
						return;
					if (!this.needSave() || this.doSave(item, e, true)) {
						list.grid.getSelectionModel().select(cell[0], cell[1]);
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						if (!list.goback(this.panel)) {
							this.panel.el.unmask();
						}
					}
				}
			},
			doGobackGroup : function(item, e) {
				var tab = this.tabModule.tab.getActiveTab();
				if (tab) {
					var list = this.tabModule.midiModules[tab.id];
					var cell = list.grid.getSelectionModel().getSelectedCell();
					if (!cell)
						return;
					if (!this.needSave() || this.doSave(item, e, true)) {
						list.grid.getSelectionModel().select(cell[0], cell[1]);
						this.panel.el.mask("正在处理数据...", "x-mask-loading")
						if (!list.gobackGroup(this.panel)) {
							this.panel.el.unmask();
						}
					}
				}
			},
			// 查询
			doQuery : function() {
				var module = this.createModule("wardAdviceQueryModule",
						this.refWardAdviceQueryModule);
				if (module) {
					module.initDataId = this.initDataId;
					module.exContext = this.exContext;
					module.opener = this;
					if (this.adviceQueryWin) {
						this.adviceQueryWin.show();
						return;
					}
					this.adviceQueryWin = module.getWin();
					this.adviceQueryWin.add(module.initPanel());
					this.adviceQueryWin.setHeight(600);
					this.adviceQueryWin.setWidth(1024);
					this.adviceQueryWin.maximize();
					this.adviceQueryWin.show();
				}
			},
			doYjSubmit : function() {
				var curTab = this.tabModule.tab.getActiveTab();
				if (curTab && this.tabModule.midiModules[curTab.id]) {
					var m = this.tabModule.midiModules[curTab.id];
					var rs = m.store.getModifiedRecords();
					var exrs = m.exList.store.getModifiedRecords();
					if (m.removeRecords.length > 0) {
						MyMessageTip.msg("提示", "数据发生改变，先保存数据!", true);
						return;
					}
					for (var i = 0; i < rs.length; i++) {
						if (rs[i].get("YPXH")
								|| (rs[i].get("JFBZ") == 3 && rs[i].get("YZMC"))) {
							MyMessageTip.msg("提示", "数据发生改变，先保存数据!", true);
							return;
						}
					}
					for (var i = 0; i < exrs.length; i++) {
						if (exrs[i].get("YPXH")) {
							MyMessageTip.msg("提示", "数据发生改变，先保存数据!", true);
							return;
						}
					}
				}
				var module = this.createModule("wardProjectSubmitModule",
						this.refWardProjectSubmitModule);
				if (module) {
					module.initDataId = this.initDataId;
					module.exContext = this.exContext;
					module.opener = this;
					module.on("doSave", this.doRefresh, this);
					if (this.projectSubmitWin) {
						module.resetData();
						this.projectSubmitWin.show();
						return;
					}
					this.projectSubmitWin = module.getWin();
					this.projectSubmitWin.add(module.initPanel());
					this.projectSubmitWin.setHeight(450);
					this.projectSubmitWin.setWidth(800);
					this.projectSubmitWin.show();
				}
			},
			showYzjf : function(initDataId) {
				var module = this.createModule("wardDoctorAdviceJFList",
						this.refWardDoctorAdviceJFList);
				if (module) {
					module.initDataId = initDataId;
					if (this.adviceJFWin) {
						this.adviceJFWin.show();
						return;
					}
					this.adviceJFWin = module.getWin();
					this.adviceJFWin.add(module.initPanel());
					this.adviceJFWin.setHeight(400);
					this.adviceJFWin.setWidth(1024);
					this.adviceJFWin.show();
				}
			},
			doClose : function() {
				this.win.hide();
			},
			// 关闭事件
			onClose : function() {
				this.unlock();
			},
			unlock : function() {
				var p = {};
				p.YWXH = this.openBy == "nurse" ? "1006" : "1007";
				p.SDXH = this.exContext.brxx.get("ZYH") + "";
				p.BRID = this.exContext.brxx.get("BRID");
				p.BRXM = this.exContext.brxx.get("BRXM");
				this.bclUnlock(p, this.complexPanel.el)// 业务锁
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
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
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								return this.fireEvent("beforeWinShow");
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
			},
			beforeCylr : function(m) {
				if (confirm('当前医嘱数据已经修改，是否保存?')) {
					return this.doSave()
				} else {
					m.store.rejectChanges();
					m.removeRecords = [];
					m.exList.store.rejectChanges();
					m.exList.removeRecords = [];
				}
			},
			// 草药录入
			doCylr : function() {
				var tab = this.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				var list = this.tabModule.midiModules[tab.id];
				var rs = list.store.getModifiedRecords();
				var exrs = list.exList.store.getModifiedRecords();
				if (list.removeRecords.length > 0) {
					MyMessageTip.msg("提示", '当前医嘱数据已经修改,请先保存再录入草药方', true);
					return;
					// if (confirm('当前医嘱数据已经修改，是否保存?')) {
					// return this.doSave()
					// } else {
					// list.store.rejectChanges();
					// list.removeRecords = [];
					// list.exList.store.rejectChanges();
					// list.exList.removeRecords = [];
					// this.doNewGroup()
					// }
				}
				for (var i = 0; i < rs.length; i++) {
					if (rs[i].get("YPXH")
							|| (rs[i].get("JFBZ") == 3 && rs[i].get("YZMC"))) {
						MyMessageTip.msg("提示", '当前医嘱数据已经修改,请先保存再录入草药方', true);
						return;
						// if (confirm('当前医嘱数据已经修改，是否保存?')) {
						// return this.doSave()
						// } else {
						// list.store.rejectChanges();
						// list.removeRecords = [];
						// list.exList.store.rejectChanges();
						// list.exList.removeRecords = [];
						// this.doNewGroup()
						// }
					}
				}
				for (var i = 0; i < exrs.length; i++) {
					if (exrs[i].get("YPXH")) {
						MyMessageTip.msg("提示", '当前医嘱数据已经修改,请先保存再录入草药方', true);
						return;
						// if (confirm('当前医嘱数据已经修改，是否保存?')) {
						// return this.doSave()
						// } else {
						// list.store.rejectChanges();
						// list.removeRecords = [];
						// list.exList.store.rejectChanges();
						// list.exList.removeRecords = [];
						// this.doNewGroup()
						// }
					}
				}
				var r = list.getSelectedRecord();
				var herbList = [];
				if (r.data.JLXH) {
					for (var i = 0; i < list.store.getCount(); i++) {
						if (list.store.getAt(i).data["YZZH"] == r.data.YZZH) {
							herbList.push(list.store.getAt(i).data)
						}
					}
				}
				var module = this.createModule("wardHerbEnrty",
						this.refWardHerbEnrty);
				if (module) {
					module.initDataId = this.initDataId;
					module.exContext = this.exContext;
					module.opener = this;
					module.openBy = this.openBy;
					module.data = r.data;
					module.YFSZ = this.YFSZ;
					// if (this.adviceQueryWin) {
					// this.adviceQueryWin.show();
					// return;
					// }
					this.herbEnrtyWin = module.getWin();
					this.herbEnrtyWin.add(module.initPanel());
					this.herbEnrtyWin.setHeight(400);
					this.herbEnrtyWin.setWidth(600);
					// this.herbEnrtyWin.maximize();
					// if(herbList.length){
					module.doUpdate(herbList);
					// }
					this.herbEnrtyWin.show();
					module.doModuleNew();

				}
			}
		});