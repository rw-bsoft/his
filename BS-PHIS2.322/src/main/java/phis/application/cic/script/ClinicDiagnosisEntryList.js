$package("phis.application.cic.script")

$import("phis.script.EditorList", "phis.script.common")

phis.application.cic.script.ClinicDiagnosisEntryList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = 'MedicalDiagnosisZdlr';
	cfg.remoteUrlzh = 'MedicalDiagnosisZhlr';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
	cfg.remoteTplzh = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{ZHMC}</td></td>';
	cfg.queryParams = {
		"ZXLB" : 1
	};
	this.removeByFiled = "ZDMC";
	phis.application.cic.script.ClinicDiagnosisEntryList.superclass.constructor
			.apply(this, [cfg]);
	this.on("afterCellEdit", this.afterGridEdit, this);
	this.on("beforeCellEdit", this.beforeGridEdit, this);
}
var temp = 1;
Ext.extend(phis.application.cic.script.ClinicDiagnosisEntryList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cic.script.ClinicDiagnosisEntryList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 4 > count) {
							_ctr.doNewClinic();
							// if (e.shiftKey) {
							// newCell = g.walkCells(ed.row, ed.col - 1, -1,
							// sm.acceptsNav, sm);
							// } else {
							// newCell = g.walkCells(ed.row, ed.col + 1, 1,
							// sm.acceptsNav, sm);
							// }
							// r = newCell[0];
							// g.startEditing(r, 1);
							return;
						}
					}

					this.selModel.onEditorKey(field, e);
				}
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
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.clinicManageService";
				this.requestData.serviceAction = "queryclinicManageList";
				this.requestData.BRID = this.BRID, this.requestData.JZXH = this.JZXH
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
			ZzbzRenderer : function(value, metaData, r, row, col) {
				// 是否主诊断
				value = row == 0 ? 1 : 0;
				return value == 1
						? "<font style='color:red;font-weight:bold'>主</font>"
						: "";
			},
			numRenderer : function(value, metaData, r, row, col, store) {
				metaData.css = "x-grid3-col x-grid3-cell x-grid3-td-numberer x-grid3-cell-first";
				if (r.data.DEEP > 0)
					return "";
				var num = row + 1// 序号
				var records = store.getRange(0, row);
				for (var i = 0; i < records.length; i++) {
					if (records[i].get('DEEP') > 0)
						num--;
				}
				return num;
			},
			zxzdRenderer : function(value) {
				if (value == 1) {
					return "<font style='color:red;font-weight:bold'>西</font>";
				} else if (value == 2) {
					return "<font style='color:red;font-weight:bold'>中</font>";
				}
			},
			zdmcRenderer : function(value, metaData, r) {
				var deep = r.data.DEEP;
				if (deep > 0) {
					metaData.style = 'text-indent:' + deep * 2 + 'em';
				}
				return value;
			},
			doNewClinic : function() {
				// 插入一条诊断信息
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var data = {
					'_opStatus' : 'create',
					'DEEP' : '0',
					'ZDMC' : '测试诊断1' + temp,
					'ICD10' : 'A001.001' + temp,
					'ZZBZ' : temp,
					'JZSJ' : '2012-06-15 15:30',
					'ZDLB' : '中医',
					'ZDBW' : '手臂'
				}
				var r = new Record(data)
				store.add([r])
				temp++;
				var n = store.getCount() - 1
				this.grid.startEditing(n, 2);
			},
			doSubClinic : function() {
				// 获得当前选中的诊断信息
				var record = this.grid.getSelectionModel().getSelected();
				if (!record) {
					MyMessageTip.msg('警告', '请选中父诊断信息', true);
					return;
				}
				var parentId = record.get("JLBH") ? record.get("JLBH") : record
						.get("ZDXH");
				var row = this.grid.getSelectionModel().lastActive;
				// 获得子诊断插入的行号
				row = this.getSubInsertRow(row, parentId);
				var deep = record.get("DEEP");
				// 插入一条诊断信息
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var data = {
					'_opStatus' : 'create',
					'DEEP' : parseInt(deep) + 1,
					'SJZD' : parentId,
					'ZDMC' : '子诊断1' + temp,
					'ICD10' : 'A002.005' + temp,
					'ZZBZ' : '0',
					'JZSJ' : '2012-06-15 13:30',
					'ZDLB' : '西医',
					'ZDBW' : '手臂'
				}
				var r = new Record(data)
				store.insert(row + 1, [r])
				temp++;
				var n = store.getCount() - 1
				this.grid.startEditing(n, 2);
			},
			getSubInsertRow : function(row, parentId) {
				var records = this.grid.getStore().getRange(row);
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					if (r.get('SJZD') == parentId) {
						row++;
						var next_parentId = r.get("JLBH") ? r.get("JLBH") : r
								.get("ZDXH");
						row = this.getSubInsertRow(row, next_parentId);
					}
				}
				return row;
			},
			doUpClick : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg('警告', '请选中需要上移的诊断记录', true);
					return;
				}
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index == 0) {
					MyMessageTip.msg('警告', '当前诊断记录已是顶级节点', true);
					return;
				}
				var store = this.grid.getStore();
				var deep = record.get("DEEP");// 获得选中行的deep
				var insertRow = null;
				// 向上获取最近一个同deep的记录
				for (var i = index - 1;; i--) {
					if (i < 0 || store.getAt(i).get("DEEP") < deep) {
						MyMessageTip.msg('警告', '当前诊断记录已是顶级节点', true);
						return;
					}
					if (store.getAt(i).get("DEEP") > deep)
						continue;
					insertRow = i;
					break;
				}
				if (insertRow >= 0) {
					// 查找需要移动的记录集合
					var records = [];
					records.push(record);
					var parentId = record.get("JLBH")
							? record.get("JLBH")
							: record.get("ZDXH");
					var children = this.findChildren(parentId, index);
					if (children)
						records = records.concat(children)
					for (var i = 0; i < records.length; i++) {
						store.remove(records[i]);
					}
					for (var i = 0; i < records.length; i++) {
						store.insert(insertRow + i, records[i]);
					}
					// store.insert(insertRow, records[i]);
					this.grid.getView().refresh();
					this.grid.getSelectionModel().select(insertRow, 1);
				}
				this.opener.needSave = true;
			},
			doDownClick : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg('警告', '请选中需要下移的诊断记录', true);
					return;
				}
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				var store = this.grid.getStore();
				var count = store.getCount();
				if (index == count - 1) {
					MyMessageTip.msg('警告', '当前诊断记录已是底级节点', true);
					return;
				}

				var deep = record.get("DEEP");// 获得选中行的deep
				var insertRow = null;
				// 向下获取最近一个同deep的记录
				for (var i = index + 1;; i++) {
					if (i > count - 1 || store.getAt(i).get("DEEP") < deep) {
						MyMessageTip.msg('警告', '当前诊断记录已是底级节点', true);
						return;
					}
					if (store.getAt(i).get("DEEP") > deep)
						continue;
					insertRow = i;
					break;
				}
				if (insertRow) {
					var next_record = store.getAt(insertRow);
					var next_parentId = next_record.get("JLBH") ? next_record
							.get("JLBH") : next_record.get("ZDXH");
					// 获得下移目标诊断的子节点数量
					insertRow = this.getSubInsertRow(insertRow, next_parentId);
					// 查找需要移动的记录集合
					var records = [];
					records.push(record);
					var parentId = record.get("JLBH")
							? record.get("JLBH")
							: record.get("ZDXH");
					var children = this.findChildren(parentId, index);
					if (children)
						records = records.concat(children)
					for (var i = 0; i < records.length; i++) {
						store.remove(records[i]);
					}
					for (var i = 0; i < records.length; i++) {
						store.insert(insertRow + i - records.length + 1,
								records[i]);
					}
					// store.insert(insertRow, records[i]);
					this.grid.getView().refresh();
					this.grid.getSelectionModel().select(
							insertRow - records.length + 1, 1);
				}
				this.opener.needSave = true;
			},
			findChildren : function(parentId, startIndex) {
				var records = [];
				var total_records = this.grid.getStore().getRange(startIndex);
				for (var i = 0; i < total_records.length; i++) {
					var r = total_records[i];
					if (r.get("SJZD") == parentId) {
						records.push(r);
						var next_parentId = r.get("JLBH") ? r.get("JLBH") : r
								.get("ZDXH");
						var children = this.findChildren(next_parentId,
								startIndex + i)
						if (children)
							records = records.concat(children)
					}
				}
				return records;
			},
			doMyCreate : function() {
				if (this.win) {
					this.doNew(["MS_BRZD", "MS_FSZD"]);
					this.win.show();
					this.win.setPosition(300, 80)
					return;
				}
				var module = this.createModule("ClinicDiagnosisEntryForm",
						"CLINIC0301");
				this.module = module;
				this.win = module.getWin();
				this.win.add(module.initPanel());
				this.win.show();
				this.win.setPosition(300, 80)
			},
			doNew : function(entryNames) {
				if (this.data) {
					this.data = {}
				}
				for (var index = 0; index < entryNames.length; index++) {
					var form = this.module.form.getForm()
					var re = util.schema.loadSync(entryNames[index])
					var schema = null;
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
					var items = schema.items
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var f = form.findField(it.id)
						if (f) {
							f.setValue(it.defaultValue)
							if (!it.fixed && !it.evalOnServer) {
								f.enable();
							} else {
								f.disable();
							}
							if (it.type == "date") { // ** add by yzh
								if (it.minValue)
									f.setMinValue(it.minValue)
								if (it.maxValue)
									f.setMaxValue(it.maxValue)
							}
						}
					}
				}
				this.module.form.findById("FSZD_SET").collapse(false);
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

								}, {
									name : 'JBBGK'

								}]);
			},
			createRemoteDicField : function(it) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrl
					// ,pageSize : this.pageSize || 25,
					// pageNo : 1
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonData
						});
				var mdsstore = new Ext.data.Store({
							proxy : proxy,
							reader : mds_reader
						});
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
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
				this.remoteDicStore = mdsstore;
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTpl + '</tr>', '</table>',
						'</div>', '</tpl>');
				var _ctx = this;
				var remoteField = new Ext.form.ComboBox({
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
								_ctx.setBackInfo(this, record);
								// this.hasFocus = false;// add by yangl
								// 2013.9.4
								// 解决新增行搜索时重复调用setBack问题
							}
						});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keyup", function(obj, e) {// 实现数字键导航
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
										_ctx.setBackInfo(obj, record);
									}
								}
							}
							// 支持翻页
							if (key == 37) {
								obj.pageTb.movePrevious();
							} else if (key == 39) {
								obj.pageTb.moveNext();
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
				if (remoteField.store) {
					remoteField.store.load = function(options) {
						Ext.apply(_ctx.comboJsonData, options.params);
						Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
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
				remoteField.isSearchField = true;
				remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							// 设置下拉框的分页信息
							// remoteField.pageTb.changePage(0);
							return this.beforeSearchQuery();
						}, this);
				// remoteField.store.on("load",function(store){
				// if(store.getCount() == 1) {
				// this.setBackInfo(remoteField,store.getAt(0));
				// }
				// },this);
				this.remoteDic = remoteField;
				return remoteField
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
				obj.collapse();
				obj.triggerBlur();
				rowItem.set('ZDMC', record.get("MSZD"));
				rowItem.set('ZDXH', record.get("JBXH"));
				rowItem.set('ICD10', record.get("JBBM"));
				rowItem.set('JBPB', record.get("JBPB"));
				rowItem.set('JBBGK', record.get("JBBGK"));
				rowItem.set('JBPB_text', record.get("JBPB_text"));
				obj.setValue(record.get("MSZD"));
				this.remoteDic.lastQuery = "";
				this.grid.startEditing(row, 5);
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
				rowItem.set('MC', record.get("ZHMC"))
				obj.setValue(record.get("ZHMC"));
				this.remoteDiczh.lastQuery = "";
				if (rowItem.get("ZXLB") == 2) {
					this.doNewClinic();
				} else {
					this.grid.startEditing(row, 6);
				}
			},
			doSave : function() {
				var isFZ=false; //add by rw 2020-10-15 诊断状态是否复诊，当诊断是传染病，但状态为复诊时，不弹出传染病报告卡提示
				this.opener.needSaveCrb = false;
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
				this.removeEmptyRecord();
				this.mask("数据保存中...");
				var store = this.grid.getStore();
				var data = [];
				if (store.getCount() == 0) {
					MyMessageTip.msg('提示', '当前没有需要保存的数据!', true);
					this.unmask();
					return false;
				}
				var crbFlag = 1;//add by lizhi 2018-01-17传染病识别
				var icd10 = null;//add by lizhi 2018-01-31 传染病诊断icd10码
				var flag = 1;
				var list = [];
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					//智能提醒 wy
					list.push(r.json);
					if (r.get("ZDXH") == null || r.get("ZDXH") == "") {
						MyMessageTip.msg('提示', '诊断名称不能为空!错误行：' + (i + 1), true);
						this.unmask();
						return false;
					}
					r.set("CFLX", r.get("ZXLB"));
					if (r.get("ZXLB") != 1 && r.get("ZDBW") == "") {
						MyMessageTip.msg('提示', '证侯不能为空!错误行：' + (i + 1), true);
						this.unmask();
						return false;
					}
					// 状态为复诊 增加发病日期提示
					if (r.get("FZBZ") == '1') {
						var fbrq = r.get("FBRQ") + "";
						var now = this.formate2Date(new Date(), 'yyyy-MM-dd');
						if (fbrq == now) {
							flag = 2;
						}
					}
					if(r.get("JBPB") == '09' || r.get("JBBGK") == '06'){//add by lizhi 2018-01-17传染病识别
						crbFlag = 2;
						icd10 = r.get("ICD10");
						if(r.get("FZBZ") == '1'){
							isFZ=true;
						}
					}
					data.push(r.data);
				}
				if (flag == 2) {
					var flag2 = window.confirm("发病日期与当前日期相同，是否继续?")
					if (!flag2) {
						this.unmask();
						return false;
					}
				}
				//wy 智能提醒写在这里
				try {
					var obj = document.getElementById("DemoActiveX");
					var ip = obj.GetIpAddressAndHostname().split(",")[0];//支付终端IP
					debugger
					phis.script.rmi.jsonRequest({
						serviceId: "clinicDiagnossisService",
						serviceAction: "DiagnosisMsg",
						body: {
							"list": list,
							"KH": this.exContext.empiData.idCard,
							"manageUnit": this.mainApp.deptId,
							"YYKSBM": this.mainApp.departmentId,//科室编码
							"YYYSGH": this.mainApp.uid,//医生工号
							"YSXM": this.mainApp.uname,//医生姓名
							"ip": ip

						}
					}, function (code, msg, json) {
						console.log("处方提醒:" + code);

					}, this)// jsonRequest
				} catch (e) {
					console.log(e);
				}
				
				var jzxh = this.exContext.ids.clinicId;
				var brid = this.exContext.ids.brid;
				this.opener.needSave = false;
				var saveData = data;
				var me = this;
				phis.script.rmi.jsonRequest({
							serviceId : "clinicDiagnossisService",
							serviceAction : "saveDiagnossis",
							body : {
								"jzxh" : jzxh,
								"brid" : brid,
								"dignosisList" : data
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return false;
							}
							this.fireEvent("onDiagnosisSave");
							// 加上延迟,防止太快执行,参数没有完全加载
							var d = new Ext.util.DelayedTask(function(){
                                // @@----社区建档案任务提示-----------s----------
                                if (json.JBPB) {
									var JBPB = json.JBPB.JBPB;
									this.CHISJDRWTS(JBPB, saveData, me);
								}
                                // @@===============-----------e----------
                            },this);
                            d.delay(1000);
                            debugger;
							if (crbFlag == 2 && json.JBPB.MS_BRZD_JLBH!=""&&isFZ!=true) {//add by lizhi 2018-01-17传染病识别
								this.CRBBGK(json.JBPB.MS_BRZD_JLBH,icd10);
							}
							debugger;
						}, this)// jsonRequest
				// 向社区增加疾病史
				//this.CHISAddJBS(store);
				return true;
			},
			CRBBGK : function(MS_BRZD_JLBH,ICD10){
				var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
				if(param!="1"){
					return;
				}
				this.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH || '';
				this.exContext.args.ICD10 = ICD10 || '';
				var _this = this;
				//根据门诊诊断记录编号查询是否已保存传染病报告卡
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryIdrReport",
							body : {
								"MS_BRZD_JLBH" : MS_BRZD_JLBH,
								"EMPIID" : this.exContext.empiData.empiId,
								"ICD10" : ICD10
							}
						});
				if(resData.code > 200){
					return;
				}
				if(resData.json.result){
					var isSaved = resData.json.result.isSaved;
					if(typeof(isSaved)!="undefined" && !isSaved){
						_this.opener.needSaveCrb = true;
						Ext.Msg.show({
							title : '确认',
							msg : '该诊断为传染病诊断，是否填写传染病报告卡？',
							modal : false,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									_this.popBgkWin();
								}
							},
							scope : this
						});
					}
				}
			},
			popBgkWin : function(){
				this.midiModules["crbBgkModule"]=null;
				var crbBgkmodule= this.createModule("crbBgkModule", "phis.application.cic.CIC/CIC/CIC0403");
				this.crbModule = crbBgkmodule;
				Ext.apply(this.crbModule.exContext, this.exContext);
				crbBgkmodule.opener = this;
				var win = crbBgkmodule.getWin();
				win.add(crbBgkmodule.initPanel());
				win.setWidth(1000);
				win.setHeight(550);
				win.show();
				win.center();
			},
			CHISJDRWTS : function(JBPB, saveData, me) {// 社区建档案任务提示,执行

				if (!this.SFQYGWXT) {
					var publicParam = {
						"commons" : ['SFQYGWXT']
					}
					this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
				}
				if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
					var GXY = false;
					var TNB = false;
					var CRB = false;
					var XXG = false;
					var THQAll = ["03", "04", "05", "06", "07", "08"];
					var THQ = [];
					var isTHQ = false;
					for (var ij = 0; ij < saveData.length; ij++) {
						// 如果是传染病报卡就去判断
						if (saveData[ij].JBBGK == '06') {
							CRB = true;
						}
						// 如果是心脑血管报卡就去判断
						if (saveData[ij].JBBGK == '09') {
							var result = util.rmi.miniJsonRequestSync({
										serviceId : "chis.CommonService",
										serviceAction : "saveDiseaseManagement",
										method : "execute",
										body : {
											empiId : me.exContext.empiData.empiId,
											mzhm : me.exContext.empiData.MZHM,
											jzjg : me.mainApp.deptId,
											jbzd : saveData[ij].ICD10,
											zdmc : saveData[ij].ZDMC,
											mzys : me.mainApp.uid
										}
									})
							if (result.code < 300) {
								// 28天已经没有报卡
								if (result.json.status == 0) {
									XXG = true;
								}
							} else {
								this.processReturnMsg(result.code, result.msg);
							}
						}

					}

					if (JBPB) {
						if (JBPB.indexOf('01') >= 0) {
							GXY = true;
						}
						if (JBPB.indexOf('02') >= 0) {
							TNB = true;
						}
						for (var ti = 0, tiLen = THQAll.length; ti < tiLen; ti++) {
							var bm = THQAll[ti];
							if (JBPB.indexOf(bm) >= 0) {
								THQ.push(bm);
								isTHQ = true;
								break;
							}
						}
						if (JBPB.JBBGK) {
							if (JBPB.JBBGK.indexOf("06") >= 0) {
								CRB = true;
							}
							if (JBPB.JBBGK.indexOf("09") >= 0) {
								XXG = true;
							}
						}
					}
					// 判断病人诊断，是否要创建高血压，糖尿病档案及做肿瘤问题
					util.rmi.miniJsonRequestAsync({
								serviceId : "chis.chisRecordFilter",
								serviceAction : "getCDMsgInfo",
								method : "execute",
								body : {
									empiId : this.exContext.empiData.empiId,
									JZXH : '' + this.exContext.ids.clinicId,
									GXY : GXY,
									TNB : TNB,
									THQ : THQ,
									isTHQ : isTHQ,
									CRB : CRB,
									XXG : XXG
								}
							}, function(code, msg, json) {
								if (code < 300) {
									var body = json.body;
									if (body) {
										this.openNodes = body.openNodes || '';
										var isOpenTHQ = body.isOpenTHQ || false;
										this.empiId = body.empiId;
										if (isOpenTHQ) {
											var CHISTHQModule = this
													.createModule(
															"CHIS_THQ_Win",
															"chis.application.diseasemanage.DISEASEMANAGE/TR/THQM");
											if (!this.exContext.args) {
												this.exContext.args = {};
											}
											this.exContext.args.masterplateTypes = body.mtList;
											this.exContext.args.empiId = this.exContext.ids.empiId;
											this.exContext.args.MS_BRZD_JLBH = this.JLBH
													|| "";
											this.exContext.args.JZXH = this.exContext.ids.clinicId
													|| "";
											Ext.apply(CHISTHQModule.exContext,
													this.exContext);
											CHISTHQModule.initPanel();
											var CHIS_THQ_Win = CHISTHQModule
													.getWin();
											CHIS_THQ_Win.show();
										}
										var rsMsgList = body.rsMsgList;
										var len = rsMsgList.length;
										if (len > 0) {
											var ts = "";
											ts = "<table id='chisJDTS_Table'><tbody style='font-size:15px;font-weight:bold;color:#ff0000;'>";
											for (var i = 0; i < len; i++) {
												ts += "<tr><td>" + rsMsgList[i] + "</td></tr>";
											}
											ts += "</tbody></table>";
											ymPrompt.confirmInfo({
												message : ts,
												title : "您有待办社区业务",
												winPos : 'c',
												okTxt : "执行",
												cancelTxt : "退出",
												scope : this,
												hashcode : "jcbryztztx",
												showMask : false,
												useSlide : true,
												width : 300,
												height : 160,
												handler : this.CHISJDTSBtnClick,
												autoClose : false
											});
										}
									}
								} else {
									this.processReturnMsg(code, msg);
								}
							}, this);

				}
			},
			CHISJDTSBtnClick : function(sign) {
				if (sign == "cancel" || sign == "close") {
					ymPrompt.doHandler('doClose', true);
				} else if (sign == "ok") {
					this.fireEvent("openPagesOfCHIS", this.openNodes,
							this.empiId);
				}
			},
			CHISAddJBS : function(store) {// 向社区增加疾病史
				// 与公卫业务联动开始，疾病添加到个人既往史中
				// 获取 是否启用公卫系统的参数
				if (!this.SFQYGWXT) {
					var publicParam = {
						"commons" : ['SFQYGWXT']
					}
					this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
				}
				// 如果存在公卫系统，将疾病保存到公卫个人既往史中
				if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
					var jblbs = {
						'01' : '0202',
						'02' : '0203',
						'09' : '0298',
						'10' : '0204',
						'11' : '0205',
						'12' : '0206',
						'13' : '0207',
						'14' : '0208',
						'15' : '0209',
						'16' : '0210',
						'17' : '0211',
						'18' : '0212',
						'19' : '0213',
						'20' : '0214'
					}
					var brempiid = this.exContext.empiData.empiId;
					var nowDate = (new Date()).format('Y-m-d');
					var jbsRecords = [];// 需要保存的疾病史记录集
					// var jblbHistory={};
					var jbmcHistory = {};
					// 每条记录循环
					var ssize = store.getCount();
					for (var i = 0; i < ssize; i++) {
						var grecord = store.getAt(i).data;
						// 如果存在疾病判别 (过滤掉没有选则疾病类别的疾病)
						if (grecord.JBPB) {
							// 疾病判别存在多选的情况 如'01,02','高血压,糖尿病'
							var jbbms = grecord.JBPB.split(',');
							var jbmcs = grecord.JBPB_text.split(',');

							for (var j = 0; j < jbbms.length; j++) {
								var gmywlb = '0299';
								var gmywlbtext = '其他';
								if (jblbs.hasOwnProperty(jbbms[j]))
									gmywlb = jblbs[jbbms[j]];
								if (jbmcs[j]) {
									gmywlbtext = jbmcs[j];
								}
								var jbsRecord = {
									empiId : brempiid,
									pastHisTypeCode_text : '疾病史',
									pastHisTypeCode : '02',
									methodsCode : '',
									protect : '',
									diseaseCode : gmywlb,
									diseaseText : gmywlbtext,
									vestingCode : '',
									startDate : '',
									endDate : '',
									confirmDate : nowDate,
									recordUnit : this.mainApp.deptId,
									recordUser : this.mainApp.uid,
									recordDate : nowDate,
									lastModifyUser : this.mainApp.uid,
									lastModifyUnit : this.mainApp.deptId,
									lastModifyDate : nowDate
								}
								if (!jbmcHistory.hasOwnProperty(gmywlbtext)) {
									jbsRecords.push(jbsRecord);
									jbmcHistory[gmywlbtext] = gmywlbtext;
								}
							}
						}

					}

					if (jbsRecords.length > 0) {
						var comreq1 = util.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecordService",
							serviceAction : "savePastHistoryHis",
							schema : 'chis.application.hr.schemas.EHR_PastHistory',
							op : 'create',
							body : {
								empiId : brempiid,
								record : jbsRecords,
								delPastId : []
							}
						});
						if (comreq1.code != 200) {
							this.processReturnMsg(comreq1.code, comreq1.msg);
							return;
						} else {
						}

					}

				}
				// 与公卫业务联动结束
			},
			saveGYXYSAfterSaveTNBYS : function(btn) {
				var gxyys = "0";
				if (btn == 'ok') {
					gxyys = "1";
				}
				if (btn == 'cancel') {
					gxyys = "0";
				}
				this.fireEvent("happenGYXYSEvent", gxyys);
				// 再处理TNB
				Ext.Msg.show({
							title : '提示',
							msg : '诊断中有糖尿病类别疾病，是否 确认 为 糖尿病？',
							buttons : {
								"ok" : "确诊",
								"cancel" : "疑似"
							},
							closable : false,
							fn : this.saveTNBYS,
							animEl : 'elId',
							icon : Ext.MessageBox.QUESTION,
							scope : this
						});
			},
			saveGYXYS : function(btn) {
				var gxyys = "0";
				if (btn == 'ok') {
					gxyys = "1";
				}
				if (btn == 'cancel') {
					gxyys = "0";
				}
				this.fireEvent("happenGYXYSEvent", gxyys);
			},
			saveTNBYS : function(btn) {
				var tnbys = "0";
				if (btn == 'ok') {
					tnbys = "1";
				}
				if (btn == 'cancel') {
					tnbys = "0";
				}
				this.fireEvent("happenTNBYSEvent", tnbys);
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index < this.store.getCount() - 1) {
					if (this.store.getAt(index + 1).get("DEEP") > r.get("DEEP")) {
						MyMessageTip.msg('警告', '当前诊断信息存在子诊断，不允许删除!', true);
						return;
					}
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						title += r.get(this.schema.pkeys[i])
					}
				}
				// add by liyl 2012-06-17 提示信息增加名称显示功能
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
									this.processRemove();
								}
							},
							scope : this
						})
			},
			processRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				this.mask("删除数据中...");
				if (r.get("JLBH")) {
					phis.script.rmi.jsonRequest({
								serviceId : "clinicDiagnossisService",
								serviceAction : "removeDiagnossis",
								body : {
									"jlbh" : r.get("JLBH")
								}
							}, function(code, msg, json) {
								if (code > 300) {
									this.processReturnMsg(code, msg, this
													.doRemove())
									return
								}
								this.fireEvent("onDiagnosisRemove");
							}, this)// jsonRequest
				}
				this.unmask();
				this.store.remove(r)
				this.grid.getView().refresh();
				if (this.store.getCount() == 0) {
					this.doNewClinic();
				} else {
					var n = this.store.getCount() - 1
					this.grid.startEditing(n, 2);
				}

				// 与公卫业务联动开始，过敏药品添加到个人既往史中
				if (!this.SFQYGWXT) {
					var publicParam = {
						"commons" : ['SFQYGWXT']
					}
					this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
					this.SFQYGWXT = true;
				}
				if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
					// 如果要删除掉的疾病存在疾病类别
					if (r.data.JBPB) {

						var jblbs = {
							'01' : '0202',
							'02' : '0203',
							'09' : '0298',
							'10' : '0204',
							'11' : '0205',
							'12' : '0206',
							'13' : '0207',
							'14' : '0208',
							'15' : '0209',
							'16' : '0210',
							'17' : '0211',
							'18' : '0212',
							'19' : '0213',
							'20' : '0214'
						}
						var brempiid = this.exContext.empiData.empiId;
						var nowDate = (new Date()).format('Y-m-d');

						var delRecords = [];

						// 疾病判别存在多选的情况 如'01,02','高血压,糖尿病'
						var rjbbms = r.data.JBPB.split(',');
						var rjbmcs = r.data.JBPB_text.split(',');

						// 循环所有记录查看是否还存在此种疾病，如果过存在则不删除
						var ssize = this.store.getCount();
						for (var k = 0; k < rjbbms.length; k++) {
							var flag = true;
							for (var i = 0; i < ssize; i++) {
								var grecord = this.store.getAt(i).data;
								if (grecord.JLBH && grecord.JLBH != r.data.JLBH) {

									// 疾病判别存在多选的情况 如'01,02','高血压,糖尿病'
									var jbbms = grecord.JBPB.split(',');
									var jbmcs = grecord.JBPB_text.split(',');
									for (var j = 0; j < jbbms.length; j++) {
										if (rjbbms[k] == jbbms[j]
												&& rjbmcs[k] == jbmcs[j]) {
											flag = false;
										}

									}
								}
							}
							if (flag) {
								var gmywlb = '0299';
								var gmywlbtext = '其他';
								if (jblbs.hasOwnProperty(rjbbms[k]))
									gmywlb = jblbs[rjbbms[k]];
								if (rjbmcs[k]) {
									gmywlbtext = rjbmcs[k];
								}
								var delRecord = {
									empiId : brempiid,
									pastHisTypeCode : '02',
									ysid : this.mainApp.uid,
									diseaseCode : gmywlb,
									diseaseText : gmywlbtext
								}
								delRecords.push(delRecord);
							}
						}
						if (delRecords.length > 0) {
							// 删除本人当天操作的个人既往史

							var comreq1 = util.rmi.miniJsonRequestSync({
										serviceId : "chis.CommonService",
										serviceAction : "delPastHistory",
										body : {
											empiId : brempiid,
											record : delRecords
										}
									});
							if (comreq1.code != 200) {
								this
										.processReturnMsg(comreq1.code,
												comreq1.msg);
								return;
							} else {
							}

						}

					}
				}
				// 与公卫业务联动结束
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.fireEvent("onDiagnosisRemove");
			},
			doClose : function() {
				var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
				if(this.opener.needSaveCrb && param == "1"){//add by lizhi at 2018-01-24传染病填写传染病报告卡
					var _this = this;
					Ext.Msg.alert("提示", "传染病诊断未填写传染病报告卡,请先填写！",function(){_this.popBgkWin();});
					return;
				}
				if (this.opener.win) {
					this.opener.win.hide();
				} else {
					this.opener.opener.cardWin.hide();
				}
			},
			formate2Date : function(x, y) {
				var z = {
					M : x.getMonth() + 1,
					d : x.getDate(),
					h : x.getHours(),
					m : x.getMinutes(),
					s : x.getSeconds()
				};
				y = y.replace(/(M+|d+|h+|m+|s+)/g, function(v) {
					return ((v.length > 1 ? "0" : "") + eval('z.' + v.slice(-1)))
							.slice(-2)
				});
				return y.replace(/(y+)/g, function(v) {
							return x.getFullYear().toString().slice(-v.length)
						});
			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == "FBRQ") {
					if (record.get("FBRQ") == "NaN-NaN-NaN") {
						record.set("FBRQ", new Date().format('Y-m-d'));
					}
				}
			},
			onReady : function() {
				phis.application.cic.script.ClinicDiagnosisEntryList.superclass.onReady
						.call(this);
				var zxlb = this.grid.getColumnModel().getColumnById("ZXLB").editor;
				zxlb.on("select", this.onZxlbSelect, this);
			},
			onZxlbSelect : function(f, record, index) {
				if (index == 0) {
					this.fireEvent("changeTab", 1);
				} else if (index == 1) {
					this.fireEvent("changeTab", 2);
				}
			},
			setZXLB : function(newTab) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				if (cell) {
					var row = cell[0];
					var griddata = this.grid.store.data;
					var rowItem = griddata.itemAt(row);
					if (!rowItem.get("ZDXH")) {
						if (newTab.id == "WestQuickInput") {
							rowItem.set('ZXLB', 1);
							rowItem.set('ZXLB_text', "西医");
						} else if (newTab.id == "CenterQuickInput") {
							rowItem.set('ZXLB', 2);
							rowItem.set('ZXLB_text', "中医");
						}
					}
				}
			},
			beforeGridEdit : function(it, record, field, value) {
				if (it.id == "ZXLB") {
					if (record.get("ZDXH")) {
						return false;
					} else {
						return true;
					}
				}
				// if (it.id == "ZDBW") {
				// if (record.get("ZXLB") == 2) {
				// var filters = "['eq',['$','item.properties.JBBS'],['i',0]]";
				// if(record.get("ZDXH")){
				// filters = "['eq',['$','item.properties.JBBS'],['l',"
				// + record.get("ZDXH")
				// + "]]";
				// }
				// field.store.removeAll();
				// field.store.proxy = new Ext.data.HttpProxy({
				// method : "GET",
				// url : util.dictionary.SimpleDicFactory.getUrl({
				// id : "phis.dictionary.diseaseZyCode",
				// filter : filters
				// })
				// })
				// field.store.load()
				// } else {
				// field.store.removeAll();
				// field.store.proxy = new Ext.data.HttpProxy({
				// method : "GET",
				// url : util.dictionary.SimpleDicFactory.getUrl({
				// id : "phis.dictionary.position"
				// })
				// })
				// field.store.load()
				// }
				// }
				if (it.id == "ZDMC") {
					this.remoteDicStore.baseParams = {
						"ZXLB" : record.get("ZXLB")
					}
					this.remoteDic.lastQuery = "";
				}
				if (it.id == "MC") {
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
			}
		});