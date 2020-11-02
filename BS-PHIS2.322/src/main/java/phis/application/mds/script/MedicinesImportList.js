$package("phis.application.mds.script")

$import("phis.script.SelectList")
phis.application.mds.script.MedicinesImportList = function(cfg) {
	cfg.enableCnd = false;
	cfg.autoLoadData = false;
	cfg.initCnd=['and',['ne',['$','a.ZFPB'],['i',1]],['ne',['$','a.ZFYP'],['i',1]]];
	phis.application.mds.script.MedicinesImportList.superclass.constructor.apply(
			this, [cfg])

}
Ext.extend(phis.application.mds.script.MedicinesImportList,
		phis.script.SelectList, {
			
		})