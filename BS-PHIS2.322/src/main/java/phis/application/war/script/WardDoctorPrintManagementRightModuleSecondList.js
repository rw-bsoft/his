$package("phis.application.war.script")
$import("phis.script.SimpleList")
phis.application.war.script.WardDoctorPrintManagementRightModuleSecondList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr=true;
	phis.application.war.script.WardDoctorPrintManagementRightModuleSecondList.superclass.constructor
			.apply(this, [cfg])
}

Ext
		.extend(
				phis.application.war.script.WardDoctorPrintManagementRightModuleSecondList,
				phis.script.SimpleList, {
					loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.serviceAction;
						phis.application.war.script.WardDoctorPrintManagementRightModuleSecondList.superclass.loadData.call(this)
					}
				})