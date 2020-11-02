$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ConfigassetsEditorList = function(cfg) {
	cfg.autoLoadData = false;
	this.editRecords = [];
	cfg.selectOnFocus = true;
	phis.application.cfg.script.ConfigassetsEditorList.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeedit", this.beforeCellEdit, this);
	this.on("afterCellEdit", this.afterGridEdit, this);
}
Ext.extend(phis.application.cfg.script.ConfigassetsEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.ConfigassetsEditorList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 2 > count) {
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
			doCreate : function(item, e) {
				phis.application.cfg.script.ConfigassetsEditorList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			// 新增
			doNew : function() {
				this.clear();
				this.doInit();
			},
			doInit : function() {
				this.editRecords = [];
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("JLXH") == null || r.get("JLXH") == 0) {
					this.store.remove(r);
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
					return;
				}
				Ext.Msg.confirm("请确认", "确认数量为【" + r.get("WZSL") + "】的物资吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
									serviceId : "configInventoryInitialService",
									serviceAction : "deleteAssetsIn",
									pkey : r.get("JLXH")
								}, function(code, msg, json) {
									this.grid.el.unmask()
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										if (code != 604) {
											return;
										}
									}
									this.store.remove(r);
									// 移除之后焦点定位
									var count = this.store.getCount();
									if (count > 0) {
										cm.select(cell[0] < count
														? cell[0]
														: (count - 1), cell[1]);
									}
									this.fireEvent("afterRemove", this.grid);
								}, this)
							}
						}, this);
				return
			},
			beforeCellEdit : function(e) {
				var f = e.field
				var record = e.record
				var cm = this.grid.getColumnModel()
				var c = cm.config[e.column]
				var it = c.schemaItem
				if (it.id == "ZYKS") {
					if (record.get("WZZT") == "2") {
						return false;
					} else {
						return true;
					}
				}
				if (it.id == "QYRQ") {
					if (record.get("WZZT") != "1") {
						return false;
					} else {
						return true;
					}
				}
			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == "QYRQ") {
					if (record.get("QYRQ") == "NaN-NaN-NaN") {
						record.set("QYRQ", "");
					}
				}
				if (it.id == "CZRQ") {
					if (record.get("CZRQ") == "NaN-NaN-NaN") {
						record.set("CZRQ", "");
					}
				}
				if (it.id == "ZJRQ") {
					if (record.get("ZJRQ") == "NaN-NaN-NaN") {
						record.set("ZJRQ", "");
					}
				}
				if (it.id == "WZSL") {
					if (record.get("WZSL") == '') {
						record.set("WZSL", 0);
					}
				}
				if (it.id == "CZYZ") {
					if (record.get("CZYZ") == '') {
						record.set("CZYZ", 0);
					}
				}
				if (it.id == "JTZJ") {
					if (record.get("JTZJ") == '') {
						record.set("JTZJ", 0);
					}
				}
			},
			onStoreLoadData:function(store,records,ops){
				this.store.each(function(r) {
					if(r.get("WZZT")==0){
						r.set("WZZT", 2);
						r.set("WZZT_text", "在库");
					}
				}, this);
				this.grid.store.commitChanges();
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				}
				else{
					this.selectRow(this.selectedIndex);
				}
			}
		})