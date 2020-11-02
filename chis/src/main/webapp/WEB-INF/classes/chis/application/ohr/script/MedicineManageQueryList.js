$package("chis.application.ohr.script")

$import("chis.script.BizSimpleListView")

chis.application.ohr.script.MedicineManageQueryList = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.ohr.script.MedicineManageQueryList.superclass.constructor.apply(this, [cfg]);
	this.disablePagingTbr=true;
}

Ext.extend(chis.application.ohr.script.MedicineManageQueryList, chis.script.BizSimpleListView, {
			
		});