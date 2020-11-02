$package("chis.application.his.script")

$import("chis.script.BizSimpleListView")

chis.application.his.script.WardPatientDiseaseList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.showButtonOnPT = true;
	chis.application.his.script.WardPatientDiseaseList.superclass.constructor
			.apply(this, [cfg]);
	this.showButtonOnTop = false;
	this.on("loadData", this.afterLoadData, this);
}
Ext.extend(chis.application.his.script.WardPatientDiseaseList,
		chis.script.BizSimpleListView, {
			afterLoadData : function() {
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					r.set("ZDXH_D", r.get("ZDXH"))
					r.set("ZDLB_D", r.get("ZDLB"))
				}
			}
		});
