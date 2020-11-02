$package("phis.application.sas.script")

$import("phis.script.SimpleList","phis.prints.script.SuppliesStockYjSearchPrintView")

phis.application.sas.script.SuppliesStockSearchList = function(cfg) {
	phis.application.sas.script.SuppliesStockSearchList.superclass.constructor
			.apply(this, [cfg])
	this.entryName="phis.application.sas.schemas.WL_WZKC_CX";
}
Ext.extend(phis.application.sas.script.SuppliesStockSearchList,
		phis.script.SimpleList, {
			loadData : function(zblbValue) {
				// var ZBLB = this.ZBLB;
				if(zblbValue){
					this.ZBLB = zblbValue;
				}
				this.clear(); 
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryStockDetails";
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
				var pWin = this.midiModules["SuppliesStockYjSearchPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.SuppliesStockYjSearchPrintView(cfg)
				this.midiModules["SuppliesStockYjSearchPrintView"] = pWin
				pWin.getWin().show()
			}
		})