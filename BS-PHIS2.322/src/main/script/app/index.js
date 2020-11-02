// ﻿$import("org.ext.ext-base", "org.ext.ext-all", "org.ext.ext-patch",
$styleSheet("sencha.ext3.css.ext-all")
$styleSheet("sencha.ext3.css.xtheme-gray")
$styleSheet("sencha.ext3.css.ext-patch")
$styleSheet("app.desktop.TaskBar")
$styleSheet("app.desktop.Logon")
$styleSheet("app.desktop.main")

$import("sencha.ext3.ext-base", "sencha.ext3.ext-all",
		"sencha.ext3.ext-lang-zh_CN");

var __docRdy = false
Ext.onReady(function() {
	if (__docRdy) {
		return
	}
	__docRdy = true
	$require(["app.desktop.Module", "app.desktop.TaskManager",
					"phis.script.PhisLogon", "app.desktop.plugins.LogonWin"],
			function() {
				$logon = new phis.script.PhisLogon({
							forConfig : true
						})
				$logon.on("appsDefineLoaded", function(userRoleToken, res) {
							// add by yangl 修正提示信息不出现的问题
							if (document.getElementById("msg-div22")) {
								msgCt = null;
							}
							if (!Ext.isIE) {
								var ocx_p = document
										.getElementById("emrOcx_Personal");
								if (ocx_p) {
									ocx.FunActiveXInterface("BsCloseDocument",
											'', '');
								}
								var nodes = document
										.getElementsByName("emrOcxFrame");
								for (var i = 0; i < nodes.length; i++) {
									var iframe = nodes[i];
									var ocx = iframe.contentWindow.document
											.getElementById("emrOcx");
									if (ocx) {
										ocx.FunActiveXInterface(
												"BsCloseDocument", '', '');
									}
									iframe.contentWindow.document.body.innerHTML = "";
								}
								if (document.getElementById("lodopOcx")) {
									var lodop = getLodop();
									if (lodop) {
										lodop.PRINT_INIT();
									}
									document.body.innerHTML = "";
								}
							}
							var body = Ext.get(document.body)
							body.update("")
							if (window.ymPrompt) {
								delete window.ymPrompt
								$destory("phis.script.widgets.ymPrompt");
								$import("phis.script.widgets.ymPrompt")
							}
							var apps = res.apps;
							if (res.layoutCss) {
								$styleSheet(res.layoutCss)
							}
							if (apps && apps.length > 0) {
								var myPage = {}
								for (var i = 0; i < apps.length; i++) {
									var ap = apps[i]
									if (!ap) {
										continue;
									}
									if (ap.type == "index") {
										Ext.apply(myPage, ap);
									}
								}
								var exContext = {};

								var mainCfg = {
									title : res.title,
									uid : userRoleToken.userId,
									userPhoto : res.userPhoto,
									logonName : userRoleToken.userName,
									uname : userRoleToken.userName,
									dept : userRoleToken.manageUnitName,
									deptId : userRoleToken.manageUnitId,
									deptRef : userRoleToken.manageUnit.ref,
									jobtitle : userRoleToken.roleName,
									jobtitleId : userRoleToken.roleId,
									jobId : userRoleToken.roleId,
									urt : userRoleToken.id,
									regionCode : res.regionCode,
									regionText : res.regionText,
									mapSign : res.mapSign,
									apps : apps,
									myPage : myPage,
									pageCount : res.pageCount,
									tabnum : res.tabNumber,
									sysMessage : res.sysMessage,
									tabRemove : res.tabRemove,
									serverDate : res.serverDate,
									serverDateTime : res.serverDateTime,
									exContext : exContext,
									staffId : res.staffId,// 员工ID
									topUnitId : res.topUnitId, // 顶级机构ID
									chisActive : res.chisActive
									// 社区是否可用
								}
								// eval("globalDomain=res.domain")
								globalDomain = "platform"
								// alert(res.userDomain);
								eval("userDomain=res.userDomain")
								if (res.title != undefined) {
									document.title = res.title
								}
								$import("app.viewport.MyDesktop",
										"app.viewport.App")
								if (res.myDesktop) {
									mainCfg.myDesktop = res.myDesktop
									$import(res.myDesktop)
								}
								mainApp = new app.viewport.App(mainCfg)
							}
						})
			})// require
})
