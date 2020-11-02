$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalPatientNhFyxxSendFylist = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.sortable = false;
	cfg.minListWidth = 510;
	phis.application.hos.script.HospitalPatientNhFyxxSendFylist.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.hos.script.HospitalPatientNhFyxxSendFylist,
		phis.script.SimpleList, {
			
		})