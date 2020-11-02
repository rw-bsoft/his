$package("chis.application.mhc.script.delivery")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.delivery.DeliveryChildrenRecordModule = function(cfg) {
	cfg.autoLoadData = false
	cfg.height = 600;
	cfg.frame = true;
	chis.application.mhc.script.delivery.DeliveryChildrenRecordModule.superclass.constructor
			.apply(this, [cfg])
	this.width = 880;
	this.itemWidth = 160;
}
Ext.extend(chis.application.mhc.script.delivery.DeliveryChildrenRecordModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.delivery.DeliveryChildrenRecordModule.superclass.initPanel
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
				var index = 0;
				var selectRecord = this.exContext.args.babyId;
				if (selectRecord) {
					index = this.list.store.find("DRCID", selectRecord);
				}
				this.list.selectedIndex = index > -1 ? index : 0;
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
							.castListDataToForm(r.data, this.list.schema);
					var exc = {
						"formDatas" : data
					};
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

			onFormSave : function(entryName, op, json, body) {
				this.exContext.args.babyId = this.form.initDataId;
				this.list.refresh();
				this.DRCID = body.DRCID;
			},


			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			}
		});