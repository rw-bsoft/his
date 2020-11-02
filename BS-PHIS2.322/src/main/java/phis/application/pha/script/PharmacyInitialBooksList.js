/**
 * 初始化账簿列表
 * 
 * @author : caijy
 */
$package("phis.application.pha.script")
$import("phis.script.EditorList")
phis.application.pha.script.PharmacyInitialBooksList = function(cfg) {
	this.isTransfer = 0;
	phis.application.pha.script.PharmacyInitialBooksList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.pha.script.PharmacyInitialBooksList,
		phis.script.EditorList, {
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if (this.isTransfer == 0) {
					this.setButtonsState(['preserve'], true);
					this.setButtonsState(['transfer'], false);
				}
				if (it.id == "YPSL") {
					if ((v != null && v != "") || v == 0) {
						var pfje = (v * record.get("PFJG")).toFixed(4);
						var jhje = (v * record.get("JHJG")).toFixed(4);
						var lsje = (v * record.get("LSJG")).toFixed(4);
						record.set("JHJE", jhje);
						record.set("PFJE", pfje);
						record.set("LSJE", lsje);

					}
				}
				if (it.id == "YPPH") {
					if (v.length > 20) {
						record.set("YPPH", "");
					}

				}
				if (!this.editRecords) {
					this.editRecords = {};
				}
				this.editRecords[record.id] = record.data;
			},
			// 页面加载时候保存按钮变灰
			onReady : function() {
				phis.application.pha.script.PharmacyInitialBooksList.superclass.onReady
						.call(this);
				if (this.isTransfer == 0) {
					this.setButtonsState(['preserve'], false);
					this.on("afterCellEdit", this.onAfterCellEdit, this);
					this.grid.on("keypress", this.onKeyPress, this);
					var sm = this.grid.getSelectionModel();
					var _ctr = this;
					sm.onEditorKey = function(field, e) {
					}
				} else {
					this.setButtonsState(['preserve'], false);
					this.setButtonsState(['transfer'], false);
					this.grid.on("keypress", this.onKeyPress, this);
					var sm = this.grid.getSelectionModel();
					var _ctr = this;
					sm.onEditorKey = function(field, e) {
					}
				}
			},
			// 保存
			doPreserve : function() {
				var ed=this.grid.activeEditor;
				if (!ed) {
						ed = this.grid.lastActiveEditor;
					}
					if(ed){
					ed.completeEdit();
					}
				this.setButtonsState(['preserve'], false);
				this.setButtonsState(['transfer'], true);
				if (!this.editRecords) {
					return;
				}
				var body = this.getEditRecords();
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveInventoryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doPreserve);
					return;
				}
				this.editRecords = {};
			},
			// 转账
			doTransfer : function() {
				var records = this.getEditRecords();
				if (records.length != 0) {
					this.setButtonsState(['preserve'], false);
					this.setButtonsState(['transfer'], true);
					return;
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.transferActionId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doPreserve);
					return;
				}
				this.refresh();
				this.setButtonsState(['preserve'], false);
				this.setButtonsState(['transfer'], false);
				this.isTransfer = 1;
			},
			// 关闭窗口
			doClose : function() {
				this.fireEvent("listClose", this);
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
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
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 加载时回填缓存数据
			onStoreLoadData : function(store, records, ops) {
				phis.application.pha.script.PharmacyInitialBooksList.superclass.onStoreLoadData
						.call(this, store, records, ops)
				for (var id in this.editRecords) {
					var r = store.getById(id)
					if (r) {
						var index = store.indexOf(r);
						store.remove(r);
						Ext.apply(r.data, this.editRecords[id]);
						store.insert(index, r);
					}
				}

			},
			// 获取编辑过的数据.获取前自行判断是否有记录被修改,无记录则会返回空数组
			getEditRecords : function() {
				var records = []
				for (var id in this.editRecords) {
					records.push(this.editRecords[id])
				}
				return records
			},
			onRenderer_two : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(2);
				}
				return value;
			},
			onRenderer_four : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(4);
				}
				return value;
			},
			// 跳光标
			onKeyPress : function(e) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				if (e.getKey() == 13) {
					if (col == 7) {
						this.grid.startEditing(row, 8);
					}
					if (col == 8) {
						this.grid.startEditing(row, 9);
					}
					if (col == 9) {
						this.grid.startEditing(row + 1, 7);
					}
				} else if (e.getKey() == 9) {
					this.grid.startEditing(row + 1, col - 1);
				}
			},
			resetButtons : function() {},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("SBXH"))
				}
				var cfg = {
					requestData : ids
				}
				var module = this.createModule("InitialBooksList", this.refInitialBooksListPrint)
				module.requestData = ids;
				module.getWin().show();
			}
		});