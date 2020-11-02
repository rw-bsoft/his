$package("phis.application.ccl.script")
$import("phis.script.TabModule")

phis.application.ccl.script.CheckApplyMaintain = function(cfg) {
	phis.application.ccl.script.CheckApplyMaintain.superclass.constructor.apply(this,
			[cfg])
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(phis.application.ccl.script.CheckApplyMaintain, phis.script.TabModule, {
			onMyTabChange : function(tabPanel, newTab, curTab) {
				var newModule = this.midiModules[newTab.id];
				if (newModule.id != "SYS1504") {
					newModule.refresh();
				}else{
					newModule.checkTypeList.refresh();
					newModule.checkPointList.store.removeAll();
					newModule.checkProjectList.store.removeAll();
				}
			}
		})
