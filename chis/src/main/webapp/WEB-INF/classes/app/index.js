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
					"chis.script.ChisLogon", "app.desktop.plugins.LogonWin"],
			function() {
				$logon = new chis.script.ChisLogon({
							forConfig : true
						})
				$logon.on("appsDefineLoaded", function(userRoleToken, res) {
							// add by yangl 修正提示信息不出现的问题
							if (document.getElementById("msg-div22")) {
								msgCt = null;
							}
							var body = Ext.get(document.body)
							body.update("")
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
								var exContext = {
									childrenRegisterAge : res.childrenRegisterAge,
									childrenDieAge : res.childrenDieAge,
									oldPeopleAge : res.oldPeopleAge,
									hypertensionMode : res.hypertensionMode,
									diabetesMode : res.diabetesMode,
									pregnantMode : res.pregnantMode,
									oldPeopleMode : res.oldPeopleMode,
									diabetesPrecedeDays : res.diabetesPrecedeDays,
									diabetesDelayDays : res.diabetesDelayDays,
									areaGridType : res.areaGridType,
									areaGridShowType : res.areaGridShowType,
									healthCheckType : res.healthCheckType,
									hypertensionType : res.hypertensionType,
									diabetesType : res.diabetesType,
									childrenCheckupType : res.childrenCheckupType,
									psychosisType : res.psychosisType,
									debilityShowType:res.debilityShowType,
									postnatal42dayType : res.postnatal42dayType,
									postnatalVisitType : res.postnatalVisitType,
									KLX : res.KLX,
									oldPeopleStartMonth : res.oldPeopleStartMonth || '01',
									hypertensionStartMonth : res.hypertensionStartMonth || '01',
									hypertensionRiskStartMonth : res.hypertensionRiskStartMonth ||'01',
									diabetesStartMonth : res.diabetesStartMonth || '01',
									diabetesRiskStartMonth : res.diabetesRiskStartMonth || '01',
									psychosisStartMonth : res.psychosisStartMonth || '01',
									tumourHighRiskStartMonth : res.tumourHighRiskStartMonth || '01',
									tumourPatientVisitStartMonth : res.tumourPatientVisitStartMonth || '01'
								};

								var mainCfg = {
									title : res.title,
									uid : userRoleToken.userId,
									urt : userRoleToken.id,
									userPhoto : res.userPhoto,
									logonName : userRoleToken.userName,
									uname : userRoleToken.userName,
									dept : userRoleToken.manageUnitName,
									deptId : userRoleToken.manageUnitId,
									deptRef : userRoleToken.manageUnit.ref,
									jobtitle : userRoleToken.roleName,
									jobtitleId : userRoleToken.roleId,
									jobId : userRoleToken.roleId,
									jobType : res.roleType,
									regionCode : userRoleToken.regionCode,
									// regionText : res.regionText,
									mapSign : res.mapSign,
									apps : apps,
									pageCount : res.pageCount,
									myPage : myPage,
									appwelcome : res.appwelcome,
									tabnum : res.tabNumber,
									banner : res.banner,
									sysMessage : res.sysMessage,
									tabRemove : res.tabRemove,
									serverDate : res.serverDate,
									serverDateTime : res.serverDateTime,
									fds : res.fds,
									phisActiveYW : res.phisActiveYW,
									phisActive : res.phisActive,
									centerUnit:res.centerUnit,
									centerUnitName:res.centerUnitName,
									exContext : exContext
								}
								// eval("globalDomain=res.domain")
								globalDomain = "platform"
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
