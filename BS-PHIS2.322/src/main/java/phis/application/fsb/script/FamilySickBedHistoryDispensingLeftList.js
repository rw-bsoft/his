$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedHistoryDispensingLeftList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FamilySickBedHistoryDispensingLeftList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedHistoryDispensingLeftList,
		phis.script.SimpleList, {
			// 单击时调出发药列表
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.fireEvent("recordClick",r.data.JLID);
			},
			//打开页面选中第一条记录 并显示右边的
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