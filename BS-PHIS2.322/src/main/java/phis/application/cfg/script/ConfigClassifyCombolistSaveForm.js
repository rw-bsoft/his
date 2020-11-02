$package("phis.application.cfg.script")

/**
 * 分类类别维护 gaof 2013.03.25
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigClassifyCombolistSaveForm = function(cfg) {
	this.LBMCValue = "";
	phis.application.cfg.script.ConfigClassifyCombolistSaveForm.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.cfg.script.ConfigClassifyCombolistSaveForm,
		phis.script.TableForm, {
			// onReady : function() {
			// this.LBMCValue = this.form.getForm().findField("LBMC")
			// .getValue()
			// alert(this.LBMCValue);
			// },

			doSave : function() {
                this.form.el.mask("正在保存...", "x-mask-loading");
				var LBMC = this.form.getForm().findField("LBMC").getValue();
				if (LBMC.length > 15) {
					MyMessageTip.msg("提示", "类别名称过长", true);
                    this.form.el.unmask();
					return;
				}
                if (LBMC==null||LBMC=="") {
                    MyMessageTip.msg("提示", "类别名称不能为空", true);
                    this.form.el.unmask();
                    return;
                }
				var data = {};
				data["LBMC"] = LBMC;
				data["LBXH"] = this.initDataId;
				data["OP"] = this.op;
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "isLBMCUsed",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
							if (json.isClassifyCancel) {
								Ext.Msg.alert("错误", "已注销的分类类别不能修改");
								return;
							}
							if (json.countLBMC > 0) {
								Ext.Msg.alert("错误", "类别名称重复");
								return;
							}
							// if (this.saving) {
							// return
							// }
							// var values = this.getFormData();
							// if (!values) {
							// return;
							// }
							// Ext.apply(this.data, values);
							// this.saveToServer(values);
							// this.doCancel();
							phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : "saveClassify",
										body : data
									}, function(code, msg, json) {
										if (code >= 300) {
											MyMessageTip.msg("提示", msg, true);
										}
                                        this.form.el.unmask();
										this.fireEvent("save")
										this.doCancel();
									}, this)

						}, this)
			}

		})
