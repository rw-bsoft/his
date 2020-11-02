$package("chis.script.viewport")
$import("util.Logger", "util.rmi.miniJsonRequestSync", "util.CSSHelper",
		"app.desktop.plugins.LoadStateMessenager", "org.ext.ux.TabCloseMenu",
		"util.widgets.ItabPanel", "chis.script.viewport.WelcomePortal",
		"org.ext.ux.message.fadeOut")
$styleSheet("chis.css.index")
$styleSheet("chis.resources.app.desktop.Desktop")
$styleSheet("chis.resources.app.desktop.ExtendMain")
var reportClickEvent = function(obj) {
	var id = obj.chartId;
	Ext.getCmp(id).reportClickEvent(obj);
}
chis.script.viewport.MyDesktop = function(mainApp) {
	this.mainApp = mainApp;
	this.activeModules = {}
	this.moduleView = {}
	this.leftTDWidth = 155
	this.leftTDPadding = 5
	this.addEvents({
				'ready' : true,
				'NotLogon' : true
			})
}
var desktop_ctx = null;
function topBtnClick(id) {
	desktop_ctx.onTopLnkClick(id);
}

Ext.extend(chis.script.viewport.MyDesktop, Ext.util.Observable, {
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
		return this.getWinWidth() - 155 - 10 - 10 - 2
	},

	getMainTabHeight : function() {
		/**
		 * <td> padding-bottom: 5px <div class=top> height: 90px
		 */
		return this.getWinHeight() - 76 - 10
	},

	getMainTabDiv : function() {
		if (!this.mainDiv) {
			this.mainDiv = Ext.get('_maintab')
		}
		return this.mainDiv
	},

	setMainTabSize : function() {
		var h = this.mainTab.header.getHeight()
		var width = this.getMainTabWidth()
		var div = Ext.fly('_leftDiv')
		if (!div.isDisplayed()) {
			width = width + 155
		}
		this.mainTab.setWidth(width)
		this.mainTab.header.setHeight(h)
		this.mainTab.body.setHeight(this.getMainTabHeight() - h)
		div.setHeight(this.getMainTabHeight())
	},

	setClass : function(el, removeClass, addClass) {
		el.removeClass(removeClass)
		el.addClass(addClass)
	},
	getWelcomPageWidth : function() {
		return this.getWinWidth() - 10 - 2
	},
	getWelcomPortalWidth : function() {
		return this.getWinWidth() - 10 - 2
	},
	getWelcomPageHeight : function() {
		return this.getWinHeight() - 60
	},

	getWelcomPortalHeight : function() {
		return this.getWinHeight() - 76 - 10 - 5
	},

	setWelcomPortalSize : function() {
		this.welcomPortal.portal.setWidth(this.getWelcomPortalWidth())
	},

	initWelcomePortal : function() {
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
			var welcomPortal = new chis.script.viewport.WelcomePortal({
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
	onOpenWin : function(lo, needRefresh, replaceCnd, initCnd) {
		this.openWelcomeWin(lo, needRefresh, replaceCnd, initCnd);
	},
	openWelcomeWin : function(lo, needRefresh, replaceCnd, initCnd) {
		var appId = lo.appId
		var catalogId = lo.catalogId
		var moduleId = lo.moduleId
		if (lo.param)
			this.moduleParam = lo.param;
		var fullId = appId + "/" + catalogId + "/" + moduleId
		this.fullId = fullId
		var find = this.activeModules[fullId];
		this.initCnd = initCnd
		if (find) {
			this.mainApp.taskManager.loadInstance(fullId, {
						showButtonOnTop : true,
						autoLoadSchema : false,
						isCombined : true,
						param : this.moduleParam
					}, function(module) {
						var listModule = module.midiModules["list"];
						listModule.requestData.cnds = initCnd;
						// console.log(listModule.requestData);
						// console.log(listModule);
						if (this.moduleParam && this.moduleParam.date) {
							var formatDate = this.moduleParam.date
									.format('Y-m-d');
							// console.log(formatDate);
							listModule.requestData.fromDate = formatDate;
							listModule.requestData.toDate = formatDate;
							listModule.fromDateField.setValue(formatDate);
							listModule.fromDateField.fireEvent('select',
									listModule.fromDateField);
							listModule.toDateField.setValue(formatDate);
							listModule.toDateField.fireEvent('select',
									listModule.toDateField);
						}
						listModule.loadData();
					}, this)
		}
		if (this.mainApp) {
			var desktop = this.mainApp.desktop
			var topview = desktop.topview
			var i = topview.getStore().indexOfId(appId)
			if (!topview.isSelected(i)) {
				desktop.onTopTabClick(topview, i)
				topview.select(i)
			}
			var navView = desktop.navView
			i = navView.getStore().indexOfId(catalogId)
			var pel = desktop.getNavElement(catalogId)
			if (!navView.isSelected(i) && pel.dom.childNodes.length == 0) {
				desktop.onBeforeExpand(navView, i)
			}
			navView.select(i)
			var moduleView = desktop.moduleView[catalogId]
			i = moduleView.getStore().indexOfId(moduleId)
			desktop.onNavClick(moduleView, i)
			if (desktop.activeModules[moduleId]) {
				moduleView.select(i)
			}
			if (this.win) {
				this.win.hide()
			}
		}
	},
	initViewPort : function() {
		// add by yangl 重写的方法
		this.overrideFunc();
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
					p.getUpdater().on("update", this.initPanel, this);
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

	initPanel : function() {
		if (Ext.isIE6) {
			try {
				if ($ct) {
					pngfix()
				}
			} catch (e) {
			};
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
		this.addKeyEvent();
	},
	addKeyEvent : function() {
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
					this.mainTab.setActiveTab(this.mainTab.items.item(tabIndex
							- 1));
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
			var f1_f12 = ['F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9',
					'F10', 'F11', 'F12'];
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
			// 全局键盘事件
			if (keyName == "F12" && this.colp) {
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
			// 获取当前tab页
			var panel = this.mainTab.getActiveTab();
			if (panel !== null) {
				var m = this.mainApp.taskManager.getModule(panel._mId)
				if (!m) {
					return
				}
				var module = m.instance;
				if (m && m.shortcutKeyFunc) {
					module.shortcutKeyFunc(keyCode, keyName)
				}
			}
		}, this);
		// 增加退出时注销session操作
//		window.onbeforeunload = function() {
//			var re = util.rmi.miniJsonRequestSync({
//						url : "logon/logonOff"
//					});
//		}
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
					height : this.getMainTabHeight()
				})
		mainTab.on("tabchange", this.onModuleActive, this)
		this.mainTab = mainTab
	},

	initLogininfo : function() {
		desktop_ctx = this;
		var tpl = new Ext.XTemplate(
				'<div>欢迎您登入系统，{logonName}</div>',
				'<ul class="toolbar">',
				'<li><img src="resources/chis/css/images/line.png" class="line" /></li>',
				'<li><a href="javascript:void(0);" style="color:#fff;" title="帮助" id="HP"><i class="ico help"></i></a></li>',
				'<li><a id="CF" href="javascript:void(0);" title="系统设置"><i class="ico set"></i></a></li>',
				'<li><img src="resources/chis/css/images/line.png" class="line" /></li>',
				'<li class="tip"><a id="MS" href="javascript:void(0);" title="邮箱"><i id="msgp" class="ico email"></i><span class="badge badge-warning"><b id="mn">0</b></span></a></li>',
				'<li><img src="resources/chis/css/images/line.png" class="line" /></li>',
				'<li><a id="QT" href="javascript:void(0);" title="退出系统"><i class="ico exit"></i></a></li>',
				'</ul>')
		tpl.overwrite('_logininfo', this.mainApp)
		this.tpl = tpl
		var lnkSelectors = ["CF", "MS", "QT", "HP"]
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
		//妇保儿保医生隐藏疾病模块
		if(this.mainApp.jobId=='chis.08'||this.mainApp.jobId=='chis.07')
		{
			var arrs=[];
			var tempApps=this.mainApp.apps;
			for(var jj=0,ll=tempApps.length;jj<ll;jj++)
			{
				if(tempApps[jj].id!='chis.application.diseasemanage.DISEASEMANAGE')
					arrs.push(tempApps[jj])
			}
			this.mainApp.apps=arrs;
		}
		//
		var apps = this.mainApp.apps
		
		var tpl = new Ext.XTemplate('<tpl for=".">',
				'<li><a href="#">{name}</a></li>', '</tpl>');
		var store = new Ext.data.Store({
					reader : new Ext.data.JsonReader({}, ["id", 'name', 'type',
									'pageCount', 'properties']),
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
		var ap = view.store.getAt(index).data;
		var id = ap.id
		var title = ap.name
		var type = ap.type
		if (type == "index") {
			if (this.mtr.isDisplayed()) {
				this.mtr.setDisplayed('none')
				this.btr.setStyle('display', '')
			}
			var wp = this.initWelcomePortal();
			if (wp) {
				wp.doLayout();
			}
			return
		}
		if (type == "welcome") {
			var script = ap.properties.script
			if (this.mtr.isDisplayed()) {
				this.mtr.setDisplayed('none')
				this.btr.setStyle('display', '')
			}
			if (!this.welcomePage) {
				var cfg = {};
				cfg.mainApp = this.mainApp;
				$import(script);
				var module = eval("new " + script + "(cfg)");
				var firstPage = module.initPanel();
				var welcomePage = new Ext.Panel({
							region : 'center',
							border : false,
							bodyBorder : false,
							items : [firstPage],
							// width:'100%',
							height : this.getWelcomPageHeight(),
							// layout:'fit',
							autoScroll : true,
							renderTo : '_index',
							listeners : {
								afterrender : function() {
									if (module.onReady)
										module.onReady();
								}
							}
						});
				this.welcomePage = welcomePage;
				if (this.welcomePage) {
					this.welcomePage.doLayout();
				}
			}
			return
		}
		if (!this.mtr.isDisplayed()) {
			this.mtr.setStyle('display', '')
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
							+ 'chis/css/images/allico.png"/><a href="#" class="up">{name}</a>',
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

	onTopLnkClick : function(e) {
		var lnk = e.getTarget();
		var cmd = lnk.id;
		if (!cmd || cmd == "msgp") {
			cmd = lnk.parentNode.getAttribute("id");
		}
		switch (cmd) {
			case "HP" :
				window.open("resources/chis/help/BS-CHIS-HELP.html");
				break;
			case "CF" : // 设置
				this.doSetting(e)
				break;
			case "MS" :
				if (this.mainApp.sysMessage) {
					this.doMessage()
				}
				break;
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
		var id = view.store.getAt(index).data.id
		var span = Ext.fly(id).child("a")
		// 显示隐藏子模块
		var pel = this.getNavElement(id) // Ext.fly(mid)
		if (pel.dom.childNodes.length > 0) { // 有子节点,则隐藏
			if (pel.isDisplayed()) {
				pel.setDisplayed('none')
				this.setClass(span, "", "up")
			} else {
				pel.setDisplayed('block')
				this.setClass(span, "up", "")
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
		return true;
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
							+ '_module_{id}">', '<a href="#" title="{name}">{name:ellipsis(10)}</a>',
					'</li>', '</tpl>');
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
		var id
		if (view.store.getAt(index)) {
			id = view.store.getAt(index).data.fullId// .id
		} else {
			id = this.fullId
		}
		this.openWin(id)
	},

	openWin : function(id) {
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
				var it = this.mainTab.items.item(0)
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
					isCombined : true,
					cnds : this.initCnd,
					param : this.moduleParam
				}, this.onModuleLoad, this)
		this.initCnd = null
	},
	onModuleLoad : function(module) {
		if (module.showWinOnly) {
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

	onClose : function(panel) {
		var id = panel._mId
		if (id) {
			this.activeModules[id] = 'undfined';
			delete this.activeModules[id];
			this.mainApp.taskManager.destroyInstance(id);
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
			img.src = "resources/chis/css/images/rhmp8.png"
			// 设置mainTab宽度
			this.mainTab.setWidth(this.mainTab.getWidth() + 155)
		} else {
			td.setWidth('155px')
			div.setDisplayed('block')
			img.src = "resources/chis/css/images/rhmp9.png"
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
				this.mainApp.tabRemove ? this.mainTab.add(panel) : this.mainTab
						.insert(0, panel);
				this.mainTab.activate(panel)
				if (this.mainApp.rendered) {
					this.mainTab.doLayout()
				}
				this.activeModules[panel._mId] = panel;
			}
		}
	},
	overrideFunc : function() {
		/**
		 * add by yangl 1、解决editorlist中编辑项输入非法值后不失去焦点 2、解决combo远程查询支持回车查询
		 */
		Ext.override(Ext.Editor, {
			onSpecialKey : function(field, e) {
				if (!this.lastEnterTime) {
					this.lastEnterTime = 0;
				}
				var thisEnterTime = new Date().getTime();
				if (thisEnterTime - this.lastEnterTime < 50) {// 不知道为什么，回车事件会被执行两次
					// yangl
					e.stopEvent();
					return;
				}
				// if (field.isExpanded)
				// MyMessageTip.msg("提示", "desktop:" + field.isExpanded(),
				// true);
				this.lastEnterTime = thisEnterTime;

				var key = e.getKey(), complete = this.completeOnEnter
						&& key == e.ENTER, cancel = this.cancelOnEsc
						&& key == e.ESC;
				if (key == e.TAB && !field.validate()) {
					e.stopEvent();
					return;
				}
				if (complete || cancel) {
					e.stopEvent();
					if (complete) {
						if (!field.validate()) {
							return;
						}

						if (key == e.ENTER && field.isSearchField) {
							if (!field.isExpanded()) {
								// 是否是字母
								// var patrn = /^[a-zA-Z.]+$/;
								// if (patrn.exec(field.getValue())) {
								// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
								field.getStore().removeAll();
								field.lastQuery = "";
								if (field.doQuery(field.getValue(), true) !== false) {
									e.stopEvent();
									return;
								}
							}
							// }
						}
						if (key == e.ENTER
								&& field instanceof util.widgets.MyCombox
								&& !field.isSearchField) {
							var searchValue = field.getRawValue()
							var searchType = field.searchField;
							var st = field.getStore();
							var sign = false;
							if (searchValue.length > 0) {
								for (var i = 0; i < st.getCount(); i++) {
									var r = st.getAt(i)
									var reg = new RegExp("^"
											+ searchValue.toLowerCase());
									if (reg.test(r.get(searchType)
											.toLowerCase())) {
										if (sign)
											continue;
										sign = true;
										if (field.getValue() != r.get("key")) {
											field.setValue(r.get("key"));
										}
									}
								}
							}
							// field.setValue(searchValue)
						}
						this.completeEdit();
					} else {
						this.cancelEdit();
					}
					if (field.triggerBlur) {
						field.triggerBlur();
					}
				}
				this.fireEvent('specialkey', field, e);
			}
		});
		// add by yangl 解决多工具栏时grid内容区域高度不对的问题
		Ext.override(Ext.grid.GridView, {
			layout : function(initial) {
				if (!this.mainBody) {
					return; // not rendered
				}

				var grid = this.grid, gridEl = grid.getGridEl(), gridSize = gridEl
						.getSize(true), gridWidth = gridSize.width, gridHeight = gridSize.height, scroller = this.scroller, scrollStyle, headerHeight, scrollHeight;

				if (gridWidth < 20 || gridHeight < 20) {
					return;
				}

				if (grid.autoHeight) {
					scrollStyle = scroller.dom.style;
					scrollStyle.overflow = 'visible';

					if (Ext.isWebKit) {
						scrollStyle.position = 'static';
					}
				} else {
					this.el.setSize(gridWidth, gridHeight);

					headerHeight = this.mainHd.getHeight();
					scrollHeight = gridHeight - headerHeight;
					if (this.showBtnOnLevel) {
						scrollHeight = scrollHeight - 27;
					}
					scroller.setSize(gridWidth, scrollHeight);

					if (this.innerHd) {
						this.innerHd.style.width = (gridWidth) + "px";
					}
				}

				if (this.forceFit || (initial === true && this.autoFill)) {
					if (this.lastViewWidth != gridWidth) {
						this.fitColumns(false, false);
						this.lastViewWidth = gridWidth;
					}
				} else {
					this.autoExpand();
					this.syncHeaderScroll();
				}
				this.onLayout(gridWidth, scrollHeight);
			}
		});
		Ext.override(Ext.Button, {
					onClick : function(e) {
						if (e) {
							e.preventDefault();
						}
						if (e.button !== 0) {
							return;
						}
						if (!this.lastEnterTime) {
							this.lastEnterTime = 0;
						}
						var thisEnterTime = new Date().getTime();
						// 两次执行之间的间隔，防止重复操作（如果有必要，可增加参数控制哪些按钮需要这个延迟判断）
						if (thisEnterTime - this.lastEnterTime < 800) {
							// MyMessageTip.msg("提示", "两次操作间隔太短，忽略本次操作", false);
							return;
						}
						this.lastEnterTime = thisEnterTime;
						if (!this.disabled) {
							this.doToggle();
							if (this.menu && !this.hasVisibleMenu()
									&& !this.ignoreNextClick) {
								this.showMenu();
							}
							this.fireEvent('click', this, e);
							if (this.handler) {
								// this.el.removeClass('x-btn-over');
								this.handler.call(this.scope || this, this, e);
							}
						}
					}
				});
		// 捕捉GridView的一些异常，防止IE下操作有问题
		Ext.override(Ext.grid.GridView, {
			getCell : function(row, col) {
				try {
					return Ext.fly(this.getRow(row)).query(this.cellSelector)[col];
				} catch (e) {
					return null;
				}
			},
			getResolvedXY : function(resolved) {
				if (!resolved) {
					return null;
				}
				var cell = resolved.cell, row = resolved.row;
				if (cell) {
					return Ext.fly(cell).getXY();
				} else {
					if (!row)
						return null;
					return [this.el.getX(), Ext.fly(row).getY()];
				}
			}
		});
		/**
		 * 解决角色切换引起的消息框显示有问题的缺陷
		 */
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