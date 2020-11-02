$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.DiagnosisContrastHISList = function(cfg) {
	phis.application.tcm.script.DiagnosisContrastHISList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.tcm.script.DiagnosisContrastHISList,
		phis.script.SimpleList, {
			loadData : function() {
				var pydmhis = "%"+Ext.get("pydmhis").getValue().toUpperCase()+"%";
				this.requestData.cnd = ['like', ['$', 'PYDM'], ['s', pydmhis]];
				this.requestData.serviceId = "phis.TcmService";
				this.requestData.serviceAction = "queryDiagnosisContrastHIS";
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 100;
				phis.application.tcm.script.DiagnosisContrastHISList.superclass.loadData
						.call(this)
			}
		});