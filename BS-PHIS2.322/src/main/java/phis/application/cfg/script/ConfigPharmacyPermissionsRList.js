$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigPharmacyPermissionsRList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "secondGridDDGroup";
	// 从用户改回从员工信息取数据
	this.serverParams = {
		"serviceAction" : cfg.serviceAction
	}
	phis.application.cfg.script.ConfigPharmacyPermissionsRList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigPharmacyPermissionsRList,
		phis.script.SimpleList, {
		// expansion : function(cfg) {
		// var labelText = new Ext.Button({
		// text:"员工信息"
		// });
		// delete cfg.tbar;
		// cfg.tbar = [];
		// cfg.tbar.push(labelText);
		// }
		});