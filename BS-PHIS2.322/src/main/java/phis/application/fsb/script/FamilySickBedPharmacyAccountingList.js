$package("phis.application.fsb.script")

$import("phis.script.EditorList")

phis.application.fsb.script.FamilySickBedPharmacyAccountingList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.minListWidth = 500;
	cfg.remoteUrl = "Medicines"
	cfg.queryParams={"tag":"jz","type":1};
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{JHJG}</td>';
	this.labelText=" 零售合计:0 ";//底部合计
	phis.application.fsb.script.FamilySickBedPharmacyAccountingList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(phis.application.fsb.script.FamilySickBedPharmacyAccountingList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				if (this.grid) {
					if (!this.isCombined) {
						this.fireEvent("beforeAddToWin", this.grid)
						this.addPanelToWin();
					}
					return this.grid;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				this.isCompositeKey = schema.compositeKey;
				var items = schema.items
				if (!items) {
					return;
				}
				this.store = this.getStore(items)
				// if (this.mutiSelect) {
				// this.sm = new Ext.grid.CheckboxSelectionModel()
				// }
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var cfg = {
					border : false,
					store : this.store,
					cm : this.cm,
					height : this.height,
					loadMask : {
						msg : '正在加载数据...',
						msgCls : 'x-mask-loading'
					},
					buttonAlign : 'center',
					clicksToEdit : 1,
					frame : true,
					plugins : this.rowExpander,
					viewConfig : {
						// forceFit : true,
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				if (this.sm) {
					cfg.sm = this.sm
				}
				if (this.viewConfig) {
					Ext.apply(cfg.viewConfig, this.viewConfig)
				}
				if (this.group) {
					cfg.view = new Ext.grid.GroupingView({
								// forceFit : true,
								showGroupName : true,
								enableNoGroups : false,
								hideGroupedColumn : true,
								enableGroupingMenu : false,
								columnsText : "表格字段",
								groupByText : "使用当前字段进行分组",
								showGroupsText : "表格分组",
								groupTextTpl : this.groupTextTpl
							});
				}
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				if (this.summaryable) {
					$import("phis.script.ux.GridSummary");
					var summary = new org.ext.ux.grid.GridSummary();
					cfg.plugins = [summary]
					this.summary = summary;
				}
				var cndbars = this.getCndBar(items)
				if (!this.disablePagingTbr) {
					cfg.bbar = this.getPagingToolbar(this.store)
				} else {
					cfg.bbar = this.bbar
				}
				if (!this.showButtonOnPT) {
					if (this.showButtonOnTop) {
						cfg.tbar = (cndbars.concat(this.tbar || []))
								.concat(this.createButton())
					} else {
						cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.buttons = this.createButton()
					}
				}
				this.expansion(cfg);// add by yangl
				this.grid = new this.gridCreator(cfg)
				// this.grid.getTopToolbar().enableOverflow = true
				this.grid.on("render", this.onReady, this)
				this.grid.on("contextmenu", function(e) {
							e.stopEvent()
						})
				this.grid.on("rowcontextmenu", this.onContextMenu, this)
				this.grid.on("rowdblclick", this.onDblClick, this)
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("keydown", function(e) {
							if (e.getKey() == e.PAGEDOWN) {
								e.stopEvent()
								this.pagingToolbar.nextPage()
								return
							}
							if (e.getKey() == e.PAGEUP) {
								e.stopEvent()
								this.pagingToolbar.prevPage()
								return
							}
						}, this)

				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				var grid = this.grid;
				grid.on("afteredit", this.afterCellEdit, this)
				grid.on("beforeedit", this.beforeCellEdit, this)
				grid.on("doNewColumn", this.doInsertAfter, this)
				var sm = grid.getSelectionModel();
				var _ctr=this;
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
								g.startEditing(cell[0],2);
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
						var cell = sm.getSelectedCell();
						var storeCount=_ctr.store.getCount();
						if (cell[1] >5) {
							if(cell[0]+1==storeCount){
								if(_ctr.store.getAt(cell[0]).get("YPXH")==null||_ctr.store.getAt(cell[0]).get("YPXH")==""||_ctr.store.getAt(cell[0]).get("YPXH")==0||_ctr.store.getAt(cell[0]).get("YPXH")==undefined){
								g.startEditing(cell[0], 1);
								}else{_ctr.doCreate();}
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
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				if (this.butRule) {
					var ac = util.Accredit;
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								});
					}
				}
				// var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					// btn.accessKey = f1 + i + this.buttonIndex,
					btn.cmd = action.id;
					btn.text = action.name;
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				var kcsb = record.get("KCSB");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPXH") ==  record.get("YPXH")&&griddata.itemAt(i).get("YPCD") ==  record.get("YPCD")
							&& i != row) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品量",true);
						return;
					}
				}
				var data=record.data;
				rowItem.set('YPMC', data.YPMC);
				rowItem.set('CDMC', data.CDMC);
				// var rowItem = griddata.itemAt(row);
				rowItem.set('YFGG', data.YFGG);
				rowItem.set('YFDW', data.YFDW);
				rowItem.set('LSJG', data.LSJG);
				rowItem.set('YPSL', 0);
				rowItem.set('LSJE', 0);
				rowItem.set('YPCD', data.YPCD);
				rowItem.set('YPXH', data.YPXH);
				rowItem.set('YPPH', data.YPPH);
				rowItem.set('YPXQ', data.YPXQ);
				rowItem.set('KCSL', data.KCSL);
				//rowItem.set('KCSB', data.KCSB);
				rowItem.set('YFBZ', data.YFBZ);
				rowItem.set('JHJG', data.JHJG);
				rowItem.set('JYLX', data.JYLX);
				rowItem.set('FYFS', data.FYFS);
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				this.grid.startEditing(row, 8);
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
								},  {
									name : 'JHJG'
								},{
									name : 'YPMC'
								}, {
									name : 'YFGG'
								},{
									name : 'YFBZ'
								}, {
									name : 'YFDW'
								}, {
									name : 'KCSB'
								}, {
									name : 'KCSL'
								}, {
									name : 'YPPH'
								}, {
									name : 'YPXQ'
								}, {
									name : 'JYLX'
								},{
									name : 'FYFS'
								}
								]);
			},
			doNew:function(){
			this.clear();
			this.label.setText(this.labelText)
			},
			expansion : function(cfg) {
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
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined||r.get("YPSL")==null||r.get("YPSL")==""||r.get("YPSL")==undefined){
					continue;
					}
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("零售合计:"+ lsje.toFixed(4));
			},
			onAfterCellEdit : function(it, record, field, v) {
				if(it.id=="YPSL"){
					if(parseFloat(v)==0){
					this.doRemove();
					return;
					}
					if(parseFloat(v)<0){//如果是负数,则去数据库查询发药记录里面的价格
						if(record.get("YPXH")==null||record.get("YPXH")==""||record.get("YPXH")==undefined){
						MyMessageTip.msg("提示", "未输入药品,不能将数量改成负数", true);
						record.set("YPSL",0);
						record.set("LSJE",0);
						this.doJshj();
						return ;
						}
					var ZYH=this.opener.getZyh();
					var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : {
							"ZYH":ZYH,
							"KCSB":record.get("KCSB"),
							"YPXH":record.get("YPXH"),
							"YPCD":record.get("YPCD")
							}
						});
					if (r.code > 300) {
					MyMessageTip.msg("提示", r.msg, true);
					record.set("YPSL",0);
					record.set("LSJE",0);
					this.doJshj();
					return;
					}else{
					var retRecord=r.json.body;
					if(retRecord.TYPE==1){//后台查询返回只有一条记录的时候(KCSB不为0或者相同的记账记录只有一条)
					record.set("LSJG",retRecord.LSJG);
					record.set("JHJG",retRecord.JHJG);
					record.set("KCSB",retRecord.KCSB);
					record.set("LSJE",(parseFloat(record.get("YPSL"))*parseFloat(record.get("LSJG"))).toFixed(2));
					this.doJshj();
					return;
					}
					//后台查询出相同的记账记录有多条时
					this.checkRecord = record;
					this.list = this.createModule("list", this.refList);
							this.list.on("checkData", this.onCheckData, this);
							this.list.on("close", this.onClose, this);
							var m = this.list.initPanel();
							var win = this.getWin();
							win.add(m);
							win.show()
							win.center()
							if (!win.hidden) {
								this.list.requestData.cnd = ['and',['in',['$','KCSB'],retRecord.KCSB],['eq',['$','ZYH'],['l',ZYH]]];
								this.list.loadData();
								return;
							}
					}
					}else{
//					if(parseFloat(v)>parseFloat(record.get("KCSL"))){
//					MyMessageTip.msg("提示", "输入数量大于库存数,当前输入:"+v+",实际库存"+record.get("KCSL")+"!", true);
//					record.set("YPSL",parseFloat(record.get("KCSL")));
//					}
					record.set("LSJE",(parseFloat(record.get("YPSL"))*parseFloat(record.get("LSJG"))).toFixed(2));
					this.doJshj();
					}
				}
			},
			doCreate : function(item, e) {
				if(this.store.getCount()>0){
				var ypxh=this.store.getAt(this.store.getCount()-1).get("YPXH");
				if(ypxh==null||ypxh==""||ypxh==undefined){
				this.grid.startEditing(this.store.getCount()-1, 2);
				return;
				}
				}
				phis.application.fsb.script.FamilySickBedPharmacyAccountingList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				store.getAt(n).set("LB",this.getLb());
				this.grid.startEditing(n, 2);
			},
			getLb : function() {
				if(!this.type){
				return ;}
				if(this.type==1){
				return "西";
				}
				if(this.type==2){
				return "成";
				}
				if(this.type==3){
				return "草";
				}
			},
			onCheckData : function(sbxh,jhjg,lsjg) {
				var count=this.store.getCount();
				for(var i=0;i<count;i++){
				if(this.store.getAt(i)!=this.checkRecord&&this.store.getAt(i).get("KCSB")==sbxh&&this.store.getAt(i).get("YPSL")<0){
				MyMessageTip.msg("提示","第"+(i+1)+"行相同批次的药品已经存在,请修改数量!", true);
				this.store.remove(this.checkRecord);
				this.doCreate();
				this.getWin().hide();
				return;
				}
				}
				this.checkRecord.set("KCSB", sbxh);
				this.checkRecord.set("JHJG", jhjg);
				this.checkRecord.set("LSJG", lsjg);
				this.checkRecord.set("LSJE",(parseFloat(this.checkRecord.get("YPSL"))*parseFloat(this.checkRecord.get("LSJG"))).toFixed(2));
				this.doJshj();
				this.onClose();
			},
			onClose : function() {
				if (this.checkRecord.get("KCSB") == null
						|| this.checkRecord.get("KCSB") == 0
						|| this.checkRecord.get("KCSB") == undefined) {
					MyMessageTip.msg("提示", "未选择对应记账记录,数量不能为负", true);
					this.checkRecord.set("YPSL", 0);
					this.checkRecord.set("LSJE", 0);
				}
				this.getWin().hide();
				this.doJshj();
			}
		});