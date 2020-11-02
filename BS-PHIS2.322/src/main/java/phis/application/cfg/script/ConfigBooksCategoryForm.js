$package("phis.application.cfg.script");
/**
 * 账簿类别维护form shiwy 2013.11.05
 */
$import("phis.script.TableForm");

phis.application.cfg.script.ConfigBooksCategoryForm = function(cfg) {
	cfg.width = 800;
	cfg.modal = true;
	phis.application.cfg.script.ConfigBooksCategoryForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigBooksCategoryForm,
		phis.script.TableForm, {
			onBeforeSave : function(entryName, op, saveData) {
				var s = /^[0-9]+$/i;
				if (!s.test(saveData.SXH)) {
					MyMessageTip.msg("提示", "顺序号只能输入数字!", true);
					return false;
				}
				if (parseInt(saveData.SXH) < 1) {
					MyMessageTip.msg("提示", "顺序号要大于0!", true);
					return false;
				}
				var data = {
					"SXH" : saveData.SXH,
					"ZBMC" : saveData.ZBMC
				};
				if (this.initDataId) {
					data["ZBLB"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configBooksCategoryService",
							serviceAction : "sxhAndZBMCVerification",
							schemaDetailsList : "WL_ZBLB",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			}
		})