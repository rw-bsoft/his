$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRQxbmWhLeft = function(cfg) {
	cfg.showRowNumber = false;
	phis.application.emr.script.EMRQxbmWhLeft.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.emr.script.EMRQxbmWhLeft, phis.script.SimpleList, {})
