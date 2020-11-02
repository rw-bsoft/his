$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.RepairRequestrForm = function(cfg) {
	cfg.width = 700;
	cfg.height = 600;
	cfg.colCount = 3;
	phis.application.sup.script.RepairRequestrForm.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.application.sup.script.RepairRequestrForm, phis.script.TableForm, {
			doUpdate : function() {
				var form = this.form.getForm();
					if(form){
						form.findField("SYKS").setValue(this.SYKS);
						form.findField("SYKS").setRawValue(this.SYKS_text);
						form.findField("JJCD").setValue(this.JJCD);
						form.findField("JJCD").setRawValue(this.JJCD_text);
						form.findField("JJCD").focus(false,200);
						form.findField("KFXH").setValue(this.KFXH);
						form.findField("KFXH").setRawValue(this.KFXH_text);
						form.findField("KFXH").focus(false,200);
						form.findField("SQGH").setValue(this.SQGH);
						form.findField("SQGH").setRawValue(this.SQGH_text);
						form.findField("LXDH").setValue(this.LXDH);
						form.findField("SXRQ").setValue(this.SXRQ);
						form.findField("BZXX").setValue(this.BZXX);
						form.findField("GZMS").setValue(this.GZMS);
					}
			},
			saveToServer : function(saveData) {
				var op="create";
					if(this.WXXH){
						saveData.WXXH = this.WXXH;
						op="update";
					}
					this.saving = true
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
								serviceId : "repairRequestrService",
							    serviceAction :"saveform",
								// schema : "WL_WXBG",
								body : saveData,
								op : op
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false
								if (code > 300) {
									this.processReturnMsg(code, msg, this.saveToServer,
											[saveData]);
									return
								}
								Ext.apply(this.data, saveData);
								if (json.body) {
									this.initFormData(json.body)
									this.fireEvent("save", this.entryName, this.op, json,
											this.data)
								}
								MyMessageTip.msg("提示", "保存成功！", true);
								this.fireEvent("refresh", this);
								this.doCancel();
							}, this)// jsonRequest
				}
		})