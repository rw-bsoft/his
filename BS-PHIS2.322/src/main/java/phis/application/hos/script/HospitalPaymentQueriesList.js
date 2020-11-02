$package("phis.application.hos.script")

$import("phis.script.SimpleList",'phis.prints.script.HospitalPaymentQueriesPrintView')

phis.application.hos.script.HospitalPaymentQueriesList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hos.script.HospitalPaymentQueriesList.superclass.constructor.apply(
			this, [cfg]);
	this.disableContextMenu = true;
}

Ext.extend(phis.application.hos.script.HospitalPaymentQueriesList,
		phis.script.SimpleList, {
			zfpbRender : function(v, params, reocrd) {
				if (v == '作废') {
					params.style = "color:red;";
				}
				return v;
			},
			doPrint: function(){
				var cm = this.grid.getColumnModel()
				var pWin = this.midiModules["printView"]
				var cfg = {
					title : this.printTitle || this.title,
					requestData : this.requestData,
					cm:cm
				}
				if(pWin){
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.HospitalPaymentQueriesPrintView(cfg)
				this.midiModules["printView"] = pWin
				pWin.getWin().show()
			}
		});