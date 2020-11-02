$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPaymentRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true;
	phis.application.fsb.script.FamilySickBedPaymentRecordList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedPaymentRecordList,
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