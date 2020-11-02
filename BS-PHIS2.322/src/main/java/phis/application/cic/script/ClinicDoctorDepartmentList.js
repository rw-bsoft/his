$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicDoctorDepartmentList = function(cfg) {
	phis.application.cic.script.ClinicDoctorDepartmentList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.ClinicDoctorDepartmentList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				/*var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("dblChoose", record);
				}*/
			},
			onRowClick : function(grid, index, e) {
				/*var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}*/
			},
			onESCKey : function() {
				this.cndField.focus();
			}
	});