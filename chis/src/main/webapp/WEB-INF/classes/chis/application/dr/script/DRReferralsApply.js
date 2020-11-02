$package("chis.application.dr.script")

$import("chis.script.BizModule")
chis.application.dr.script.DRReferralsApply = function(cfg) {
	chis.application.dr.script.DRReferralsApply.superclass.constructor.apply(
			this, [cfg]);
}
/*******************************************************************************
 * 转诊申请(大数据接口)页面内嵌HIS zhaojian 2019-08-08
 */
Ext.extend(chis.application.dr.script.DRReferralsApply,
		chis.script.BizModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl="";
				var pdata ="jobNumber="+this.mainApp.uid//人员标识
				+"&organizCode="+this.mainApp.deptId//机构编码
				+"&token=" //权限标识
				+"&serialNumber="+(this.mainApp.jobId=="phis.51"?this.exContext.empiData.ZYH:this.exContext.empiData.cardNo)//就诊序列号
				+"&visitSign="+(this.mainApp.jobId=="phis.51"?2:1)//门诊住院标识 1:门诊 2住院
				+"&patientName="+this.exContext.empiData.personName//姓名
				+"&idCard="+this.exContext.empiData.idCard//身份证号
				+"&mobile="+this.exContext.empiData.mobileNumber//手机号码
				+"&area="//所在地区使用逗号”,”隔开,如44,4402,440204,字典area.dic
				+"&address="+this.exContext.empiData.address//居住地址
				+"&diseaseDescription="//病情描述
				+"&diseaseSummary="//病情摘要
				+"&businessType="+(this.mainApp.jobId=="phis.51"?2:1);//转诊类型 1：门诊；2：住院；3：检查
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.drApplyService",
					serviceAction : "getPageUrl_HTTPPOST",
					method:"execute",
					body : {
						pdata : pdata,
						serviceurl : "patientForm"//服务地址
					}
				});
				if(result.code == 200 && result.json.tagUrl){
					tagUrl = result.json.tagUrl;
				}else{
					this.emrview.mainTab.el.unmask();
					Ext.Msg.alert("提示", result.msg+"请联系管理员！");
					return;
				}
				if(tagUrl==""){
					this.emrview.mainTab.el.unmask();
					Ext.Msg.alert("提示", "转诊申请页面无法正常打开，请联系管理员！");
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