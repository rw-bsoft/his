// @@ 就诊记录列表。
$package("chis.application.hy.script.visit");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.visit.HypertensionClinicList = function(cfg) {
	// this.empiId = cfg.empiId;
	cfg.entryName = "chis.application.hy.schemas.MDC_HypertensionClinicRecord";
	cfg.cnds = ['eq', ['$', 'empiId'], ['s', cfg.empiId]];
	cfg.actions = [{
				id : "import",
				name : "导入",
				iconCls : "healthDoc_import"
			}];
	cfg.title = "就诊记录";
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.isCombined = false;
	cfg.showButtonOnTop = true;
	cfg.modal = true;
	chis.application.hy.script.visit.HypertensionClinicList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.visit.HypertensionClinicList, chis.script.BizSimpleListView,{
	doImport : function() {
		this.fireEvent("import", this.getSelectedRecord(), this);
		this.win.hide();
	},

	onDblClick : function() {
		this.doImport();
	}
});