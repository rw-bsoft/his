$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.HypertensionConfigManageModule2 = function(cfg) {
	cfg.autoLoadData = false;
	if (!cfg.exContext) {
		cfg.exContext = {};
	}
	chis.application.conf.script.mdc.HypertensionConfigManageModule2.superclass.constructor
			.apply(this, [cfg])
	// this.isAutoScroll = true
	this.layOutRegion = "north"
	this.height = 1060
	this.itemHeight = 750
	this.itemCollapsible = false
	this.secondTitle = "高血压高危参数设置"
}
Ext.extend(chis.application.conf.script.mdc.HypertensionConfigManageModule2,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mdc.HypertensionConfigManageModule2.superclass.initPanel
						.call(this)
				var form = new Ext.Panel({
							frame : true,
							defaultType : 'textfield',
							// autoScroll : true,
							height : this.height,
							items : panel
						});
				return form
			},

			loadData : function() {
				this.midiModules[this.actions[0].id].loadData()
				this.midiModules[this.actions[1].id].loadData()
			},
			planModeChange : function(value) {
				if (this.midiModules[this.actions[0].id]) {
					this.midiModules[this.actions[0].id].planModeChange(value);
				}
			},

			doSave : function() {
				if (!this.midiModules[this.actions[0].id].validate()
						|| !this.midiModules[this.actions[1].id].validate()) {
					return
				}
				this.midiModules[this.actions[0].id].doSave()
				this.midiModules[this.actions[1].id].doSave()
			},

			validate : function() {
				return this.midiModules[this.actions[0].id].validate()
						&& this.midiModules[this.actions[1].id].validate()
			}

		});
