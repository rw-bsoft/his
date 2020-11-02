$package("phis.application.war.script")

$import("phis.script.SimpleModule")
phis.application.war.script.MedicalExpressionModule = function(cfg) {
	cfg.exContext = {};
	phis.application.war.script.MedicalExpressionModule.superclass.constructor.apply(
			this, [cfg]);
}
var emr_this = emr_this || {};
Ext.extend(phis.application.war.script.MedicalExpressionModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var mPanel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										// height : 200,
										width : 450,
										items : this.getExpList()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getActiveXPanel()
									}]
						});
				this.mPanel = mPanel;
				return mPanel;
			},

			getExpList : function() {
				var module = this.createModule("refExpList", this.refExpList);
				this.list = module;
				module.on("openModule", this.hideEmr, this);
				module.on("formWinHide", this.showEmr, this);
				module.on("save", this.afterSave, this);
				module.on("listRowClick", this.onListRowClick, this);
				var grid = module.initPanel();
				return grid;
			},
			afterSave : function() {
				var data = this.list.getCheckData();
				this.DYBDSBH = data.DYBDSBH;
				var name = data.BDSMC;
				var str = data.BDSNR || "";
				if (str != "") {
					var r = this.emr.FunActiveXInterface('BsEditMedicTemplate',
							"1", name + '#' + str);
				} else {
					var r = this.emr.FunActiveXInterface('BsEditMedicTemplate',
							"0", name + '#' + str);
				}
			},
			onListRowClick : function() {
				var data = this.list.getCheckData();
				this.DYBDSBH = data.DYBDSBH;
				var name = data.BDSMC;
				var str = data.BDSNR || this.valueB||"";
				var r = this.emr.FunActiveXInterface('BsEditMedicTemplate',
						"3", name + '#' + str);
			},
			getActiveXPanel : function() {
				if (this.panel)
					return this.panel;
				this.mKey = "EEE1";
				var url = ClassLoader.appRootOffsetPath
						+ (Ext.isIE ? 'emrocx_ie.html' : 'emrocx_ff.html');
				var panel = new Ext.Panel({
					// id : this.mKey,
					frame : true,
					border : false,
					html : '<div id="emrOcxContainer_' + this.mKey
							+ '"><IFRAME id=' + this.mKey + ' name='
							+ this.mKey + ' width="100%" height="700" src="'
							+ url + '?mKey=' + this.mKey
							+ '" frameborder=0  scrolling=no  ></iframe></div>'
				});
				this.panel = panel;
				panel.on("afterrender", this.onActiveXReady, this);
				emr_this[this.mKey] = this;
				return panel;
			},

			onActiveXReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE ? document.frames[this.mKey] : document
						.getElementById(this.mKey);
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
					this.hideEmr();
					Ext.Msg.confirm("提示", "检查到您尚未安装电子病历插件,是否需要下载安装程序？",
							function(btn) {
								this.showEmr();
								if (btn == 'yes') {
									this.doDownLoad();
								}
							}, this)
					return;
				}
				this.emr.height = this.panel.getHeight() - 50;
				var _ctx = this;
				if (Ext.isIE) {
					this.emr.attachEvent("OnClick", function() {
								_ctx.emrCallBackFunc();
							});
				}
			},
			emrCallBackFunc : function() {
				var retObj = eval("(" + this.emr.StrReturnData + ")");
				var retType = retObj.FunName;
				var valueA = retObj.ValueA;
				var valueB = retObj.ValueB;
				this.valueB=null;
				if (retType == "SaveRecordMedicalPic" && valueB != null) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicalExpMaintainService",
								serviceAction : "saveMedicalExpContent",
								pkey : this.DYBDSBH,
								valueB : valueB
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.doLogout);
					} else {
						this.valueB=valueB
						this.list.refresh();
					}
				}
			},
			doDownLoad : function() {
				window.open(ClassLoader.appRootOffsetPath
						+ "component/ActiveX.exe");
			},
			hideEmr : function() {// 隐藏Iframe
				var iframe = document.getElementById("emrOcxContainer_"
						+ this.mKey);
				iframe.style.display = "none";
			},
			showEmr : function() {// 显示Iframe
				var iframe = document.getElementById("emrOcxContainer_"
						+ this.mKey);
				iframe.style.display = "";
			}
		});