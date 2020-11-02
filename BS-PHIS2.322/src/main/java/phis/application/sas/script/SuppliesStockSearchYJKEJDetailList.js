$package("phis.application.sas.script")
$import("phis.script.SimpleList",
		"phis.prints.script.SuppliesStockYjSearchDetailsPrintView")

phis.application.sas.script.SuppliesStockSearchYJKEJDetailList = function(cfg) {
	phis.application.sas.script.SuppliesStockSearchYJKEJDetailList.superclass.constructor
			.apply(this, [cfg])
	this.entryName="phis.application.sas.schemas.WL_WZKC_YJKEJ_HZ";
}
Ext.extend(phis.application.sas.script.SuppliesStockSearchYJKEJDetailList,
		phis.script.SimpleList, {
			loadData : function(zblbValue) {
				if(zblbValue){
					this.ZBLB = zblbValue;
				}
				this.clear();
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryStockEjCollectByKfxh";
				if(this.ZBLB){
					this.requestData.ZBLB = this.ZBLB;
				}
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
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("WZXH"))
				}
				var pWin = this.midiModules["SuppliesStockEjSearchDetailsPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.SuppliesStockEjSearchDetailsPrintView(cfg)
				this.midiModules["SuppliesStockEjSearchDetailsPrintView"] = pWin
				pWin.getWin().show()
			}
		})