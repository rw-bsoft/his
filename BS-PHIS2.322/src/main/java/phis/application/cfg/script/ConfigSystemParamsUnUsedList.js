$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigSystemParamsUnUsedList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "firstGridDDGroup";
	phis.application.cfg.script.ConfigSystemParamsUnUsedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.ConfigSystemParamsUnUsedList,
		phis.script.SimpleList, {})