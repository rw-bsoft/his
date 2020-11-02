$package("phis.application.mds.script.")

$import("phis.script.TableForm")

phis.application.mds.script.MedicinesManufacturerForm = function(cfg) {
	cfg.repeatInspectionServiceId = "medicinesManageService";
	cfg.repeatInspectionActionId = "validationManufacturerModeName";
	cfg.colCount = 2;
	phis.application.mds.script.MedicinesManufacturerForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.mds.script.MedicinesManufacturerForm,
		phis.script.TableForm, {
			// 新增生产厂家时，录入全称后应默认简称值和全称一样，但是简称还可以再编辑
			onReady : function() {
				phis.application.mds.script.MedicinesManufacturerForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var CDQC = form.findField("CDQC");
				if (CDQC) {
					CDQC.on("change", this.onCDQCBlur, this);
				}
			},

			onCDQCBlur : function(f) {
				var form = this.form.getForm();
				var CDMC = form.findField("CDMC");
				if (CDMC) {
					CDMC.setValue(f.getValue());
					var field = {};
					field.codeType = [];
					field.srcField = [];
					field.codeType.push("py");
					field.srcField.push("PYDM");
					this.onChange(field, f.getValue());
				}
			},

			// 保存前检验产地名称和产地简称是否有重复的
			onBeforeSave : function(entryName, op, saveRequest) {
				var body = {};
				body["cdmc"] = saveRequest.CDQC;
				body["cdjc"] = saveRequest.CDMC;
				body["ypcd"] = saveRequest.YPCD;
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