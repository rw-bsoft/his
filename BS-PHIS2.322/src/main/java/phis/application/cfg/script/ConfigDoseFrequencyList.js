$package("phis.application.cfg.script")
$import("phis.script.EditorList")
phis.application.cfg.script.ConfigDoseFrequencyList = function(cfg) {
	cfg.selectOnFocus = true;
	phis.application.cfg.script.ConfigDoseFrequencyList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.afterGridEdit, this)
}
Ext.extend(phis.application.cfg.script.ConfigDoseFrequencyList,
		phis.script.EditorList, {
			init : function() {
				this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						})
				this.requestData = {
					serviceId : "configDoseFrequencyService",
					schema : "phis.application.cfg.schemas.GY_SYPC",
					serviceAction : "listQuery",
					cnd : this.cnds,
					method : "execute",
					pageSize : this.pageSize > 0 ? this.pageSize : 0,
					pageNo : 1
				}
				if (this.serverParams) {
					Ext.apply(this.requestData, this.serverParams)
				}
				if (this.autoLoadSchema) {
					this.getSchema();
				}
			},
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.ConfigDoseFrequencyList.superclass.initPanel
						.call(this, sc)
				grid.onEditorKey = function(field, e) {
					if (field.needFocus) {
						field.needFocus = false;
						ed = this.activeEditor;
						if (!ed) {
							ed = this.lastActiveEditor;
						}
						this.startEditing(ed.row, ed.col);
						return;
					}
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 1 >= (count - 1) && !this.editing) {
							this.fireEvent("doNewColumn");
							return;
						}
					}
					this.selModel.onEditorKey(field, e);
				}
				var sm = grid.getSelectionModel();
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
			doInsertAfter : function() {
				this.doCreate();
			},
			onReady : function() {
				phis.script.EditorList.superclass.onReady.call(this);
				var PCMC = this.grid.colModel.getColumnById("PCMC").editor;
			},
			doCreate : function(item, e) {
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if (selectdRecord.get("PCMC") == null
							|| selectdRecord.get("PCMC") == ""
							|| selectdRecord.get("PCMC") == 0) {
						selectRow = selectRow;
					} else {
						selectRow = this.store.indexOf(selectdRecord) + 1;
					}
				} else {
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
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
							var o = factory.load(dic);
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
				var r = new Record(data);
				store.insert(row, [r])
				this.grid.getView().refresh()// 刷新行号
				var storeData = store.data;
				this.grid.startEditing(row, 1);
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("PCMC") == null || r.get("PCMC") == ""
							|| r.get("PCMC") == 0) {
						store.remove(r);
					}
				}
			},
			afterGridEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var r = this.store.getAt(i);
						var r2 = this.store.getAt(i + 1);
						if (it.id == "PCMC"
								&& (r.get("PCMC") == v.toLowerCase() || r
										.get("PCMC") == v.toUpperCase())) {
							MyMessageTip.msg("提示",
									"本条频次名称和第" + (i + 1) + "重复！", true);
							record.set("PCMC", "");
							this.grid.startEditing(curRow, 1);
							return false;
						}
						if(r2){
							if (r2.get("RLZ") == 1 && r2.get("ZXZQ") != 7) {
								record.set("ZXZQ", 7);
								this.grid.startEditing(curRow, 4);
							}
						}
					}
				}
				return true;
			},
			doRemoveFrequency : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var title = r.id;
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			},
			processRemove : function(r) {
				this.mask("在正删除数据...");
				var body = [];
				body.push(r.data);
				phis.script.rmi.jsonRequest({
							serviceId : "configDoseFrequencyService",
							serviceAction : "removeFrequency",
							method : "execute",
							body : body
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.refresh();
								return true;
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
			doCommit : function() {
				this.mask();
				this.removeEmptyRecord();
				var body = [];
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.data.ZXSJ == null || r.data.ZXSJ == "") {
						Ext.Msg.alert("提示", "请先维护执行时间");
						this.unmask();
						return;
					}
					if (r.data.RZXZQ == null || r.data.RZXZQ == "") {
						Ext.Msg.alert("提示", "请先维护频次周率");
						this.unmask();
						return;
					}
					if (r.data.ZXZQ != r.data.RZXZQ.length) {
						Ext.Msg.alert("提示", "频次【" + r.data.PCMC
										+ "】的最小周期和周期天数不一致！");
						this.unmask();
						return;
					}
					if (r.data.PCMC.length > 10) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行,频次名称长度过长");
						this.unmask();
						return;
					}
					body.push(r.data);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configDoseFrequencyService",
							serviceAction : "saveCommit",
							method : "execute",
							body : body
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				this.unmask();
				if (code < 300) {
					MyMessageTip.msg("提示", "保存成功!", true);
					this.refresh();
					return true;
				} else {
					this.processReturnMsg(code, msg);
					return false;
				}
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("PCMC") == null || r.get("PCMC") == ""
							|| r.get("PCMC") == 0) {
						store.remove(r);
					}
				}
			},
			// 执行时间维护exectime
			doExectime : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					Ext.Msg.alert("提示", "请选择行!");
					return;
				}
				var ZXSJ = r.data.ZXSJ;
				this.exectimeModule = this.createModule("exectimeModule",
						this.exectimeRef);
				this.exectimeModule.ZXSJ = ZXSJ;
				this.exectimeModule.on("doSave", this.onExectimeSave, this);
				// 重新加载form表单中的数据
				var win = this.exectimeModule.getWin();
				this.exectimeModule.win = win;
				win.add(this.exectimeModule.initPanel());
				win.show();
				win.center();
