$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedCostsSumListTab = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "familySickBedCostProcessingService";
	cfg.serverParams ={serviceAction :"hospitalCostSumQuery"}
	phis.application.fsb.script.FamilySickBedCostsSumListTab.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedCostsSumListTab,phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.fsb.script.FamilySickBedCostsSumListTab.superclass.refresh.call(this);
			}
		});