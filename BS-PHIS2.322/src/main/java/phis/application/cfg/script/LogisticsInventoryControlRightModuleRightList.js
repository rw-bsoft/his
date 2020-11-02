$package("phis.application.cfg.script")
$import("phis.script.SimpleList")

phis.application.cfg.script.LogisticsInventoryControlRightModuleRightList = function(
		cfg) {
	phis.application.cfg.script.LogisticsInventoryControlRightModuleRightList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.cfg.script.LogisticsInventoryControlRightModuleRightList,
				phis.script.SimpleList,
				{
					loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.queryActionId;
						// this.requestData.cnd=['and',['eq',['$','SFBZ'],['i',1]],['not
						// in',['$','KFXH'],['select KFXH FROM WL_KFXX WHERE
						// KFLB = 1 AND JGID ='+this.mainApp['phisApp'].deptId]]];
						phis.application.cfg.script.LogisticsInventoryControlRightModuleRightList.superclass.loadData
								.call(this)
					},
					onDblClick : function(grid, index, e) {
						var r = this.getSelectedRecord();
						if (r == null) {
							return;
						}
						this.fireEvent("wzClick", r.data)
					}
				})