//				this.exectimeModule.doNew();
			},
			onExectimeSave : function(values) {
				var r = this.getSelectedRecord();
				var MRCS = r.data.MRCS;
				var items = this.exectimeModule.schema.items;
				var time = "";
				var time1 = "";
				var MRCS1 = 0;
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (values[it.id] == true) {
						time1 = document.getElementById("input_sj" + i).value;
						var index = time1.substring(0, 2);
						var index1 = time1.substring(2, 3);
						var index2 = time1.substring(3, 5);
						if(time1.length !=5){
							Ext.Msg.alert("提示", "数据格式不正确！");
							return;
						}
						if (index < 0 || index > 23) {
							Ext.Msg.alert("提示", "数据格式不正确！");
							return;
						}
						if (index1 != ':') {
							Ext.Msg.alert("提示", "数据格式不正确！");
							return;
						}
						if (index2 < 0 || index2 > 59) {
							Ext.Msg.alert("提示", "数据格式不正确！");
							return;
						}
						if (time == "") {
							time += time1;
							MRCS1++;
						} else {
							time += "-" + time1;
							MRCS1++;
						}
					}
				}
				if (MRCS != MRCS1) {
					Ext.Msg.alert("提示", "每日次数和所选时间不一致！");
					return;
				}
				r.set("ZXSJ", time);
				this.exectimeModule.doCancel();
			},

			// 频次周率维护
			doFrequency : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					Ext.Msg.alert("提示", "请选择行!");
					return;
				}
				var RZXZQ = r.data.RZXZQ;
				var RLZ = r.data.RLZ;
				var ZXZQ = r.data.ZXZQ;
				if (!RLZ && RLZ != 0) {
					Ext.Msg.alert("提示", "请选择日历周");
					return;
				}
				this.frequencyModule = this.createModule("frequencyModule",
						this.frequencyNORef);
				this.frequencyModule.selectRecord = r;
				this.frequencyModule.on("doSave", this.frequencyNOSave, this);
				var frequencyView=this.frequencyModule.initPanel();
				this.frequencyModule.grid;
				var win = this.frequencyModule.getWin();
				this.frequencyModule.win = win;
				win.add(frequencyView);
				this.frequencyModule.doNew();
				for (var i = 0; i < ZXZQ; i++) {
					var f_date = this.frequencyModule.form.getForm()
							.findField("date" + (i + 1));
					var f_xq = this.frequencyModule.form.getForm()
							.findField("z" + (i + 1));
					if (RLZ == 1) {
						f_xq.show();
						f_date.hide();
					} else {
						f_date.show();
						for (var j = 0; j < 7; j++) {
							this.frequencyModule.form.getForm().findField("z"
									+ (j + 1)).hide();
						}
					}
				}
				for (var i = ZXZQ; i < 30; i++) {
					this.frequencyModule.form.getForm().findField("date"
							+ (i + 1)).hide();
				}
				// 日历周 打开页面显示已选择过的钩钩
				if (RZXZQ && RLZ == 1) {
					for (var i = 0; i < RZXZQ.length; i++) {
						var RZXZQ_index = RZXZQ.substring(i, i + 1);
						if (RZXZQ_index == 1) {
							this.frequencyModule.form.getForm().findField("z"
									+ (i + 1)).setValue(true);
						}
					}
				}
				// 非日历周显示的钩钩
				if (RZXZQ && RLZ == 0) {
					for (var i = 0; i < RZXZQ.length; i++) {
						var RZXZQ_index = RZXZQ.substring(i, i + 1);
						if (RZXZQ_index == 1) {
							this.frequencyModule.form.getForm()
									.findField("date" + (i + 1)).setValue(true);
						}
					}
				}		
				win.show();
				win.center();	
			},
			frequencyNOSave : function(values) {
				var r = this.getSelectedRecord();
				var items = this.frequencyModule.schema.items;
				var unm = "";
				var n = 0;
				var length = r.data.ZXZQ;
				if (r.data.RLZ == 1) {
					for (var i = 30; i < 37; i++) {
						var it = items[i];
						if (values[it.id] == true) {
							unm += '1';
							n++
						}
						if (values[it.id] == false) {
							unm += '0';
						}
					}
				} else {
					for (var i = 0; i < length; i++) {
						var it = items[i];
						if (values[it.id] == true) {
							unm += '1';
							n++
						}
						if (values[it.id] == false) {
							unm += '0';
						}
					}
				}
				if (n == 0) {
					Ext.Msg.alert("提示", "请选择周期!");
					return;
				}
				r.set("RZXZQ2", n);
				r.set("RZXZQ", unm);
				this.frequencyModule.doCancel();
			}

		})
