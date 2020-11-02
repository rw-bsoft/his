$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectLeftList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectLeftList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectLeftList,
		phis.script.SimpleList, {
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					this.fireEvent("noRecord");
					return;
				}
				this.fireEvent("recordClick",r.data.YPXH,r.data.YPCD,r.data.YPGG,r.data.YPDJ);
			},
			//打开页面选中第一条记录 并显示右边的
		onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			this.fireEvent("noRecord");
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