$package("phis.application.cfg.script")
$import("phis.script.EditorList")

phis.application.cfg.script.LogisticsInventoryControlRightModuleLeftList = function(
		cfg) {
	cfg.autoLoadData = false;
	phis.application.cfg.script.LogisticsInventoryControlRightModuleLeftList.superclass.constructor
			.apply(this, [ cfg ])
	this.on("afterCellEdit", this.onAfterCellEdit, this)
}
Ext
		.extend(
				phis.application.cfg.script.LogisticsInventoryControlRightModuleLeftList,
				phis.script.EditorList, {
					doCreate : function(d) {
						var count = this.store.getCount();
						for ( var i = 0; i < count; i++) {
							if (this.store.getAt(i).get("WZXH") == d.WZXH) {
								MyMessageTip.msg("提示", "该物资已经存在，请维护数量！", true);
								return;
							}
						}
						var store = this.grid.getStore();
						var o = this.getStoreFields(this.schema.items)
						var Record = Ext.data.Record.create(o.fields)
						var items = this.schema.items
						var factory = util.dictionary.DictionaryLoader
						var r = new Record(d);
						store.add([ r ])
						this.grid.getView().refresh();
					},
					onAfterCellEdit : function() {
						this.isEdit = 1;
					}
				})