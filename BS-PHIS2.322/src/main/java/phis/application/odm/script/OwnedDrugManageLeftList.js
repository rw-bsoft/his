$package("phis.application.odm.script")

$import("phis.script.SimpleList")

phis.application.odm.script.OwnedDrugManageLeftList = function(cfg) {
	phis.application.odm.script.OwnedDrugManageLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.odm.script.OwnedDrugManageLeftList,
		phis.script.SimpleList, {
		onStoreLoadData:function(store,records,ops){
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if(records.length == 0){
			return
		}
		phis.application.odm.script.OwnedDrugManageLeftList.superclass.onStoreLoadData.call(this,store,records,ops);
		this.fireEvent("recordClick",this.getSelectedRecord());
		},
		onRowClick:function(grid,index,e){
		phis.application.odm.script.OwnedDrugManageLeftList.superclass.onRowClick.call(this,grid,index,e);
		this.fireEvent("recordClick",this.getSelectedRecord());
		}
		})