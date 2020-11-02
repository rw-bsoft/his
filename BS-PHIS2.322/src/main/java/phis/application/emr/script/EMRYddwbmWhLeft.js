$package("phis.application.emr.script")

$import("phis.script.SelectList")

phis.application.emr.script.EMRYddwbmWhLeft = function(cfg) {
	cfg.showRowNumber = false;
	phis.application.emr.script.EMRYddwbmWhLeft.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.emr.script.EMRYddwbmWhLeft, phis.script.SelectList, {})
