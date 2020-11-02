$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.MaintenanceMaterialsEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.minListWidth = 520;
	cfg.remoteUrl = "MaintenanceMaterials";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td>'
		+ '<td width="100px">{WZMC}</td><td width="140px">{CJMC}</td>'
		+ '<td width="40px">{WZGG}</td><td width="40px">{WZDW}</td>';
	cfg.disablePagingTbr = true;
	phis.application.sup.script.MaintenanceMaterialsEditorList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.MaintenanceMaterialsEditorList,
		phis.script.EditorList, {
	initPanel : function(sc) {
		var grid = phis.application.sup.script.MaintenanceMaterialsEditorList.superclass.initPanel
				.call(this, sc)
		grid.on("beforeedit", this.onBeforeCellEdit, this);
		var sm = grid.getSelectionModel();
		var _ctr = this;
		grid.onEditorKey = function(field, e) {
			var sm = this.getSelectionModel();
			var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
			if (e.getKey() == e.ENTER && !e.shiftKey) {
				var cell = sm.getSelectedCell();
				var count = this.colModel.getColumnCount()
				if (cell[1] + 4 > count) {
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
				phis.application.sup.script.MaintenanceMaterialsEditorList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			onReady : function() {
				phis.application.sup.script.MaintenanceMaterialsEditorList.superclass.onReady.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
			},
			expansion : function(cfg) {
				var yjrklabel = new Ext.form.Label({
					html : "<div id='wxpj"
						+ this.tabid
						+ "' align='center' style='color:blue'>金额：0.00￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(yjrklabel);
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mats',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mtssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'WZXH'
								}, {
									name : 'WZMC'
								}, {
									name : 'WZGG'
								}, {
									name : 'WZDW'
								}, {
									name : 'GLFS'
								}, {
									name : 'CJXH'
								}, {
									name : 'CJMC'
								}, {
									name : 'WZJG'
								}, {
									name : 'KCXH'
								}, {
									name : 'TJSL'
								}, {
									name : 'LSJG'
								}]);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var wzxh = record.get("WZXH");
				// }
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('PJMC', record.get("WZMC"));
				if (record.get("WZGG") && record.get("WZGG") != 'null') {
					rowItem.set('PJGG', record.get("WZGG"));
				}
				if (record.get("WZDW") && record.get("WZDW") != 'null') {
					rowItem.set('PJDW', record.get("WZDW"));
				}
				if (record.get("WZJG")) {
					rowItem.set('PJJG', record.get("WZJG"));
				}else {
					rowItem.set('PJJG', 0.0000);
					
				}
				obj.setValue(record.get("WZMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 3);
				this.remoteDic.lastQuery = "";
			},
			setCountInfo : function() {
				var totalPJJE = 0;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					var pjje = parseFloat(r.get("PJJE"));
					if (pjje) {
						totalPJJE += parseFloat(pjje);
					}
					if (isNaN(totalPJJE)) {
						totalPJJE = 0;
					}
				}
				this.fireEvent("setclfy",parseFloat(totalPJJE).toFixed(2));
				document.getElementById("wxpj" + this.tabid).innerHTML = "金额："
					+ parseFloat(totalPJJE).toFixed(2) + "￥";
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				this.store.rejectChanges();
				this.setCountInfo();
				if (records.length == 0) {
					this.store.rejectChanges();
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.store.rejectChanges();
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.store.remove(r);
				this.grid.getView().refresh();
				// 移除之后焦点定位
				var count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
				this.setCountInfo();
			},
			onAfterCellEdit : function(it, record, field, v) {
				if (it.id == "PJSL" || it.id == "PJJG") {
					var pjje = 0;
					if (record.get("PJSL") == null || record.get("PJSL") == ""
							|| record.get("PJJG") == null
							|| record.get("PJJG") == "") {
						return;
					}
					pjje = (Number(record.get("PJSL")) * Number(record
							.get("PJJG"))).toFixed(2);
					record.set("PJJE", pjje);
					this.setCountInfo();
				}
			},
			onBeforeCellEdit : function(e) {
				var record = e.record;
				var op = record.get("_opStatus");
				var cm = this.grid.getColumnModel();
				var c = cm.config[e.column];
				var it = c.schemaItem;
				var ac = util.Accredit;
				if (op == "create") {
					if (!ac.canCreate(it.acValue)) {
						return false;
					}
				} else {
					if (!ac.canUpdate(it.acValue)) {
						return false;
					}
				}
				if (it.id == "PJJG") {
					var r = this.getSelectedRecord();
					if (r.get("PJJG") != 0.00){
						return false;
					}
				}
			}
		})