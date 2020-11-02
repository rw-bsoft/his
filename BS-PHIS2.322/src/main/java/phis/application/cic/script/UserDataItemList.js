$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.UserDataItemList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = true;
	phis.application.cic.script.UserDataItemList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.UserDataItemList, phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				this.fireEvent("itemDbClick");
			}
		});