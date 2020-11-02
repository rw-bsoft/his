$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView", "chis.script.demographicView",
        "chis.script.EHRView",
        "chis.script.area")
chis.application.hr.script.QueryZlls_EHR = function(cfg) {
	this.isModify = false;
	chis.application.hr.script.QueryZlls_EHR.superclass.constructor.apply(
			this, [cfg]);
}

/*******************************************************************************
 * 【浦口】EHR(大数据健康档案浏览器)页面内嵌HIS zhaojian 2017-11-01
 */
Ext.extend(chis.application.hr.script.QueryZlls_EHR,
		chis.script.BizSimpleListView, {
			initPanel : function() {
					var panel = new Ext.Panel({
						html : "<iframe id='ehr_frame' width='100%' height='100%'></iframe>"
					});
					this.panel = panel;
					panel.on("afterrender", this.onReady, this)
					return panel;
			},
			onReady : function() {
				if(this.exContext.empiData.idCard==""||this.exContext.empiData.idCard.length!=18)
				{						
					Ext.Msg.alert("提示", "未查询到该病人的身份证号码！");
					window.close();
					return;
				}
				var params_array = [{
					name : "idcard",
					value : this.exContext.empiData.idCard
				},{name:"sys_organ_code",value:this.mainApp.deptId.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_code",value:"jkda"},{name : "opeCode",value : this.mainApp.uid},{name : "opeName",value : this.mainApp.uname}];
				//调用大数据健康档案浏览器接口服务，跳转html页面  zhaojian 2018-06-26
				util.rmi.jsonRequest({
					serviceId : "chis.desedeService",
					schema : "",
					serviceAction : "getDesInfo",
					method : "execute",
					params : JSON.stringify(params_array)
				}, function(code, msg, json) {
					if (msg == "Success") {
						this.openBHRView( json, "getPersonInfo",this.panel);             		
					} else {
						Ext.Msg.alert("提示", "操作失败");
						return false;
					}
				}, this)
			}
		});