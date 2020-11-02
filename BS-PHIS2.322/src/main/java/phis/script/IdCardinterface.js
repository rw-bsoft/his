﻿$package("phis.script");
phis.script.IdCardinterface={
	// 页面加入对象
	addIdCardObject:function(){
		var ua=navigator.userAgent.toLowerCase();
		check=function(r){
			return r.test(ua);
		};
		version=function(is,regex){
			var m;
			return (is&&(m=regex.exec(ua)))?parseFloat(m[1]):0;
		}
		var isOpera=check(/opera/);
		var isWebKit=check(/webkit/);
		var isChrome=check(/\bchrome\b/);
		var isSafari=!isChrome&&check(/safari/);
		var isIE=!isOpera&&check(/msie/);
		var isIE6=isIE&&check(/msie 6/);
		var isGecko=!isWebKit&&check(/gecko/);
		var isFirefox=check(/\bfirefox/);
		var html="";
		if(isFirefox ||isSafari){
			isIE=false;
		}else{
			isIE=true;
		}
		this.isIE=isIE;
		if(isIE){
			html='<OBJECT id="IDCARDOBJ" name="IDCARDOBJ" width="0px" height="0px" classid="clsid:6236993F-2C67-47FB-B5DE-A387F98970BF"></OBJECT>';
		}else{
			html='<object id="IDCARDOBJ" ALIGN="baseline" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{6236993F-2C67-47FB-B5DE-A387F98970BF}" progid="sfzdk.SDevice#version=1,0,0,0"></object>';
		}
		var node=document.getElementById("IDCARDOBJ");
		if(node){
			node.parentNode.removeChild(node);
		}
		var ele=document.createElement("div");
		ele.setAttribute("width","0px");
		ele.setAttribute("height","0px");
		ele.innerHTML=html;
		document.body.appendChild(ele);
	}
	,getIdCardInfo:function(){
		var ac=document.getElementById("IDCARDOBJ");
		var st="";
		try{
			var rh = ac.getICCinfo();
			st+=rh;
			if(st.length<20){
				st="";
			}
		}catch(e){
			alert("读卡未成功，请检查是否安装控件和动态库");
			return "";
		}
		return st;
	}
}