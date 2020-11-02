$package("phis.application.znts.script")

$import("phis.script.SimpleModule")
phis.application.znts.script.HealthRecordBrowser = function(cfg) {
	phis.application.znts.script.HealthRecordBrowser.superclass.constructor.apply(
			this, [cfg]);
}
/*******************************************************************************
 * 【溧水】市EHR(东软健康档案浏览器)页面内嵌HIS zhaojian 2018-10-19
 */
Ext.extend(phis.application.znts.script.HealthRecordBrowser,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl="";
				var pdata ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><url><yljgdm></yljgdm>" +
						"<ysgh>"+this.mainApp.uid+"</ysgh><ysxm>"+this.mainApp.uname+"</ysxm><ksmc>内科</ksmc>" +
						"<ip>127.0.0.1</ip><zjhm>"+this.exContext.empiData.idCard+"</zjhm><hzxm>"+this.exContext.empiData.personName
						+"</hzxm><ywxt>HIS</ywxt></url>";
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.ZntsService",
					serviceAction : "getUrlPageContent",
					body : {
						pdata : pdata,
						timeout : 2000
					}
				});
				if(r.code == 200 && r.json.body.tagUrl){
					tagUrl = r.json.body.tagUrl;
					//var index=r.json.body.tagUrl.lastIndexOf("get-lt=true");
					//var strObj=r.json.body.tagUrl.substring(index,r.json.body.tagUrl.length);
					//tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(r.json.body.tagUrl.replace(strObj,"get-lt=true"));
				}else{
					Ext.Msg.alert("提示", r.json.body.msg);
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "市健康档案浏览器无法正常打开，请联系管理员！");
					this.opener.resetTcmSelectItem(0);
					return;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							html : "<iframe src='"+tagUrl+"'  width='100%' height='100%' frameborder='no'></iframe>",
							frame : false
						});
				this.panel = panel;
				return panel;
			}
		});