
$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.ZyDiagnosisContrastTCMList = function(cfg) {
	phis.application.tcm.script.ZyDiagnosisContrastTCMList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.tcm.script.ZyDiagnosisContrastTCMList,
		phis.script.SimpleList, {
			loadData : function() {
				var pydmtcm = "%"+Ext.get("pydmtcm").getValue().toUpperCase()+"%";
				this.requestData.cnd = ['like', ['$', 'PYDM'], ['s', pydmtcm]];
				this.requestData.serviceId = "phis.TcmService";
				this.requestData.serviceAction = "queryZyDiagnosisContrastTCM";
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 100;
				phis.application.tcm.script.ZyDiagnosisContrastHISList.superclass.loadData
						.call(this)
			}
		});