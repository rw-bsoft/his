$package("chis.application.def.script.intellect");

$import("app.modules.list.SimpleListView");

chis.application.def.script.intellect.IntellectTrainingEvaluateList = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_IntellectTrainingEvaluate"
	cfg.height = 230;
	cfg.listServiceId = "chis.defIntellectService"
	chis.application.def.script.intellect.IntellectTrainingEvaluateList.superclass.constructor.apply(this,
			[cfg]);
};

Ext.extend(chis.application.def.script.intellect.IntellectTrainingEvaluateList,
		app.modules.list.SimpleListView, {
			onStoreBeforeLoad : function(store, op) {
				this.requestData.defId = this.exContext.r.get("id");
			},
			onRowClick : function(grid, index, e) {
				this.fireEvent("rowclick", grid, index, e)
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			}
			,
			onReady : function() {
				chis.application.def.script.intellect.IntellectTrainingEvaluateList.superclass.onReady
						.call(this)
				this.requestData.serviceAction = "getIntellectTrainingEvaluateList";
				var columns = this.grid.getColumnModel().getColumnsBy(
						function(c) {
							c.sortable = false
						});
			}
		});