$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRQxbmWh = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.emr.script.EMRQxbmWh.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.emr.script.EMRQxbmWh, phis.script.SimpleList, {
})
