$package("phis.application.cfg.script")

/**
 * 病人性质维护from zhangyq 2012.5.25
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigPatientPropertiesForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width = 500;
	this.serviceId = "configPatientPropertiesService";
	this.actionId = "savePatientNature";
	phis.application.cfg.script.ConfigPatientPropertiesForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigPatientPropertiesForm,
		phis.script.TableForm, {
			doDepartmentNew : function(node) {
				this.node = node;
				this.doNew();
				/*var pid = node.id
				var ptext = node.text
				var pjgid = node.attributes.GSXZ;
				this.data["SJXZ"] = pid;
				this.data["GSXZ"] = pjgid;*/
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				this.saveToServer(values);
			},
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData); // **
																	// 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							op : this.op,
							method : "execute",
							body : saveRequest
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return;
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.op = "update";
						}, this)
			},
			onBeforeSave : function(entryName, op, saveData) {
				var data = {
					"XZMC" : saveData.XZMC
				};
				if (this.initDataId) {
					data["BRXZ"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configPatientNatureService",
							serviceAction : "patientNatureNameVerification",
							schemaDetailsList : "GY_BRXZ",
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
			},
			doNew : function(){
				phis.application.cfg.script.ConfigPatientPropertiesForm.superclass.doNew.call(this);
//				this.initDataId = "";
				var node = this.node;
				var pid = node.id
				var ptext = node.text
				var pjgid = node.attributes.GSXZ;
				this.data["SJXZ"] = pid;
				this.data["GSXZ"] = pjgid;
			}
		})