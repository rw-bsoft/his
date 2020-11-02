$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.AntimicrobialDrugUseCausesForm = function(cfg) {
	phis.application.cfg.script.AntimicrobialDrugUseCausesForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.AntimicrobialDrugUseCausesForm,
		phis.script.TableForm, {

			saveToServer : function(saveData) {
				phis.script.rmi.jsonRequest({
									serviceId : "phis.AntimicrobialDrugUseCausesService",
									serviceAction : "updateAntimicrobialDrug",
									data:saveData
								}, function(code, msg, json) {
									if (code >= 300) {
										this.processReturnMsg(code, msg);
									}else{
										MyMessageTip.msg("提示", "保存成功!", true);
									}
									this.opener.refresh();
								}, this)
			}
		})