$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyApplyRefundDetailList = function(cfg) {
	cfg.isEdit=false;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.selectOnFocus = true;
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YPGG}</td><td width="20px">{YPDW}</td><td width="80px">{CDMC}</td><td width="60px">{LSJG}</td><td width="60px">{JHJG}</td><td width="80px">{YPPH}</td><td width="100px">{YPXQ}</td>';
	cfg.minListWidth=800;
	phis.application.pha.script.PharmacyApplyRefundDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.pha.script.PharmacyApplyRefundDetailList,
		phis.script.EditorList, {
			//回车跳行,自动新增行
		initPanel : function(sc) {
				var grid = phis.application.pha.script.PharmacyApplyRefundDetailList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr=this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 6 > count) {
							_ctr.doCreate();
							return;
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
			onAfterCellEdit : function(it, record, field, v) {
				this.isEdit=true;
				if(v==""||v==undefined){v=0;}
				if(-v>parseFloat(record.get("YPSL"))){
				MyMessageTip.msg("提示", "超过库存数量", true);
				v=-parseFloat(record.get("YPSL"));
				record.set("SQSL",v);
				}
				if(it.id=="SQSL"){
					if(record.get("JHJG")==null||record.get("JHJG")==""||record.get("JHJG")==undefined||record.get("LSJG")==null||record.get("LSJG")==""||record.get("LSJG")==undefined){
					record.set("JHJE",0);
					record.set("LSJE",0);
					}else{
				record.set("JHJE",(parseFloat(v)*parseFloat(record.get("JHJG"))).toFixed(4));
				record.set("LSJE",(parseFloat(v)*parseFloat(record.get("LSJG"))).toFixed(4));}
				}
				this.doJshj();
			},
			doNew : function() {
				this.clear();
				this.doInit();
				this.doJshj();
			},
			//清空修改数据,关闭窗口用
			doInit : function() {
				this.isEdit=false;
			},
			//新增的时候光标定位到药品栏
			doCreate : function(item, e) {
				phis.application.pha.script.PharmacyApplyRefundDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				this.grid.startEditing(n, 1);
				//this.isEdit=true;
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
									name : 'YPGG'
								}, {
									name : 'YPDW'
								}, {
									name : 'PFJG'
								}, {
									name : 'YFBZ'
								}, {
									name : 'ZXBZ'
								}, {
									name : 'YPPH'
								}, {
									name : 'YPXQ'
								}, {
									name : 'YPSL'
								}, {
									name : 'YFKCSB'
								}, {
									name : 'YKJH'
								}, {
									name : 'YKLJ'
								}, {
									name : 'YKPJ'
								}, {
									name : 'YKJJ'
								}, {
									name : 'KCSB'
								}
								]);
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
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				if(record.get("YKLJ")!=0){
				rowItem.set('LSJG', record.get("YKLJ"));
				}else{
				rowItem.set('LSJG',Math.round(parseFloat(record.get("LSJG"))*parseFloat(record.get("ZXBZ"))/parseFloat(record.get("YFBZ"))*10000)/10000);
				}
				if(record.get("YKJJ")!=0){
				rowItem.set('JHJG', record.get("YKJJ"));
				}else{
				rowItem.set('JHJG',Math.round(parseFloat(record.get("JHJG"))*parseFloat(record.get("ZXBZ"))/parseFloat(record.get("YFBZ"))*10000)/10000);
				}
				if(record.get("YKPJ")!=0){
				rowItem.set('PFJG', record.get("YKPJ"));
				}else{
				rowItem.set('PFJG',Math.round(parseFloat(record.get("PFJG"))*parseFloat(record.get("ZXBZ"))/parseFloat(record.get("YFBZ"))*10000)/10000);
				}
				rowItem.set('BZLJ', record.get("LSJG"));
				rowItem.set('YKJH', record.get("JHJG"));
				rowItem.set('YPGG', record.get("YPGG"));
				rowItem.set('YPDW', record.get("YPDW"));
				rowItem.set('YPXQ', record.get("YPXQ"));
				rowItem.set('YPPH', record.get("YPPH"));
				rowItem.set('YFKCSB', record.get("YFKCSB"));
				rowItem.set('KCSL', Math.round(parseFloat(record.get("YPSL"))*parseFloat(record.get("YFBZ"))/parseFloat(record.get("ZXBZ"))*10000)/10000);
				rowItem.set('KCSB', record.get("KCSB"));
				if (rowItem.get("SQSL") != null && rowItem.get("SQSL") != ""
						&& rowItem.get("SQSL") != 0) {
							if(-rowItem.get("SQSL")>rowItem.get("KCSL")){
							rowItem.set('SQSL', rowItem.get("KCSL"));
							}
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("SQSL")).toFixed(4));
					rowItem.set('JHJE', (record.get("JHJG") * rowItem
									.get("SQSL")).toFixed(4));
									this.doJshj();
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				this.isEdit=true;
				this.grid.startEditing(row, 7);
			},
			doRemove : function() {
			phis.application.pha.script.PharmacyApplyRefundDetailList.superclass.doRemove.call(this);
				this.isEdit=true;
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
				this.doJshj();
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
			//查看和确认不能修改
			onBeforeCellEdit:function(){
			this.remoteDic.lastQuery = "";	
			if(this.isCommit){return false;}
			return true;
			},
			//重写为了不让加载数据的时候重新改变按钮
			resetButtons : function() { 
			}
		})