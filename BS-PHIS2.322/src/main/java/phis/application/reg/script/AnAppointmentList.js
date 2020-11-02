
$package("phis.application.reg.script");

$import("phis.script.SimpleList")

phis.application.reg.script.AnAppointmentList = function(cfg) {
	cfg.enableCnd = false;
	cfg.gridDDGroup = true;
	cfg.autoLoadData = false;
	phis.application.reg.script.AnAppointmentList.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.reg.script.AnAppointmentList, phis.script.SimpleList, {
	onDblClick : function(grid, index, e) {
		var lastIndex = grid.getSelectionModel().lastActive;
		var record = grid.store.getAt(lastIndex);
		if (record) {
			this.fireEvent("reservationChoose", grid, record);
			this.doCancel();
		}
	},
	doCancel : function() {
		if (this.opener.win) {
			this.opener.win.hide();
		}
	},
	doCommit : function(grid) {
		var rs = this.grid.getSelectionModel().getSelected();
		if(rs){
			this.fireEvent("reservationChoose", grid, rs);
			this.doCancel();
		} else {
			this.doCancel();
		}
	}
});