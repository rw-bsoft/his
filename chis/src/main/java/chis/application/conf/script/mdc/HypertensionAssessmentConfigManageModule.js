$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule = function(
		cfg) {
	cfg.autoLoadData = false;
	if (!cfg.exContext) {
		cfg.exContext = {};
	}
	chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule.superclass.constructor
			.apply(this, [cfg])
	this.isAutoScroll = true
	this.layOutRegion = "north"
	this.height = 580;
	this.itemHeight = 330
	this.itemCollapsible = false
	// this.secondTitle = "血压控制";
}
Ext
		.extend(
				chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule,
				chis.script.BizCombinedModule2, {

					initPanel : function() {
						var panel = chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule.superclass.initPanel
								.call(this)
						this.panel = panel;
						this.form = this.midiModules[this.actions[0].id];
						this.form.on("save", this.doSave, this)
						this.list = this.midiModules[this.actions[1].id];
						this.grid = this.list.grid;
						this.grid.setAutoScroll(true);
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
						if (this.form) {
							this.form.planModeChange(value);
						}
					},

					doSave : function() {
						var formData = this.form.getFormData();
						var listData = this.list.getListData();
						var data = {
							"formData" : formData,
							"listData" : listData
						}
						this.panel.el.mask("正在保存数据...", "x-mask-loading")
						util.rmi.jsonRequest({
									serviceId : this.saveServiceId,
									serviceAction : this.saveAction,
									method : "execute",
									body : data
								}, function(code, msg, json) {
									this.panel.el.unmask();
									if (code > 300) {
										this.processReturnMsg(code, msg);
										return
									}
									this.list.store.commitChanges();
									// this.mainApp.exContext.hypertensionMode =
									// planMode;
							}, this)
					},

					validate : function() {
						return this.form.validate()
					}

				});