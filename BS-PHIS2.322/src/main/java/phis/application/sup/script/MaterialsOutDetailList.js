$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.MaterialsOutDetailList = function(cfg) {
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.remoteUrl = "MaterialsOut";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="200px">{WZMC}</td><td width="60px">{WZGG}</td><td width="40px">{WZJG}</td><td width="40px">{TJSL}</td><td width="100px">{WZPH}</td>';
	cfg.minListWidth = 600;
	cfg.disablePagingTbr = true;
	phis.application.sup.script.MaterialsOutDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sup.script.MaterialsOutDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sup.script.MaterialsOutDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 10 > count) {
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
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var labelyjck = new Ext.form.Label({
					html : "<div id='yjckwzxx_tjxx_"
							+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "数量：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "进货金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "零售金额：0.00&nbsp;&nbsp;￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(labelyjck);
			},
			onReady : function() {
				phis.application.sup.script.MaterialsOutDetailList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'sups',
							// 类里面总数的参数名
							totalProperty : 'count'
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
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (record.get("GLFS") != "3") {
						if (record.get("KCXH") && record.get("KCXH") != "0") {
							if (r.get("KCXH") == record.get("KCXH")) {
								MyMessageTip.msg("提示", "\""
												+ record.get("WZMC")
												+ "\"在这组中已存在，请进行修改！", true);
								return;
							}
						} else {
							if (r.get("WZXH") == record.get("WZXH")) {
								MyMessageTip.msg("提示", "\""
												+ record.get("WZMC")
												+ "\"在这组中已存在，请进行修改！", true);
								return;
							}
						}
					}
				}
				obj.collapse();
				obj.triggerBlur();
				rowItem.set('WZXH', record.get("WZXH"));
				rowItem.set('WZMC', record.get("WZMC"));
				rowItem.set('CJXH', record.get("CJXH"));
				rowItem.set('CJMC', record.get("CJMC"));
				rowItem.set('WZGG', record.get("WZGG"));
				rowItem.set('WZDW', record.get("WZDW"));
				rowItem.set('KCXH', record.get("KCXH"));
				if (record.get("WZPH") != null) {
					rowItem.set('WZPH', record.get("WZPH"));
				} else {
					rowItem.set('WZPH', "");
				}
				if (record.get("SCRQ") != null) {
					rowItem.set('SCRQ', record.get("SCRQ"));
				} else {
					rowItem.set('SCRQ', "");
				}
				if (record.get("SXRQ") != null) {
					rowItem.set('SXRQ', record.get("SXRQ"));
				} else {
					rowItem.set('SXRQ', "");
				}
				if (record.get("WZJG")) {
					rowItem.set('WZJG', record.get("WZJG"));
				}
				if (record.get("LSJG")) {
					rowItem.set('LSJG', record.get("LSJG"));
				}
				rowItem.set('TJCKSL', record.get("TJSL"));
				rowItem.set('BKBZ', record.get("BKBZ"));
				rowItem.set('GLFS', record.get("GLFS"));
				if (record.get("GLFS") == 1) {
					rowItem.set('GLFS_text', "库存管理");
				} else if (record.get("GLFS") == 2) {
					rowItem.set('GLFS_text', "科室管理");
				} else {
					rowItem.set('GLFS_text', "台帐管理");
				}
				if (record.get("MJPH") != null) {
					rowItem.set('MJPH', record.get("MJPH"));
				} else {
					rowItem.set('MJPH', "");
				}
				obj.setValue(record.get("WZMC"));
				this.grid.startEditing(row, 8);
			},
			doInit : function() {
				this.editRecords = [];
			},
			doCreate : function(item, e) {
				phis.application.sup.script.MaterialsOutDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},// 选择固定资产后
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
							store.add([r])
						}
					}
					this.setCountInfo();
				}
			},// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "WZSL") {
					var wzje = 0;
					var lsje = 0;
					if (((v != null && v != "") || v == 0)
							&& record.get("WZJG") != null
							&& record.get("WZJG") != "") {
						wzje = (v * record.get("WZJG")).toFixed(2);
					}
					if (((v != null && v != "") || v == 0)
							&& record.get("LSJG") != null
							&& record.get("LSJG") != "") {
						lsje = (Number(record.get("WZSL")) * Number(record
								.get("LSJG"))).toFixed(2);
					}
					this.setCountInfo();
					record.set("WZJE", wzje);
					record.set("LSJE", lsje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					if (record.get("GLFS") == 3) {
						if(record.get("KTSL")){
							if ((Number(record.get("WZSL"))) > (Number(record.get("KTSL")))) {
								MyMessageTip.msg("提示", "退回数量不能大于可退数量", true);
							
							}else{
								var module = this.createModule("getSUP0301010201",
								"phis.application.sup.SUP/SUP/SUP0301010201");
								module.operater = this;
								this.module = module;
								module.WZXH = record.get("WZXH")
								module.DJXH = record.get("DJXH");
								module.KCXH = record.get("KCXH");
								module.tableName = "wl_ck01";
								module.th=this.th;
								this.rr = this.getSelectedRecord()
								if (this.rr == null) {
									return
								}
								this.win2 = module.getWin();
								this.win2.add(module.initPanel());
								module.loadData();
								this.win2.show();
							}
						}else{
							if(record.get("TJCKSL")){
								if ((Number(record.get("WZSL"))) > (Number(record.get("TJCKSL")))) {
									MyMessageTip.msg("提示", "退回数量不能大于可退数量", true);
								
								}else{
									var module = this.createModule("getSUP0301010201",
									"phis.application.sup.SUP/SUP/SUP0301010201");
									module.operater = this;
									this.module = module;
									module.WZXH = record.get("WZXH")
									module.DJXH = record.get("DJXH");
									module.KCXH = record.get("KCXH");
									module.tableName = "wl_ck01";
									module.th=this.th;
									this.rr = this.getSelectedRecord()
									if (this.rr == null) {
										return
									}
									this.win2 = module.getWin();
									this.win2.add(module.initPanel());
									module.loadData();
									this.win2.show();
								}
							}
						}
					}
					this.grid.getView().refresh();
				}
			},
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				this.requestData.serviceId = "phis.materialsOutService";
				this.requestData.serviceAction = "getCK02Info";
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
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
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
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
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

				document.getElementById("yjckwzxx_tjxx_").innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
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
			}
		})