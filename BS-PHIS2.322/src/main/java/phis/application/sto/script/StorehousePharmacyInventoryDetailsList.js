$package("phis.application.sto.script")

$import("phis.script.SimpleList",
		"phis.application.ivc.script.ClinicInvoiceQueriesPagingToolbar")

phis.application.sto.script.StorehousePharmacyInventoryDetailsList = function(cfg) {
	phis.application.sto.script.StorehousePharmacyInventoryDetailsList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehousePharmacyInventoryDetailsList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceActionId;
				this.requestData.body = {};
				phis.application.sto.script.StorehousePharmacyInventoryDetailsList.superclass.loadData
						.call(this);
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("YPXH"))
				}
				var showZero = this.grid.getTopToolbar().get("showZero")
						.getValue();
				var pWin = this.midiModules["script.StorehousePriceBooksInventoryPrintView"]
				var cfg = {
					requestData : ids,
					showZero : showZero
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new app.modules.print.script.StorehousePriceBooksInventoryPrintView(cfg)
				this.midiModules["script.StorehousePriceBooksInventoryPrintView"] = pWin
				pWin.getWin().show()
			}
		})