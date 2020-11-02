$package("phis.application.med.script");

$import("phis.script.SimpleList");

phis.application.med.script.MedicalTechnologyLeftList = function(cfg) {
	phis.application.med.script.MedicalTechnologyLeftList.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(phis.application.med.script.MedicalTechnologyLeftList,
		phis.script.SimpleList, {
			onRowClick : function(grid, index, e) {
				this.fireEvent("click", this);
			},
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
				this.fireEvent("click",this);
			}
		});