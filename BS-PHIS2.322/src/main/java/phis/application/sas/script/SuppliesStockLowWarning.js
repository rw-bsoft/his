$package("phis.application.sas.script")

$import("phis.script.SimpleList")

phis.application.sas.script.SuppliesStockLowWarning = function(cfg) {
	phis.application.sas.script.SuppliesStockLowWarning.superclass.constructor.apply(
			this, [cfg])
	this.entryName = "phis.application.sas.schemas.WL_KCYJ_CX";
}
Ext.extend(phis.application.sas.script.SuppliesStockLowWarning,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "querySuppliesStockLowWarning";

				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			doRefresh : function() {
				this.loadData();
			}
		})