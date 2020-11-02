$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyMySimpleDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.selectOnFocus = true;
//	this.remoteUrl="MedicineCheckInAndOut";
//	this.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{JHJG}</td><td width="80px">{YPPH}</td><td width="100px">{YPXQ}</td>';
	this.minListWidth=600;
//	this.queryParams={"tag":"ck"};//模糊查询标志判断,需重写
	phis.application.pha.script.PharmacyMySimpleDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.pha.script.PharmacyMySimpleDetailList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.pha.script.PharmacyMySimpleDetailList.superclass.initPanel
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
//						if (cell[1] + 6 > count) {
//							if(cell[0] + 1 == storeCount){
//								if(_ctr.store.getAt(cell[0]).get("YPXH") == null || _ctr.store.getAt(cell[0]).get("YPXH") == ""
//									|| _ctr.store.getAt(cell[0]).get("YPXH")==0 || _ctr.store.getAt(cell[0]).get("YPXH") == undefined){
//									g.startEditing(cell[0],1);
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
						var storeCount=_ctr.store.getCount();
						if (cell[1] > 6) {
							if(cell[0] + 1 == storeCount){
								if(_ctr.store.getAt(cell[0]).get("YPXH") == null || _ctr.store.getAt(cell[0]).get("YPXH") == ""
									|| _ctr.store.getAt(cell[0]).get("YPXH")==0 || _ctr.store.getAt(cell[0]).get("YPXH") == undefined){
									g.startEditing(cell[0],1);
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
				var kcsb = record.get("KCSB");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("KCSB") == kcsb
							&& i != row) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品量",true);
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
				rowItem.set('KCSB', record.get("KCSB"));
				rowItem.set('YPPH', record.get("YPPH"));
				rowItem.set('YPXQ', record.get("YPXQ"));
				rowItem.set('KCSL', record.get("KCSL"));
				if(rowItem.get("YPSL")!=null&&rowItem.get("YPSL")!=""&&rowItem.get("YPSL")!=0){
				rowItem.set('LSJE', (record.get("LSJG")*rowItem.get("YPSL")).toFixed(4));
				rowItem.set('JHJE', (record.get("JHJG")*rowItem.get("YPSL")).toFixed(4));
				}else{
				rowItem.set('LSJE',0);
				rowItem.set('JHJE',0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				this.doJshj();	
				this.grid.startEditing(row, 7);
			}
			,// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "YPMC") {
				if(v==null||v==""||v==0){
				record.set('YPCD', "");
				record.set('YPXH', "");
				record.set('CDMC', "");
				record.set('YPMC', "");
				record.set('LSJG', 0);
				record.set('JHJG', 0);
				record.set('YPGG', "");
				record.set('YFDW', "");
				record.set('YFBZ', 0);
				record.set('PFJG', 0);
				record.set('KCSB', "");
				record.set('YPPH', "");
				record.set('YPXQ', "");
				record.set('YPSL', 0);
				record.set('LSJE', 0);
				record.set('JHJE', 0);
				this.doJshj();
					}
				}
				if (it.id == "YPSL") {
					var pfje=0;
					var jhje=0;
					var lsje=0;
					if (((v != null && v != "") || v == 0)&&record.get("YPXH")!=undefined&&record.get("YPXH")!="") {
						if(v>record.get("KCSL")){
						MyMessageTip.msg("提示", "库存数量不足", true);
						v=record.get("KCSL");
						record.set("YPSL", v);
						}
						var pfje = (v * record.get("PFJG")).toFixed(4);
						var jhje = (v * record.get("JHJG")).toFixed(4);
						var lsje = (v * record.get("LSJG")).toFixed(4);}
						record.set("JHJE", jhje);
						record.set("PFJE", pfje);
						record.set("LSJE", lsje);
						if (!this.editRecords) {
							this.editRecords = [];
						}
						this.editRecords.push(record.data);
					this.doJshj();
				}

			},
			// 新增
			doNew : function() {
				this.clear();
				this.doInit();
			},
			doInit : function() {
				this.editRecords = [];
				this.doJshj();
			},
			doCreate : function(item, e) {
				phis.application.pha.script.PharmacyMySimpleDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
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
				// 底部 统计信息,未完善
				this.label = new Ext.form.Label({
					text : "合计   零售金额:0  进货金额:0"
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
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
					continue;
					}
					if(r.get("JHJE")!=null&&r.get("JHJE")!=""&&r.get("JHJE")!=undefined){
					hjje += parseFloat(r.get("JHJE"));
					}
					if(r.get("LSJE")!=null&&r.get("LSJE")!=""&&r.get("LSJE")!=undefined){
					lsje += parseFloat(r.get("LSJE"));
					}
					
				}
				this.label.setText("合计   零售金额:"+ lsje.toFixed(4) + " 进货金额:" + hjje.toFixed(4));
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