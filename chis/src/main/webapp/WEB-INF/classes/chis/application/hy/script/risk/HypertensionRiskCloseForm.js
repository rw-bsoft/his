$package("chis.application.hy.script.risk")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hy.script.risk.HypertensionRiskCloseForm = function(cfg) {
	cfg.colCount  = 3
	chis.application.hy.script.risk.HypertensionRiskCloseForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow",this.onWinShow,this)
}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskCloseForm,
		chis.script.BizTableFormView, {
			onWinShow:function(){
				this.loadData()
			}
		});