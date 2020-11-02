$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyCheckInDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td><td width="70px">{JHJG}</td>';
	cfg.minListWidth = 600;
	cfg.queryParams = {
		"tag" : "rk"
	};
	cfg.selectOnFocus = true;
	phis.application.pha.script.PharmacyCheckInDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckInDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.pha.script.PharmacyCheckInDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						var storeCount=_ctr.store.getCount();
						if (cell[1] + 2 > count) {
							if(cell[0]+1==storeCount){
								if(_ctr.store.getAt(cell[0]).get("YPXH")==null||_ctr.store.getAt(cell[0]).get("YPXH")==""||_ctr.store.getAt(cell[0]).get("YPXH")==0||_ctr.store.getAt(cell[0]).get("YPXH")==undefined){
								g.startEditing(cell[0], 1);
								}else{_ctr.doCreate();}
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
//						var cell = sm.getSelectedCell();
//						var storeCount=_ctr.store.getCount();
//						if (cell[1] > 10) {
//							if(cell[0]+1==storeCount){
//								if(_ctr.store.getAt(cell[0]).get("YPXH")==null||_ctr.store.getAt(cell[0]).get("YPXH")==""||_ctr.store.getAt(cell[0]).get("YPXH")==0||_ctr.store.getAt(cell[0]).get("YPXH")==undefined){
//								g.startEditing(cell[0], 1);
//								}else{_ctr.doCreate();}
//							return;
//							}
//						}
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
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var ypcd = record.get("YPCD");
				var ypxh = record.get("YPXH");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPCD") == ypcd
							&& griddata.itemAt(i).get("YPXH") == ypxh
							&& i != row) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品", true);
						return;
					}
				}
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('JHJG', record.get("JHJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YFDW', record.get("YFDW"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('PFJG', record.get("PFJG"));
				if (rowItem.get("RKSL") != null && rowItem.get("RKSL") != ""
						&& rowItem.get("RKSL") != 0) {
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("RKSL")).toFixed(4));
					rowItem.set('JHJE', (record.get("JHJG") * rowItem
									.get("RKSL")).toFixed(4));
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				this.calculatEmount();
				this.grid.startEditing(row, 7);
			},
			onReady : function() {
				phis.application.pha.script.PharmacyCheckInDetailList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
				// this.grid.on("keypress", this.onKeyPress, this)
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
						record.set('YFDW', "");
						record.set('YFBZ', 0);
						record.set('PFJG', 0);
						record.set('LSJE', 0);
						record.set('JHJE', 0);
						this.calculatEmount();
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
					record.set("JHJE", jhje);
					record.set("PFJE", pfje);
					record.set("LSJE", lsje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);

					this.calculatEmount();
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
			},
			doInit : function() {
				this.editRecords = [];
			},

			doCreate : function(item, e) {
				phis.application.pha.script.PharmacyCheckInDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
			},
			// // 按回车跳光标
			// onKeyPress : function(e) {
			// if (e.getKey() == 13) {
			// var cell = this.grid.getSelectionModel().getSelectedCell();
			// var row = cell[0];
			// var col = cell[1];
			// if (col == 1) {
			// this.grid.startEditing(row, 7);
			// }
			// if (col == 7) {
			// this.grid.startEditing(row, 10);
			// }
			// if (col == 10) {
			// this.grid.startEditing(row, 11);
			// }
			// if (col == 11) {
			// this.doCreate();
			// }
			// }
			// },
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
			// 计算金额
			calculatEmount : function() {
				var count = this.store.getCount();
				var allJhje = 0;
				var allLsje = 0;
				for (var i = 0; i < count; i++) {
					var jhje = this.store.getAt(i).data["JHJE"] == undefined
							? 0
							: this.store.getAt(i).data["JHJE"];
					var lsje = this.store.getAt(i).data["LSJE"] == undefined
							? 0
							: this.store.getAt(i).data["LSJE"];
					allJhje = parseFloat(parseFloat(allJhje) + parseFloat(jhje))
							.toFixed(4);
					allLsje = parseFloat(parseFloat(allLsje) + parseFloat(lsje))
							.toFixed(4);
				}
				this.fireEvent("recordAdd", allJhje, allLsje);
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
				this.calculatEmount();
			},
			// 改变按钮状态
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
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.isRead) {
					return false;
				}
				return true;
			}
		})