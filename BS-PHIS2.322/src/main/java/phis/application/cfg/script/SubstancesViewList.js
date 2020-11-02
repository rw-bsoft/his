$package("phis.application.cfg.script")

$import("phis.script.SimpleList", "phis.script.SimpleForm")

phis.application.cfg.script.SubstancesViewList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.cfg.script.SubstancesViewList.superclass.constructor
			.apply(this, [ cfg ]);
	this.entryName = "phis.application.cfg.schemas.WL_WZZD";
}
Ext.extend(phis.application.cfg.script.SubstancesViewList,
		phis.script.SimpleList, {
			loadData : function(DXXH) {
				this.clear();
				if (!DXXH) {
					return;
				}
				this.requestData.serviceId = "phis.configManufacturerForWZService";
				this.requestData.serviceAction = "manufacturerForWZQuery";
				this.requestData.cnd = DXXH;

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

		});