$package("phis.script")

$import("phis.script.SimpleList", "util.dictionary.DictionaryLoader",
		"phis.script.widgets.MyEditorGrid", "phis.script.widgets.SpinnerField",
		"phis.script.widgets.DatetimeField",
		"phis.script.cookie.CookieOperater")

phis.script.EditorList = function(cfg) {
	this.selectOnFocus = false;
	this.summaryable = false;
	this.cookie = util.cookie.CookieOperater;
	this.remoteUrl = 'Medicine';
	this.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="100px">({YBFL_text}){YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td>';
	this.sortable = true;// add by yangl 增加是否启用排序参数
	cfg.gridCreator = util.widgets.MyEditorGrid
	phis.script.EditorList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.script.EditorList, phis.script.SimpleList, {
	initPanel : function(sc) {
		if (this.mutiSelect) {
			this.sm = new Ext.grid.CheckboxSelectionModel()
		}
		var grid = phis.script.EditorList.superclass.initPanel.call(this, sc)
		grid.on("afteredit", this.afterCellEdit, this)
		grid.on("beforeedit", this.beforeCellEdit, this)
		grid.on("doNewColumn", this.doInsertAfter, this)
		return grid
	},
	doRemove : function() {
		var cm = this.grid.getSelectionModel();
		var cell = cm.getSelectedCell();
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		this.store.remove(r);
		this.grid.getView().refresh();
		// 移除之后焦点定位
		var count = this.store.getCount();
		if (count > 0) {
			cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
		}
	},
	beforeCellEdit : function(e) {
		var f = e.field
		var record = e.record
		var op = record.get("_opStatus")
		var cm = this.grid.getColumnModel()
		var c = cm.config[e.column]
		var enditor = cm.getCellEditor(e.column)
		var it = c.schemaItem
		var ac = util.Accredit;
		if (op == "create") {
			if (!ac.canCreate(it.acValue)) {
				return false
			}
		} else {
			if (!ac.canUpdate(it.acValue)) {
				return false
			}
		}
		// add by yangl 回写未修改下的中文名称
		e.value = e.value || ""
		return this.fireEvent("beforeCellEdit", it, record, enditor.field,
				e.value)

	},
	afterCellEdit : function(e) {
		var f = e.field
		var v = e.value
		var record = e.record
		var cm = this.grid.getColumnModel()
		var enditor = cm.getCellEditor(e.column, e.row)
		var c = cm.config[e.column]
		var it = c.schemaItem
		var field = enditor.field
		if (it.dic) {
			record.set(f + "_text", field.getRawValue())
		}
		if (it.type == "date") {
			var dt = new Date(v)
			v = dt.format('Y-m-d')
			record.set(f, v)
		}
		if (it.codeType)
			record.set(f, v.toUpperCase())
		if (this.CodeFieldSet) {
			var bField = {};
			for (var i = 0; i < this.CodeFieldSet.length; i++) {
				var CodeField = this.CodeFieldSet[i];
				var target = CodeField[0];
				var codeType = CodeField[1];
				var srcField = CodeField[2];
				if (it.id == target) {
					if (!bField.codeType)
						bField.codeType = [];
					bField.codeType.push(codeType);
					if (!bField.srcField)
						bField.srcField = [];
					bField.srcField.push(srcField);
				}
			}
			if (bField.srcField) {
				var body = {};
				body.codeType = bField.codeType;
				body.value = v;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "codeGeneratorService",
							serviceAction : "getCode",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.saveToServer,
							[saveData]);
					return;
				}
				for (var i = 0; i < bField.codeType.length; i++) {
					record.set(bField.srcField[i], eval('ret.json.body.'
									+ bField.codeType[i]));
				}
			}
		}
		// add by yangl 回写未修改下的中文名称
		// if(field.isSearchField) {
		// var patrn=/^[a-zA-Z0-9]+$/;
		// if((v.trim()=="" || patrn.exec(v)) && this.beforeSearchQuery()) {
		// record.set(f,record.modified[f]);
		// }else {
		// record.modified[f] = v;
		// }
		// }
		if (field.isSearchField) {// 远程查询字段不允许修改
			if (!field.bySelect) {
				record.set(f, record.modified[f]);
			}
			field.bySelect = false;
		}
		this.fireEvent("afterCellEdit", it, record, field, v)
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
			if ((it.display <= 0 || it.display == 2) || it.noList || it.hidden
					|| !ac.canRead(it.acValue)) {
				continue
			}
			// if(it.length < 80){it.length = 80}//
			// modify by yangl
			var width = parseInt(it.width || (it.length < 80 ? 80 : it.length)
					|| 80)
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
							&& it.type != "date" && it.type != "datetime") {
						c.align = "right";
						c.css = "color:#00AA00;font-weight:bold;"
						c.precision = it.precision;
						c.nullToValue = it.nullToValue;
						if (!c.renderer) {
							c.renderer = function(value, metaData, r, row, col,
									store) {
								if (value == null && this.nullToValue) {
									value = parseFloat(this.nullToValue)
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
									try {
										r.set(this.id, retValue);
									} catch (e) {
										// 防止新增行报错
									}
									return retValue;
								}
								if (value != null) {
									value = parseFloat(value);
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
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
							if (it.type == 'double' || it.type == 'bigDecimal') {
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								c.renderer = function(value, metaData, r, row,
										col, store) {
									if (value == null && this.nullToValue) {
										value = parseFloat(this.nullToValue)
										var retValue = this.precision
												? value.toFixed(this.precision)
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
												? value.toFixed(this.precision)
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
										|| keyCode == 88 || keyCode == 67
										|| keyCode == 65) {
									return true;
								}
							}
							if (e.ctrlKey || e.altKey
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
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : 'mdssearch'
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
						}, {
							name : 'YPSL'
						}, {
							name : 'JLDW'
						}, {
							name : 'YPJL'
						}, {
							name : 'PSPB'
						},// 判断是否皮试药品
						{
							name : 'YFBZ'
						}, {
							name : 'GYFF'
						},// 药品用法
						{
							name : 'LSJG'
						}, {
							name : 'YPCD'
						}, {
							name : 'CDMC'
						}, {
							name : 'TYPE'
						}, {
							name : 'TSYP'
						}, {
							name : 'YFDW'
						}, {
							name : 'YBFL'
						}, {
							name : 'YBFL_text'
						}, {
							name : 'GYFF_text'
						}, {
							name : 'JYLX'
						}, {
							name : 'KCSL'
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
						var json = eval("(" + response.responseText + ")")
						if (json) {
							var code = json["code"]
							var msg = json["msg"]
							MyMessageTip.msg("提示", msg, true)
						}
					} else {
						MyMessageTip.msg("提示", "貌似网络不是很给力~请重新尝试!", true)
					}
				}, this)
		this.remoteDicStore = mdsstore;
		Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + this.remoteTpl + '</tr>', '</table>', '</div>',
				'</tpl>');
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
					maxLength : it.length,
					enableKeyEvents : true,
					lazyInit : false,
					itemSelector : 'div.search-item',
					onSelect : function(record) { // override default onSelect
						// to do
						this.bySelect = true;
						_ctx.setBackInfo(this, record);
						// this.hasFocus = false;// add by yangl 2013.9.4
						// 解决新增行搜索时重复调用setBack问题
					}
				});
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);
		remoteField.on("keyup", function(obj, e) {// 实现数字键导航
					var key = e.getKey();
					if ((key >= 48 && key <= 57) || (key >= 96 && key <= 105)) {
						var searchTypeValue = _ctx.cookie
								.getCookie(_ctx.mainApp.uid + "_searchType");
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
					return this.execute('read', null, options); // <-- null
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
		remoteField.onMouseDown = function(e) {
			if (e.button == 2) { // 右键
				var ed = _ctx.grid.activeEditor || _ctx.grid.lastActiveEditor;
				_ctx.onContextMenu(grid, ed.row, e);
			}
		}
		// remoteField.store.on("load",function(store){
		// if(store.getCount() == 1) {
		// this.setBackInfo(remoteField,store.getAt(0));
		// }
		// },this);
		this.remoteDic = remoteField;
		return remoteField
	},
	beforeSearchQuery : function() {
		return true;
	},
	setBackInfo : function(obj, record) {
		// 将选中的记录设置到行数据中，继承后实现具体功能
	},
	getStoreFields : function(items) {
		var o = phis.script.EditorList.superclass.getStoreFields.call(this,
				items)
		o.fields.push({
					name : "_opStatus"
				})
		return o
	},
	createDicField : function(dic) {
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Simple";
		} else {
			cls += dic.render
		}
		cls += "DicFactory"
		$import(cls)
		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)

		return field
	},
	// update by caijy on 2012-05-30 for dic defaultValue失效的问题
	doCreate : function(item, e) {
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
			if (it.defaultValue != undefined) {
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
		try {
			store.add([r])
			this.grid.getView().refresh();
		} catch (e) {
			store.removeAll();// 解决处方录入模块双击插入操作报错问题
		}

	},
	doSave : function(item, e) {
		var store = this.grid.getStore();
		var n = store.getCount()
		var data = []
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i)
			if (r.dirty) {
				var o = r.getChanges()
				o[this.schema.pkey] = r.id
				o['_opStatus'] = r.get('_opStatus')
				data.push(o)
				continue
			}
			var items = this.schema.items
			for (var j = 0; j < items.length; j++) {
				var it = items[j]
				if (it['not-null'] && r.get(it.id) == "") {
					return
				}
			}
			if (r.get('_opStatus') == "create") {
				data.push(r.data)
			}
		}
	},
	doAction : function(item, e) {
		var cmd = item.cmd
		var script = item.script
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	},
	getSelectedRecord : function(muli) { // ** modify by yzh 2011-01-05
		try {
			var selectModel = this.grid.getSelectionModel();
			if (!this.mutiSelect) {
				var cell = selectModel.getSelectedCell()
				return this.grid.store.getAt(cell[0])
			} else {
				if (muli) {
					return selectModel.getSelections();
				} else {
					return selectModel.getSelected();
				}
			}
		} catch (e) {

		}
	}
});
