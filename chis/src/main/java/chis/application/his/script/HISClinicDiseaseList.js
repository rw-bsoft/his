$package("chis.application.his.script")

$import("chis.script.BizSimpleListView")

chis.application.his.script.HISClinicDiseaseList = function(cfg) {
	cfg.showButtonOnPT = true;
	cfg.showRowNumber = true;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	chis.application.his.script.HISClinicDiseaseList.superclass.constructor
			.apply(this, [cfg]);
	this.showButtonOnTop = false;
}
Ext.extend(chis.application.his.script.HISClinicDiseaseList,
		chis.script.BizSimpleListView, {

		});
