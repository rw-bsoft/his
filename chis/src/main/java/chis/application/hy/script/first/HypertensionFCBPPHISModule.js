$package("chis.application.hy.script.first");

$import("chis.script.BizCombinedModule2");

chis.application.hy.script.first.HypertensionFCBPPHISModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.hy.script.first.HypertensionFCBPPHISModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "west";
	this.itemWidth = 600;
	this.itemCollapsible = false;
};
Ext.extend(chis.application.hy.script.first.HypertensionFCBPPHISModule,
		chis.script.BizCombinedModule2, {
			initPanel : function(sc) {
				var panel = chis.application.hy.script.first.HypertensionFCBPPHISModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.grid = this.list.grid;

				this.grid.on("rowClick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.parent = this;
				this.form.on("save", this.onFormSave, this);
				return panel;
			},
			loadData : function() {	
				this.form.doCreate();
				this.list.requestData.cnd = ['eq', ['$', 'a.empiId'],
						['s', this.exContext.ids.empiId]]
				this.list.loadData();
			},
			onFormSave : function() {
				this.list.requestData.cnd = ['eq', ['$', 'a.empiId'],
						['s', this.exContext.ids.empiId]]
				this.list.loadData();
				this.fireEvent("chisSave");
			},
			onListLoadData : function(store) {
				if (store.getCount() == 0) {
					this.form.doCreate();
					return;
				}
				var index = this.list.selectedIndex;
				if (!index) {
					index = 0;
				}
				this.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				this.process(r, index);
			},
			onRowClick : function(grid, index, e) {
				if (!this.list) {
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				this.selectedIndex = index;
				this.process(r, index);
			},
			process : function(r, index) {
				var formData = this
						.castListDataToForm(r.data, this.form.schema);
				this.form.initFormData(formData);
			}
		});