$package("chis.application.dbs.script.risk")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.dbs.script.risk.DiabetesRiskCloseForm = function(cfg) {
	cfg.colCount  = 3
	chis.application.dbs.script.risk.DiabetesRiskCloseForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow",this.onWinShow,this)
}
Ext.extend(chis.application.dbs.script.risk.DiabetesRiskCloseForm,
		chis.script.BizTableFormView, {
			onWinShow:function(){
				this.loadData()
			}
		});