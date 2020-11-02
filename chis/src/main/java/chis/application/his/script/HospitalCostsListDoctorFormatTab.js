$package("chis.application.his.script")

$import("chis.script.BizSimpleListView")

chis.application.his.script.HospitalCostsListDoctorFormatTab = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalCostProcessingService";
	cfg.serverParams ={serviceAction :"hospitalCostDetalsQuery"}
	chis.application.his.script.HospitalCostsListDoctorFormatTab.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.his.script.HospitalCostsListDoctorFormatTab,chis.script.BizSimpleListView,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				chis.application.his.script.HospitalCostsListDoctorFormatTab.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				chis.application.his.script.HospitalCostsListDoctorFormatTab.superclass.loadData.call(this);
			}
		});