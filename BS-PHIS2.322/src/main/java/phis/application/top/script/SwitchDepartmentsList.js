$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.SimpleList")

com.bsoft.phis.pub.SwitchDepartmentsList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true;
	cfg.width = 185;
	cfg.height = 300;
	com.bsoft.phis.pub.SwitchDepartmentsList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(com.bsoft.phis.pub.SwitchDepartmentsList,
		com.bsoft.phis.SimpleList, {
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