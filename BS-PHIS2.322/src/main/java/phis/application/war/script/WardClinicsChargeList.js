$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.WardClinicsChargeList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "secondGridGroup";
	cfg.listServiceId = "wardPatientManageService";
	cfg.disablePagingTbr = true;
	phis.application.war.script.WardClinicsChargeList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.WardClinicsChargeList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceAction = "loadClinicsCharge";
				this.requestData.body = {
					ZYH : this.initDataId
				}
				this.clear();
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