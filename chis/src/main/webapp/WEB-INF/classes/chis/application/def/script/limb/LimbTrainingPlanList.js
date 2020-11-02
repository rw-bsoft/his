$package("chis.application.def.script.limb");

$import("chis.script.BizSimpleListView");

chis.application.def.script.limb.LimbTrainingPlanList = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_LimbTrainingPlan"
	cfg.listServiceId = "chis.defLimbService"
	chis.application.def.script.limb.LimbTrainingPlanList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.def.script.limb.LimbTrainingPlanList, chis.script.BizSimpleListView, {
			onStoreBeforeLoad : function(store, op) {
				this.requestData.defId = this.exContext.r.get("id");
			},
			onRowClick : function(grid, rowIndex, e) {
				this.fireEvent("rowclick",grid,rowIndex,e)
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store,records, ops)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			},
			onReady : function() {
				chis.application.def.script.limb.LimbTrainingPlanList.superclass.onReady
						.call(this)
				this.requestData.serviceAction = "getLimbTrainingPlanList";
				this.grid.getColumnModel().getColumnsBy(
						function(c) {
							c.sortable = false
						});
			}
		});