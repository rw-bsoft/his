$package("phis.application.sto.script")

$import("phis.script.TabModule")

phis.application.sto.script.StorehouseMedicinesPirvateManageModule = function(cfg) {
	phis.application.sto.script.StorehouseMedicinesPirvateManageModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateManageModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				return phis.application.sto.script.StorehouseMedicinesPirvateManageModule.superclass.initPanel
						.call(this);
			}
		})