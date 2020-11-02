/**
 * 家床发药-待发药病区选择List
 * 
 * @author : caijy
 */
$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedDispensingRightCollectList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.fsb.script.FamilySickBedDispensingRightCollectList.superclass.constructor
			.apply(this, [ cfg ])
}

Ext.extend(
		phis.application.fsb.script.FamilySickBedDispensingRightCollectList,
		phis.script.SimpleList, {
			onStoreBeforeLoad : function(store, op) {
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				this.fireEvent("BeforeLoadData");
			},
			loadData : function(arr) {
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				for ( var i = 0; i < arr.length; i++) {
					store.add([ new Record(arr[i]) ]);
				}
			},
			onStoreLoadData:function(store,records,ops){
				this.fireEvent("loadData", store)// ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				}
				else{
					this.selectRow(this.selectedIndex);
				}
			},
			onRendererNull : function(value, metaData, r) {
				if (value == null || value == "null") {
					return "";
				} else {
					return value;
				}
			}
		});