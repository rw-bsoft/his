$package("chis.application.quality.script")

$import("chis.script.BizCombinedModule3")
chis.application.quality.script.HypertensionVisitBaseInfoForm_module = function(cfg) {
	chis.application.quality.script.HypertensionVisitBaseInfoForm_module.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(chis.application.quality.script.HypertensionVisitBaseInfoForm_module,
		chis.script.BizCombinedModule3, {
		 initPanel : function() {
				var items = this.getPanelItems();
				 var module = this.createCombinedModule("addModule", this.addModule);
				this.AduitFormWin = module.getWin();
				this.AduitFormWin.add(module.initPanel());
				this.AduitFormWin.setPosition(250, 100);
				//this.AduitFormWin.show();
				this.panel = this.AduitFormWin
				return this.panel
			}
		})