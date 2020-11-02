$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigSystemParamsUsedList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "secondGridGroup";
	phis.application.cfg.script.ConfigSystemParamsUsedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.ConfigSystemParamsUsedList,
		phis.script.SimpleList, {})