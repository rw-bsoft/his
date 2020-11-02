$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigDepartmentList = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	cfg.autoLoadData = false;
	this.disablePagingTbr = cfg.disablePagingTbr = true;
	cfg.gridDDGroup = "secondGridDepartmentGroup";
	phis.application.cfg.script.ConfigDepartmentList.superclass.constructor.apply(this,
			[cfg])

}
Ext.extend(phis.application.cfg.script.ConfigDepartmentList, phis.script.SimpleList,
		{
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				this.requestData.serviceId = "phis.configHsqxYgService";
				this.requestData.serviceAction = "getKSDMInfo";
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