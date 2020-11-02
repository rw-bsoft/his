$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.ForRegistrationEditorList = function(cfg) {
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	cfg.remoteUrl = "MaterialsOut";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="150px">{WZMC}</td><td width="60px">{WZGG}</td><td width="70px">{WZDW}</td><td width="40px">{TJSL}</td>';
	phis.application.sup.script.ForRegistrationEditorList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ForRegistrationEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sup.script.ForRegistrationEditorList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 8 > count) {
							_ctr.doCreate();
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
			onReady : function() {
				phis.application.sup.script.ForRegistrationEditorList.superclass.onReady
						.call(this);
				// this.on("afterCellEdit", this.onAfterCellEdit, this);
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'sups',
							totalProperty : 'count',
							id : 'materialsout'
						}, [{
									name : 'numKey'
								}, {
									name : 'WZXH'
								}, {
									name : 'WZMC'
								}, {
									name : 'CJXH'
								}, {
									name : 'CJMC'
								}, {
									name : 'WZGG'
								}, {
									name : 'WZDW'
								}, {
									name : 'KCXH'
								}, {
									name : 'WZPH'
								}, {
									name : 'SCRQ'
								}, {
									name : 'SXRQ'
								}, {
									name : 'WZJG'
								}, {
									name : 'TJSL'
								}, {
									name : 'BKBZ'
								}, {
									name : 'GLFS'
								}, {
									name : 'MJPH'
								}, {
                                    name : 'LSJG'
                                }]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('WZMC', record.get("WZMC"));
				rowItem.set('CJXH', record.get("CJXH"));
				rowItem.set('CJMC', record.get("CJMC"));
				rowItem.set('WZGG', record.get("WZGG"));
				rowItem.set('WZDW', record.get("WZDW"));
				rowItem.set('KCXH', record.get("KCXH"));
				rowItem.set('WZPH', record.get("WZPH"));
				rowItem.set('SCRQ', record.get("SCRQ"));
				rowItem.set('SXRQ', record.get("SXRQ"));
				rowItem.set('WZJG', record.get("WZJG"));
                rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('TJCKSL', record.get("TJSL"));
				rowItem.set('BKBZ', record.get("BKBZ"));
				rowItem.set('MJPH', record.get("MJPH"));
				rowItem.set('GLFS', record.get("GLFS"));
				obj.setValue(record.get("WZMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 5);
				this.remoteDic.lastQuery = "";
			},
			doInit : function() {
				this.editRecords = [];
			},
			doCreate : function(item, e) {
				phis.application.sup.script.ForRegistrationEditorList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				this.requestData.serviceId = "phis.materialsOutService";
				this.requestData.serviceAction = "getCK02DBDJInfo";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			}
		})