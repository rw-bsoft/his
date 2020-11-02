$package("chis.application.def.script.limb");

$import("chis.script.BizSimpleListView");

chis.application.def.script.limb.LimbDeformityRecordList = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_LimbDeformityRecord_Module"
	cfg.height = 240;
	this.selectedIndex = 0
	cfg.listServiceId = "chis.defLimbService"
	chis.application.def.script.limb.LimbDeformityRecordList.superclass.constructor.apply(this,
			[cfg]);
	this.on("getStoreFields",this.onGetStoreFields,this)
};

Ext.extend(chis.application.def.script.limb.LimbDeformityRecordList,
		chis.script.BizSimpleListView, {
			onStoreBeforeLoad : function(store, op) {
				this.requestData.empiId = this.exContext.ids.empiId;
			},
			onRowClick : function(grid, index, e) {
				this.fireEvent("rowClick", grid, index, e)
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
				}
			},
			onReady : function() {
				chis.application.def.script.limb.LimbDeformityRecordList.superclass.onReady
						.call(this)
				this.requestData.serviceAction = "getLimbDeformityRecordList";
				var columns = this.grid.getColumnModel().getColumnsBy(
						function(c) {
							c.sortable = false
						});
			},
			onGetStoreFields : function(fields) {
				fields.push({
							name : "_actions",
							type : "object"
						});
				fields.push({
							name : "trainingEvaluateCount",
							type : "int"
						});
			}

		});