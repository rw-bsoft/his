$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseMySimpleDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	this.disablePagingTbr = true;
	cfg.selectOnFocus = true;
//	this.remoteUrl="MedicineCheckInAndOut";
//	this.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{JHJG}</td><td width="80px">{YPPH}</td><td width="100px">{YPXQ}</td>';
	this.minListWidth=600;
//	this.queryParams={"tag":"ck"};//模糊查询标志判断,需重写
	//this.columnNum=3;//在哪一列换行
	this.toColumnNum=2;//换行到下一行的哪列
	this.count=13;//当前列大于多少换行
	this.labelText=" 零售合计:0  进货合计:0";//底部合计
	phis.application.sto.script.StorehouseMySimpleDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseMySimpleDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sto.script.StorehouseMySimpleDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr=this;
//				grid.onEditorKey = function(field, e) {
//					var sm = this.getSelectionModel();
//					var g = sm.grid;
//					if (e.getKey() == e.ENTER && !e.shiftKey) {
//						var cell = sm.getSelectedCell();
//						var count = this.colModel.getColumnCount();
//						var storeCount=_ctr.store.getCount();
//						if (cell[1] + this.columnNum > count) {
//							if(cell[0] + 1 == storeCount){
//								if(_ctr.store.getAt(cell[0]).get("YPXH") == null || _ctr.store.getAt(cell[0]).get("YPXH") == ""
//									|| _ctr.store.getAt(cell[0]).get("YPXH")==0 || _ctr.store.getAt(cell[0]).get("YPXH") == undefined){
//									g.startEditing(cell[0],this.toColumnNum);
//								}else{
//									_ctr.doCreate();
//								}
//								return;
//							}
//						}
//					}
//
//					this.selModel.onEditorKey(field, e);
//				}
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
						//var count = this.colModel.getColumnCount();
						var storeCount=_ctr.store.getCount();
						if (cell[1] > _ctr.count) {
							if(cell[0] + 1 == storeCount){
								if(_ctr.store.getAt(cell[0]).get("YPXH") == null || _ctr.store.getAt(cell[0]).get("YPXH") == ""
									|| _ctr.store.getAt(cell[0]).get("YPXH")==0 || _ctr.store.getAt(cell[0]).get("YPXH") == undefined){
									g.startEditing(cell[0],_ctr.toColumnNum);
								}else{
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
							id : 'checkoutmdssearch'
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
								}, {
									name : 'KCSB'
								}, {
									name : 'KCSL'
								}
								, {
									name : 'YPPH'
								}
								, {
									name : 'YPXQ'
								}, {
									name : 'DJFS'
								}, {
									name : 'DJGS'
								}, {
									name : 'BZLJ'
								}]);
			},
			// 数据回填,需重写
			setBackInfo : function(obj, record) {
			}
			,// 数量操作后,需重写
			onAfterCellEdit : function(it, record, field, v) {
				
			},
			// 新增
			doNew : function() {
				this.clear();
				this.doInit();
				if(this.labelText){
				this.label.setText(this.labelText)
				}
			},
			doInit : function() {
				this.editRecords = [];
				this.doJshj();
			},
			doCreate : function(item, e) {
				phis.application.sto.script.StorehouseMySimpleDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, this.toColumnNum);
			},
			onRenderer_two : function(value, metaData, r) {
				if (value!=null&&value!=0) {
					return parseFloat(value).toFixed(2);
				}
				return value;
			},	
			onRenderer_four : function(value, metaData, r) {
				if (value!=null&&value!=0) {
					return parseFloat(value).toFixed(4);
				}
				return value;
			},
			//删行时重新计算金额
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
			if(count>0) {
				cm.select(cell[0]<count?cell[0]:(count-1),cell[1]);
			}
			this.doJshj();
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
				this.doJshj();
			},
			expansion : function(cfg) {
				if(!this.labelText){
				return;}
				// 底部 统计信息,未完善
				this.label = new Ext.form.Label({
					text : this.labelText
				})
				cfg.bbar = [];
				cfg.bbar.push(this.label);
			},
			doJshj : function() {
				if(!this.label){
				return;}
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined||r.get("RKSL")==null||r.get("RKSL")==""||r.get("RKSL")==undefined){
					continue;
					}
					hjje += parseFloat(r.get("JHHJ"));
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("零售合计:"+ lsje.toFixed(4) + "  进货合计:" + hjje.toFixed(4));
			},
			onBeforeCellEdit:function(it, record, field, v){
			if (this.isRead) {
					return false;
				}
				return true;
			}
			,
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
				this.doJshj();
			}
		})