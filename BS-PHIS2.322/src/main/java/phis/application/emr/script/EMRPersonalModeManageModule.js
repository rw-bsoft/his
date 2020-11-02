$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRPersonalModeManageModule = function(cfg) {
	cfg.exContext = {};
	phis.application.emr.script.EMRPersonalModeManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRPersonalModeManageModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										// height : 200,
										width : 200,
										items : this.getBllbTree()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getBlmbAndGrmbList()
									}, {
										layout : "fit",
										region : 'east',
										width : 0,
										items : this.getActiveXPanel()
									}]
						});
				this.panel = panel;
				this.panel.on("beforeclose", this.onBeforeclose, this);
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				// this.list.grid.getColumnModel().setHidden(2, false);
				// this.list.grid.getColumnModel().setHidden(1, true);
			},
			getBllbTree : function() {
				var module = this.createModule("refBllbTree", this.refBllbTree);
				module.node = this.node;
				module.on("treeClick", this.onBeforeTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				this.treePanel = tree;
				return tree;
			},
			getBlmbAndGrmbList : function() {
				var module = this.createModule("refBlmbList", this.refBlmbList);
				module.on("listRowClick", this.onListRowClick, this);
				module.on("afterCellEdit", this.onAfterCellEdit, this);
				this.list = module;
				module.opener = this;
				var list = module.initPanel();
				this.grid = list;
				return list;
			},
			getActiveXPanel : function() {
				var ocxStr = ""
				if (Ext.isIE) {
					ocxStr = "<div style='display:none'><OBJECT id='emrOcx_Personal' name='emrOcx_Personal' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
				} else {
					ocxStr = "<div><OBJECT id='emrOcx_Personal' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
				}
				var panel = new Ext.Panel({
							frame : true,
							border : false,
							html : ocxStr
						});
				return panel;
			},
			onAfterCellEdit : function(item, record, field, v) {
				this.list.setHasChange(record);
			},
			onListRowClick : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					return;
				}
				var btns = this.panel.getTopToolbar();
				var PTTYPE = r.get("PTTYPE");
				if (PTTYPE == 3) {
					btns.items.items[3].disable();
					btns.items.items[4].disable();
				} else {
					btns.items.items[3].enable();
					btns.items.items[4].enable();
				}
				var PTSTATE = r.get("PTSTATE");
				if (PTSTATE == "0") {
					btns.items.items[0].enable();
					btns.items.items[1].disable();
				} else {
					btns.items.items[0].disable();
					btns.items.items[1].enable();
				}
			},
			onBeforeTreeClick : function(node) {
				var store = this.list.getListStore();
				var hasChange = false;
				store.each(function(r) {
							if (r.get("hasChange")) {
								hasChange = true;
							}
						});
				if (hasChange) {
					Ext.Msg.show({
								title : '提示',
								msg : '当前页面已被修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									hasChange = false;
									if (btn == "ok") {
										this.doSave();
										this.onTreeClick(node);
									} else {
										this.onTreeClick(node);
									}
								},
								scope : this
							})
				} else {
					this.onTreeClick(node);
				}
			},
			onTreeClick : function(node) {
				this.list.requestData.MBLB = node.id;
				var fieldName = 'a.TEMPLATETYPE';
				if (node.getDepth() == 1) {
					fieldName = 'a.FRAMEWORKCODE'
				}
				this.list.requestData.cnd = ['eq', ['$', fieldName],
						['s', node.id]];
				this.list.initCnd = ['eq', ['$', fieldName], ['s', node.id]];
				if (node.getDepth() == 0) {
					this.list.requestData.cnd = null;
					this.list.initCnd = null;
				}
				this.list.doRefresh();
			},
			doCancellation : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					return;
				}
				Ext.Msg.show({
							title : "注销",
							msg : "是否注销模版[" + r.get("PTNAME") + "]！",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var ret = phis.script.rmi.miniJsonRequestSync({
												serviceId : "emrManageService",
												serviceAction : "saveCancellationMode",
												pkey : r.id
											});
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doCancellation);
									} else {
										this.list.doRefresh();
									}
								}
							},
							scope : this
						})
			},
			doRenew : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					return;
				}
				Ext.Msg.show({
							title : "恢复",
							msg : "是否恢复模版[" + r.get("PTNAME") + "]！",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var ret = phis.script.rmi.miniJsonRequestSync({
												serviceId : "emrManageService",
												serviceAction : "saveRenewMode",
												pkey : r.id
											});
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doRenew);
									} else {
										this.list.doRefresh();
									}
								}
							},
							scope : this
						})
			},
			doModify : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					return;
				}
				var module = this.createModule("refBlmbModifyModule",
						this.refBlmbModifyModule);
				module.selectRecord = r.data;
				var win = module.getWin();
				win.setWidth(800);
				win.setHeight(600);
				win.show();
			},
			doReview : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要预览的模版记录 !", true);
					return;
				}
				var data = r.data;
				var BLLX;
				if (data.PTTYPE == "1") {
					BLLX = 1;
				} else {
					BLLX = 0;
				}
				var emr = document.getElementById("emrOcx_Personal");
				if (emr) {
					emr.FunActiveXInterface('BsNewDocument', '', '');
				}
				resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadTemplateData",
							body : {
								"step" : 3,
								"MBLB" : 2,// 1 病历模版 2 个人模版
								"CHTCODE" : data.PTID,
								"BLLX" : BLLX
							}
						});
				if (resData.code > 200) {
					Ext.Msg.alert("警告", resData.msg);
					return;
				}
				var s = emr.FunActiveXInterface("BsPreviewAsHtml", (BLLX == 1
								? '2'
								: '1'), resData.json.uft8Text);
				var url = this.emr.StrReturnData;
				if (url) {
					window.open(url);
				}
			},
			doCancel : function() {
				this.onBeforeclose();
			},
			onBeforeclose : function() {
				var store = this.list.getListStore();
				var hasChange = false;
				store.each(function(r) {
							if (r.get("hasChange")) {
								hasChange = true;
							}
						});
				if (hasChange) {
					Ext.Msg.show({
								title : '提示',
								msg : '当前页面已被修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									hasChange = false;
									if (btn == "ok") {
										this.doSave();
										this.panel.destroy();
									} else {
										this.panel.destroy();
									}
								},
								scope : this
							})
				} else {
					this.panel.destroy();
				}
				return false;
			},
			doSave : function() {
				var store = this.list.getListStore();
				var records = [];
				store.each(function(r) {
							if (r.get("hasChange")) {
								var data = {
									PTID : r.get("PTID"),
									PTNAME : r.get("PTNAME")
								}
								records.push(data);
							}
						});
				if (records.length == 0) {
					return;
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "saveChangeModeRecords",
							records : records
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
				} else {
					this.list.doRefresh();
				}
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
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
