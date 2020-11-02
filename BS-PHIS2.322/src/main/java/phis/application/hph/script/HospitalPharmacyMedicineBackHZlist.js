$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyMedicineBackHZlist = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hph.script.HospitalPharmacyMedicineBackHZlist.superclass.constructor
			.apply(this, [ cfg ])
}

Ext
		.extend(
				phis.application.hph.script.HospitalPharmacyMedicineBackHZlist,
				phis.script.SimpleList,
				{
					loadData : function() {
						this.requestData.serviceId = this.fullserviceId;
						this.requestData.serviceAction = this.queryServiceActionID;
						this.requestData.pageNo = 1;
						phis.application.hph.script.HospitalPharmacyMedicineBackHZlist.superclass.loadData
								.call(this);
					},
				});