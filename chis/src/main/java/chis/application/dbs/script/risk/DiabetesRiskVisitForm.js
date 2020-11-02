$package("chis.application.dbs.script.risk")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.dbs.script.risk.DiabetesRiskVisitForm = function(cfg) {
	cfg.colCount  = 3
	cfg.isCombined = false
	cfg.autoLoadSchema = true
	cfg.labelWidth = 140;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
//	cfg.width = 780
	chis.application.dbs.script.risk.DiabetesRiskVisitForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(chis.application.dbs.script.risk.DiabetesRiskVisitForm,
		chis.script.BizTableFormView, {
//			onReady : function() {
//				chis.application.dbs.script.risk.DiabetesRiskVisitForm.superclass.onReady
//						.call(this)
//				
//			},
			saveToServer:function(saveData){
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				saveData.planId = this.exContext.args.planId
				saveData.planDate = this.exContext.args.planDate
				saveData.sn = this.exContext.args.sn
				saveData.riskId = this.exContext.ids.riskId
				
				chis.application.dbs.script.risk.DiabetesRiskVisitForm.superclass.saveToServer.call(this,saveData)
			}
			,
			setButtonEnable : function(status) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (status)
						btn.enable()
					else
						btn.disable()
				}
			}
		});