$package("phis.application.sas.script")
$import("phis.script.SimpleList")

phis.application.sas.script.SuppliesStockExpirationWarning = function(cfg) {
	phis.application.sas.script.SuppliesStockExpirationWarning.superclass.constructor
			.apply(this, [cfg])
	this.entryName = "phis.application.sas.schemas.WL_WZKC_YJ";
}
Ext.extend(phis.application.sas.script.SuppliesStockExpirationWarning,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "querySuppliesStockExpirationWarning";

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