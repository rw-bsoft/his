$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ConfigDepartmentsInEditorList = function(cfg) {
	cfg.autoLoadData = false;
	this.editRecords = [];
	cfg.remoteUrl = "SupplyUnits";
	cfg.selectOnFocus = true;
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{#}.</td><td width="100px">{DWMC}</td>';
	phis.application.cfg.script.ConfigDepartmentsInEditorList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.afterGridEdit, this);
}
Ext.extend(phis.application.cfg.script.ConfigDepartmentsInEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.ConfigDepartmentsInEditorList.superclass.initPanel
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
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'DWXH'
								}, {
									name : 'DWMC'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
				obj.triggerBlur();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('GHDW', record.get("DWXH"));
				obj.setValue(record.get("DWMC"));
				this.doCreate();
			},
			doCreate : function(item, e) {
				phis.application.cfg.script.ConfigDepartmentsInEditorList.superclass.doCreate
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
			afterGridEdit : function(it, record, field, v) {
				if (it.id == "WZSL" || it.id == "WZJG") {
					if (!record.get("WZSL")) {
						record.set("WZSL", 0);
					}
					if (!record.get("WZJG")) {
						record.set("WZJG", 0.00);
					}
					var v = parseFloat(record.get("WZSL") * record.get("WZJG"))
							.toFixed(2);
					record.set("WZJE", v);
				}
				if (it.id == "WZPH") {
					var wzph = record.get("WZPH");
					var n = 0;
					for (var i = 0; i < this.store.getCount(); i++) {
						var row = this.store.getAt(i)
						if (row.get("WZPH") == wzph) {
							n++;
						}
					}
					if (n > 1) {
						MyMessageTip.msg("提示", "该批号已存在", true);
						record.set("WZPH", "");
					}
				}
				if (it.id == "SCRQ") {
					if (record.get("SCRQ") == "NaN-NaN-NaN") {
						record.set("SCRQ", "");
					}
				}
				if (it.id == "SXRQ") {
					var sxrq = "";
					if (record.get("SXRQ")) {
						sxrq = new Date(record.get("SXRQ")).format("Y-m-d");
					}
					if (sxrq < new Date().format('Y-m-d')) {
						MyMessageTip.msg("提示", "失效日期不能小于当前日期!", true);
						record.set("SXRQ", "");
						this.savesignks = 1;
					} else {
						this.savesignks = 0;
					}
					if (record.get("SXRQ") == "NaN-NaN-NaN") {
						record.set("SXRQ", "");
					}
				}
				if (it.id == "RKRQ") {
					if (record.get("RKRQ") == "NaN-NaN-NaN") {
						record.set("RKRQ", "");
					}
				}
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
				Ext.Msg.confirm("请确认", "确认删除物资批号为【" + r.get("WZPH") + "】的物资吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
									serviceId : "configInventoryInitialService",
									serviceAction : "deleteInventoryIn",
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

			}
		})