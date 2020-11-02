$package("phis.application.fsb.script")

$import("phis.script.TableForm",
		"phis.application.fsb.script.FsbApplicationForm")

phis.application.fsb.script.FsbApplicationForEmrForm = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FsbApplicationForEmrForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FsbApplicationForEmrForm,
		phis.application.fsb.script.FsbApplicationForm, {
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				if (this.mainApp.jobId == 'phis.50'
						|| this.mainApp.jobId == 'phis.53') {
					values.SQZT = 1;
				}
				values.SQFS = values.SQFS || "3";
				values.JGID = this.mainApp.deptId;
				Ext.apply(this.data, values);
				Ext.Msg.show({
							title : '',
							msg : '是否提交家床申请?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									values.SQZT = 2;
									this.saveToServer(values);
									this.doCancel();
								}else{
									values.SQZT = 1;
									//this.saveToServer(values);
									this.doCancel();
								}
							},
							scope : this
						})
			}
		});