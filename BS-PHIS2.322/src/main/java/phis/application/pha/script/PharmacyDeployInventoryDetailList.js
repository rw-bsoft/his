$package("phis.application.pha.script")

$import("phis.application.pha.script.PharmacyMySimpleDetailList")

phis.application.pha.script.PharmacyDeployInventoryDetailList = function(cfg) {
	cfg.remoteDicStore={};
	phis.application.pha.script.PharmacyDeployInventoryDetailList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyDeployInventoryDetailList,
		phis.application.pha.script.PharmacyMySimpleDetailList, {
			initPanel : function(sc) {
				var grid = phis.application.pha.script.PharmacyMySimpleDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr=this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 6 > count) {
							return;
						}
					}

					this.selModel.onEditorKey(field, e);
				}
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						e.stopEvent();
						if (!ed) {
							ed = g.lastActiveEditor;
						}
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}

					} else if (k == e.TAB) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}
					} else if (k == e.ESC) {
						ed.cancelEdit();
					}
					if (newCell) {
						r = newCell[0];
						c = newCell[1];
						this.select(r, c);
						if (g.isEditor && !g.editing) {
							ae = g.activeEditor;
							if (ae && ae.field.triggerBlur) {
								ae.field.triggerBlur();
							}
							g.startEditing(r, c);
						}
					}

				};
				return grid
			},
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "QRSL") {
					var pfje = 0;
					var jhje = 0;
					var lsje = 0;
					if (((v != null && v != "") || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
						if (v > record.get("KCSL")) {
							MyMessageTip.msg("提示", "库存数量不足", true);
							v = record.get("KCSL");
							record.set("QRSL", v);
						}
						var pfje = (v * record.get("PFJG")).toFixed(4);
						var jhje = (v * record.get("JHJG")).toFixed(4);
						var lsje = (v * record.get("LSJG")).toFixed(4);
					}
					record.set("JHJE", jhje);
					record.set("PFJE", pfje);
					record.set("LSJE", lsje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					this.doJshj();
				}
			},
			onBeforeCellEdit:function(it, record, field, v){
			if (this.isRead) {
					return false;
				}
				if(parseFloat(record.get("YPSL"))<0){
				return false;
				}
				return true;
			}
		})