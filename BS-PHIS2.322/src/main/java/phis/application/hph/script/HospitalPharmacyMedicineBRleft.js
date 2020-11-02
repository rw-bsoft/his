$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyMedicineBRleft = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalPharmacyMedicineService";
	cfg.serverParams = {
		serviceAction : "queryBRFYXX"
	};
	phis.application.hph.script.HospitalPharmacyMedicineBRleft.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyMedicineBRleft,
		phis.script.SimpleList, {
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					this.fireEvent("noRecord");
					return;
				}
				this.body.ZYH=r.get("ZYH")
				this.fireEvent("recordClick", this.body);
			},
			// 打开页面选中第一条记录 并显示右边的
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.onRowClick();
			}
		});