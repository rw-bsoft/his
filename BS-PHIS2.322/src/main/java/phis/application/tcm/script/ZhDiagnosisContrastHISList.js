$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.ZhDiagnosisContrastHISList = function(cfg) {
	phis.application.tcm.script.ZhDiagnosisContrastHISList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.tcm.script.ZhDiagnosisContrastHISList,
		phis.script.SimpleList, {
			loadData : function() {
				var pydmhis = "%"+Ext.get("pydmhis").getValue().toUpperCase()+"%";
				this.requestData.cnd = ['like', ['$', 'PYDM'], ['s', pydmhis]];
				this.requestData.serviceId = "phis.TcmService";
				this.requestData.serviceAction = "queryZhDiagnosisContrastHIS";
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 100;
				phis.application.tcm.script.ZhDiagnosisContrastHISList.superclass.loadData
						.call(this)
			}
		});