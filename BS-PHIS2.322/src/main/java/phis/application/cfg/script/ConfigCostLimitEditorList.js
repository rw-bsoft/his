$package("phis.application.cfg.script")

/**
 * 病人性质维护的费用限制界面list zhangyq 2012.5.25
 */
$import("phis.script.EditorList")

phis.application.cfg.script.ConfigCostLimitEditorList = function(cfg) {
	cfg.autoLoadData = false;
	this.serviceId = "phis.configPatientPropertiesService";
	this.actionId = "saveLimit";
	this.xyyp = [];
	cfg.remoteUrl = "ClinicBasic";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{FYMC}</td><td width="20px">{FYDW}</td>';
	phis.application.cfg.script.ConfigCostLimitEditorList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigCostLimitEditorList,
		phis.script.EditorList, {
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				var body = {
					"BRXZ" : this.mainApp.BRXZ
				};
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "listLimit";
				this.requestData.body = body;
				this.requestData.schema = "GY_FYJY";
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
			},
			afterOpen : function() {
				this.pagingToolbar.on("beforechange", this.beforeStorechange,
						this);
			},
			beforeStorechange : function(pagingToolbar, o) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var save = false;
				if (this.xyyp.length != n && this.xyyp.length != 0) {
					save = true;
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						save = true;
						break;
					}
				}
				if (save) {
					Ext.Msg.show({
								title : '提示',
								msg : '是否保存已作的修改?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										if (this.doSave()) {
											this.xyyp = [];
											pagingToolbar.store.load({
														params : o
													});
										}
									} else {
										this.xyyp = [];
										pagingToolbar.store.load({
													params : o
												});
									}
								},
								scope : this
							})
					return false;
				} else {
					var r = this.getSelectedRecord()
					var n = this.store.indexOf(r)
					if (n > -1) {
						this.selectedIndex = n
					}
					return true;
				}
				return false;
			},
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.ConfigCostLimitEditorList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var this1 = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 2 > count) {
							this1.doInsert();
							/*
							 * this.fireEvent("doNewColumn"); if (e.shiftKey) {
							 * newCell = g.walkCells(ed.row, ed.col - 1, -1,
							 * sm.acceptsNav, sm); } else { newCell =
							 * g.walkCells(ed.row, ed.col + 1, 1, sm.acceptsNav,
							 * sm); } r = newCell[0]; g.startEditing(r, 1);
							 */
							return;
						}
					}

					this.selModel.onEditorKey(field, e);
				}
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						if (this1.xyyp.length == 0) {
							var store = this1.grid.getStore();
							var n = store.getCount()
							if (n > 0) {
								var body = {};
								var data = []
								for (var i = 0; i < n; i++) {
									var r = store.getAt(i)
									this1.xyyp.push(r);
								}
							}
						}
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
				if (this.mainApp.BRXZ) {
					this.requestData.cnd = ['eq', ['$', 'BRXZ'],
							['s', this.mainApp.BRXZ]];
					this.autoLoadData = true;
				}
				return grid
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'clinic',
							totalProperty : 'count',
							id : 'clinicsearch'
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
			doInsert : function(item, e, newGroup) {// 当前记录前插入一条记录
				var store = this.grid.getStore();
				if (this.xyyp.length == 0) {
					var n = store.getCount()
					if (n > 0) {
						for (var i = 0; i < n; i++) {
							var r = store.getAt(i)
							this.xyyp.push(r);
						}
					}
				}
				if (!this.mainApp.BRXZ) {
					MyMessageTip.msg("提示", "请先选择病人性质", true);
					return;
				}
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
				store.insert(0, [r]);
				this.grid.getView().refresh()// 刷新行号
				this.grid.startEditing(0, 1);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("FYXH") == record.get("FYXH")) {
						MyMessageTip.msg("提示", "\"" + record.get("FYMC")
										+ "\"已存在，请进行修改！", true);
						return;
					}
				}
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				rowItem.set('FYDW', record.get("FYDW"));
				rowItem.set('FYXH', record.get("FYXH"));
				rowItem.set('ZFBL', "0");
				rowItem.set('FYXE', "0");
				rowItem.set('CXBL', "0");
				obj.setValue(record.get("FYMC"));
				obj.triggerBlur();
				setTimeout(function() {
							_ctx.grid.startEditing(row, 3);
						}, 100);
			},
			doSave : function(item, e) {
				if (!this.mainApp.BRXZ) {
					MyMessageTip.msg("提示", "请先选择病人性质", true);
					return;
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				var body = {};
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (!r.get('FYXH')) {
						continue;
					}
					r.set('BRXZ', this.mainApp.BRXZ);
					// var o = r.getChanges()
					// var o = {};
					var items = this.schema.items
					var commit = true;
					var notNULL = false;// 没填的项是不是必填的
					var allNull = true;// 全空
					var msg = "保存失败";
					for (var j = 0; j < items.length; j++) {
						var it = items[j]
						if (it.id == 'BRXZ')
							continue;
						if (r.get(it.id) != null) {
							msg = "数据保存失败，本次修改无效！错误行 " + (i + 1) + " 。";
							allNull = false;
							break;
						}
					}
					if (allNull) {
						commit = false;
					} else {
						for (var j = 0; j < items.length; j++) {
							var it = items[j]
							if (it.id == 'BRXZ')
								continue;
							if (r.get(it.id) == null) {
								if (it['not-null']) {
									notNULL = true;
								}
							}
						}
					}
					if (!allNull && notNULL) {
						MyMessageTip.msg("提示", msg, true);
						return;
					}
					r.data['_opStatus'] = 'create';
					if (commit) {
						data.push(r.data)
					}
				}
				body['data'] = data;
				body['fName'] = "BRXZ";
				body['fValue'] = this.mainApp.BRXZ;
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							schema : "GY_FYJY",
							method : "execute",
							body : body
						}, function(code, msg, json) {

							this.grid.el.unmask()
							if (code >= 300) {
								this.xyyp = [];
								this.processReturnMsg(code, msg);
								return;
							}
							var store = this.grid.getStore();
							var n = store.getCount()
							for (var i = 0; i < n; i++) {
								var r = store.getAt(i)
								if (r.dirty) {
									r.commit();
								}
							}
						}, this)
			},
			doRemove : function() {
				if (!this.mainApp.BRXZ) {
					MyMessageTip.msg("提示", "请先选择病人性质", true);
					return;
				}
				var body = {};
				body['BRXZ'] = parseInt(this.mainApp.BRXZ);
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				body['FYXH'] = r.get("FYXH");
				if (!r.get("FYXH")) {
					this.store.remove(r);
					var count = this.store.getCount();// 移除之后焦点定位
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
					return;
				}
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.get("FYMC") + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.grid.el.mask("正在删除数据...",
											"x-mask-loading")
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "removeLimit",
												schema : "GY_FYJY",
												method : "execute",
												body : body
											}, function(code, msg, json) {
												this.grid.el.unmask()
												if (code >= 300) {
													this.processReturnMsg(code,
															msg);
													return;
												}
												this.store.remove(r);
												var count = this.store
														.getCount();// 移除之后焦点定位
												if (count > 0) {
													cm
															.select(
																	cell[0] < count
																			? cell[0]
																			: (count - 1),
																	cell[1]);
												}
											}, this)
								}
							},
							scope : this
						})

			}
		})