$package("phis.application.cic.script");

/**
 * 处方组套维护明细list zhangyq 2012.05.25
 */
$import("phis.script.EditorList");

phis.application.cic.script.ClinicPersonalComboNameDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.minListWidth = 480;
	cfg.remoteUrl = 'MedicineSet';
	cfg.remoteTpl = '<td width="3px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="50px">{YFGG}</td><td width="20px">{YFDW}</td>';
	phis.application.cic.script.ClinicPersonalComboNameDetailList.superclass.constructor
			.apply(this, [cfg])

}

var ypzh = 1;
Ext.extend(phis.application.cic.script.ClinicPersonalComboNameDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cic.script.ClinicPersonalComboNameDetailList.superclass.initPanel
						.call(this, sc)
				// var index =
				// grid.getColumnModel().findColumnIndex('SYPC');//拿到使用频次的对象
				// var cid = grid.getColumnModel().getColumnId(index);
				// var editor = grid.getColumnModel().getColumnById(cid).editor;
				// editor.on("select",this.onSelect,this);
				var sm = grid.getSelectionModel();
				/*
				 * grid.onEditorKey = function(field, e) { var sm =
				 * this.getSelectionModel(); var k = e.getKey(), newCell, g =
				 * sm.grid, ed = g.activeEditor; if (e.getKey() == e.ENTER &&
				 * !e.shiftKey) { var cell = sm.getSelectedCell(); var count =
				 * this.colModel.getColumnCount() if (cell[1] + 2 > count) {
				 * this.fireEvent("doNewColumn"); if (e.shiftKey) { newCell =
				 * g.walkCells(ed.row, ed.col - 1, -1, sm.acceptsNav, sm); }
				 * else { newCell = g.walkCells(ed.row, ed.col + 1, 1,
				 * sm.acceptsNav, sm); } r = newCell[0]; g.startEditing(r, 1);
				 * return; } }
				 * 
				 * this.selModel.onEditorKey(field, e); }
				 */
				var refThis = this;
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
						var cell = sm.getSelectedCell();
						if(cell[1] >= 7){
							refThis.doInsert();
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
				this.checkBox = new Ext.form.Checkbox({
							id : "autoNewGroup",
							boxLabel : "自动新组"
						});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(this.checkBox, ['-'], tbar);
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:blue'>药品条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
				// checkBox.disable();
				// 统计信息
				// var summary = new Ext.ux.grid.GridSummary();
				// cfg.plugins = [summary];
			},
			doInsert : function(item, e, newGroup) {// 当前记录后插入一条记录
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if (selectdRecord.get("XMBH") == null
							|| selectdRecord.get("XMBH") == ""
							|| selectdRecord.get("XMBH") == 0) {
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
				/** modified by chzhxiang 2013.10.11 张伟要求
				if (store.getCount() > 29) {
					MyMessageTip.msg("提示,纸张容量限制", '每张处方不能超过30条药品!', true, 5);
					return;
				}*/
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
					if (it.defaultValue) {
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
				if (row > 0) {
					if (!store.data.itemAt(row - 1).data.SYPC) {
						MyMessageTip.msg("提示", '使用频次不能为空!', true);
						return;
					}
					if (!store.data.itemAt(row - 1).data.GYTJ) {
						MyMessageTip.msg("提示,", '用法不能为空!', true);
						return;
					}
				}
				store.insert(row, [r])
				this.grid.getView().refresh()// 刷新行号
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YYTS", 1);
					rowItem.set("YPZH", 1);
					// this.ypzh++;
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					var rowItem = storeData.itemAt(row);
					rowItem.set("YPZH", upRowItem.get("YPZH"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("SYPC", upRowItem.get("SYPC"));
					rowItem.set("SYPC_text", upRowItem.get("SYPC_text"));
					rowItem.set("GYTJ", upRowItem.get("GYTJ"));
					rowItem.set("GYTJ_text", upRowItem.get("GYTJ_text"));
				}
				this.grid.startEditing(row, 2);
				var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "药品条数：" + n;
			},
			doInsertAfter : function(item, e, newGroup) {// 当前记录后插入一条记录
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();
				/** modified by chzhxiang 2013.10.11 张伟要求
				if (maxIndex > 29) {
					MyMessageTip.msg("提示,纸张容量限制", '每张处方不能超过30条药品!', true, 5);
					return;
				}*/
				if (maxIndex > 0) {
					if (!storeData.itemAt(maxIndex - 1).data.SYPC) {
						MyMessageTip.msg("提示", '使用频次不能为空!', true);
						return;
					}
					if (!storeData.itemAt(maxIndex - 1).data.GYTJ) {
						MyMessageTip.msg("提示,", '用法不能为空!', true);
						return;
					}
				}
				this.doCreate();
				var autoNewGroup = this.grid.getTopToolbar()
						.get("autoNewGroup").getValue();
				if (newGroup == true)
					autoNewGroup = true;
				if (maxIndex == 0 || autoNewGroup) {// 处方的第一条记录或者自动新组
					var ypzh = 1;
					var upRowItem = storeData.itemAt(maxIndex - 1);
					if (maxIndex > 0) {
						ypzh = upRowItem.get("YPZH") + 1;
					}
					var rowItem = storeData.itemAt(maxIndex);
					rowItem.set("JLBH", null);
					rowItem.set("YYTS", "1");
					rowItem.set("YPZH", ypzh);
					// this.ypzh++;
				} else {
					var upRowItem = storeData.itemAt(maxIndex - 1);
					var rowItem = storeData.itemAt(maxIndex);
					rowItem.set("YPZH", upRowItem.get("YPZH"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("SYPC", upRowItem.get("SYPC"));
					rowItem.set("SYPC_text", upRowItem.get("SYPC_text"));
					rowItem.set("GYTJ", upRowItem.get("GYTJ"));
					rowItem.set("GYTJ_text", upRowItem.get("GYTJ_text"));
				}
				this.grid.startEditing(maxIndex, 2);
				var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "药品条数：" + n;
			},
			doNewGroup : function(item, e) {
				this.doInsertAfter(item, e, true);
			},
			doNewClinic : function(item, e) {
				this.grid.store.removeAll()
				this.fireEvent("doNew");
			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH") % 2 + 1;
				switch (YPZH) {
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

			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'

								}, {
									name : 'YPJL'

								}, {
									name : 'YFGG'

								}, {
									name : 'YFDW'

								}, {
									name : 'JLDW'

								}, {
									name : 'GYFF'

								}, {
									name : 'GYFF_text'

								}]);
			},
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
					if (i != row && r.get("YPZH") == rowItem.get("YPZH")) {
						if (r.get("XMBH") == record.get("YPXH")) {
							MyMessageTip.msg("提示", "\"" + record.get("YPMC")
											+ "\"在这组中已存在，请进行修改！", true);
							return;
						}
					}
				}
				obj.collapse();
				
				// rowItem.set('XMMC', record.get("YPMC"));
				rowItem.set('XMBH', record.get("YPXH"));
				rowItem.set('YCJL', record.get("YPJL"));
				rowItem.set('JLDW', record.get("JLDW"));
				// 判断是否已经存在GYTJ GYFF
				if (rowItem.get("GYTJ") == null || rowItem.get("GYTJ") == ""
						|| rowItem.get("GYTJ") == 0) {
					rowItem.set('GYTJ', record.get("GYFF"));
					rowItem.set('GYTJ_text', record.get("GYFF_text"));
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 3);
			},
			doSave : function(item, e) {
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (!this.ZTBH) {
						MyMessageTip.msg("提示", "请先维护组套名称 ", true);
						return;
					}
					r.set('ZTBH', this.ZTBH);
					var items = this.schema.items;
					var msg = "保存失败";
					if (r.get("JLBH") == 0)
						r.set("JLBH", null);
					if (r.get("XMBH") == null) {
						MyMessageTip.msg("提示", "药品名称不能手工输入！错误行 " + (i + 1)
										+ " 。", true);
						return;
					} else {
						if (r.get("YYTS") <= 0) {
							MyMessageTip.msg("提示", "用药天数不能小于等于0！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						if (!r.get("SYPC")) {
							MyMessageTip.msg("提示", "使用频次不能为空！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						if (!r.get("GYTJ")) {
							MyMessageTip.msg("提示", "给药途径不能为空！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						for (var j = 0; j < items.length; j++) {
							var it = items[j]
							if (it.id == 'ZTBH' || it.id == 'JLBH')
								continue;
							if (r.get(it.id) == null || r.get(it.id) == "") {
								if (it['not-null']) {
									MyMessageTip.msg("提示", "数据保存失败，本次修改无效！错误行 "
													+ (i + 1) + " 。", true);
									return;
								}
							}
						}
						if (r.get("JLBH") == null) {
							r.data['_opStatus'] = 'create';
						} else {
							r.data['_opStatus'] = 'update';
						}
						data.push(r.data)
					}
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionSave,
							schema : "phis.application.cic.schemas.YS_MZ_ZT02_CF",
							body : data
						}, function(code, msg, json) {

							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
				this.store.rejectChanges();
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var date = {};
				date["ZTBH"] = r.get("ZTBH");
				if (r.get("JLBH") == null || r.get("JLBH") == 0) {
					this.store.remove(r);
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
					return;
				}
				Ext.Msg.confirm("请确认", "确认删除药品名称为【" + r.get("XMMC")
								+ "】的处方组套明细吗？", function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.serviceActionDel,
											schemaList : "YS_MZ_ZT01",
											schemaDetailsList : "YS_MZ_ZT02",
											pkey : r.get("JLBH"),
											body : date
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
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
																: (count - 1),
														cell[1]);
											}
											this.fireEvent("afterRemove",
													this.grid);
										}, this)
							}
						}, this);
				return

			},
			/**
			 * 获取保存数据的请求数据
			 * 
			 * @return {}
			 */
			getSaveRequest : function(saveData) {
				var values = saveData;
				return values;
			},
			getYpzh : function() {
				// yjzh = 1;
				ypzh = 0;
				var store = this.grid.getStore();
				var n = store.getCount()
				var YPZHs = [];
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						ypzh = 1;
						var r = store.getAt(i)
						YPZHs.push(ypzh)
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YPZH') == r.get('YPZH')) {
							YPZHs.push(ypzh)
						} else {
							YPZHs.push(++ypzh)
						}
					}
				}
				for (var i = 0; i < YPZHs.length; i++) {
					var r = store.getAt(i);
					r.set('YPZH', YPZHs[i]);
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.getYpzh();
					document.getElementById("totcount").innerHTML = "药品条数：0";
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.getYpzh();
				store.commitChanges();
				// var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "药品条数：" + n;

			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == 'SYPC' || it.id == 'GYTJ' || it.id == 'YYTS') {
					var store = this.store;
					store.each(function(r) {
								if (r.get('YPZH') == record.get('YPZH')) {
									if (r.get("YPXH") != record.get("YPXH")) {
										r.set(it.id, v);
										r.set(it.id + '_text', record.get(it.id
														+ '_text'));
										if (it.id == 'SYPC') {
											r.set("MRCS", record.get("MRCS"));
										}
									}
								}
							}, this)
				}
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("XMBH") == null || r.get("XMBH") == ""
							|| r.get("XMBH") == 0) {
						store.remove(r);
					}
				}
			}
		})