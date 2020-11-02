$package("phis.application.mds.script")

$import("phis.script.TableForm")

phis.application.mds.script.MedicinesDosageForm = function(cfg) {
	cfg.repeatInspectionServiceId = "medicinesManageService";
	cfg.repeatInspectionActionId = "propertyAdd";
	cfg.width = 400;
	phis.application.mds.script.MedicinesDosageForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.mds.script.MedicinesDosageForm, phis.script.TableForm, {
			// 保存前检验药品剂型名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				var body = {"keyName":"SXMC","keyValue":saveRequest.SXMC,"tableName":"YK_YPSX"};
				var ypsx = saveRequest.YPSX;
				if (ypsx) {
					body.PKName="YPSX";
					body.PKValue=ypsx;
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