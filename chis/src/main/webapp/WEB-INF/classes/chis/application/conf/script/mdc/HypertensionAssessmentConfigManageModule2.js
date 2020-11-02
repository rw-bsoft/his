$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule2 = function(
		cfg) {
	cfg.autoLoadData = false;
	if (!cfg.exContext) {
		cfg.exContext = {};
	}
	chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule2.superclass.constructor
			.apply(this, [cfg])
	this.isAutoScroll = true
	this.layOutRegion = "north"
	this.height = 650;
	this.itemHeight = 170
	this.itemCollapsible = false
	// this.secondTitle = "血压控制";
}
Ext
		.extend(
				chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule2,
				chis.script.BizCombinedModule2, {

					initPanel : function() {
						var panel = chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule2.superclass.initPanel
								.call(this)
						this.panel = panel;
						this.form = this.midiModules[this.actions[1].id];
						this.form.on("save", this.doSave, this)
						this.list = this.midiModules[this.actions[0].id];
						this.grid = this.list.grid;
						this.grid.setAutoScroll(true);
						return panel;
					},

					loadData : function() {
						this.midiModules[this.actions[0].id].loadData()
						this.midiModules[this.actions[1].id].loadData()
					},
					planModeChange : function(value) {
						if (this.form) {
							this.form.planModeChange(value);
						}
					},

					doSave : function() {
						if (!this.midiModules[this.actions[0].id].validate()
								|| !this.midiModules[this.actions[1].id]
										.validate()) {
							return
						}
						this.midiModules[this.actions[0].id].doSave()
						this.midiModules[this.actions[1].id].doSave()
					},

					validate : function() {
						return this.form.validate()
					}

				});