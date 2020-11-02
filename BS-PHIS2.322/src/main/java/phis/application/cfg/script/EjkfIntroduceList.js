$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.EjkfIntroduceList = function(cfg) {
	phis.application.cfg.script.EjkfIntroduceList.superclass.constructor.apply(this,
			[cfg])
}
var recordIds = new Array();
Ext.extend(phis.application.cfg.script.EjkfIntroduceList, phis.script.EditorList, {
	initPanel : function(sc) {
		if (!this.mainApp['phis'].treasuryId) {
			Ext.MessageBox.alert("提示", "您还没有选择库房， 请先选择库房 !");
			return;
		}
		if (this.mainApp['phis'].treasuryEjkf == 0) {
			Ext.MessageBox.alert("提示", "该库房不是二级库房!");
			return;
		}
		if (this.grid)
			return this.grid;
		var grid = phis.application.cfg.script.EjkfIntroduceList.superclass.initPanel
				.call(this, sc)
		var sm = grid.getSelectionModel();
		// 重写grid的onEditorKey事件
		grid.onEditorKey = function(field, e) {
			if (e.getKey() == e.ENTER && !e.shiftKey) {
				var sm = this.getSelectionModel();
				var cell = sm.getSelectedCell();
				var count = this.colModel.getColumnCount()
				if (cell[1] + 4 >= count) {// 实现倒数第二格单元格回车新增行操作
					return;
				}
			}
			this.selModel.onEditorKey(field, e);
		}
		// 重写onEditorKey方法，实现Enter键导航功能
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
					newCell = g.walkCells(ed.row, ed.col + 1, 1, sm.acceptsNav,
							sm);
				}

			} else if (k == e.TAB) {
				e.stopEvent();
				ed.completeEdit();
				if (e.shiftKey) {
					newCell = g.walkCells(ed.row, ed.col - 1, -1,
							sm.acceptsNav, sm);
				} else {
					newCell = g.walkCells(ed.row, ed.col + 1, 1, sm.acceptsNav,
							sm);
				}
			} else if (k == e.ESC) {
				ed.cancelEdit();
			}
			if (newCell) {
				r = newCell[0];
				c = newCell[1];
				// 判断单价是否为0
				if (c == 4) {
					if (field.getValue() != 0) {
						c++;
					}
				}
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
	loadData : function() {
		this.clear();
		this.requestData.pageNo = 1;
		this.requestData.pageSize = 25;
		this.requestData.serviceId = "phis.materialInformationManagement";
		this.requestData.serviceAction = "getEJJKInfo";
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
		this.resetButtons();
	},
	doIntroduce : function() {
		if (this.win1) {
			var module = this.midiModules["getCFG5801"];
			module.mainApp = this.mainApp;
			module.requestData.cnd = null;
			module.loadData();
			this.win1.show();
			this.win1.center();
			return;
		}
		var module = this.createModule("getCFG5801", "phis.application.cfg.CFG/CFG/CFG5801");
		module.on("save", this.onSave, this);
		this.module = module;
		module.initDataId = this.initDataId
		module.mainApp = this.mainApp;
		this.win1 = module.getWin();
		var p = module.initPanel();
		this.win1.add(p);
		this.win1.show();
	},
	doSave : function() {
		var body = {};
		body["WL_KCYJ"] = [];
		var count = this.store.getCount();
		for (var i = 0; i < count; i++) {
			if (parseInt(this.store.getAt(i).data["GCSL"]) < parseInt(this.store
					.getAt(i).data["DCSL"])) {
				MyMessageTip.msg("提示", "第" + (i + 1) + "行高储数量不能小于低储数量!", true);
				return
			}
			if ((this.store.getAt(i).data["GCSL"] != undefined && this.store
					.getAt(i).data["GCSL"] != 0)
					|| (this.store.getAt(i).data["DCSL"] != undefined && this.store
							.getAt(i).data["DCSL"] != 0)) {
				body["WL_KCYJ"].push(this.store.getAt(i).data);
			}
		}
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "materialInformationManagement",
					serviceAction : "updateKCYJ",
					body : body
				});
		if (r.code > 300) {
			MyMessageTip.msg("提示", "保存失败!", true);
			return;
		} else {
			MyMessageTip.msg("提示", "保存成功!", true);
			return;
		}
	},
	doInvalid : function() {
		var r = this.getSelectedRecord();
		var data = {};
		if (r == null) {
			MyMessageTip.msg("提示", '请选择注销的记录!', true);
			return
		}
		var n = this.store.indexOf(r)
		if (n > -1) {
			this.selectedIndex = n
		}
		data["JLXH"] = r.get("JLXH");
		data["WZXH"] = r.get("WZXH");
		if (r.get("WZZT") == "-1") {
			data["WZZT"] = "1";
			this.grid.el.mask("正在取消注销...", "x-mask-loading")
		} else {
			data["WZZT"] = "-1";
			this.grid.el.mask("正在注销...", "x-mask-loading")
		}
		phis.script.rmi.jsonRequest({
					serviceId : "materialInformationManagement",
					serviceAction : "updateCanceledEjjk",
					schemaList : "WL_EJJK",
					body : data
				}, function(code, msg, json) {
					this.grid.el.unmask()
					if (code >= 300) {
						MyMessageTip.msg("提示", msg, true);
						var btns = this.grid.getTopToolbar();
						var btn = btns.find("cmd", "invalid");
						btn = btn[0];
						if (r.data.WZZT == 1) {
							if (btn.getText().indexOf("取消注销") > -1) {
								return;
							}
							btn.setText(btn.getText().replace("注销", "取消注销"));
						} else {
							btn.setText(btn.getText().replace("取消注销", "注销"));
						}
						this.refresh();
						return;
					}
				}, this)
	},
	onContextMenu : function(grid, rowIndex, e) {
		if (e) {
			e.stopEvent()
		}
		if (this.disableContextMenu) {
			return
		}
		this.grid.getSelectionModel().selectRow(rowIndex)
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
				it.handler = this.doAction
				it.scope = this
				items.push(it)
			}
			cmenu = new Ext.menu.Menu({
						items : items
					})
			this.midiMenus['gridContextMenu'] = cmenu
		}
		// @@ to set menuItem disable or enable according to buttons of
		// toptoolbar.
		var toolBar = this.grid.getTopToolbar();
		if (toolBar) {
			for (var i = 0; i < this.actions.length; i++) {
				var btn = toolBar.find("cmd", this.actions[i].id);
				if (this.actions[i].id == "invalid") {
					var btnResult = btn[0].getText().substring(0, 2);
					if (btn[0].getText().indexOf("取消注销") > -1) {
						var btnResult = btn[0].getText().substring(0, 4);
					}
					cmenu.items.itemAt(i).setText(btnResult);
				}
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
	},// 单击时改变作废按钮
	onRowClick : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var btns = this.grid.getTopToolbar();
		var btn = btns.find("cmd", "invalid");
		btn = btn[0];
		if (r.data.WZZT == -1) {
			if (btn.getText().indexOf("取消注销") > -1) {
				return;
			}
			btn.setText(btn.getText().replace("注销", "取消注销"));
		} else {
			btn.setText(btn.getText().replace("取消注销", "注销"));
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
	},// 上下时改变分配和转床状态
	onKeypress : function(e) {
		if (e.getKey() == 40 || e.getKey() == 38) {
			this.onRowClick();
		}
	}
})