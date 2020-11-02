$package("chis.application.conf.script.mhc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mhc.PregnantConfigManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.mhc.PregnantConfigManageModule.superclass.constructor.apply(
			this, [cfg])
	this.layOutRegion = "north"
	this.itemWidth = this.width
	this.itemHeight = 155
}
Ext.extend(chis.application.conf.script.mhc.PregnantConfigManageModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mhc.PregnantConfigManageModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[1].id];
				this.grid = this.list.grid;
				this.grid.setAutoScroll(true);
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this)
				this.form.on("planModeChange", this.planModeChange, this)
				this.form.on("check", function(checked) {
							if (checked) {
								this.grid.disable()
							} else {
								this.grid.enable()
							}
						}, this);
				return panel;
			},

			loadData : function() {
				if (this.form) {
					this.form.readOnly = this.readOnly;
					this.form.fieldReadOnly = this.fieldReadOnly;
					this.form.loadData();
				}
				if (this.list) {
					this.list.readOnly = this.readOnly;
					this.list.loadData();
				}
			},

			planModeChange : function(value) {
				if (value == "2") {
					this.grid.disable();
				} else {
					this.grid.enable();
					if (this.form.planMode != value) {
						this.grid.store.removeAll();
					}
				}
			},

			onSave : function(formData) {
				var gridData = [];
				var planMode = formData.planMode
				if (!formData.visitIntervalSame && planMode == "1") {
					gridData = this.list.getSaveData();
					if (gridData == false) {
						return
					}
				}
				var saveData = {
					form : formData,
					grid : gridData
				}
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							schema : this.entryName,
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction,
							method:"execute",
							body : saveData
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.list.store.commitChanges();
							this.mainApp.exContext.pregnantMode = planMode;
						}, this)
			}

		});
