/**
 * 药房模块,中间下面List
 * 
 * @author caijy
 */
$package("phis.application.pcm.script")
$import("phis.script.SimpleList")

phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleUnderList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleUnderList.superclass.constructor
			.apply(this, [cfg])
}
Ext
		.extend(
				phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleUnderList,
				phis.script.SimpleList, {
					onRenderer_dp : function(value, metaData, r) {
						if (value == 0) {
							return "〇"
						} else {
							return "√"
						}
					},
					onRenderer_bz:function(value, metaData, r){
					if(r.get("DPBZ")==1){
					return value;					
					}else{
					return "";
					}
					},
					onRowClick : function(grid,index,e) {
						var r = this.getSelectedRecord();
						if (r == null) {
							return;
						}
						if(index&&index!=null){
						this.selectedIndex = index
						}
						this.fireEvent("cymxClick", r.get("CFSB"), r
										.get("DPBZ"), r.get("CFLX"));
					},
					onStoreLoadData : function(store, records, ops) {
						this.fireEvent("loadData", store) // **
															// 不管是否有记录，都fire出该事件
						if (records.length == 0) {
							this.fireEvent("noRecord");
							return
						}
						this.totalCount = store.getTotalCount()
						if (!this.selectedIndex
								|| this.selectedIndex >= records.length) {
							this.selectRow(0)
							this.selectedIndex = 0;
						} else {
							this.selectRow(this.selectedIndex);
						}
						this.onRowClick();
					}
				})