$package("phis.application.sas.script")
$import("phis.script.SimpleList","phis.prints.script.SuppliesStockYjSearchDetailsPrintView")

phis.application.sas.script.SuppliesStockSearchDetailsList = function(cfg) {
	phis.application.sas.script.SuppliesStockSearchDetailsList.superclass.constructor
			.apply(this, [cfg])
	this.entryName="phis.application.sas.schemas.WL_WZKC_HZ";
}
Ext.extend(phis.application.sas.script.SuppliesStockSearchDetailsList,
		phis.script.SimpleList, {
			loadData : function(zblbValue) {
				// var ZBLB = this.ZBLB;
				if(zblbValue){
					this.ZBLB = zblbValue;
				}
				this.clear(); 
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryStockCollect";
				this.requestData.ZBLB = this.ZBLB;
				
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
				var pWin = this.midiModules["SuppliesStockYjSearchDetailsPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.SuppliesStockYjSearchDetailsPrintView(cfg)
				this.midiModules["SuppliesStockYjSearchDetailsPrintView"] = pWin
				pWin.getWin().show()
			}
		})