$package("chis.application.conf.script.pub")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.pub.InterfaceManageForm = function(cfg) {
	cfg.fldDefaultWidth = 220
	cfg.autoFieldWidth = false;
	chis.application.conf.script.pub.InterfaceManageForm.superclass.constructor.apply(this,
			[cfg])
	this.colCount = 2;
	this.saveServiceId = "chis.interfaceManageService";
	this.saveAction = "saveConfig";
    this.loadServiceId = "chis.interfaceManageService";
	this.loadAction = "getConfig"
}
Ext.extend(chis.application.conf.script.pub.InterfaceManageForm,
		chis.application.conf.script.SystemConfigUtilForm, {
      
			doTest : function() {
				if (this.testing)
					return
				if (this.form && this.form.el)
					this.form.el.mask("正在测试连接.....", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "testConfig",
							method:"execute",
							body : this.getFormData()
						}, function(code, msg, json) {
							if (this.form && this.form.el)
								this.form.el.unmask();
							this.testing = false
							if (code > 300) {
				                this.processReturnMsg(code, msg);
				                return
				            }
							if (code == 200) {
								Ext.MessageBox.alert("提示", "测试成功！")
								return
							} else {
								Ext.MessageBox.alert("提示", "测试失败！")
								return
							}
						}, this)
			}
      
		});