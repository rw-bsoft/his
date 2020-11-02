$package("phis.application.ccl.script");
$import("phis.script.TabModule")

phis.application.ccl.script.CheckApplyTabModule1 = function(cfg) {
	phis.application.ccl.script.CheckApplyTabModule1.superclass.constructor.apply(this,
			[cfg])
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(phis.application.ccl.script.CheckApplyTabModule1, phis.script.TabModule, {
			onMyTabChange : function(tabPanel, newTab, curTab) {
				
				var curModule = this.midiModules[newTab.id];
				if (curModule.id == "CCL400102"||curModule.id=="CCL220102") {
					curModule.checkApplyExchangedApplicationList.refresh();
					
				}
			}
		})
