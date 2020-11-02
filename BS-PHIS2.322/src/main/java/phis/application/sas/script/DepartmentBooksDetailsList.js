$package("phis.application.sas.script")
$import("phis.script.SimpleList","phis.prints.script.DepartmentBooksDetailsPrintView")

phis.application.sas.script.DepartmentBooksDetailsList = function(cfg) {
	phis.application.sas.script.DepartmentBooksDetailsList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sas.script.DepartmentBooksDetailsList,
		phis.script.SimpleList, {
			loadData : function(zblbValue) {
				if(zblbValue){
					this.ZBLB = zblbValue;
				}
				this.clear(); 
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryDepartmentBooksCollect";
				this.requestData.cnd = this.ZBLB;
				
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
				var pWin = this.midiModules["DepartmentBooksDetailsPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.DepartmentBooksDetailsPrintView(cfg)
				this.midiModules["DepartmentBooksDetailsPrintView"] = pWin
				pWin.getWin().show()
			}
		})