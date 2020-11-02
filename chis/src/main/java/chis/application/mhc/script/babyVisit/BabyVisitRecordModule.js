/**
 * 新生儿访视记录整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.babyVisit.BabyVisitRecordModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.mhc.script.babyVisit.BabyVisitRecordModule.superclass.constructor.apply(
			this, [cfg])
	this.itemCollapsible = false;
	this.layOutRegion = "north";
	this.itemHeight = 150;
	this.itemWidth = 800;
    this.width = Ext.getBody().getWidth() > 800 ? 890 : Ext.getBody().getWidth() * 0.9;
    this.height = Ext.getBody().getHeight() > 600 ?  600 : Ext.getBody().getHeight() * 0.9;
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitRecordModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.babyVisit.BabyVisitRecordModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("create", this.onCreate, this)
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onSave, this);
				this.form.on("cancel", this.onCancel, this);
				return panel;
			},

			onCreate : function() {
				this.form.initDataId = null;
				this.form.doNew();
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
				if (!this.list.selectedIndex) {
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

			onCancel : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},

			onWinShow : function() {
				this.loadData()
			}
		});