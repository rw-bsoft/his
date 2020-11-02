$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.StorageOfMaterialsEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.minListWidth = 520;
	cfg.remoteUrl = "StorageOfMaterials";
	
	// cfg.remoteTpl = '<td width="12px"
	// style="background-color:#deecfd">{numKey}.</td>'
	// + '<td width="120px">{WZMC}</td><td width="10px"></td><td
	// width="180px">{CJMC}</td>';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td>'
			+ '<td width="100px">{WZMC}</td><td width="140px">{CJMC}</td>'
			+ '<td width="40px">{WZGG}</td><td width="40px">{WZDW}</td>'
			+ '<td width="80px">{TJSL}</td>';
	cfg.disablePagingTbr = true;
	phis.application.sup.script.StorageOfMaterialsEditorList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sup.script.StorageOfMaterialsEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sup.script.StorageOfMaterialsEditorList.superclass.initPanel
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
							// if (e.shiftKey) {
							// newCell = g.walkCells(ed.row, ed.col - 1, -1,
							// sm.acceptsNav, sm);
							// } else {
							// newCell = g.walkCells(ed.row, ed.col + 1, 1,
							// sm.acceptsNav, sm);
							// }
							// r = newCell[0];
							// g.startEditing(r, 1);
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
			expansion : function(cfg) {
				var yjrklabel = new Ext.form.Label({
					html : "<div id='yjrkwzxx_tjxx_"
						+ this.openedBy
						+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "数量：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "进货金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "临售金额：0.00&nbsp;&nbsp;￥</div>"
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
				// for (var i = 0; i < griddata.length; i++) {
				// if (griddata.itemAt(i).get("WZXH") == wzxh
				// && griddata.itemAt(i).get("CJXH") == record
				// .get("CJXH") && i != row) {
				// MyMessageTip.msg("提示", "该物资已存在,请修改此物资", true);
				// return;
				// }
				// }
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('WZMC', record.get("WZMC"));
				if (record.get("WZGG") && record.get("WZGG") != 'null') {
					rowItem.set('WZGG', record.get("WZGG"));
				}
				if (record.get("WZDW") && record.get("WZDW") != 'null') {
					rowItem.set('WZDW', record.get("WZDW"));
				}
				rowItem.set('GLFS', record.get("GLFS"));
				if (record.get("GLFS") == 1) {
					rowItem.set('GLFS_text', "库存管理");
				}
				if (record.get("GLFS") == 2) {
					rowItem.set('GLFS_text', "科室管理");
				}
				if (record.get("GLFS") == 3) {
					rowItem.set('GLFS_text', "台账管理");
				}
				if (record.get("WZJG")) {
					rowItem.set('WZJG', record.get("WZJG"));
				}else {
					rowItem.set('WZJG', 0.0000);
					
				}
				if (record.get("LSJG")) {
					rowItem.set('LSJG', record.get("LSJG"));
				}else{
					rowItem.set('LSJG', 0.0000);
				}
				if (record.get("TJSL")) {
					rowItem.set('KTSL', record.get("TJSL"));
				}
				if (record.get("KCXH")) {
					rowItem.set('KCXH', record.get("KCXH"));
				}
				rowItem.set('CJXH', record.get("CJXH"));
				rowItem.set('CJMC', record.get("CJMC"));
				obj.setValue(record.get("WZMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 3);
				this.remoteDic.lastQuery = "";
			},
			// 设置按钮状态
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
			doCreate : function(item, e) {
				phis.application.sup.script.StorageOfMaterialsEditorList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			onReady : function() {
				phis.application.sup.script.StorageOfMaterialsEditorList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
				// this.grid.on("keypress", this.onKeyPress, this)
			},
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if (!this.editRecords) {
					this.editRecords = [];
				}
				this.editRecords.push(record.data);
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "WZSL" || it.id == "WZJG" || it.id == "LSJG") {
					var wzje = 0;
					var lsje = 0;
					if (record.get("WZSL") == null || record.get("WZSL") == ""
							|| record.get("WZJG") == null
							|| record.get("WZJG") == "") {
						return;
					}
					wzje = (Number(record.get("WZSL")) * Number(record
							.get("WZJG"))).toFixed(2);
					lsje = (Number(record.get("WZSL")) * Number(record
							.get("LSJG"))).toFixed(2);
					this.setCountInfo();
					record.set("WZJE", wzje);
					record.set("LSJE", lsje);
					// this.calculatEmount();
				}

				if (it.id == "WZJE") {
					var wzjg = 0;
					if (record.get("WZJE") == null || record.get("WZJE") == ""
							|| record.get("WZSL") == null
							|| record.get("WZSL") == "") {
						return;
					}
					wzjg = (Number(record.get("WZJE")) / Number(record
							.get("WZSL"))).toFixed(4);
					record.set("WZJG", wzjg);
					// this.calculatEmount();
				}
				// 判断退回数量是否大于可退数量
				if (it.id == "WZSL" && record.get("KTSL")) {
					if ((Number(record.get("WZSL"))) > (Number(record.get("KTSL")))) {
						MyMessageTip.msg("提示", "退回数量不能大于可退数量", true);
					}else{
						if (record.get("GLFS") == 3&&this.th==1) {
							var module = this.createModule("getSUP0101010201","phis.application.sup.SUP/SUP/SUP0101010201");
							module.operater = this;
							this.module = module;
							module.WZXH = record.get("WZXH");
							module.DJXH = record.get("DJXH");
							module.KCXH = record.get("KCXH");
							module.tableName = "wl_rk01";
							module.th=this.th;
							this.rr = this.getSelectedRecord()
							if (this.rr == null) {
								return
							}
							this.win1 = module.getWin();
							this.win1.add(module.initPanel());
							module.loadData();
							this.win1.show();
						}
					}
				}
			},
			setCountInfo : function() {
				var totalWZSL = 0;
				var totalWZJE = 0;
				var totalLSJE = 0;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					var wzsl = parseFloat(r.get("WZSL"));
					var wzjg = parseFloat(r.get("WZJG"));
					var lsjg = parseFloat(r.get("LSJG"));
					if (wzsl) {
						totalWZSL += parseFloat(wzsl);
					}
					if (wzjg) {
						totalWZJE += parseFloat(wzsl * wzjg);
					}
					if (lsjg) {
						totalLSJE += parseFloat(wzsl * lsjg);
					}
					if (isNaN(totalWZSL)) {
						totalWZSL = 0;
					}
					if (isNaN(totalWZJE)) {
						totalWZJE = 0;
					}
					if (isNaN(totalLSJE)) {
						totalLSJE = 0;
					}
				}

				document.getElementById("yjrkwzxx_tjxx_" + this.openedBy).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "数量："
					+ parseFloat(totalWZSL).toFixed(2)
					+"&nbsp;&nbsp;"
					+ "进货金额："
					+ parseFloat(totalWZJE).toFixed(2)
					+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "零售金额："
					+ parseFloat(totalLSJE).toFixed(2) + "&nbsp;&nbsp;￥";
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				this.store.rejectChanges();
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
				this.setCountInfo();
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
			doCre : function() {
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				if (this.zczb) {
					this.store.remove(this.rr);
					var n = store.getCount()
					if (n > 0) {
						for (var i = 0; i < this.zczb.length; i++) {
							var sign = 0;
							var r = new Record(this.zczb[i]);
							r.set("LSJE",r.get("WZJE"));
							r.set("LSJG",r.get("WZJG"));
							r.set("KTSL",1);
							for (var j = 0; j < n; j++) {
								var re = store.getAt(j);
								if (re.get("ZBXH") == this.zczb[i].ZBXH) {
									sign = 1
								}
							}
							if (sign == 0) {
								store.add([r])
							}
						}
					}
					if (n == 0) {
						for (var i = 0; i < this.zczb.length; i++) {
							var r = new Record(this.zczb[i]);
							r.set("LSJE",r.get("WZJE"));
							r.set("LSJG",r.get("WZJG"));
							r.set("KTSL",1);
							store.add([r])
						}
					}
					this.setCountInfo();
				}
			}
		})