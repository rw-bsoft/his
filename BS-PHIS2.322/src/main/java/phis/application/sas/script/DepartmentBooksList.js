$package("phis.application.sas.script")

$import("phis.script.SimpleList","phis.prints.script.DepartmentBooksPrintView")

phis.application.sas.script.DepartmentBooksList = function(cfg) {
	phis.application.sas.script.DepartmentBooksList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sas.script.DepartmentBooksList,
		phis.script.SimpleList, {
			loadData : function(zblbValue) {
				if(zblbValue){
					this.ZBLB = zblbValue;
				}
				this.clear(); 
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryDepartmentBooksDetails";
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
				var pWin = this.midiModules["DepartmentBooksPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.DepartmentBooksPrintView(cfg)
				this.midiModules["DepartmentBooksPrintView"] = pWin
				pWin.getWin().show()
			}
		})