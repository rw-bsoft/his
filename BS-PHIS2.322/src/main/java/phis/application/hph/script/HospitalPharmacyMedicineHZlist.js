$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyMedicineHZlist = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hph.script.HospitalPharmacyMedicineHZlist.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyMedicineHZlist,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.queryServiceActionID;
				this.requestData.pageNo = 1;
				phis.application.hph.script.HospitalPharmacyMedicineHZlist.superclass.loadData
						.call(this);
			},
			onRendererNull : function(value, metaData, r) {
				if (value == null || value == "null") {
					return "";
				} else {
					return value;
				}
			}
		});