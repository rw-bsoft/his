$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRmodeChooseModule = function(cfg) {
	// cfg.exContext = {};
	phis.application.emr.script.EMRmodeChooseModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.emr.script.EMRmodeChooseModule, phis.script.SimpleModule,
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
										items : this.node.BLLX == 1 ? this
												.getFixPanel() : this
												.getBlmbAndGrmbList()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				this.list.grid.getColumnModel().setHidden(1, false);
				this.list.grid.getColumnModel().setHidden(2, true);
			},
			getFixPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							items : [{
										layout : "fit",
										region : 'south',
										// height : 200,
										height : 78,
										items : this.getBllbForm()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getBlmbAndGrmbList()
									}]
						});
				return panel;
			},
			getBllbTree : function() {
				var module = this.createModule("refBllbTree_"
								+ (this.node.BLBH || this.node.key),
						this.refBllbTree);
				module.node = this.node;
				module.on("treeClick", this.onTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				return tree;
			},
			getBllbForm : function() {
				var module = this.createModule("refBlmbForm_"
								+ (this.node.BLBH || this.node.key),
						this.refBlmbForm);
				module.on("changeYl", this.changeYl, this);
				this.form = module;
				return module.initPanel();
			},
			getBlmbAndGrmbList : function() {
				var module = this.createModule("refBlmbList_"
								+ (this.node.BLBH || this.node.key),
						this.refBlmbList);
				this.list = module;
				module.opener = this;
				module.openBy = this.openBy;
				module.exContext = this.exContext;
				module.node = this.node;
				module.on("changeYl", this.changeYl, this);
				var _ctx = this;
				module.onDblClick = function() {
					_ctx.doSave();
				}
				var list = module.initPanel();
				return list;
			},
			changeYl : function() {
				var r = this.list.getSelectedRecord();
				if (!r || this.node.BLLX != 1)
					return;
				var form = this.form.form.getForm();
				var XSMC = r.get("XSMC");
				var data = r.data;
				var title = '';
				if (XSMC == "模板名称") {
					title = data.MBMC;
				} else if (XSMC == "科室名称") {
					title = "全科";
				} else {
					var xsmc = XSMC.split("+");
					for (var i = 0; i < xsmc.length; i++) {
						if (xsmc[i].indexOf("记录日期") >= 0) {
							var cgFmt = "";
							var fmt = xsmc[i].substring(xsmc[i].indexOf('{')
											+ 1, xsmc[i].indexOf('}'))
							if (fmt == 'yyyy年mm月dd日 hh:mm') {
								cgFmt = 'Y年m月d日 H:i'
							} else {
								cgFmt = 'Y-m-d H:i'
							}
							var jlrq = form.findField("JLSJ").getValue();
							var d = Date.parseDate(jlrq, 'Y-m-d H:i:s')
							title += d.format(cgFmt);
						} else if (xsmc[i].indexOf("类别名称") >= 0) {
							title += data.MBMC;
						} else if (xsmc[i].indexOf("医生姓名") >= 0) {
							title += form.findField("YSDM").getRawValue();
						} else if (xsmc[i].indexOf("换行符") >= 0) {
							title += '\r\n';
						} else {
							title += xsmc[i].replace(/\'/g, "");
						}
					}
				}
				form.findField("YL").setValue(title);
				this.blTitle = title;
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
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadTemplateData",
							body : {
								"step" : 3,
								"MBLB" : data.mblb,// 1 病历模版 2 个人模版
								"CHTCODE" : (data.mblb == 2
										? data.PTID
										: data.MBBH),
								"BLLX" : this.node.BLLX
							}
						});
				if (resData.code > 200) {
					Ext.Msg.alert("警告", resData.msg);
					return;
				}
				this.opener.emr.FunActiveXInterface("BsPreviewAsHtml",
						(this.node.BLLX == 1 ? '2' : '1'+'#'+data.MBMC),
						resData.json.uft8Text);
				var url = this.opener.emr.StrReturnData;
				if (url) {
					window.open(url);
				}
			},
			onTreeClick : function(node) {
				if (node.attributes.isRoot) {
					this.list.requestData.BLLB = node.id;
					this.list.requestData.MBLB = 0;
				} else {
					this.list.requestData.MBLB = node.id;
				}
				// this.list.requestData.initCnd =
				// ['eq',['$','a.BLLB'],['d',nodeId]];
				this.list.doRefresh();
			},
			doSave : function() {
				var r = this.list.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要调入的模版记录 !", true);
					return;
				}
				if (!this.checkEmrPermission('CKQX', r.get("MBLB"))) {
					MyMessageTip.msg("提示", "对不起，您没有查看该病历/病程的权限 !", true);
					return;
				}
				if (!this.checkEmrPermission('SXQX', r.get("MBLB"))) {
					MyMessageTip.msg("提示", "对不起，您没有书写该病历/病程的权限 !", true);
					return;
				}
				if (this.node.BLLX == 1) {
					r.data.YSXM = this.form.form.getForm().findField("YSDM")
							.getRawValue();
					r.data.JLRQ = Date.parseDate(this.form.form.getForm()
									.findField("JLSJ").getValue(),
							'Y-m-d H:i:s');
					r.data.title = this.blTitle;
				} else {
					r.data.YSXM = this.mainApp.uname;
					r.data.JLRQ = Date.getServerDateTime();
				}
				if (this.ifNew) {
					delete this.opener.BLBH;
				}
				r.data.mblb = this.list.sslb || 1;
				this.opener.blchose="1";
				this.win.close();
				this.opener.blchose="0";
				this.fireEvent("loadTemplate", r.data);
			},
			/**
			 * SXQX 代表书写权限 CKQX 代表查看权限 SYQX 代表审阅权限 DYQX 代表打印权限 特殊
			 * 参数为JSJB时，返回当前医生的角色级别
			 * 
			 * @param {}
			 *            op
			 * @return {Boolean}
			 */
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
			doClose : function() {
				this.win.close();
			},
			getWin : function() {
//				var win = this.win;
				var closeAction = "close";
//				if (!win) {
				var	win = new Ext.Window({
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
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
//				}
				return win;
			}
		});
