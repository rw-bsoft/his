$package("phis.application.cic.script")

$import("phis.script.SimpleList")
phis.application.cic.script.HealthRecipeImportList = function(cfg) {
	phis.application.cic.script.HealthRecipeImportList.superclass.constructor
			.apply(this, [cfg]);
	this.autoLoadData = false;
}
Ext.extend(phis.application.cic.script.HealthRecipeImportList,
		phis.script.SimpleList, {
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.grid.fireEvent("rowclick", this.grid);
			},
			onStoreBeforeLoad : function(store, op) {
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				this.fireEvent("beforeLoad");
			}
		});