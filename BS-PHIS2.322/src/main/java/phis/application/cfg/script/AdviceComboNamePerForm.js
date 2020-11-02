$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.AdviceComboNamePerForm = function(cfg) {
	this.serviceId = "clinicComboNameService";
	this.serviceAction = "clinicComboNameVerification"
	phis.application.cfg.script.AdviceComboNamePerForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.AdviceComboNamePerForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.cfg.script.AdviceComboNamePerForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var f = form.findField("ZTLB");
				if (f) {
					f.store.on("load", this.fillRkfs, this);
				}
			},
			doNew : function() {
				phis.application.cfg.script.AdviceComboNamePerForm.superclass.doNew
						.call(this);
				this.fillRkfs();
			},
			// 新增页面打开药品类别值带过来
			fillRkfs : function() {
				/*
				 * if(!this.ZTLB) return; if (this.ZTLB && this.op == "create") {
				 * var form = this.form.getForm(); if(form){
				 * form.findField("ZTLB").setValue(this.ZTLB); } }
				 */
			},
			/*
			 * 重写getSaveRequest 目的是为了把待保存的数据里的SSLB
			 */
			getSaveRequest : function(saveData) {
				var values = saveData;
				if (this.ZTLB) {
					values["ZTLB"] = this.ZTLB;
				} else {
					values["ZTLB"] = 1;
				}
				if (this.SSLB) {
					values["SSLB"] = this.SSLB;
				} else {
					values["SSLB"] = 1;
				}
				if (this.KSDM && this.KSDM != 0) {
					if (this.KSDM == -1) {
						values["KSDM"] = this.mainApp['phis'].wardId;
					} else {
						values["KSDM"] = this.KSDM;
					}
				} else {
					if (this.KSDM == 0) {
						values["KSDM"] = '';
					} else {
						values["KSDM"] = this.mainApp['phis'].wardId;
					}
				}
				return values;
			},
			onBeforeSave : function(entryName, op, saveData) {
				var data = {
					"ZTMC" : saveData.ZTMC,
					"YGDM" : saveData.YGDM,
					"JGID" : saveData.JGID,
					"SSLB" : saveData.SSLB,
					"ZTLB" : saveData.ZTLB,
					"KSDM" : saveData.KSDM
				};
				if (this.initDataId) {
					data["ZTBH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							method : "execute",
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
		});