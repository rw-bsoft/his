$package("phis.application.cic.script")
$import("phis.script.TableForm")
phis.application.cic.script.ClinicTherapeuticRegimenForm = function(cfg) {
	cfg.colCount = 1;
	phis.application.cic.script.ClinicTherapeuticRegimenForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSaves, this);
}

Ext.extend(phis.application.cic.script.ClinicTherapeuticRegimenForm,
		phis.script.TableForm, {
			onBeforeSaves : function(entryName, op, saveData) {
				if (!this.initDataId) {
					MyMessageTip.msg("提示", "请先维护诊疗方案名称", true);
					return false;
				}
			}
		});