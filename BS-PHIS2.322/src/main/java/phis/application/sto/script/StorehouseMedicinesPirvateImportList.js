$package("phis.application.sto.script")

$import("phis.script.SelectList", "phis.script.rmi.jsonRequest")
phis.application.sto.script.StorehouseMedicinesPirvateImportList = function(cfg) {
	cfg.enableCnd = false;
	cfg.autoLoadData = false;
	cfg.initCnd=['ne',['$','ZFYP'],['i',1]]
	phis.application.sto.script.StorehouseMedicinesPirvateImportList.superclass.constructor.apply(
			this, [cfg])

}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateImportList,
		phis.script.SelectList, {
		})