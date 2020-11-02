$package("phis.application.cfg.script")
$import("phis.script.EditorList")

phis.application.cfg.script.MateriaIitemAliasTab = function(cfg) {
	this.removeRecords = [];
	cfg.autoLoadData = false;
	phis.application.cfg.script.MateriaIitemAliasTab.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.MateriaIitemAliasTab, phis.script.EditorList,
		{
			doCreate : function(item, e) {
				phis.application.cfg.script.MateriaIitemAliasTab.superclass.doCreate
						.call(this);
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
				var n = store.getCount();
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)

					if (r.dirty) {
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
				if (r.get("BMXH") == null || r.get("BMXH") == "") {
					this.store.remove(r);
					// 移除之后焦点定位
					if (cell[0] == this.store.getCount()) {
						this.doInsertAfter();
					} else {
						var count = this.store.getCount();
						if (count > 0) {
							cm.select(cell[0] < count ? cell[0] : (count - 1),
									cell[1]);
						}
					}
					return;
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.data.JBMC + ']',
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
															: (count - 1),
													cell[1]);
										}
									}
									if (r.get("_opStatus") != "create") {
										r.set("_opStatus", "remove");
										this.removeRecords.push(r.data);
									}
								}
							},
							scope : this
						})
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("WZBM") == null || r.get("WZBM") == "") {
						store.remove(r);
					}
				}
			},
			isDirty : function() {
				var dirty = false;
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						dirty = true;
					}
				}
				return dirty;
			},
			refresh : function() {
				this.requestData.cnd = ['eq', ['$', 'WZXH'],
						['i', this.opener.cfg.WZXH]];
				phis.application.cfg.script.MateriaIitemAliasTab.superclass.refresh
						.call(this);
				this.removeRecords = [];
			}
		})
