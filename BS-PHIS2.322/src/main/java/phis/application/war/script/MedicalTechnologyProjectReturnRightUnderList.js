$package("phis.application.war.script")
$import("phis.script.SimpleList")

phis.application.war.script.MedicalTechnologyProjectReturnRightUnderList = function(cfg) {
	phis.application.war.script.MedicalTechnologyProjectReturnRightUnderList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.war.script.MedicalTechnologyProjectReturnRightUnderList, phis.script.SimpleList, {
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.fireEvent("jzwc")
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.fireEvent("jzwc");
			},
			onRenderer_je:function(value, metaData, r){
			return (parseFloat(r.get("YLDJ"))*parseFloat(r.get("YLSL"))).toFixed(2)
			}
		})