$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseStoreroomInventoryCollectList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.sto.script.StorehouseStoreroomInventoryCollectList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseStoreroomInventoryCollectList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.sto.script.StorehouseStoreroomInventoryCollectList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
//				sm.onEditorKey = function(field, e) {
//					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
//					if (k == e.ENTER) {
//						e.stopEvent();
//						if (!ed) {
//							ed = g.lastActiveEditor;
//						}
//						ed.completeEdit();
//						if (e.shiftKey) {
//							newCell = g.walkCells(ed.row, ed.col - 1, -1,
//									sm.acceptsNav, sm);
//						} else {
//							newCell = g.walkCells(ed.row, ed.col + 1, 1,
//									sm.acceptsNav, sm);
//						}
//
//					} else if (k == e.TAB) {
//						e.stopEvent();
//						ed.completeEdit();
//						if (e.shiftKey) {
//							newCell = g.walkCells(ed.row, ed.col - 1, -1,
//									sm.acceptsNav, sm);
//						} else {
//							newCell = g.walkCells(ed.row, ed.col + 1, 1,
//									sm.acceptsNav, sm);
//						}
//					} else if (k == e.ESC) {
//						ed.cancelEdit();
//					}
//					if (newCell) {
//						r = newCell[0];
//						c = newCell[1];
//						this.select(r, c);
//						if (g.isEditor && !g.editing) {
//							ae = g.activeEditor;
//							if (ae && ae.field.triggerBlur) {
//								ae.field.triggerBlur();
//							}
//							g.startEditing(r, c);
//						}
//					}
//
//				};
				return grid;
			},
			onAfterCellEdit : function(it, record, field, v) {
				if (v == "" || v == undefined) {
					v = 0;
				}
				if (it.id == "SPSL") {
					this.checkRecord = record;
					this.openDetail(record.get("YPXH"),record.get("PDDH"))
				}
			},
			// 明细界面点确认后处理
			onSave : function(spsls) {
				var count = spsls.length;
				var length = this.change_kcsl.length;
				var sum = 0;
				var tag = 0;
				for (var i = 0; i < count; i++) {
					sum += parseFloat(spsls[i].SPSL);
					for (var j = 0; j < length; j++) {
						if (spsls[i].KCSB == this.change_kcsl[j].KCSB) {
							this.change_kcsl[j].SPSL = spsls[i].SPSL;
							tag = 1;
							break;
						}
					}
						if (tag == 0) {
							this.change_kcsl.push(spsls[i]);
						}
						tag = 0
					
				}
				this.checkRecord.set("SPSL", sum);
			},
			openDetail : function(ypxh,pddh) {
				if(this.isRead){
				return;}
				this.list = this.createModule("list", this.detailRef);
				this.list.on("save", this.onSave, this);
				var win = this.list.getWin();
				win.add(this.list.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					var body = {};
					body["YPXH"] = ypxh;
					body["PDDH"] = pddh;
					this.list.change_kcsl = this.change_kcsl;
					this.list.requestData.body = body;
					this.list.requestData.op = this.op;
					this.list.requestData.serviceId = this.fullserviceId;
					this.list.requestData.serviceAction = this.queryDetailActionId;
					this.list.loadData();
				}
			},
			//双击打开明细界面
			onDblClick : function(grid, index, e) {
			var record = this.getSelectedRecord();
			if(parseFloat(record.get("KCSL"))==parseFloat(record.get("SPSL"))){
			return;
			}
			this.checkRecord = record;
			this.openDetail(record.get("YPXH"),record.get("PDDH"))
			},
			onBeforeCellEdit:function(){
			if(this.isRead){
			return false;}
			return true;
			},
			//获取修改的数据
			getData:function(){
			return this.change_kcsl;
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				var count=store.getCount();
				var length=this.change_kcsl.length;
				for(var i=0;i<count;i++){
					var spsl=0;
					for(var j=0;j<length;j++){
						//alert("YPXH:"+store.getAt(i).get("YPXH")+"-"+this.change_kcsl[j].YPXH+",YPCD:"+store.getAt(i).get("YPCD")+"-"+this.change_kcsl[j].YPCD+",SPSL:"+this.change_kcsl[j].SPSL)
					if(store.getAt(i).get("YPXH")==this.change_kcsl[j].YPXH&&store.getAt(i).get("YPCD")==this.change_kcsl[j].YPCD){
						spsl+=parseFloat(this.change_kcsl[j].SPSL);
					}
					}
					store.getAt(i).set("SPSL",spsl)
				}
			}
		})