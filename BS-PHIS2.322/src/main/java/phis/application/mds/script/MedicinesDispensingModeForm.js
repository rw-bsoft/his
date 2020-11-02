$package("phis.application.mds.script")

$import("phis.script.TableForm")

phis.application.mds.script.MedicinesDispensingModeForm = function(cfg) {
	cfg.repeatInspectionServiceId = "medicinesManageService";
	cfg.repeatInspectionActionId = "dispensingAdd";//dispensingAdd
	cfg.width = 400;
	phis.application.mds.script.MedicinesDispensingModeForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.mds.script.MedicinesDispensingModeForm, phis.script.TableForm, {
			// 保存前检验发药方式名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				var body = {"keyName":"FSMC","keyValue":saveRequest.FSMC,"tableName":"ZY_FYFS"};
				var fyfs = saveRequest.FYFS;
				if (fyfs) {
					body.PKName="FYFS";
					body.PKValue=fyfs;
				} 
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.repeatInspectionServiceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return false;
				}
				return true;
			}

		});