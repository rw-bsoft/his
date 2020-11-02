$package("phis.script")


phis.script.HealthCardReader = {
	initHtmlElement:function(){
		var ua = navigator.userAgent.toLowerCase();
		check = function(r){               
			return r.test(ua);  
		};
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
		var isGecko = !isWebKit && check(/gecko/);
		var isFirefox = check(/\bfirefox/);
		var html=""
		this.isIE=isIE;
		if(isIE){
			html='<OBJECT id="HealthCardInfo" name="HealthCardInfo" width="0px" height="0px" CLASSID="CLSID:7EADA96E-0B77-48AA-AD08-7551A7C63B6D"></OBJECT>'
		}else{
			html='<object id="HealthCardInfo" ALIGN="baseline" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{7EADA96E-0B77-48AA-AD08-7551A7C63B6D}"></object>';
		}
		var node = document.getElementById("HealthCardInfo");
		if (node) {
			node.parentNode.removeChild(node);
		}
		var ele = document.createElement("div");
		ele.setAttribute("width","0px")
		ele.setAttribute("height","0px")
		ele.innerHTML = html;
		document.body.appendChild(ele);
		document.getElementById("HealthCardInfo").style.visibility = "hidden";
	},
	initCard : function(){//读写设备初始化：签到
        var activeX = document.getElementById("HealthCardInfo");
	    return activeX.readCard("911^000000^001|张三|");
	},
	readCardInfo: function (){//读卡信息
        var activeX = document.getElementById("HealthCardInfo");
        return activeX.readCard("131^000000^");
	},
	OffCardRead : function(){//签退
		 var activeX = document.getElementById("HealthCardInfo");
        return activeX.readCard("912^000000^");
	}
}