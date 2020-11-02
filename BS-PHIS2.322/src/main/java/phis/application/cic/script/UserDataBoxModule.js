$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.UserDataBoxModule = function(cfg) {
	phis.application.cic.script.UserDataBoxModule.superclass.constructor.apply(
			this, [cfg]);
	this.indexId = 11;
	this.on("winShow", this.onWinShow, this);
}
var emr_this = emr_this || {};
Ext.extend(phis.application.cic.script.UserDataBoxModule,
		phis.script.SimpleModule, {
			initPanel : function(type) {
				if (this.panel) {
					return this.panel;
				}
				var list1 = this.getDataList();
				var list2 = this.getDataList2();
				var tab = this.getTemporaryTab();
				var dic = this.getDicModule();
				var jylis = this.getJYList();//检验列表
				var panel = new Ext.Panel({
//							border : false,
							hideBorders : true,
							width : 1024,
							height : this.height,
//							frame : true,
//							split : true,
							layout : 'border',
//							defaults : {
//								border : false
//							},
							items : [{
										id : "view",
										layout : "fit",
//										border : false,
//										split : true,
										region : 'west',
										width : '20%',
										collapsible : true,
										titleCollapse : true,
										items : this.getViewPanel()
									}, {
										xtype : 'panel',
//										border : false,
//										split : true,
//										frame : true,
										layout : 'border',
										region : 'center',
										items : [{
													id : "changePanel",
													layout : "card",
//													border : false,
													activeItem : 0,
													collapsible : true,
													titleCollapse : true,
													collapseFirst : false,
													floatable : false,
													animCollapse : false,
//													split : true,
//													frame : true,
													width : '100%',
													region : 'west',
													items : [list1, list2, tab,
															dic,jylis]
												}, {
													id : "emrPan",
													layout : "fit",
//													border : false,
//													split : true,
//													frame : true,
													hidden : true,
													// width : 450,
													region : 'center',
													items : []
												}]
									}]
						});
				this.panel = panel;
				return panel;
			},
			onWinShow : function() {
				Ext.getCmp("changePanel").expand();
			},
			getViewPanel : function() {
				var module = this.createModule("viewPanel", this.viewPanel);
				this.vPanel = module;
				module.on("viewClick", this.onViewPanelClick, this);
				var pan = module.initPanel();
				return pan;
			},
			getDataList : function() {
				var module = this.createModule("dataList", this.dataList);
				this.dList = module;
				var BRID = this.exContext.ids.brid;
				if (BRID) {
					var cnd1 = ['ne', ['$', 'BLZT'], ['s', '9']];
					var cnd2 = ['eq', ['$', 'a.BRID'], ['i', BRID]];
					var cnd3 = ['ne', ['$', 'DLLB'], ['s', '-1']];
					var cnd4 = ['ne', ['$', 'BLLB'], ['s', '2000001']];
					module.requestData.cnd = ['and', cnd1, cnd2, cnd3, cnd4];
				} else {
					var cnd1 = ['ne', ['$', 'BLZT'], ['s', '9']];
					var cnd2 = ['ne', ['$', 'DLLB'], ['s', '-1']];
					var cnd3 = ['ne', ['$', 'BLLB'], ['s', '2000001']];
					module.requestData.cnd = ['and', cnd1, cnd2, cnd3];
				}
				module.exContext = this.exContext
				module.on("cancel", this.onCancel, this);
				module.on("appoint", this.onAppointData, this);
				var gird = module.initPanel();
				this.grid = gird;
				return gird;
			},
			getDataList2 : function() {
				var module = this.createModule("dataList2", this.dataList2);
				this.dList2 = module;
				var ZYH = this.exContext.ids.clinicId;
				module.ZYH = ZYH;
				module.on("appoint", this.onAppointData, this);
				module.on("cancel", this.onCancel, this);
				var gird = module.initPanel();
				this.grid2 = gird;
				return gird;
			}
			,getJYList : function() {
				var module = this.createModule("jylist", this.jytestresult);
				this.jylist = module;
				var ZYH = this.exContext.ids.clinicId;
				module.ZYH = ZYH;
				module.ZYHM = this.exContext.brxx.data.ZYHM;
				module.doRefresh();
				module.on("appoint", this.onAppointData, this);
				module.on("cancel", this.onCancel, this);
				var gird = module.initPanel();
				this.jygrid = gird;
				return gird;
			}
			,getTemporaryTab : function() {
				var module = this.createModule("temporaryTab",
						this.temporaryTab);
				this.tabPanel = module;
				module.on("cancel", this.onCancel, this);
				var tab = module.initPanel();
				module.on("appoint", this.onAppointData, this);
				this.tab = tab;
				return tab;
			},
			getDicModule : function() {
				var module = this.createModule("dicNormal", this.dicNormal);
				this.dicMod = module;
				module.on("cancel", this.onCancel, this);
				module.on("appoint", this.onAppointData, this);
				var dicPan = module.initPanel();
				return dicPan;
			},
			onAppointData : function(value, type) {
				this.resultValue = value;
				if (type != 3) {
					this.fireEvent("appoint", type, value);
					// type=1:特殊符号；type=2：字符串；type=3：患者病史的BLBH
					this.onCancel();
				} else {
					this.setEMRIntoBox(value);
				}
			},
			setEMRIntoBox : function(data) {
				var node = {
					key : data.BLLB,
					BLBH : data.BLBH,
					text : "患者病史-" + data.BLMC,
					BLLX : data.BLLX,
					BLLB : data.BLLB,
					MBLB : data.MBLB,
					ReadOnly : true
				};
				Ext.getCmp("emrPan").removeAll(true);
				var emrPanel = this.openEmrEditorModule(node);
				Ext.getCmp("emrPan").add(emrPanel);
				Ext.getCmp("changePanel").collapse();
				Ext.getCmp("emrPan").show();
			},

			openEmrEditorModule : function(node, recoveredBl01) {
				// if (!this.checkEmrPermission("CKQX", node.MBLB)) {
				// MyMessageTip.msg("提示", "对不起，您没有查看该病历/病程的权限!", true);
				// return;
				// }
				// 是否单文档
				var nodeId = node.BLBH || node.key;
				if (node.BLLX == 1) {// 病程
					nodeId = node.key;
				}
				var key = "phis.application.war.WAR/WAR/WAR36";
				var moduleCfg = null;;
				var res = this.mainApp.taskManager.loadModuleCfg(key);
				if (!res.code) {
					moduleCfg = res;
				} else if (res.code != 200) {
					Ext.MessageBox.alert("错误", res.msg)
					return
				}
				if (!moduleCfg) {
					moduleCfg = res.json.body;
				}
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(moduleCfg, moduleCfg.properties);
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var m = eval("new " + cls + "(cfg)");
				m.exContext = this.exContext
				m.mKey = key + nodeId + "box";
				m.opener = this;
				m.node = node;
				m.actions = [];
				m.needBlht = true;
				m.setMainApp(this.mainApp);
				var p = m.initPanel();
				p.closable = true;
				if (node.BLLX == 1) {
					p.title = node.LBMC || node.text;
				} else {
					p.title = node.text;
				}
				p.mKey = key + nodeId + "box";
				p.key = key + nodeId + "box";
				//m.loadEmrHT(nodeId);
				this.emrPanel = p;
				return p;
			},
			checkEmrPermission : function(op, bllb) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "checkPermission",
							body : {
								BLLB : bllb
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return false;
				}
				return result.json.emrPermissions[op]
			},
			onCancel : function() {
				if (this.win) {
					this.win.close();
				}
			},
			/**
			 * 获取选中的值
			 * 
			 * @return {}
			 */
			getResultValue : function() {
				return this.resultValue;
			},
			onViewPanelClick : function(indexId) {
				if (this.indexId == indexId) {
					return;
				}
				this.indexId = indexId;
				Ext.getCmp("changePanel").expand();
				if (indexId.toString().substring(0, 1) == "1" && indexId != 11) {
					this.dList2.indexId = indexId;
					Ext.getCmp('changePanel').layout.setActiveItem(1);
					var initCnd = this.dList2.getCnd();
					var queryCnd = this.getQueryCnd(indexId);
					this.dList2.requestData.cnd = initCnd;
					this.dList2.requestData.queryCnd = queryCnd;
					this.dList2.loadData();
					this.dList2.doTimeNew();
				} else if (indexId == 11) {
					Ext.getCmp('changePanel').layout.setActiveItem(0);
				} else if (indexId == 21) {
					Ext.getCmp('changePanel').layout.setActiveItem(2);
				} else if (indexId == 22) {
					Ext.getCmp('changePanel').layout.setActiveItem(3);
				}else if (indexId == 23) {
					Ext.getCmp('changePanel').layout.setActiveItem(4);
				}
			},
			getQueryCnd : function(indexId) {
				var cnd = null;
				if (indexId == 12) {
					cnd = ["eq", ["$", "LSYZ"], ["s", "1"]];
				} else if (indexId == 13) {
					cnd = ["eq", ["$", "LSYZ"], ["s", "0"]];
				}
				return cnd;
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : 1024,
								height : 600,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : true,
								closeAction : closeAction,
								closable : true,
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