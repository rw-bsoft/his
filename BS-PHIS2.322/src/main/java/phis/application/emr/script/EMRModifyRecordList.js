$package("phis.application.emr.script")

$import("phis.script.SelectList")

phis.application.emr.script.EMRModifyRecordList = function(cfg) {
	// cfg.mutiSelect = true;
	// cfg.selectFirst = false;
	// cfg.pageSize = 5;
	phis.application.emr.script.EMRModifyRecordList.superclass.constructor
			.apply(this, [cfg])
	// this.on("loadData", this.onMyLoadData, this);
}

Ext.extend(phis.application.emr.script.EMRModifyRecordList,
		phis.script.SelectList, {
			onRowClick : function(grid, index, e) {
				this.selectedIndex = index
				this.fireEvent("firstRowSelected");
			}
			// ,
		// getCM : function(items) {
		// return app.modules.list.SelectListView.superclass.getCM.call(
		// this, items)
		// }
	})
