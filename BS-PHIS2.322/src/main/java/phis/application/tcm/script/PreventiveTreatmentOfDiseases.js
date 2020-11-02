$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.PreventiveTreatmentOfDiseases = function(cfg) {
	phis.application.tcm.script.PreventiveTreatmentOfDiseases.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * 【中医馆】治未病
 */
Ext.extend(phis.application.tcm.script.PreventiveTreatmentOfDiseases,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl = "";
				if(this.exContext.empiData.idCard==""){
					Ext.Msg.alert("提示", "病人没有身份证号，无法使用【中医馆】治未病！");
					this.opener.resetTcmSelectItem(0);
					return;
				}
				//获取系统参数：【中医馆】页面免登陆地址 TCM_URL_LOGIN
				var params = this.loadSystemParams({
					"commons" : ['TCM_URL_LOGIN']
				})
				//获取系统参数：【中医馆】治未病页面TCM_URL_ZWB
				var params2 = this.loadSystemParams({
					"commons" : ['TCM_URL_ZWB']
				})
				if (params.TCM_URL_LOGIN && params.TCM_URL_LOGIN!="null" && params.TCM_URL_LOGIN!="" 
					&& params2.TCM_URL_ZWB && params2.TCM_URL_ZWB!="null" && params2.TCM_URL_ZWB!="") {
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "TcmService",
						serviceAction : "getUrlPageContent",
						body : {
							url : params2.TCM_URL_ZWB,
							pdata : "resource=HEAL_CHECKBODY&CertificatesNumber="+this.exContext.empiData.idCard+"&CertificatesType=01",
							timeout : 2000
						}
					});
					if(r.code == 200 && r.json.body.tagUrl){
						//tagUrl = r.json.body.tagUrl;
						var index=r.json.body.tagUrl.lastIndexOf("CertificatesType=01");
						var strObj=r.json.body.tagUrl.substring(index,r.json.body.tagUrl.length);
						tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(r.json.body.tagUrl.replace(strObj,"CertificatesType=01"));
					}
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "中医馆治未病无法正常打开，请联系管理员！");
					this.opener.resetTcmSelectItem(0);
					return;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							html : "<iframe src='"+ tagUrl +"'  width='100%' height='100%' frameborder='no'></iframe>",
							frame : false
						});
				this.panel = panel;
				return panel;
			}
		});