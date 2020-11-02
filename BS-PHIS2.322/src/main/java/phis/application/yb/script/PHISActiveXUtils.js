(function(global){
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
	var id = "phisActiveXObject"
	//var codebase = "PHIS.cab";
	var codebase = "phisYBIntf.ocx";
	var html=null
	var sybhtml=null;
	var hzyb2014html = null;
/**	if(isIE){
		html = "<OBJECT ProgID='PHIS.MedicalInsurance' classid='clsid:E28F7E48-2C08-4E42-8860-15681CA3FCD5' width='100' height='100' id='" + id + "' name='" +id+ "' codebase='" + codebase + "'></OBJECT>"
	}
	else {
		html = '<object id="' +id+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{E28F7E48-2C08-4E42-8860-15681CA3FCD5}" codeBaseURL="' + codebase + '"></object>';
	}
**/	
	if(isIE){
		html = "<OBJECT  classid='CLSID:7E076340-AC8C-4BEF-B6EF-27EC69B24EEC' width='0' height='0' style='display:none' id='" + id + "' name='" +id + "'></OBJECT>"
	}
	else {
		html = '<object id="' +id+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{7E076340-AC8C-4BEF-B6EF-27EC69B24EEC}"></object>';
	}
	
	global.$PhisActiveXObjectUtils = {
		initHtmlElement:function(){
//			if(!this.initHtmlElement){
			if(!this.getObject()){
			this.ele = document.createElement("div")
				this.ele.innerHTML = html;
			document.body.appendChild(this.ele);
			}
//			this.initHtmlElement = true;
//			}
		},
		getObject:function(){
			if (window.document[id]) {
				return window.document[id];
			}
			if (isIE) {
				if (document.embeds && document.embeds[id])
				  return document.embeds[id]; 
			} 
			else {
				return document.getElementById(id);
			}
		}
	}
})(this)