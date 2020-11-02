$package("chis.application.wl.script");

$import("chis.script.BizTableFormView", "util.widgets.LookUpField");

chis.application.wl.script.HssXmMatchForm = function(cfg) {
	cfg.autoLoadSchema = false
	chis.application.wl.script.HssXmMatchForm.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.wl.script.HssXmMatchForm,
		chis.script.BizTableFormView, {
			doMatch:function(){
				var values = this.getFormData();
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.hssXmManage",
					serviceAction : "matchXm",
					method:"execute",
					data : values
				})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.fireEvent("save");
			}
		});