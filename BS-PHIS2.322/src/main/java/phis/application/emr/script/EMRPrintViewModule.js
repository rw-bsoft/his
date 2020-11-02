$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRPrintViewModule = function(cfg) {
	phis.application.emr.script.EMRPrintViewModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("beforeclose", this.beforeMyClose, this);
}

Ext.extend(phis.application.emr.script.EMRPrintViewModule, phis.script.SimpleModule,
		{
			initPanel : function() {
				// 判断是否有权限
				if (this.panel)
					return this.panel;
				var url = ClassLoader.appRootOffsetPath
						+ (Ext.isIE ? 'emrocx_ie' : 'emrocx_ff');
				var panel = new Ext.Panel({
					// id : this.mKey,
					frame : true,
					border : false,
					html : '<IFRAME id=printView name="emrOcxFrame" width="100%" height="100%" src="'
							+ url
							+ '.html" frameborder=0  scrolling=no  ></iframe>',
					// autoScroll : true,
					tbar : this.createMyButtons()//zhaojian 2017-10-13 解决出院病人电子病历打印按钮灰色问题 createButtons改为createMyButtons
				});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;
			},
			beforeMyClose : function() {
				if (this.printType == 'AddPrintRecord') {
					this.emr.FunActiveXInterface("BsSetContinuePrint", '-1',
							this.paraA + '#7')
				} else {
					this.emr.FunActiveXInterface("BsSetContinuePrint", '-1',
							(this.paraA - 1) + '#' + (this.paraB - 1))
				}
			},
			onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE ? document.frames['printView'] : document
						.getElementById('printView');
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() {
								_ctx.initEmrActiveX(iframe);
							});
				} else {
					iframe.onload = function() {
						_ctx.initEmrActiveX(iframe);
					};
				}
			},
			initEmrActiveX : function(iframe) {
				this.emr = Ext.isIE
						? iframe.document.getElementById("emrOcx")
						: iframe.contentWindow.document
								.getElementById("emrOcx");
				if (!this.emr) {
					this.alert("提示", "检查到您尚未安装电子病历插件，请安装后重新尝试!")
					return;
				}
				this.onWinShow();
			},
			onWinShow : function() {
				if (!this.emr)
					return;
				this.emr.height = this.win.getHeight() - 70;
				this.emr.width = this.panel.getWidth() - 20;
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadTemplateData",
							body : {
								"step" : 1
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					this.emrLoading = false;
					return false;
				}
				if (!Ext.isIE) {
					var d = new Ext.util.DelayedTask(function() {
								if (!this.firstLoad) {
									this.firstLoad = true;
									this.emr.FunActiveXInterface(
											"BsNewDocument",
											resData.json.docText, '');
								} else {
									this.emr.FunActiveXInterface("BsEmptyDoc",
											'', '');
								}
								this.loadEmrPrint(this.cachePrtSetup);
							}, this);
					d.delay(200);
					return;
				}
				if (!this.firstLoad) {
					this.firstLoad = true;
					this.emr.FunActiveXInterface("BsNewDocument",
							resData.json.docText, '');
				} else {
					this.emr.FunActiveXInterface("BsEmptyDoc", '', '');
				}
				this.loadEmrPrint(this.cachePrtSetup);
			},
			loadEmrPrint : function(body) {
				if (body.DYTS) {
					this.parent.alert("提示", body.DYTS);
				}
				if (body.prtSetup) {
					this.emr.FunActiveXInterface("BsLoadPrtsetup",
							body.prtSetup, '');// 加载打印参数
				}
				// 获取父页面病历信息
				this.parent.emr.FunActiveXInterface("BsGetCurrentDoument", '',
						'');
				this.emr.FunActiveXInterface("BsLoadCaseDocData",
						this.parent.emr.WordData, '')
				this.emr.FunActiveXInterface("BsLoadCaseDocXml",
						this.parent.emr.WordXML, '')
				if (this.parent.node.BLLX == 1) {
					this.emr.FunActiveXInterface("BsSetDocFirstPageNum",
							(this.parent.printFirstPage || '0'), '')
					this.emr.FunActiveXInterface("BsSendMyHeaderFooter",
							this.parent.ymysText, '')
				}
				this.emr.FunActiveXInterface("BsClearBackColor", '', '')

				if (this.printType == 'printAll') {

				} else if (this.printType == 'AddPrintRecord') {
					this.emr.FunActiveXInterface("BsSetContinuePrint", '1',
							this.paraA + '#7')
				} else {
					this.emr.FunActiveXInterface("BsSetContinuePrint", '1',
							(this.paraA - 1) + '#' + (this.paraB - 1))
					this.emr.FunActiveXInterface('BsCurrentPos', '1',
							this.paraA + "");
				}
				if (body.WGXBZ > 0) {
					this.emr.FunActiveXInterface("BsGridLinePrint", body.WGXBZ
									|| '0', '10')
				}
				this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', '1');
				this.emr.FunActiveXInterface('BsSetDocView', 'PrintView', '1');
			},
			getHtml : function() {
				if (Ext.isIE) {
					return "<OBJECT id='printView name='printView' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'  align=center hspace=0 vspace=0 ></OBJECT>"
				} else {
					return '<object	id="printView" TYPE="application/x-itst-activex" event_onclick="doTest" WIDTH="1024" HEIGHT="450" clsid="{FFAA1970-287B-4359-93B5-644F6C8190BB}"></OBJECT>';
				}
			},
			doPrint : function() {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "savePrintLog",
							body : {
								RZNR : this.parent.bl01.BLMC,
								YWID1 : this.exContext.ids.clinicId,
								YWID2 : this.printBlbh,
								YEID3 : this.printBlbh
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					return;
				}
				this.emr.FunActiveXInterface("BsClearBackColor", '', '')
				var mrdyj = this.cachePrtSetup.MRDYJ
				if (mrdyj) {
					var printName = this.emr.FunActiveXInterface(
							"BsGetDefaultPrinter", '', '');
					var r = this.emr.FunActiveXInterface("BsSetDefaultPrinter",
							mrdyj, '');
					if (r > 0) {
						this.parent.alert("提示", this.emr.StrReturnData);
					}
					this.emr
							.FunActiveXInterface('BsPrintDocument', '1', '0#0#');
				} else {
					this.emr
							.FunActiveXInterface('BsPrintDocument', '1', '0#0#');
				}
			},
			doClose : function() {
				this.win.hide();
			},
			//zhaojian 2017-10-13 解决出院病人电子病历打印按钮灰色问题
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
					var btn = {
						accessKey : f1 + i,
						text : action.name + "(F" + (i + 1) + ")",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								resizable : false,
								maximizable : false,
								maximized : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("beforehide", function() {
								this.fireEvent("beforeclose", this)
							}, this)
					this.win = win
				}
				return win;
			}
		});