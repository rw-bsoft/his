$package("chis.application.pub.script");

$import("chis.script.BizTabModule", "chis.script.util.widgets.MyMessageTip");

chis.application.pub.script.PelpleDiagnoseTabModule = function(cfg) {
	this.width = 1000;
	this.height = 580;
	this.activateId = 0;
	chis.application.pub.script.PelpleDiagnoseTabModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.pub.script.PelpleDiagnoseTabModule,
		chis.script.BizTabModule, {
			onLoadModule : function(moduleName, module) {
				module.on("select", this.recordsSelect, this);
			},
			recordsSelect : function(records, diagnoseType) {
				if (diagnoseType != "2" && diagnoseType != "3") {
					diagnoseType = "1";
				}
				this.fireEvent("select", records, diagnoseType, this);
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			}
		});