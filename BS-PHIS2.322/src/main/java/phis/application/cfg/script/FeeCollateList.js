$package("phis.application.cfg.script");

$import("phis.script.SimpleList");
/**
 * 费用对照列表
 */
phis.application.cfg.script.FeeCollateList = function(cfg) {
	this.serverParams = {serviceAction:cfg.serviceAction};
	phis.application.cfg.script.FeeCollateList.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.cfg.script.FeeCollateList,
		phis.script.SimpleList, {
		onDblClick : function(grid, index, e) {
			this.opener.doUpdate();
		}
		
});