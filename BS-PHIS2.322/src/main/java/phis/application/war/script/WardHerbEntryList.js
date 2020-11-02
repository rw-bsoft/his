$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.WardHerbEntryList = function(cfg) {
	cfg.remoteUrl = 'Herb';
	cfg.minListWidth = 510;
	// cfg.remoteTpl = this.getRemoteTpl();
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td>'
			+ '<td width="160px" title="{YPMC}">({YBFL_text}){YPMC}</td><td width="70px">{YFGG}</td>'
			+ '<td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td>'
			+ '<td width="50px">{KCSL}</td>';
	cfg.autoLoadData = false;
	// cfg.listServiceId = "wardAdviceQuery";
	this.serviceId = "wardPatientManageService";
	cfg.selectOnFocus = true;
	cfg.disablePagingTbr = true;
	cfg.sortable = false;
	cfg.enableHdMenu = false;
	cfg.showButtonOnTop = false;
	// this.serviceId = "wardPatientManageService";
	phis.application.war.script.WardHerbEntryList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.afterGridEdit, this);
}
Ext.extend(phis.application.war.script.WardHerbEntryList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.war.script.WardHerbEntryList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						// console.debug("count",count)
						// console.debug("cell",cell)
						if (cell[1] + 3 > count) {
							_ctr.doCreate();
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
			afterGridEdit : function(it, record, field, v) {
				if ((it.id == 'SRCS'||it.id == "YCJL") && record.get("YPJL")) {
					this.opener.needSave = true;
					var cfts = this.opener.form.form.getForm().findField("CFTS").getValue();
					if(record.get("YCJL")){
						record.set("YCSL",
										parseFloat(parseFloat(record.get("YCJL"))
												/ (parseFloat(record.get("YPJL")) * parseFloat(record
														.get("YFBZ")))*parseFloat(cfts)).toFixed(2));
						this.checkInventory(record);
					}
				}
				if (it.id == 'SRCS' && v > record.get("MRCS")) {
					MyMessageTip.msg("提示", "首日次数不能超过频次的每日次数"
									+ record.get("MRCS") + "!", true)
					var cell = this.grid.getSelectionModel()
							.getSelectedCell();
					this.grid.startEditing(cell[0], 5);
					return;
				}
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = "phis.wardPatientManageService";
				this.requestData.serviceAction = "queryHerbInfo";
				this.requestData.ZYH = this.opener.initDataId;
				this.requestData.YZZH = this.opener.data.YZZH;
//				this.requestData.cnd = ['and',['eq',['$', 'a.ZYH'], ['i', this.opener.initDataId]],['eq', ['$', 'a.YZZH'], ['i', this.opener.data.YZZH]]];
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load();
					} else {
						var pt = this.grid.getBottomToolbar();
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor);
					}
				}
				// ** add by yzh **
			},
			doCreate : function(item, e) {
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if (selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0) {
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
					store.insert(row, [r])
				} catch (e) {
					store.removeAll();// 解决处方录入模块双击插入操作报错问题
				}
				this.grid.startEditing(row, 1);
				this.onRowClick();
				
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("YPXH") == null || r.get("YPXH") == "" || (r
							.get("YPXH") == 0)) {
						store.remove(r);
					}
				}
			},
			beforeSearchQuery : function() {
				// 判断当前行是否满足同组输入
				var r = this.getSelectedRecord();
				if (!r)
					return false;
				if(this.remoteDicStore){
					if(this.remoteDicStore.baseParams){
						this.remoteDicStore.baseParams.YFSZ = this.opener.YFSZ;
					}else{
						this.remoteDicStore.baseParams = {};
						this.remoteDicStore.baseParams.YFSZ = this.opener.YFSZ;
					}
				}else{
					this.remoteDicStore = {};
					this.remoteDicStore.baseParams = {};
					this.remoteDicStore.baseParams.YFSZ = this.opener.YFSZ;
				}
				this.remoteDic.lastQuery = "";
				return true;
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
									name : 'YZMC'
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
									name : 'PSPB'
								}, {
									name : 'TSYP'
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
									name : 'YPGG'
								}, {
									name : 'KCSL'
								}, {// 以下为项目部分
									name : 'FYXH'
								}, {
									name : 'FYMC'
								}, {
									name : 'FYDW'
								}, {
									name : 'XMLX'
								}, {
									name : 'FYDJ'
								}, {
									name : 'FYGB'
								}, {
									name : 'FYKS'
								}, {
									name : 'FYKS_text'
								}, {
									name : 'YPLX'
								}, {
									name : 'FYFS'
								}, {
									name : 'FYFS_text'
								}, {
									name : 'YJSY'
								},// 抗菌药物
								{
									name : 'KSBZ'
								}, {
									name : 'YQSYFS'
								}, {
									name : 'YCYL'
								}, {
									name : 'KSSDJ'
								}, {
									name : 'SFSP'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// console.debug("setBackInfo",record)
				// 将选中的记录设置到行数据中
				this.opener.needSave = true;
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
//				var col = cell[1];
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
											+ "\"在这组中已存在，请进行修改!", true);
							return false;
						}
					}
				}
				obj.collapse();
				rowItem.set('YPXH', record.get("YPXH"));
				var YZMC = record.get("YZMC")
							+ (record.get("YFGG")
									? "/" + record.get("YFGG")
									: "")
							+ (record.get("YFDW")
									? "/" + record.get("YFDW")
									: "")
				rowItem.set('YZMC', YZMC);
				rowItem.set('JLDW', record.get("JLDW"));
				rowItem.set('YFDW', record.get("YFDW"));
				rowItem.set('YPJL', record.get("YPJL"));
				rowItem.set('YFGG', record.get("YFGG"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('YPDJ', record.get("LSJG"));
				rowItem.set('YPLX', 3);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPCD_text', record.get("CDMC"));
				rowItem.set("FYFS", record.get("FYFS"));
				rowItem.set("FYFS_text", record.get("FYFS_text"));

				obj.setValue(YZMC);
				var tab = this.opener.opener.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				var openerList = this.opener.opener.tabModule.midiModules[tab.id];
				var sypc = openerList.grid.getColumnModel().getColumnById("SYPC").editor;
				var sypc_rec = sypc.findRecord("key",  this.opener.form.form.getForm().findField("SYPC").getValue());
				if(sypc_rec){
					rowItem.set("MRCS",sypc_rec.get("MRCS"));
					rowItem.set("YZZXSJ",sypc_rec.get("ZXSJ"));
					rowItem.set("SRCS",sypc_rec.get("MRCS"));
					rowItem.set("SYPC_text",sypc_rec.get("text"));
				}
				rowItem.set("SYPC",this.opener.form.form.getForm().findField("SYPC").getValue());
				rowItem.set("YFSB",this.exContext.yfxx.bqcyf)
				rowItem.set("YFSB_text",this.exContext.yfxx.bqcyf_text);
				if(rowItem.get("YPDJ")&&rowItem.get("YCJL")){
					var cfts = this.opener.form.form.getForm().findField("CFTS").getValue();
					rowItem.set("YCSL",
									parseFloat(parseFloat(rowItem.get("YCJL"))
											/ (parseFloat(rowItem.get("YPJL")) * parseFloat(rowItem
													.get("YFBZ")))*parseFloat(cfts)).toFixed(2));
					this.checkInventory(rowItem);
				}
				this.grid.startEditing(row, 2);
			},
			setCFTS : function(cfts){
				var store = this.grid.getStore();
				var n = store.getCount();
				for(var i = 0 ; i < n ; i ++){
					var rowItem = store.getAt(i);
					if(rowItem.get("YPDJ")&&rowItem.get("YCJL")){
						rowItem.set("YCSL",
								parseFloat(parseFloat(rowItem.get("YCJL"))
										/ (parseFloat(rowItem.get("YPJL")) * parseFloat(rowItem
												.get("YFBZ")))*parseFloat(cfts)).toFixed(2));
						this.checkInventory(rowItem);
					}
				}
			},
			setMRCS : function(sypc_v){
				var store = this.grid.getStore();
				var n = store.getCount();
				var tab = this.opener.opener.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				var openerList = this.opener.opener.tabModule.midiModules[tab.id];
//				if(openerList.grid.getColumnModel().getColumnById("SYPC")){
					var sypc = openerList.grid.getColumnModel().getColumnById("SYPC").editor;
					var sypc_rec = sypc.findRecord("key",  sypc_v);
					for(var i = 0 ; i < n ; i ++){
						var rowItem = store.getAt(i);
						rowItem.set("MRCS",sypc_rec.get("MRCS"));
						rowItem.set("YZZXSJ",sypc_rec.get("ZXSJ"));
						rowItem.set("SRCS",sypc_rec.get("MRCS"));
						rowItem.set("SYPC",sypc_v);
						rowItem.set("SYPC_text",sypc_rec.get("text"));
						var cfts = this.opener.form.form.getForm().findField("CFTS").getValue();
						rowItem.set("YCSL",
										parseFloat(parseFloat(rowItem.get("YCJL"))
												/ (parseFloat(rowItem.get("YPJL")) * parseFloat(rowItem
														.get("YFBZ")))*parseFloat(cfts)).toFixed(2));
						this.checkInventory(rowItem);
					}
//				}
			},
			checkInventory : function(record) {
				var YYZL = record.get("YCSL");
				if (YYZL == null || YYZL == 0)
					return;
				var SRCS = record.get("SRCS");
				var pharmId = record.get("YFSB");
				var data = {};
				var medId = record.get("YPXH");
				if (!record.get("SYPC") || !medId || !pharmId || !SRCS) {
					return;
				}
				data.medId = medId;
				data.medsource = record.get("YPCD");
				data.quantity = parseFloat(YYZL) * parseFloat(SRCS);
				data.pharmId = pharmId;
				data.ypmc = record.get("YZMC");
				data.lsjg = record.get("YPDJ");
				if (!data.quantity)
					return;
				// 校验是否有足够药品库存
				var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "checkInventory",
					body : data
				});
				if (resData.code >= 300) {
					this.processReturnMsg(resData.code, resData.msg);
					return false;
				}
				if (resData.json.sign <= 0) {
					var msg = "药品【" + data.ypmc
					+ "】库存不足!库存数量："
					+ (resData.json.KCZL || 0) + ",实际数量："
					+ data.quantity;
					MyMessageTip.msg("警告", msg, true);
					record.set("msg",msg);
					record.set("SIGN", -1);
				} else {
					record.set("msg","");
					record.set("SIGN", 1);
				}
				// 校验是否有足够药品库存
//				phis.script.rmi.jsonRequest({
//							serviceId : this.serviceId,
//							serviceAction : "checkInventory",
//							body : data
//						}, function(code, mmsg, json) {
//							if (code >= 300) {
//								this.processReturnMsg(code, mmsg);
//								return;
//							}
//							if (json.sign <= 0) {
//								var msg = "药品【" + data.ypmc
//								+ "】库存不足!库存数量："
//								+ (json.KCZL || 0) + ",实际数量："
//								+ data.quantity;
//								MyMessageTip.msg("警告", msg, true);
//								record.set("msg",msg);
//								record.set("SIGN", -1);
//							} else {
//								record.set("msg","");
//								record.set("SIGN", 1);
//							}
//
//						}, this)
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
										c.itemId = it.id;
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
											if (value != null) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												if (this.itemId == "YCJL") {
													if (r.get("SIGN") == -1) {
														return "<font color='red'>"
																+ retValue
																+ "</font>";
													}
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
						editor.on("keydown", this.onShortcutKey, this)
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			}
		});
