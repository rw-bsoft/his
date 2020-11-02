$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.ResetBusinessDetailList = function(cfg) {
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	phis.application.sup.script.ResetBusinessDetailList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ResetBusinessDetailList,phis.script.EditorList, {
		})