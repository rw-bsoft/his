$package("phis.application.hos.script");

$import("phis.script.SimpleList");

phis.application.hos.script.HospitalZYBRList = function(cfg) {
	phis.application.hos.script.HospitalZYBRList.superclass.constructor
			.apply(this, [ cfg ])
	this.autoLoadData = false;
	this.width = 800;
	this.modal = true;
}

Ext.extend(phis.application.hos.script.HospitalZYBRList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("zybrChoose", record);
				}
			}
		});