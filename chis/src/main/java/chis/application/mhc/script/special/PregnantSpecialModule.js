/**
 * 孕妇特殊情况整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.special")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.special.PregnantSpecialModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.mhc.script.special.PregnantSpecialModule.superclass.constructor.apply(
			this, [cfg])
	this.width = 880;
	this.itemWidth = 170;
}
Ext.extend(chis.application.mhc.script.special.PregnantSpecialModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.special.PregnantSpecialModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				this.grid.on("rowdblclick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				return panel;
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.form.op = "create";
					this.form.initDataId = null;
					this.refreshExContextData(this.form, this.exContext);
					this.form.doNew();
					return;
				}
				if (!this.list.selectedIndex) {
					this.list.selectedIndex = 0;
				}
				var r = store.getAt(this.list.selectedIndex);
				this.process(r, this.list.selectedIndex);
			},

			onRowClick : function(grid, index, e) {
				this.list.selectedIndex = index;
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			process : function(r, index) {
				if (r) {
					this.form.op = "update";
					this.form.initDataId = r.id;
					var data = this
							.castListDataToForm(r.data, this.list.schema)
					var exc = {
						"formDatas" : data
					}
					Ext.apply(this.exContext.args, exc);
					this.refreshExContextData(this.form, this.exContext);
					this.form.loadData();
				} else {
					this.form.op = "create";
					this.form.initDataId = null;
					this.refreshExContextData(this.form, this.exContext);
					this.form.doNew();
				}
			},

			onFormSave : function(saveData) {
				this.list.refresh();
			},

			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			}
		});