$package("chis.application.dr.script")

$import("chis.script.BizModule")
chis.application.dr.script.DRReferralsApplyList = function(cfg) {
	chis.application.dr.script.DRReferralsApplyList.superclass.constructor.apply(
			this, [cfg]);
}
/*******************************************************************************
 * 转诊申请列表(大数据接口)页面内嵌HIS zhaojian 2019-08-08
 */
Ext.extend(chis.application.dr.script.DRReferralsApplyList,
		chis.script.BizModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl="";
				var pdata ="jobNumber="+this.mainApp.uid//人员标识
				+"&organizCode="+this.mainApp.deptId//机构编码
				+"&token=";//权限标识
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.drApplyService",
					serviceAction : "getPageUrl_HTTPPOST",
					method:"execute",
					body : {
						pdata : pdata,
						serviceurl : "sendExchangeManage"//服务地址
					}
				});
				if(result.code == 200 && result.json.tagUrl){
					tagUrl = result.json.tagUrl;
				}else{
					Ext.Msg.alert("提示", result.msg+"请联系管理员！");
					return;
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "转诊申请列表页面无法正常打开，请联系管理员！");
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