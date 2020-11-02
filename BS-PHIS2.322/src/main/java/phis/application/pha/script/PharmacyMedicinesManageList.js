$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMedicinesManageList = function(cfg) {
	cfg.modal = true
	phis.application.pha.script.PharmacyMedicinesManageList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyMedicinesManageList,
		phis.script.SimpleList, {
			initPanel:function(sc){
				if(this.mainApp['phis'].pharmacyId==null||this.mainApp['phis'].pharmacyId==""||this.mainApp['phis'].pharmacyId==undefined){
				Ext.Msg.alert("提示","未设置登录药房,请先设置");
				return null;
				}
				//初始化判断
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryInitActionId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
			var grid = phis.application.pha.script.PharmacyMedicinesManageList.superclass.initPanel.call(this,sc);
			this.grid=grid;
			return grid;
			},
			// 药品导入
			doImport : function() {
				if (!this.fireEvent("beforeSave", this.entryName, this.op)) {
					return;
				};
				var actionId = this.importActionId;
				var servicesId = this.serviceId
				_ctr=this;
				Ext.MessageBox.confirm("提示", "确定从药库导入?",
						function(button, text) {
							if (button == "yes") {
								var r = phis.script.rmi.miniJsonRequestSync({
											serviceId : servicesId,
											serviceAction : actionId
										});
										Ext.Msg.alert("提示",r.msg);
										_ctr.refresh();
							}
						});
			},
			// 作废和取消作废
			doInvalid : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {};
				body["ypxh"] = r.data.YPXH;
				body["yfzf"] = r.data.YFZF;
				body["yfsb"] = r.data.YFSB;
				body["jgid"] = r.data.JGID;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.invalidActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "invalid");
					btn = btn[0];
					this.refresh();
					if (r.data.YFZF == 0) {
						if (btn.getText().indexOf("取消") > -1) {
							return;
						}
						btn.setText(btn.getText().replace("作废", "取消作废"));
					} else {
						btn.setText(btn.getText().replace("取消作废", "作废"));
					}
				}
			},
			//由于查询时要带药房识别条件,如果登录用户没有选药房 会报错,暂时现在这里做控制,以后框架更改后改正
			loadData : function() {
				phis.application.pha.script.PharmacyMedicinesManageList.superclass.loadData
						.call(this);
			},
			// 数据加载时判断记录是否作废
			onRenderer : function(value, metaData, r) {
				if (r.data.YFZF == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/>";
				}

			},
			onReady : function() {
				phis.application.pha.script.PharmacyMedicinesManageList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.YFZF == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '药品已作废'
									}, false);

						}
					}
				}
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
				if (r.data.YFZF == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("作废", "取消作废"));
				} else {
					btn.setText(btn.getText().replace("取消作废", "作废"));
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
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
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
				if (r.data.YFZF == 1) {
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
				if (r.data.YFZF == 1) {
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
			doXg:function(){
				var r=this.getSelectedRecord();
				if(r==null){
				return;}
				var form = this.createModule("applyModule",
						"phis.application.pha.PHA/PHA/PHA0901");
				form.on("save", this.onSave, this);
				form.on("close", this.onOpenClose, this);
				var win = this.getWin();
				win.add(form.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					form.initDataBody=r.data;
					form.loadData();
				}
	},
	onDblClick:function(){
	this.doXg();
	},
	onOpenClose:function(){
	this.getWin().hide();
	}
		})