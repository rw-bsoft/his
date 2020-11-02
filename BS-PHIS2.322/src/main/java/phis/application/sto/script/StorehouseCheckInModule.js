/**
 * 采购入库module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleModule");

phis.application.sto.script.StorehouseCheckInModule = function(cfg) {
	cfg.leftTitle="未确定入库";//界面左边List标题
	cfg.rightTitle="确定入库";//界面右边List标题
	phis.application.sto.script.StorehouseCheckInModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckInModule,
				phis.application.sto.script.StorehouseMySimpleModule, {
		
});