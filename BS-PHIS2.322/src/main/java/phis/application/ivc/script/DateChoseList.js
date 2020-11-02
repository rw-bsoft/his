$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.DateChoseList = function(cfg) {
	cfg.modal = true;
	phis.application.ivc.script.DateChoseList.superclass.constructor.apply(
			this, [ cfg ]);
}

Ext.extend(phis.application.ivc.script.DateChoseList, phis.script.SimpleList, {

});