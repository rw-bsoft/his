$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigChargingProjectsDetailList = function(cfg) {
	cfg.modal = true;
	cfg.width = 900;
	cfg.height = 500;
	cfg.autoLoadData = false;
	phis.application.cfg.script.ConfigChargingProjectsDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigChargingProjectsDetailList,
		phis.script.SimpleList, {
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					win.setTitle(module.name)
					win.setWidth(850)
					win.show()
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doNew()
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			onWinShow : function() {
				if (this.grid) {
					this.cndField.setValue("");
					this.initCnd = ["eq", ["$", "FYGB"], ["d", this.initDataId]];
					this.requestData.cnd = ["eq", ["$", "FYGB"],
							["d", this.initDataId]];
					this.loadData();
				}
			},
			doCancel : function() {
				this.win.hide();
			},
			doLogOut : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {};
				body["FYXH"] = r.data.FYXH;
				body["ZFPB"] = r.data.ZFPB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configChargingProjectsService",
							serviceAction : "logoutMedicalItems",
							method : "execute",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "logOut");
					btn = btn[0];
					if (r.data.ZFPB == 0) {
						if (btn.getText().indexOf("取消") > -1) {
							return;
						}
						btn.setText(btn.getText().replace("作废", "取消作废"));
					} else {
						btn.setText(btn.getText().replace("取消作废", "作废"));
					}
					this.refresh();
				}
			},
			// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.cfg.script.ConfigChargingProjectsDetailList.superclass.onReady
						.call(this);
				this.initCnd = ["eq", ["$", "FYGB"], ["d", this.initDataId]];
				this.requestData.cnd = ["eq", ["$", "FYGB"],
						["d", this.initDataId]];
				this.loadData();
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.ZFPB == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '医疗项目明细已作废'
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
				var btn = btns.find("cmd", "logOut");
				btn = btn[0];
				if (r.data.ZFPB == 1) {
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
			processRemove : function() {
				var r = this.getSelectedRecord()
				this.mask("正在删除数据...")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							method : "execute",
							body : {
								fyxh : r.id
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.store.remove(r)
							} else {
								MyMessageTip.msg("提示", msg, true);
								return false;
							}
						}, this);
			}, // 右键 作废和取消作废相应改变
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
						if (action.id == "logOut") {
							if (r.data.ZFPB == 1) {
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
				} else {
					for (var i = 0; i < cmenu.items.length; i++) {
						if (cmenu.items.itemAt(i).cmd == "logOut") {
							if (r.data.ZFPB == 1) {
								cmenu.items.itemAt(i).setText("取消作废");
							} else {
								cmenu.items.itemAt(i).setText("作废");
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
			}
		})