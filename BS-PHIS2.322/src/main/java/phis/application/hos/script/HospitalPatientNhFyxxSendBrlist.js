$package("phis.application.hos.script")

$import("phis.script.SelectList")

phis.application.hos.script.HospitalPatientNhFyxxSendBrlist = function(cfg) {
	// cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	phis.application.hos.script.HospitalPatientNhFyxxSendBrlist.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.hos.script.HospitalPatientNhFyxxSendBrlist,
		phis.script.SelectList, {
			
		})