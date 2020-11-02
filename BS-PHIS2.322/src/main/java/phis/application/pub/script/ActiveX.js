$package("phis.application.pub.script")
$import("phis.script.widgets.MyMessageTip")
phis.application.pub.script.ActiveX = {
	initHtmlElement:function(id,clsId){
		var html = "<object ProgID='{0}' id='{1}'  TYPE='application/x-itst-activex' BORDER='0' WIDTH='0' HEIGHT='0' clsid='{{2}}'> </object>"
		html=String.format(html,id,id,clsId);	
		var ele = document.createElement("div")
		ele.innerHTML = html;
		document.body.appendChild(ele);
	},
	getDom:function(id){
		if(!id){
			id='ControlBsoftCard';
		}
		if (window.document[id]) {
			return window.document[id];
		}
		if (Ext.isIE) {
			if (document.embeds && document.embeds[id])
			  return document.embeds[id]; 
		} 
		else {
			return document.getElementById(id);
		}
	},
	getControl:function(id,clsId){
		var control=this.getDom(id);
		if(!control){
			this.initHtmlElement(id, clsId);
			control=this.getDom(id);
		}
		return control;
	}
}