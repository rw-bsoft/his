$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalPaymentRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true;
	phis.application.hos.script.HospitalPaymentRecordList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalPaymentRecordList,
		phis.script.SimpleList, {
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onReady : function() {
				this.on("winShow",this.onWinShow,this);
			},
			onWinShow : function(){
				 this.refresh();
			}
		});