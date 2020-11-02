﻿$package("chis.application.def.script.intellect");

$import("chis.script.BizSimpleListView");

chis.application.def.script.intellect.IntellectTrainingPlanList = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_IntellectTrainingPlan"
	cfg.listServiceId = "chis.defIntellectService"
	chis.application.def.script.intellect.IntellectTrainingPlanList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.def.script.intellect.IntellectTrainingPlanList, chis.script.BizSimpleListView, {
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
				chis.application.def.script.intellect.IntellectTrainingPlanList.superclass.onReady
						.call(this)
				this.requestData.serviceAction = "getIntellectTrainingPlanList";
				this.grid.getColumnModel().getColumnsBy(
						function(c) {
							c.sortable = false
						});
			}
		});