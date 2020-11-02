
$package("phis.application.ccl.script");

$import("phis.script.SimpleList")

phis.application.ccl.script.CheckApplyProjectForFee = function(cfg) {
	phis.application.ccl.script.CheckApplyProjectForFee.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.ccl.script.CheckApplyProjectForFee,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = "phis.checkApplyService";
				this.requestData.serviceAction = "getCheckApplyUnboundProject";
				this.requestData.body = {
					jgid:this.mainApp.deptId
				}
				phis.application.ccl.script.CheckApplyProjectForFee.superclass.loadData
						.call(this)
			}
		});