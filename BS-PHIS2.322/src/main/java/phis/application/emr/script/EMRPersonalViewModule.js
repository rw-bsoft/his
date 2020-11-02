$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRPersonalViewModule = function(cfg) {
	phis.application.emr.script.EMRPersonalViewModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(phis.application.emr.script.EMRPersonalViewModule,
		phis.script.SimpleModule, {
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
					html : '<IFRAME id=personalView name=personalView width="100%" height="100%" src="'
							+ url
							+ '.html" frameborder=0  scrolling=no  ></iframe>',
					// autoScroll : true,
					tbar : this.createButtons()
				});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames['personalView']
						: document.getElementById('personalView');
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
								this.doLoadTemplate();
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
				this.doLoadTemplate();
			},
			doLoadTemplate : function() {
				var data = this.selectRecord;
				this.emrLoading = true;
				// 前台获取病历编号
				// this.bl01.MBLB = data.MBLB;
				// this.bl01.MBBH = data.MBBH;
				// 是否病程，是否需要载入页眉页脚
				if (data.PTTYPE == 1) {
					if (this.emr
							.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
						// 载入页面页脚
						// alert(Ext.encode(this.node))
						// alert(Ext.encode(this.exContext.empiData))
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadTemplateData",
									body : {
										"step" : 3,
										"BLLB" : data.FRAMEWORKCODE,
										"MBLB" : '1',// 1 病历模版 2 个人模版
										"KSDM" : -1
									}
								});
						if (resData.code > 200) {
							this.alert("警告", resData.msg);
							// this.emrLoading = false;
							// this.emr.FunActiveXInterface("BsEmptyDoc", '',
							// '');
							this.win.hide();
							return;
						}
						// 载入页面页脚
						this.emr.FunActiveXInterface(
								"BsIllrecLoadHdrftrTemplate",
								resData.json.gbkText, resData.json.uft8Text)
						// 载入页眉元素
						this.ymysText = resData.json.ymysText;
					}
				}

				// 调用设置空段落建
				this.emr.FunActiveXInterface("BsIllrecNewPara", 'Illrc_1_temp',
						' ');
				// 载入模版数据
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadTemplateData",
							body : {
								"step" : 3,
								"MBLB" : 2,// 1 病历模版 2 个人模版
								"CHTCODE" : data.PTID,
								"BLLX" : data.PTTYPE
							}
						});
				if (resData.code > 200) {
					this.alert("警告", resData.msg);
					return;
				}
				if (data.PTTYPE == 1) {
					this.emr.FunActiveXInterface("BsIllrecAddTemplate", "000#"
									+ resData.json.gbkText,
							resData.json.uft8Text);
				} else {
					this.emr.FunActiveXInterface("BsLoadTemplateDate",
							resData.json.gbkText, resData.json.uft8Text);
				}
				// 页眉元素加载
				if (data.PTTYPE == 1) {
					this.emr.FunActiveXInterface("BsIllRecDataEnd", '', '');
				}
				resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadEmrEditorData",
							body : {
								BLLX : data.PTTYPE,
								CHTCODE : data.TEMPLATECODE
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					return null;;
				}
				if (data.PTTYPE == 1) {
					this.emr.FunActiveXInterface('BsLoadIllRecXml', '000#'
									+ resData.json.gbkText, '');
				} else {
					this.emr.FunActiveXInterface('BsLoadCaseDocXml',
							resData.json.gbkText, '');
				}
				if (data.PTTYPE == 1) {
					this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', 0);
					this.emr.FunActiveXInterface('BsSetParaReadOnly',
							'element..Illrc_1_temp', 0);
				} else {
					this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', 0);
				}
				// 清除痕迹
				this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
				this.modifySign = 0;
			},
			getHtml : function() {
				if (Ext.isIE) {
					return "<OBJECT id='personalView name='personalView' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'  align=center hspace=0 vspace=0 ></OBJECT>"
				} else {
					return '<object	id="personalView" TYPE="application/x-itst-activex" event_onclick="doTest" WIDTH="1024" HEIGHT="450" clsid="{FFAA1970-287B-4359-93B5-644F6C8190BB}"></OBJECT>';
				}
			},
			doCancel : function() {
				this.win.hide();
			},
			doSave : function() {
				var data = this.selectRecord;
				if (data.PTTYPE == 1) {
					this.emr.FunActiveXInterface('BsSaveAsPrivateData', "1",
							"element..Illrc_1_temp");
				} else {
					this.emr
							.FunActiveXInterface('BsSaveAsPrivateData', "0", "");
				}
				var textData = this.emr.WordData;
				var xmlData = this.emr.WordXML;
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "savePTTemplateData",
							textData : textData,
							xmlData : xmlData,
							pkey:this.selectRecord.PTID
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					return null;;
				}
				MyMessageTip.msg("提示", "保存成功！", true);
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