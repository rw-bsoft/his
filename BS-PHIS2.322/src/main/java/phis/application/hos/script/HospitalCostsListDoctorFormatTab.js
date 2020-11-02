$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalCostsListDoctorFormatTab = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalCostProcessingService";
	cfg.serverParams ={serviceAction :"hospitalCostDetalsQuery"}
	phis.application.hos.script.HospitalCostsListDoctorFormatTab.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalCostsListDoctorFormatTab,phis.script.SimpleList,
		{
			refresh : function() {
				this.requestData.pageNo=1;
				phis.application.hos.script.HospitalCostsListDoctorFormatTab.superclass.refresh.call(this);
			},
			loadData : function() {
//				if (!this.exContext.ZYH) {
//					return;
//				}
				phis.application.hos.script.HospitalCostsListDoctorFormatTab.superclass.loadData.call(this);
			}
		});