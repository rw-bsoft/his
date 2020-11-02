$package("phis.application.fsb.script")
$import("phis.script.EditorList")

phis.application.fsb.script.FamilySickBedClinicPlanDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.remoteUrl = 'MedicineClinicForFSB';
	cfg.remoteTpl = this.getRemoteTpl();
	cfg.minListWidth = 500;
	this.serviceId = "familySickBedManageService"
	phis.application.fsb.script.FamilySickBedClinicPlanDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	this.on("afterCellEdit", this.afterGridEdit, this)
}
Ext.extend(phis.application.fsb.script.FamilySickBedClinicPlanDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.fsb.script.FamilySickBedClinicPlanDetailList.superclass.initPanel
						.call(this, sc);
				var sm = grid.getSelectionModel();
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
							if (ed.fieldName == "XMMC"
									&& g.getStore().getAt(ed.row).get("XMLX") == 3) {
								ed.col = g.getColumnModel()
										.getIndexById("BZXX")
										- 1;
							}
							if (ed.fieldName == "SYPC") {
								if (g.getStore().getAt(ed.row).get("XMLX") == 2) {
									ed.col = g.getColumnModel()
											.getIndexById("BZXX")
											- 1;
								}
							}
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

				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(tbar);
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:blue'>明细条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			jldwRender : function(value, metaData, r, row, col, store) {
				if (r.get("XMLX") != 1)
					return null;
				return value;
			},
			doInsert : function(item, e, newGroup) {// 当前记录后插入一条记录
				if (!this.ZTBH) {
					// alert(1);
					// MyMessageTip.msg("提示", "请先维护组套名称 ", true);
					return;
				}
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("XMBH") == null
							|| selectdRecord.get("XMBH") == "" || selectdRecord
							.get("XMBH") == 0)
							&& selectdRecord.get("XMLX") != 3) {
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
				/**
				 * modified by chzhxiang 2013.10.11 张伟要求 if (store.getCount() >
				 * 29) { MyMessageTip.msg("提示,纸张容量限制", '每张处方不能超过30条药品!', true,
				 * 5); return; }
				 */
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
				// if (row > 0) {
				// if (!store.data.itemAt(row - 1).data.SYPC) {
				// MyMessageTip.msg("提示", '使用频次不能为空!', true);
				// return;
				// }
				// if (r.get("YPLX") != 0) {
				// if (!store.data.itemAt(row - 1).data.GYTJ) {
				// MyMessageTip.msg("提示,", '用法不能为空!', true);
				// return;
				// }
				// }
				// }
				store.insert(row, [r])
				this.grid.getView().refresh()// 刷新行号
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YYTS", 1);
					rowItem.set("YPZH", 1);
					rowItem.set("XMLX", -1);
					rowItem.set("KSSJ", Date.getServerDate());
					rowItem
							.set("JSSJ", Date.parseDate(rowItem.get("KSSJ"),
											'Y-m-d').add(Date.MONTH, 1)
											.format('Y-m-d'));
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
					rowItem.set("XMLX", upRowItem.get("XMLX"));
					rowItem.set("YCJL", upRowItem.get("YCJL"));
					rowItem.set("KSSJ", upRowItem.get("KSSJ"));
					rowItem.set("JSSJ", upRowItem.get("JSSJ"));
				}
				this.grid.startEditing(row, 2);
				var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "明细条数：" + n;
			},
			doInsertAfter : function(item, e, newGroup) {// 当前记录后插入一条记录
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();
				/**
				 * modified by chzhxiang 2013.10.11 张伟要求 if (maxIndex > 29) {
				 * MyMessageTip.msg("提示,纸张容量限制", '每张处方不能超过30条药品!', true, 5);
				 * return; }
				 */
				this.doCreate();
				if (maxIndex == 0 || newGroup) {// 处方的第一条记录或者自动新组
					var ypzh = 1;
					var upRowItem = storeData.itemAt(maxIndex - 1);
					if (maxIndex > 0) {
						ypzh = upRowItem.get("YPZH") + 1;
					}
					var rowItem = storeData.itemAt(maxIndex);
					rowItem.set("JLBH", null);
					rowItem.set("YYTS", 1);
					rowItem.set("YPZH", ypzh);
					rowItem.set("XMLX", -1);
					rowItem.set("KSSJ", Date.getServerDate());
					rowItem
							.set("JSSJ", Date.parseDate(rowItem.get("KSSJ"),
											'Y-m-d').add(Date.MONTH, 1)
											.format('Y-m-d'));
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
					rowItem.set("YCJL", upRowItem.get("YCJL"));
					rowItem.set("XMLX", upRowItem.get("XMLX"));
					rowItem.set("KSSJ", upRowItem.get("KSSJ"));
					rowItem.set("JSSJ", upRowItem.get("JSSJ"));
				}
				this.grid.startEditing(maxIndex, 2);
				var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "明细条数：" + n;
			},
			doNewGroup : function(item, e) {
				if (!this.ZTBH) {
					MyMessageTip.msg("提示", "请先维护组套名称 ", true);
					return;
				}
				this.doInsertAfter(item, e, true);
			},
			doNewClinic : function(item, e) {
				this.grid.store.removeAll()
				this.fireEvent("doNew");
			},
			getRemoteTpl : function() {
				return '<tpl if="YPLX !== 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YPDW}</tpl>'
						+ '<tpl if="YPLX === 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="200px">{FYMC}</td><td width="85px">{FYDW}</td></tpl>';
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
									name : 'YFDW'
								}, {
									name : 'PSPB'
								}, {
									name : 'JLDW'
								}, {
									name : 'YPJL'
								}, {
									name : 'YCJL'
								}, {
									name : 'GYFF'
								}, {
									name : 'GYFF_text'

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
									name : 'YPLX'
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
				if (!this.setRecordIntoList(record.data, rowItem, row)) {
					return;
				}
				obj.collapse();
				if (rowItem.get("GYTJ") == null || rowItem.get("GYTJ") == ""
						|| rowItem.get("GYTJ") == 0) {
					rowItem.set('GYTJ', record.get("GYFF"));
					rowItem.set('GYTJ_text', record.get("GYFF_text"));
				}
				var yzmc = record.get("YPLX") === 0
						? record.get("FYMC")
						: record.get("YPMC");
				obj.setValue(yzmc);
				rowItem.modified["XMMC"] = yzmc;
				obj.triggerBlur();
				if (record.get("YPLX") === 0) {
					this.grid.startEditing(row, 5);
				} else {
					this.grid.startEditing(row, 3);
				}
			},
			setRecordIntoList : function(data, rowItem, curRow) {
				var store = this.grid.getStore();
				var n = store.getCount()
				if (data.YPLX === 0) {// 项目
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (i != curRow && r.get("YPZH") == rowItem.get("YPZH")) {
							if (r.get("XMBH") == data.FYXH) {
								MyMessageTip.msg("提示", "\"" + data.FYMC
												+ "\"在这组中已存在，请进行修改!", true);
								return false;
							}
						}
					}
					rowItem.set("XMLX", 2);
					rowItem.set("XMBH", data.FYXH);
					rowItem.set("YPZH", rowItem.get("YPZH"));
					rowItem.set("XMMC", data.FYMC);
					rowItem.set("YPLX", 0);
					rowItem.set('YCJL', 0);
				} else {
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (i != curRow && r.get("YPZH") == rowItem.get("YPZH")) {
							if (r.get("XMBH") == data.YPXH) {
								MyMessageTip.msg("提示", "\"" + data.YPMC
												+ "\"在这组中已存在，请进行修改!", true);
								return false;
							}
						}
					}
					rowItem.set("XMLX", 1);
					rowItem.set('XMMC', data.YPMC);
					rowItem.set('YCJL', data.YCJL);
					rowItem.set('XMBH', data.YPXH);
					rowItem.set("JLDW", data.JLDW);
				}
				return true;
			},
			doSave : function(item, e) {
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var n = store.getCount();
				var data = []
				if (!this.ZTBH) {
					MyMessageTip.msg("提示", "请先维护组套名称 ", true);
					return;
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (!this.ZTBH) {
						MyMessageTip.msg("提示", "请先维护组套名称 ", true);
						return;
					}
					// alert(Ext.encode(r.data));
					r.set('ZTBH', this.ZTBH);
					var items = this.schema.items;
					var msg = "保存失败";
					if (r.get("JLBH") == 0)
						r.set("JLBH", null);
					if (r.get("XMBH") == null && r.get("XMLX") != 3) {
						MyMessageTip.msg("提示", "药品名称不能手工输入！错误行 " + (i + 1)
										+ " 。", true);
						return;
					} else {
						if (r.get("XMLX") == 1 && r.get("YYTS") <= 0) {
							MyMessageTip.msg("提示", "用药天数不能小于等于0！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						if (!r.get("SYPC") && r.get("XMLX") != 3) {
							MyMessageTip.msg("提示", "使用频次不能为空！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						if (r.get("XMLX") == 1 && !r.get("GYTJ")) {
							MyMessageTip.msg("提示", "给药途径不能为空！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						if(r.get("KSSJ") > r.get("JSSJ")) {
							MyMessageTip.msg("提示", "开始日期不能大于结束日期！错误行 " + (i + 1)
											+ " 。", true);
							return;
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
							serviceAction : "savePrescriptionDetails",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "诊疗计划明细保存成功", true);
							this.loadData();
						}, this)
				this.store.commitChanges();
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
				Ext.Msg.confirm("请确认", "确认删除名称为【" + r.get("XMMC") + "】的计划明细 吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
									serviceId : this.serviceId,
									serviceAction : "removePrescriptionDetails",
									method : "execute",
									schemaList : "JC_ZL_ZT01",
									schemaDetailsList : "JC_ZL_ZT02",
									pkey : r.get("JLBH"),
									body : date
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
			/*******************************************************************
			 * // 获取保存数据的请求数据
			 * 
			 * @return {}
			 */
			/*
			 * /
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
					document.getElementById("totcount").innerHTML = "明细条数：0";
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
				document.getElementById("totcount").innerHTML = "明细条数：" + n;

			},
			beforeGridEdit : function(it, record, field, value) {
				// 行为医嘱
				if (it.id == "YCJL" || it.id == "SYPC" || it.id == "GYTJ") {
					if (record.get("XMLX") == 3) {
						return false;
					}
					if (!record.get("XMBH")) {
						return false;
					}
				}
				return true;
			},
			afterGridEdit : function(it, record, field, v) {
				var sign = 1;
				if (it.id == "XMMC") {
					if (v.substr(0, 1) == '*' || record.get("XMLX") == 3) {
						var yzmc = v.substr(0, 1) == '*' ? v.substr(1) : v;
						record.set("XMMC", yzmc)
						record.set("XMLX", 3);
						record.set("YCJL", 0);
						record.modified[it.id] = yzmc;
					}
				}
				if (it.id == "SYPC") {
					field.getStore().each(function(r) {
								if (r.data.key == v) {
									if (record.get("MRCS") != r.data.MRCS) {

										record.set("MRCS", r.data.MRCS);
									} else {
										sign = 0;// 频次没有改变
									}
								}
							}, this);
				}
				if (it.id == 'SYPC' || it.id == 'GYTJ' || it.id == "KSSJ"
						|| it.id == "JSSJ") {
					var store = this.store;
					store.each(function(r) {
								if (r.get('YPZH') == record.get('YPZH')) {
									if (r.get("XMBH") != record.get("XMBH")) {
										r.set(it.id, v);
										if (record.get(it.id + '_text')) {
											r.set(it.id + '_text', record
															.get(it.id
																	+ '_text'));
										}
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

						if (r.get("XMLX") == 3 && r.get("XMMC")) {
							continue;
						}
						store.remove(r);
					}
				}
			},
			beforeSearchQuery : function() {
				// 判断当前行是否满足同组输入
				var r = this.getSelectedRecord();
				if (!r)
					return false;
				this.remoteDic.lastQuery = "";
				if (!this.remoteDicStore.baseParams) {
					this.remoteDicStore.baseParams = {};
				}
				var s = this.remoteDic.getValue();
				if (s == null || s == "" || s.length == 0)
					return true;
				this.remoteDicStore.baseParams.TYPE = 1;// 药品
				if (s.substr(0, 1) == '*') {
					if (r.get("XMLX") != -1 && r.get("XMLX") != 3) {
						MyMessageTip.msg("提示", "当前组不能录入行为医嘱!", true);
						this.remoteDic.setValue("");
					}
					return false;
				} else if (s.substr(0, 1) == '.') {
					if (r.get("XMLX") != -1 && r.get("XMLX") != 2) {
						// 判断是否有同组信息
						var count = 0;
						this.store.each(function(rd) {
									if (rd.get("YPZH")
											&& rd.get("YPZH") == r.get("YPZH")) {
										count++;
									}
								});
						if (count > 1) {
							MyMessageTip.msg("提示", "当前组不能录入费用医嘱!", true);
							this.remoteDic.setValue("");
							return false;
						}
					}
					this.remoteDicStore.baseParams.TYPE = 0;// 费用
					return s.length >= 2;
				} else {
					if (r.get("XMLX") == 3) {
						return false;
					}
					if (r.get("XMLX") == 2) {
						// 自动转为费用医嘱查询
						this.remoteDicStore.baseParams.TYPE = 0;// 费用
					}
					return true;
				}
			},
			getCM : function(items) {
				var cm = []
				var fm = Ext.form
				var ac = util.Accredit;
				var _this = this;
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2) || it.noList
							|| it.hidden || !ac.canRead(it.acValue)) {
						continue
					}
					var width = parseInt(it.width
							|| (it.length < 80 ? 80 : it.length) || 80)
					var c = {
						id : it.id,
						header : it.alias,
						width : width,
						sortable : false,
						dataIndex : it.id,
						schemaItem : it
					}
					/** ******************** */
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					// add by yangl,modify simple code Generation methods
					if (it.codeType) {
						if (!this.CodeFieldSet)
							this.CodeFieldSet = [];
						this.CodeFieldSet.push([it.target, it.codeType, it.id]);
					}
					var editable = true;

					if ((it.pkey && it.generator == 'auto') || it.fixed) {
						editable = false
					}
					if (it.evalOnServer && ac.canRead(it.acValue)) {
						editable = false
					}
					var notNull = !(it['not-null'] == 'true')

					var editor = null;
					var dic = it.dic
					if (it.properties && it.properties.mode == "remote") {
						// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
						editor = this.createRemoteDicField(dic, it);
					} else if (dic) {
						dic.defaultValue = it.defaultValue
						dic.width = width
						dic.listWidth = it.listWidth;
						if (dic.render == "Radio" || dic.render == "Checkbox") {
							dic.render = ""
						}
						if (dic.fields) {
							if (typeof(dic.fields) == 'string') {
								var fieldsArray = dic.fields.split(",")
								dic.fields = fieldsArray;
							}
						}
						if (editable) {
							editor = this.createDicField(dic)
							if (it['not-null']) {
								editor.notNull = true;
							}
							editor.isDic = true
							if (Ext.isIE) {
								editor.on("select", function(f) {
											var sm = this.grid
													.getSelectionModel();
											var cell = sm.getSelectedCell()
											this.grid.walkCells(cell[0],
													cell[1] + 1, 1,
													sm.acceptsNav, sm);
										}, this);
							}
						}
						var _ctx = this
						c.isDic = true
						c.renderer = function(v, params, record, r, c, store) {
							var cm = _ctx.grid.getColumnModel()
							var f = cm.getDataIndex(c)
							return record.get(f + "_text")
						}
					} else {
						if (!editable) {
							if (it.type != "string" && it.type != "text"
									&& it.type != "date") {
								c.align = "right";
								c.css = "color:#00AA00;font-weight:bold;"
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								if (!c.renderer) {
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							cm.push(c);
							continue;
						}
						editor = new fm.TextField({
									allowBlank : notNull
								});
						var fm = Ext.form;
						switch (it.type) {
							case 'string' :
							case 'text' :
								var cfg = {
									allowBlank : notNull,
									maxLength : it.length
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								if (it.inputType) {
									cfg.inputType = it.inputType
								}
								editor = new fm.TextField(cfg)
								break;
							case 'date' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期",
									format : 'Y-m-d'
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.DateField(cfg)
								break;
							case 'datetime' :
							case 'datetimefield' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期"
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new phis.script.widgets.DateTimeField(cfg)
								break;
							case 'double' :
							case 'bigDecimal' :
							case 'int' :
								if (!it.dic) {
									c.css = "color:#00AA00;font-weight:bold;"
									c.align = "right"
									if (it.type == 'double'
											|| it.type == 'bigDecimal') {
										c.precision = it.precision;
										c.nullToValue = it.nullToValue;
										c.itemId = it.id;
										c.renderer = function(value, metaData,
												r, row, col, store) {
											if (r.get("JFBZ") == 3) {
												return "";
											}
											if (this.itemId == "YCJL") {
												if (value == 0 || value == "") {
													return "";
												}
											}
											if (value == null
													&& this.nullToValue) {
												value = parseFloat(this.nullToValue)
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												try {
													r
															.set(this.itemId,
																	retValue);
												} catch (e) {
													// 防止新增行报错
												}
												return retValue;
											}
											if (value != null) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												if (this.itemId == "YCSL") {
													if (r.get("SIGN") == -1) {
														return "<font color='red'>"
																+ retValue
																+ "</font>";
													}
												}
												return retValue;
											}
										}
									}
								}
								var cfg = {}
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.min) {
									cfg.minValue = it.min;
								} else {
									cfg.minValue = 0;
								}
								if (it.max) {
									cfg.maxValue = it.max;
								}
								cfg.allowBlank = notNull
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.NumberField(cfg)
								if (it.properties.xtype == "checkBox") {
									c.xtype = 'checkcolumn';
									editor = new Ext.ux.grid.CheckColumn();
									// editor.on("beforeedit",this.beforeCheckEdit,this)
								}
								break;
						}
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			}
		})