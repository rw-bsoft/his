$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMedicinesReceiveWayList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true;
	cfg.width = 185;
	cfg.height = 300;
	phis.application.pha.script.PharmacyMedicinesReceiveWayList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyMedicinesReceiveWayList,
		phis.script.SimpleList, {
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onReady : function() {
				this.on("winShow", this.onWinShow, this);
			},
			onWinShow : function() {
				this.refresh();
			},
			onDblClick : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					this.doCancel();
					return
				}
				this.fireEvent("doSubmit", r.data)
				this.doCancel();
			}
		});