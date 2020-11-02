$package("phis.application.cfg.script");

$import("phis.script.SimpleList");
/**
 * 费用医保对照列表
 */
phis.application.cfg.script.FeeYBCollateList = function(cfg) {
//	this.serverParams = {serviceAction:cfg.serviceAction};
	phis.application.cfg.script.FeeYBCollateList.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.cfg.script.FeeYBCollateList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				var rs = this.grid.getSelectionModel().getSelected();
				this.fireEvent("onDBAK03List", rs);
			}
});