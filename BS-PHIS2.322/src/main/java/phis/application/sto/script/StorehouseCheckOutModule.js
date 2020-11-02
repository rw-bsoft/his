/**
 * 药库出库功能
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleModule");

phis.application.sto.script.StorehouseCheckOutModule = function(cfg) {
	cfg.leftTitle="未确定出库";//界面左边List标题
	cfg.rightTitle="确定出库";//界面右边List标题
	phis.application.sto.script.StorehouseCheckOutModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutModule,
		phis.application.sto.script.StorehouseMySimpleModule, {
			//选中条件下拉框
			onSelect : function(record) {
				this.leftList.selectValue = parseInt(record.data.key);
				this.leftList.doRefreshWin();
				this.rightList.selectValue = parseInt(record.data.key);
				this.rightList.ksly=record.data.ksly;
				this.rightList.doCndQuery();
			}
		});