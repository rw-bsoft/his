$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.HypertensionConfigManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.mdc.HypertensionConfigManageModule.superclass.constructor
			.apply(this, [cfg])
	this.isAutoScroll = true
	this.layOutRegion = "north"
	this.itemWidth = 800
	this.itemHeight = 360
	this.itemCollapsible = false
	this.firstTitle = "高血压档案参数设置"
	// this.secondTitle= "高血压高危参数设置"
}
Ext.extend(chis.application.conf.script.mdc.HypertensionConfigManageModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mdc.HypertensionConfigManageModule.superclass.initPanel
						.call(this)

				var form = new Ext.Panel({
							frame : true,
							tbar : [{
										text : '确定',
										iconCls : 'save',
										handler : this.doSave,
										scope : this
									}],
							defaultType : 'textfield',
							autoScroll : true,
							height : 1200,
							items : panel
						});
				this.midiModules[this.actions[0].id].on("planModeChange",
						this.planModeChange, this)
				this.form = form
				return form
				// this.form = form
				// return form;
				// this.panel = panel;
				// this.form = this.midiModules[this.actions[0].id];
				// this.form.on("save", this.onSave, this)
				// this.form.on("planModeChange", this.planModeChange, this)
				// this.list = this.midiModules[this.actions[1].id];
				// this.list.on("loadData", this.onLoadData, this)
				// this.grid = this.list.grid;
				// this.grid.setAutoScroll(true);
				// return panel;
			},
			planModeChange : function(value) {
				if (this.midiModules[this.actions[1].id]) {
					this.midiModules[this.actions[1].id].planModeChange(value);
				}
			},
			resetButtonsReadOnly : function() {
				if (this.readOnly == null) {
					return;
				}
				if (this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					if (!btns) {
						return;
					}
					var n = btns.getCount();
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i);
						if (btn.prop && btn.prop.notReadOnly) {
							continue;
						}
						var status = this.readOnly;
						if (status == null) {
							return;
						}
						this.changeButtonStatus(btn, !status);
					}
				}
			},
			loadData : function() {
				this.midiModules[this.actions[0].id].loadData()
				this.midiModules[this.actions[1].id].loadData()
				this.resetButtonsReadOnly();
			},

			onSave : function() {
				// this.saveData();
			},

			doSave : function() {
				if (!this.midiModules[this.actions[0].id].validate()
						|| !this.midiModules[this.actions[1].id].validate()) {
					return
				}
				this.midiModules[this.actions[0].id].doSave()
				this.midiModules[this.actions[1].id].doSave()
			}

		});
