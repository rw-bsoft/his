$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.WardClinicsRefundList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.disablePagingTbr = true;
	cfg.selectOnFocus = true;
	phis.application.war.script.WardClinicsRefundList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.WardClinicsRefundList,
		phis.script.EditorList, {
			expansion : function(cfg) {
				cfg.sm = new Ext.grid.RowSelectionModel();
			}
		})