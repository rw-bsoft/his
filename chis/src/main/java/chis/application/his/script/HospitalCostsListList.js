$package("chis.application.his.script")

$import("chis.script.BizSimpleListView")

chis.application.his.script.HospitalCostsListList = function(cfg) {
	cfg.autoLoadData = false;
//	cfg.listServiceId = "hospitalCostProcessingService";
//	cfg.serverParams ={serviceAction :"HospitalCostMxQuery"}
	chis.application.his.script.HospitalCostsListList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.his.script.HospitalCostsListList, chis.script.BizSimpleListView,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				chis.application.his.script.HospitalCostsListList.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				chis.application.his.script.HospitalCostsListList.superclass.loadData.call(this);
			}
		});