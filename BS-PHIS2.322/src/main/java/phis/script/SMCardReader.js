$package("phis.script");

phis.script.SMCardReader = {
	
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
	//		html='<OBJECT id="GetCardInfo" name="GetCardInfo" width="0px" height="0px" CLASSID="CLSID:57DB97AB-F6A1-4a91-A1B7-234758257AB4"></OBJECT>'
			html='<OBJECT id="GetCardInfo" name="GetCardInfo" width="0px" height="0px" CLASSID="CLSID:E61D928F-76D0-49F5-B80A-529DB3AD716B"></OBJECT>'
		}else{
	//		html='<object id="GetCardInfo" ALIGN="baseline" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{C393D515-609F-41B1-AB66-736358D547EE}"></object>';
			html='<object id="GetCardInfo" ALIGN="baseline" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{E61D928F-76D0-49F5-B80A-529DB3AD716B}"></object>';
		}
		var node = document.getElementById("GetCardInfo");
		if (node) {
			node.parentNode.removeChild(node);
			}
		var ele = document.createElement("div");
		ele.setAttribute("width","0px")
		ele.setAttribute("height","0px")
		ele.innerHTML = html;
		document.body.appendChild(ele);
	},
	readCard: function (){
		document.getElementById("GetCardInfo").style.visibility = "hidden";
        var activeX = document.getElementById("GetCardInfo");
//        var card="320100D1560000050744799101020304;3;20110803;532122198108021643;艾清美;2;1002327377;8007447991;"
         var card="";
//        if(this.isIE){
//       		card=activeX.LinkReaderPro();
//        }else{
//        	card=activeX.PK_LinkReaderPro();
//        }
        
        var ret = activeX.readICCard();
        var resL = ret.split("|")[0];
        var res = ret.split("|")[1];
        if(resL>0){
        	if(res.indexOf(":")>-1){
	        	card=res.split(":")[1];
        	}else{
        		card=res;
        	}
        }
//		alert("cardInfo:"+card);
        return card;
	},
	readOldCard: function (){
		document.getElementById("GetCardInfo").style.visibility = "hidden";
        var activeX = document.getElementById("GetCardInfo");
        var card=""
        if(this.isIE){
       		card=activeX.LinkReaderPro();
        }else{
        	card=activeX.ReadOldCard();
        	if(card.length >52){
        		card=card.substring(34,18)
        	}
        }
        return card;
	}
}