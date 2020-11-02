/**
	 * 公共文件
	 * 
	 * @author : yaozh
	 */
$package("chis.script")
$import("util.rmi.miniJsonRequestSync", "chis.script.util.widgets.MyMessageTip")

chis.script.BizCommon = {
	xy : [250, 100],

	/**
	 * 根据模块的编号，创建一个组合模块，并初始化面板
	 * 
	 * @param {}
	 *            moduleName 模块名称
	 * @param {}
	 *            moduleId 模块编号
	 * @return {}
	 */
	getCombinedModule : function(moduleName, moduleId, buttonIndex) {
		var item = this.createCombinedModule(moduleName, moduleId);
		item.buttonIndex = buttonIndex || 0;
		var itemPanel = item.initPanel();
		return itemPanel;
	},

	/**
	 * 根据模块的编号，创建一个组合模块，并初始化面板
	 * 
	 * @param {}
	 *            moduleName 模块名称
	 * @param {}
	 *            moduleId 模块编号
	 */
	createCombinedModule : function(moduleName, moduleId) {
		return this.loadNewModule(moduleName, moduleId, true);
	},

	/**
	 * 根据模块的编号，创建一个简单模块(非组合)，并初始化面板
	 * 
	 * @param {}
	 *            moduleName 模块名称
	 * @param {}
	 *            moduleId 模块编号
	 * @return {}
	 */
	getSimpleModule : function(moduleName, moduleId) {
		var item = this.createSimpleModule(moduleName, moduleId);
		var itemPanel = item.initPanel();
		return itemPanel;
	},

	/**
	 * 根据模块的编号， 创建一个简单模块(非组合)，不初始化面板
	 * 
	 * @param {}
	 *            moduleName 模块名称
	 * @param {}
	 *            moduleId 模块编号
	 */
	createSimpleModule : function(moduleName, moduleId) {
		return this.loadNewModule(moduleName, moduleId, false);
	},

	/**
	 * 根据模块的编号，从配置文件中读取数据，创建模块不初始化面板
	 * 
	 * @param {}
	 *            moduleName
	 * @param {}
	 *            moduleId
	 * @param {}
	 *            isCombinedModule
	 */
	loadNewModule : function(moduleName, moduleId, isCombinedModule) {
		var item = this.midiModules[moduleName]
		if (!item) {
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(moduleId);
			var cfg = {
				border : false,
				frame : false,
				exContext : {},
				mainApp : this.mainApp
			};
			if (isCombinedModule) {
				cfg.autoLoadData = false;
				cfg.autoLoadSchema = false;
				cfg.isCombined = true;
			} else {
				cfg.autoLoadData = true;
				cfg.autoLoadSchema = true;
				cfg.isCombined = false;
			}
			// guol
			if (moduleCfg.json && moduleCfg.json.body) {
				Ext.apply(cfg, moduleCfg.json.body);
				Ext.apply(cfg, moduleCfg.json.body.properties);
			} else {
				Ext.apply(cfg, moduleCfg);
				Ext.apply(cfg, moduleCfg.properties);
			}
			delete cfg.id;
			this.refreshExContextData(cfg, this.exContext);
			var cls = cfg.script;
			if (!cls) {
				return;
			}
			if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
				return;
			}
			$import(cls);
			item = eval("new " + cls + "(cfg)");
			item.setMainApp(this.mainApp)
			this.midiModules[moduleName] = item;
			if (!this.fireEvent("afterLoadModule", moduleName, cfg)) {
				return;
			}
			return item;
		} else {
			return item;
		}
	},

	/**
	 * 打开窗口
	 * 
	 * @param {}
	 *            module
	 */
	showWin : function(module) {
		var win = module.getWin();
		// win.setPosition(this.xy[0], this.xy[1]);
		win.show();
	},

	/**
	 * 刷新页面上下文数据
	 * 
	 * @param {}
	 *            m
	 * @param {}
	 *            exContext
	 */
	refreshExContextData : function(m, exContext) {
		m.exContext.ids = {}; // ** 档案主键
		m.exContext.empiData = {}; // **个人基本信息
		m.exContext.args = {}; // ** 其他参数
		m.exContext.control = {}; // ** 只读标识
		if (exContext) {
			Ext.apply(m.exContext.ids, exContext.ids);
			Ext.apply(m.exContext.empiData, exContext.empiData);
			Ext.apply(m.exContext.args, exContext.args);
			Ext.apply(m.exContext.control, exContext.control);
		}
	},

	/**
	 * 设置按钮权限
	 * 
	 * @param {}
	 *            btn
	 * @param {}
	 *            controlData
	 */
	setButtonControl : function(btn) {
		var status = this.getButtonStatus(btn);
		if (status == undefined) {
			return;
		}
		this.changeButtonStatus(btn, status);
	},

	/**
	 * 获取按钮控制权限
	 * 
	 * @param {}
	 *            btn
	 * @param {}
	 *            controlData
	 * @return {Boolean}
	 */
	getButtonStatus : function(btn) {
		if (!this.exContext.control) {
			return undefined;
		}
		if (!btn.prop) {
			return true
		}
		if (!btn.prop.properties) {
			return true
		}
		var group = btn.prop.properties.group;
		if (!group) {
			return true;
		}
		if (group == "create||update") {
			if (!this.initDataId) {
				this.exContext.control["update"] = this.exContext.control["create"];
				return this.exContext.control["create"];
			} else {
				return this.exContext.control["update"];
			}
		} else {
			return this.exContext.control[group];
		}
	},

	/**
	 * 改变按钮状态
	 * 
	 * @param {}
	 *            btn
	 * @param {}
	 *            status
	 */
	changeButtonStatus : function(btn, status) {
		if (!btn) {
			return;
		}
		if (status) {
			btn.enable();
		} else {
			btn.disable();
		}
	},

	loadModuleCfg: function (id) {
		var moduleCfg = this.mainApp.taskManager.loadModuleCfg(id);
		var cfg = {}
		// guol
		if (moduleCfg.json && moduleCfg.json.body) {
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
		} else {
			Ext.apply(cfg, moduleCfg);
			Ext.apply(cfg, moduleCfg.properties);
		}
		return cfg
	},

	castFormDataToList : function(data, schema) {
		var listData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = data[key];
				listData[key] = dicData.key;
				listData[key + "_text"] = dicData.text;
			} else {
				listData[key] = data[key];
			}
		}
		Ext.applyIf(listData, data)
		return listData;
	},

	/**
	 * 将list数据转换为form数据
	 * 
	 * @param {}
	 *            data
	 * @return {}
	 */
	castListDataToForm : function(data, schema) {
		var formData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = {
					"key" : data[key],
					"text" : data[key + "_text"]
				};
				formData[key] = dicData;
			} else {
				formData[key] = data[key];
			}
		}
		Ext.applyIf(formData, data)
		return formData;
	},
	/**
	 * 默认实现方式，已实现以下功能，如特殊需求，可扩展此功能 //优先判断是否有直接的实现方法 如：ctrl_D : function(){} 规则
	 * 1、ctrl、shift、alt未组合键,组合建对应的方法格式为 ctrl_shift_alt_X(X为A-Z 0-9);
	 * 组合键数量可以自定义，但是顺序必须按示例所示，如 ctrl_D alt_F ctrl_shift_A
	 * 2、指定action的accessKey,如：accessKey="doSubmit" 则会调用对应module的doSubmit方法
	 * 3、默认仍采用以前的F1-F12的方式
	 * 
	 * 遗留问题：当快捷键调用的方法中存在同步的ajax请求时，时间无法中断，会调用到浏览器的快捷键
	 * 
	 * @param {}
	 *            keyCode 按键值
	 * @param {}
	 *            keyName 按键名称
	 * @return {Boolean} 返回false表示事件被监听，屏蔽浏览器按键事件
	 */
	shortcutKeyFunc : function(keyCode, keyName) {
		// 不可见的窗口,不传递事件
		if (this.win && !this.win.isVisible())
			return true;
		var actions = this.actions;
		if (this.btnAccessKeys) {
			var btn = this.btnAccessKeys[keyCode];
			if (btn && btn.disabled) {
				return true;
			}
		}
		// MyMessageTip.msg("提示", "actions:" + Ext.encode(actions), true);
		// 优先当前模块的实现
		// 先处理按键直接对应的实现方法
		try {
			// 增加快捷键统一处理功能(总入口，由相应模块自己处理)
			if (eval("this.keyManageFunc")) {
				eval("this.keyManageFunc(keyCode,keyName)");
				return false;
			}
			// 单个事件实现
			if (keyName && keyName != "" && eval("this." + keyName)) {
				eval("this." + keyName + "()");
				return false;
			}
		} catch (e) {
			return true;
		}
		// 处理按钮事件
		if (actions) {
			for (var j = 0; j < actions.length; j++) {
				if (actions[j].accessKey) {
					if (eval("this." + actions[j].accessKey)) {
						eval("this." + actions[j].accessKey + "(keyCode)");
						return false;
					}
				} else {// 默认取F1-F12
					if (this.noDefaultBtnKey)
						return false;
					var defaultKey = 112 + (this.buttonIndex || 0) + j;
					if (keyCode == defaultKey) {
						if (eval("this.btnAccessKeys")) {
							eval("this.doAction(this.btnAccessKeys[keyCode])");
						}
						return false;
					}
				}
			}
		}
		// MyMessageTip.msg("提示", "缓存的module：" + this.listenModules.length,
		// true);
		// 禁止多个module多窗口之间的传递
		// 遍历其它模块
		// if (this.listenModules) {
		// for (var i = 0; i < this.listenModules.length; i++) {
		// var m = this.listenModules[i];
		// // MyMessageTip.msg("提示", "next1", true);
		// if (!m.shortcutKeyFunc(keyCode, keyName)) {
		// return false;
		// }
		// }
		// // return false;
		// }
		return true;
	},

	chars : ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
	generateMixed : function(n) {
		var res = "";
		for (var i = 0; i < n; i++) {
			var id = Math.ceil(Math.random() * 35);
			res += this.chars[id];
		}
		return res;
	},
	createButtons : function(level) {
		var actions = this.actions;
		var buttons = []
		if (!actions) {
			return buttons
		}
		if (this.butRule) {
			var ac = util.Accredit
			if (ac.canCreate(this.butRule)) {
				this.actions.unshift({
							id : "create",
							name : "新建"
						})
			}
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			if (action.properties && action.properties.hide) {
				continue
			}
			level = level || 'one';
			if (action.properties) {
				action.properties.level = action.properties.level || 'one';
			} else {
				action.properties = {};
				action.properties.level = 'one';
			}
			if (action.properties && action.properties.level != level) {
				continue;
			}
			// ** add by yzh **
			var btnFlag;
			if (action.notReadOnly)
				btnFlag = false
			else
				btnFlag = (this.exContext && this.exContext.readOnly) || false

			if (action.properties && action.properties.scale) {
				action.scale = action.properties.scale
			}
			var btn = {
				accessKey : f1 + i,
				text : action.name
						+ (this.noDefaultBtnKey ? "" : "(F" + (i + 1) + ")"),
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				scale : action.scale || "small",
				// ** add by yzh **
				disabled : btnFlag,
				notReadOnly : action.notReadOnly,

				script : action.script,
				handler : this.doAction,
				scope : this
			}
			btn.prop = {};
			Ext.apply(btn.prop, action);
			Ext.apply(btn.prop, action.properties);
			buttons.push(btn)
		}
		return buttons

	},
	/**
	 * 根据模块的url，创建一个模块，并初始化面板
	 * 
	 * @param {}
	 *            id 模块id
	 * @param {}
	 *            url 模块的路径
	 * @param {}
	 *            param 参数
	 * @return {}
	 */
	getModuleToWin : function(id, url, param) {
		var module = this.midiModules[id];
		if (!module) {
			var cfg = {};
			if (param)
				Ext.apply(cfg, param);
			cfg.mainApp = this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(url);
			if (moduleCfg.code > 200) {
				app.modules.common.processReturnMsg(moduleCfg.code,
						moduleCfg.msg, this.getModuleToWin, [id, url, param],
						null);
				return;
			}
			if (moduleCfg.json && moduleCfg.json.body)
				Ext.apply(cfg, moduleCfg.json.body);
			if (moduleCfg.json && moduleCfg.json.body
					&& moduleCfg.json.body.properties)
				Ext.apply(cfg, moduleCfg.json.body.properties);
			// delete cfg.id;
			cfg.exContext = this.exContext || {};
			// cfg.autoInit=false;
			cfg.autoLoadSchema = false;
			// cfg.id=cfg.id+''+new Date();
			var cls = cfg.script;
			$import(cls);
			module = eval("new " + cls + "(cfg)");
			module.mainApp = this.mainApp;
			module.opener = this;
			module.closeId = id;
			this.midiModules[id] = module;
		}
		var win = this.midiWins[id];
		var closeAction = "hide";
		if (!win) {
			var winpanel = module.initPanel();
			// console.log(winpanel);
			win = new Ext.Window({
						// id : this.id,
						title : param.title || module.name || this.name || '',
						width : param.width || 600,
						height : param.height || 320,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrain : true,
						resizable : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						maximized : param.maximized || false,
						shadow : false,
						modal : this.modal || true,
						items : winpanel
					});
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this);
			win.on("beforeshow", function() {
						this.fireEvent("beforeWinShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.midiWins[id] = win;
		}
		// console.log(win);
		if (module.autoLoadData == false) {
			module.loadData();
		}
		win.show();
	}

	// 新增
	,
	createRemoteDicField : function(it) {
		var mds_reader = this.getRemoteDicReader();
		// store远程url
		// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
		var url = ClassLoader.serverAppUrl || "";
		this.comboJsonData = {
			serviceId : this.remoteDicService || "chis.CommonService",
			serviceAction : this.remoteDicAction || "loadDicData",
			method : "execute",
			className : this.remoteUrl || "MedicineAllCHIS"
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
		var remoteTpl = this.remoteTpl
				|| '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YPDW}</td><td width="30px">{YPJL}</td><td width="30px">{JLDW}</td>';;
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + remoteTpl + '</tr>', '</table>', '</div>', '</tpl>');
		var _ctx = this;
		var remoteField = new Ext.form.ComboBox({
					// id : "YPMC",
					width : this.readerWidth || it.width || 120,
					name : it.id,
					fieldLabel : it.alias + ":",
					store : mdsstore,
					selectOnFocus : true,
					typeAhead : false,
					loadingText : '搜索中...',
					pageSize : 10,
					hideTrigger : true,
					minListWidth : this.minListWidth || 360,
					tpl : resultTpl,
					minChars : 1,
					enableKeyEvents : true,
					lazyInit : false,
					itemSelector : 'div.search-item',
					onSelect : function(record) { // override default
						// onSelect
						// to do
						this.bySelect = true;
						_ctx.setBackInfo(this, record, it.afterSelect);
						// this.hasFocus = false;// add by yangl
						// 2013.9.4
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
								_ctx.setBackInfo(obj, record, it.afterSelect);
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
	beforeSearchQuery : function() {
		return true;
	},
	setBackInfo : function(obj, record, afterSelect) {
		obj.setValue(record.get(this.backField || "YPMC"));
		if (this.backOtherField && this.backOtherField.length > 0 && this.form) {
			var form = this.form.getForm();
			for (var i = 0; i < this.backOtherField.length; i++) {
				var fies = this.backOtherField[i];
				for (var f in fies) {
					var vfs = fies[f];
					var value = record.get(vfs)
					if (vfs.indexOf(",") > -1) {
						value = "";
						var vfss = vfs.split(",");
						for (var j = 0; j < vfss.length; j++) {
							value += record.get(vfss[j]);
						}
					}
					if (form.findField(f)) {
						form.findField(f).setValue(value);
					}
				}
			}
		}
		if (this.backDataField && this.backDataField.length > 0 && this.data) {
			for (var i = 0; i < this.backDataField.length; i++) {
				var fies = this.backDataField[i];
				for (var f in fies) {
					this.data[f] = record.get(fies[f]);
				}
			}
		}
		obj.collapse();
		if (afterSelect) {
			if (afterSelect instanceof Function)
				afterSelect(obj, record);
		}
	},
	getRemoteDicReader : function() {
		var id = this.remoteDicReaderId + "_" + this.generateMixed(7)
				|| "icd10search_" + this.generateMixed(7);
		var fields = [{
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
					name : 'YPJL'

				}, {
					name : 'JLDW'

				}];
		if (this.readerFields && this.readerFields.length > 0) {
			fields = this.readerFields;
		}
		return new Ext.data.JsonReader({
					root : this.readerRoot || 'mds',
					totalProperty : 'count',
					id : id
				}, fields);
	},
	// 公用与医疗联动导入数据
	doImportJzInfo : function() {
		if (this.mainApp.jobId.split('.')[0] == 'chis'
				|| this.mainApp.phisActiveYW == true
				|| this.mainApp.phisActiveYW == "true") {
			var module = this.createSimpleModule(Ext.id() + 'JzInfoListModule',
					'chis.application.common.COMMON/COMMON/CM03');
			module.opener = this;
			var win = module.getWin();
			win.show();
		} else {
			Ext.Msg.alert('提示', '未开启基层医疗服务无法获取信息,请联系系统管理员');
		}
	},
	doImportDrugInfo : function() {
		if (this.mainApp.jobId.split('.')[0] == 'chis'
				|| this.mainApp.phisActiveYW == true
				|| this.mainApp.phisActiveYW == "true") {
			var module = this.createSimpleModule(Ext.id()
							+ 'DrugInfoListModule',
					'chis.application.common.COMMON/COMMON/CM01');
			module.opener = this;
			var win = module.getWin();
			win.show();
		} else {
			Ext.Msg.alert('提示', '未开启基层医疗服务无法获取信息,请联系系统管理员');
		}
	},
	// 药品字典
	createLocalDicField : function(it) {
		var mds_reader = this.getLocalDicReader();
		// store远程url
		// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
		var url = ClassLoader.serverAppUrl || "";
		var comboJsonData = {
			serviceId : "chis.CommonService",
			serviceAction : "loadDicData",
			method : "execute",
			className : "MedicineAllCHIS"
		}
		var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'POST',
					jsonData : comboJsonData
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

		Ext.apply(mdsstore.baseParams, it.queryParams);
		var remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YPDW}</td><td width="30px">{YPJL}</td><td width="30px">{JLDW}</td><td width="30px">{YCJL}</td>';;
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + remoteTpl + '</tr>', '</table>', '</div>', '</tpl>');
		var _ctx = this;
		var remoteField = new Ext.form.ComboBox({
					// id : "YPMC",
					selectData : {},
					width : it.width || 120,
					name : it.id,
					fieldLabel : it.alias + ":",
					store : mdsstore,
					selectOnFocus : true,
					typeAhead : false,
					loadingText : '搜索中...',
					pageSize : 10,
					hideTrigger : true,
					minListWidth : it.minListWidth || 380,
					tpl : resultTpl,
					minChars : 1,
					enableKeyEvents : true,
					lazyInit : false,
					itemSelector : 'div.search-item',
					onSelect : function(record) { // override default
						// onSelect
						// to do
						this.bySelect = true;
						_ctx.setLocalBackInfo(this, record, it.afterSelect);
						// this.hasFocus = false;// add by yangl
						// 2013.9.4
						// 解决新增行搜索时重复调用setBack问题
					}
				});
		remoteField.comboJsonData;
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);

		remoteField.on("blur", function(t) {
					var a = t.getValue();
					if (t.getValue() != t.selectData.YPMC) {
						//t.reset();药品名字可手动输入
						if (it.afterClear) {
							if (it.afterClear instanceof Function)
								it.afterClear(t);
						}
					}
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
								_ctx.setLocalBackInfo(obj, record,
										it.afterSelect);
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
				Ext.apply(comboJsonData, options.params);
				Ext.apply(comboJsonData, mdsstore.baseParams);
				options = Ext.apply({}, options);
				this.storeOptions(options);
				if (remoteField.sortInfo && remoteField.remoteSort) {
					var pn = this.paramNames;
					options.params = Ext.apply({}, options.params);
					options.params[pn.sort] = remoteField.sortInfo.field;
					options.params[pn.dir] = remoteField.sortInfo.direction;
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
					comboJsonData.query = qe.query;
					// 设置下拉框的分页信息
					// remoteField.pageTb.changePage(0);
					return true;
				}, this);
		return remoteField
	},
	setLocalBackInfo : function(obj, record, afterSelect) {
		obj.setValue(record.get("YPMC"));
		obj.selectData = {};
		obj.selectData = record.data;
		obj.collapse();
		if (afterSelect) {
			if (afterSelect instanceof Function)
				afterSelect(obj, record);
		}
	},
	getLocalDicReader : function() {
		var id = "ypxxSearch_" + Ext.id();
		var fields = [{
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
					name : 'YPJL'

				}, {
					name : 'JLDW'

				}, {
					name : 'YCJL'

				}];
		return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : id
				}, fields);
	},
	refreshEhrTopIcon : function() {
		if (this.ehrview) {
			if (this.ehrview.emrView) {
				this.ehrview.emrView.refreshTopIcon();
			} else {
				this.ehrview.refreshTopIcon();
			}
		}
		if (this.emrview) {
			this.ehrview.refreshTopIcon();
		}

	},
	/**
	 * 根据yyyy-MM-dd的字符串类型的数据，返回date 是date的直接返回，无法解析的返回当天日期；
	 * yyyy/MM/dd和yyyy.MM.dd均可
	 * @param {}
	 *            strDate
	 * @return {}
	 */
	getDate : function(strDate) {
		if (strDate instanceof Date) {
			return strDate
		}
		if(typeof strDate!="string"){
			return new Date();
		}
		var result = strDate.match(/^\d{4}(\-|\/|\.)\d{1,2}\1\d{1,2}$/);
		if (result == null) {
			return new Date();
		}
		var strSeparator = "-";
		if (strDate.indexOf("/") > 0) {
			strSeparator = "/";
		} else if (strDate.indexOf(".") > 0) {
			strSeparator = ".";
		}
		var oDate;
		oDate = strDate.split(strSeparator);
		var date = new Date(oDate[0], oDate[1] - 1, oDate[2]);
		return date;
	},
	/**
	 * 创建模块*可传自定义参数exCfg
	 * @param moduleName
	 * @param moduleId
	 * @param exCfg
	 */
	createModule: function (moduleName, moduleId, exCfg) {
		var item = this.midiModules[moduleName]
		if (!item) {
			var moduleCfg = this.loadModuleCfg(moduleId);
			var cfg = {
				showButtonOnTop: true,
				closable: false,
				border: false,
				frame: false,
				autoLoadSchema: false,
				isCombined: true,
				exContext: {}
			};
			Ext.apply(cfg, exCfg);
			Ext.apply(cfg, moduleCfg);
			var cls = moduleCfg.script;
			if (!cls) {
				return;
			}
			if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
				return;
			}
			$import(cls);
			item = eval("new " + cls + "(cfg)");
			item.setMainApp(this.mainApp);
			this.midiModules[moduleName] = item;
		}
		return item;
	},
	//完成家庭医生任务
	updateTaskStatus: function () {
		if (this.exContext.ids && this.exContext.ids.taskId) {
			util.rmi.jsonRequest({
				serviceId: "chis.signContractRecordService",
				method: "execute",
				serviceAction: "updateTaskStatus",
				taskId: this.exContext.ids.taskId
			}, function (code, msg, json) {
				if (code > 300) {
					return;
				} else {
					var date = new Date;
					var year = date.getFullYear();
					var cnd = ['and', ['eq', ['$', 'a.empiId'], ['s', this.exContext.ids.empiId || this.empiId || '']], ['eq', ['$', 'a.year'], ['s', year]]];
					this.exContext.openerList.requestData.cnd = cnd;
					this.exContext.openerList.refresh();
				}
			}, this);
		}
	},
	//【溧水】调用大数据健康档案浏览器接口服务, 打开健康档案嵌入html zhaojian 2017-11-01
	//参数：action为接口服务的方法名
	openBHRView:function(json,action,frame1){
		var temp_form = document.createElement("form"); 
		//IP对应大数据健康档案浏览器接口服务地址
		temp_form .action = "http://10.2.202.56:8080/BHRView/api/v1/"+action+".do";  
        temp_form .method = "post";
        temp_form .style.display = "none"; 
        temp_form.enctype = "application/json";
        var opt
        for(var obj in json){
			opt = document.createElement("textarea");      
        	opt.name = obj;
        	opt.value = json[obj];
        	temp_form .appendChild(opt);
        }                 		 
		if(frame1==undefined){
        	temp_form .target = "_blank";
        	document.body.appendChild(temp_form);    
		}else{
        	temp_form .target = "_self";	
       		var iframe = Ext.isIE ? document.frames['ehr_frame'] : document
						.getElementById('ehr_frame');
       		iframe.contentWindow.document.body.appendChild(temp_form);
		}  
        temp_form .submit(); 
	}
}
