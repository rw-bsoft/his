$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList_br = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList_br.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList_br,
		phis.script.SimpleList, {
		onRowClick : function() {// 单击时调出发药列表
			var r = this.getSelectedRecord();
			if (r == null) {
				return;
			}
//			this.fireEvent("recordClick_br",r.data.ZYH);
			this.fireEvent("recordClick_br",r.data);
		},
		onStoreLoadData : function(store, records, ops) {//打开页面选中第一条记录 并显示右边的
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