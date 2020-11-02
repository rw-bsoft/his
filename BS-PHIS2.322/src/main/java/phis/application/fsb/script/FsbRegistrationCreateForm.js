$package("phis.application.fsb.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil","phis.application.fsb.script.FsbApplicationForm")

phis.application.fsb.script.FsbRegistrationCreateForm = function(cfg) {
	phis.application.fsb.script.FsbRegistrationCreateForm.superclass.constructor.apply(
			this, [cfg])
	this.colCount = 8;
	this.labelWidth = 55;
	this.width = 900;
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.fsb.script.FsbRegistrationCreateForm,
		phis.application.fsb.script.FsbApplicationForm, {
			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					Ext.Msg.alert("提示", "表单不完整，请校验!");
					return;
				}
				values.SQZT = 2;
				values.SQFS = values.SQFS || "3";
				Ext.apply(this.data, values);
				Ext.Msg.show({
							title : '提交',
							msg : '确认提交家床申请?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.saveToServer(values);
									this.opener.doQueryBrsq(values.MZHM,2);
									this.doCancel();
								}
								this.doCancel();
							},
							scope : this
						})
			},
			onBeforeSave : function(entryName, op, saveData) {
				if (this.op == "create") {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "checkRepetition",
								cnds : ['eq', ['$', 'BRID'],
										['i', this.BRXX.BRID]],
								entryName : entryName
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return false;
					} else {
						if (r.json.body) {
							Ext.Msg.alert("提示", "请勿重复申请!");
							return false;
						}
					}
				} else {
					if (this.data.SQZT == "2") {
						Ext.Msg.alert("提示", "请先退回申请!");
						return false;
					}
				}
			}
		});