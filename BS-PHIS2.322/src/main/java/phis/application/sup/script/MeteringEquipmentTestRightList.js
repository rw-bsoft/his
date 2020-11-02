$package("phis.application.sup.script");

$import("phis.script.SimpleList");

phis.application.sup.script.MeteringEquipmentTestRightList = function(cfg) {
	cfg.autoLoadData = false;
	// cfg.disablePagingTbr = true;
	phis.application.sup.script.MeteringEquipmentTestRightList.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.MeteringEquipmentTestRightList,
		phis.script.SimpleList, {

		})