$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicdiagnosisOftenList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.disablePagingTbr = true;
	phis.application.cic.script.ClinicdiagnosisOftenList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicdiagnosisOftenList,
		phis.script.SimpleList, {
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			}
		})
