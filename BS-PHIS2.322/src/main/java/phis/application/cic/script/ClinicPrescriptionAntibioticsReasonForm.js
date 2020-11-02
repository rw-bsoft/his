$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicPrescriptionAntibioticsReasonForm = function(
		cfg) {
	cfg.colCount = 2;
	// cfg.autoFieldWidth = false;
	cfg.labelWidth = 120;
	cfg.width = 450;
	cfg.showButtonOnTop = false;
	phis.application.cic.script.ClinicPrescriptionAntibioticsReasonForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(phis.application.cic.script.ClinicPrescriptionAntibioticsReasonForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				var form = phis.application.cic.script.ClinicPrescriptionAntibioticsReasonForm.superclass.initPanel
						.call(this, sc);
				var checkBox = new Ext.form.Checkbox({
							name : "QT",
							boxLabel : "其它",
							width : 125
						});
				var textField = new Ext.form.TextField({
							name : "QTYY",
							width : 150,
							maxLength : 200,
							disabled : true
						});
				checkBox.on("check", function(f, checked) {
							textField.setDisabled(!checked)
						}, this);
				var panel = new Ext.Container({
							colspan : 2,
							layout : "hbox",
							items : [checkBox, textField]
						})
				this.form.insert(1, panel);
				return form;
			},
			onWinShow : function() {
				var form = this.form.getForm();
				if (this.syyy) {
					var arrays = this.syyy.split('-');
					if (arrays.length > 1) {
						form.findField("SYYY").setValue(arrays[0]);
						form.findField("QT").setValue(true);
						form.findField("QTYY").setValue(arrays[1]);
					} else {
						form.findField("SYYY").setValue(arrays[0]);
						form.findField("QT").setValue(false);
						form.findField("QTYY").setValue("");
					}
				} else {
					form.findField("SYYY").setValue("");
					form.findField("QT").setValue(false);
					form.findField("QTYY").setValue("");
				}
				// this.doNew();
			},
			doConfirm : function() {
				var form = this.form.getForm();
				var syyy = form.findField("SYYY").getValue();
				if (form.findField("QT").getValue()) {
					syyy += "-" + form.findField("QTYY").getValue();
				}
				if (syyy.realLength() > 200) {
					MyMessageTip.msg("提示", "抗菌药物使用原因操作允许的最大长度!允许录入的最大长度为" + 200
									+ ",已输入长度为" + syyy.realLength(), true);
					return;
				}
				this.win.hide();
				this.fireEvent("AntibioticsConfirm", syyy);
			},
			doClose : function() {
				this.fireEvent("AntibioticsConfirm", "");
				this.win.hide();
			}
		});