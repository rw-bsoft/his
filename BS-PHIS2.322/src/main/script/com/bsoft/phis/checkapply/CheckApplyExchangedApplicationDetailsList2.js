$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckApplyExchangedApplicationDetailsList2 = function(cfg) {
	cfg.disablePagingTbr = true; // 分页暂时不要
	cfg.autoLoadData = false;
	com.bsoft.phis.checkapply.CheckApplyExchangedApplicationDetailsList2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyExchangedApplicationDetailsList2,
		com.bsoft.phis.SimpleList, {
			loadData : function() {
				com.bsoft.phis.checkapply.CheckApplyExchangedApplicationDetailsList2.superclass.loadData
						.call(this);
			}
		});