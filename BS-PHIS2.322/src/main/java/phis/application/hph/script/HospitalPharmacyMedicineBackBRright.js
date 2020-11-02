$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyMedicineBackBRright = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalPharmacyMedicineService";
	cfg.serverParams = {
		serviceAction : "queryBRFYMXBack"
	};
	phis.application.hph.script.HospitalPharmacyMedicineBackBRright.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyMedicineBackBRright,
		phis.script.SimpleList, {
			onReady : function(store, records, ops) {
				this.store.on("datachanged", this.setRowBackground, this)
				phis.application.hph.script.HospitalPharmacyMedicineBackBRright.superclass.onReady
						.call(this, store, records, ops);
			},
			setRowBackground : function() {
				var girdcount = 0;
				this.store.each(function(r) {
					var LSJE = r.get("LSJE");
					if (LSJE < 0) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
			}
		});