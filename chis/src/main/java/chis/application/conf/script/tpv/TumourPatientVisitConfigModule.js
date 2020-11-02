$package("chis.application.conf.script.tpv");

$import("chis.script.BizCombinedModule2");

chis.application.conf.script.tpv.TumourPatientVisitConfigModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.tpv.TumourPatientVisitConfigModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.itemWidth = this.width;
	this.itemHeight = 154;
}

Ext.extend(chis.application.conf.script.tpv.TumourPatientVisitConfigModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.conf.script.tpv.TumourPatientVisitConfigModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
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

			onSave : function(formData) {
				var gridData = this.list.getSaveData();
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
							method : "execute",
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