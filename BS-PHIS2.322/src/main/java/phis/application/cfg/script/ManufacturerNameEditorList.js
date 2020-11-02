$package("phis.application.cfg.script");
$import("phis.script.EditorList");

phis.application.cfg.script.ManufacturerNameEditorList = function(cfg) {
	cfg.entryName="phis.application.cfg.schemas.WL_CJBM";
	cfg.autoLoadData = false;
	phis.application.cfg.script.ManufacturerNameEditorList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.afterGridEdit, this)
}
Ext.extend(phis.application.cfg.script.ManufacturerNameEditorList,
		phis.script.EditorList, {
			afterGridEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var r = this.store.getAt(i);
						var r2 = this.store.getAt(i + 1);
						if (it.id == "CJBM"
								&& (r.get("CJBM") == v.toLowerCase() || r
										.get("CJBM") == v.toUpperCase())) {
							MyMessageTip.msg("提示",
									"本条名称和第" + (i + 1) + "重复！", true);
							record.set("CJBM", "");
							this.grid.startEditing(curRow, 1);
							return false;
						}
					}
				}
				return true;
			}

		})
