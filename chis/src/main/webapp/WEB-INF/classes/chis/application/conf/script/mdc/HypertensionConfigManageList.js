$package("chis.application.conf.script.mdc")
$import("chis.script.BizEditorListView")
chis.application.conf.script.mdc.HypertensionConfigManageList = function(cfg) {
	cfg.cnds = ["eq", ["$", "dicType"], ["s", "1"]]
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	chis.application.conf.script.mdc.HypertensionConfigManageList.superclass.constructor.apply(
			this, [cfg]);
	this.requestData.cnd = this.cnds;
}
Ext.extend(chis.application.conf.script.mdc.HypertensionConfigManageList,
		chis.script.BizEditorListView, {

});