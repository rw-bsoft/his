$package("phis.application.sto.script");
$import("phis.script.SimpleModule", "phis.script.TabModule");

phis.application.sto.script.StorehousePurchaseHistoryTabModule = function(cfg) {
	phis.application.sto.script.StorehousePurchaseHistoryTabModule.superclass.constructor
			.apply(this, [ cfg ]);
};
Ext.extend(phis.application.sto.script.StorehousePurchaseHistoryTabModule,
		phis.script.TabModule, {
			onBeforeTabChange : function(tabPanel, newTab, curTab) {
				this.opener.beforetabchange(tabPanel, newTab, curTab);
			},
			setValue : function(data) {
				this.opener.setValue(data);
			}
		});