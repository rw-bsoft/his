$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicDepartmentList = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	this.disablePagingTbr=cfg.disablePagingTbr = true;
	phis.application.cic.script.ClinicDepartmentList.superclass.constructor.apply(this, [cfg])
	
}
Ext.extend(phis.application.cic.script.ClinicDepartmentList, phis.script.SimpleList, {
			doNew :function(){}
		})