$import("org.ext.ext-base", "org.ext.ext-all", "org.ext.ext-lang-zh_CN",
		"util.rmi.miniJsonRequestSync", "app.desktop.Desktop",
		"app.desktop.TaskManager", "app.desktop.App","chis.application.mpi.script.EMPIInfoModule");

$styleSheet("ext.ext-all");
$styleSheet("ext.ext-patch");
$styleSheet("app.desktop.Desktop")
$styleSheet("app.biz.interface");

__docRdy = false;
Ext.onReady(function() {
	if (__docRdy) {
		return;
	}
	__docRdy = true;
	Ext.BLANK_IMAGE_URL = ClassLoader.appRootOffsetPath + "resources/s.gif";
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
		serviceId : "chis.webServiceLogon",
		forConfig : true,
		deep : false,
		uid : cfg["uid"],
		psw : cfg["pass"],
		urole : cfg["role"],
		empiId:cfg.empiId||"",
		replace : cfg["replace"]
	});
	if (result.code != 200) {
		Ext.Msg.alert("错误", "登录失败：" + result.msg);
		return;
	}
	
	var ref = cfg.ref;
	if (ref) {
		var mcl = util.rmi.miniJsonRequestSync({
					serviceId : "moduleConfigLocator",
					id : ref
				});
		if (mcl.code == 200) {
			Ext.apply(cfg, mcl.json.body);
		}
	}
	
	var exContext = {
		childrenRegisterAge : result.json.childrenRegisterAge,
		childrenDieAge : result.json.childrenDieAge,
		oldPeopleAge : result.json.oldPeopleAge,
		hypertensionMode : result.json.hypertensionMode,
		diabetesMode : result.json.diabetesMode,
		pregnantMode : result.json.pregnantMode,
		diabetesPrecedeDays : result.json.diabetesPrecedeDays,
		diabetesDelayDays : result.json.diabetesDelayDays,
		areaGridType : result.json.areaGridType,
		diabetesType : result.jso.diabetesType,
		hypertensionType : result.json.hypertensionType,
		healthCheckType : result.json.healthCheckType,
		postnatal42dayType : result.json.postnatal42dayType,
		postnatalVisitType : result.json.postnatalVisitType
	}; 
	var mainCfg = {
		title : result.json.title,
		uid : result.json.userId,
		uname : result.json.userName,
		dept : result.json.dept,
		deptId : result.json.deptId,
		jobtitle : result.json.jobtitle,
		topManage : result.topManage,
		serverDate : result.json.serverDate,
		apps : [],
		modules : [],
		exContext : exContext
	};
	this.mainApp = new app.desktop.App(mainCfg);
	cfg["mainApp"] = this.mainApp;
	
	//如果社区有数据，不弹基本信息。
	var query = util.rmi.miniJsonRequestSync({
		serviceId : "chis.empiService",
		serviceAction:"empiIdExists",
		body:{empiId:cfg.empiId}
	});
	if (query.code != 200) {
		Ext.Msg.alert("错误", "empiId查询失败：" + query.msg);
		return;
	}
	if(query.json.body.exists == true || query.json.body.exists=="true"){
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
		return ;
	}
	//如果社区中没的数据，弹出窗口要求补全
	m = new chis.application.mpi.script.EMPIInfoModule({
				entryName : "MPI_DemographicInfo",
				title : "个人基本信息查询",
				height : 450,
				modal : true,
				mainApp : this.mainApp
			});
	m.on("onEmpiReturn", function (body){
		var cls = cfg["cls"];
		cfg["empiId"] = body.empiId;
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
	},this);
	var win = m.getWin();
	win.setPosition(250, 100);
	win.show();
	m.interfaceInvoke(cfg.empiId)
	alert(cfg.empiId)
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
		
function openModule(cfg){
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