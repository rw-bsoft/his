
$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.ZhDiagnosisContrastTCMList = function(cfg) {
	phis.application.tcm.script.ZhDiagnosisContrastTCMList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.tcm.script.ZhDiagnosisContrastTCMList,
		phis.script.SimpleList, {
			loadData : function() {
				var pydmtcm = "%"+Ext.get("pydmtcm").getValue().toUpperCase()+"%";
				this.requestData.cnd = ['like', ['$', 'PYDM'], ['s', pydmtcm]];
				this.requestData.serviceId = "phis.TcmService";
				this.requestData.serviceAction = "queryZhDiagnosisContrastTCM";
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 100;
				phis.application.tcm.script.ZhDiagnosisContrastHISList.superclass.loadData
						.call(this)
			}
		});