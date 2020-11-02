$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.Telemedicine = function(cfg) {
	phis.application.tcm.script.Telemedicine.superclass.constructor.apply(
			this, [cfg]);
}
/***
 * 【中医馆】远程会诊
 */
Ext.extend(phis.application.tcm.script.Telemedicine,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				Ext.Msg.alert("提示", "中医馆远程会诊暂未开放......");
				this.opener.resetTcmSelectItem(0);
				return;	
					
				var tagUrl = "";
				var tcmjgid = "";
				//获取当前门诊医生所在机构对应的省中医馆平台机构编码
				var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "TcmService",
						serviceAction : "getTcmJgid",
						body : {
						}
				});
				if(r.code == 200 && r.json.body.tcmjgid){
					tcmjgid = r.json.body.tcmjgid;
				}
				if(tcmjgid==""){
					Ext.Msg.alert("提示", "中医馆远程会诊无法正常打开，请联系管理员！");					
					this.opener.resetTcmSelectItem(0);
					return;
				}
				//获取系统参数：【中医馆】URL页面免登陆地址 TCM_URL_LOGIN
				var params = this.loadSystemParams({
					"commons" : ['TCM_URL_LOGIN']
				})
				//获取系统参数：【中医馆】远程会诊页面TCM_URL_YCHZ
				var params2 = this.loadSystemParams({
					"commons" : ['TCM_URL_YCHZ']
				})
				if (params.TCM_URL_LOGIN && params.TCM_URL_LOGIN!="null" && params.TCM_URL_LOGIN!="" 
					&& params2.TCM_URL_YCHZ && params2.TCM_URL_YCHZ!="null" && params2.TCM_URL_YCHZ!="") {
					var brxz = "";
					switch(this.exContext.empiData.BRXZ){
						case '2000':
						case '3000':
							brxz = "2";
						break;
						case '6000':
							brxz = "3";
						break;
						case '1000':
							brxz = "1";
						break;
						default:
							brxz = "1";
					}
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "TcmService",
						serviceAction : "getUrlPageContent",
						body : {
							url : params2.TCM_URL_YCHZ,
							pdata : "openid="+this.mainApp.uid+"&m=test&clz=/eh.bus.web.cloud. wizard.Wizard&patientName="+this.exContext.empiData.personName
							+"&mobile="+this.exContext.empiData.phoneNumber+"&certid="+this.exContext.empiData.idCard+"&gender="+this.exContext.empiData.sexCode+"&patientType="+brxz+"&organCode="+tcmjgid+"&birthday="+this.exContext.empiData.birthday,
							timeout : 2000
						}
					});
					if(r.code == 200 && r.json.body.tagUrl){
						//tagUrl = r.json.body.tagUrl;
						tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(r.json.body.tagUrl);
					}
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "中医馆远程会诊无法正常打开，请联系管理员！");
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