$package("phis.script")
$import("util.Logger", "util.rmi.miniJsonRequestSync", "util.CSSHelper",
		"app.desktop.plugins.LoadStateMessenager",
		"app.viewport.WelcomePortal", 'phis.script.WelcomePortal_phsa',
		"org.ext.ux.TabCloseMenu", "util.widgets.ItabPanel",
		"org.ext.ux.message.fadeOut", "phis.script.common",
		"phis.script.cookie.CookieOperater")
$styleSheet("phis.css.index")
$styleSheet("phis.resources.css.app.desktop.Desktop")
$styleSheet("phis.resources.css.skin.qq.ymPrompt")
phis.script.MyDesktop = function(mainApp) {
	this.mainApp = mainApp;
	this.activeModules = {}
	this.activeTasks = {}
	this.moduleView = {}
	this.leftTDWidth = 155
	this.leftTDPadding = 5
	this.addEvents({
				'ready' : true,
				'NotLogon' : true
			})
}
var deskTop_ctx = null;
var phisCtxTask2 = null;
function topBtnClick(id) {
	if (isNaN(id)) {
		deskTop_ctx.openWin(id);
		return;
	}
	switch (id) {
		case 1 :
			if (!deskTop_ctx.mainApp.departmentId) {
				deskTop_ctx.openWin("DepartmentSwitch_out");
				return;
			}
			deskTop_ctx.openWin("phis.application.cic.CIC/CIC/PatientList");
			break;
		case 2 :
			deskTop_ctx
					.openWin("phis.application.top.TOP/TOPFUNC/PharmacySwitch");
			break;
		case 3 :
			// deskTop_ctx.mainApp.logon();
			break;
		case 4 :
			deskTop_ctx
					.openWin("phis.application.top.TOP/TOPFUNC/DepartmentSwitch_out");
			break;
		case 5 :
			// 切换病区
			deskTop_ctx.openWin("phis.application.top.TOP/TOPFUNC/WardSwitch");
			break
		case 7 :
			// 切换病区
			deskTop_ctx
					.openWin("phis.application.top.TOP/TOPFUNC/StoreHouseSwitch");
			break
		case 8 :
			// 库房切换
			deskTop_ctx
					.openWin("phis.application.top.TOP/TOPFUNC/TreasurySwitch");
			break
		case 9 :
			// 切换医技科室
			deskTop_ctx
					.openWin("phis.application.top.TOP/TOPFUNC/MedicalSwitch");
			break;
		case 6 :
			deskTop_ctx.doQuitSystem()
			break;
		case 10 :
			deskTop_ctx.bschisEntrance();
			break;
		case 11 :
			deskTop_ctx.openWin("SwitchRole");
			break

	}
}

