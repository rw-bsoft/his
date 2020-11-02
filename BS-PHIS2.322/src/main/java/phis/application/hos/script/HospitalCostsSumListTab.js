$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalCostsSumListTab = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalCostProcessingService";
	cfg.serverParams ={serviceAction :"hospitalCostSumQuery"}
	phis.application.hos.script.HospitalCostsSumListTab.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalCostsSumListTab,phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.hos.script.HospitalCostsListList.superclass.refresh.call(this);
			}
		});