/**
 * 孕妇产后访视整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatalVisit")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule.superclass.constructor
			.apply(this, [cfg])
	this.width = 1000
	this.height = 400
	this.itemWidth = 175
}
Ext.extend(chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onSave, this)
				this.form.on("create", this.onCreate, this)
				return panel;
			},

			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			},

			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.form.op = "create";
					this.form.initDataId = null;
					this.refreshExContextData(this.form, this.exContext);
					this.form.doNew();
					return;
				}
				if (!this.list.selectedIndex || this.list.selectedIndex >= store.getCount()) {
					this.list.selectedIndex = 0;
				}
				var r = store.getAt(this.list.selectedIndex);
				this.process(r, this.list.selectedIndex);
			},

			onRowClick : function(grid, rowIndex, e) {
				this.list.selectedIndex = rowIndex;
				var r = grid.store.getAt(rowIndex);
				this.process(r, rowIndex);
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

			onSave : function() {
				this.list.refresh();
			},

			onCreate : function() {
				this.list.selectedIndex = this.grid.store.getCount()
				this.selectedIndex = this.grid.store.getCount()
			}
		});