Ext.extend(phis.script.MyDesktop, Ext.util.Observable, {
	getDesktopEl : function() {
		return Ext.get(document.body)
	},

	getWinWidth : function() {
		return Ext.getBody().getWidth()
	},

	getWinHeight : function() {
		return Ext.getBody().getHeight()
	},

	getMainTabWidth : function() {
		/**
		 * <td class=leftmuen> 155px
		 * <td class=leftzkss> 10px
		 * <td class=rightbody> 5px(padding-left) + 5px(padding-right) <div
		 * class=tabkk> border 1px * 2
		 */
		return this.getWinWidth() - 155 - 10 - 10
	},

	getMainTabHeight : function() {
		/**
		 * <td> padding-bottom: 5px <div class=top> height: 90px
		 */
		return this.getWinHeight() - 60
	},

	getMainTabDiv : function() {
		if (!this.mainDiv) {
			this.mainDiv = Ext.get('_maintab')
		}
		return this.mainDiv
	},

	setMainTabSize : function() {
		var width = this.getMainTabWidth()
		var div = Ext.fly('_leftDiv')
		if (!div.isDisplayed()) {
			width = width + 155
		}
		this.mainTab.setWidth(width)
		this.mainTab.setHeight(this.getMainTabHeight())
		div.setHeight(this.getMainTabHeight())
	},

	setClass : function(el, removeClass, addClass) {
		el.removeClass(removeClass)
		el.addClass(addClass)
	},

	getWelcomPortalWidth : function() {
		return this.getWinWidth() - 10 - 2
	},

	getWelcomPortalHeight : function() {
		return this.getWinHeight() - 76 - 10 - 5
	},

	setWelcomPortalSize : function() {
		this.welcomPortal.portal.setWidth(this.getWelcomPortalWidth())
		this.welcomPortal.portal.setHeight(this.getWelcomPortalHeight())
	},

	createModule : function(moduleName, moduleId, exCfg) {
		var moduleCfg = null;;
		var res = this.mainApp.taskManager.loadModuleCfg(moduleId);
		if (!res.code) {
			moduleCfg = res;
		} else if (res.code != 200) {
			if (res.msg == "NotLogon") {
				this.mainApp.logon()
			} else {
				if (typeof fCallback == "function") {
					fCallback.apply(scope, []);
				}
				if (res.msg.indexOf("is not accessible") > 0) {
					this.mainApp.desktop.mainTab.el.unmask()
					Ext.MessageBox.alert("错误", "当前角色没有权限,请联系管理员")
				}
			}
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
		Ext.apply(cfg, exCfg);
		Ext.apply(cfg, moduleCfg);
		var cls = moduleCfg.script;
		if (!cls) {
			return;
		}
		if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
			return;
		}
		$import(cls);
		var item = eval("new " + cls + "(cfg)");
		item.setMainApp(this.mainApp);
		return item;
	},
	initWelcomePortal : function(id) {
		if (this.welcomPortal) {
			this.welcomPortal.mainApp = this.mainApp;
			var p = this.welcomPortal.initPanel();
			if (p) {
				this.refreshWelcomePortal();
			}
			return p;
		}

		var myPage = this.mainApp.myPage;
		if (myPage && myPage.items.length > 0) {
			$import("phis.script.viewport.WelcomePortal");
			var welcomPortal = new phis.script.viewport.WelcomePortal({
						myPage : myPage,
						width : this.getWelcomPortalWidth(),
						mainApp : this.mainApp
					});
			welcomPortal.on("openWin", this.onOpenWin, this);
			this.welcomPortal = welcomPortal;
			var p = welcomPortal.initPanel();
			if (p) {
				this.refreshWelcomePortal();
			}
			return p;
		}
		return "";
	},

	initViewPort : function() {
		this.overrideFunc();// add by yangl,重新Ext的一些方法以满足现有需求
		deskTop_ctx = this;
		var panel = new Ext.Panel({
					region : 'center',
					border : false,
					// autoScroll:true,
					autoLoad : {
						url : "html/main.html",
						nocache : true
					}
				})
		panel.on("afterrender", function(p) {
			p.getUpdater().on("update", this.initPanel, this)
			// add by yangl 全键盘事件
			Ext.get(document.body).on("keydown", function(e) {
				var keyCode = e.getKey();
				// 全局键盘事件
				if (e.ctrlKey == true) {
					// ctrl+c ctrl+v 等系统快捷键不屏蔽
					// 86, 90, 88, 67, 65
					if (keyCode == 86 || keyCode == 90 || keyCode == 88
							|| keyCode == 67 || keyCode == 65) {
						return true;
					}
				}
				/**
				 * 屏蔽组合功能键及F1-F12
				 */
				// MyMessageTip.msg("tt",keyCode,true,2);
				if (e.ctrlKey || e.altKey || (keyCode >= 112 && keyCode <= 123)) {
					e.preventDefault();// or e.stopEvent()
					// e.stopEvent();
				}

				// 判断当前是否遮罩状态
				if (Ext.WindowMgr.getActive()) {
					return false;
				}
				/**
				 * 快捷功能一：alt+数字键切换tab页
				 */
				if (e.ctrlKey == true) {
					if (keyCode >= 48 && keyCode <= 57) {
						var tabIndex = keyCode - 48;
						var n = this.mainTab.items.getCount()
						if (n <= 0)
							return;
						tabIndex = tabIndex > n ? n : tabIndex;
						this.mainTab.setActiveTab(this.mainTab.items
								.item(tabIndex - 1));
					}
					if (keyCode == 75) {// 绑定模块快捷键
						this.moduleShortcutKey();
					}
				}
				/**
				 * 屏蔽组合功能键及F1-F12
				 */
				// MyMessageTip.msg("tt",keyCode,true,2);
				if (e.ctrlKey || e.altKey || (keyCode >= 112 && keyCode <= 123)) {
					e.preventDefault();// or e.stopEvent()
					// e.stopEvent();
				}
				return false;
			}, this);
			Ext.get(document.body).on("keyup", function(e) {
				var keyCode = e.getKey();
				var keyName = "";
				var isCombo = false;
				var f1_f12 = ['F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8',
						'F9', 'F10', 'F11', 'F12'];
				if (keyCode >= 16 && keyCode <= 18)
					return;
				if (e.ctrlKey == true) {
					keyName += "ctrl_";
					isCombo = true;
				}
				if (e.altKey == true) {
					keyName += "alt_";
					isCombo = true;
				}
				if (keyCode >= 112 && keyCode <= 123) {// F1-F12
					keyName = f1_f12[keyCode - 112];
				} else if (isCombo) {// 组合键
					keyName = keyName + String.fromCharCode(keyCode);
				}
				if (!this.lastEnterTime) {
					this.lastEnterTime = 0;
				}
				var thisEnterTime = new Date().getTime();
				// 两次执行之间的间隔，防止重复操作（如果有必要，可增加参数控制哪些按钮需要这个延迟判断）
				if (thisEnterTime - this.lastEnterTime < 500) {
					// MyMessageTip.msg("提示", "两次操作间隔太短，忽略本次操作", false);
					return;
				}
				this.lastEnterTime = thisEnterTime;
				// 全局键盘事件
				if (keyName == "F12") {
					var topDiv = Ext.get("_logininfo").parent();
					if (topDiv.isDisplayed()) {
						topDiv.setDisplayed('none');
						// 设置mainTab宽度
						this.mainTab.setHeight(this.mainTab.getHeight() + 60)
					} else {
						topDiv.setDisplayed('block')
						this.mainTab.setHeight(this.mainTab.getHeight() - 60)
					}
					var leftDiv = Ext.fly('_leftDiv')
					leftDiv.setHeight(this.mainTab.getHeight());
					this.collapsed();
				}
				// 判断是否有有效窗口
				var win = Ext.WindowMgr.getActive();
				if (win) {
					var m = win.instance;
					if (m && m.shortcutKeyFunc) {
						m.shortcutKeyFunc(keyCode, keyName)
					}
					return;
				}
				// 判断当前是否遮罩状态
				if (Ext.getBody().hasClass("x-body-masked")) {
					return false;
				}
				// 模块快捷键打开功能
				if (this.hasModuleKey(keyCode, keyName)) {
					return;
				}

				// 获取当前tab页
				var panel = this.mainTab.getActiveTab();
				if (panel) {
					var m = this.mainApp.taskManager.getModule(panel._mId)
					if (!m) {
						return
					}
					var module = m.instance
					if (module && module.shortcutKeyFunc) {
						module.shortcutKeyFunc(keyCode, keyName)
					}
				}
			}, this);
			// 增加退出时注销session操作
			window.onbeforeunload = function(event) {
				// 以下代码主要解决电子病历刷新崩溃问题
				if (!Ext.isIE) {
					var ocx_p = document.getElementById("emrOcx_Personal");
					if (ocx_p) {
						ocx.FunActiveXInterface("BsCloseDocument", '', '');
					}
					var nodes = document.getElementsByName("emrOcxFrame");
					// if (nodes.length > 0) {
					var lodop = getLodop();
					if (lodop) {
						lodop.PRINT_INIT();
					}
					// }
					for (var i = 0; i < nodes.length; i++) {
						var iframe = nodes[i];
						var ocx = iframe.contentWindow.document
								.getElementById("emrOcx");
						if (ocx) {
							ocx.FunActiveXInterface("BsCloseDocument", '', '');
						}
						iframe.contentWindow.document.body.innerHTML = "";
					}
					// if (this.LODOP) {
					// this.LODOP.PRINT_INIT();
					// }
					if (document.getElementById("lodopOcx")) {
						var lodop = getLodop();
						if (lodop) {
							lodop.PRINT_INIT();
						}
						document.body.innerHTML = "";
					}
					// }
				}
				var re = util.rmi.miniJsonRequestSync({
							url : "logon/logonOff"
						});
				// event.returnValue = "确定离开当前页面吗？";
				// document.body.innerHTML = "";
			}
		}, this)
		var viewport = new Ext.Viewport({
					layout : 'border',
					hideBorders : true,
					frame : false,
					items : [panel]
				})
		this.viewport = viewport;
		viewport.on("resize", function(e) {
					if (this.mtr && this.mtr.isDisplayed()) {
						this.setMainTabSize()
					}
					if (this.welcomPortal && !this.mtr.isDisplayed()) {
						this.setWelcomPortalSize()
					}
				}, this)

	},
	hasModuleKey : function(keyCode, keyName) {
		var cookie = util.cookie.CookieOperater;
		var info = cookie.getCookie(this.mainApp.uid + "_"
				+ this.mainApp.jobtitleId + "_" + keyName);
		if (info) {
			info = Ext.decode(info)
			this.openWin(info._mId);
			return true;
		}
	},
	moduleShortcutKey : function() {
		var panel = this.mainTab.getActiveTab();
		if (!panel)
			return;
		// alert(panel._mId)
		// alert(panel.title)
		if (!this.shortcutKeyWin) {
			var m = this.createModule("shortcutKeyConfig",
					"phis.application.menu.COMM/PUB/PUB01");
			this.shortcutKeyWin = m.getWin();
			this.shortcutKeyWin.module = m;
			this.shortcutKeyWin.add(m.initPanel());
		}
		this.shortcutKeyWin.module.curPanel = panel;
		this.shortcutKeyWin.show();
	},
	initPanel : function() {
		if (Ext.isIE6) {// IE10也会进入这里
			try {
				if ($ct) {
					pngfix()
				}
			} catch (e) {
			}
		}
		var banner = this.mainApp.banner;
		if (banner) {
			var topEl = Ext.get("_logininfo").parent("div.topbg");
			topEl.applyStyles("background:url(" + ClassLoader.stylesheetHome
					+ "banners/" + banner + ") no-repeat;")
		}
		var mtr = Ext.get("_middle")
		var btr = Ext.get("_bottom")
		this.mtr = mtr
		this.btr = btr
		var loadMask = new Ext.LoadMask(Ext.getBody(), {
					msg : "数据加载中..."
				});
		loadMask.show();
		// 初始化用户信息显示
		this.initLogininfo()
		// 生成主面板
		this.initMainTab()
		// 初始化上面的标签页
		this.initTopTabs()
		// 功能区的伸缩
		var colp = Ext.get('collapse')
		this.colp = colp
		colp.on("click", this.collapsed, this)
		loadMask.hide();
		this.doLoadSystemParams();
		
		if (phisCtxTask2) {
			phisCtxTask2.stopAll();
		}
		var t = 1000 * 10
		Ext.apply(this, phis.script.common.loadSystemParams({
							"privates" : ['SFQYWJZTX', 'WJZCLSX', 'WJZCSSX',
									'WJZTXJGSJ']
						}));
		if (this.SFQYWJZTX == 1) {
			t = 1000 * this.WJZTXJGSJ
			// t=1000;
			if (this.mainApp.jobtitleId == "phis.50"
					|| this.mainApp.jobtitleId == "phis.55"
					|| this.mainApp.jobtitleId == "phis.51"
					|| this.mainApp.jobtitleId == "phis.52"
					|| this.mainApp.jobtitleId == "phis.53") {
				if (!this.initTasks) {
					this.initTasks = {};
				}
				this.initTasks["WJZ"] = this.doWjztx;
			} else {
				t = 1000 * 10
				if (this.initTasks && this.initTasks["WJZ"]) {
					this.initTasks["WJZ"] = function() {
					}
				}
			}
		}
		// 增加定时访问程序，防止超时
		var task2 = new Ext.util.TaskRunner();
		task2.start({
					run : this.doExecuteTask,
					interval : t,
					scope : this,
					args : [this]//zhaojian 2017-09-25 定时提醒只在护士站显示，将this传给taskRun方法，以便过滤角色
				})
		phisCtxTask2 = task2;
		
		this.longpolling();
	},
	doLoadSystemParams : function() {
		util.rmi.jsonRequest({
					serviceId : "phis.permissionsVerifyService",
					serviceAction : "saveInitSystemParams",
					method : "execute"
				}, function(code, msg, json) {
				}, this)
	},
	/**
	 * 定时任务执行方法
	 * 
	 * @param {}
	 *            interval 执行间隔
	 */
	doExecuteTask : function(interval) {
		// phis定时任务
		// phis初始化任务，登录后就需要定时运行
		for (var id in this.initTasks) {
			this.initTasks[id](interval);
		}
		// phis模块任务，打开相应模块后定时运行
		for (var id in this.activeTasks) {
			this.activeTasks[id](interval);
		}
	},
	longpolling : function() {
		util.rmi.jsonRequest({
					serviceId : "phis.publicService",
					serviceAction : "messageListener",
					method : "execute"
				}, function(code, msg, json) {
					if (code > 200) {
						return;// 未订阅消息，不用开启长轮询
					}
					this.longpolling();
					if (json.message) {// 有提示信息
						// 根据消息类型可进行扩展，业务关键字在服务器端完成，减少无谓的通信次数
						var msg = json.message;
						// alert(msg);
						if (msg.remindMode == "2") {
							if (msg.body)
								MyMessageTip.msg("提示", msg.body, false);
						} else if (msg.remindMode == "3") {// 存在moduleId时,直接操作对应模块
							if(msg.moduleId) {
								this.doMsgEvent(msg)
							}
						} else if (msg.remindMode == "9") {// 系统消息
							var cfg = {
									message : msg.body,
									title : (msg.title || '警告') + ' - '
											+ Date.getServerDateTime(),
									okTxt : "知道了",
									showMask : false,
									useSlide : true,
									width : 400,
									height : 250,
									scope : this
								}
							ymPrompt.alert(cfg);
						} else {
							if (msg.body) {
								var cfg = {
									message : msg.body,
									title : (msg.title || '提示') + ' - ' + Date.getServerDateTime(),
									winPos : 'rb',
									okTxt : (msg.moduleId?"去处理":"知道了"),
									showMask : false,
									useSlide : true,
									scope : this
								}
								if (msg.moduleId) {
									cfg.autoClose = false,
									cfg.handler = function(sign) {
										if (sign == 'ok') {
											// 判断当前是否存在窗口
											var win = Ext.WindowMgr.getActive();
											if (win) {
												MyMessageTip.msg("提示",
														"发现未关闭的窗口业务,请先结束当前业务!",
														true)
												return false;
											}
											this.openWin(msg.moduleId);
										}
										ymPrompt.close();
									};
								}
								ymPrompt.alert(cfg);
							}
						}
					}
				}, this)
	},
	doWjztx : function() {
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "wjzManageService",
					serviceAction : "queryWjztx"
				});
		if (ret.code > 300) {
			Ext.Msg.alert("提示", ret.msg)
			return;
		}
		var body = ret.json.body;
		if (body.totalCount == 0) {
			return;
		}
		ymPrompt.confirmInfo({
					// message : "<table id='wjzTable'><tbody><tr><td>开单科室:儿科
					// 医生:陆正川 单号:1234567 <br> 发布时间:123<div style='display:
					// none;'>{'WJZXH':123,'BRXM':'abc','DH':123}</div></td></tr><tr><td>开单科室:儿科
					// 医生:陆正川 单号:1234567 <br>
					// 发布时间:123</td></tr></tbody></table>",
					message : body.msg,
					title : "危机值提醒 共" + body.totalCount + "条",
					winPos : 'rb',
					okTxt : "处理",
					cancelTxt : "退出",
					scope : desktop_ctx,
					hashcode : "jcbryztztx",
					showMask : false,
					useSlide : true,
					width : 500,
					height : 350,
					handler : desktop_ctx.btnClick,
					autoClose : false
				})
		if (!document.getElementById('wjzTable')
				|| document.getElementById('wjzTable') == null) {
			return;
		}
		var trs = document.getElementById('wjzTable')
				.getElementsByTagName('tr');
		for (var i = 0; i < trs.length; i++) {
			trs[i].onmousedown = function() {
				tronmousedown(this)
			}
		}
		function tronmousedown(obj) {
			for (var o = 0; o < trs.length; o++) {
				if (trs[o] == obj) {
					trs[o].style.backgroundColor = '#DFEBF2';
				} else {
					trs[o].style.backgroundColor = '';
				}
			}
		}
	},
	btnClick : function(sign) {
		if (sign == "cancel" || sign == "close") {
			ymPrompt.doHandler('doClose', true);
		} else if (sign == "ok") {
			var trs = document.getElementById('wjzTable')
					.getElementsByTagName('tr');
			var dqtr = null;
			for (var i = 0; i < trs.length; i++) {
				if (trs[i].style.backgroundColor != '') {
					dqtr = trs[i];
					break;
				}
			}
			if (dqtr == null) {
				Ext.Msg.alert("提示", "请先选择要处理的行");
				return;
			}
			this.wjzCheck = eval("("
					+ dqtr.getElementsByTagName('div')[0].innerHTML + ")")
			this.openWin("phis.application.wjz.WJZ/WJZ/WJZ01")
		}
	},
	doMsgEvent : function(msg) {
		var find = this.activeModules[msg.moduleId]
		if (find) {
			this.mainTab.activate(find)
			if (m = this.mainApp.taskManager.tasks.item(msg.moduleId)) {
				m.instance.fireEvent("messageEvent",msg);
			}
		}
	},
	doLoadSystemInfo : function() {
		util.rmi.jsonRequest({
					serviceId : "phis.publicService",
					serviceAction : "getServerDateTime",
					method : "execute"
				}, function(code, msg, json) {
				}, this)
	},
	initMainTab : function() {
		var backgroundUrl = "background:url("
				+ ClassLoader.stylesheetHome
				+ "app/desktop/images/homepage/background.jpg) no-repeat center;"

		var mainTab = new util.widgets.ItabPanel({
					applyTo : '_maintab',
					bodyStyle : 'padding:5px;' + backgroundUrl,
					minTabWidth : 130,
					tabWidth : 130,
					border : false,
					deferredRender : false,
					plugins : new Ext.ux.TabCloseMenu(),
					enableTabScroll : true,
					width : this.getMainTabDiv().getComputedWidth(),
					height : this.getMainTabHeight(),
					defaults : ({
						// 解决tab切换时电子病历控件被销毁的问题
						hideMode : 'offsets'
					})
				})
		mainTab.on("tabchange", this.onModuleActive, this)
		// add by yangl 监听关闭事件（后续补充完整）
		// mainTab.on("beforeremove", this.beforeClose, this);
		this.mainTab = mainTab
	},
	getNewTopInfo : function() {
		var switch_info = '<li class="write">欢迎 {jobtitle}  {uname}  {dept} ';
		var pvRet = this.filterPermissios(this.mainApp.jobtitleId);
		// add by yangl 初始化操作，如没有默认时自动弹出窗口
		var resourcePath = ClassLoader.appRootOffsetPath
				+ "resources/phis/resources/";
		if (pvRet['TOPFUNC.StoreHouseSwitch'].hasPvs) {// 药库切换
			if (pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId) {// 是否有默认 药库
				this.mainApp.storehouseId = pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId;
				this.mainApp.storehouseName = pvRet['TOPFUNC.StoreHouseSwitch'].storehouseName;
			} else {
				this.mainApp.storehouseId = null;
			}
			switch_info += '|<span title="点击切换当前药库"><a onClick="topBtnClick(7)"><img src="'
					+ resourcePath
					+ 'images/yaoku.png" /><tpl if="storehouseId">{storehouseName}</tpl><tpl if="!storehouseId">{切换药库}</tpl></a></span>';
		}
		if (pvRet['TOPFUNC.PharmacySwitch'].hasPvs) {// 药房切换
			if (pvRet['TOPFUNC.PharmacySwitch'].pharmacyId) {// 是否有默认 药房
				this.mainApp.pharmacyId = pvRet['TOPFUNC.PharmacySwitch'].pharmacyId;
				this.mainApp.pharmacyName = pvRet['TOPFUNC.PharmacySwitch'].pharmacyName;
			} else {
				this.mainApp.pharmacyId = null;
			}
			switch_info += '|<span title="点击切换当前药房"><a onClick="topBtnClick(2)" ><img src="'
					+ resourcePath
					+ 'images/yaofang.png" /><tpl if="pharmacyId">{pharmacyName}</tpl><tpl if="!pharmacyId">{切换药房}</tpl></a></span>';
		}
		if (pvRet['TOPFUNC.TreasurySwitch'].hasPvs) {// 库房切换
			if (pvRet['TOPFUNC.TreasurySwitch'].treasuryId) {// 是否有默认 库房
				this.mainApp.treasuryId = pvRet['TOPFUNC.TreasurySwitch'].treasuryId;
				this.mainApp.treasuryName = pvRet['TOPFUNC.TreasurySwitch'].treasuryName;
				this.mainApp.treasuryEjkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryEjkf;
				this.mainApp.treasuryLbxh = pvRet['TOPFUNC.TreasurySwitch'].treasuryLbxh;
				this.mainApp.treasuryKflb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKflb;
				this.mainApp.treasuryKfzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzt;
				this.mainApp.treasuryKfzb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzb;
				this.mainApp.treasuryGlkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryGlkf;
				this.mainApp.treasuryWxkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryWxkf;
				this.mainApp.treasuryCkfs = pvRet['TOPFUNC.TreasurySwitch'].treasuryCkfs;
				this.mainApp.treasuryCsbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryCsbz;
				this.mainApp.treasuryZjbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjbz;
				this.mainApp.treasuryZjyf = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjyf;
				this.mainApp.treasuryHzpd = pvRet['TOPFUNC.TreasurySwitch'].treasuryHzpd;
				this.mainApp.treasuryPdzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryPdzt;
			} else {
				this.mainApp.treasuryId = null;
			}
			switch_info += '|<span title="点击切换当前库房"><a onClick="topBtnClick(8)"><img src="'
					+ resourcePath
					+ 'images/kufang.png" /><tpl if="treasuryId">{treasuryName}</tpl><tpl if="!treasuryId">{切换库房}</tpl></a></span>';
		}
		if (pvRet['TOPFUNC.WardSwitch'].hasPvs) {// 病区切换
			if (pvRet['TOPFUNC.WardSwitch'].wardId) {// 是否有默认 病区
				this.mainApp.wardId = pvRet['TOPFUNC.WardSwitch'].wardId;
				this.mainApp.wardName = pvRet['TOPFUNC.WardSwitch'].wardName;

			} else {
				this.mainApp.wardId = null;
			}
			switch_info += '|<span title="点击切换当前病区"><a onClick="topBtnClick(5)"><img src="'
					+ resourcePath
					+ 'images/bingqu.png" /><tpl if="wardId">{wardName}</tpl><tpl if="!wardId">切换病区</tpl></a></span>';
		}
		if (pvRet['TOPFUNC.DepartmentSwitch_out'].hasPvs) {// 科室切换，如门诊业务权限时，载入默认科室信息
			if (pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId) {// 是否有默认科室
				this.mainApp.departmentId = pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId;
				this.mainApp.departmentName = pvRet['TOPFUNC.DepartmentSwitch_out'].departmentName;
				this.mainApp.reg_departmentId = pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentId;
				this.mainApp.reg_departmentName = pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentName;
				this.mainApp.departmentType = pvRet['TOPFUNC.DepartmentSwitch_out'].departmentType;
			} else {
				this.mainApp.departmentId = null;
				this.mainApp.reg_departmentId = null;
				this.mainApp.departmentType = null;
			}
			switch_info += '|<span title="点击切换当前科室"><a onClick="topBtnClick(4)"><img src="'
					+ resourcePath
					+ 'images/keshi.png" /><tpl if="reg_departmentId">{reg_departmentName}</tpl><tpl if="!reg_departmentId">切换门诊科室</tpl></a></span>';

		}
		if (pvRet['TOPFUNC.MedicalSwitch'].hasPvs) {// 医技科室切换，如门诊业务权限时，载入默认科室信息
			if (pvRet['TOPFUNC.MedicalSwitch'].MedicalId) {// 是否有默认科室
				this.mainApp.MedicalId = pvRet['TOPFUNC.MedicalSwitch'].MedicalId;
				this.mainApp.MedicalName = pvRet['TOPFUNC.MedicalSwitch'].MedicalName;
			} else {
				this.mainApp.MedicalId = null;
			}
			switch_info += '|<span title="点击切换医技科室"><a onClick="topBtnClick(9)"><img src="'
					+ resourcePath
					+ 'images/yiji.png" /><tpl if="MedicalId">{MedicalName}</tpl><tpl if="!MedicalId">切换医技科室</tpl></a></span>';
		}
		return switch_info;
	},
	initWelComePanel : function() {
		// 个人信息
		// if (this.userInfoTpl) {
		// this.userInfoTpl.overwrite('_doctor_info', this.mainApp);
		// return
		// }
		var d = document.getElementById("_logininfo");
		// add by gaof
		if (!this.mainApp['phis'].storehouseId) {
			this.mainApp['phis'].storehouseId = "";
		}
		if (!this.mainApp['phis'].pharmacyId) {
			this.mainApp['phis'].pharmacyId = "";
		}
		if (!this.mainApp['phis'].treasuryId) {
			this.mainApp['phis'].treasuryId = "";
		}
		if (!this.mainApp['phis'].wardId) {
			this.mainApp['phis'].wardId = "";
		}
		if (!this.mainApp['phis'].reg_departmentId) {
			this.mainApp['phis'].departmentId = "";
			this.mainApp['phis'].reg_departmentId = "";
		}
		if (!this.mainApp['phis'].MedicalId) {
			this.mainApp['phis'].MedicalId = "";
		}
		// add by gaof end
		Ext.apply(this.mainApp['phis'], this.mainApp['phisApp']);
		d.innerHTML = this.tpl.apply(this.mainApp['phis']);

		var lnkSelectors = ["CR", "CF", "MS", "UP", "QT", "HP"]
		for (var i = 0; i < lnkSelectors.length; i++) {
			var lnk = Ext.get(lnkSelectors[i])
			if (lnk) {
				lnk.on("click", this.onTopLnkClick, this)
			}
		}
		return;
		var dateS = new Date().format("Y年m月d号") + ' 星期'
				+ '天一二三四五六'.charAt(new Date().getDay());
		var tpl = new Ext.XTemplate(
				'<strong style="font-weight:bold;">欢迎 【{jobtitle}】{uname} 使用本系统</strong>',
				this.initUserinfo(),
				'<div class="jz" style="margin: 0;padding:0;text-align:right;">'
						+ dateS + '&nbsp;&nbsp;</div>')
		tpl.overwrite('_doctor_info', this.mainApp['phis'])
		this.userInfoTpl = tpl;
		// 图表
		/**
		 * modified by gaof
		 */
		if (this.mainApp.jobtitleId == 'phis.50'
				|| this.mainApp.jobtitleId == 'phis.55') {
			this.mainApp.taskManager.loadInstance(
					"phis.application.menu.COMM/PUB/REG27", {},
					function(module) {
						module.initPanel();
					}, this);
		} else if (this.mainApp.jobtitleId == 'phis.60') {
			this.mainApp.taskManager.loadInstance(
					"phis.application.reg.REG/REG/REG28", {}, function(module) {
						module.initPanel();
					}, this);
		} else if (this.mainApp.jobtitleId == 'phis.63'
				|| this.mainApp.jobtitleId == 'phis.74') {
			this.mainApp.taskManager.loadInstance(
					"phis.application.ivc.IVC/IVC/IVC11", {}, function(module) {
						module.initPanel();
					}, this);
		} else if (this.mainApp.jobtitleId == 'phis.73') {
			this.mainApp.taskManager.loadInstance(
					"phis.application.med.MED/MED/MED05", {}, function(module) {
						module.initPanel();
					}, this);
		}
		/**
		 * modified end
		 */
		else {
			this.mainApp.taskManager.loadInstance(
					"phis.application.menu.COMM/PUB/REG27", {},
					function(module) {
						module.initPanel();
					}, this);
		}
	},
	initUserinfo : function() {
		// add by yangl 业务权限过滤
		var switch_info = "";
		var totalCount = 6;
		var pvRet = this.filterPermissios(this.mainApp.jobtitleId);
		// add by yangl 初始化操作，如没有默认时自动弹出窗口
		if (pvRet['TOPFUNC.StoreHouseSwitch'].hasPvs) {// 药库切换
			if (pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId) {// 是否有默认 药库
				this.mainApp['phis'].storehouseId = pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId;
				this.mainApp['phis'].storehouseName = pvRet['TOPFUNC.StoreHouseSwitch'].storehouseName;
				switch_info += '<div>当前药库：<a onClick="topBtnClick(7)">{storehouseName}</a></div>';
			} else {
				switch_info += '<div>当前药库：<a onClick="topBtnClick(7)"><font color="red">点击选择药库</font></a></div>';
				// if (pvRet['TOPFUNC.StoreHouseSwitch'].showWin != 'false') {
				// this.openWin("StoreHouseSwitch");// 打开药房选择
				// }
			}
			totalCount--;
		}
		if (pvRet['TOPFUNC.PharmacySwitch'].hasPvs) {// 药房切换
			if (pvRet['TOPFUNC.PharmacySwitch'].pharmacyId) {// 是否有默认 药房
				this.mainApp['phis'].pharmacyId = pvRet['TOPFUNC.PharmacySwitch'].pharmacyId;
				this.mainApp['phis'].pharmacyName = pvRet['TOPFUNC.PharmacySwitch'].pharmacyName;
				switch_info += '<div>当前药房：<a onClick="topBtnClick(2)">{pharmacyName}</a></div>';
			} else {
				switch_info += '<div>当前药房：<a onClick="topBtnClick(2)"><font color="red">点击选择药房</font></a></div>';
				// if (pvRet['TOPFUNC.PharmacySwitch'].showWin != 'false') {
				// this.openWin("PharmacySwitch");// 打开药房选择
				// }
			}
			totalCount--;
		}
		if (pvRet['TOPFUNC.TreasurySwitch'].hasPvs) {// 库房切换
			if (pvRet['TOPFUNC.TreasurySwitch'].treasuryId) {// 是否有默认 库房
				this.mainApp['phis'].treasuryId = pvRet['TOPFUNC.TreasurySwitch'].treasuryId;
				this.mainApp['phis'].treasuryName = pvRet['TOPFUNC.TreasurySwitch'].treasuryName;
				this.mainApp['phis'].treasuryEjkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryEjkf;
				this.mainApp['phis'].treasuryLbxh = pvRet['TOPFUNC.TreasurySwitch'].treasuryLbxh;
				this.mainApp['phis'].treasuryKflb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKflb;
				this.mainApp['phis'].treasuryKfzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzt;
				this.mainApp['phis'].treasuryKfzb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzb;
				this.mainApp['phis'].treasuryGlkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryGlkf;
				this.mainApp['phis'].treasuryWxkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryWxkf;
				this.mainApp['phis'].treasuryCkfs = pvRet['TOPFUNC.TreasurySwitch'].treasuryCkfs;
				this.mainApp['phis'].treasuryCsbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryCsbz;
				this.mainApp['phis'].treasuryZjbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjbz;
				this.mainApp['phis'].treasuryZjyf = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjyf;
				this.mainApp['phis'].treasuryHzpd = pvRet['TOPFUNC.TreasurySwitch'].treasuryHzpd;
				this.mainApp['phis'].treasuryPdzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryPdzt;
				switch_info += '<div>当前库房：<a onClick="topBtnClick(8)">{treasuryName}</a></div>';
			} else {
				switch_info += '<div>当前库房：<a onClick="topBtnClick(8)"><font color="red">点击选择库房</font></a></div>';
				// if (pvRet['TOPFUNC.TreasurySwitch'].showWin != 'false') {
				// this.openWin("TreasurySwitch");// 打开库房选择
				// }
			}
			totalCount--;
		}
		// if (pvRet['TOPFUNC.WindowSwitch'].hasPvs == 'true') {// 窗口切换
		//
		// }
		if (pvRet['TOPFUNC.WardSwitch'].hasPvs) {// 病区切换
			if (pvRet['TOPFUNC.WardSwitch'].wardId) {// 是否有默认 病区
				this.mainApp['phis'].wardId = pvRet['TOPFUNC.WardSwitch'].wardId;
				this.mainApp['phis'].wardName = pvRet['TOPFUNC.WardSwitch'].wardName;
				switch_info += '<div>当前病区：<a onClick="topBtnClick(5)">{wardName}</a></div>';
			} else {
				switch_info += '<div>当前病区：<a onClick="topBtnClick(5)"><font color="red">点击选择病区</font></a></div>';
				// if (pvRet['TOPFUNC.WardSwitch'].showWin != 'false') {
				// this.openWin("WardSwitch");// 打开病区选择
				// }
			}
			totalCount--;
		}
		if (pvRet['TOPFUNC.DepartmentSwitch_out'].hasPvs) {// 科室切换，如门诊业务权限时，载入默认科室信息
			if (pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId) {// 是否有默认科室
				this.mainApp['phis'].departmentId = pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId;
				this.mainApp['phis'].departmentName = pvRet['TOPFUNC.DepartmentSwitch_out'].departmentName;
				this.mainApp['phis'].reg_departmentId = pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentId;
				this.mainApp['phis'].reg_departmentName = pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentName;
				switch_info += '<div>门诊科室：<a onClick="topBtnClick(4)">{reg_departmentName}</a></div>';
			} else {
				switch_info += '<div>门诊科室：<a onClick="topBtnClick(4)"><font color="red">点击选择科室</font></a></div>';
				// if (pvRet['TOPFUNC.DepartmentSwitch_out'].showWin != 'false')
				// {
				// this.openWin("DepartmentSwitch_out");// 打开科室选择
				// }
			}
			totalCount--;
		}
		if (pvRet['TOPFUNC.MedicalSwitch'].hasPvs) {// 医技科室切换，如门诊业务权限时，载入默认科室信息
			if (pvRet['TOPFUNC.MedicalSwitch'].MedicalId) {// 是否有默认科室
				this.mainApp['phis'].MedicalId = pvRet['TOPFUNC.MedicalSwitch'].MedicalId;
				this.mainApp['phis'].MedicalName = pvRet['TOPFUNC.MedicalSwitch'].MedicalName;
				switch_info += '<div>医技科室：<a onClick="topBtnClick(9)">{MedicalName}</a></div>';
			} else {
				switch_info += '<div>医技科室：<a onClick="topBtnClick(9)"><font color="red">点击选择科室</font></a></div>';
				// if (pvRet['TOPFUNC.MedicalSwitch'].showWin != 'false') {
				// this.openWin("MedicalSwitch");// 打开科室选择
				// }
			}
			totalCount--;
		}
		for (var i = 0; i < totalCount; i++) {
			switch_info += "<div></div>";
		}
		return switch_info;
	},
	initLogininfo : function() {
		desktop_ctx = this;

		var tpl = new Ext.XTemplate(
				'<ul class="toolbar">',
				this.getNewTopInfo(),
				'<li><img src="resources/phis/css/images/line.png" class="line" /></li>',
				'<li><a id="HP"><i class="ico help"></i></a></li> ',
				// '<li><a id="CR" href="javascript:void(0);" class="roles"
				// title="角色切换"><i class="ico role"></i></a></li>',
				'<li><a id="CF"  title="系统设置"><i class="ico set"></i></a></li>',
				'<li class="tip"><a id="MS"  title="邮箱"><i id="msgp" class="ico email"></i><span class="badge badge-warning"><b id="mn">0</b></span></a></li>',
				'<li><img src="resources/phis/css/images/line.png" class="line" /></li>',
				'<li><a id="QT"  title="退出系统"><i class="ico exit"></i></a></li>',
				'</ul>')
		tpl.overwrite('_logininfo', this.mainApp)
		this.tpl = tpl
		var lnkSelectors = ["CR", "CF", "MS", "UP", "QT", "HP"]
		for (var i = 0; i < lnkSelectors.length; i++) {
			var lnk = Ext.get(lnkSelectors[i])
			if (lnk) {
				lnk.on("click", this.onTopLnkClick, this)
			}
		}
		if (this.mainApp.sysMessage) {
			this.doRemind();
			if (this.mainApp.instantExtractMSG) {
				this_ = this
				setInterval(function() {
							this_.doRemind()
						}, 8000)
			}
		}
	},
	doRemind : function() {
		util.rmi.jsonRequest({
					serviceId : "message",
					schema : "SYS_MESSAGE",
					operate : "showMessage",
					method : "execute"
				}, function(code, msg, json) {
					if (code == 200) {
						var msgp = Ext.get("msgp").dom;
						if (json.flag == 1) {
							msgp.setAttribute("class", "logininfomail_new");
							var oldCount = Ext.get("mn").dom.innerHTML, newCount = json.num;
							Ext.get("mn").dom.innerHTML = newCount;
							if (newCount > oldCount) {
								Ext.fadeOut.msg('提醒:', '您有 (' + json.num
												+ ') 条新的消息', this.doMessage,
										this);
							}
						} else {
							msgp.setAttribute("class", "ico email");
						}
					} else {
						this.mainApp.logon(this.initLogininfo, this)
					}
				}, this)
	},
	doMessage : function(t, m) {
		var msWin = this.msWin
		if (!msWin) {
			$import("sys.message.Message");
			msWin = new sys.message.Message({
						mainApp : this.mainApp
					});
			msWin.on("close", function() {
						Ext.get("msgp").dom.setAttribute("class", "ico email");
					}, this);
			this.msWin = msWin
		} else {
			msWin.getTreeNum()
		}
		if (msWin.selectId) {
			msWin.gridModule.refresh()
		}
		msWin.getWin().show()
	},
	/**
	 * 上部的标签页 初始化
	 */
	initTopTabs : function() {
		var apps = this.mainApp.apps
		var tpl = new Ext.XTemplate('<tpl for=".">', '<li><a >{name}</a></li>',
				'</tpl>');
		var store = new Ext.data.Store({
					reader : new Ext.data.JsonReader({}, ["id", 'name', 'type',
									'pageCount']),
					data : []
				});
		store.loadData(apps)
		var view = new Ext.DataView({
					applyTo : "_topTab",
					store : store,
					tpl : tpl,
					singleSelect : true,
					selectedClass : "select",
					itemSelector : 'a',
					listeners : {
						afterrender : function(view) {
							var id = view.store.getAt(0).data.id
							// this.initNavPanel(id) //选择显示 第一项
							this.onTopTabClick(view, 0)
						},
						scope : this
					}
				})
		view.select(0)
		this.topview = view
		view.on("click", this.onTopTabClick, this)

	},
	/**
	 * 上部的标签页 点击事件
	 */
	onTopTabClick : function(view, index, node, e) {
		var ap = view.store.getAt(index).data
		var id = ap.id
		var title = ap.name
		var type = ap.type
		if (type == "index") {
			if (!this.mtr.hasClass('x-hide-offsets')) {
				this.mtr.addClass('x-hide-offsets');
				// this.mtr.setDisplayed('none')
				this.btr.setStyle('display', '')
			}
			this.lastExpandId = null;
			this.initWelcomePortal(ap.id)
			return
		}
		if (this.mtr.hasClass('x-hide-offsets')) {
			this.mtr.removeClass('x-hide-offsets');
			// this.mtr.setStyle('display', '')
			this.btr.setDisplayed('none')
		}

		var div = Ext.fly('_leftDiv')
		// div.update("")
		if (!div.isDisplayed()) {
			this.collapsed()
		}
		this.initNavPanel(id, title)
		if (this.mainTab) {
			this.setMainTabSize()
			if (this.mainApp.tabRemove) {
				this.mainTab.removeAll()
			}
			if (this.mainApp.appwelcome) {
				if (!this.mainApp.tabRemove) {
					this.mainTab.remove(this.mainTab.items.item(0))
				}
				this.createAppWelcome(ap)
			}

		}
	},
	/**
	 * 初始化 左边功能区菜单
	 */
	initNavPanel : function(id, title) {
		var apps = this.mainApp.apps;
		var ap;
		for (var i = 0; i < apps.length; i++) {
			var d = apps[i];
			if (id == d.id) {
				ap = d;
				break;
			}
		}
		if (!ap || !ap.items || ap.items.length == 0) {
			return;
		}
		var cata = ap.items;
		this.cata = cata;
		if (!this.navView) {
			var navStore = new Ext.data.Store({
						reader : new Ext.data.JsonReader({}, ["id", 'name',
										'fullId']),
						data : cata
					})
			var navTpl = new Ext.XTemplate(
					'<h2 class="title">工作计划</h2>',
					'<tpl for=".">',
					'<ul class="LCatalong">',
					'<li id="{id}">',
					'<img style="float:left;padding-left:5px;padding-top:10px;" src="'
							+ ClassLoader.stylesheetHome
							+ 'app/desktop/images/homepage/allico.png"/><a  class="up" title="{name}">{name:ellipsis(10)}</a>',
					'</li>', '</ul>',
					'<ul id="{id}_module" class="LModule"></ul>', '</tpl>')
			var navView = new Ext.DataView({
						applyTo : "_leftDiv",
						store : navStore,
						tpl : navTpl,
						singleSelect : true,
						autoScroll : true,
						selectedClass : "",
						itemSelector : 'ul.LCatalong'
					})
			navView.select(0)
			this.navView = navView
			navView.on("click", this.onBeforeExpand, this)
		} else {
			this.navView.getStore().loadData(cata)
			this.navView.select(0)
		}
		var h2 = Ext.fly("_leftDiv").child("h2")
		h2.dom.innerHTML = title
		// 进入就展开第一层
		this.onBeforeExpand(this.navView, 0)
	},

	onTopLnkClick : function(e, t) {
		var lnk = e.getTarget();
		var cmd = lnk.id;
		if (!cmd || cmd == "msgp") {
			cmd = lnk.parentNode.getAttribute("id");
		}
		switch (cmd) {
			case "HP" :
				window.open("resources/phis/help/BS-PHIS-HELP.html");
				break;
			case "CR" :
				break;
			case "CF" : // 设置
				this.doSetting(e)
				break;
			case "MS" :
				if (this.mainApp.sysMessage) {
					this.doMessage()
				}
				break;
			case "UP" :
				this.doShowSwitchBar();
				break;
			// case "LK":
			// this.mainApp.logon();
			// break
			// case "QF": // 便捷
			// this.doQuickChange(e)
			// break;
			// case "CL":
			// this.doCloseSwitchBar();
			// break;
			case "QT" : // 退出
				util.rmi.miniJsonRequestAsync({
							url : "logon/logonOff"
						}, function(code, msg, json) {
							// window.location.reload()
							window.location.href = "index.html"
						}, this)
		}
	},
	doShowSwitchBar : function() {
		Ext.get('topFunction_div').fadeIn({
					duration : 0.5
				});
	},
	doCloseSwitchBar : function() {
		Ext.get('topFunction_div').fadeOut({
					duration : 0.5
				});
	},
	doQuickChange : function(e) {
		var cls = "app.modules.config.homePage.QuickPanel"
		var m = this.quickChangeModule
		if (!m) {
			$import(cls)
			m = eval("new " + cls + "({})")
			m.setMainApp(this.mainApp)
			this.quickChangeModule = m
			var p = m.initPanel()
			if (!p) {
				return
			}
		}
		var win = m.getWin()
		if (win) {
			win.setPosition(e.getPageX() - 350, e.getPageY() + 12)
			win.show()
		}
	},

	doSetting : function(e) {
		var cls = "app.modules.config.homePage.SettingPanel"
		var m = this.settingModule
		if (!m) {
			$import(cls)
			m = eval("new " + cls + "({})")
			m.setMainApp(this.mainApp)
			this.settingModule = m
			var p = m.initPanel()
			if (!p) {
				return
			}
		}
		var win = m.getWin()
		if (win) {
			win.setPosition(e.getPageX() - 520, e.getPageY() + 12)
			win.show()
			m.loadRoles()
		}
	},

	showOnlines : function() {
		var olw = this.olw
		if (!olw) {
			var cfg = {
				width : 980,
				entryName : "SYS_OnlineUser",
				listServiceId : "onlineHandler",
				disablePagingTbr : true
			}
			$import("app.modules.list.SimpleListView")
			var m = new app.modules.list.SimpleListView(cfg)
			olw = m.getWin()
			olw.on("show", function() {
						m.refresh()
					}, this)
			this.olw_m = m
			this.olw = olw
		}
		olw.show()
	},
	refreshOnlines : function() {
		this.getOnlines()
		var target = this
		setInterval(function() {
					target.getOnlines()
				}, 30000)
	},
	getOnlines : function() {
		var re = util.rmi.miniJsonRequestSync({
					serviceId : "onlineHandler",
					op : "count"
				})
		if (re.code == 200) {
			Ext.get("OL").dom.innerHTML = "在线人数：" + re.json.body
			if (this.olw_m) {
				this.olw_m.refresh()
			}
		}
	},
	doChangePsw : function() {
		var m = this.changePswModule
		if (m) {
			if (m.getWin().hidden) {
				m.getWin().show();
			}
		} else {
			$import("app.desktop.plugins.PasswordChanger")
			m = new app.desktop.plugins.PasswordChanger({});
			m.setMainApp(this.mainApp)
			var p = m.initPanel()
			if (p) {
				this.changePswModule = m
				m.getWin().show();
			}

		}
	},

	onReady : function() {
		var messenger = new app.desktop.plugins.LoadStateMessenager({})
		messenger.setMainApp(this.mainApp)
		messenger.getWin()

	},
	onModuleActive : function(tab, panel) {
		if (panel && panel._mId) {
			var m = this.mainApp.taskManager.getModule(panel._mId)
			if (!m) {
				return
			}
			var instance = m.instance
			if (instance && typeof instance.active == "function") {
				instance.active()
			}
			this.changeSelection(m.id)
		} else {
			if (this.welcomPortals) {
				for (var i = 0; i < this.welcomPortals.length; i++) {
					this.welcomPortals[i].refresh()
				}
			}
		}
	},

	onExpand : function() {
		this.startExpanding = false;
		this.navView.el.unmask()
	},

	/**
	 * 功能区 --子模块生成
	 */
	onBeforeExpand : function(view, index, node, e) {
		var h2 = Ext.fly("_leftDiv").child("h2")
		var title = h2.dom.innerHTML;
		var id = view.store.getAt(index).data.id
		var span = Ext.fly(id).child("a")
		// 显示隐藏子模块
		var pel = this.getNavElement(id) // Ext.fly(mid)
		if (pel.dom.childNodes.length > 0) { // 有子节点,则隐藏
			if (pel.isDisplayed()) {
				// 关闭
				pel.setDisplayed('none')
				this.setClass(span, "", "up")
				this.lastExpandId = null;
			} else {
				// 展开
				pel.setDisplayed('block')
				this.setClass(span, "up", "")
				this.closeCatalog(this.lastExpandId, title)
				this.lastExpandId = title + "." + id;
			}
			return
		}

		this.startExpanding = true;
		var catalogId = id

		var modules;
		for (var i = 0; i < this.cata.length; i++) {
			var ca = this.cata[i];
			if (ca.id == catalogId) {
				modules = ca.modules;
				break;
			}
		}
		for (var i = 0; i < modules.length; i++) {
			var module = modules[i]
			this.mainApp.taskManager.addModuleCfg(module)
		}
		if (modules.length > 0) {
			this.initNavIcons(catalogId, modules)
			this.setClass(span, "up", "")
		}
		this.onExpand()
		this.closeCatalog(this.lastExpandId, title)
		if (this.lastExpandId == (title + "." + id)) {
			this.lastExpandId = null;
		} else {
			this.lastExpandId = title + "." + id;
		}
		return true;
	},
	closeCatalog : function(id, title) {
		if (!id || id.indexOf(title) < 0)
			return;
		id = id.split(".")[1];
		if (!Ext.fly(id))
			return;
		var span = Ext.fly(id).child("a")
		// 显示隐藏子模块
		var pel = this.getNavElement(id) // Ext.fly(mid)
		// 关闭
		if (pel.isDisplayed()) {
			// 关闭
			pel.setDisplayed('none')
			this.setClass(span, "", "up")
		}
	},
	getNavElement : function(id) {
		var mid = id + '_module' // navTpl中定义
		var pel = Ext.fly(mid)
		return pel
	},

	getModuleTpl : function(id) {
		var tpl = this.iconTpl
		if (!tpl) {
			tpl = new Ext.XTemplate('<tpl for=".">', '<li id="' + id
							+ '_module_{id}">',
					'<a title="{name}">{name:ellipsis(10)}</a>', '</li>',
					'</tpl>');
			this.iconTpl = tpl;
		}
		return tpl;
	},

	/**
	 * 初始化 module模块
	 */
	initNavIcons : function(catalogId, data) {
		var store = new Ext.data.Store({
					reader : new Ext.data.JsonReader({}, ["id", 'name', 'type',
									'fullId']),
					data : data
				});
		var view = new Ext.DataView({
					applyTo : catalogId + '_module',
					store : store,
					tpl : this.getModuleTpl(catalogId),
					singleSelect : true,
					autoScroll : true,
					selectedClass : 'selected',
					itemSelector : 'a'
				})
		view.on("click", this.onNavClick, this)
		this.moduleView[catalogId] = view
	},

	onNavClick : function(view, index, node, e) {
		var id = view.store.getAt(index).data.fullId// .id
		this.openWin(id)
	},

	openWin : function(id) {
		if (!this.mainTab)
			return;
		var find = this.activeModules[id]
		if (find) {
			this.mainTab.activate(find)
			return;
		}
		var tabnum = this.mainApp.tabnum || 8
		if (tabnum) {
			var n = this.mainTab.items.getCount()
			if (n >= tabnum) {
				/*
				 * this.clearSelection(id) Ext.Msg.alert("提示","已经达到当前选项卡最大数量[" +
				 * tabnum + "]的限制") var panel = this.mainTab.getActiveTab()
				 * this.changeSelection(panel._mId) return
				 */
				var it = this.mainTab.items.item(1)
				if (this.mainApp.appwelcome) {
					it = this.mainTab.items.item(1)
				}
				this.mainTab.remove(it)
			}
		}
		this.mainTab.el.mask("加载中...", "x-mask-loading")
		this.mainApp.taskManager.loadInstance(id, {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}, this.onModuleLoad, this)
	},
	onModuleLoad : function(module) {
		if (!module) {
			this.mainTab.el.unmask()
			return
		}
		if (module.showWinOnly) {
			module.opener = this;
			var win = module.getWin()
			win._mId = module.fullId // id
			win._keyId = module.id
			win.show();
			win.on("close", this.onCloseWin, this)
			this.mainTab.el.unmask()
			return;
		}
		if (module.initPanel) {
			var panel = module.initPanel()
			if (panel && module.warpPanel) {
				panel = module.warpPanel(panel)
			}
			if (panel) {
				panel.on("destroy", this.onClose, this)
				panel._mId = module.fullId // id
				panel._keyId = module.id
				panel.closable = true
				panel.setTitle(module.name)
				this.mainTab.add(panel)
				this.mainTab.activate(panel)
				if (this.mainApp.rendered) {
					this.mainTab.doLayout()
				}
				this.activeModules[panel._mId] = panel;
			}
			// add by yangl
			if (panel && module.afterOpen) {
				module.afterOpen();
			}
			// PHIS提醒信息
			if (panel && module.taskRun) {
				this.activeTasks[module.fullId] = module.taskRun;
			}
			this.mainTab.el.unmask()
			return;
		}
		if (module.initHTML) {
			var html = module.initHTML();
			var panel = this.mainTab.add({
						title : module.title,
						closable : true,
						layout : "fit",
						html : html
					})
			panel.on("destroy", this.onClose, this)
			panel._mId = module.fullId
			panel._keyId = module.id
			this.mainTab.activate(panel)
			if (this.mainApp.rendered) {
				this.mainTab.doLayout()
			}
			this.activeModules[panel._mId] = panel;
			this.mainTab.el.unmask()
			return;
		}
	},
	// beforeClose : function(tab, panel) {
	// var id = panel._mId
	// if (id) {
	// if (this.activeModules[id] && this.activeModules[id].beforeClose) {
	// return this.activeModules[id].beforeClose();
	// }
	// }
	// return true;
	// },
	onClose : function(panel) {
		var id = panel._mId
		if (id) {
			this.mainApp.taskManager.destroyInstance(id)
			delete this.activeModules[id]
			// 删除模块对应定时任务
			delete this.activeTasks[id]
		}
		if (this.mainTab.items.length == 0) {
			this.clearSelection(panel._keyId)
		}
	},

	onCloseWin : function(win) {
		var id = win._mId
		if (id) {
			this.mainApp.taskManager.destroyInstance(id)
			delete this.activeModules[id]
		}
	},
	closeCurrentTab : function() {
		if (this.mainApp.locked) {
			return;
		}
		var mainTab = this.mainTab
		var act = mainTab.getActiveTab()
		if (act && act._mId) {
			mainTab.remove(act)
		}
	},
	changeSelection : function(mid) {
		var store = this.navView.getStore()
		var n = store.getCount()
		for (var i = 0; i < n; i++) {
			var cid = store.getAt(i).data.id
			var view = this.moduleView[cid]
			if (view) {
				var index = view.getStore().indexOfId(mid)
				if (index != -1) {
					if (!view.isSelected(index)) {
						view.select(index)
					}
				} else {
					view.clearSelections()
				}
			}
		}
	},

	clearSelection : function(mid) {
		var store = this.navView.getStore()
		var n = store.getCount()
		for (var i = 0; i < n; i++) {
			var cid = store.getAt(i).data.id
			var view = this.moduleView[cid]
			if (view) {
				var index = view.getStore().indexOfId(mid)
				if (index != -1) {
					if (view.isSelected(index)) {
						view.deselect(index)
					}
					break
				}
			}
		}
	},

	/**
	 * 伸缩条 操作
	 */
	collapsed : function() {
		var div = Ext.fly('_leftDiv')
		var td = div.parent()
		var img = this.colp.dom
		if (div.isDisplayed()) {
			td.setWidth('0px')
			div.setDisplayed('none')
			img.src = "resources/phis/resources/images/rhmp8.png"
			// 设置mainTab宽度
			this.mainTab.setWidth(this.mainTab.getWidth() + 155)
		} else {
			td.setWidth('155px')
			div.setDisplayed('block')
			img.src = "resources/phis/resources/images/rhmp9.png"
			// 设置mainTab宽度
			// this.mainTab.setWidth(this.getMainTabDiv().getComputedWidth()-155)
			this.mainTab.setWidth(this.mainTab.getWidth() - 155)
		}
	},

	refreshWelcomePortal : function() {
		if (this.welcomPortal) {
			this.welcomPortal.refresh()
		}
	},

	createAppWelcome : function(ap) {
		var id = ap.id
		ap.fullId = id
		var find = this.activeModules[id]
		if (find) {
			this.mainTab.activate(find)
		} else {
			var cfg = {
				appId : id,
				count : ap.pageCount,
				height : this.getMainTabHeight() - 40
			}
			var myPage = this.mainApp.myPage
			if (myPage.properties) {
				cfg.entryName = myPage.properties.entryName
			}

			this.mainApp.taskManager.addModuleCfg(ap)
			$import("app.viewport.AppWelcome")
			var wel = new app.viewport.AppWelcome(cfg)
			if (wel) {
				wel.mainApp = this.mainApp
				ap.instance = wel
				wel.name = "欢迎"
				this.onAppWelcomeLoader(wel)
			}
		}
	},

	onAppWelcomeLoader : function(m) {
		if (m.initPanel) {
			var panel = m.initPanel()
			if (panel) {
				panel.on("destroy", this.onClose, this)
				panel._mId = m.appId
				panel.setTitle(m.name)
				this.mainTab.add(panel)
				this.mainTab.activate(panel)
				if (this.mainApp.rendered) {
					this.mainTab.doLayout()
				}
				this.activeModules[panel._mId] = panel;
			}
		}
	},
	filterPermissios : function(roleId) {
		// if (this.pvRet)
		// return this.pvRet;
		var body = ['TOPFUNC.PatientList', 'TOPFUNC.PharmacySwitch',
				'TOPFUNC.WardSwitch', 'TOPFUNC.DepartmentSwitch_out',
				'TOPFUNC.StoreHouseSwitch', 'TOPFUNC.TreasurySwitch',
				'TOPFUNC.MedicalSwitch'];
		var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.permissionsVerifyService",
					serviceAction : "filterPermissions",
					method : "execute",
					roleId : roleId,
					body : body
				});
		if (res.code > 200) {
			alert("载入用户业务权限失败...")
			logoning = false;
			return "";
		}
		this.pvRet = res.json.pvRet;
		return res.json.pvRet;
	},
	overrideFunc : function() {
		// IE下关闭模块报错
		Ext.override(Ext.layout.FormLayout, {
					// add by yangl 非友好方式，不知道为什么IE下会报错，临时处理如下
					onRemove : function(c) {
						Ext.layout.FormLayout.superclass.onRemove.call(this, c);
						if (this.trackLabels) {
							c.un('show', this.onFieldShow, this);
							c.un('hide', this.onFieldHide, this);
						}

						var el = c.getPositionEl(), ct = c.getItemCt
								&& c.getItemCt();
						if (c.rendered && ct) {
							if (el && el.dom) {
								try {
									el.insertAfter(ct);
								} catch (e) {
									// toolbar中存在radioGroup的时候，IE下关闭的时候此处会报JS错误，导致关闭失败
								}
							}
							Ext.destroy(ct);
							Ext.destroyMembers(c, 'label', 'itemCt');
							if (c.customItemCt) {
								Ext.destroyMembers(c, 'getItemCt',
										'customItemCt');
							}
						}
					}
				});
		Ext.MessageBox = function() {
			var dlg, opt, mask, waitTimer, bodyEl, msgEl, textboxEl, textareaEl, progressBar, pp, iconEl, spacerEl, buttons, activeTextEl, bwidth, bufferIcon = '', iconCls = '', buttonNames = [
					'ok', 'yes', 'no', 'cancel'];

			// private
			var handleButton = function(button) {
				buttons[button].blur();
				if (dlg.isVisible()) {
					dlg.hide();
					handleHide();
					Ext.callback(opt.fn, opt.scope || window, [button,
									activeTextEl.dom.value, opt], 1);
				}
			};

			// private
			var handleHide = function() {
				if (opt && opt.cls) {
					dlg.el.removeClass(opt.cls);
				}
				progressBar.reset();
			};

			// private
			var handleEsc = function(d, k, e) {
				if (opt && opt.closable !== false) {
					dlg.hide();
					handleHide();
				}
				if (e) {
					e.stopEvent();
				}
			};

			// private
			var updateButtons = function(b) {
				var width = 0, cfg;
				if (!b) {
					Ext.each(buttonNames, function(name) {
								buttons[name].hide();
							});
					return width;
				}
				dlg.footer.dom.style.display = '';
				Ext.iterate(buttons, function(name, btn) {
							cfg = b[name];
							if (cfg) {
								btn.show();
								btn.setText(Ext.isString(cfg)
										? cfg
										: Ext.MessageBox.buttonText[name]);
								width += btn.getEl().getWidth() + 15;
							} else {
								btn.hide();
							}
						});
				return width;
			};

			return {
				getDialog : function(titleText) {
					if (!dlg) {
						var btns = [];

						buttons = {};
						Ext.each(buttonNames, function(name) {
									btns.push(buttons[name] = new Ext.Button({
												text : this.buttonText[name],
												handler : handleButton
														.createCallback(name),
												hideMode : 'offsets'
											}));
								}, this);
						dlg = new Ext.Window({
									autoCreate : true,
									title : titleText,
									resizable : false,
									constrain : true,
									constrainHeader : true,
									minimizable : false,
									maximizable : false,
									stateful : false,
									modal : true,
									shim : true,
									buttonAlign : "center",
									width : 400,
									height : 100,
									minHeight : 80,
									plain : true,
									footer : true,
									closable : true,
									close : function() {
										if (opt && opt.buttons
												&& opt.buttons.no
												&& !opt.buttons.cancel) {
											handleButton("no");
										} else {
											handleButton("cancel");
										}
									},
									fbar : new Ext.Toolbar({
												items : btns,
												enableOverflow : false
											})
								});
						dlg.render(document.body);
						dlg.getEl().addClass('x-window-dlg');
						mask = dlg.mask;
						bodyEl = dlg.body.createChild({
							html : '<div class="ext-mb-icon"></div><div class="ext-mb-content"><span class="ext-mb-text"></span><br /><div class="ext-mb-fix-cursor"><input type="text" class="ext-mb-input" /><textarea class="ext-mb-textarea"></textarea></div></div>'
						});
						iconEl = Ext.get(bodyEl.dom.firstChild);
						var contentEl = bodyEl.dom.childNodes[1];
						msgEl = Ext.get(contentEl.firstChild);
						textboxEl = Ext.get(contentEl.childNodes[2].firstChild);
						textboxEl.enableDisplayMode();
						textboxEl.addKeyListener([10, 13], function() {
									if (dlg.isVisible() && opt && opt.buttons) {
										if (opt.buttons.ok) {
											handleButton("ok");
										} else if (opt.buttons.yes) {
											handleButton("yes");
										}
									}
								});
						textareaEl = Ext
								.get(contentEl.childNodes[2].childNodes[1]);
						textareaEl.enableDisplayMode();
						progressBar = new Ext.ProgressBar({
									renderTo : bodyEl
								});
						bodyEl.createChild({
									cls : 'x-clear'
								});
					}
					return dlg;
				},
				updateText : function(text) {
					if (!dlg.isVisible() && !opt.width) {
						dlg.setSize(this.maxWidth, 100); // resize first so
						// content is never
						// clipped from
						// previous shows
					}
					// Append a space here for sizing. In IE, for some reason,
					// it wraps text incorrectly without one in some cases
					msgEl.update(text ? text + ' ' : '&#160;');

					var iw = iconCls != '' ? (iconEl.getWidth() + iconEl
							.getMargins('lr')) : 0, mw = msgEl.getWidth()
							+ msgEl.getMargins('lr'), fw = dlg
							.getFrameWidth('lr'), bw = dlg.body
							.getFrameWidth('lr'), w;

					w = Math
							.max(	Math.min(opt.width || iw + mw + fw + bw,
											opt.maxWidth || this.maxWidth),
									Math.max(opt.minWidth || this.minWidth,
											bwidth || 0));

					if (opt.prompt === true) {
						activeTextEl.setWidth(w - iw - fw - bw);
					}
					if (opt.progress === true || opt.wait === true) {
						progressBar.setSize(w - iw - fw - bw);
					}
					if (Ext.isIE && w == bwidth) {
						w += 4; // Add offset when the content width is smaller
						// than the buttons.
					}
					msgEl.update(text || '&#160;');
					dlg.setSize(w, 'auto').center();
					return this;
				},
				updateProgress : function(value, progressText, msg) {
					progressBar.updateProgress(value, progressText);
					if (msg) {
						this.updateText(msg);
					}
					return this;
				},
				isVisible : function() {
					return dlg && dlg.isVisible();
				},
				hide : function() {
					var proxy = dlg ? dlg.activeGhost : null;
					if (this.isVisible() || proxy) {
						dlg.hide();
						handleHide();
						if (proxy) {
							// unghost is a private function, but i saw no
							// better solution
							// to fix the locking problem when dragging while it
							// closes
							dlg.unghost(false, false);
						}
					}
					return this;
				},
				show : function(options) {
					if (this.isVisible()) {
						this.hide();
					}
					opt = options;
					var d = this.getDialog(opt.title || "&#160;");

					d.setTitle(opt.title || "&#160;");
					var allowClose = (opt.closable !== false
							&& opt.progress !== true && opt.wait !== true);
					d.tools.close.setDisplayed(allowClose);
					activeTextEl = textboxEl;
					opt.prompt = opt.prompt || (opt.multiline ? true : false);
					if (opt.prompt) {
						if (opt.multiline) {
							textboxEl.hide();
							textareaEl.show();
							textareaEl.setHeight(Ext.isNumber(opt.multiline)
									? opt.multiline
									: this.defaultTextHeight);
							activeTextEl = textareaEl;
						} else {
							textboxEl.show();
							textareaEl.hide();
						}
					} else {
						textboxEl.hide();
						textareaEl.hide();
					}
					activeTextEl.dom.value = opt.value || "";
					if (opt.prompt) {
						d.focusEl = activeTextEl;
					} else {
						var bs = opt.buttons;
						var db = null;
						if (bs && bs.ok) {
							db = buttons["ok"];
						} else if (bs && bs.yes) {
							db = buttons["yes"];
						}
						if (opt.defBtnFocus) {
							db = buttons[opt.defBtnFocus]
						}
						if (db) {
							d.focusEl = db;
						}
					}
					if (Ext.isDefined(opt.iconCls)) {
						d.setIconClass(opt.iconCls);
					}
					this.setIcon(Ext.isDefined(opt.icon)
							? opt.icon
							: bufferIcon);
					bwidth = updateButtons(opt.buttons);
					progressBar.setVisible(opt.progress === true
							|| opt.wait === true);
					this.updateProgress(0, opt.progressText);
					this.updateText(opt.msg);
					if (opt.cls) {
						d.el.addClass(opt.cls);
					}
					d.proxyDrag = opt.proxyDrag === true;
					d.modal = opt.modal !== false;
					d.mask = opt.modal !== false ? mask : false;
					if (!d.isVisible()) {
						// force it to the end of the z-index stack so it gets a
						// cursor in FF
						document.body.appendChild(dlg.el.dom);
						d.setAnimateTarget(opt.animEl);
						// workaround for window internally enabling keymap in
						// afterShow
						d.on('show', function() {
									if (allowClose === true) {
										d.keyMap.enable();
									} else {
										d.keyMap.disable();
									}
								}, this, {
									single : true
								});
						d.show(opt.animEl);
					}
					if (opt.wait === true) {
						progressBar.wait(opt.waitConfig);
					}
					return this;
				},
				setIcon : function(icon) {
					if (!dlg) {
						bufferIcon = icon;
						return;
					}
					bufferIcon = undefined;
					if (icon && icon != '') {
						iconEl.removeClass('x-hidden');
						iconEl.replaceClass(iconCls, icon);
						bodyEl.addClass('x-dlg-icon');
						iconCls = icon;
					} else {
						iconEl.replaceClass(iconCls, 'x-hidden');
						bodyEl.removeClass('x-dlg-icon');
						iconCls = '';
					}
					return this;
				},
				progress : function(title, msg, progressText) {
					this.show({
								title : title,
								msg : msg,
								buttons : false,
								progress : true,
								closable : false,
								minWidth : this.minProgressWidth,
								progressText : progressText
							});
					return this;
				},
				wait : function(msg, title, config) {
					this.show({
								title : title,
								msg : msg,
								buttons : false,
								closable : false,
								wait : true,
								modal : true,
								minWidth : this.minProgressWidth,
								waitConfig : config
							});
					return this;
				},
				alert : function(title, msg, fn, scope) {
					this.show({
								title : title,
								msg : msg,
								buttons : this.OK,
								fn : fn,
								scope : scope,
								minWidth : this.minWidth
							});
					return this;
				},
				confirm : function(title, msg, fn, scope) {
					this.show({
								title : title,
								msg : msg,
								buttons : this.YESNO,
								fn : fn,
								scope : scope,
								icon : this.QUESTION,
								minWidth : this.minWidth
							});
					return this;
				},
				prompt : function(title, msg, fn, scope, multiline, value) {
					this.show({
								title : title,
								msg : msg,
								buttons : this.OKCANCEL,
								fn : fn,
								minWidth : this.minPromptWidth,
								scope : scope,
								prompt : true,
								multiline : multiline,
								value : value
							});
					return this;
				},
				OK : {
					ok : true
				},
				CANCEL : {
					cancel : true
				},
				OKCANCEL : {
					ok : true,
					cancel : true
				},
				YESNO : {
					yes : true,
					no : true
				},
				YESNOCANCEL : {
					yes : true,
					no : true,
					cancel : true
				},
				INFO : 'ext-mb-info',
				WARNING : 'ext-mb-warning',
				QUESTION : 'ext-mb-question',
				ERROR : 'ext-mb-error',
				defaultTextHeight : 75,
				maxWidth : 600,
				minWidth : 100,
				minProgressWidth : 250,
				minPromptWidth : 250,
				buttonText : {
					ok : "确定",
					cancel : "取消",
					yes : "是",
					no : "否"
				}
			};
		}();
		Ext.Msg = Ext.MessageBox;
	}
})
function MM_swapImgRestore() { // v3.0
	var i, x, a = document.MM_sr;
	for (i = 0; a && i < a.length && (x = a[i]) && x.oSrc; i++)
		x.src = x.oSrc;
}
function MM_preloadImages() { // v3.0
	var d = document;
	if (d.images) {
		if (!d.MM_p)
			d.MM_p = new Array();
		var i, j = d.MM_p.length, a = MM_preloadImages.arguments;
		for (i = 0; i < a.length; i++)
			if (a[i].indexOf("#") != 0) {
				d.MM_p[j] = new Image;
				d.MM_p[j++].src = a[i];
			}
	}
}

function MM_findObj(n, d) { // v4.01
	var p, i, x;
	if (!d)
		d = document;
	if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
		d = parent.frames[n.substring(p + 1)].document;
		n = n.substring(0, p);
	}
	if (!(x = d[n]) && d.all)
		x = d.all[n];
	for (i = 0; !x && i < d.forms.length; i++)
		x = d.forms[i][n];
	for (i = 0; !x && d.layers && i < d.layers.length; i++)
		x = MM_findObj(n, d.layers[i].document);
	if (!x && d.getElementById)
		x = d.getElementById(n);
	return x;
}

function MM_swapImage() { // v3.0
	var i, j = 0, x, a = MM_swapImage.arguments;
	document.MM_sr = new Array;
	for (i = 0; i < (a.length - 2); i += 3)
		if ((x = MM_findObj(a[i])) != null) {
			document.MM_sr[j++] = x;
			if (!x.oSrc)
				x.oSrc = x.src;
			x.src = a[i + 2];
		}
}