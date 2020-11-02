$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyCustodianBooksDetailList = function(cfg) {
	phis.application.pha.script.PharmacyCustodianBooksDetailList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyCustodianBooksDetailList,
		phis.script.SimpleList, {
			
		})