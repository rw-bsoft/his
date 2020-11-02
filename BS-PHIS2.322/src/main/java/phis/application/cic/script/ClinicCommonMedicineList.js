$package("phis.application.cic.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

phis.application.cic.script.ClinicCommonMedicineList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	phis.application.cic.script.ClinicCommonMedicineList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cic.script.ClinicCommonMedicineList, phis.script.SimpleList,
		{
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			}
		})
