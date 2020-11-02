$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ManufacturerList = function(cfg) {
	this.removeRecords = [];
	cfg.autoLoadData = false;
	cfg.remoteUrl = 'Disease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.minListWidth = 250;
	phis.application.cfg.script.ManufacturerList.superclass.constructor.apply(this,
			[cfg])
	this.on("loadData", this.onLoadData, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(phis.application.cfg.script.ManufacturerList, phis.script.EditorList, {
	initPanel : function(sc) {
		var grid = phis.application.cfg.script.ManufacturerList.superclass.initPanel
				.call(this, sc)
		grid.onEditorKey = function(field, e) {
			if (e.getKey() == e.ENTER && !e.shiftKey) {
				var sm = this.getSelectionModel();
				var cell = sm.getSelectedCell();
				var count = this.colModel.getColumnCount()
				if (cell[1] + 1 >= count && !this.editing) {
					this.fireEvent("doNewColumn");
					return;
				}
			}
			this.selModel.onEditorKey(field, e);
		}
		var sm = grid.getSelectionModel();
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
		// var editor = grid.getColumnModel().getColumnById("ZDLB").editor;
		// editor.on("beforeselect", this.zdlbSelect, this);
		return grid
	},
	onRowClick : function() {
		var cm = this.grid.getSelectionModel();
		var r = this.getSelectedRecord();
		if (r.get("CJXH") == this.lastCJXH) {
			return;
		}
		this.lastCJXH = r.get("CJXH");
		var CJXH = +r.get("CJXH");
		this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].requestData.cnd = ['eq',
				['$', 'DXXH'], ['i', CJXH]];
		this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].refresh();
	},
	onAfterCellEdit : function(it, record, field, v) {
		var cell = this.grid.getSelectionModel().getSelectedCell();
		var row = cell[0];
		if (it.id == "WZJG") {
			var cm = this.grid.getSelectionModel();
			var r = this.getSelectedRecord();
			if (r.get("CJXH") == this.lastCJXH) {
				return;
			}
			this.lastCJXH = r.get("CJXH");
			var CJXH = +r.get("CJXH");
			this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].requestData.cnd = ['eq',
					['$', 'DXXH'], ['i', CJXH]];
			this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].refresh();
		}
		if (it.id == "LSJG") {
			if (record.get("LSJG") != null && record.get("LSJG") != ""
					&& record.get("WZJG") != null && record.get("WZJG") != "") {
				var jgbl = 0;
				var lsjg = Number(record.get("LSJG"));
				var wzjg = Number(record.get("WZJG"));
				jgbl = ((lsjg - wzjg) / wzjg*100).toFixed(4);
				record.set("JGBL", jgbl);
			}
		}
		if (it.id == "JGBL") {
			if (record.get("LSJG") != null && record.get("LSJG") != ""
					&& record.get("WZJG") != null && record.get("WZJG") != "") {
				var jgbl = Number(record.get("JGBL"));
				var lsjg = 0;
				var wzjg = Number(record.get("WZJG"));
				lsjg = ((wzjg) * (1 + jgbl/100)).toFixed(4);
				record.set("LSJG", lsjg);
			}
		}
	},
	doCreate : function(item, e) {
		phis.application.cfg.script.ManufacturerList.superclass.doCreate.call(this);
		var store = this.grid.getStore();
		var n = store.getCount() - 1
		this.grid.startEditing(n, 1);
	},
	getSaveData : function() {
		// 获取需要保存的数据
		if (this.grid.activeEditor != null) {
			this.grid.activeEditor.completeEdit();
		}
		this.removeEmptyRecord();
		var store = this.grid.getStore();
		var n = store.getCount()
		var data = []
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i)
			var items = this.schema.items
			for (var j = 0; j < items.length; j++) {
				var it = items[j]
				if (it['not-null'] && r.get(it.id) == "") {
					MyMessageTip.msg("提示", it.alias + "不能为空", true)
					return false
				}
			}
			if (r.get('_opStatus') != "create") {
				r.data._opStatus = "update";
			}
			data.push(r.data)
		}
		data = this.removeRecords.concat(data);
		// if (this.removeRecords) {
		// for (var i = 0; i < this.removeRecords.length; i++) {
		// data.push(this.removeRecords[i]);
		// }
		// }
		return data;
	},
	doInsertAfter : function() {
		this.doCreate();
	},
	doRemove : function() {
		var cm = this.grid.getSelectionModel();
		var cell = cm.getSelectedCell();
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		if (r.get("CJXH") == null || r.get("CJXH") == "") {
			this.store.remove(r);
			// 移除之后焦点定位
			if (cell[0] == this.store.getCount()) {
				this.doInsertAfter();
			} else {
				var count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
			}
			return;
		}
		// 如果当前状态为“注销”状态,不能删除。
		if (r.get("SYZT") == "-1") {
			Ext.MessageBox.alert("提示", "该厂家已注销，不能删除！");
			return;
		}
		// 库存帐中存在则也不允许删除；
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "queryIfCanInvalid",
					body : r.data
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
		} else {
			if (ret.json.count != 0) {
				Ext.MessageBox.alert("提示", "该厂家已经在库存帐中使用，不能删除！");
				return;
			};

		}

		Ext.Msg.show({
					title : '确认删除记录[' + r.data.CJXH_text + ']',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.store.remove(r);
							// 移除之后焦点定位
							if (cell[0] == this.store.getCount()) {
								this.doInsertAfter();
							} else {
								var count = this.store.getCount();
								if (count > 0) {
									cm.select(cell[0] < count
													? cell[0]
													: (count - 1), cell[1]);
								}
							}
							if (r.get("_opStatus") != "create") {
								r.set("_opStatus", "remove");
								this.removeRecords.push(r.data);
							}
							this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].requestData.cnd = [
									'eq', ['$', 'DXXH'], ['i', 0]];
							this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].refresh();
							this.onLoadData();
						}
					},
					scope : this
				})
	},
	removeEmptyRecord : function() {
		var store = this.grid.getStore();
		for (var i = 0; i < store.getCount(); i++) {
			var r = store.getAt(i);
			if (r.get("CJXH") == null || r.get("CJXH") == "") {
				store.remove(r);
			}
		}
	},
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'disease',
					totalProperty : 'count',
					id : 'mdssearch_a'
				}, [{
							name : 'numKey'
						}, {
							name : 'JBXH'
						}, {
							name : 'JBMC'

						}, {
							name : 'ICD10'

						}]);
	},
	setBackInfo : function(obj, record) {
		// 将选中的记录设置到行数据中
		var cell = this.grid.getSelectionModel().getSelectedCell();
		var row = cell[0];
		var col = cell[1];
		var griddata = this.grid.store.data;
		_ctx = this;
		var rowItem = griddata.itemAt(row);
		var store = this.grid.getStore();
		var n = store.getCount()
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i)
			if (i != row) {
				if (r.get("ZDXH") == record.get("JBXH")
						&& r.get("ZDLB") == rowItem.get("ZDLB")) {
					MyMessageTip.msg("提示", "\"" + record.get("JBMC")
									+ "\"已存在，请勿重复录入！", true);
					return;
				}
			}
		}
		obj.collapse();
		obj.triggerBlur();
		rowItem.set('ZDXH', record.get("JBXH"));
		rowItem.set('ICD10', record.get("ICD10"));
		obj.setValue(record.get("JBMC"));
		this.grid.startEditing(row, 4);
	},
	doInvalid : function() {
		var r = this.getSelectedRecord();
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "queryIfCanInvalid",
					body : r.data
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
		} else {
			if (ret.json.count == 0) {
				var value = r.get("SYZT") == -1 ? 1 : -1;
				r.set("SYZT", value);
				r.set("SYZT_text", value == 1 ? "在用" : "注销");
			} else {
				Ext.MessageBox.alert("提示", "该厂家已经在库存帐中使用，不能注销！");
			};

		}
	},
	onLoadData : function() {
		var store = this.grid.getStore();
		var n = store.getCount()
		var cjxh = [];
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i)
			cjxh.push(r.get("CJXH"));
		}
		if (cjxh == '') {
			cjxh.push(0);
		}
		if (cjxh.length > 0) {
			this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].requestData.cnd = ['in',['$', 'DXXH'], cjxh];
			this.opener.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].refresh();
		}
	}
});