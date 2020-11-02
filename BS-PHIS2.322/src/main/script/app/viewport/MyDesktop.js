$import("util.Logger", "util.rmi.miniJsonRequestSync", "util.CSSHelper",
		"app.desktop.plugins.LoadStateMessenager",
		"app.viewport.WelcomePortal", "org.ext.ux.TabCloseMenu",
		"util.widgets.ItabPanel", "org.ext.ux.message.fadeOut",
		"phis.script.common")
app.viewport.MyDesktop = function(mainApp) {
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
var deskTop_ctx = null;
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

Ext.extend(app.viewport.MyDesktop, Ext.util.Observable, {
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

	initWelcomePortal : function() {
		if (this.welcomPortal) {
			var p = this.welcomPortal.initPanel()
			if (p) {
				this.refreshWelcomePortal()
			}
			return p
		}
		var myPage = this.mainApp.myPage
		var cfg = {
			appId : myPage.id,
			renderTo : '_index',
			count : this.mainApp.pageCount,
			height : this.getWelcomPortalHeight()
		}
		if (myPage.properties) {
			cfg.entryName = myPage.properties.entryName
		}
		var welcomPortal = new app.viewport.WelcomePortal(cfg)
		welcomPortal.mainApp = this.mainApp
		this.welcomPortal = welcomPortal
		var p = welcomPortal.initPanel()
		if (p) {
			this.refreshWelcomePortal()
		}
		return p
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
			if ($ct) {
				pngfix()
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
		// modify by yangl 异步加载登录信息
		this.filterPermissios(this.mainApp.jobtitleId);
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
	},
	doLoadSystemParams : function() {
		util.rmi.jsonRequest({
					serviceId : "phis.permissionsVerifyService",
					serviceAction : "saveInitSystemParams"
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
					height : this.getMainTabHeight()
				})
		mainTab.on("tabchange", this.onModuleActive, this)
		this.mainTab = mainTab
	},
	getNewTopInfo : function() {
		var switch_info = '<li class="write">欢迎 {dept}  {jobtitle}  {uname}';
		var pvRet = this.pvRet;
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
		d.innerHTML = this.tpl.apply(this.mainApp);
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
		tpl.overwrite('_doctor_info', this.mainApp)
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
	// initUserinfo : function() {
	// // add by yangl 业务权限过滤
	// var switch_info = "";
	// var totalCount = 6;
	// var pvRet = this.filterPermissios(this.mainApp.jobtitleId);
	// // add by yangl 初始化操作，如没有默认时自动弹出窗口
	// if (pvRet['TOPFUNC.StoreHouseSwitch'].hasPvs) {// 药库切换
	// if (pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId) {// 是否有默认 药库
	// this.mainApp.storehouseId =
	// pvRet['TOPFUNC.StoreHouseSwitch'].storehouseId;
	// this.mainApp.storehouseName =
	// pvRet['TOPFUNC.StoreHouseSwitch'].storehouseName;
	// switch_info += '<div>当前药库：<a
	// onClick="topBtnClick(7)">{storehouseName}</a></div>';
	// } else {
	// switch_info += '<div>当前药库：<a onClick="topBtnClick(7)"><font
	// color="red">点击选择药库</font></a></div>';
	// // if (pvRet['TOPFUNC.StoreHouseSwitch'].showWin != 'false') {
	// // this.openWin("StoreHouseSwitch");// 打开药房选择
	// // }
	// }
	// totalCount--;
	// }
	// if (pvRet['TOPFUNC.PharmacySwitch'].hasPvs) {// 药房切换
	// if (pvRet['TOPFUNC.PharmacySwitch'].pharmacyId) {// 是否有默认 药房
	// this.mainApp.pharmacyId = pvRet['TOPFUNC.PharmacySwitch'].pharmacyId;
	// this.mainApp.pharmacyName = pvRet['TOPFUNC.PharmacySwitch'].pharmacyName;
	// switch_info += '<div>当前药房：<a
	// onClick="topBtnClick(2)">{pharmacyName}</a></div>';
	// } else {
	// switch_info += '<div>当前药房：<a onClick="topBtnClick(2)"><font
	// color="red">点击选择药房</font></a></div>';
	// // if (pvRet['TOPFUNC.PharmacySwitch'].showWin != 'false') {
	// // this.openWin("PharmacySwitch");// 打开药房选择
	// // }
	// }
	// totalCount--;
	// }
	// if (pvRet['TOPFUNC.TreasurySwitch'].hasPvs) {// 库房切换
	// if (pvRet['TOPFUNC.TreasurySwitch'].treasuryId) {// 是否有默认 库房
	// this.mainApp.treasuryId = pvRet['TOPFUNC.TreasurySwitch'].treasuryId;
	// this.mainApp.treasuryName = pvRet['TOPFUNC.TreasurySwitch'].treasuryName;
	// this.mainApp.treasuryEjkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryEjkf;
	// this.mainApp.treasuryLbxh = pvRet['TOPFUNC.TreasurySwitch'].treasuryLbxh;
	// this.mainApp.treasuryKflb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKflb;
	// this.mainApp.treasuryKfzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzt;
	// this.mainApp.treasuryKfzb = pvRet['TOPFUNC.TreasurySwitch'].treasuryKfzb;
	// this.mainApp.treasuryGlkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryGlkf;
	// this.mainApp.treasuryWxkf = pvRet['TOPFUNC.TreasurySwitch'].treasuryWxkf;
	// this.mainApp.treasuryCkfs = pvRet['TOPFUNC.TreasurySwitch'].treasuryCkfs;
	// this.mainApp.treasuryCsbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryCsbz;
	// this.mainApp.treasuryZjbz = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjbz;
	// this.mainApp.treasuryZjyf = pvRet['TOPFUNC.TreasurySwitch'].treasuryZjyf;
	// this.mainApp.treasuryHzpd = pvRet['TOPFUNC.TreasurySwitch'].treasuryHzpd;
	// this.mainApp.treasuryPdzt = pvRet['TOPFUNC.TreasurySwitch'].treasuryPdzt;
	// switch_info += '<div>当前库房：<a
	// onClick="topBtnClick(8)">{treasuryName}</a></div>';
	// } else {
	// switch_info += '<div>当前库房：<a onClick="topBtnClick(8)"><font
	// color="red">点击选择库房</font></a></div>';
	// // if (pvRet['TOPFUNC.TreasurySwitch'].showWin != 'false') {
	// // this.openWin("TreasurySwitch");// 打开库房选择
	// // }
	// }
	// totalCount--;
	// }
	// // if (pvRet['TOPFUNC.WindowSwitch'].hasPvs == 'true') {// 窗口切换
	// //
	// // }
	// if (pvRet['TOPFUNC.WardSwitch'].hasPvs) {// 病区切换
	// if (pvRet['TOPFUNC.WardSwitch'].wardId) {// 是否有默认 病区
	// this.mainApp.wardId = pvRet['TOPFUNC.WardSwitch'].wardId;
	// this.mainApp.wardName = pvRet['TOPFUNC.WardSwitch'].wardName;
	// switch_info += '<div>当前病区：<a
	// onClick="topBtnClick(5)">{wardName}</a></div>';
	// } else {
	// switch_info += '<div>当前病区：<a onClick="topBtnClick(5)"><font
	// color="red">点击选择病区</font></a></div>';
	// // if (pvRet['TOPFUNC.WardSwitch'].showWin != 'false') {
	// // this.openWin("WardSwitch");// 打开病区选择
	// // }
	// }
	// totalCount--;
	// }
	// if (pvRet['TOPFUNC.DepartmentSwitch_out'].hasPvs) {//
	// 科室切换，如门诊业务权限时，载入默认科室信息
	// if (pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId) {// 是否有默认科室
	// this.mainApp.departmentId =
	// pvRet['TOPFUNC.DepartmentSwitch_out'].departmentId;
	// this.mainApp.departmentName =
	// pvRet['TOPFUNC.DepartmentSwitch_out'].departmentName;
	// this.mainApp.reg_departmentId =
	// pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentId;
	// this.mainApp.reg_departmentName =
	// pvRet['TOPFUNC.DepartmentSwitch_out'].reg_departmentName;
	// switch_info += '<div>门诊科室：<a
	// onClick="topBtnClick(4)">{reg_departmentName}</a></div>';
	// } else {
	// switch_info += '<div>门诊科室：<a onClick="topBtnClick(4)"><font
	// color="red">点击选择科室</font></a></div>';
	// // if (pvRet['TOPFUNC.DepartmentSwitch_out'].showWin != 'false')
	// // {
	// // this.openWin("DepartmentSwitch_out");// 打开科室选择
	// // }
	// }
	// totalCount--;
	// }
	// if (pvRet['TOPFUNC.MedicalSwitch'].hasPvs) {// 医技科室切换，如门诊业务权限时，载入默认科室信息
	// if (pvRet['TOPFUNC.MedicalSwitch'].MedicalId) {// 是否有默认科室
	// this.mainApp.MedicalId = pvRet['TOPFUNC.MedicalSwitch'].MedicalId;
	// this.mainApp.MedicalName = pvRet['TOPFUNC.MedicalSwitch'].MedicalName;
	// switch_info += '<div>医技科室：<a
	// onClick="topBtnClick(9)">{MedicalName}</a></div>';
	// } else {
	// switch_info += '<div>医技科室：<a onClick="topBtnClick(9)"><font
	// color="red">点击选择科室</font></a></div>';
	// // if (pvRet['TOPFUNC.MedicalSwitch'].showWin != 'false') {
	// // this.openWin("MedicalSwitch");// 打开科室选择
	// // }
	// }
	// totalCount--;
	// }
	// for (var i = 0; i < totalCount; i++) {
	// switch_info += "<div></div>";
	// }
	// return switch_info;
	// },
	initLogininfo : function() {
		desktop_ctx = this;

		var tpl = new Ext.XTemplate(
				'<ul class="toolbar">',
				this.getNewTopInfo(),
				'<li><img src="resources/chis/css/images/line.png" class="line" /></li>',
				'<li><a href="javascript:void(0);" id="HP"><i class="ico help"></i></a></li> ',
				// '<li><a id="CR" href="javascript:void(0);" class="roles"
				// title="角色切换"><i class="ico role"></i></a></li>',
				'<li><a id="CF" href="javascript:void(0);" title="系统设置"><i class="ico set"></i></a></li>',
				'<li class="tip"><a id="MS" href="javascript:void(0);" title="邮箱"><i id="msgp" class="ico email"></i><span class="badge badge-warning"><b id="mn">0</b></span></a></li>',
				'<li><img src="resources/chis/css/images/line.png" class="line" /></li>',
				'<li><a id="QT" href="javascript:void(0);" title="退出系统"><i class="ico exit"></i></a></li>',
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
		var tpl = new Ext.XTemplate('<tpl for=".">',
				'<li><a href="javascript:void(0)">{name}</a></li>', '</tpl>');
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
			if (this.mtr.isDisplayed()) {
				this.mtr.setDisplayed('none')
				this.btr.setStyle('display', '')
			}
			this.initWelcomePortal()
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
							+ 'app/desktop/images/homepage/allico.png"/><a href="javascript:void(0)" class="up">{name}</a>',
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
							+ '_module_{id}">', '<a href="javascript:void(0)">{name}</a>',
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
			this.mainApp.taskManager.destroyInstance(id)
			delete this.activeModules[id]
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
			img.src = "images/rhmp8.png"
			// 设置mainTab宽度
			this.mainTab.setWidth(this.mainTab.getWidth() + 155)
		} else {
			td.setWidth('155px')
			div.setDisplayed('block')
			img.src = "images/rhmp9.png"
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
	// 调整成异步的，看是否能解决ajax请求没有反应的问题
	filterPermissios : function(roleId, callBackFunc) {
		// if (this.pvRet)
		// return this.pvRet;
		var body = ['TOPFUNC.PatientList', 'TOPFUNC.PharmacySwitch',
				'TOPFUNC.WardSwitch', 'TOPFUNC.DepartmentSwitch_out',
				'TOPFUNC.StoreHouseSwitch', 'TOPFUNC.TreasurySwitch',
				'TOPFUNC.MedicalSwitch'];
		util.rmi.miniJsonRequestAsync({
					serviceId : "phis.permissionsVerifyService",
					serviceAction : "filterPermissions",
					roleId : roleId,
					body : body
				}, function(code, msg, json) {
					if (code > 200) {
						alert("载入用户业务权限失败...")
						logoning = false;
						return "";
					}
					this.pvRet = json.pvRet;
					this.initLogininfo();
				}, this);
	},
	overrideFunc : function() {
		Ext.useShims = true;
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
		// IE下关闭模块报错
//		Ext.override(Ext.Element, {
//					// add by yangl 非友好方式，不知道为什么IE下会报错，临时处理如下
//					insertAfter : function(el) {
////						try {
//							(el = GETDOM(el)).parentNode.insertBefore(this.dom,
//									el.nextSibling);
////						} catch (e) {
////							return;
////						}
//						return this;
//					}
//				});

		// add by yangl 解决toFixed四舍五入问题
		Ext.applyIf(Number.prototype, {
					toFixed : function(s) {
						return (parseInt(this * Math.pow(10, s) + 0.5) / Math
								.pow(10, s)).toString();
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
		// add by yangl 解决radio在IE下toolbar中的样式问题 //新版本未发现有此问题，故注释掉了
		// Ext.override(Ext.layout.ColumnLayout, {
		// renderAll : function(ct, target) {
		// if (!this.innerCt) {
		// this.innerCt = target.createChild({
		// cls : 'x-column-inner'
		// });
		// this.innerCt.createChild({
		// cls : 'x-clear'
		// });
		// }
		// Ext.layout.ColumnLayout.superclass.renderAll.call(this,
		// ct, this.innerCt);
		// }
		// });
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
							if (searchValue.length > 0) {
								for (var i = 0; i < st.getCount(); i++) {
									var r = st.getAt(i)
									var reg = new RegExp("^"
											+ searchValue.toLowerCase());
									if (reg.test(r.get(searchType)
											.toLowerCase())) {
										if (field.getValue() != r.get("key")) {
											field.setValue(r.get("key"));
										}
									}
								}
							}
							// f.setValue(searchValue)
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
		// EMR ocx中IE下删除键无效
		if (Ext.isIE) {
			document.onkeydown = function() {
				if (window.event.keyCode == 8 || window.event.keyCode == 9
						|| window.event.keyCode == 37
						|| window.event.keyCode == 38
						|| window.event.keyCode == 39
						|| window.event.keyCode == 40) {
					window.event.keyCode = 0;
				}
			}
		}
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