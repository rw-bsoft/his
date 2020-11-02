$package("chis.application.conf.script.mdc")
$import("chis.script.BizEditorListView")
chis.application.conf.script.mdc.HypertensionGroupManageList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	chis.application.conf.script.mdc.HypertensionGroupManageList.superclass.constructor.apply(
			this, [cfg]);
	this.requestData.cnd = this.cnds;
	this.saveServiceId = "chis.hypertensionConfigManageService";
	this.saveAction = "saveHypertensionControl";
}
Ext.extend(chis.application.conf.script.mdc.HypertensionGroupManageList,
		chis.script.BizEditorListView, {
			doSave : function() {
				var data = this.getListData();
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction,
							method:"execute",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.store.commitChanges();
						}, this)
			}
			
});