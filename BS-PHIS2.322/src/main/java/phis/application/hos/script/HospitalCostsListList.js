$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalCostsListList = function(cfg) {
	cfg.autoLoadData = false;
//	cfg.listServiceId = "hospitalCostProcessingService";
//	cfg.serverParams ={serviceAction :"HospitalCostMxQuery"}
	phis.application.hos.script.HospitalCostsListList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalCostsListList, phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.hos.script.HospitalCostsListList.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				phis.application.hos.script.HospitalCostsListList.superclass.loadData.call(this);
			}
		});