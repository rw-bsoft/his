$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.KnowledgeBase = function(cfg) {
	phis.application.tcm.script.KnowledgeBase.superclass.constructor.apply(
			this, [cfg]);
}
/***
 * 【中医馆】知识库
 */
Ext.extend(phis.application.tcm.script.KnowledgeBase,
		phis.script.SimpleModule, {
			initPanel : function() {
debugger;				
				if (this.panel)
					return this.panel;					
				var tagUrl = "";
				//获取系统参数：【中医馆】页面免登陆地址 TCM_URL_LOGIN
				var params = this.loadSystemParams({
					"commons" : ['TCM_URL_LOGIN']
				})
				//获取系统参数：【中医馆】知识库页面TCM_URL_DZBL
				var params2 = this.loadSystemParams({
					"commons" : ['TCM_URL_ZSK']
				})
				if (params.TCM_URL_LOGIN && params.TCM_URL_LOGIN!="null" && params.TCM_URL_LOGIN!="" 
					&& params2.TCM_URL_ZSK && params2.TCM_URL_ZSK!="null" && params2.TCM_URL_ZSK!="") {
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "TcmService",
						serviceAction : "getUrlPageContent",
						body : {
							url : params2.TCM_URL_ZSK,
							pdata : "resource=K_LOOK",
							timeout : 2000
						}
					});
					if(r.code == 200 && r.json.body.tagUrl){
						//tagUrl = r.json.body.tagUrl;
						var index=r.json.body.tagUrl.lastIndexOf("get-lt=true");
						var strObj=r.json.body.tagUrl.substring(index,r.json.body.tagUrl.length);
						tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(r.json.body.tagUrl.replace(strObj,"get-lt=true"));
					}
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "中医馆知识库无法正常打开，请联系管理员！");
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