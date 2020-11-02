$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseOtherCheckInDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td><td width="70px">{JHJG}</td>';
	cfg.minListWidth = 600;
	cfg.queryParams = {
		"tag" : "cszc"
	};
	cfg.selectOnFocus = true;
	cfg.modal = this.modal = true;
	cfg.isRead = 0;// 提交和查看的时候只读
	phis.application.sto.script.StorehouseOtherCheckInDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseOtherCheckInDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sto.script.StorehouseOtherCheckInDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						var storeCount = _ctr.store.getCount();
						if (cell[1] + 2 > count) {
							if (cell[0] + 1 == storeCount) {
								if (_ctr.store.getAt(cell[0]).get("YPXH") == null
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == ""
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == 0
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == undefined) {
									g.startEditing(cell[0], 1);
								} else {
									_ctr.doCreate();
								}
								return;
							}
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
						var cell = sm.getSelectedCell();
						var storeCount = _ctr.store.getCount();
						if (cell[1] > 9) {
							if (cell[0] + 1 == storeCount) {
								if (_ctr.store.getAt(cell[0]).get("YPXH") == null
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == ""
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == 0
										|| _ctr.store.getAt(cell[0])
												.get("YPXH") == undefined) {
									g.startEditing(cell[0], 1);
								} else {
									_ctr.doCreate();
								}
								return;
							}
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
									name : 'numKey'
								}, {
									name : 'YPCD'
								}, {
									name : 'YPXH'
								}, {
									name : 'CDMC'
								}, {
									name : 'LSJG'
								}, {
									name : 'JHJG'
								}, {
									name : 'YPMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YFDW'
								}, {
									name : 'YFBZ'
								}, {
									name : 'PFJG'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var ypcd = record.get("YPCD");
				var ypxh = record.get("YPXH");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPCD") == ypcd
							&& griddata.itemAt(i).get("YPXH") == ypxh
							&& i != row
							&& (griddata.itemAt(i).get("YPXQ") == ""
									|| griddata.itemAt(i).get("YPXQ") == null || griddata
									.itemAt(i).get("YPXQ") == undefined)
							&& (griddata.itemAt(i).get("YPPH") == ""
									|| griddata.itemAt(i).get("YPPH") == null || griddata
									.itemAt(i).get("YPPH") == undefined)) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品", true);
						return;
					}
				}
				obj.collapse();
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('JHJG', record.get("JHJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YPDW', record.get("YFDW"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('PFJG', record.get("PFJG"));
				rowItem.set('RKSL', 0);
				if (rowItem.get("RKSL") != null && rowItem.get("RKSL") != ""
						&& rowItem.get("RKSL") != 0) {
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("RKSL")).toFixed(4));
					rowItem.set('JHHJ', (record.get("JHJG") * rowItem
									.get("RKSL")).toFixed(4));
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				this.grid.startEditing(row, 6);
			},
			onReady : function() {
				phis.application.sto.script.StorehouseOtherCheckInDetailList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
			},// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "YPMC") {
					if (v == null || v == "" || v == 0) {
						record.set('YPCD', "");
						record.set('YPXH', "");
						record.set('CDMC', "");
						record.set('LSJG', 0);
						record.set('JHJG', 0);
						record.set('YPGG', "");
						record.set('YPDW', "");
						record.set('PFJG', 0);
						record.set('LSJE', 0);
						record.set('JHHJ', 0);
					}
				}
				if (it.id == "YPPH") {
					if (v.length > 20) {
						MyMessageTip.msg("提示", "批号长度不能超过20位", true);
						v = v.substring(0, 10);
						record.set("YPPH", v);
					}
				}
				if (it.id == "RKSL") {

					// if(!record.get("YPXH")){return;}
					var pfje = 0;
					var jhje = 0;
					var lsje = 0;
					if (((v != null && v != "") || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
						pfje = (v * record.get("PFJG")).toFixed(4);
						jhje = (v * record.get("JHJG")).toFixed(4);
						lsje = (v * record.get("LSJG")).toFixed(4);
					}
					record.set("JHHJ", jhje);
					record.set("PFJE", pfje);
					record.set("LSJE", lsje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					this.doJshj();
					if (v < 0) {
						if (record.get("YPXH") == undefined
								|| record.get("YPXH") == "") {
							MyMessageTip.msg("提示", "没选药品入库数量不能为负!", true);
							record.set('RKSL', 0);
							return;
						} else {
							this.checkRecord = record;
							this.list = this.createModule("list", this.refList);
							this.list.on("checkData", this.onCheckData, this);
							this.list.on("hide", this.onClose, this);
							var m = this.list.initPanel();
							var win = this.list.getWin();
							win.add(m);
							win.show()
							win.center()
							if (!win.hidden) {
								this.list.requestData.cnd = [
										'and',
										['eq', ['$', 'JGID'],
												['$', '%user.manageUnit.id']],
										[
												'and',
												[
														'eq',
														['$', 'YPXH'],
														[
																'l',
																record
																		.get("YPXH")]],
												[
														'eq',
														['$', 'YPCD'],
														[
																'l',
																record
																		.get("YPCD")]]]];
								this.list.loadData();
								return;
							}
						}

					}
				}
				if (it.id == "YPXQ") {
					var today = new Date().format('Ymd');
					//这样转成数字比较是因为ie8不支持date的gettime
					var d=(v.substring(0,10)).split('-');
					var date = d[0]+d[1]+d[2];
					if (today > date) {
						Ext.Msg.alert("提示", "药品已过期，请确认效期是否填写正确");
					}else if(today == date){
						Ext.Msg.alert("提示", "药品即将过期，请确认效期是否填写正确");
					}
				}
			},
			// 新增
			doNew : function() {
				this.clear();
				this.doInit();
				this.setButtonsState(['create', 'remove'], true);
				// document.getElementById("YK_RK02_QT").innerHTML = "合计 零售金额:0
				// 进货金额:0";
				this.label.setText("合计   零售金额:0 进货金额:0");
			},
			doInit : function() {
				this.editRecords = [];
			},

			doCreate : function(item, e) {
				phis.application.sto.script.StorehouseOtherCheckInDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			onRenderer_two : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(2);
				}
				return value;
			},
			onRenderer_four : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(4);
				}
				return value;
			},
			// 删行时重新计算金额
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.editRecords.push(r.data);
				this.store.remove(r);
				this.doJshj();
				var count = this.store.getCount();
				var tag = true;
				for (var i = 0; i < count; i++) {
					if (this.store.getAt(i).data.YPXH == null
							|| this.store.getAt(i).data.YPXH == ""
							|| this.store.getAt(i).data.YPXH == undefined) {
						tag = false;
						break;
					}
				}
				if (tag) {
					this.doCreate();
				}
				// 移除之后焦点定位
				count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
				// this.calculatEmount();
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				this.label = new Ext.form.Label({
							text : "合计   零售金额:0  进货金额:0"
						})
				cfg.bbar = [];
				cfg.bbar.push(this.label);
			},
			// 数据加载时计算总金额
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				if (this.isRead == 0) {
					this.setButtonsState(['create', 'remove'], true);
				} else {
					this.setButtonsState(['create', 'remove'], false);
				}
				var _ctr = this;
				var whatsthetime = function() {
					_ctr.doJshj();
				}
				whatsthetime.defer(500);
			},
			doJshj : function() {
				var n = this.store.getCount();
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if (r.get("YPXH") == null || r.get("YPXH") == ""
							|| r.get("YPXH") == undefined
							|| r.get("JHHJ") == null || r.get("JHHJ") == ""
							|| r.get("JHHJ") == undefined
							|| r.get("LSJE") == null || r.get("LSJE") == ""
							|| r.get("LSJE") == undefined) {
						continue;
					}
					hjje += parseFloat(r.get("JHHJ"));
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("合计   零售金额:" + lsje.toFixed(4) + " 进货金额:"
						+ hjje.toFixed(4));
				// document.getElementById("YK_RK02_QT").innerHTML = "合计 零售金额:"
				// + lsje.toFixed(2) + " 进货金额:" + hjje.toFixed(2);
			},
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.isRead == 1) {
					return false;
				}
				return true;
			},
			// 处理库存界面返回的SBXH
			onCheckData : function(sbxh, kcsl, ypph, ypxq, jhjg) {
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					if (this.store.getAt(i).get("KCSB") == sbxh) {
						MyMessageTip.msg("提示", "第" + (i + 1)
										+ "行相同批次的药品已经存在,请修改数量!", true);
						this.store.remove(this.checkRecord);
						this.doCreate();
						this.getWin().hide();
						return;
					}
				}
				this.checkRecord.set("KCSB", sbxh);
				this.checkRecord.set("YPPH", ypph);
				this.checkRecord.set("YPXQ", ypxq);
				this.checkRecord.set("JHJG", jhjg);
				if (kcsl < -this.checkRecord.get("RKSL")) {
					MyMessageTip.msg("提示", "库存不够!", true);
					this.checkRecord.set("RKSL", -kcsl);
					this.checkRecord.set("LSJE", (parseFloat(this.checkRecord
									.get("LSJG")) * parseFloat(this.checkRecord
									.get("RKSL"))).toFixed(4));
				}
				this.checkRecord.set("JHHJ",
						(parseFloat(jhjg) * parseFloat(this.checkRecord
								.get("RKSL"))).toFixed(4));
				this.doJshj();
				this.onClose();
			},
			onClose : function() {
				// alert(this.checkRecord.get("KCSB"))
				if (this.checkRecord.get("KCSB") == null
						|| this.checkRecord.get("KCSB") == 0
						|| this.checkRecord.get("KCSB") == undefined) {
					MyMessageTip.msg("提示", "未选择库存,库存数量不能为负", true);
					this.checkRecord.set("RKSL", 0);
					this.checkRecord.set("JHHJ", 0);
					this.checkRecord.set("PFJE", 0);
					this.checkRecord.set("LSJE", 0);
					this.doJshj();
				}
				//this.getWin().hide();
			},// 改变按钮状态
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
			}
		})