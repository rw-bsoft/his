$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.SyndromeDifferentiationAndRreatment = function(cfg) {
	phis.application.tcm.script.SyndromeDifferentiationAndRreatment.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * 【中医馆】辨证论治
 */
Ext.extend(phis.application.tcm.script.SyndromeDifferentiationAndRreatment,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
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
					Ext.Msg.alert("提示", "中医馆辨证论治无法正常打开，请联系管理员！");
					this.opener.resetTcmSelectItem(0);
					return;
				}
				//获取系统参数：【中医馆】页面免登陆地址 TCM_URL_LOGIN
				var params = this.loadSystemParams({
					"commons" : ['TCM_URL_LOGIN']
				})
				//获取系统参数：【中医馆】辨证论治页面TCM_URL_BZLZ
				var params2 = this.loadSystemParams({
					"commons" : ['TCM_URL_BZLZ']
				})
				if (params.TCM_URL_LOGIN && params.TCM_URL_LOGIN!="null" && params.TCM_URL_LOGIN!="" 
					&& params2.TCM_URL_BZLZ && params2.TCM_URL_BZLZ!="null" && params2.TCM_URL_BZLZ!="") {
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "TcmService",
						serviceAction : "getUrlPageContent",
						body : {
							url : params2.TCM_URL_BZLZ,
							pdata : "resource=AE_EDIT&jsonpCallback=_&orgCode="+tcmjgid+"&chisZggh="+this.mainApp.uid+"&chisEmpName="+this.mainApp.uname+"&clinicId="+this.exContext.ids.clinicId+"&brxm="+this.exContext.empiData.personName+"&sex="+this.exContext.empiData.sexCode+"&patientid="+this.exContext.empiData.BRID+"&birthDate="+this.exContext.empiData.birthday+"&returnHisUrl=",
							timeout : 2000
						}
					});
					if(r.code == 200 && r.json.body.tagUrl){
						var index=r.json.body.tagUrl.lastIndexOf("returnHisUrl=");
						var strObj=r.json.body.tagUrl.substring(index,r.json.body.tagUrl.length);
						tagUrl = r.json.body.tagUrl.replace('_(["','').replace(strObj,"");
						tagUrl = tagUrl.replace(this.mainApp.uname,encodeURIComponent(this.mainApp.uname)).replace(this.exContext.empiData.personName,encodeURIComponent(this.exContext.empiData.personName));
						tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(tagUrl);
					}
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "中医馆辨证论治无法正常打开，请联系管理员！");
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