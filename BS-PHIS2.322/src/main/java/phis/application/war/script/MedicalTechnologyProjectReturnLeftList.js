$package("phis.application.war.script")
$import("phis.script.SimpleList")

phis.application.war.script.MedicalTechnologyProjectReturnLeftList = function(
		cfg) {
	phis.application.war.script.MedicalTechnologyProjectReturnLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.MedicalTechnologyProjectReturnLeftList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.war.script.MedicalTechnologyProjectReturnLeftList.superclass.loadData
						.call(this);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.fireEvent("noRecord")
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.fireEvent("selectRecord", this.getSelectedRecord());
			},
			onRowClick : function(grid, index, e) {
				this.fireEvent("selectRecord", this.getSelectedRecord());
			}
		})