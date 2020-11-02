$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicHospitalEHR = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicHospitalEHR.superclass.constructor.apply(
			this, [cfg]);
}

/*******************************************************************************
 * 【浦口】EHR(大数据健康档案浏览器)页面内嵌HIS zhaojian 2017-10-25
 */
Ext.extend(phis.application.cic.script.ClinicHospitalEHR,
		phis.script.SimpleModule, {
			initPanel : function() {
					var panel = new Ext.Panel({
						html : "<iframe id='ehr_frame' width='100%' height='100%'></iframe>"
					});
					this.panel = panel;
					panel.on("afterrender", this.onReady, this)
					return panel;
			},
			onReady : function() {
				debugger;
				if(this.exContext.empiData.idCard==""||this.exContext.empiData.idCard.length!=18)
				{						
					Ext.Msg.alert("提示", "未查询到该病人的身份证号码！");
					window.close();
					return;
				}
				var params_array = [{
					name : "idcard",
					value : this.exContext.empiData.idCard
				}, {
					name : "sys_organ_code",
					value : this.mainApp['phisApp'].deptId
				}, {
					name : "sys_code",
					value : "his"
				}, {
					name : "opeCode",
					value : this.mainApp.uid
				}, {
					name : "opeName",
					value : this.mainApp.uname
				}];
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "DESedeService",
					serviceAction : "GetDesInfo",
					params : JSON.stringify(params_array)
				});
				if (res.code > 300) {
					return false;
				} else {
					this.openBHRView(this.panel, res.json, "getPersonInfo");
				}
			}
		});