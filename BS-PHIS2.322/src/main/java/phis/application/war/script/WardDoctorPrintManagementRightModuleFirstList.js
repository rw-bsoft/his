$package("phis.application.war.script")
$import("phis.script.SimpleList")
phis.application.war.script.WardDoctorPrintManagementRightModuleFirstList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.war.script.WardDoctorPrintManagementRightModuleFirstList.superclass.constructor
			.apply(this, [cfg])
}

Ext
		.extend(
				phis.application.war.script.WardDoctorPrintManagementRightModuleFirstList,
				phis.script.SimpleList, {
					loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.serviceAction;
						phis.application.war.script.WardDoctorPrintManagementRightModuleFirstList.superclass.loadData
								.call(this)
					}
				})