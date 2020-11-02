$package("phis.application.war.script")

$import("phis.script.SimpleModule")
phis.application.war.script.EMRmodeChooseModule = function(cfg) {
	cfg.exContext = {};
	phis.application.war.script.EMRmodeChooseModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.war.script.EMRmodeChooseModule, phis.script.SimpleModule,
		{
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
										items : this.getActiveXPanel()
									}]
						});
				this.panel = panel;
				return panel;
			},

			getBllbTree : function() {
				var module = this.createModule("reflbTree_" + this.BLLB,
						this.reflbTree);
				module.BLLB = this.BLLB;
				module.on("treeClick", this.onTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				return tree;
			},
			getBlmbAndGrmbList : function() {
				var module = this.createModule("refmbList_" + this.BLLB,
						this.refmbList);
				this.list = module;
				module.on("listDblClick", this.doSave, this);
				var grid = module.initPanel();
				this.grid = grid;
				return grid;
			},
			getActiveXPanel : function() {
				var ocxStr = ""
				if (Ext.isIE) {
					ocxStr = "<div style='display:none'><OBJECT id='emrOcx_hide' name='emrOcx_hide' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
				} else {
					ocxStr = "<div><OBJECT id='emrOcx_hide' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
				}
				var panel = new Ext.Panel({
							frame : true,
							border : false,
							html : ocxStr
						});
				return panel;
			},
			onTreeClick : function(node) {
				if (node.getDepth() == 0) {
					this.list.requestData.BLLB = 0;
					this.list.requestData.MBLB = 0;
				} else if (node.id == this.BLLB) {
					this.list.requestData.BLLB = 1;
					this.list.requestData.MBLB = node.id;
				} else {
					this.list.requestData.BLLB = 0;
					this.list.requestData.MBLB = node.id;
				}
				this.list.doRefresh();
			},
			doSave : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要调入的模版记录 !", true);
					return;
				}
				this.fireEvent("loadTemplate", r);
				this.doClose();
			},
			doClose : function() {
				this.win.close();
			},
			doLoadEMR : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要预览的模版记录 !", true);
					return;
				}
				r.data.mblb = this.list.sslb || 1;
				// 根据mbbh获取模版内容
				var data = r.data;
				var BLLX;
				if (data.BLLB == "294") {
					BLLX = 1;
				} else {
					BLLX = 0;
				}
				var emr = document.getElementById("emrOcx_hide");
				if (emr) {
					emr.FunActiveXInterface('BsNewDocument', '', '');
				}
				resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadTemplateData",
							body : {
								"step" : 3,
								"MBLB" : data.mblb,// 1 病历模版 2 个人模版
								"CHTCODE" : (data.mblb == 2
										? data.PTID
										: data.MBBH),
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
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
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