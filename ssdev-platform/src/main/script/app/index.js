// ﻿$import("org.ext.ext-base", "org.ext.ext-all", "org.ext.ext-patch",
$styleSheet("sencha.ext3.css.ext-all")
$styleSheet("sencha.ext3.css.xtheme-gray")
$styleSheet("sencha.ext3.css.ext-patch")
$styleSheet("app.desktop.Desktop")
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
					"platform.script.PlatformLogon",
					"app.desktop.plugins.LogonWin"], function() {
				$logon = new platform.script.PlatformLogon({
							forConfig : true
						})
				$logon.on("appsDefineLoaded", function(userRoleToken, res) {
					var body = Ext.get(document.body)
					body.update("")
					var apps = res.apps;
					if (res.layoutCss) {
						$styleSheet(res.layoutCss)
					}
					if (apps) {
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
						if (res.chisStatus == "1") {
							exContext.childrenRegisterAge = res.childrenRegisterAge;
							exContext.childrenDieAge = res.childrenDieAge;
							exContext.oldPeopleAge = res.oldPeopleAge;
							exContext.hypertensionMode = res.hypertensionMode;
							exContext.diabetesMode = res.diabetesMode;
							exContext.pregnantMode = res.pregnantMode;
							exContext.oldPeopleMode = res.oldPeopleMode;
							exContext.diabetesPrecedeDays = res.diabetesPrecedeDays;
							exContext.diabetesDelayDays = res.diabetesDelayDays;
							exContext.areaGridType = res.areaGridType;
							exContext.diabetesType = res.diabetesType;
							exContext.hypertensionType = res.hypertensionType;
							exContext.healthCheckType = res.healthCheckType;

							exContext.debilityShowType=res.debilityShowType;

							exContext.postnatalVisitType = res.postnatalVisitType;
							exContext.postnatal42dayType = res.postnatal42dayType;


						}

						var mainCfg = {
							title : res.title,
							uid : userRoleToken.userId,
							userPhoto : res.userPhoto,
							logonName : userRoleToken.userName,
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
							banner : res.banner,
							serverDate : res.serverDate,
							exContext : exContext,
							sysMessage : res.sysMessage,
							instantExtractMSG : res.instantExtractMSG,// 是否即时提取消息
							chisStatus : res.chisStatus, // 是否启用社区
							phisStatus : res.phisStatus
							// 是滞启用医疗
						}
						if (res.phisStatus == "1") {
							mainCfg.uname = userRoleToken.userName;
							mainCfg.staffId = res.staffId; // 员工ID
							mainCfg.topUnitId = res.topUnitId; // 顶级机构ID
						}
						eval("globalDomain=res.domain")
						eval("userDomain=res.userDomain")
						if (res.title != undefined) {
							document.title = res.title
						}
						$import("app.viewport.MyDesktop", "app.viewport.App")
						if (res.myDesktop) {
							mainCfg.myDesktop = res.myDesktop
							$import(res.myDesktop)
						} else {// 基层平台默认desktop
							var defaultDesktop = "platform.script.viewport.MyDesktop";
							mainCfg.myDesktop = defaultDesktop
							$import(defaultDesktop)
						}

						mainApp = new app.viewport.App(mainCfg)
					}
				})
			})// require
})
