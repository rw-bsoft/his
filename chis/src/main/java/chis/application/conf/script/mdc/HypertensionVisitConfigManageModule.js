$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.HypertensionVisitConfigManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.mdc.HypertensionVisitConfigManageModule.superclass.constructor
			.apply(this, [cfg])
	this.isAutoScroll = true
	this.layOutRegion = "north"
	this.itemWidth = this.width
	this.itemHeight = 180
	this.itemCollapsible = false
	this.firstTitle = ""
	this.secondTitle= ""
	
}
Ext.extend(chis.application.conf.script.mdc.HypertensionVisitConfigManageModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mdc.HypertensionVisitConfigManageModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.doSave, this)
				this.form.on("planModeChange", this.planModeChange, this)
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("loadData", this.onLoadData, this)
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

			onLoadData : function(value) {
				if (this.readOnly) {
					this.grid.disable();
				}
			},

			planModeChange : function(value) {
				this.fireEvent("planModeChange", value);
			},

			doSave : function() {
				this.saveData();
			},

			saveData : function() {
				var formData = this.form.getFormData();
				var listData = this.list.getListData();
				var data = {
					"formData" : formData,
					"listData" : listData
				}
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							schema : this.entryName,
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction,
							method:"execute",
							body : data
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.list.store.commitChanges();
							this.mainApp.exContext.hypertensionMode = formData.planMode;
						}, this)
			}
			,
			validate:function(){
				return this.form.validate()
			}
		});
