$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.WardAppendProjectList = function(cfg) {
	this.removeRecords = [];
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.remoteUrl = 'Clinic';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="140px">{FYMC}</td><td>{FYDW}</td>';
	cfg.minListWidth = 250;
	cfg.queryParams = {
		useType : "ZYSY"
	}
	cfg.disablePagingTbr = true;
	phis.application.cfg.script.WardAppendProjectList.superclass.constructor.apply(this,
			[cfg])
	this.on("loadData", this.afterLoadData, this);
}
Ext.extend(phis.application.cfg.script.WardAppendProjectList, phis.script.EditorList,
		{
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.WardAppendProjectList.superclass.initPanel
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
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
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
				var editor = grid.getColumnModel().getColumnById("KSDM").editor;
				editor.store.on("load", this.departmentLoad, this);
				editor.on("beforeselect", this.ksdmSelect, this);
				return grid
			},
			departmentLoad : function(store) {
				var data = {
					"key" : 0,
					"text" : "全部科室",
					"PYDM" : "QBBQ"
				};
				var r = new Ext.data.Record(data);
				store.insert(0, r);
			},
			afterLoadData : function() {
				if (this.store.getCount() == 0) {
					this.doInsertAfter();
				}
				this.store.each(function(r) {
							if (r.get("KSDM") === 0) {
								r.set("KSDM_text", "全部科室");
							}
						})
				this.store.commitChanges();
			},
			ksdmSelect : function(f, record, index) {
				Ext.EventObject.stopEvent();// 停止事件
				var curR = this.getSelectedRecord();
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r != curR) {
						if (r.get("GLXH") == curR.get("GLXH")
								&& (record.get("key") == r.get("KSDM")
										|| r.get("KSDM") === 0 || record
										.get("key") === 0)) {
							MyMessageTip.msg("提示", "\"已存在该附加项目!", true);
							return false;
						}
					}
				}
				return true
			},
			fymcRender : function(value, params, r, row, col, store) {
				if (r.get("FYDW")) {
					return value + "/" + r.get("FYDW");
				}
				return value;
			},
			doInsertAfter : function() {
				this.doCreate();
			},
			doCreate : function(item, e) {
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							data[it.id] = v.key;
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v.key];
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
					if (it.type && it.type == "int") {
						data[it.id] = (data[it.id] == "0" || data[it.id] == "" || data[it.id] == undefined)
								? 0
								: parseInt(data[it.id]);
					}

				}
				var r = new Record(data)
				r.set("XMXH", this.XMXH);
				r.set("JGID", this.mainApp['phisApp'].deptId);
				r.set("KSDM", 0);
				r.set("KSDM_text", "全部科室");
				store.add([r])
				this.grid.startEditing(this.store.getCount() - 1, 1)
			},
			doSave : function() {
				// 获取需要保存的数据
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					var items = this.schema.items
					for (var j = 0; j < items.length; j++) {
						var it = items[j]
						if (it['not-null'] && r.get(it.id) === "") {
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
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardPatientManageService",
							serviceAction : "saveAddProjects",
							method : "execute",
							body : data
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return false;
				}
				this.removeRecords = [];
				MyMessageTip.msg("提示", "保存成功!", true)
				this.store.rejectChanges();
				this.refresh();
				return true;
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("GLXH") == null || r.get("GLXH") == ""
						|| r.get("GLXH") == 0) {
					this.store.remove(r);
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
					return;
				}
				Ext.Msg.show({
					title : "确认删除附加项目【" + r.data.FYMC + "/" + r.data.FYDW + "】",
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.store.remove(r);
							// 移除之后焦点定位
							var count = this.store.getCount();
							if (count > 0) {
								cm.select(cell[0] < count
												? cell[0]
												: (count - 1), cell[1]);
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
					if (r.get("GLXH") == null || r.get("GLXH") == ""
							|| r.get("GLXH") == 0) {
						store.remove(r);
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'clinic',
							totalProperty : 'count'
						}, [{
									name : 'numKey'
								}, {
									name : 'FYXH'
								}, {
									name : 'FYMC'

								}, {
									name : 'FYDW'
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
						if (r.get("GLXH") == record.get("FYXH")
								&& (r.get("KSDM") == rowItem.get("KSDM")
										|| r.get("KSDM") === 0 || rowItem
										.get("KSDM") === 0)) {
							MyMessageTip.msg("提示", "\"" + record.get("FYMC")
											+ "\"已存在，请勿重复录入！", true);
							return;
						}
					}
				}
				rowItem.set('GLXH', record.get("FYXH"));
				obj.setValue(record.get("FYMC") + "/" + record.get("FYDW"));
				obj.collapse();
				obj.triggerBlur();
				this.grid.startEditing(row, 2);
			}
		});
