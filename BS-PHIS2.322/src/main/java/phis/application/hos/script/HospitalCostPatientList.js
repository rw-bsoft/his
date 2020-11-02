$package("phis.application.hos.script")
$import("app.modules.list.SelectListView")
/**
 * 病人列表
 * 
 * @param cfg
 */
phis.application.hos.script.HospitalCostPatientList= function(cfg) {
	this.pageSize = 100;
	phis.application.hos.script.HospitalCostPatientList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.hos.script.HospitalCostPatientList,app.modules.list.SelectListView, {
	loadData : function() {
		this.requestData.serviceId = "phis.hospitalPatientManagementService";
		this.requestData.serviceAction = "getPatientList";
		phis.application.hos.script.HospitalCostPatientList.superclass.loadData
				.call(this);
	},

})