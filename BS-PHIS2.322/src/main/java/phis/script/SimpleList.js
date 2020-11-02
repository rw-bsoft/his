$package("phis.script")

$import("app.modules.list.SimpleListView", "phis.script.common",
		"phis.script.widgets.Spinner", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.DatetimeField",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync")

phis.script.SimpleList = function(cfg) {
	this.showRowNumber = true;
	this.winState = null;
	this.group = null;// 提供分组功能
	this.groupTextTpl = '{text} ({[values.rs.length]} 条记录)';
	this.summaryable = false;// 统计信息
	this.sortable = true;// add by yangl 增加是否启用排序参数
	this.menuDisabled = false; // add by yangl 禁用grid下拉菜单
	this.enableHdMenu = true; // add by yangl 增加是否显示头部下拉按钮
	cfg.createCls = cfg.createCls || "phis.script.TableForm";
	cfg.updateCls = cfg.updateCls || "phis.script.TableForm";
	cfg.pageSize = 100;
	Ext.apply(cfg, phis.script.common);// modify by yangl
	// 方法改为applyIf,并且直接赋值到this属性
	phis.script.SimpleList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.script.SimpleList, app.modules.list.SimpleListView, {
	init : function() {
		this.addEvents({
					"gridInit" : true,
					"beforeLoadData" : true,
					"loadData" : true,
					"loadSchema" : true
				})
		this.requestData = {
			serviceId : this.listServiceId,
			method : this.listMethod,
			schema : this.entryName,
			cnd : this.initCnd,
			pageSize : (!this.disablePagingTbr && this.pageSize > 0) ? this.pageSize : 0,
			pageNo : 1
		}
		if (this.serverParams) {
			Ext.apply(this.requestData, this.serverParams)
		}
		this.fixRequestData(this.requestData);
		if (this.autoLoadSchema) {
			this.getSchema();
		}
	},
	initPanel : function(sc) {
		if (this.grid) {
			if (!this.isCombined) {
				this.fireEvent("beforeAddToWin", this.grid)
				this.addPanelToWin();
			}
			return this.grid;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.isCompositeKey = schema.compositeKey;
		var items = schema.items
		if (!items) {
			return;
		}
		this.store = this.getStore(items)
		// if (this.mutiSelect) {
		// this.sm = new Ext.grid.CheckboxSelectionModel()
		// }
		if (this.lockingGrid) {
			$styleSheet("phis.resources.css.ext.ux.LockingGridView")
			$import("phis.script.ux.LockingGridView")
			this.cm = new Ext.ux.grid.LockingColumnModel(this.getCM(items))
		} else {
			this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		}
		var cfg = {
			border : false,
			store : this.store,
			cm : this.cm,
			height : this.height,
			loadMask : {
				msg : '正在加载数据...',
				msgCls : 'x-mask-loading'
			},
			buttonAlign : 'center',
			clicksToEdit : 1,
			frame : true,
			plugins : this.rowExpander,
			viewConfig : {
				// forceFit : true,
				enableRowBody : this.enableRowBody,
				getRowClass : this.getRowClass
			}
		}
		if (this.sm) {
			cfg.sm = this.sm
		}
		if (this.viewConfig) {
			Ext.apply(cfg.viewConfig, this.viewConfig)
		}
		if (this.group) {
			cfg.view = new Ext.grid.GroupingView({
						// forceFit : true,
						showGroupName : true,
						enableNoGroups : false,
						hideGroupedColumn : true,
						enableGroupingMenu : false,
						columnsText : "表格字段",
						groupByText : "使用当前字段进行分组",
						showGroupsText : "表格分组",
						groupTextTpl : this.groupTextTpl
					});
		}
		if (this.lockingGrid) {
			cfg.view = new Ext.ux.grid.LockingGridView();
		}
		if (this.gridDDGroup) {
			cfg.ddGroup = this.gridDDGroup;
			cfg.enableDragDrop = true
		}
		if (this.summaryable) {
			$import("phis.script.ux.GridSummary");
			var summary = new org.ext.ux.grid.GridSummary();
			cfg.plugins = [summary]
			this.summary = summary;
		}
		var cndbars = this.getCndBar(items)
		if (!this.disablePagingTbr) {
			cfg.bbar = this.getPagingToolbar(this.store)
		} else {
			cfg.bbar = this.bbar
		}
		if (!this.showButtonOnPT) {
			if (this.showButtonOnTop) {
				cfg.tbar = (cndbars.concat(this.tbar || [])).concat(this
						.createButtons())
			} else {
				cfg.tbar = cndbars.concat(this.tbar || [])
				cfg.buttons = this.createButtons()
			}
		}
		this.expansion(cfg);// add by yangl
		this.grid = new this.gridCreator(cfg)
		// this.grid.getTopToolbar().enableOverflow = true
		this.grid.on("render", this.onReady, this)
		this.grid.on("contextmenu", function(e) {
					e.stopEvent()
				})
		this.grid.on("rowcontextmenu", this.onContextMenu, this)
		this.grid.on("rowdblclick", this.onDblClick, this)
		this.grid.on("rowclick", this.onRowClick, this)
		this.grid.on("keydown", function(e) {
					if (e.getKey() == e.PAGEDOWN) {
						e.stopEvent()
						this.pagingToolbar.nextPage()
						return
					}
					if (e.getKey() == e.PAGEUP) {
						e.stopEvent()
						this.pagingToolbar.prevPage()
						return
					}
				}, this)

		if (!this.isCombined) {
			this.fireEvent("beforeAddToWin", this.grid)
			this.addPanelToWin();
		}
		return this.grid
	},
	onReady : function() {
		if (this.showBtnOnLevel) {
			var otherTbar = new Ext.Toolbar(this.createButtons("two"));
			this.otherTbar = otherTbar;
			this.grid.getView().showBtnOnLevel = true;
			this.grid.add(otherTbar)
			this.grid.doLayout();
			this.grid.getView().refresh();
		}
		if (this.autoLoadData == '0' || this.autoLoadData == 'false') {
			this.autoLoadData = false;
		}
		// phis.script.SimpleList.superclass.onReady.call(this);
		if (this.autoLoadData) {
			this.loadData();
		}
		var el = this.grid.el
		if (!el) {
			return
		}
		var actions = this.actions
		if (!actions) {
			return
		}
		var keyMap = new Ext.KeyMap(el)
		keyMap.stopEvent = true

		// index btns
		var btnAccessKeys = {}
		var keys = []
		if (this.showButtonOnTop) {
			if (this.grid.getTopToolbar()) {
				var btns = this.grid.getTopToolbar().items || [];
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					var key = btn.accessKey
					if (key) {
						btnAccessKeys[key] = btn
						keys.push(key)
					}
				}
			}
		} else {
			var btns = this.grid.buttons || []
			for (var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				var key = btn.accessKey
				if (key) {
					btnAccessKeys[key] = btn
					keys.push(key)
				}
			}
		}
		this.btnAccessKeys = btnAccessKeys
		// keyMap.on(keys, this.onAccessKey, this)
		keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)
	},
	expansion : function() {

	},
	getStoreFields : function(items) {
		var fields = []
		var ac = util.Accredit;
		var pkey = [];
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			var f = {}
			if (it.pkey) {
				pkey.push(it.id)
			}
			f.name = it.id
			switch (it.type) {
				case 'date' :
					// f.type = "date"
					break;
				case 'int' :
					f.type = "int"
					f.useNull = true
					break
				case 'double' :

				case 'bigDecimal' :
					f.type = "float"
					f.useNull = true
					break
				case 'string' :
					f.type = "string"
			}
			fields.push(f)
			if (it.dic) {
				fields.push({
							name : it.id + "_text",
							type : "string"
						})
			}
		}
		this.fireEvent("getStoreFields", fields); // **此处可修改列表控件
		return {
			pkey : pkey,
			fields : fields
		}
	},
	getStore : function(items) {
		var o = this.getStoreFields(items)
		var readCfg = {
			root : 'body',
			totalProperty : 'totalCount',
			fields : o.fields
		}
		if (!this.isCompositeKey) {
			readCfg.id = o.pkey;
		}
		var reader = new Ext.data.JsonReader(readCfg)
		var url = ClassLoader.serverAppUrl || "";
		// add by yangl 请求统一加前缀
		if (this.requestData && this.requestData.serviceId
				&& this.requestData.serviceId.indexOf(".") < 0) {
			this.requestData.serviceId = 'phis.' + this.requestData.serviceId
		}
		var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'POST',
					jsonData : this.requestData,
					timeout : this.timeout || 30000
				})
		proxy.on("loadexception", function(proxy, o, response, arg, e) {
					if (response.status == 200) {
						var json = eval("(" + response.responseText + ")")
						if (json) {
							var code = json["code"]
							var msg = json["msg"]
							// modified by gaof 2015-1-4 解决msg为undefined时报错的问题
							this.processReturnMsg(code, msg ? msg : "",
									this.refresh)
						}
					} else {
						this.processReturnMsg(404, 'ConnectionError',
								this.refresh)
					}
				}, this)

		if (this.group) {
			var store = new Ext.data.GroupingStore({
						reader : reader,
						proxy : proxy,
						sortInfo : {
							field : this.groupSort || this.group,
							direction : "ASC"
						},
						groupField : this.group
					});
		} else {
			var store = new Ext.data.Store({
						proxy : proxy,
						reader : reader,
						remoteSort : this.remoteSort || false
					})
		}
		store.on("load", this.onStoreLoadData, this)
		store.on("beforeload", this.onStoreBeforeLoad, this)
		return store

	},
	getCM : function(items) {
		var cm = []
		var ac = util.Accredit;
		var expands = []
		if (this.showRowNumber) {
			cm.push(new Ext.grid.RowNumberer({
						locked : this.lockingGrid
					}))
		}
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if ((it.display <= 0 || it.display == 2 || it.hidden == true)
					|| !ac.canRead(it.acValue)) {
				continue
			}
			if (it.expand) {
				var expand = {
					id : it.dic ? it.id + "_text" : it.id,
					alias : it.alias,
					xtype : it.xtype
				}
				expands.push(expand)
				continue
			}
			if (!this.fireEvent("onGetCM", it)) { // **
				// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
				continue;
			}
			var width = parseInt(it.width || 80)
			// if(width < 80){width = 80}
			var c = {
				header : it.alias,
				width : width,
				sortable : this.sortable,
				dataIndex : it.dic ? it.id + "_text" : it.id,
				menuDisabled : this.menuDisabled //禁用grid下拉菜单
			}
			if (it.sortable) {
				c.sortable = (it.sortable == "true");
			}
			if (!this.isCompositeKey && it.pkey) {
				c.id = it.id
			}
			if (it.summaryType) {
				c.summaryType = it.summaryType;
			}
			if (it.locked) {
				c.locked = true;
			}
			switch (it.type) {
				case 'int' :
				case 'double' :
				case 'bigDecimal' :
					if (!it.dic) {
						c.css = "color:#00AA00;font-weight:bold;"
						c.align = "right"
						if (it.precision > 0) {
							var nf = '0.';
							for (var j = 0; j < it.precision; j++) {
								nf += '0';
							}
							c.renderer = Ext.util.Format.numberRenderer(nf);
						}
					}
					break
				case 'date' :
					c.renderer = function(v) {
						if (v && typeof v == 'string' && v.length > 10) {
							return v.substring(0, 10);
						}
						return v;
					}
					break
				case 'timestamp' :
				case 'datetime' :
					if (it.xtype == 'datefield') {
						c.renderer = function(v) {
							if (v && typeof v == 'string' && v.length > 10) {
								return v.substring(0, 10);
							} else {
								return v;
							}
						}
					}
					break
			}
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
			if (this.fireEvent("addfield", c, it)) {
				cm.push(c)
			}
		}
		if (expands.length > 0) {
			this.rowExpander = this.getExpander(expands)
			cm = [this.rowExpander].concat(cm)
		}
		return cm
	},
	getCndBar : function(items) {
		var fields = [];
		if (!this.enableCnd) {
			return []
		}
		var selected = null;
		var defaultItem = null;
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.queryable || it.queryable == 'false') {
				continue
			}
			if (it.selected == "true") {
				selected = it.id;
				defaultItem = it;
			}
			fields.push({
						// change "i" to "it.id"
						value : it.id,
						text : it.alias
					})
		}
		if (fields.length == 0) {
			return [];
		}
		var store = new Ext.data.JsonStore({
					fields : ['value', 'text'],
					data : fields
				});
		var combox = null;
		if (fields.length > 1) {
			combox = new Ext.form.ComboBox({
						store : store,
						valueField : "value",
						displayField : "text",
						value : selected,
						mode : 'local',
						triggerAction : 'all',
						emptyText : '选择查询字段',
						selectOnFocus : true,
						width : 100
					});
			combox.on("select", this.onCndFieldSelect, this)
			this.cndFldCombox = combox
		} else {
			combox = new Ext.form.Label({
						text : fields[0].text
					});
			this.cndFldCombox = new Ext.form.Hidden({
						value : fields[0].value
					});
		}

		var cndField;
		if (defaultItem) {
			if (defaultItem.dic) {
				defaultItem.dic.src = this.entryName + "." + it.id
				defaultItem.dic.defaultValue = defaultItem.defaultValue
				defaultItem.dic.width = 150
				cndField = this.createDicField(defaultItem.dic)
			} else {
				cndField = this.createNormalField(defaultItem)
			}
		} else {
			cndField = new Ext.form.TextField({
						width : 150,
						selectOnFocus : true,
						name : "dftcndfld"
					})
		}
		this.cndField = cndField
		cndField.on("specialkey", this.onQueryFieldEnter, this)
		var queryBtn = new Ext.Toolbar.SplitButton({
					text : '',
					iconCls : "query",
					notReadOnly : true, // ** add by yzh **
					menu : new Ext.menu.Menu({
								items : {
									text : "高级查询",
									iconCls : "common_query",
									handler : this.doAdvancedQuery,
									scope : this
								}
							})
				})
		this.queryBtn = queryBtn
		queryBtn.on("click", this.doCndQuery, this);
		return [combox, '-', cndField, '-', queryBtn]
	},
	onCndFieldSelect : function(item, record, e) {
		var tbar = this.grid.getTopToolbar()
		var tbarItems = tbar.items.items
		var itid = record.data.value
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		var field = this.cndField
		// field.destroy()
		field.hide();
		var f = this.midiComponents[it.id]
		if (!f) {
			if (it.dic) {
				it.dic.src = this.entryName + "." + it.id
				it.dic.defaultValue = it.defaultValue
				it.dic.width = 150
				f = this.createDicField(it.dic)
			} else {
				f = this.createNormalField(it)
			}
			f.on("specialkey", this.onQueryFieldEnter, this)
			this.midiComponents[it.id] = f
		} else {
			f.on("specialkey", this.onQueryFieldEnter, this)
			// f.rendered = false
			f.show();
		}
		this.cndField = f
		tbarItems[2] = f
		tbar.doLayout()
	},
	onQueryFieldEnter : function(f, e) {
		if (e.getKey() == e.ENTER) {
			e.stopEvent()
			this.doCndQuery()
		}
	},
	doAdvancedQuery : function() {
		if (!this.schema) {
			return
		}
		var items = this.schema.items
		var qWin = this.midiModules["qWin"]
		var cfg = {
			list : this,
			entryName : this.entryName,
			items : items
		}
		if (!qWin) {
			$import("phis.script.QueryWin")
			qWin = new phis.script.QueryWin(cfg)
			this.midiModules["qWin"] = qWin
		} else {
			Ext.apply(qWin, cfg)
		}
		qWin.getWin().show()
	},
	doCndQuery : function(button, e, addNavCnd) { // ** modified by
		// yzh ,
		// 2010-06-09 **
		var initCnd = this.initCnd
		var itid = this.cndFldCombox.getValue()
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		if (!it) {
			if (addNavCnd) {
				if (initCnd) {
					this.requestData.cnd = ['and', initCnd, this.navCnd];
				} else {
					this.requestData.cnd = this.navCnd;
				}
				this.refresh()
				return
			} else {
				return;
			}
		}
		this.resetFirstPage()
		var v = this.cndField.getValue()
		var rawV = this.cndField.getRawValue();
		if (v == null || v == "" || rawV == null || rawV == "") {
			var cnd = []
			this.queryCnd = null;
			if (addNavCnd) {
				if (initCnd) {
					this.requestData.cnd = ['and', initCnd, this.navCnd];
				} else {
					this.requestData.cnd = this.navCnd;
				}
				this.refresh()
				return
			} else {
				if (initCnd)
					cnd = initCnd
			}
			this.requestData.cnd = cnd.length == 0 ? null : cnd;
			this.refresh()
			return
		}
		// 替换'，解决拼sql语句查询的时候报错
		if (typeof v == 'string') {
			v = (v + "").replace(/'/g, "''")
		}
		var refAlias = it.refAlias || "a"
		var cnd = ['eq', ['$', refAlias + "." + it.id]]
		if (it.dic) {
			if (it.dic.render == "Tree") {
				// var node = this.cndField.selectedNode
				// @@ modified by chinnsii 2010-02-28, add "!node"
				cnd[0] = 'eq'
				// if (!node || !node.isLeaf()) {
				// cnd[0] = 'like'
				// cnd.push(['s', v + '%'])
				// } else {
				cnd.push(['s', v])
				// }
			} else {
				cnd.push(['s', v])
			}
		} else {
			switch (it.type) {
				case 'int' :
					cnd.push(['i', v])
					break;
				case 'double' :
				case 'bigDecimal' :
					cnd.push(['d', v])
					break;
				case 'string' :
					// add by liyl 07.25 解决拼音码查询大小写问题 //modifyed by zhangxw
					// 2014.10.16解决ICD10查询大小写问题
					if (it.id == "PYDM" || it.id == "WBDM" || it.id == "ICD10") {
						v = v.toUpperCase();
					}
					cnd[0] = 'like'
					cnd.push(['s', v + '%'])
					break;
				case "date" :
					v = v.format("Y-m-d")
					cnd[1] = ['$',
							"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
					cnd.push(['s', v])
					break;
				case "datetime" :
					v = v.format("Y-m-d H:i:s")
					cnd[1] = [
							'$',
							"str(" + refAlias + "." + it.id
									+ ",'yyyy-MM-dd hh:mm:ss')"]
					cnd.push(['s', v])
					break;
			}
		}
		this.queryCnd = cnd
		if (initCnd) {
			cnd = ['and', initCnd, cnd]
		}
		if (addNavCnd) {
			this.requestData.cnd = ['and', cnd, this.navCnd];
			this.refresh()
			return
		}
		this.requestData.cnd = cnd
		this.refresh()
	},
	createNormalField : function(it) {
		var cfg = {
			name : it.id,
			fieldLabel : it.alias,
			width : this.queryWidth || 150,
			value : it.defaultValue
		}
		var field;
		switch (it.type) {
			case 'int' :
			case 'double' :
			case 'bigDecimal' :
				cfg.xtype = "numberfield"
				if (it.type == 'int') {
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				} else {
					cfg.decimalPrecision = it.precision || 2;
				}
				if (it.minValue) {
					cfg.minValue = it.minValue;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				field = new Ext.form.NumberField(cfg)
				break;
			case 'date' :
				cfg.xtype = 'datefield'
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
				field = new Ext.form.DateField(cfg)
				break;
			case 'timestamp' :
			case 'datetime' :
				cfg.xtype = 'datetimefield'
				cfg.emptyText = "请选择日期时间"
				cfg.format = 'Y-m-d H:i:s'
				field = new util.widgets.DateTimeField(cfg)
				break;
			case 'string' :
				field = new Ext.form.TextField(cfg)
				break;
		}
		return field;
	},
	loadDataByLocal : function(data, add) {
		if (!add) {
			this.store.removeAll();
		}
		var records = this.getExtRecord(data);
		this.store.add(records);
	},
	getExtRecord : function(data) {
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var record = new Ext.data.Record(data[i]);
			records.push(record);
		}
		return records;
	},
	updateView : function(result) {

	},
	loadModule : function(cls, entryName, item, r) {
		if (this.loading) {
			return
		}
		var cmd = item.cmd
		var cfg = {}
		cfg._mId = this.grid._mId // 增加module的id
		cfg.title = (this.title || this.name) + '-' + item.text
		cfg.entryName = entryName
		cfg.op = cmd
		cfg.butRule = this.getButRule()
		cfg.exContext = {}
		Ext.apply(cfg.exContext, this.exContext)

		if (cmd != 'create') {
			if (this.isCompositeKey) {
				var keys = this.schema.keys;
				var initDataBody = {};
				for (var i = 0; i < keys.length; i++) {
					initDataBody[keys[i]] = r.get(keys[i])
				}
				cfg.initDataBody = initDataBody;
			} else {
				cfg.initDataId = r.id;
			}
			cfg.exContext[entryName] = r;
		}
		if (this.saveServiceId) {
			cfg.saveServiceId = this.saveServiceId;
		}
		var m = this.midiModules[cmd]
		if (!m) {
			this.loading = true
			$require(cls, [function() {
								this.loading = false
								cfg.autoLoadData = false;
								cfg.mainApp = this.mainApp// initPanel中用到mainApp可能未设置
								var module = eval("new " + cls + "(cfg)")
								module.on("save", this.onSave, this)
								module.on("close", this.active, this)
								module.opener = this
								module.setMainApp(this.mainApp)
								this.midiModules[cmd] = module
								this.fireEvent("loadModule", module)
								this.openModule(cmd, r, [200, 50])
							}, this])
		} else {
			Ext.apply(m, cfg)
			this.openModule(cmd, r)
		}
	},
	openModule : function(cmd, r, xy) {
		var module = this.midiModules[cmd]
		if (module) {
			var win = module.getWin()
			win.setTitle(module.title)
			win.show()
			if (this.winState) {
				if (this.winState == 'center') {
					win.center();
				} else {
					xy = this.winState;
					win.setPosition(this.xy[0] || xy[0], this.xy[1] || xy[1])
				}
			} else {
				var default_xy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(default_xy[0], default_xy[1]);
			}

			this.fireEvent("openModule", module)
			if (!win.hidden) {
				switch (cmd) {
					case "create" :
						module.doNew()
						break;
					case "read" :
					case "update" :
						module.loadData()
				}
			}
		}
	},
	doRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		var title = r.id;
		if (this.isCompositeKey) {
			title = "";
			for (var i = 0; i < this.schema.keys.length; i++) {
				title += r.get(this.schema.keys[i])
			}
		}
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
	processRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		if (!this.fireEvent("beforeRemove", this.entryName, r)) {
			return;
		}
		this.mask("正在删除数据...");
		var pkey = r.id;
		if (this.isCompositeKey) {
			pkey = {};
			for (var i = 0; i < this.schema.keys.length; i++) {
				pkey[this.schema.keys[i]] = r.get(this.schema.keys[i]);
			}
		}
		var removeRequest = this.getRemoveRequest(r)
		var removeCfg = {
			serviceId : this.removeServiceId,
			method : this.removeMethod,
			pkey : pkey,
			body : removeRequest,
			schema : this.entryName,
			action : "remove", // 按钮标识
			module : this.grid._mId
			// 增加module的id
		}
		this.fixRemoveCfg(removeCfg);
		phis.script.rmi.jsonRequest(removeCfg, function(code, msg, json) {
					this.unmask()
					if (code < 300) {
						this.store.remove(r)
						this.updatePagingInfo()
						this.fireEvent("remove", this.entryName, 'remove',
								json, r.data)
					} else {
						this.processReturnMsg(code, msg, this.doRemove)
					}
				}, this)
	},
	getWin : function() {
		var win = this.win
		var closeAction = this.closeAction || "hide"
		if (!this.mainApp || this.closeAction == true) {
			closeAction = "hide"
		}
		if (!win) {
			win = new Ext.Window({
				id : this.id,
				title : this.title || this.name,
				width : this.width,
				iconCls : 'icon-grid',
				shim : true,
				layout : "fit",
				animCollapse : true,
				closeAction : closeAction,
				constrainHeader : true,
				constrain : true,
				minimizable : true,
				maximizable : true,
				shadow : false,
				modal : this.modal || false
					// add by huangpf.
				})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() { // ** add by yzh 2010-06-24 **
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		win.instance = this;
		return win;
	},
	getButRule : function() {
		var actions = this.actions
		var rule = 0
		if (!actions) {
			return rule
		}
		var ac = util.Accredit.ModeFlag
		for (var i = 0; i < actions.length; i++) {
			var a = actions[i];
			if (a.id == "read") {
				rule += ac.ACCESSIBLE_FLAG
			}
			if (a.id == "create") {
				rule += ac.CREATEABLE_FLAG
			}
			if (a.id == "update") {
				rule += ac.UPDATEABLE_FLAG
			}
		}
		return rule

	}
});
