$package("phis.application.cic.script")

$import("phis.script.EditorList")

phis.application.cic.script.ClinicPrescriptionEntryAdditionalList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	cfg.selectOnFocus = true;
	cfg.sortable = false;
	//添加了phis.
	cfg.listServiceId = "phis.clinicManageService";
	cfg.remoteUrl = 'Clinic';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="120px">{FYMC}</td><td width="30px">{FYDW}</td>';
	cfg.minListWidth = 240;
	this.totalStore = {};
	phis.application.cic.script.ClinicPrescriptionEntryAdditionalList.superclass.constructor
			.apply(this, [cfg])
	this.requestData.serviceAction = "loadAddition";
	this.on("loadData", this.myLoadData, this);
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	this.on("afterCellEdit", this.afterGridEdit, this);
}
Ext.extend(phis.application.cic.script.ClinicPrescriptionEntryAdditionalList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				if (!this.exContext.ids.clinicId) {
					this.exContext.ids.clinicId = 0;
				}
				this.requestData.body = {
					JZXH : this.exContext.ids.clinicId
				}
				var grid = phis.application.cic.script.ClinicPrescriptionEntryAdditionalList.superclass.initPanel
						.call(this, sc)
				grid.onEditorKey = function(field, e) {
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						var r = this.store.getAt(cell[0]);
						if (cell[1] + 2 >= count && !this.editing) {
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
						if (ed.col == 3) {
							if (g.getStore().getAt(ed.row).get("YLDJ") > 0) {
								g.fireEvent("doNewColumn");
								return;
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
				return grid;
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
			},
			onStoreBeforeLoad : function(store, op) {
			},
			beforeGridEdit : function(it, record, field, value) {
				if (this.opener.panel.chargeStatus)
					return false;
				var mainRow = this.mainList.getSelectedRecord();
				var sfjg = mainRow.get('SFJG');
				if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
					return false;
				}
				if (it.id == "YLDJ" && value > 0) {
					return false;
				}
			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == 'YLSL' || it.id == 'YLDJ') {
					record.set("HJJE", record.get("YLDJ") * record.get("YLSL"));
				}
			},
			removeEmptyRecord : function() {
				var store = this.totalStore;
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if ((r.get("YLXH") == null || r.get("YLXH") == "" || r
							.get("YLXH") == 0)) {
						store.remove(r);
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'clinic',
							totalProperty : 'count'
						}, [{
									name : 'numKey'
								}, {
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
								}]);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (i != row) {
						if (r.get("YLXH") == record.get("FYXH")) {
							MyMessageTip.msg("提示", "\"" + record.get("FYMC")
											+ "\"已存在，请勿重复录入！", true);
							return;
						}
					}
				}
				obj.collapse();
				rowItem.set('YLXH', record.get("FYXH"));
				rowItem.set('FYGB', record.get("FYGB"));
				rowItem.set('FYDW', record.get("FYDW"));
				rowItem.set('YLDJ', record.get("FYDJ"));
				rowItem.set('HJJE', record.get("FYDJ") * rowItem.get("YLSL"));
				this.setZFBL(this.exContext.empiData.BRXZ, rowItem);
				obj.setValue(record.get("FYMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 3);
			},
			setZFBL : function(brxz, r) {
				var body = {};
				body["BRXZ"] = brxz;
				body["FYXH"] = r.get("YLXH");
				body["FYGB"] = r.get("FYGB");
				body["TYPE"] = 0;
				phis.script.rmi.jsonRequest({
							serviceId : "clinicDisposalEntryService",
							serviceAction : "getZFBL",
							body : body
						}, function(code, msg, json) {
							if (json.ZFBL.ZFBL != null || json.ZFBL.ZFBL != "") {
								r.set("ZFBL", json.ZFBL.ZFBL);
							} else {
								r.set("ZFBL", 1);
							}
						}, this);
			},
			doInsertAfter : function() {
				this.doInsert(0, 0, true);
			},
			doInsert : function(item, e, maxRow) {// 当前记录前插入一条记录
				var mainRow = this.mainList.getSelectedRecord();
				if (!mainRow || (!mainRow.get("YPXH") && !mainRow.get("YPZH"))) {
					MyMessageTip.msg("提示", "请先选择主医嘱后再添加附加项目!", true);
					return;
				}
				var sfjg = mainRow.get('SFJG');
				if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
					MyMessageTip.msg("提示", "当前选择的处方记录已经审核，不允许修改附加项目!", true);
					return;
				}
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YLXH") == null
							|| selectdRecord.get("YLXH") == "" || selectdRecord
							.get("YLXH") == 0)
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
					rowItem.set("YPZH", mainRow.get("YPZH"));
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					rowItem = storeData.itemAt(row);
					rowItem.set("YCSL", 1);
					rowItem.set("YPZH", upRowItem.get("YPZH"));
				}
				this.grid.getView().refresh()// 刷新行号
				// 设置公共属性
				rowItem.set("uniqueId", mainRow.get("uniqueId"));
				rowItem.set("JGID", this.mainApp['phisApp'].deptId);
				rowItem.set("ZYH", this.mainList.initDataId);
				if (maxRow) {
					this.grid.startEditing(this.store.getCount() - 1, 1);
				} else {
					this.grid.startEditing(row, 1);
				}
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);

					if ((r.get("YLXH") == null || r.get("YLXH") == "" || r
							.get("YLXH") == 0)) {
						store.remove(r);
					}
				}
			},
			/**
			 * 重写doRemove，当grid中的数据未保存在数据库时，直接从grid中删除，若删除的数据 已保存，则发起请求删除数据库中数据
			 */
			doRemove : function() {
				var mainRow = this.mainList.getSelectedRecord();
				var sfjg = mainRow.get('SFJG');
				if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
					MyMessageTip.msg("提示", "当前选择的处方记录已经审核，不允许修改附加项目!", true);
					return;
				}
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("SBXH") == null && !r.get("YLXH")) {
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
								title : '确认删除附加项目[' + r.data.FYMC + ']',
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
										if (r.data.SBXH) {
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
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
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
				this.store.rejectChanges();
			}
		});
