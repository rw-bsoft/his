/**
 * 新生儿访视基本信息整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.babyVisit.BabyVisitModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.mhc.script.babyVisit.BabyVisitModule.superclass.constructor.apply(this,
			[cfg])
	this.itemCollapsible = false
	this.layOutRegion = "north"
	this.itemWidth = 800
	this.itemHeight = 190
	this.width = 1000
	this.height = 450
}
Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitModule, chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.babyVisit.BabyVisitModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("create", this.onCreate, this)
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onSave, this)
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

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								item : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;

			}
		});