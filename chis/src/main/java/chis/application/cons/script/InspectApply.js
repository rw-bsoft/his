$package("chis.application.cons.script")

$import("chis.script.BizModule")
chis.application.cons.script.InspectApply = function(cfg) {
	chis.application.cons.script.InspectApply.superclass.constructor.apply(
			this, [cfg]);
}
/*******************************************************************************
 * 远程开单申请(大数据接口)页面内嵌HIS hujian 2020-03-04
 */
Ext.extend(chis.application.cons.script.InspectApply,
		chis.script.BizModule, {
			initPanel : function() {
				debugger;
				
				if (this.panel)
					return this.panel;
				var tagUrl="";
				var pdata ="jobNumber="+this.mainApp.uid//人员标识
				+"&organizCode="+this.mainApp.deptId//机构编码
				+"&token=" //权限标识
				+"&inspectionType=1"//检查类型默认B超：1：心电 2：放射 3:B超
				+"&visitSign="+(this.mainApp.jobId=="phis.51"?2:1)//门诊住院标识 1:门诊 2住院
				+"&patientName="+this.exContext.empiData.personName//姓名
				+"&patientCardNumber="+this.exContext.empiData.idCard//手机号码
				+"&patientId="+this.exContext.empiData.BRID//病人id
				+"&jzxh="+this.exContext.ids.clinicId
				+"&applyDepartmentCode="+this.mainApp.departmentId;//科室代码
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.drApplyService",
					serviceAction : "getPageUrl_HTTPPOST",
					method:"execute",
					body : {
						pdata : pdata,
						serviceurl : "inspectReceiptForm"//服务地址
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