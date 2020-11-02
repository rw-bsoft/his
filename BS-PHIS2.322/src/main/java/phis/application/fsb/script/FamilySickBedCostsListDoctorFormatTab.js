$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "familySickBedCostProcessingService";
	cfg.serverParams ={serviceAction :"hospitalCostDetalsQuery"}
	phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab,phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab.superclass.loadData.call(this);
			}
		});