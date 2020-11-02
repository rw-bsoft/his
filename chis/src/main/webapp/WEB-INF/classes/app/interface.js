$import("sencha.ext3.ext-base", "sencha.ext3.ext-all",
		"sencha.ext3.ext-lang-zh_CN", "util.rmi.miniJsonRequestSync",
		"app.desktop.Desktop", "app.desktop.TaskManager", "app.desktop.App",
		"chis.application.mpi.script.EMPIInfoModule");

$styleSheet("sencha.ext3.css.ext-all")
$styleSheet("sencha.ext3.css.ext-patch")
$styleSheet("chis.resources.app.desktop.Desktop")
$styleSheet("chis.resources.app.biz.interface");

__docRdy = false;
Ext.onReady(function() {
			if (__docRdy) {
				return;
			}
			__docRdy = true;
			Ext.BLANK_IMAGE_URL = ClassLoader.appRootOffsetPath
					+ "resources/sencha/ext3/images/default/s.gif"
			var argStr = window.location.search.substring(1);
			argStr = base64decode(argStr);
			console.log(argStr);
			var args = argStr.split('&');
			var cfg = {};
			for (var i = 0; i < args.length; i++) {
				var arg = args[i];
				var temp = arg.split('=');
				var s = temp[1].charAt(0);
				var end = temp[1].charAt(temp[1].length);
				if (s != "[" && s != "{" && end != "]" && end != "}") {
					cfg[temp[0]] = temp[1];
				} else {
					cfg[temp[0]] = eval(temp[1]);
				}
			}

			var result = util.rmi.miniJsonRequestSync({
						url : "webServiceLogonController/logon",
						uid : cfg["uid"],
						psw : cfg["pass"],
						urole : cfg["role"]
					});
			if (result.code != 200) {
				Ext.Msg.alert("错误", "登录失败：" + result.msg);
				return;
			}
			if (cfg.ref) {
				var moduleCfg = util.rmi.miniJsonRequestSync({
							url : 'app/loadModule',
							id : cfg.ref
						})
				Ext.apply(cfg, moduleCfg.json.body);
				Ext.apply(cfg, moduleCfg.json.body.properties);
			}
			var exContext = {
				childrenRegisterAge : result.json.res.childrenRegisterAge,
				childrenDieAge : result.json.res.childrenDieAge,
				oldPeopleAge : result.json.res.oldPeopleAge,
				hypertensionMode : result.json.res.hypertensionMode,
				diabetesMode : result.json.res.diabetesMode,
				pregnantMode : result.json.res.pregnantMode,
				oldPeopleMode : result.json.res.oldPeopleMode,
				diabetesPrecedeDays : result.json.res.diabetesPrecedeDays,
				diabetesDelayDays : result.json.res.diabetesDelayDays,
				areaGridType : result.json.res.areaGridType,
				areaGridShowType : result.json.areaGridShowType,
				healthCheckType : result.json.res.healthCheckType,
				hypertensionType : result.json.res.hypertensionType,
				diabetesType : result.json.res.diabetesType,
				psychosisType : result.json.res.psychosisType,
				postnatal42dayType : result.json.res.postnatal42dayType,
				postnatalVisitType : result.json.res.postnatalVisitType,
				oldPeopleStartMonth : result.json.res.oldPeopleStartMonth || '01',
				hypertensionStartMonth : result.json.res.hypertensionStartMonth || '01',
				hypertensionRiskStartMonth : result.json.res.hypertensionRiskStartMonth ||'01',
				diabetesStartMonth : result.json.res.diabetesStartMonth || '01',
				diabetesRiskStartMonth : result.json.res.diabetesRiskStartMonth || '01',
				psychosisStartMonth :result.json. res.psychosisStartMonth || '01',
				tumourHighRiskStartMonth : result.json.res.tumourHighRiskStartMonth || '01',
				tumourPatientVisitStartMonth : result.json.res.tumourPatientVisitStartMonth || '01'
			};
			var mainCfg = {
				title : result.json.res.title,
				uid : result.json.userRoleToken.userId,
				uname : result.json.userRoleToken.userName,
				dept : result.json.userRoleToken.manageUnitName,
				deptId : result.json.userRoleToken.manageUnitId,
				jobtitle : result.json.userRoleToken.roleName,
				jobId : result.json.userRoleToken.roleId,
				urt : result.json.userRoleToken.id,
				regionCode : result.json.userRoleToken.regionCode,
				regionText : result.json.userRoleToken.regionText,
				serverDate : result.json.res.serverDate,
				serverDateTime : result.json.res.serverDateTime,
				fds:result.json.res.fds,
				phisActiveYW : result.json.res.phisActiveYW,
				phisActive : result.json.res.phisActive,
				exContext : exContext
			}
			this.mainApp = new app.desktop.App(mainCfg);
			cfg["mainApp"] = this.mainApp;

			// 如果社区有数据，不弹基本信息。
			var query = util.rmi.miniJsonRequestSync({
						serviceId : "chis.empiService",
						serviceAction : "empiIdExists",
						method : "execute",
						body : {
							empiId : cfg.empiId
						}
					});
			if (query.code != 200) {
				Ext.Msg.alert("错误", "empiId查询失败：" + query.msg);
				return;
			}
			if (query.json.body.exists == true
					|| query.json.body.exists == "true") {
				var cls = cfg["cls"];
				var pageWidth = document.documentElement.clientWidth;
				var pageHeight = document.documentElement.clientHeight;
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)");
									var size = m.getWin().getSize();
									var x = (pageWidth - size.width) / 2;
									var y = (pageHeight - size.height) / 2;
									m.getWin().setPosition(x, y);
									m.getWin().show();
								}, this]);
				return;
			}
			// 如果社区中没的数据，弹出窗口要求补全
			m = new chis.application.mpi.script.EMPIInfoModule({
						entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
			m.on("onEmpiReturn", function(body) {
						var cls = cfg["cls"];
						cfg["empiId"] = body.empiId;
						var pageWidth = document.documentElement.clientWidth;
						var pageHeight = document.documentElement.clientHeight;
						$require(cls, [function() {
											var m = eval("new " + cls + "(cfg)");
											var size = m.getWin().getSize();
											var x = (pageWidth - size.width)
													/ 2;
											var y = (pageHeight - size.height)
													/ 2;
											m.getWin().setPosition(x, y);
											m.getWin().show();
										}, this]);
					}, this);
			var win = m.getWin();
			win.setPosition(250, 100);
			win.show();
			m.interfaceInvoke(cfg.empiId)
		});

