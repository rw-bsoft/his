$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigProjectsDetailList = function(cfg) {

	cfg.modal = true;
	cfg.width = 900;
	cfg.height = 500;
	cfg.autoLoadData = false;
	phis.application.cfg.script.ConfigProjectsDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.serviceId = "configDeptCostService";
}
Ext.extend(phis.application.cfg.script.ConfigProjectsDetailList,
		phis.script.SimpleList, {
			onRenderer : function(value, metaData, r) {
				var ZFBZ = r.get("ZFBZ");
				var src = (ZFBZ == 1) ? "no" : "yes";
				if (ZFBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/" + src
							+ ".png'/>";
				} else {
					return "";
				}
			},
			doCallIn : function() {
				if (this.win1) {
					var module = this.midiModules["getCFG080401"];
					module.initDataId = this.initDataId
					module.mainApp = this.mainApp;
					module.requestData.cnd = null;
					module.loadData();
					this.win1.show();
					this.win1.center();
					// this.win.setPosition(300,80);
					return;
				}
				var module = this.createModule("getCFG080401", "phis.application.cfg.CFG/CFG/CFG080401");
				module.on("save", this.onSave, this);
				this.module = module;
				module.initDataId = this.initDataId
				module.mainApp = this.mainApp;
				this.win1 = module.getWin();
				this.win1.add(module.initPanel());
				this.win1.show();
				// this.win.setPosition(300,80);
			},
			onSave : function() {
				this.refresh();
			},
			onWinShow : function() {
				if (this.grid) {
					this.cndField.setValue("");
					this.initCnd = ["eq", ["$", "b.FYGB"],
							["d", this.initDataId]];
					this.requestData.cnd = ["eq", ["$", "b.FYGB"],
							["d", this.initDataId]];
					// this.requestData.cnd =
					// ["eq",["$","YSGH"],["s",this.initDataId]];
					this.loadData();
				}
			},
			openModule : function(cmd, r, xy) {
				phis.application.cfg.script.ConfigProjectsDetailList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd]
				var win = module.getWin();
				var default_xy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(default_xy[0], 80);
			},
			// 全部调入
			doCallInAll : function() {
				if (this.saving)
					return;
				this.saving = true
				var body = {
					"FYGB" : this.initDataId
				};
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveCallinAll",
							method : "execute",
							body : body
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							this.loadData();
						}, this)// jsonRequest
			},
			doCancel : function() {
				this.win.hide();
			},
			doLogout : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {};
				body["FYXH"] = r.data.FYXH;
				body["JGID"] = r.data.JGID;
				body["ZFPB"] = r.data.ZFPB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "logoutProject",
							method : "execute",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "logout");
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
			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "logout");
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
			onReady : function() {
				phis.application.cfg.script.ConfigProjectsDetailList.superclass.onReady
						.call(this);
				this.cndField.setValue("");
				this.initCnd = ["eq", ["$", "b.FYGB"], ["d", this.initDataId]];
				this.requestData.cnd = ["eq", ["$", "b.FYGB"],
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
			onRenderer : function(value, metaData, r) {
				var ZFPB = r.get("ZFPB");
				var src = (ZFPB == 1) ? "yes" : "no";
				if (ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/" + src
							+ ".png'/>";
				} else {
					return "";
				}
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
						if (action.id == "logout") {
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
						if (cmenu.items.itemAt(i).cmd == "logout") {
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
			},
			loadModule : function(cls, entryName, item, r) {
				if (this.loading) {
					return
				}
				var cmd = item.cmd
				var cfg = {}
				cfg._mId = this.grid._mId // 增加module的id
				cfg.title = this.name + '-' + item.text
				cfg.entryName = entryName
				cfg.op = cmd
				cfg.exContext = {}
				Ext.apply(cfg.exContext, this.exContext)

				if (cmd != 'create') {
					var initDataBody = {};
					initDataBody["JGID"] = r.get("JGID");
					initDataBody["FYXH"] = r.get("FYXH");
					cfg.initDataBody = initDataBody;
					cfg.exContext[entryName] = r;
				}
				if (this.saveServiceId) {
					cfg.saveServiceId = this.saveServiceId;
				}
				var m = this.midiModules[cmd]
				if (!m) {
					this.loading = true
					$require(cls, [function() {
										this.loading = false
										cfg.autoLoadData = false;
										var module = eval("new " + cls
												+ "(cfg)")
										module.on("save", this.onSave, this)
										module.on("close", this.active, this)
										module.opener = this
										module.setMainApp(this.mainApp)
										this.midiModules[cmd] = module
										this.fireEvent("loadModule", module)
										this.openModule(cmd, r, 100, 50)
									}, this])
				} else {
					Ext.apply(m, cfg)
					this.openModule(cmd, r)
				}
			}
		})