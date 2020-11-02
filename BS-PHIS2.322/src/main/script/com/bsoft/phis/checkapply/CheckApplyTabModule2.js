$package("com.bsoft.phis.checkapply")
$import("com.bsoft.phis.TabModule")

com.bsoft.phis.checkapply.CheckApplyTabModule2 = function(cfg) {
	com.bsoft.phis.checkapply.CheckApplyTabModule2.superclass.constructor.apply(this,
			[cfg])
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyTabModule2, com.bsoft.phis.TabModule, {
			onMyTabChange : function(tabPanel, newTab, curTab) {
				var curModule = this.midiModules[newTab.id];
				if (curModule.id == "CIC410102"||curModule.id=="WAR220102") {
					curModule.checkApplyExchangedApplicationList.refresh();
				}
			}
		})
