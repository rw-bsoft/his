$package("chis.application.hc.script")

$import("chis.script.BizCombinedModule2")

chis.application.hc.script.lifestySituationTabModule = function(cfg) {
	cfg.layOutRegion = "north";
	cfg.height = 500;
	cfg.itemHeight = 200;
	cfg.itemCollapsible = false;
	cfg.autoLoadData = false;
	chis.application.hc.script.lifestySituationTabModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.hc.script.lifestySituationTabModule, chis.script.BizCombinedModule2, {
	initPanel : function(){
		this.panel = chis.application.hc.script.lifestySituationTabModule.superclass.initPanel.call(this);
		this.upList = this.midiModules[this.actions[0].id];
		this.downList = this.midiModules[this.actions[1].id];
		return this.panel;
	},
	
	getLoadRequest : function() {
		var actions = this.actions
		for (var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			if (this.midiModules[ac.id]) {
				this.midiModules[ac.id].exContext.control = this.exContext.control;
			}
		}
		this.upList.exContext.args.healthCheck = this.exContext.args.healthCheck;
		this.downList.exContext.args.healthCheck = this.exContext.args.healthCheck;
		return {
			healthCheck : this.exContext.args.healthCheck
		};
	}
})