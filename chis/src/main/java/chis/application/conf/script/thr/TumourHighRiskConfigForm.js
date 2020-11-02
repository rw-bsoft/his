$package("chis.application.conf.script.thr");

$import("chis.application.conf.script.RecordManageYearConfigForm");

chis.application.conf.script.thr.TumourHighRiskConfigForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 220
	cfg.colCount = 2;
	cfg.labelWidth = 120;
	chis.application.conf.script.thr.TumourHighRiskConfigForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.conf.script.thr.TumourHighRiskConfigForm,
		chis.application.conf.script.RecordManageYearConfigForm, {
			saveToServer : function(saveData) {
				this.fireEvent("save", saveData, this);
			}
		});