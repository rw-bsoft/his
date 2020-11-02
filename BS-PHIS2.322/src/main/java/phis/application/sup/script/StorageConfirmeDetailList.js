$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.StorageConfirmeDetailList = function(cfg) {
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
    phis.application.sup.script.StorageConfirmeDetailList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.StorageConfirmeDetailList,
		phis.script.SimpleList, {
			
		})