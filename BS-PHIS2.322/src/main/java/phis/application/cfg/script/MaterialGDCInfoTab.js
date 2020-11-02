$package("phis.application.cfg.script")
$import("phis.script.common", "phis.script.TableForm")

phis.application.cfg.script.MaterialGDCInfoTab = function(cfg) {
	phis.application.cfg.script.MaterialGDCInfoTab.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.MaterialGDCInfoTab, phis.script.TableForm, {
			onReady : function() {
				phis.application.cfg.script.MaterialGDCInfoTab.superclass.onReady
						.call(this);
				var gcsl = this.form.getForm().findField("GCSL");
				var dcsl = this.form.getForm().findField("DCSL");
				gcsl.setValue(this.GCSL);
				dcsl.setValue(this.DCSL);
			},
			setGDC : function(dcslvalue, gcslvalue) {
				if (this.form) {
					var gcsl = this.form.getForm().findField("GCSL");
					var dcsl = this.form.getForm().findField("DCSL");
					gcsl.setValue(gcslvalue);
					dcsl.setValue(dcslvalue)
				}
			},
			doSave : function() {
				var gcsl = this.form.getForm().findField("GCSL");
				var dcsl = this.form.getForm().findField("DCSL");
				if (parseInt(gcsl.getValue()) < 0
						|| parseInt(dcsl.getValue()) < 0) {
					MyMessageTip.msg("提示", "数量不能小于0!", true);
					return;
				}
				if (parseInt(gcsl.getValue()) < parseInt(dcsl.getValue())) {
					MyMessageTip.msg("提示", "高储数量不能小于低储备数量!", true);
					return;
				}
				var data = {
					"WZXH" : this.WZXH,
					"GCSL" : gcsl.getValue(),
					"DCSL" : dcsl.getValue()
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "materialInformationManagement",
							serviceAction : "saveGDCMaterialInfo",
							body : data
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							} else {
								MyMessageTip.msg("提示", "保存成功!", true);
								this.doCancel();
								this.oper.refresh();
							}
						}, this)
			},
			doCancel : function() {
				this.getWin().hide();
			}
		})