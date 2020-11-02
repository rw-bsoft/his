$package("phis.application.ivc.script")

/**
 * 处方组套维护
 */
$import("phis.script.SimpleList")

phis.application.ivc.script.ReportSettingList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = true;
	phis.application.ivc.script.ReportSettingList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ReportSettingList, phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.reportSettingService";
				this.requestData.serviceAction = "BBMCQuery";
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
