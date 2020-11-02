$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.selectOnFocus = true;
	this.exContext = {};
	cfg.remoteUrl="Medicines";
	/** gejj 2013-07-18修改2162bug 调整{CDMC}{LSJG}{JHJG}宽度**/
//	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{JHJG}</td>';
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="120px">{CDMC}</td><td width="60px">{LSJG}</td><td width="80px">{JHJG}</td>';
	/** end**/
	this.minListWidth=500
	cfg.queryParams={"tag":"tj"};
	phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);

}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr=this;
//				grid.onEditorKey = function(field, e) {
//					var sm = this.getSelectionModel();
//					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
//					if (e.getKey() == e.ENTER && !e.shiftKey) {
//						var cell = sm.getSelectedCell();
//						var count = this.colModel.getColumnCount()
//						var storeCount=_ctr.store.getCount();
//						if (cell[1] + 3 > count) {
//							if(cell[0]+1==storeCount){
//								if(_ctr.store.getAt(cell[0]).get("YPXH")==null||_ctr.store.getAt(cell[0]).get("YPXH")==""||_ctr.store.getAt(cell[0]).get("YPXH")==0||_ctr.store.getAt(cell[0]).get("YPXH")==undefined){
//								g.startEditing(cell[0], 1);
//								}else{_ctr.doCreate();}
//							return;
//							}
//						}
//					}
//
//					this.selModel.onEditorKey(field, e);
//				}
				grid.onEditorKey = function(field, e) {//重写防止最后一列回车报错的问题
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
						var storeCount=_ctr.store.getCount();
						if (cell[1]  > 7) {
							if(cell[0]+1==storeCount){
								if(_ctr.store.getAt(cell[0]).get("YPXH")==null||_ctr.store.getAt(cell[0]).get("YPXH")==""||_ctr.store.getAt(cell[0]).get("YPXH")==0||_ctr.store.getAt(cell[0]).get("YPXH")==undefined){
								g.startEditing(cell[0], 1);
								}else{_ctr.doCreate();}
							return;
							}
						}else{
						g.startEditing(cell[0], cell[1]+1);
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
						Ext.Msg.alert("提示", "该药品已存在,请修改此药品量");
						return;
					}
				}
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('YLSJ', record.get("LSJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YPDW', record.get("YFDW"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('YJHJ', record.get("JHJG"));
				rowItem.set('YFSB', 0);
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				this.grid.startEditing(row, 7);
			},
			// 获取批零加成率
			getPljc : function() {
				if (this.pljc) {
					return this.pljc;
				} else {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryPljcActionId
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getPljc);
						return;
					}
					this.pljc = 1 + ret.json.pljc
					return this.pljc
				}
			},
			// 操作后
			onAfterCellEdit : function(it, record, field, v) {
				// 用于关闭界面提示
				this.editRecords[1] = 1;
				if (it.id == "XJHJ") {
					var pljc = this.getPljc();
					record.set("XLSJ", (v * pljc).toFixed(4));
					var ypxh = record.get("YPXH");
					var ypcd = record.get("YPCD");
					var count = this.store.getCount();
					for (var i = 0; i < count; i++) {
						if (this.store.getAt(i).data["XJHJ"] != v
								&& this.store.getAt(i).data["YPXH"] == ypxh
								&& this.store.getAt(i).data["YPCD"] == ypcd) {
							this.store.getAt(i).set("XJHJ", v);
							this.store.getAt(i).set("XLSJ",
									(v * pljc).toFixed(4));
						}

					}
				}
				if (it.id == "XLSJ") {
					var ypxh = record.get("YPXH");
					var ypcd = record.get("YPCD");
					var count = this.store.getCount();
					for (var i = 0; i < count; i++) {
						if (this.store.getAt(i).data["XLSJ"] != v
								&& this.store.getAt(i).data["YPXH"] == ypxh
								&& this.store.getAt(i).data["YPCD"] == ypcd) {
							this.store.getAt(i).set("XLSJ", v);
						}

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
				phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList.superclass.doCreate
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
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var ypxh = r.data.YPXH;
				var ypcd = r.data.YPCD;
				this.editRecords.push(r.data);
				this.store.remove(r);
				var count = this.store.getCount();
				var removes = [];
				for (var i = 0; i < count; i++) {
					if (this.store.getAt(i).data["YPXH"] == ypxh
							&& this.store.getAt(i).data["YPCD"] == ypcd) {
						removes.push(this.store.getAt(i))
					}
				}
				for (var i = 0; i < removes.length; i++) {
					this.store.remove(removes[i]);
				}
				var count = this.store.getCount();
				var tag=true;
				for(var i=0;i<count;i++){
				if(this.store.getAt(i).data.YPXH==null||this.store.getAt(i).data.YPXH==""||this.store.getAt(i).data.YPXH==undefined){
					tag=false;
				break;
				}
				}
				if(tag){
				this.doCreate();
				}
		// 移除之后焦点定位
			count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
			}
//			,
//			onReady : function() {
//				phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList.superclass.onReady
//						.call(this);
//				this.grid.on("keypress", this.onKeyPress, this);
//			},
//			// 按回车跳光标
//			onKeyPress : function(e) {
//				if (e.getKey() == 13) {
//					var cell = this.grid.getSelectionModel().getSelectedCell();
//					var row = cell[0];
//					var col = cell[1];
//					if (col == 1) {
//						this.grid.startEditing(row, 7);
//					}
//					if (col == 7) {
//						this.grid.startEditing(row, 8);
//					}
//					if (col == 8) {
//						this.doCreate();
//					}
//				}
//			}
		})