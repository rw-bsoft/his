$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseMedicinesPirvateManageList = function(cfg) {
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	phis.application.sto.script.StorehouseMedicinesPirvateManageList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateManageList,
		phis.script.SimpleList, {
			doChange:function(){
				var r=this.getSelectedRecord();
				if(r == "" || r == null || r == undefined){
					Ext.Msg.alert("提示", "请选择需更换的记录！");
					return;
				}
				if (!this.DRWind) {
					var _ctx = this;
					var dicId = "phis.dictionary.yklb_ykdr";
					this.GTCombo = util.dictionary.SimpleDicFactory.createDic({id : dicId,width : 150});
					this.GTCombo.store.on('load',function(){
						if(_ctx.GTCombo.store.getCount()>0)
							_ctx.GTCombo.setValue(_ctx.GTCombo.store.getAt(0).get('key'));
					})
					this.GTCombo.store.load();
					this.DRForm = new Ext.FormPanel({
								frame : true,
								items : [{layout : "column", items : [{xtype:"label", text:"选择药库："}, this.GTCombo]}]
							})
					this.DRWind = new Ext.Window({
								layout : "form",
								title : '选择药库',
								width : 250,
								resizable : false,
								modal : false,
								iconCls : 'x-logon-win',
								shim : true,
								buttonAlign : 'center',
								closeAction : 'hide',
								buttons : [{
											text : '确定',
											handler : this.doChangeData,
											scope : this
										}]
							})
					this.midiWins["DRWind"] = this.DRWind;
					this.DRWind.add(this.DRForm);
				}
				this.DRWind.show();
			},
			doChangeData : function(){
				var r=this.getSelectedRecord();
				var data ={};
				data["JGID"] = r.get("JGID");
				data["YPXH"] = r.get("YPXH");
				var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "countKcsl",
									body : data
								});
								debugger;
				var list = res.json.body;
//				alert("库存："+list[0].KCSL);
				if(res.code > 300 || list==null){
					MyMessageTip.msg("提示", "库存查询失败!", true);
					return;
				}else if(list[0].KCSL>0){
					MyMessageTip.msg("提示", "该药品现有库存，无法转移!", true);
					return;
				}
				var body ={};
				body["JGID"] = r.get("JGID");
				body["YPXH"] = r.get("YPXH");
				body["RYKSB"] = r.get("YKSB"); //原记录的药库识别
				body["YKSB"] = this.GTCombo.getValue();
				var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "changeYKSB",
									body : body
								});
				if (ret.code > 300) {
					MyMessageTip.msg("提示", "转移失败!", true);
					return;
				} else {
					MyMessageTip.msg("提示", "转移成功!", true);
					this.loadData();
					this.DRWind.close();
					return;
				}
			},
			doImport : function() {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return;
				}
				this.list = this.createModule("impList",this.impref);
				this.list.on("save", this.onSave, this);
				var p = this.list.getWin();
				p.show();
			},
			doXg:function(){
			if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return;
				}
				var initDataBody = {};
				var r=this.getSelectedRecord();
				initDataBody["YPXH"]=r.get("YPXH");
				initDataBody["JGID"]=r.get("JGID");
				this.xgmodule = this.createModule("xgmodule",this.xgref);
				this.xgmodule.on("save", this.onSave, this);
				var p = this.xgmodule.getWin();
				p.add(this.xgmodule.initPanel());
				p.show();
				this.xgmodule.initDataBody = initDataBody;
				this.xgmodule.op="update"
				this.xgmodule.loadData();
			},
			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "invalid");
				btn = btn[0];
				if (r.data.YKZF == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("作废", "取消作废"));
				} else {
					btn.setText(btn.getText().replace("取消作废", "作废"));
				}

			},
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
			// 作废和取消作废
			doInvalid : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("提示", "未选中记录", true);
					return;
				}
				var body = {};
				body["ypxh"] = r.data.YPXH;
				body["op"] = r.data.YKZF;
				body["jgid"] = r.data.JGID;
				body["yksb"] = r.data.YKSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.invalidActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "invalid");
					btn = btn[0];
					this.refresh();
					if (r.data.YKZF == 0) {
						if (btn.getText().indexOf("取消") > -1) {
							return;
						}
						btn.setText(btn.getText().replace("作废", "取消作废"));
					} else {
						btn.setText(btn.getText().replace("取消作废", "作废"));
					}
				}
			},
			// 页面加载时判断药品是否已作废,如作废则加上图片
			onRenderer : function(value, metaData, r) {
				if (r.data.YKZF == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/>";
				}
				return value;
			},
			// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.sto.script.StorehouseMedicinesPirvateManageList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.YKZF == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '药品已作废'
									}, false);

						}
					}
				}
			},
			// 刚打开页面时候默认选中数据,这时候判断下作废按钮
		onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0);
			this.onRowClick();
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
			this.onRowClick();
		}
	},
	//ref界面增加双主键传值
	loadRemote : function(ref, btn) {
		if (this.loading) {
			return
		}
		var r = this.getSelectedRecord()
		var cmd = btn.cmd
		if (cmd == "update" || cmd == "read") {
			if (r == null) {
				return
			}
		}
		var cfg = {}
		cfg.title = "药品私用信息-修改"
		cfg.op = cmd
		cfg.openWinInTaskbar = false
		cfg.autoLoadData = false;
		cfg.exContext = {}
		Ext.apply(cfg.exContext, this.exContext)
		if (cmd != 'create') {
			if (this.isCompositeKey) {
				var pkeys = this.schema.pkeys;
				var initDataBody = {};
				initDataBody["YPXH"]=r.get("YPXH");
				initDataBody["JGID"]=r.get("JGID");
				cfg.initDataBody = initDataBody;
			} else {
				cfg.initDataId = r.id;
			}
			cfg.exContext[this.entryName] = r
		} else {
			cfg.initDataId = null;
		}
		var module = this.midiModules[cmd]
		if (module) {
			Ext.apply(module, cfg)
			this.openModule(cmd, r)
			module.tab.doLayout();
			module.tab.syncSize();
		} else {
			this.loading = true
			this.mainApp.taskManager.loadInstance(ref, cfg, function(m, from) {
						this.loading = false
						m.on("save", this.onSave, this)
						m.on("close", this.active, this)
						m._refId = ref
						this.midiModules[cmd] = m
						if (from == "local") {
							Ext.apply(m, cfg)
						}
						this.fireEvent("loadModule", m)
						this.openModule(cmd, r, [100, 50])
					}, this)
		}
	}
	,
			//右键 作废和取消作废相应改变
	onContextMenu : function(grid, rowIndex, e) {
		if (e) {
			e.stopEvent()
		}
		if (this.disableContextMenu) {
			return
		}
		this.grid.getSelectionModel().selectRow(rowIndex)
		var r = this.getSelectedRecord();
		var cmenu = this.midiMenus['gridContextMenu']
		if (!cmenu) {
			var items = [];
			var actions = this.actions
			if (!actions) {
				return;
			}
			for (var i = 0; i < actions.length; i++) {
				var action = actions[i];
				var it = {}
				it.cmd = action.id
				it.ref = action.ref
				it.iconCls = action.iconCls || action.id
				it.script = action.script
				it.text = action.name
				if(action.id=="invalid"){
				if (r.data.YKZF == 1) {
					it.text = "取消作废";
				}
				}
				it.handler = this.doAction
				it.scope = this
				items.push(it)
			}
			cmenu = new Ext.menu.Menu({
						items : items
					})
			this.midiMenus['gridContextMenu'] = cmenu
		}else{
			this.onRowClick();
			for(var i=0;i<cmenu.items.length;i++){
			if(cmenu.items.itemAt(i).cmd=="invalid"){
				if (r.data.YKZF == 1) {
			cmenu.items.itemAt(i).setText("取消作废");}
			else{
			cmenu.items.itemAt(i).setText("作废") ;
			}
			}
			}
		
		}
		// @@ to set menuItem disable or enable according to buttons of
		// toptoolbar.
		var toolBar = this.grid.getTopToolbar();
		if (toolBar) {
			for (var i = 0; i < this.actions.length; i++) {
				var btn = toolBar.find("cmd", this.actions[i].id);
				if (!btn || btn.length == 0) {
					continue;
				}
				if (btn[0].disabled) {
					cmenu.items.itemAt(i).disable();
				} else {
					cmenu.items.itemAt(i).enable();
				}

			}
		}
		cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
	},
	onDblClick:function(grid,index,e){
	this.doXg();
	}
		})