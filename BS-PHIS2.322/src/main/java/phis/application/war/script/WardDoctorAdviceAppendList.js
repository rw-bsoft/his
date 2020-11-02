$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.WardDoctorAdviceAppendList = function(cfg) {
	cfg.sortable = false;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.minListWidth = 420;
	cfg.remoteUrl = 'MedicineClinic';
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="250px">{FYMC}</td><td width="80px">{FYDW}</td>';
	cfg.queryParams = {
		TYPE : 0
	}
	this.totalStore = {};
	this.removeAppendRecords = [];
	phis.application.war.script.WardDoctorAdviceAppendList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.myLoadData, this);
	this.on("beforeCellEdit", this.beforeGridEdit, this);
}
Ext.extend(phis.application.war.script.WardDoctorAdviceAppendList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.war.script.WardDoctorAdviceAppendList.superclass.initPanel
						.call(this, sc)
				grid.onEditorKey = function(field, e) {
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						var r = this.store.getAt(cell[0]);
						if (cell[1] + 1 >= count && !this.editing) {
							this.fireEvent("doNewColumn");
							return;
						}
					}
					this.selModel.onEditorKey(field, e);
				}
				var sm = grid.getSelectionModel();
				// 重写onEditorKey方法，实现Enter键导航功能
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						e.stopEvent();
						if (!ed) {
							ed = g.lastActiveEditor;
						}
						ed.completeEdit();
						if (ed.col == 2) {
							if (g.getStore().getAt(ed.row).get("YPDJ") > 0) {
								g.fireEvent("doNewColumn");
								return;
							} else {
								ed.col = 3;
							}
						}
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
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
			},
			onStoreBeforeLoad : function(store, op) {
			},
			beforeGridEdit : function(it, record, field, value) {
				if (record.get("LSBZ") == 1)
					return false;
				if (it.id == "YPDJ" && value > 0) {
					return false;
				}
			},
			removeEmptyRecord : function() {
				var store = this.totalStore;
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if ((r.get("YPXH") == null || r.get("YPXH") == "" || r
							.get("YPXH") == 0)) {
						store.remove(r);
					}
				}
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH_SHOW") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			dateFormat : function(value, params, r, row, col, store) {
				return Ext.util.Format.date(Date
								.parseDate(value, "Y-m-d H:i:s"), 'Y.m.d H:i')
			},
			doInsertAfter : function() {
				this.doInsert(0, 0, true);
			},
			doInsert : function(item, e, maxRow) {// 当前记录前插入一条记录
				var mainRow = this.mainList.getSelectedRecord();
				if (!mainRow || !mainRow.get("YPXH")) {
					if (mainRow.get("JFBZ") == 3) {
						MyMessageTip.msg("提示", "行为医嘱不能添加附加项目!", true);
					} else {
						MyMessageTip.msg("提示", "请先选择主医嘱后再添加附加项目!", true);
					}
					return;
				}
				for (var i = 0; i < this.mainList.store.getCount(); i++) {
					var rm = this.mainList.store.getAt(i);
					if (rm.get("YZZH_SHOW") == mainRow.get("YZZH_SHOW")
							&& (rm.get("SYBZ") == 1 || (rm.get("QRSJ") && rm
									.get("QRSJ").length > 0))) {
						var cell = this.mainList.grid.getSelectionModel()
								.getSelectedCell();
						MyMessageTip.msg("提示", "主医嘱已确认或者执行，不能添加附加项目!", true);
						return;
					}
				}
				// if (mainRow.get("YPLX") <= 0) {
				// MyMessageTip.msg("提示", "非药品医嘱，不能插入附加项目!", true);
				// return;
				// }
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0)
							&& selectdRecord.get("JFBZ") != 3) {
						selectRow = selectRow;
					} else {
						selectRow = this.store.indexOf(selectdRecord) + 1;
					}
				} else {
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue != undefined) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v]
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
				}
				var r = new Record(data)
				try {
					this.totalStore.add(r);
					if (maxRow) {
						store.add(r);
					} else {
						store.insert(row, [r])
					}
				} catch (e) {
					store.removeAll();
					return;
				}
				var storeData = store.data;
				var rowItem = null;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 第一条记录
					rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YCSL", 1);
					rowItem.set("KSSJ", mainRow.get("KSSJ"));
					rowItem.set("LSYZ", mainRow.get("LSYZ"));
					rowItem.set("YZZH", mainRow.get("YZZH"));
					rowItem.set("YPYF", mainRow.get("YPYF") || 0);
					rowItem.set("YPYF_text", mainRow.get("YPYF_text") || "");
					rowItem.set("SYPC", mainRow.get("SYPC"));
					rowItem.set("SYPC_text", mainRow.get("SYPC_text"));
					rowItem.set("MRCS", mainRow.get("MRCS"));
					rowItem.set("SRCS", mainRow.get("SRCS"));
					rowItem.set("YZZXSJ", mainRow.get("YZZXSJ"));
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					rowItem = storeData.itemAt(row);
					rowItem.set("YCSL", 1);
					rowItem.set("KSSJ", upRowItem.get("KSSJ"));
					rowItem.set("YZZH", upRowItem.get("YZZH"));
					rowItem.set("LSYZ", upRowItem.get("LSYZ"));
					rowItem.set("YPYF", upRowItem.get("YPYF") || 0);
					rowItem.set("YPYF_text", upRowItem.get("YPYF_text") || "");
					rowItem.set("SYPC", upRowItem.get("SYPC"));
					rowItem.set("SYPC_text", upRowItem.get("SYPC_text"));
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("SRCS", upRowItem.get("SRCS"));
					rowItem.set("YZZXSJ", upRowItem.get("YZZXSJ"));
				}
				this.grid.getView().refresh()// 刷新行号
				// 设置公共属性
				rowItem.set("YSGH", mainRow.get("YSGH"));
				rowItem.set("YZPB", mainRow.get("XMLX") || 1);
				rowItem.set("uniqueId", mainRow.get("uniqueId"));
				rowItem.set("YSBZ", (this.mainList.openBy == "nurse" ? 0 : 1));
				rowItem.set("SYBZ", 0);
				rowItem.set("YPCD", 0);
				rowItem.set("JFBZ", 2);
				rowItem.set("YCJL", 0);
				rowItem.set("YJZX", 0);
				rowItem.set("YFSB", 0);
				rowItem.set("YPLX", 0);
				rowItem.set("LSBZ", 0);
				rowItem.set("ZFPB", 0);
				rowItem.set("YJXH", 0);
				rowItem.set("JGID", this.mainApp['phisApp'].deptId);
				rowItem.set("ZYH", this.mainList.initDataId);
				if (maxRow) {
					this.grid.startEditing(this.store.getCount() - 1, 1);
				} else {
					this.grid.startEditing(row, 1);
				}
			},
			setBackInfo : function(obj, record) {
				Ext.EventObject.stopEvent();// 停止事件
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				if (!this.setRecordIntoList(record.data, rowItem, row)) {
					return;
				}
				var YZMC = record.get("FYMC")
						+ (record.get("FYDW") ? "/" + record.get("FYDW") : "")
				obj.setValue(YZMC);
				obj.collapse();
				obj.triggerBlur();
				rowItem.set("YZMC", YZMC);
				this.grid.startEditing(row, 2);
			},
			setRecordIntoList : function(data, rowItem, curRow) {
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (i != curRow) {
						if (r.get("YPXH") == data.FYXH) {
							MyMessageTip.msg("提示", "\"" + data.FYMC
											+ "\"在这组中已存在，请进行修改!", true);
							return false;
						}
					}
				}
				rowItem.set("ZXKS", data.FYKS
								|| this.mainList.exContext.brxx.get("BRKS"));
				rowItem.set("XMLX", data.XMLX || 4);
				rowItem.set("YPXH", data.FYXH);
				rowItem.set("YJXH", 0);
				rowItem.set("YZMC", data.FYMC);
				rowItem.set("YPDJ", data.FYDJ);
				rowItem.set("YPDW", data.FYDW);
				rowItem.set("YPLX", 0);
				rowItem.set("YFBZ", 0);
				return true;
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);

					if ((r.get("YPXH") == null || r.get("YPXH") == "" || r
							.get("YPXH") == 0)) {
						store.remove(r);
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'
								}, {
									name : 'YZMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YPDW'
								}, {
									name : 'YPSL'
								}, {
									name : 'JLDW'
								}, {
									name : 'YPJL'
								}, {
									name : 'YFBZ'
								}, {
									name : 'GYFF'
								},// 药品用法
								{
									name : 'LSJG'
								}, {
									name : 'YPCD'
								}, {
									name : 'CDMC'
								}, {
									name : 'PSPB'
								}, {
									name : 'TSYP'
								}, {
									name : 'TYPE'
								}, {
									name : 'TSYP'
								}, {
									name : 'YFDW'
								}, {
									name : 'YBFL'
								}, {
									name : 'YBFL_text'
								}, {
									name : 'GYFF_text'
								}, {
									name : 'JYLX'
								}, {
									name : 'YPGG'
								}, {// 以下为项目部分
									name : 'FYXH'
								}, {
									name : 'FYMC'
								}, {
									name : 'FYDW'
								}, {
									name : 'XMLX'
								}, {
									name : 'FYDJ'
								}, {
									name : 'FYGB'
								}, {
									name : 'FYKS'
								}, {
									name : 'FYKS_text'
								}, {
									name : 'YPLX'
								}, {
									name : 'FYFS'
								}, {
									name : 'FYFS_text'
								}]);
			},
			/**
			 * 重写doRemove，当grid中的数据未保存在数据库时，直接从grid中删除，若删除的数据 已保存，则发起请求删除数据库中数据
			 */
			doRemove : function() {
				var mainRow = this.mainList.getSelectedRecord();
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("QRRQ")) {
					MyMessageTip.msg("提示", "该医嘱已计费，不能删除!", true);
					return;
				}
				if (r.get("LSBZ") == 1) {
					MyMessageTip.msg("提示", "该医嘱已转为历史，不能删除!", true);
					return;
				}
				for (var i = 0; i < this.mainList.store.getCount(); i++) {
					var rm = this.mainList.store.getAt(i);
					if (rm.get("YZZH_SHOW") == mainRow.get("YZZH_SHOW")
							&& (rm.get("SYBZ") == 1 || (rm.get("QRSJ") && rm
									.get("QRSJ").length > 0))) {
						MyMessageTip.msg("提示", "主医嘱已确认或者执行，不能删除附加项目!", true);
						return;
					}
				}
				if (r.get("JLXH") == null && !r.get("YPXH")) {
					this.store.remove(r);
					this.totalStore.remove(r);
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
				} else {
					Ext.Msg.show({
								title : '确认删除附加项目[' + r.data.YZMC + ']',
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										// 移除之后焦点定位
										var count = this.store.getCount();
										if (count > 0) {
											cm.select(cell[0] < count
															? cell[0]
															: (count - 1),
													cell[1]);
										}
										if (r.data.JLXH) {
											// 记录后台需要删除的处方识别
											r.set("_opStatus", "remove");
										} else {
											this.totalStore.remove(r);
										}
										this.store.remove(r);
										// 移除之后焦点定位
										var count = this.store.getCount();
										if (count > 0) {
											cm.select(cell[0] < count
															? cell[0]
															: (count - 1),
													cell[1]);
										}
									}
								},
								scope : this
							})

				}
			},
			myLoadData : function() {
				// 缓存附加项目store
				var records = [];
				this.store.each(function(r) {
							records.push(r.copy());
						});
				this.totalStore = new Ext.data.JsonStore({
							recordType : this.store.recordType
						});
				this.totalStore.add(records);
				this.store.removeAll();
			}
		});
