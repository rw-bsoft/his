$package("chis.application.dbs.script.ogtt");

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule");

chis.application.dbs.script.ogtt.DiabetesOGTTList = function(cfg) {
	chis.application.dbs.script.ogtt.DiabetesOGTTList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd=false;
	this.disablePagingTbr=true;
}

Ext.extend(chis.application.dbs.script.ogtt.DiabetesOGTTList,
		chis.script.BizSimpleListView, {
			
		});