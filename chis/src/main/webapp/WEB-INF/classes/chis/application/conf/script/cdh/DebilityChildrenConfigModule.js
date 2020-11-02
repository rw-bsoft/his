$package("chis.application.conf.script.cdh")

$import("chis.script.BizCombinedModule2")

chis.application.conf.script.cdh.DebilityChildrenConfigModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.cdh.DebilityChildrenConfigModule.superclass.constructor.apply(
			this, [cfg])
	this.layOutRegion = "north"
	this.itemWidth = this.width
	this.itemHeight = 130
}

Ext.extend(chis.application.conf.script.cdh.DebilityChildrenConfigModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.cdh.DebilityChildrenConfigModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
				this.form.on("exceptionalCase", this.onExceptionalCase, this)
				this.form.on("loadData", this.onFormLoadData, this)
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

			// 初始化时如果"例外情况"为勾选,则默认disable下面list
			onFormLoadData : function(entryName, body) {
				var exCase = body.exceptionalCase;
				if (exCase == "false") {
					this.grid.disable();
				}
			},

			// 根据"例外情况"判断LIST是否只读
			onExceptionalCase : function(value) {
				if (value) {
					this.exceptionalCase = true;
					this.grid.enable();
				} else {
					this.exceptionalCase = false;
					this.grid.disable();
				}
			},

			onSave : function(formData) {
				var gridData = this.list.getSaveData(this.exceptionalCase);
				if (!gridData) {
					return
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
						}, this)
			}
		});
