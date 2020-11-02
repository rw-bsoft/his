$package("phis.application.cfg.script")
$import("phis.script.EditorList")

phis.application.cfg.script.AdvicePersonalComboNameDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	this.entryName = "phis.application.cfg.schemas.BQ_ZT02";
	// this.schema = "BQ_ZT02";
	cfg.remoteUrl = 'MedicineSetBQ';
	cfg.remoteTpl = this.getRemoteTpl();
	cfg.minListWidth = 500;
	phis.application.cfg.script.AdvicePersonalComboNameDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.afterGridEdit, this)
	this.ZTLB = 1;
}
Ext.extend(phis.application.cfg.script.AdvicePersonalComboNameDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.AdvicePersonalComboNameDetailList.superclass.initPanel
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
				this.checkBox = new Ext.form.Checkbox({
							id : "autoNewGroup",
							boxLabel : "自动新组",
							hidden : true
						});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(this.checkBox, tbar);
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:blue'>明细条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
				var params=this.loadSystemParams({
									  "privates" : ['QYJCBZ']
								 })
				this.QYJYBZ=params.QYJYBZ;
			},
			doInsert : function(item, e, newGroup) {// 当前记录后插入一条记录
				debugger;
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
							|| selectdRecord.get("XMBH") == ""
							|| selectdRecord.get("XMBH") == 0)
							&& (this.opener.ztlbValue!=5 
							|| (this.opener.ztlbValue==5 && selectdRecord.get("XMMC")==null))) {//modify by lizhi 文字组套不需要判断
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
				if (maxIndex > 0 && this.opener.ztlbValue!=5) {//modify by lizhi 文字组套不需要判断
					if (!storeData.itemAt(maxIndex - 1).data.SYPC) {
						MyMessageTip.msg("提示", '使用频次不能为空!', true);
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
				return '<tpl if="YPLX !== 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YPDW}</td></tpl>'
						+ '<tpl if="YPLX === 0"><td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="200px">{FYMC}</td><td width="85px">{FYDW}</td><td width="20px">{FYDJ}</td><td width="20px">{YYZBM}</td></tpl>';
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
								}, {
									name : 'JCDL'
								}, {
									name : 'YYZBM'
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
					this.grid.startEditing(row, 3);
				} else if(record.get("YPXH")!=""){
					this.grid.startEditing(row, 4);
				}else {
					this.grid.startEditing(row, 6);
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
							if(!this.QYJCBZ){
							var params=this.loadSystemParams({
									  "privates" : ['QYJCBZ']
								 })
							this.QYJCBZ=params.QYJCBZ;
							}
							if(this.QYJCBZ=="1"){
							if(r.get("XMLX")!=data.XMLX){
							MyMessageTip.msg("提示", "同组项目不一致，请输入相同的检查项目！", true);
							return false;
							}else if(r.get("XMLX")=="11"){//11是pacs
							if(r.get("JCDL")!=data.JCDL){
							MyMessageTip.msg("提示", "同组检查大类不一致，请输入相同的检查项目！", true);
							return false;
							}
							}
							}
						}
					}
					rowItem.set("XMBH", data.FYXH);
					rowItem.set("YPZH", rowItem.get("YPZH"));
					rowItem.set("XMMC", data.FYMC);
					rowItem.set("YPLX", 0);
					rowItem.set("XMLX", data.XMLX);
					rowItem.set("JCDL", data.JCDL);
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
					if (r.get("YPLX") === 0) {
						r.set("GYTJ", "27");
					}
					var items = this.schema.items;
					var msg = "保存失败";
					if (r.get("JLBH") == 0)
						r.set("JLBH", null);
					if(this.opener.ztlbValue==5){//add by lizhi 2017-12-01增加文字组套
						for (var j = 0; j < items.length; j++) {
							var it = items[j]
							if (it.id == 'ZTBH' || it.id == 'JLBH')
								continue;
							if (r.get("MRCS") == null) {
								r.data['MRCS'] = it.defaultValue;
							}
							if (this.opener.comboNameList.ZTLB<5 && (r.get(it.id) == null || r.get(it.id) == "")) {
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
					}else if (r.get("XMBH") == null) {
						MyMessageTip.msg("提示", "药品名称不能手工输入！错误行 " + (i + 1)
										+ " 。", true);
						return;
					}else{
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
						if (!r.get("GYTJ") && this.opener.comboNameList.ZTLB<5) {
							MyMessageTip.msg("提示", "给药途径不能为空！错误行 " + (i + 1)
											+ " 。", true);
							return;
						}
						for (var j = 0; j < items.length; j++) {
							var it = items[j]
							if (it.id == 'ZTBH' || it.id == 'JLBH')
								continue;
							if (r.get("MRCS") == null) {
								r.data['MRCS'] = it.defaultValue;
							}
							if (this.opener.comboNameList.ZTLB<5 && (r.get(it.id) == null || r.get(it.id) == "")) {
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
							method : "execute",
							schema : "phis.application.cfg.schemas.BQ_ZT02",
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
				Ext.Msg.confirm("请确认", "确认删除名称为【" + r.get("XMMC") + "】的组套明细吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.serviceActionDel,
											method : "execute",
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
			afterGridEdit : function(it, record, field, v) {
				var sign = 1;
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
				if (it.id == "XMMC") {//add by lizhi 增加文字医嘱
					if (v.substr(0, 1) == '*') {
						var xmmc = v.substr(0, 1) == '*' ? v.substr(1) : v;
						record.set("XMMC", xmmc)
						record.modified[it.id] = xmmc;
					}
				}
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (this.opener.comboNameList.ZTLB<5 && (r.get("XMBH") == null || r.get("XMBH") == ""
							|| r.get("XMBH") == 0)) {
						store.remove(r);
					}else if(this.opener.comboNameList.ZTLB==5 && (r.get("XMMC") == null || r.get("XMMC") == "")){
						store.remove(r);
					}
				}
			},
			createRemoteDicField : function(dic) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrl
					// ,pageSize : this.pageSize || 25,
					// pageNo : 1
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonData
						});
				var mdsstore = new Ext.data.Store({
							proxy : proxy,
							reader : mds_reader
						});
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)

				this.remoteDicStore = mdsstore;
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>');
				var _ctx = this;
				var remoteField = new Ext.form.ComboBox({
							// id : "YPMC",
							width : 280,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : this.minListWidth || 280,
							tpl : resultTpl,
							minChars : 2,
							enableKeyEvents : true,
							lazyInit : false,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								// onSelect to do
								this.bySelect = true;
								_ctx.setBackInfo(this, record);
								// this.hasFocus = false;
							}
						});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keyup", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								var searchTypeValue = _ctx.cookie
										.getCookie(_ctx.mainApp.uid
												+ "_searchType");
								if (searchTypeValue != 'BHDM') {
									if (obj.isExpanded()) {
										if (key == 48 || key == 96)
											key = key + 10;
										key = key < 59 ? key - 49 : key - 97;
										var record = this.getStore().getAt(key);
										obj.bySelect = true;
										_ctx.setBackInfo(obj, record);
									}
								}
							}
							// 支持翻页
							if (key == 37) {
								obj.pageTb.movePrevious();
							} else if (key == 39) {
								obj.pageTb.moveNext();
							}
							// 删除事件 8
							if (key == 8) {
								if (obj.getValue().trim().length == 0) {
									if (obj.isExpanded()) {
										obj.collapse();
									}
								}
							}
						})
				if (remoteField.store) {
					remoteField.store.load = function(options) {
						Ext.apply(_ctx.comboJsonData, options.params);
						Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
						options = Ext.apply({}, options);
						this.storeOptions(options);
						if (this.sortInfo && this.remoteSort) {
							var pn = this.paramNames;
							options.params = Ext.apply({}, options.params);
							options.params[pn.sort] = this.sortInfo.field;
							options.params[pn.dir] = this.sortInfo.direction;
						}
						try {
							return this.execute('read', null, options); // <--
							// null
							// represents
							// rs. No rs for
							// load actions.
						} catch (e) {
							this.handleException(e);
							return false;
						}
					}
				}
				remoteField.isSearchField = true;
				remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							// 设置下拉框的分页信息
							// remoteField.pageTb.changePage(0);
							return this.beforeSearchQuery();
						}, this);
				// remoteField.store.on("load",function(store){
				// if(store.getCount() == 1) {
				// this.setBackInfo(remoteField,store.getAt(0));
				// }
				// },this);
				remoteField.onMouseDown = function(e) {
					if (e.button == 2) { // 右键
						var ed = _ctx.grid.activeEditor
								|| _ctx.grid.lastActiveEditor;
						e.stopEvent();
						_ctx.onContextMenu(_ctx.grid, ed.row, e);
					}
				}
				this.remoteDic = remoteField;
				return remoteField
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
				if (s.substr(0, 1) == '*') {
					return false;
				}
			}
		})