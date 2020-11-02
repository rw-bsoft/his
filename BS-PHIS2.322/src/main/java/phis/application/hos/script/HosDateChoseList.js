$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HosDateChoseList = function(cfg) {
	cfg.modal = true;
	phis.application.hos.script.HosDateChoseList.superclass.constructor.apply(
			this, [ cfg ]);
}

Ext.extend(phis.application.hos.script.HosDateChoseList, phis.script.SimpleList, {

});