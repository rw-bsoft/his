$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.WardPatientDiseaseList = function(cfg) {
	this.removeRecords = [];
	cfg.autoLoadData = false;
	cfg.remoteUrl = 'MedicalDiagnosisZdlr';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="250px">{MSZD}</td></td>';
	cfg.remoteUrlzh = 'MedicalDiagnosisZhlr';
	cfg.remoteTplzh = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{ZHMC}</td></td>';
	cfg.queryParams = {
		"ZXLB" : 1
	};
	cfg.disablePagingTbr=true;
	cfg.minListWidth = 250;
	phis.application.war.script.WardPatientDiseaseList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadData, this);
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.war.script.WardPatientDiseaseList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.war.script.WardPatientDiseaseList.superclass.initPanel
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
				var editor = grid.getColumnModel().getColumnById("ZDLB").editor;
				editor.on("beforeselect", this.zdlbSelect, this);
				var zxlb = grid.getColumnModel().getColumnById("ZXLB").editor;
				// zxlb.on("select", this.onZxlbSelect, this);
				return grid
			},
			afterLoadData : function() {
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					r.set("ZDXH_D", r.get("ZDXH"))
					r.set("ZDLB_D", r.get("ZDLB"))
				}
				this.doInsertAfter();
			},
			// onZxlbSelect : function(f, record, index) {
			// if (index == 0) {
			// this.fireEvent("changeTab", 1);
			// } else if (index == 1) {
			// this.fireEvent("changeTab", 2);
			// }
			// },
			zdlbSelect : function(f, record, index) {
				Ext.EventObject.stopEvent();// 停止事件
				var curR = this.getSelectedRecord();
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r != curR) {
						if (r.get("ZDXH") == curR.get("ZDXH")
								&& record.get("key") == r.get("ZDLB")) {
							MyMessageTip.msg("提示", "\"同一诊断类型下不能录入相同的诊断!", true);
							return false;
						}
						if(record.get("key") == 3 && record.get("key") == r.get("ZDLB")) {
							MyMessageTip.msg("提示", "\"已存在出院主诊断,请确认后重新录入!", true);
							return false;
						}
					}
				}
				return true
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
					if(it.id == 'ZDSJ') {
						data[it.id] = Date.getServerDateTime();
					}
					if(it.id == 'ZDYS') {
						var patientForm = this.opener.midiModules[(this.opener.openBy == "doctor" ? "doc_" : "")
															+ "patientBaseTab"]
						if(patientForm) {
							data[it.id] = patientForm.form.getForm().findField("ZSYS").getValue();
							data[it.id+'_text'] = patientForm.form.getForm().findField("ZSYS").getRawValue();
						}
					}

				}
				var r = new Record(data)
				store.add([r])
				this.grid.startEditing(this.store.getCount() - 1, 2)
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
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("ZDXH") == null || r.get("ZDXH") == ""
						|| r.get("ZDXH") == 0) {
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
							title : '确认删除记录[' + r.data.ZDMC + ']',
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
					if (r.get("ICD10") == null || r.get("ICD10") == ""
							|| r.get("ICD10") == 0) {
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
									name : 'MSZD'

								}, {
									name : 'JBBM'

								}, {
									name : 'JBPB'

								}, {
									name : 'JBPB_text'

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
							MyMessageTip.msg("提示", "\"" + record.get("MSZD")
											+ "\"已存在，请勿重复录入！", true);
							return;
						}
					}
				}
				obj.setValue(record.get("MSZD"));
				obj.collapse();
				obj.triggerBlur();
				rowItem.set('ZDXH', record.get("JBXH"));
				rowItem.set('ICD10', record.get("JBBM"));

				// 疾病判别
				rowItem.set('JBPB', record.get("JBPB"));
				rowItem.set('JBPB_text', record.get("JBPB_text"));
				//

				this.grid.startEditing(row, 4);
			},
			getCM : function(items) {
				var cm = []
				var fm = Ext.form
				var ac = util.Accredit;
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2) || it.noList
							|| it.hidden || !ac.canRead(it.acValue)) {
						continue
					}
					// if(it.length < 80){it.length = 80}//
					// modify by yangl
					var width = parseInt(it.width
							|| (it.length < 80 ? 80 : it.length) || 80)
					var c = {
						id : it.id,
						header : it.alias,
						width : width,
						sortable : this.sortable,
						dataIndex : it.id,
						schemaItem : it
					}
					/** ******************** */
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					// add by yangl,modify simple code Generation methods
					if (it.codeType) {
						if (!this.CodeFieldSet)
							this.CodeFieldSet = [];
						this.CodeFieldSet.push([it.target, it.codeType, it.id]);
					}
					var editable = true;

					if ((it.pkey && it.generator == 'auto') || it.fixed) {
						editable = false
					}
					if (it.evalOnServer && ac.canRead(it.acValue)) {
						editable = false
					}
					var notNull = !(it['not-null'] == 'true')

					var editor = null;
					var dic = it.dic
					if (it.properties && it.properties.mode == "remote") {
						// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
						editor = this.createRemoteDicField(it);
					} else if (it.properties
							&& it.properties.mode == "remotezh") {
						editor = this.createRemoteDicFieldzh(it);
					} else if (dic) {
						dic.src = this.entryName + "." + it.id
						dic.defaultValue = it.defaultValue
						dic.width = width
						if (dic.fields) {
							if (typeof(dic.fields) == 'string') {
								var fieldsArray = dic.fields.split(",")
								dic.fields = fieldsArray;
							}
						}
						if (dic.render == "Radio" || dic.render == "Checkbox") {
							dic.render = ""
						}
						var _ctx = this
						c.isDic = true
						c.renderer = function(v, params, record, r, c, store) {
							var cm = _ctx.grid.getColumnModel()
							var f = cm.getDataIndex(c)
							return record.get(f + "_text")
						}
						if (editable) {
							editor = this.createDicField(dic)
							editor.isDic = true
							var _ctx = this
							c.isDic = true
						}
					} else {
						if (!editable) {
							if (it.type != "string" && it.type != "text"
									&& it.type != "date"
									&& it.type != "datetime") {
								c.align = "right";
								c.css = "color:#00AA00;font-weight:bold;"
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								if (!c.renderer) {
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							cm.push(c);
							continue;
						}
						editor = new fm.TextField({
									allowBlank : notNull
								});
						var fm = Ext.form;
						switch (it.type) {
							// modify by liyunt
							case 'string' :
							case 'text' :
								var cfg = {
									allowBlank : notNull,
									maxLength : it.length
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								if (it.inputType) {
									cfg.inputType = it.inputType
								}
								editor = new fm.TextField(cfg)
								break;
							case 'date' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期",
									format : 'Y-m-d'
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.DateField(cfg)
								break;
							case 'datetime' :
							case 'timestamp' :
							case 'datetimefield' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期"
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new phis.script.widgets.DateTimeField(cfg)
								break;
							case 'double' :
							case 'bigDecimal' :
							case 'int' :
								if (!it.dic) {
									c.css = "color:#00AA00;font-weight:bold;"
									c.align = "right"
									if (it.type == 'double'
											|| it.type == 'bigDecimal') {
										c.precision = it.precision;
										c.nullToValue = it.nullToValue;
										c.renderer = function(value, metaData,
												r, row, col, store) {
											if (value == null
													&& this.nullToValue) {
												value = parseFloat(this.nullToValue)
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												try {
													r.set(this.id, retValue);
												} catch (e) {
													// 防止新增行报错
												}
												return retValue;
											}
											if (value && !isNaN(value)) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												return retValue;
											}
											return value;
										}
									}
								}
								var cfg = {}
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.min) {
									cfg.minValue = it.min;
								} else {
									cfg.minValue = 0;
								}
								if (it.max) {
									cfg.maxValue = it.max;
								}
								cfg.allowBlank = notNull
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.NumberField(cfg)
								break;
						}
					}
					if (editor) {
						editor.enableKeyEvents = true;
						editor.on("keydown", function(f, e) {
									var keyCode = e.getKey();
									if (e.ctrlKey == true) {
										// ctrl+c ctrl+v 等系统快捷键不屏蔽
										// 86, 90, 88, 67, 65
										if (keyCode == 86 || keyCode == 90
												|| keyCode == 88
												|| keyCode == 67
												|| keyCode == 65) {
											return true;
										}
									}
									if (e.ctrlKey
											|| e.altKey
											|| (keyCode >= 112 && keyCode <= 123)) {
										e.preventDefault();// editor需要额外处理全键盘事件
									}

								}, this)
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			},
			createRemoteDicFieldzh : function(it) {
				var mds_reader = this.getRemoteDicReaderzh();
				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonDatazh = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrlzh
					// ,pageSize : this.pageSize || 25,
					// pageNo : 1
				}
				var proxyzh = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonDatazh
						});
				var mdsstore = new Ext.data.Store({
							proxy : proxyzh,
							reader : mds_reader
						});
				proxyzh.on("loadexception", function(proxyzh, o, response, arg,
								e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									MyMessageTip.msg("提示", msg, true)
								}
							} else {
								MyMessageTip
										.msg("提示", "貌似网络不是很给力~请重新尝试!", true)
							}
						}, this)
				this.remoteDicStorezh = mdsstore;
				Ext.apply(this.remoteDicStorezh.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTplzh + '</tr>', '</table>',
						'</div>', '</tpl>');
				var _ctx = this;
				var remoteFieldzh = new Ext.form.ComboBox({
							// id : "YPMC",
							width : 280,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : this.minListWidth || 280,
							tpl : resultTpl,
							minChars : 2,
							enableKeyEvents : true,
							lazyInit : false,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								// onSelect
								// to do
								this.bySelect = true;
								_ctx.setBackzhInfo(this, record);
								// this.hasFocus = false;// add by yangl
								// 2013.9.4
								// 解决新增行搜索时重复调用setBack问题
							}
						});
				remoteFieldzh.on("focus", function() {
							remoteFieldzh.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteFieldzh.on("keyup", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								var searchTypeValue = _ctx.cookie
										.getCookie(_ctx.mainApp.uid
												+ "_searchType");
								if (searchTypeValue != 'BHDM') {
									if (obj.isExpanded()) {
										if (key == 48 || key == 96)
											key = key + 10;
										key = key < 59 ? key - 49 : key - 97;
										var record = this.getStore().getAt(key);
										obj.bySelect = true;
										_ctx.setBackzhInfo(obj, record);
									}
								}
							}
							var recordzxlb = _ctx.getSelectedRecord();
							if (recordzxlb.get("ZXLB") == 2) {
								// 支持翻页
								if (key == 37) {
									obj.pageTb.movePrevious();
								} else if (key == 39) {
									obj.pageTb.moveNext();
								}
							}
							// 删除事件 8
							if (key == 8) {
								if (obj.getValue().trim().length == 0) {
									if (obj.isExpanded()) {
										obj.collapse();
									}
								}
							}
						})
				if (remoteFieldzh.store) {
					remoteFieldzh.store.load = function(options) {
						Ext.apply(_ctx.comboJsonDatazh, options.params);
						Ext.apply(_ctx.comboJsonDatazh, mdsstore.baseParams);
						options = Ext.apply({}, options);
						this.storeOptions(options);
						if (this.sortInfo && this.remoteSort) {
							var pn = this.paramNames;
							options.params = Ext.apply({}, options.params);
							options.params[pn.sort] = this.sortInfo.field;
							options.params[pn.dir] = this.sortInfo.direction;
						}
						try {
							return this.execute('read', null, options); // <--
							// null
							// represents
							// rs. No rs for
							// load actions.
						} catch (e) {
							this.handleException(e);
							return false;
						}
					}
				}
				remoteFieldzh.isSearchField = true;
				remoteFieldzh.on("beforequery", function(qe) {
							this.comboJsonDatazh.query = qe.query;
							// 设置下拉框的分页信息
							// remoteField.pageTb.changePage(0);
							return this.beforeSearchQuery();
						}, this);
				// remoteField.store.on("load",function(store){
				// if(store.getCount() == 1) {
				// this.setBackInfo(remoteField,store.getAt(0));
				// }
				// },this);
				this.remoteDiczh = remoteFieldzh;
				return remoteFieldzh
			},
			getRemoteDicReaderzh : function() {
				return new Ext.data.JsonReader({
							root : 'diseasezh',
							totalProperty : 'count',
							id : 'mdssearchzh_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'ZHBS'
								}, {
									name : 'ZHMC'
								}]);
			},
			setBackzhInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount()
				obj.collapse();
				obj.triggerBlur();
				rowItem.set('ZDBW', record.get("ZHBS"));
				rowItem.set('ZHMC', record.get("ZHMC"))
				obj.setValue(record.get("ZHMC"));
				this.remoteDiczh.lastQuery = "";
				// if (rowItem.get("ZXLB") == 2) {
				// this.doNewClinic();
				// } else {
				this.grid.startEditing(row, 5);
				// }
			},
			beforeGridEdit : function(it, record, field, value) {
				if (it.id == "ZXLB") {
					if (record.get("ZDXH")) {
						return false;
					} else {
						return true;
					}
				}
				if (it.id == "ZDMC") {
					this.remoteDicStore.baseParams = {
						"ZXLB" : record.get("ZXLB")
					}
					this.remoteDic.lastQuery = "";
				}
				if (it.id == "ZHMC") {
					this.remoteDicStorezh.baseParams = {
						"ZXLB" : record.get("ZXLB")
					}
					this.remoteDiczh.lastQuery = "";
					if (record.get("ZDXH")) {
						return true;
					} else {
						return false;
					}
				}
			},
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.wardPatientManageService";
				this.requestData.serviceAction = "queryclinicManageList";
				this.requestData.ZYH=this.ZYH;
// console.debug(this.requestData)
// this.requestData.BRID = this.BRID, this.requestData.JZXH = this.JZXH
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
			onAfterCellEdit : function(it, record, field, v) {
			if(it.id=="ZDSJ"){
				if(v&&v!=null&&v!=""){
				if(new Date(v.replace(/-/g, "/"))>new Date()){
				MyMessageTip.msg("提示", "诊断时间不能大于当前时间", true);
				record.set("ZDSJ","");
				}
				}
			}
			},
			// 刷新,只有点刷新或保存按钮才会清除缓存数据
			doSx : function() {
				this.loadData();
			}
		});
