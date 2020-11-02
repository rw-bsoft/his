$package("chis.application.his.script")

$import("chis.script.BizSimpleListView")

chis.application.his.script.HISClinicChargesList = function(cfg) {
	cfg.showButtonOnPT = true;
	cfg.showRowNumber = true;
	this.initCnd=['eq',['$','c.ZFPB'],['s','0']]
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	chis.application.his.script.HISClinicChargesList.superclass.constructor
			.apply(this, [cfg]);
	this.showButtonOnTop = false;
}
Ext.extend(chis.application.his.script.HISClinicChargesList,
		chis.script.BizSimpleListView, {

		});