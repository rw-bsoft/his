$package("phis.application.fsb.script")

$import("phis.script.SimpleList",
		'phis.prints.script.FsbPaymentQueriesPrintView')

phis.application.fsb.script.FsbPaymentQueriesList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FsbPaymentQueriesList.superclass.constructor.apply(this,
			[cfg]);
	this.disableContextMenu = true;
}

Ext.extend(phis.application.fsb.script.FsbPaymentQueriesList, phis.script.SimpleList, {
			zfpbRender : function(v, params, reocrd) {
				if (v == '作废') {
					params.style = "color:red;";
				}
				return v;
			},
			doPrint : function() {
				var cm = this.grid.getColumnModel()
				var pWin = this.midiModules["printView"]
				var cfg = {
					title : this.printTitle || this.title,
					requestData : this.requestData,
					cm : cm
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.FsbPaymentQueriesPrintView(cfg)
				this.midiModules["printView"] = pWin
				pWin.getWin().show()
			}
		});