$package("chis.application.pub.script");

$import("chis.script.BizSimpleListView",
		"chis.application.pub.script.DrugImportModule",
		"chis.script.util.widgets.MyMessageTip");

chis.application.pub.script.PelpleHealthDiagnoseList = function(cfg) {
	chis.application.pub.script.PelpleHealthDiagnoseList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.pub.script.PelpleHealthDiagnoseList,
		chis.script.BizSimpleListView, {
			doAddDiagnose : function() {
				if (!this.recordId) {
					MyMessageTip.msg("提示", "请先保存健康处方内容！", true, 5);
					return;
				}
				var module = this.createSimpleModule("refDiagnoseModule",
						this.refModule);
				module.on("select", this.recordsSelect, this);
				var win = module.getWin();
				win.show();
			},
			recordsSelect : function(records, diagnoseType) {
				this.fireEvent("select", records, diagnoseType, this);
			},
			doClear : function() {
				this.fireEvent("clear", this);
			}
		});