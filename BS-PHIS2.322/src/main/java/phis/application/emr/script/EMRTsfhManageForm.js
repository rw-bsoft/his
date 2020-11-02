$package("phis.application.emr.script")

$import("phis.script.TableForm")

phis.application.emr.script.EMRTsfhManageForm = function(cfg) {
	cfg.actions = [
		{id:"save",name:"保存"},
		{id:"up",name:"上标",iconCls:"arrow-up"},
		{id:"down",name:"下标",iconCls:"arrow-down"}
	]
	phis.application.emr.script.EMRTsfhManageForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRTsfhManageForm,
		phis.script.TableForm, {
			doUp : function(){
				var val = this.form.getForm().findField("XMQZ").getValue()
				this.form.getForm().findField("XMQZ").setValue(val + "↗2↖")
			},
			doDown : function(){
				var val = this.form.getForm().findField("XMQZ").getValue()
				this.form.getForm().findField("XMQZ").setValue(val + "↘2↙")
			},
			doSave : function(){
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.emrManageService",
					serviceAction : "saveTsfh",
					body : values
				});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					Ext.apply(this.data, values);
					this.saveToServer(values);
				}
			}
		})
