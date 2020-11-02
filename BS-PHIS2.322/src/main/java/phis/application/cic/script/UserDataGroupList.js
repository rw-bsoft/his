$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.UserDataGroupList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	cfg.entryName = this.entryName || "phis.application.emr.schemas.EMR_BL01_BRRY";
	cfg.group = "RYRQ";
	phis.application.cic.script.UserDataGroupList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.UserDataGroupList, phis.script.SimpleList, {	
	
			doCancel : function() {
				this.fireEvent("cancel", this);
			},
			onDblClick : function(grid, index, e) {
				var record = this.getSelectedRecord();
				if (record == null) {
					return
				}
				this.fireEvent("appoint", record.data, 3);
			},
			doOpen : function() {
				this.onDblClick();
			}
		});