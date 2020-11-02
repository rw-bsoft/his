$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleRightList = function(cfg) {
	cfg.initCnd=['eq',['$','1'],['i',2]];
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleRightList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleRightList, phis.script.SimpleList, {
//			onStoreLoadData : function(store, records, ops) {
//				phis.script.SimpleList.superclass.onStoreLoadData.call(this,
//						store, records, ops)
//				if (records.length == 0 || !this.selects || !this.mutiSelect) {
//					return
//				}
//				
//				var selRecords = []
////				for (var id in this.selects) {
////					var r = store.getById(id)
////					if (r) {
////						selRecords.push(r)
////					}
////				}
//				var count=this.store.getCount();
//				for(var i=0;i<count;i++){
//				if(this.store.getAt(i).data.LRBZ==1){
//				selRecords.push(this.store.getAt(i));
//				}
//				}
//				this.sm.unlock(); 
//				this.grid.getSelectionModel().selectRecords(selRecords)
//				this.sm.lock();
//			}
//			,
			onRenderer_sl : function(value, metaData, r) {
				return parseFloat(r.data.SPSL-r.data.PQSL).toFixed(2);
			},
			onRenderer_ls : function(value, metaData, r) {
				return parseFloat((r.data.SPSL-r.data.PQSL)*r.data.LSJG).toFixed(4);
			},
			onRenderer_jh : function(value, metaData, r) {
				return parseFloat((r.data.SPSL-r.data.PQSL)*r.data.JHJG).toFixed(4);
			},
			onDblClick : function(grid, index, e) {
			this.fireEvent("click",this);
			}
		})