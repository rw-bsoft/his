$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyInventoryProcessingLeftList = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.pha.script.PharmacyInventoryProcessingLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingLeftList,
		phis.script.SimpleList, {
			onRowClick : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					this.fireEvent("rowClick", null);
					return;
				}
				var body = {};
				body["YFSB"] = r.data.YFSB;
				body["CKBH"] = r.data.CKBH;
				body["PDDH"] = r.data.PDDH;
				body["PDWC"] = r.data.PDWC;
				body["HZWC"] = r.data.HZWC;
				this.fireEvent("rowClick", body);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.onRowClick();
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
		})