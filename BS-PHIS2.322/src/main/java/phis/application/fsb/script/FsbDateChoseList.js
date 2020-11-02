$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbDateChoseList = function(cfg) {
	cfg.modal = true;
	phis.application.fsb.script.FsbDateChoseList.superclass.constructor.apply(
			this, [ cfg ]);
}

Ext.extend(phis.application.fsb.script.FsbDateChoseList, phis.script.SimpleList, {

});