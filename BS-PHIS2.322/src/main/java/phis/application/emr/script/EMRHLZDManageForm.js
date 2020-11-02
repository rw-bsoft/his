$package("phis.application.emr.script")

$import("phis.script.TableForm")

phis.application.emr.script.EMRHLZDManageForm = function(cfg) {
	// cfg.actions = [
	// {id:"save",name:"保存"},
	// {id:"up",name:"上标",iconCls:"arrow-up"},
	// {id:"down",name:"下标",iconCls:"arrow-down"}
	// ]
	cfg.colCount = 1;
	cfg.width = "480";
	phis.application.emr.script.EMRHLZDManageForm.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRHLZDManageForm,
		phis.script.TableForm, {
			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					return;
				}
				// this.form.el.mask("正在保存数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.emrManageService",
							serviceAction : "saveHLZDPre",
							body : values
						});
				// this.form.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					if (ret.json.num == 0) {
						Ext.apply(this.data, values);
						this.saveToServer(values);
					} else {
						MyMessageTip.msg("提示", "存在相同名称的诊断，无法保存", true);
						return;
					}

				}
			}
		})
