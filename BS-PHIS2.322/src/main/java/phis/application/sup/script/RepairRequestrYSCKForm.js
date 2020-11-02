$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.RepairRequestrYSCKForm = function(cfg) {
	cfg.width = 700;
	cfg.height = 600;
	cfg.colCount = 3;
	phis.application.sup.script.RepairRequestrYSCKForm.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.application.sup.script.RepairRequestrYSCKForm, phis.script.TableForm, {
			doYS : function() {
				phis.application.sup.script.RepairRequestrYSCKForm.superclass.doNew.call(this);
						var btns =  this.form.getTopToolbar().items;
						if (!btns) {
							return;
						}
						var n = btns.getCount();
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i);
							if(this.WXZT==3){
								if(i == 1){
									btn.enable();
								}else{
									btn.disable();
								}
							}else{
								btn.enable();
							}
						}
				this.fillRkfs();
			},
			fillRkfs : function() {
					var form = this.form.getForm();
					if(form){
					    form.findField("SYKS").setValue(this.SYKS);
						form.findField("SYKS").setRawValue(this.SYKS_text);
						form.findField("JJCD").setValue(this.JJCD);
						form.findField("JJCD").setRawValue(this.JJCD_text);
						form.findField("JJCD").focus(false,200);
						form.findField("KFXH").setValue(this.KFXH);
						form.findField("KFXH").setRawValue(this.KFXH_text);
						form.findField("SQGH").setValue(this.SQGH);
						form.findField("SQGH").setRawValue(this.SQGH_text);
						form.findField("LXDH").setValue(this.LXDH);
						form.findField("SXRQ").setValue(this.SXRQ);
						form.findField("BZXX").setValue(this.BZXX);
						form.findField("GZMS").setValue(this.GZMS);
						if(this.JSCD){
							form.findField("JSCD").setValue(this.JSCD);
							form.findField("JSCD").setRawValue(this.JSCD_text);
							}
							if(this.MYCD){
							form.findField("MYCD").setValue(this.MYCD);
							form.findField("MYCD").setRawValue(this.MYCD_text);
							}
					    form.findField("SYKS").disable();
						form.findField("JJCD").disable();
						form.findField("SQGH").disable();
						form.findField("LXDH").disable();
						form.findField("SXRQ").disable();
						form.findField("BZXX").disable();
						form.findField("GZMS").disable();
					}
				
			},
			saveToServer : function(saveData) {
					var bodys = {};
					bodys["WXXH"] = this.WXXH;
					bodys["JSCD"] = saveData.JSCD;
					bodys["MYCD"] = saveData.MYCD;
					this.saving = true
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
								serviceId : "repairRequestrService",
							    serviceAction :"acceptanceform",
								body : bodys
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