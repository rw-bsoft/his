$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseBasicInfomationForm = function(cfg) {
	cfg.repeatInspectionServiceId = "storehouseManageService";
	cfg.repeatInspectionActionId = "repeatInspection";
	phis.application.sto.script.StorehouseBasicInfomationForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.sto.script.StorehouseBasicInfomationForm,
		phis.script.TableForm, {
			// 保存前检验药库名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				var body = {};
				body["mc"] = saveRequest.YKMC;
				body["pkey"] = (saveRequest.YKSB == null || saveRequest.YKSB == "")
						? 0
						: saveRequest.YKSB;
				body["pb"] = "yk";
				body["jgid"]=saveRequest.JGID;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.repeatInspectionServiceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "药库名称已经存在!";
					this.processReturnMsg(ret.code, msg, this.onBeforeSave);
					return false;
				} else {
					return true;
				}
			}
		});