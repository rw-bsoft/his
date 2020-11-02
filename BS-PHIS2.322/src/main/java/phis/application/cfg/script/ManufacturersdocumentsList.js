$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ManufacturersdocumentsList = function(cfg) {
	cfg.autoLoadData=false;
	phis.application.cfg.script.ManufacturersdocumentsList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cfg.script.ManufacturersdocumentsList,
		phis.script.EditorList, {
});