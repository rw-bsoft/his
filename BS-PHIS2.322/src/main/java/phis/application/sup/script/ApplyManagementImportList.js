$package("phis.application.sup.script");

$import("phis.script.SimpleList")

phis.application.sup.script.ApplyManagementImportList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = true;
	phis.application.sup.script.ApplyManagementImportList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.ApplyManagementImportList, phis.script.SimpleList, {
			loadData : function(body) {
				this.clear();
				this.requestData.serviceId = "phis.applyManagementService";
				this.requestData.serviceAction = "sLXXKSQuery";
                var body = {};
                body["ZBLB"]=this.zblb;
                body["KFXH"]=this.mainApp['phis'].treasuryId;
                body["KSDM"]=this.ksdm;
                this.requestData.cnd = body;
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
			}
		})
