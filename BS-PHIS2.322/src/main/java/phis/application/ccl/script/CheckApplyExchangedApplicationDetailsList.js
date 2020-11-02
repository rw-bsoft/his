$package("phis.application.ccl.script")
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList = function(cfg) {
	cfg.disablePagingTbr = true; // 分页暂时不要
	cfg.autoLoadData = false;
	phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList,
		phis.script.SimpleList, {
			loadData : function() {
				phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList.superclass.loadData
						.call(this);
			}
		});