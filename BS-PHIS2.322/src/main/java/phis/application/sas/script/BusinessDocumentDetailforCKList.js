$package("phis.application.sas.script")
$import("phis.script.SimpleList")

phis.application.sas.script.BusinessDocumentDetailforCKList = function(cfg) {
	phis.application.sas.script.BusinessDocumentDetailforCKList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext.extend(phis.application.sas.script.BusinessDocumentDetailforCKList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryDocumentDetailforCK";

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