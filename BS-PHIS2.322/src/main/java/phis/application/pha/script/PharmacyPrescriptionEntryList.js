$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyPrescriptionEntryList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	// cfg.summaryable = true;
	cfg.sortable = false;
	cfg.minListWidth = 510;
	cfg.remoteUrl = 'MedicineFEE';
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="160px" title="{YPMC}">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td><td width="50px">{KCSL}</td>';
	this.serviceId = "clinicManageService";
	phis.application.pha.script.PharmacyPrescriptionEntryList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadData, this);
}

Ext.extend(phis.application.pha.script.PharmacyPrescriptionEntryList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				this.type = 1;
				var grid = phis.application.pha.script.PharmacyPrescriptionEntryList.superclass.initPanel
						.call(this, sc);
				var sm = grid.getSelectionModel();
				var _ctx = this;
				// 重写grid的onEditorKey事件
				grid.onEditorKey = function(field, e) {
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						// var r = _ctx.getSelectedRecord();
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount();
						if (cell[1] + 1 >= count) {// 实现倒数第二格单元格回车新增行操作
							_ctx.insert(_ctx.type, 0);
							return;
						}
					}
					this.selModel.onEditorKey(field, e);
				};
				// 重写onEditorKey方法，实现Enter键导航功能
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
						// 判断单价是否为0
						if (c == 7) {
							if (field.getValue() == 0) {
								c--;
							}
						}
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
				this.on("afterCellEdit", this.afterGridEdit, this);
				return grid;
			},
			onReady : function() {
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.grid.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				// index btns
				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				} else {
					var btns = this.grid.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				keyMap.on(keys, this.onAccessKey, this)
				keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)
			},
			onEnterKey : function() {
				Ext.EventObject.stopEvent();
			},
			setBtnStatus : function(disabled) {
				var btns = this.grid.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (disabled) {
						if (i != 5) {
							btn.disable();
						}
					} else {
						btn.enable();
					}
				}
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='cfmx_tjxx_yfhj' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "西药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "中药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "草药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			afterLoadData : function() {
				if (this.store.getCount() > 0) {
					this.grid.getSelectionModel().select(0, 4);
					this.onRowClick();
				}
				this.totalCountInfo();
				this.reloadYPZH();
				this.store.commitChanges();
			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == 'YPSL') {
					if (record.get("YPSL") === 0) {
						record.set("YPXH", "");
						this.doRemove();
						this.totalCountInfo();
						return;
					}
					if (record.get("YPDJ") && record.get("YPSL")) {
						record.set("HJJE", parseFloat(parseFloat(v)
										* parseFloat(record.get("YPDJ"))
										* parseFloat(record.get("CFTS")))
										.toFixed(2));
						this.totalCountInfo();
					}
					var YYZL = v;
					if (record.get("CFLX") == 3) {
						var CFTS = record.get("CFTS");
						if (CFTS > 0) {
							YYZL = YYZL * parseInt(CFTS);
						}
					}
					if (record.get("CFLX") > 0) {
						this.checkInventory(YYZL, record);
					}
				} else if (it.id == 'YPDJ') {
					if (record.get("YPDJ") && record.get("YPSL")) {
						record.set("HJJE", parseFloat(parseFloat(v)
										* parseFloat(record.get("YPSL"))
										* parseFloat(record.get("CFTS")))
										.toFixed(2));
						this.totalCountInfo();
					}
				} else if (it.id == 'YPYF') {
					this.store.each(function(r) {
						if (r.get('YPZH_show') == record.get('YPZH_show')) {
							r.set(it.id, v);
							r.set(it.id + '_text', record.get(it.id + '_text'));
						}
					}, this)
				}
			},
			checkInventory : function(YYZL, record) {
				if (YYZL == null || YYZL == 0)
					return;
				var type = record.get("CFLX");
				var pharmId = this.getPharmacyIdByCFLX(type);
				var data = {};
				data.medId = record.get("YPXH");
				if (data.medId == null || data.medId == "") {
					return;
				}
				data.medsource = record.get("YPCD");
				if (data.medsource == null || data.medsource == "") {
					MyMessageTip.msg("警告", record.get("msg"), true);
					return;
				}
				data.quantity = YYZL;
				data.pharmId = pharmId;
				data.ypmc = record.get("YPMC");
				if (record.get("SBXH") != null && record.get("SBXH") != "") {
					data.jlxh = record.get("SBXH");
				}
				data.lsjg = record.get("YPDJ");
				// 校验是否有足够药品库存
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "checkInventory",
							body : data
						}, function(code, mmsg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, mmsg);
								return;
							}
							if (json.sign > 0) {
								record.set("msg", "");
								var ypzh = record.get("YPZH_show");
								record.set("YPZH_show", 0);
								record.set("YPZH_show", ypzh);// 刷新提示信息
								return;
							}
							var msg = "药品【" + data.ypmc + "】库存不足!库存数量："
									+ json.KCZL + ",实际数量：" + data.quantity;
							MyMessageTip.msg("警告", msg, true);
							record.set("msg", msg);
							var ypzh = record.get("YPZH_show");
							record.set("YPZH_show", 0);
							record.set("YPZH_show", ypzh);// 刷新提示信息
						}, this)
			},
			/**
			 * 重写doRemove，当grid中的数据未保存在数据库时，直接从grid中删除，若删除的数据 已保存，则发起请求删除数据库中数据
			 */
			doRemove : function(item, e, row) {

				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord();
				if (row) {
					r = row;
				}
				if (r == null) {
					return
				}
				if (r.data.CF_NEW) {
					var n = this.store.getCount();
					var selectRow = this.store.indexOf(r);
					if (selectRow < n - 1) {
						var nextrow = this.store.getAt(selectRow + 1);
						nextrow
								.set('CF_NEW', this
												.getCFLX(nextrow.get('CFLX')));
					}
				}
				if (r.data.SBXH) {
					this.store.remove(r);
					if (this.MZXX.removeData) {
						this.MZXX.removeData.push(r.data);
						if (parseInt(r.data.CFLX) > 0) {
							this.MZXX.removeCFSB.push(r.data.CFSB);
						}
					} else {
						this.MZXX.removeData = [];
						this.MZXX.removeCFSB = [];
						this.MZXX.removeData.push(r.data);
						if (parseInt(r.data.CFLX) > 0) {
							this.MZXX.removeCFSB.push(r.data.CFSB);
						}
					}
					// this.doRemove(r.data.CFSB);
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						this.reloadYPZH();
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					} else {
						this.insert(this.type, 1, 1);
					}
				} else {
					this.store.remove(r);
					this.insert(this.type, 1, 1);
				}
				this.totalCountInfo();
			},
			getCFLX : function(type) {
				type = parseInt(type);
				var CFLXSTR = null;
				switch (type) {
					case 1 :
						CFLXSTR = "西";
						break;
					case 2 :
						CFLXSTR = "成";
						break;
					case 3 :
						CFLXSTR = "草";
						break;
					default :
						CFLXSTR = "检";
				}
				return CFLXSTR;
			},
			reloadYPZH : function() {
				ypzh = 0;
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (i == 0) {
						ypzh = 1;
						r.set('YPZH_show', ypzh);
						r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
					} else {
						var r1 = store.getAt(i - 1);
						if (r.get("CFSB") && r1.get("CFSB")) {
							if (r1.get('CFSB') == r.get('CFSB')) {
								if (r1.get('YPZH') == r.get('YPZH')) {
									r.set('YPZH_show', ypzh);
									r.set('CF_NEW', "");
								} else {
									r.set('YPZH_show', ++ypzh);
									r.set('CF_NEW', "");
								}
							} else {
								r.set('YPZH_show', ++ypzh);
								r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
							}
						} else {
							if (r.get("CF_NEW")) {
								r.set('YPZH_show', ++ypzh);
								r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
							} else {
								if (r1.get('YPZH') == r.get('YPZH')) {
									r.set('YPZH_show', ypzh);
									r.set('CF_NEW', "");
								} else {
									r.set('YPZH_show', ++ypzh);
									r.set('CF_NEW', "");
								}
							}
						}
					}
				}
			},
			doXY : function() {
				this.remoteDic.lastQuery = "";
				this.type = "1";
				this.insert("1", 1, 1);
			},
			doZY : function() {
				this.remoteDic.lastQuery = "";
				this.type = "2";
				this.insert("2", 1, 1);
			},
			doCY : function() {
				this.remoteDic.lastQuery = "";
				this.type = "3";
				this.insert("3", 1, 1);
			},
			setYSDM : function(ysdm) {
				var r = this.getSelectedRecord();
				if (!r) {
					var n = this.store.getCount();
					if (n == 0)
						return;
					var rowItem = this.store.getAt(n - 1);
					r = rowItem;
				}
				r.set("YSDM", ysdm)
				var store = this.grid.getStore();
				var n = store.getCount();
				if (r.data.CF_NEW) {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("YSDM", ysdm);
						}
					}
				} else {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow; i >= 0; i--) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							rowItem.set("YSDM", ysdm);
							break;
						} else {
							rowItem.set("YSDM", ysdm);
						}
					}
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("YSDM", ysdm);
						}
					}
				}
			},
			setKSDM : function(ksdm) {
				var r = this.getSelectedRecord();
				if (!r) {
					var n = this.store.getCount();
					if (n == 0)
						return;
					var rowItem = this.store.getAt(n - 1);
					r = rowItem;
				}
				r.set("KSDM", ksdm)
				var store = this.grid.getStore();
				var n = store.getCount();
				if (r.data.CF_NEW) {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("KSDM", ksdm);
						}
					}
				} else {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow; i >= 0; i--) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							rowItem.set("KSDM", ksdm);
							break;
						} else {
							rowItem.set("KSDM", ksdm);
						}
					}
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("KSDM", ksdm);
						}
					}
				}
			},
			insert : function(type, newGroup, newCF) {
				if (!this.opener.Brxx) {
					MyMessageTip.msg("提示", "请先调入患者信息!", true);
					return;
				}
				var selectdRecord = this.getSelectedRecord();
				this.opener.form.setCFLX(type);
				this.remoteDicStore.baseParams.type = type;
				this.remoteDicStore.baseParams.pharmacyId = this
						.getPharmacyIdByCFLX(type);
				var store = this.grid.getStore();
				// var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0)) {
						if (newGroup) {
							var n = store.getCount();
							if (n - 1 > selectRow) {
								for (var i = selectRow + 1; i < n; i++) {
									var rowItem = store.getAt(i);
									if (rowItem.get("CF_NEW")) {
										selectRow = i;
										break;
									}
									if (i == n - 1) {
										selectRow = n;
									}
								}
							}
						}
					} else {
						if (newGroup) {
							var n = store.getCount();
							if (n - 1 > selectRow) {
								for (var i = selectRow + 1; i < n; i++) {
									var rowItem = store.getAt(i);
									if (rowItem.get("CF_NEW")) {
										selectRow = i;
										break;
									}
									if (i == n - 1) {
										selectRow = n;
									}
								}
							} else {
								selectRow = n;
							}
						} else {
							selectRow = this.store.indexOf(selectdRecord) + 1;
						}
					}
				} else {
					this.removeEmptyRecord();
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
				var o = this.getStoreFields(this.schema.items);
				var Record = Ext.data.Record.create(o.fields);
				var items = this.schema.items;
				var factory = util.dictionary.DictionaryLoader;
				var data = {
					'_opStatus' : 'create'
				};
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					var v = null;
					if (it.defaultValue) {
						v = it.defaultValue;
						data[it.id] = v;
						var dic = it.dic;
						if (dic) {
							data[it.id] = v.key;
							var o = factory.load(dic);
							if (o) {
								var di = o.wraper[v.key];
								if (di) {
									data[it.id + "_text"] = di.text;
								}
							}
						}
					}
					if (it.type && it.type == "int") {
						data[it.id] = (data[it.id] == "0" || data[it.id] == "" || data[it.id] == undefined)
								? 0
								: parseInt(data[it.id]);
					}

				}
				data.YPMC = "";
				data.DJLY = 2;
				data.CFLX = type;
				if ((type + "") == "3") {
					data.CFTS = this.opener.form.form.getForm()
							.findField("CFTS").getValue();
				} else {
					data.CFTS = 1;
				}
				data.YSDM = this.opener.form.form.getForm().findField("YSDM")
						.getValue();
				data.KSDM = this.opener.form.form.getForm().findField("KSDM")
						.getValue();
				if (newCF) {
					data.CF_NEW = this.getCFLX(type);
					var n = store.getCount();
					row = n;
				}
				var r = new Record(data);
				this.selectedIndex = row;
				store.insert(row, [r]);
				this.grid.getView().refresh();// 刷新行号
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YPZH", 1);

				} else {
					// MyMessageTip.msg("提示",row,true)
					var upRowItem = storeData.itemAt(row - 1);
					var rowItem = storeData.itemAt(row);
					if (newGroup == "1") {
						rowItem.set("YPZH", upRowItem.get("YPZH") + 1);
					} else {
						rowItem.set("YPZH", upRowItem.get("YPZH"));
					}
					if (!newGroup && !newCF) {
						rowItem.set("YPYF", upRowItem.get("YPYF"));
						rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
						if ((type + "") != "3") {
							rowItem.set("GYTJ", upRowItem.get("GYTJ"));
							rowItem.set("GYTJ_text", upRowItem.get("GYTJ_text"));
						}
						rowItem.set("MRCS", upRowItem.get("MRCS"));
						rowItem.set("uniqueId", upRowItem.get("uniqueId"));
					}
				}
				if (row == 0) {
					if (!this.opener.form.form.getForm().findField("YSDM")
							.getValue()) {
						this.opener.form.form.getForm().findField("YSDM")
								.focus();
					} else if (!this.opener.form.form.getForm()
							.findField("KSDM").getValue()) {
						this.opener.form.form.getForm().findField("KSDM")
								.focus();
					} else if ((type + "") == "3" && newCF) {
						this.opener.form.form.getForm().findField("CFTS")
								.focus(true, 200);
					} else {
						this.grid.startEditing(row, 3);
					}
				} else {
					if ((type + "") == "3" && newCF) {
						this.opener.form.form.getForm().findField("CFTS")
								.focus(true, 200);
					} else {
						this.grid.startEditing(row, 3);
					}
				}
				this.reloadYPZH();
			},
			doInsert : function() {
				var row = this.getSelectedRecord();
				if (!row) {
					var n = this.store.getCount();
					if (n > 0) {
						row = this.store.getAt(n - 1);
					}
				}
				if (row.data.YPXH) {
					this.insert(row.get("CFLX"), 0, 0);
					return;
				}
				var n = this.store.indexOf(row);
				if (n > 0) {
					for (var i = n - 1; i >= 0; i--) {
						var r = this.store.getAt(i);
						if (r.data.YPXH) {
							this.insert(this.type, 0, 0);
							return;
						}
					}
				}
			},
			doNewGroup : function() {
				var row = this.getSelectedRecord();
				if (!row) {
					var n = this.store.getCount();
					if (n > 0) {
						row = this.store.getAt(n - 1);
					}
				}
				if (row.data.YPXH) {
					this.insert(row.get("CFLX"), 1, false);
					return;
				}
				var n = this.store.indexOf(row);
				if (n > 0) {
					for (var i = n - 1; i >= 0; i--) {
						var r = this.store.getAt(i);
						if (r.data.YPXH) {
							this.insert(this.type, 1, false);
							return;
						}
					}
				}
			},
			doDelGroup : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.Msg.confirm("确认", "确认删除【" + r.get("YPMC") + "】所在的组的数据吗？",
						function(btn) {
							if (btn == 'yes') {
								var r = this.getSelectedRecord();
								var store = this.grid.getStore();
								var n = store.getCount();
								if (!this.MZXX.removeData) {
									this.MZXX.removeData = [];
									this.MZXX.removeCFSB = [];
								}
								if (r.data.CF_NEW) {
									selectRow = this.store.indexOf(r);
									for (var i = selectRow + 1; i < n; i++) {
										var rowItem = store.getAt(i);
										if (rowItem
												&& rowItem.data.YPZH_show == r.data.YPZH_show) {
											if (rowItem.data.SBXH) {
												this.MZXX.removeData
														.push(rowItem.data);
												if (parseInt(rowItem.data.CFLX) > 0) {
													this.MZXX.removeCFSB
															.push(rowItem.data.CFSB);
												}
												this.store.remove(rowItem);
												// this.doRemove(rowItem.data.CFSB);
												i--;
												n--;
											} else {
												this.store.remove(rowItem);
												i--;
												n--;
											}
										} else if (rowItem) {
											if (!rowItem.data.CF_NEW) {
												rowItem.set('CF_NEW',
														this.getCFLX(r
																.get('CFLX')));
											}
											break;
										}
									}
								} else {
									selectRow = this.store.indexOf(r);
									for (var i = selectRow - 1; i >= 0; i--) {
										var rowItem = store.getAt(i);
										if (rowItem
												&& rowItem.data.YPZH_show == r.data.YPZH_show) {
											if (rowItem.get('CF_NEW')) {
												this.CF_NEW = true;
											}
											if (rowItem.data.SBXH) {
												this.MZXX.removeData
														.push(rowItem.data);
												if (parseInt(rowItem.data.CFLX) > 0) {
													this.MZXX.removeCFSB
															.push(rowItem.data.CFSB);
												}
												this.store.remove(rowItem);
												// this.opener.opener
												// .doRemove(rowItem.data.CFSB);
											} else {
												this.store.remove(rowItem);
											}
										} else {
											break;
										}
									}
									selectRow = this.store.indexOf(r);
									for (var i = selectRow + 1; i < n; i++) {
										var rowItem = store.getAt(i);
										if (rowItem
												&& rowItem.data.YPZH_show == r.data.YPZH_show) {
											if (rowItem.data.SBXH) {
												this.MZXX.removeData
														.push(rowItem.data);
												if (parseInt(rowItem.data.CFLX) > 0) {
													this.MZXX.removeCFSB
															.push(rowItem.data.CFSB);
												}
												this.store.remove(rowItem);
												i--;
												n--;
											} else {
												this.store.remove(rowItem);
												i--;
												n--;
											}
										} else if (rowItem) {
											if (!rowItem.data.CF_NEW
													&& this.CF_NEW) {
												this.CF_NEW = false;
												rowItem.set('CF_NEW',
														this.getCFLX(r
																.get('CFLX')));
											}
											break;
										}
									}
								}
								this.store.remove(r);
								if (r.data.SBXH) {
									this.MZXX.removeData.push(r.data);
									if (parseInt(r.data.CFLX) > 0) {
										this.MZXX.removeCFSB.push(r.data.CFSB);
									}
								}
								this.insert(this.type, 1, 1);
							}
						}, this);
				this.totalCountInfo();
			},
			setBackSet1 : function(type,datas,i) {
				if(i==datas.length){
					this.endZT();
					return;
				}
				datas[i].YPDJ_Y = datas[i].YPDJ;
//				if(!this.opener.opener.MZXX)return;
				if(datas[i]&&datas[i].PSPB==1){
					Ext.MessageBox.confirm('提示', '【'+datas[i].YPMC+'】使用前需要做皮试处理，是否确认录入？',function(button){
						if(button=='yes'){
							if(this.YPZH){
								if(datas[i].YPZH!=this.YPZH){
									this.setBackSet(type,1,datas,i);
								}else{
									this.setBackSet(type,0,datas,i);
								}
							}else{
								this.YPZH = datas[i].YPZH;
								datas[i].CF_NEW = this.ztCF_NEW;
								this.setBackSet(type,1,datas,i);
							}
						}else{
							i++;
							if(this.YPZH){
								if(datas[i].YPZH!=this.YPZH){
									this.setBackSet1(type,datas,i);
								}else{
									this.setBackSet1(type,datas,i);
								}
							}else{
								this.YPZH = datas[i].YPZH;
								datas[i].CF_NEW = this.ztCF_NEW;
								this.setBackSet1(type,datas,i);
							}
						}
					},this);
				}else{
					if(this.YPZH){
						if(datas[i].YPZH!=this.YPZH){
							this.setBackSet(type,1,datas,i);
						}else{
							this.setBackSet(type,0,datas,i);
						}
					}else{
						this.YPZH = datas[i].YPZH;
						datas[i].CF_NEW = this.ztCF_NEW;
						this.setBackSet(type,1,datas,i);
					}
				}
			},
			setBackSet : function(type,newGroup,datas,i) {
				var data = datas[i];
//				if(!this.opener.opener.MZXX)return;
				var selectdRecord = this.getSelectedRecord();
				this.opener.form.setCFLX(type);
				this.remoteDicStore.baseParams.type = type;
				this.remoteDicStore.baseParams.pharmacyId = this.getPharmacyIdByCFLX(type);
				var store = this.grid.getStore();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0)) {
						if(newGroup){
							var n = store.getCount();
							if(n-1>selectRow){
								for (var i = selectRow+1; i < n; i++) {
									var rowItem = store.getAt(i);
									if(rowItem.get("CF_NEW")){
										selectRow = i;
										break;
									}
									if(i==n-1){
										selectRow = n;
									}
								}
							}else if(n-1==selectRow){
								selectRow = n;
							}
						}
					} else {
						if(newGroup){
							var n = store.getCount();
							if(n-1>selectRow){
								for (var i = selectRow+1; i < n; i++) {
									var rowItem = store.getAt(i);
									if(rowItem.get("CF_NEW")){
										selectRow = i;
										break;
									}
									if(i==n-1){
										selectRow = n;
									}
								}
							}else if(n-1==selectRow){
								selectRow = n;
							}
						}else{
							selectRow = this.store.indexOf(selectdRecord) + 1;
						}
					}
				} else {
					this.removeEmptyRecord();
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
				var o = this.getStoreFields(this.schema.items);
				var Record = Ext.data.Record.create(o.fields);
				data._opStatus='create';
				data.DJLY = 6;
				data.CFLX = type;
				if((type+"")=="3"){
					data.CFTS = this.opener.form.form.getForm().findField("CFTS").getValue();
				}else{
					data.CFTS = 1;
				}
				data.YSDM = this.opener.form.form.getForm().findField("YSDM").getValue();
				data.KSDM = this.opener.form.form.getForm().findField("KSDM").getValue();
				var r = new Record(data);
				if(type>0){
					this.checkInventory((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
				}else{
					this.checkInventoryCz((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
				}
				this.selectedIndex = row;
				store.insert(row, [r]);
				this.grid.getView().refresh();// 刷新行号
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YPZH", 1);
					
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					var rowItem = storeData.itemAt(row);
					if(newGroup=="1"){
						rowItem.set("YPZH", upRowItem.get("YPZH")+1);
					}else{
						rowItem.set("YPZH", upRowItem.get("YPZH"));
					}
					if(!newGroup){
						rowItem.set("YPYF", upRowItem.get("YPYF"));
						rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
					}
				}
				if(row==0){
					if(!this.opener.form.form.getForm().findField("YSDM").getValue()){
						this.opener.form.form.getForm().findField("YSDM").focus();
					}else if(!this.opener.form.form.getForm().findField("KSDM").getValue()){
						this.opener.form.form.getForm().findField("KSDM").focus();
//					}else{
//						this.grid.startEditing(row, 7);
					}
//				}else{
//					this.grid.startEditing(row, 7);
				}
				this.reloadYPZH();
				i++;
				this.setBackSet1(type,datas,i);
			},
			endZT : function(){
				this.totalCountInfo();
//				this.ZTend = true;
//				var _this = this; 
//				var deferFunction = function(){
				this.doNewGroup();
//				}
//				deferFunction.defer(1000);
			},
			setBackInfo : function(obj, record) {
				if(record.data.ISZT){
					var cell = this.grid.getSelectionModel().getSelectedCell();
					var row = cell[0];
					var count = this.store.getCount();
					if(row+1!=count){
						obj.collapse();
						obj.triggerBlur();
						obj.hasFocus = false;// add by yangl 2013.02.18
						MyMessageTip.msg("提示", "请在最后一行增加组套!", true);
						return;
					}
					if(record.data.TYPE!=0){
						this.serviceAction = "loadMedcineSet";
					}else{
						this.serviceAction = "loadProjectSet";
					}
					record.data.pharmacyId = this.remoteDicStore.baseParams.pharmacyId;
					record.data.BRXZ = this.opener.Brxx.BRXZ;
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicManageService",
						serviceAction : this.serviceAction,
						body : record.data
					});
					if (ret.code > 300) {
						MyMessageTip.msg("提示", ret.msg, true);
						return;
					} else {
						if (ret.json.body) {
							var r = this.getSelectedRecord();
							this.ztCFLX = r.data.CFLX;
//							if(r.data.CF_NEW){
//								this.ztCF_NEW = true;
//							}
//							this.doRemove();
							this.YPZH = "";
							
//							this.grid.startEditing(row, 7);
//							return;
							this.setBackSet1(record.data.TYPE,ret.json.body,0);
						}
					}
					obj.collapse();
					obj.triggerBlur();
					obj.hasFocus = false;// add by yangl 2013.02.18
					return;
				}
				//如果是非原液药品就停止操作
				var me=this;
				if(record.data&&record.data.PSPB==1)
				{
					obj.collapse();
					obj.triggerBlur();
					Ext.MessageBox.confirm('提示', '该药品使用前需要做皮试处理，是否确认录入？',function(button){
						if(button=='yes')
						{
							record.data.DJLY = 2;
							me.setData(obj, record);
						}
					});
				}else
				{
					record.data.DJLY = 2;
					me.setData(obj, record);
				}
			},
			setData : function(obj, record) {
				Ext.EventObject.stopEvent();// 停止事件
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				record.data.YPZH = rowItem.get("YPZH");
				// if(rowItem.data.CFLX>0){
				record.data.YFSB = this.remoteDicStore.baseParams.pharmacyId
				// }
				if (!this.setMedRecordIntoList(record.data, rowItem, row)) {
					return;
				}
				obj.setValue(record.get("YPMC"));
				obj.collapse();
				obj.triggerBlur();
				// 获取药品自负比例信息
				this.getPayProportion(record.data, rowItem);
				if (!record.data.LSJG) {
					this.grid.startEditing(row, 5);
				} else {
					this.grid.startEditing(row, 6);
				}
			},
			setMedRecordIntoList : function(data, rowItem, curRow) {
				// 将选中的记录设置到行数据中
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (i != curRow && r.get("YPZH") == rowItem.get("YPZH")) {
						if (r.get("YPXH") == data.YPXH) {
							MyMessageTip.msg("提示", "\"" + data.YPMC
											+ "\"在这组中已存在，请进行修改!", true);
							return false;
						}
					}
				}
				rowItem.set('YPMC', data.YPMC);
				rowItem.set('YFGG', data.YFGG);
				rowItem.set('JLDW', data.JLDW);
				rowItem.set('YFDW', data.YFDW);
				rowItem.set('YPXH', data.YPXH);
				rowItem.set('YPJL', data.YPJL);
				rowItem.set('YCJL', data.YPJL);
				rowItem.set('YFBZ', data.YFBZ);
				rowItem.set('YPDJ', data.LSJG);
				rowItem.set('YPDJ_Y', data.LSJG);
				rowItem.set('TYPE', data.TYPE);
				// rowItem.set('CFLX', data.TYPE);
				rowItem.set('YPCD', data.YPCD);
				rowItem.set('YFSB', data.YFSB);
				rowItem.set('YPSL', 1);
				rowItem.set('HJJE', parseFloat(data.LSJG * rowItem.get("CFTS"))
								.toFixed(2));
				rowItem.set('ZFPB', data.ZFPB);// 2013-09-10 add by gejj 添加自负判别
				// 添加输液判别
				if (data.YPCD_text) {
					rowItem.set('YPCD_text', data.YPCD_text);
				} else {
					rowItem.set('YPCD_text', data.CDMC);
				}
				rowItem.set('GYTJ', data.GYFF);
				rowItem.set('GYTJ_text', data.GYFF_text);
				rowItem.set('FYGB', data.FYGB);
				rowItem.set('GBMC', data.GBMC);
				if (data.TYPE == 0) {
					if (data.FYKS) {
						rowItem.set('ZXKS', data.FYKS);
					}
				}
				this.totalCountInfo();
				return true;
			},
			getPayProportion : function(data, rowItem) {
				var body = {};
				body.BRXZ = this.opener.Brxx.BRXZ;
				body.TYPE = data.TYPE;
				body.FYGB = rowItem.get("FYGB");
				body.FYXH = data.YPXH;
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "getPayProportion",
							body : body
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var zfbl = json.body.ZFBL;
							rowItem.set('ZFBL', zfbl);
						}, this);

			},
			totalCountInfo : function() {
				var n = this.store.getCount();
				var xyf = 0;
				var zyf = 0;
				var cyf = 0;
				var jcf = 0;
				var hjje = 0;
				var zfje = 0;
				var detal = [];
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if (!r.get("HJJE")) {
						continue;
					}
					detal.push(r.data);
					if (r.get("CFLX") == 1) {
						xyf = (parseFloat(r.get("HJJE")) + parseFloat(xyf))
								.toFixed(2);
					} else if (r.get("CFLX") == 2) {
						zyf = (parseFloat(r.get("HJJE")) + parseFloat(zyf))
								.toFixed(2);
					} else if (r.get("CFLX") == 3) {
						cyf = (parseFloat(r.get("YPDJ") * r.get("CFTS")*r.get("YPSL")) + parseFloat(cyf))
								.toFixed(2);
					}
					// hjje = (parseFloat(r.get("HJJE")) + parseFloat(hjje))
					// .toFixed(2);
					// zfje = ((parseFloat(r.get("HJJE")) * parseFloat(r
					// .get("ZFBL"))) + parseFloat(zfje)).toFixed(2);
				}
				hjje = (parseFloat(xyf) + parseFloat(zyf) + parseFloat(cyf)).toFixed(2);
				document.getElementById("cfmx_tjxx_yfhj").innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "西药费："
						+ xyf
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "中药费："
						+ zyf
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "草药费："
						+ cyf
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ hjje + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;";
			},
			doSave : function() {
				if (this.saveing)
					return;
				this.saveing = true;
				var store = this.store;
				if (!this.opener.Brxx) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					this.saveing = false;
					return;
				}
				if (store.getCount() == 0) {
					Ext.Msg.alert("提示", "当前没有可以保存信息!");
					this.saveing = false;
					return;
				} else if (store.getCount() == 1) {
					var r = store.getAt(0);
					if (!r.data.YPXH) {
						Ext.Msg.alert("提示", "当前没有可以保存信息!");
						this.saveing = false;
						return;
					}
				}
				var n = store.getCount();
				var data = [];
				// var fyyfArry = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (!r.data["YPMC"]) {
						continue;
					}
					r.data.BRID = this.opener.Brxx.BRID
					r.data.BRXM = this.opener.Brxx.BRXM
					data.push(r.data);
					// if (r.get("msg")) {
					// MyMessageTip.msg("提示", r.get("msg"), true);
					// this.saveing = false;
					// this.grid.startEditing(i, 7);
					// return false;
					// }
					if (!this.checkRecordData(i, r.data)) {
						this.saveing = false;
						return false;
					}
					if (!r.get("YSDM")) {
						MyMessageTip.msg("提示",
								"开单医生不能为空！错误行 " + (i + 1) + " 。", true);
						this.saveing = false;
						this.grid.getSelectionModel().select(i, 4);
						this.onRowClick();
						this.opener.form.form.getForm().findField("YSDM")
								.focus();
						return false;
					}
					if (!r.get("KSDM")) {
						MyMessageTip.msg("提示",
								"开单科室不能为空！错误行 " + (i + 1) + " 。", true);
						this.saveing = false;
						this.grid.getSelectionModel().select(i, 4);
						this.onRowClick();
						this.opener.form.form.getForm().findField("KSDM")
								.focus();
						return false;
					}
					if (!r.get("YPDJ")) {
						MyMessageTip.msg("提示",
								"费用单价不能为0！错误行 " + (i + 1) + " 。", true);
						this.saveing = false;
						this.grid.startEditing(i, 6);
						return false;
					}
				}
				this.opener.panel.el.mask("正在保存数据...", "x-mask-loading")
				// this.MZXX.Brxx = this.opener.Brxx;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "savePhamaryClinicInfo",
							body : data,
							MZXX : this.MZXX
						});
				this.opener.panel.el.unmask();
				this.saveing = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				// this.setCFD(ret.json.djs);
				MyMessageTip.msg("提示", "处方信息保存成功!", true);
				this.doQx();
			},
			checkRecordData : function(i, data) {
				if (data["BZMC"]
						&& data["BZMC"].replace(/[^\x00-\xff]/g, "__").length > 100) {
					MyMessageTip.msg("提示", "自备药名称输入过长！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["BZXX"]
						&& data["BZXX"].replace(/[^\x00-\xff]/g, "__").length > 200) {
					MyMessageTip.msg("提示", "备注信息输入过长！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["YPSL"] <= 0) {
					MyMessageTip.msg("提示", "药品总量不能小于等于0！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["YYTS"] <= 0) {
					MyMessageTip.msg("提示", "用药天数不能小于等于0！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["MRCS"] < 0 || !data["YPYF"]) {
					MyMessageTip.msg("提示", "用药频次不能为空！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (!data["GYTJ"]) {
					MyMessageTip.msg("提示", "药品用法不能为空！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				// if (this.exContext.clinicType == 3) {// 草药
				// if (!data["YPZS"]) {
				// MyMessageTip.msg("提示", "服法不能为空！错误行 " + (i + 1) + " 。",
				// true);
				// return false;
				// }
				// }
				return true;
			},
			doQx : function() {
				// this.opener.doLoadMzxx(this.opener.Brxx.MZHM);
				this.opener.form.form.getForm().reset();
				this.opener.form.form.getForm().findField("JZKH")
						.setDisabled(false);
				this.store.removeAll();
				this.opener.afterOpen();
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if ((r.get("YPXH") == null || r.get("YPXH") == "" || (r
							.get("YPXH") == 0))
							&& (r.get("BZMC") == "" || r.get("BZMC") == undefined)) {
						store.remove(r);
					}
				}
			},
			// 判断是否有同组有医嘱
			hasEffectGroupRecord : function(record) {
				var uniqueId = (record.get("YPZH") > 0) ? "YPZH" : "uniqueId";
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get(uniqueId) && r.get("YPXH")
							&& r.get(uniqueId) == record.get(uniqueId)) {
						return true;
					}
				}
				return false;
			},
			renderCflx : function(v, params, data, row, col, store) {
				var CFLX = data.get("CFLX");
				var CFLX_show = "";
				if (CFLX == 1) {
					CFLX_show = "西"
				} else if (CFLX == 2) {
					CFLX_show = "中"
				} else if (CFLX == 3) {
					CFLX_show = "草"
				}
				if (row > 0
						&& store.getAt(row - 1).get("YPZH_show") == data
								.get("YPZH_show")) {
					return "";
				}
				return "<h2 style='color:red;font-size:14px;font-weight:bold'>"
						+ CFLX_show + "</h2>";
			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH_show") % 2 + 1;
				// var PSPB = data.get("PSPB");
				// var JBYWBZ = data.get("JBYWBZ");
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
				// var msg = data.get("msg");
				// if (msg) {
				// if (msg.error_kc) {
				// return "<img src='"
				// + ClassLoader.appRootOffsetPath
				// + "resources/phis/resources/images/i_error.gif' title='错误："
				// + msg.error_kc + "!'/>"
				// }
				// }
				// if (JBYWBZ == 0)
				// return "<h2
				// style='color:red;font-size:14px;font-weight:bold'>非</h2>";
				// return PSPB > 0 ? "<h2 style='color:red'>皮</h2>" : "";
			},
			totalYPSL : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:14px;color:black;">药品记录:&#160;'
								+ v + '</span>');
			},
			onRowClick : function(grid, index, e) {
				// 变更form中的值
				var r = this.getSelectedRecord();
				if (r == this.lastSelectRecord)
					return;
				this.lastSelectRecord = r;
				var form = this.opener.form.form.getForm();
				form.findField("KSDM").setValue(r.get("KSDM"));
				form.findField("YSDM").setValue(r.get("YSDM"));
				form.findField("CFLX").setValue(r.get("CFLX"));
				form.findField("CFHM").setValue(r.get("CFHM"));
				var cfts = form.findField("CFTS");
				if (r.get("CFLX") == "3") {
					cfts.show();
					cfts.setValue(parseInt(r.get("CFTS")));
				} else {
					cfts.hide();
					cfts.setValue(1);
				}
			},
			beforeSearchQuery : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return false;
				var s = this.remoteDic.getValue();
				this.remoteDicStore.baseParams.pharmacyId = this
						.getPharmacyIdByCFLX(r.get("CFLX"))
				if (s == null || s == "" || s.length == 0)
					return true;
				if (s.substr(0, 1) == '*') {
					return false;
				} else {
					if (r.get("BZMC") && r.get("BZMC").length > 0) {
						return false;
					}

					return true;
				}
			},
			getPharmacyIdByCFLX : function(type) {
				// if (this.exContext.systemParams.HQFYYF == '1') {
				// return this.form.getForm().findField("YFSB").getValue()
				// || -1;
				// }
				type = parseInt(type);
				var PharmacyId = null;
				switch (type) {
					case 1 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
						break;
					case 2 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_ZY;
						break;
					case 3 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_CY;
						break;
					default :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
				}
				return PharmacyId;
			},
			/** 处方复制功能* */
			doCopyClinic : function() {
				if (this.perscriptionCopyWin) {
					var module = this.midiModules['ClinicPrescriptionCopy'];
					module.initData(this.exContext.ids.brid,
							this.exContext.empiData.BRXZ,
							this.exContext.clinicType);
					module.reloadPrescription();
				} else {
					// 获取病人ID
					var brid = this.exContext.ids.brid;
					var module = this.createModule("ClinicPrescriptionCopy",
							this.rePrescriptionCopy);
					module.opener = this;
					module.initData(brid, this.exContext.empiData.BRXZ,
							this.exContext.clinicType);
					this.perscriptionCopyWin = module.getWin();
					// this.perscriptionCopyWin.add(module.initPanel());
					this.perscriptionCopyWin.setWidth(1200);
					this.perscriptionCopyWin.setHeight(700);
					this.perscriptionCopyWin.maximize();
				}
				this.perscriptionCopyWin.show();
			},
			/**
			 * 处方拷贝
			 */
			perscriptionCopy : function(json) {
				this.perscriptionCopyWin.hide();
				this.opener.perscriptionCopy(json, 'clinicPersonalSet');
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
									name : 'PSPB'
								},// 判断是否皮试药品
								{
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
									name : 'KCSL'
								}, {
									name : 'FYGB'
								}, {
									name : 'GBMC'
								}, {
									name : 'FYKS'
								}, {
									name : 'ISZT'
								}]);
			},
			setCFTS : function(cfts) {
				var r = this.getSelectedRecord();
				if (!r) {
					var n = this.store.getCount();
					if (n == 0)
						return;
					var rowItem = this.store.getAt(n - 1);
					if (rowItem.get("CFLX") == 3) {
						r = rowItem;
					} else {
						return;
					}
				}
				r.set("CFTS", cfts);
				if(r.get("YPDJ")&&r.get("YPSL")){
					r.set("HJJE",parseFloat(parseFloat(r.get("YPSL"))*parseFloat(r.get("YPDJ"))*parseFloat(r.get("CFTS"))).toFixed(2));
					this.checkInventory((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
				}
				var store = this.grid.getStore();
				var n = store.getCount();
				if (r.data.CF_NEW) {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("CFTS", cfts);
							if (rowItem.get("YPDJ") && rowItem.get("YPSL")) {
								rowItem.set("HJJE",
										parseFloat(parseFloat(rowItem
												.get("YPSL"))
												* parseFloat(rowItem
														.get("YPDJ"))
												* parseFloat(rowItem
														.get("CFTS")))
												.toFixed(2));
								this
										.checkInventory(
												(parseFloat(rowItem.get("YPSL")) * parseFloat(rowItem
														.get("CFTS")))
														.toFixed(2), rowItem);
							}
						}
					}
				} else {
					selectRow = this.store.indexOf(r);
					for (var i = selectRow; i >= 0; i--) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							rowItem.set("CFTS", cfts);
							if (rowItem.get("YPDJ") && rowItem.get("YPSL")) {
								rowItem.set("HJJE",
										parseFloat(parseFloat(rowItem
												.get("YPSL"))
												* parseFloat(rowItem
														.get("YPDJ"))
												* parseFloat(rowItem
														.get("CFTS")))
												.toFixed(2));
								this
										.checkInventory(
												(parseFloat(rowItem.get("YPSL")) * parseFloat(rowItem
														.get("CFTS")))
														.toFixed(2), rowItem);
							}
							break;
						} else {
							rowItem.set("CFTS", cfts);
							if (rowItem.get("YPDJ") && rowItem.get("YPSL")) {
								rowItem.set("HJJE",
										parseFloat(parseFloat(rowItem
												.get("YPSL"))
												* parseFloat(rowItem
														.get("YPDJ"))
												* parseFloat(rowItem
														.get("CFTS")))
												.toFixed(2));
								this
										.checkInventory(
												(parseFloat(rowItem.get("YPSL")) * parseFloat(rowItem
														.get("CFTS")))
														.toFixed(2), rowItem);
							}
						}
					}
					for (var i = selectRow + 1; i < n; i++) {
						var rowItem = store.getAt(i);
						if (rowItem.data.CF_NEW) {
							break;
						} else {
							rowItem.set("CFTS", cfts);
							if (rowItem.get("YPDJ") && rowItem.get("YPSL")) {
								rowItem.set("HJJE",
										parseFloat(parseFloat(rowItem
												.get("YPSL"))
												* parseFloat(rowItem
														.get("YPDJ"))
												* parseFloat(rowItem
														.get("CFTS")))
												.toFixed(2));
								this
										.checkInventory(
												(parseFloat(rowItem.get("YPSL")) * parseFloat(rowItem
														.get("CFTS")))
														.toFixed(2), rowItem);
							}
						}
					}
				}
				this.totalCountInfo();
			},
			hasModify : function() {
				if (this.store.getModifiedRecords().length > 0) {
					if (this.MZXX.removeData && this.MZXX.removeData.length > 0) {
						return true;
					}
					for (var i = 0; i < this.store.getCount(); i++) {
						if (this.store.getAt(i).get("YPXH")) {
							return true;
						}
					}
				} else if (this.MZXX.removeData
						&& this.MZXX.removeData.length > 0) {
					return true;
				}
				return false;
			},
			createButtons : function(level) {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				if (this.butRule) {
					var ac = util.Accredit
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								})
					}
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					level = level || 'one';
					if (action.properties) {
						action.properties.level = action.properties.level
								|| 'one';
					} else {
						action.properties = {};
						action.properties.level = 'one';
					}
					if (action.properties && action.properties.level != level) {
						continue;
					}
					// ** add by yzh **
					var btnFlag;
					if (action.notReadOnly)
						btnFlag = false
					else
						btnFlag = (this.exContext && this.exContext.readOnly) || false

					if (action.properties && action.properties.scale) {
						action.scale = action.properties.scale
					}
					var btn = {
						// accessKey : f1 + i,
						text : action.name,
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						disabled : btnFlag,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			}
		});