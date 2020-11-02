$package("phis.application.cic.script")

$import("phis.script.EditorList")

phis.application.cic.script.ClinicPatientAllergyMedList = function(cfg) {
	cfg.remoteUrl = 'MedicineSkinTest';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YPDW}</td>';
	cfg.minListWidth = 250;
	cfg.autoLoadData = false;
	this.showButtonOnTop=true;
	this.removeRecords = [];
	phis.application.cic.script.ClinicPatientAllergyMedList.superclass.constructor
			.apply(this, [cfg])

	this.on("loadData", this.afterLoadData, this);
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.ClinicPatientAllergyMedList,
		phis.script.EditorList, {
			onWinShow : function() {
				if(!this.exContext.ids.brid){
					this.grid.topToolbar.hide();
					return;
				}
				this.requestData.cnd = ['eq', ['$', 'a.BRID'],
						['l', this.exContext.empiData.BRID]];
				this.loadData();
			},
			initPanel : function(sc) {
				var grid = phis.application.cic.script.ClinicPatientAllergyMedList.superclass.initPanel
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
				return grid
			},
			beforeGridEdit : function(it, record, field, value) {
				if (record.get("JGID") != this.mainApp.deptId)
					return false;
			},
			afterLoadData : function() {
				this.doInsertAfter();
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
				store.add([r])
				this.grid.startEditing(this.store.getCount() - 1, 1)
			},
			getSaveData : function() {
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
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
						if (it['not-null'] && r.get(it.id) == "") {
							MyMessageTip.msg("提示", it.alias + "不能为空", true)
							return false
						}
					}
					if (r.get('_opStatus') != "create") {
						r.data._opStatus = "update";
					}
					r.data.BRID = this.exContext.empiData.BRID
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
			doSave : function() {
				var data = this.getSaveData();
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedManageService",
							serviceAction : "savePatientInfo",
							body : {
								"patientAllergyMedTab" : data
							},
							"ZYH" : 0
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "保存成功!", true);
							this.loadData();
						}, this)
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("JGID") != this.mainApp.deptId) {
					MyMessageTip.msg("提示", "不能操作其它机构录入的信息!", true);
					return;
				}
				if (r.get("YPXH") == null || r.get("YPXH") == ""
						|| r.get("YPXH") == 0) {
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
							title : '确认删除记录[' + r.data.YPMC + ']',
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
					if (r.get("YPXH") == null || r.get("YPXH") == ""
							|| r.get("YPXH") == 0) {
						store.remove(r);
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'

								}, {
									name : 'YFGG'

								}, {
									name : 'YPDW'

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
						if (r.get("YPXH") == record.get("YPXH")) {
							MyMessageTip.msg("提示", "\"" + record.get("YPMC")
											+ "\"已存在，请勿重复录入！", true);
							return;
						}
					}
				}
				obj.collapse();
				rowItem.set('YPXH_NEW', record.get("YPXH"));
				if (rowItem.get("_opStatus") == "create") {
					rowItem.set('YPXH', record.get("YPXH"));
				}
				rowItem.set('YPMC', record.get("YPMC"));
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 2);
			}
		});