var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
		63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
		20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
		32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
		50, 51, -1, -1, -1, -1, -1);

function openModule(cfg) {
	var cls = cfg["cls"];
	var pageWidth = document.documentElement.clientWidth;
	var pageHeight = document.documentElement.clientHeight;
	$require(cls, [function() {
						var m = eval("new " + cls + "(cfg)");
						var size = m.getWin().getSize();
						var x = (pageWidth - size.width) / 2;
						var y = (pageHeight - size.height) / 2;
						m.getWin().setPosition(x, y);
						m.getWin().show();
					}, this]);
}

// Base64解码
function base64decode(str) {
	var c1, c2, c3, c4;
	var i, len, out;

	len = str.length;
	i = 0;
	out = "";
	while (i < len) {
		/* c1 */
		do {
			c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c1 == -1);
		if (c1 == -1) {
			break;
		}
		/* c2 */
		do {
			c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c2 == -1);
		if (c2 == -1) {
			break;
		}
		out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));

		/* c3 */
		do {
			c3 = str.charCodeAt(i++) & 0xff;
			if (c3 == 61) {
				return out;
			}
			c3 = base64DecodeChars[c3];
		} while (i < len && c3 == -1);
		if (c3 == -1) {
			break;
		}
		out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));

		/* c4 */
		do {
			c4 = str.charCodeAt(i++) & 0xff;
			if (c4 == 61) {
				return out;
			}
			c4 = base64DecodeChars[c4];
		} while (i < len && c4 == -1);
		if (c4 == -1) {
			break;
		}
		out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
	}
	return out;
}