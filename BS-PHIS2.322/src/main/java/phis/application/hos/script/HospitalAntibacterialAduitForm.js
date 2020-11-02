$package("phis.application.hos.script")

$import("phis.application.hos.script.HospitalAntibacterialApplyForm")

phis.application.hos.script.HospitalAntibacterialAduitForm = function(cfg) {
	cfg.render = "aduit";
	phis.application.hos.script.HospitalAntibacterialAduitForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.afterLoadData, this);
}

Ext.extend(phis.application.hos.script.HospitalAntibacterialAduitForm,
		phis.application.hos.script.HospitalAntibacterialApplyForm, {
			aduitChange : function(group, newValue, oldValue) {
				if (newValue.inputValue == '1') {
					this.form.getForm().findField("SPYL").setDisabled(false);
				} else {
					this.form.getForm().findField("SPYL").setValue("");
					this.form.getForm().findField("SPYL").setDisabled(true);
				}
			},
			afterLoadData : function() {
				this.countTotal();
				if (this.data.DJZT > 0) {
					document.getElementById(this.render + "_status").innerHTML = this.data.SPJG > 0
							? "已审核"
							: "已提交";
					this.form.getForm().findField("YPMC").setDisabled(true);
					if (this.form.getForm().findField("SPSL")) {
						this.form.getForm().findField("SPSL").focus(true, 200);
					}
				} else {
					document.getElementById(this.render + "_status").innerHTML = "新增"
				}
				// 已提交记录不允许修改
				var btns = this.form.getTopToolbar().items;
				if (btns) {
					if (this.data.DJZT > 1 || this.data.SPJG > 0
							|| this.action == "look") {
						btns.item(0).setDisabled(true);
					} else {
						btns.item(0).setDisabled(false);
						this.radioGroup.setValue("1");
						this.form.getForm().findField("SPYL").setValue("");
						this.form.getForm().findField("SPYL")
								.setDisabled(false);
					}
				}
				if (this.action == "look") {
					this.radioGroup.setDisabled(true);
					this.form.getForm().findField("SPYL").setDisabled(true);
				} else {
					this.radioGroup.setDisabled(false);
				}
			},
			doSave : function() {
				var values = this.getFormData();
				if (!values)
					return;
				var SPJG = this.radioGroup.getValue().inputValue;
				var SPYL = this.form.getForm().findField("SPYL").getValue();
				if (!SPJG) {
					MyMessageTip.msg("提示", "审批结果不能为空!", true);
					this.form.getForm().findField("SPYL").focus(true, 20);
					return;
				}
				if (SPJG == 1 && (!SPYL || SPYL <= 0)) {
					MyMessageTip.msg("提示", "审批用量必须大于0!", true);
					return;
				}
				values.SPJG = SPJG;
				values.DJZT = 2;
				this.saveToServer(values);
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				// if (this.initDataId == null) {
				// this.op = "create";
				// }
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							MyMessageTip.msg("提示", "审批成功!", true);
							if (json.body.SQDH) {
								this.initDataId = json.body.SQDH;
							}
							this.op = "update"
							this.fireEvent("save");
							this.win.hide();
						}, this)// jsonRequest
			}
		});