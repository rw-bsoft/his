$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedCostsListList = function(cfg) {
	cfg.autoLoadData = false;
//	cfg.listServiceId = "hospitalCostProcessingService";
//	cfg.serverParams ={serviceAction :"HospitalCostMxQuery"}
	phis.application.fsb.script.FamilySickBedCostsListList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedCostsListList, phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.fsb.script.FamilySickBedCostsListList.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				phis.application.fsb.script.FamilySickBedCostsListList.superclass.loadData.call(this);
			}
		});