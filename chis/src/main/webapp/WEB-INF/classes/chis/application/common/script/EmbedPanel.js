$package("chis.application.common.script")

$import("app.desktop.Module","chis.application.common.script.Base64",
"util.dictionary.DictionaryLoader")

chis.application.common.script.EmbedPanel = function(cfg) {
	chis.application.common.script.EmbedPanel.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.common.script.EmbedPanel,app.desktop.Module, {
	url:null,		// like 'http://172.16.171.250:8081/CTDS-ETL/'
	port:null,		// like '8081'
	appName:null,	// like 'CTDS-ETL'
	param:null,		// like '?uid=jsaon&age=35'
	initPanel : function() {
		if(!this.url){
			var path = window.location.href;
			if(this.appName){
				path = path.replace(/\/([\w_-]+)\//.exec(path)[1], this.appName);
				this.url = path;
			}
			if(this.port){
				path = path.replace(/:([0-9]+)/.exec(path)[1], this.port);
				this.url = path;
			}
		}
		if(this.url && this.param){
			this.url += this.param;
		}
		var uid = mainApp.uid;
		var deptId="" ;
		var data={};
		data.schema="";
		//平台机构编码有改动，故修改取deptId方式
		var thisdeptId=mainApp.deptId;
		if(thisdeptId.length==12 && thisdeptId.substring(9,12)=="001"){
			thisdeptId=thisdeptId.substring(0,9);
		}
		deptId=thisdeptId;
//		var dicName = {
//            id : "chis.dictionary.organization"
//          };
//		var o=util.dictionary.DictionaryLoader.load(dicName)
//		if(!o){
//			alert("组织机构代码字典organization加载失败！")
//		}
//		var di = o.wraper[mainApp.deptId];
//		if(!di){
//			alert("未找到对应的机构对照！");
//			return;
//		}
//		deptId=di.text
		if(deptId.length==4){
			deptId= '';
		}else if(deptId.length<7){
			deptId="qu."+deptId
		}else{
			deptId="hosp."+deptId
		}
		var param = "system@"+deptId+"@hais.r01";
		var base64 = new chis.application.common.script.Base64();
		var baseparam = base64.base64encode(base64.utf16to8(param));
		if(this.url.indexOf("?")>0){
			this.url +="&authorize="+baseparam;
		}else{
			this.url +="?authorize="+baseparam;
		}
		var html;
		if(this.url){
			html = "<iframe src="	+ this.url + " width='100%' height='100%' frameborder='no'></iframe>";
		}else{
//			html = "missing arg: 'url' or 'appName' or 'port'.";
			html = "<img src='image/system.jpg'>";
		}
//		alert(html)
		var panel = new Ext.Panel({
			frame : false,
			autoScroll : true,
			html : html
		})
		this.panel = panel
		return panel
	}
})