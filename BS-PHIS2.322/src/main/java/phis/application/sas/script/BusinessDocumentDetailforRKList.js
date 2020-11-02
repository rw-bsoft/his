$package("phis.application.sas.script")

$import("phis.script.SimpleList","phis.prints.script.BusinessDocumentDetailforRKPrintView")


phis.application.sas.script.BusinessDocumentDetailforRKList = function(cfg) {
	phis.application.sas.script.BusinessDocumentDetailforRKList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sas.script.BusinessDocumentDetailforRKList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear(); 
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryDocumentDetailforRK";
				
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
				var pWin = this.midiModules["BusinessDocumentDetailforRKPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.BusinessDocumentDetailforRKPrintView(cfg)
				this.midiModules["BusinessDocumentDetailforRKPrintView"] = pWin
				pWin.getWin().show()
			}
		})