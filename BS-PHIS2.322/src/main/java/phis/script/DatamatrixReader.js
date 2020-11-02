$package("phis.script");
//二维码扫码
phis.script.DatamatrixReader = {	
	initHtmlElement:function(){
		var ua = navigator.userAgent.toLowerCase();
		check = function(r){
			return r.test(ua);  
		}
		version = function (is, regex) {
			var m;
			return (is && (m = regex.exec(ua))) ? parseFloat(m[1]) : 0;
		}
		var isOpera = check(/opera/);
		var isWebKit = check(/webkit/);
		var isChrome = check(/\bchrome\b/);
		var isSafari = !isChrome && check(/safari/);
		var isIE = !isOpera && check(/msie/);
		var isIE6 = isIE && check(/msie 6/);
		var isIE11 = check(/rv:11/);
		var isGecko = !isWebKit && check(/gecko/);
		var isFirefox = check(/\bfirefox/);
		
		var html=""
		this.isIE=isIE || isIE11;
		if(this.isIE){
			html='<OBJECT id="DemoActiveX" width="0px" height="0px" CLASSID="CLSID:354C50F1-89F5-4728-B041-76C6F13FFFDE" codebase="DatamatrixDevice.cab"></OBJECT>';
			//html='<OBJECT id="DemoActiveX" classid="clsid:354C50F1-89F5-4728-B041-76C6F13FFFDE" codebase="DatamatrixDevice.cab"></OBJECT>';
		}else{
			html='<object id="DemoActiveX" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{354C50F1-89F5-4728-B041-76C6F13FFFDE}" codebase="DatamatrixDevice.cab"></object>';
		}
		var node = document.getElementById("DemoActiveX");
		if (node) {
			node.parentNode.removeChild(node);
		}
		var ele = document.createElement("div");
		ele.setAttribute("width","0px")
		ele.setAttribute("height","0px")
		ele.innerHTML = html;
		document.body.appendChild(ele);
		document.getElementById("DemoActiveX").style.visibility = "hidden";
	},
	SDevice: function (){
        try {
			var obj = document.DemoActiveX;
			if (obj) {
				obj.style.visibility = "hidden";
				obj.SDevice();
			} else {
				return "设备启动失败，请重新连接设备后再试！(SDevice_newobj)";
			}
		} catch (ex) {
			return "设备启动失败，请重新连接设备后再试！(SDevice_catch)";
		}  
	},
	WaitingSaoma: function (){
        try {
			var obj = document.DemoActiveX;
			if (obj) {
				obj.style.visibility = "hidden";
				var msg = obj.WaitingSaoma();
				if(msg.length>0){
					return msg;
				}
				return "";
			} else {
				return "设备启动失败，请重新连接设备后再试！(WaitingSaoma_newobj)";
			}
		} catch (ex) {
			return "设备启动失败，请重新连接设备后再试！(WaitingSaoma_catch)";
		}  
	},
	Getdecodestring: function (){
		try {
			var obj = document.DemoActiveX;
			if (obj) {
				obj.style.visibility = "hidden";
				var msg = obj.Getdecodestring();
				if(msg.length==18){
					return msg;
				}else if(msg=="未获取到付款码信息，请重新扫码！"){
					return "";
				}
			} else {
				return "扫码设备初始化失败，请检查扫码设备是否正常！(Getdecodestringa_newobj)";
			}
		} catch (ex) {
				return "扫码设备初始化失败，请检查扫码设备是否正常！Getdecodestring_catch)";
		}
	},
	CloseSaoma: function (){
		try {
			var obj = document.DemoActiveX;
			if (obj) {
				obj.style.visibility = "hidden";
				obj.CloseSaoma();
			} else {
				return "Object is not created!";
			}
		} catch (ex) {
			return "Some error happens, error message is: " + ex.Description;
		}
	},
	GetIpAddressAndHostname: function (){
		try {
			var obj = document.DemoActiveX;
			if (obj) {
				obj.style.visibility = "hidden";
				return obj.GetIpAddressAndHostname();
			} else {
				return "";
			}
		} catch (ex) {
			return "";
		}
	}
}