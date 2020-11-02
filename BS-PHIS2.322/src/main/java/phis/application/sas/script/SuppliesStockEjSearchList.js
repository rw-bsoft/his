$package("phis.application.sas.script")

$import("phis.script.SimpleList",
		"phis.application.ivc.script.ClinicInvoiceQueriesPagingToolbar",
		"phis.prints.script.SuppliesStockEjSearchPrintView")

phis.application.sas.script.SuppliesStockEjSearchList = function(cfg) {
	phis.application.sas.script.SuppliesStockEjSearchList.superclass.constructor.apply(
			this, [cfg])
	this.entryName="phis.application.sas.schemas.WL_WZKC_EJ_CX";
}
Ext.extend(phis.application.sas.script.SuppliesStockEjSearchList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryStockEjDetails";
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
			doRefresh : function(){
				this.loadData();
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("JLXH"))
				}
				var pWin = this.midiModules["SuppliesStockEjSearchPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.SuppliesStockEjSearchPrintView(cfg)
				this.midiModules["SuppliesStockEjSearchPrintView"] = pWin
				pWin.getWin().show()
			}
		})