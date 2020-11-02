$package("phis.script")

$import("app.modules.form.SimpleFormView", "util.helper.Helper",
		"util.widgets.LookUpField", "phis.script.common",
		"util.widgets.ImageField", "app.modules.config.ImageFieldEx",
		"phis.script.widgets.DatetimeField", "phis.script.widgets.Spinner",
		"phis.script.rmi.jsonRequest", "phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync")

phis.script.SimpleForm = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.remoteUrl = 'MedicineAll'
	this.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td>';
	Ext.apply(cfg, phis.script.common)
	phis.script.SimpleForm.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.script.SimpleForm, app.modules.form.SimpleFormView, {
	onChange : function(f, v) {
		// alert(f.name+":"+v+":"+f.srcField)
		v = v.replace(/^\s+|\s+$/g, "");
		if (v != "") {
			var body = {};
			body.codeType = f.codeType;
			body.value = v;
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "codeGeneratorService",
						serviceAction : "getCode",
						body : body
					});

			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg, this.onChange);
				return;
			}
			for (var i = 0; i < f.codeType.length; i++) {
				var srcField = this.form.getForm().findField(f.srcField[i]);
				srcField.setValue(eval('ret.json.body.' + f.codeType[i]));
			}

		}
	},
	onReady : function() {
		// add by yangl 添加是否提示修改
		// add by yangl 初始化简码字段监听事件
		if (this.CodeFieldSet) {
			for (var i = 0; i < this.CodeFieldSet.length; i++) {
				var CodeField = this.CodeFieldSet[i];
				var target = CodeField[0];
				var codeType = CodeField[1];
				var srcField = CodeField[2];
				var field = this.form.getForm().findField(target);
				var s_field = this.form.getForm().findField(srcField);
				if (s_field) {
					s_field.on("change", function(p, v) {
								this.setValue(v.toUpperCase());
							})
				}
				if (field) {
					if (!field.codeType)
						field.codeType = [];
					field.codeType.push(codeType)
					if (!field.srcField)
						field.srcField = [];
					field.srcField.push(srcField);
					if (!field.hasOnChange) {
						field.hasOnChange = true;
						field.on("change", this.onChange, this);
					}
				}
			}
		}
		if (this.autoLoadData) {
			this.loadData();
		}
		var el = this.form.el
		if (!el) {
			return
		}
		var actions = this.actions
		if (!actions) {
			return
		}
		var f1 = 112
		var keyMap = new Ext.KeyMap(el)
		keyMap.stopEvent = true

		var btnAccessKeys = {}
		var keys = []
		if (this.showButtonOnTop && this.form.getTopToolbar()) {
			var btns = this.form.getTopToolbar().items;
			if (btns) {
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
			var btns = this.form.buttons
			if (btns) {
				for (var i = 0; i < btns.length; i++) {
					var btn = btns[i]
					var key = btn.accessKey
					if (key) {
						btnAccessKeys[key] = btn
						keys.push(key)
					}
				}
			}
		}
		this.btnAccessKeys = btnAccessKeys
		// keyMap.on(keys, this.onAccessKey, this)
		if (this.win) {
			keyMap.on({
						key : Ext.EventObject.ESC,
						shift : true
					}, this.onEsc, this)
		}
	},
	/**
	 * 获取页面数据
	 */
	getFormData : function() {
		var ac = util.Accredit;
		var form = this.form.getForm()
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items
		Ext.apply(this.data, this.exContext.empiData)
		if (items) {
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
				if (v == undefined) {
					v = it.defaultValue
					if (it.type == "datetime" && this.op == "create") {//
						// update
						// by
						// caijy
						// 2013-3-21
						// for
						// 新增页面的时间动态生成
						v = Date.getServerDateTime();
					}
				}
				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					v = f.getValue()
					// add by caijy from checkbox
					if (f.getXType() == "checkbox") {
						var checkValue = 1;
						var unCheckValue = 0;
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
						}
						if (v == true) {
							v = checkValue;
						} else {
							v = unCheckValue;
						}
					}
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
					// end
				}

				if (v == null || v === "") {
					if (!it.pkey && it["not-null"] && !it.ref) {
						Ext.Msg.alert("提示", it.alias + "不能为空")
						return;
					}
				}
				if (it.type && it.type == "int") {
					v = (v == "0" || v == "" || v == undefined)
							? 0
							: parseInt(v);
				}
				values[it.id] = v;
			}
		}
		return values;
	},
	loadData : function() {
		if (this.loadDataByDefaultValue) {
			this.doNew();
		} else {
			this.doNew(1);
		}
		if (this.loading) {
			return
		}
		if (!this.schema) {
			return
		}
		var loadRequest = this.getLoadRequest();
		if (!this.initDataId && !this.initDataBody && !loadRequest) {
			return;
		}
		if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId
						|| this.initDataBody, loadRequest)) {
			return
		}
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading")
		}
		this.loading = true
		var loadCfg = {
			serviceId : this.loadServiceId,
			method : this.loadMethod,
			schema : this.entryName,
			pkey : this.initDataId || this.initDataBody,
			body : loadRequest,
			action : this.op, // 按钮事件
			module : this._mId
			// 增加module的id
		}
		this.fixLoadCfg(loadCfg);
		phis.script.rmi.jsonRequest(loadCfg, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg)
						this.fireEvent("exception", code, msg, loadRequest,
								this.initDataId || this.initDataBody); // **
						// 用于一些异常处理
						return
					}
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
					} else {
						this.initDataId = null;
						// **
						// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
						this.fireEvent("loadNoData", this);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)
	},
	/**
	 * 重写父类doSave方法，将获取数据部分代码独立出来，方便独立调用
	 */
	doSave : function() {
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		// alert(this.data["SJBM"]);

		Ext.apply(this.data, values);
		// alert(this.data["JGID"]);
		// return;
		this.saveToServer(values);
	},

	/**
	 * 保存数据到后台数据库，请求中增加参数serviceAction，指定调用服务中的哪个方法
	 * 
	 * @param {}
	 *            saveData 需要保存的数据
	 */
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}

		this.saving = true;

		this.form.el.mask("正在保存数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction || "",
					method : "execute",
					op : this.op,
					schema : this.entryName,
					module : this._mId, // 增加module的id
					body : saveRequest
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], json.body);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					this.op = "update"
					MyMessageTip.msg("提示", "保存成功!", true)
					if(this.entryName=="chis.application.idr.schemas.IDR_Report"){
				        util.rmi.miniJsonRequestAsync({
						serviceId : "phis.messageService",
						serviceAction : "sendMessage",
						method : "execute"
					}, function(code, msg, json) {	
						if(json && json.body){
							if(json.body.successSend!=""){
								MyMessageTip.msg("提示", '已短信通知审核人（'+json.body.successSend+"） ", true);
							}
							if(json.body.failSend!=""){
								MyMessageTip.msg("提示", '短信通知失败：'+json.body.failSend, true);
							}
						}
				}, this);		
				}
				}, this)// jsonRequest
	},
	/**
	 * 获取保存数据的请求数据
	 * 
	 * @return {}
	 */
	getSaveRequest : function(saveData) {
		var values = saveData;
		return values;
	},
	/**
	 * form 页面增加打印功能
	 */

	doPrint : function() {
		this.printPreview();
	},

	/**
	 * 打印窗口
	 * 
	 * @param {}
	 *            btn
	 */
	printPreview : function(btn) {
		var type = {};
		var pages = {};
		// var ids_str = {};
		type.value = 1;
		// var lhref = location.href;
		// var lastInd ex = lhref.lastIndexOf("/");
		if (!this.fireEvent("beforePrint", type, pages)) {
			return;
		}
		var url = this.printurl + "print?type=" + type.value + "&pages="
				+ pages.value;
		var printWin = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes"
								+ ", scrollbars=yes, resizable=yes,location=no, status=no");
		return printWin;
	},
	createField : function(it) {
		var ac = util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		var cfg = {
			name : it.id,
			fieldLabel : it.alias,
			xtype : it.xtype || "textfield",
			vtype : it.vtype,
			width : defaultWidth,
			value : it.defaultValue,
			enableKeyEvents : it.enableKeyEvents,
			validationEvent : it.validationEvent,
			labelSeparator : ":"
		}
		if (it.hideLabel) {
			delete cfg.fieldLabel;
			cfg.hideLabel = true;
		}
		cfg.listeners = {
			specialkey : this.onFieldSpecialkey,
			// add by liyl 2012-06-17 去掉输入字符串首位空格
			blur : function(e) {
				if (typeof(e.getValue()) == 'string') {
					e.setValue(e.getValue().trim())
				}
			},
			scope : this
		}
		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it.editable) {
			cfg.editable = (it.editable == "true") ? true : false
		}
		if (it['not-null'] == "1" || it['not-null']) {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
			cfg.regex = /(^\S+)/
			cfg.regexText = "前面不能有空格字符"
		}
		// add by yangl 增加readOnly属性
		if (it.readOnly) {
			cfg.readOnly = true
			// cfg.unselectable = "on";
			cfg.style = "background:#E6E6E6;cursor:default;";
			cfg.listeners.focus = function(f) {
				f.blur();
			}
		}
		if (it.fixed) {
			cfg.disabled = true
		}
		if (it.pkey && it.generator == 'auto') {
			cfg.disabled = true
		}
		if (it.evalOnServer && ac.canRead(it.acValue)) {
			cfg.disabled = true
		}
		if (this.op == "create" && !ac.canCreate(it.acValue)) {
			cfg.disabled = true
		}
		if (this.op == "update" && !ac.canUpdate(it.acValue)) {
			cfg.disabled = true
		}
		// add by yangl,modify simple code Generation methods
		if (it.codeType) {
			if (!this.CodeFieldSet)
				this.CodeFieldSet = [];
			this.CodeFieldSet.push([it.target, it.codeType, it.id]);
		}
		if (it.properties && it.properties.mode == "remote") {
			// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
			return this.createRemoteDicField(it);
		} else if (it.dic) {
			// add by lyl, check treecheck length
			if (it.dic.render == "TreeCheck") {
				if (it.length) {
					cfg.maxLength = it.length;
				}
			}

			it.dic.src = this.entryName + "." + it.id
			it.dic.defaultValue = it.defaultValue
			it.dic.width = defaultWidth
			if (it.dic.fields) {
				if (typeof(it.dic.fields) == 'string') {
					var fieldsArray = it.dic.fields.split(",")
					it.dic.fields = fieldsArray;
				}
			}
			var combox = this.createDicField(it.dic)
			Ext.apply(combox, cfg)
			combox.on("specialkey", this.onFieldSpecialkey, this)
			return combox;
		}
		if (it.dic) {
			// add by lyl, check treecheck length
			if (it.dic.render == "TreeCheck") {
				if (it.length) {
					cfg.maxLength = it.length;
				}
			}
			it.dic.src = this.entryName + "." + it.id
			it.dic.defaultValue = it.defaultValue
			it.dic.width = defaultWidth
			var combox = this.createDicField(it.dic)
			this.changeFieldCfg(it, cfg);
			Ext.apply(combox, cfg)
			combox.on("specialkey", this.onFieldSpecialkey, this)
			return combox;
		}
		if (it.length) {
			cfg.maxLength = it.length;
		}
		if (it.maxValue) {
			cfg.maxValue = it.maxValue;
		}
		// update by caijy for minValue=0时无效的BUG
		if (it.minValue || it.minValue == 0) {
			cfg.minValue = it.minValue;
		}
		if (it.xtype) {
			if (it.xtype == "htmleditor") {
				cfg.height = it.height || 200;
			}
			if (it.xtype == "textarea") {
				cfg.height = it.height || 65
			}
			if (it.xtype == "datefield"
					&& (it.type == "datetime" || it.type == "timestamp")) {
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
			}
			this.changeFieldCfg(it, cfg);
			return cfg;
		}
		switch (it.type) {
			case 'int' :
			case 'double' :
			case 'bigDecimal' :
				cfg.xtype = "numberfield";
				cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
				if (it.type == 'int') {
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				} else {
					cfg.decimalPrecision = it.precision || 2;
				}
				break;
			case 'date' :
				cfg.xtype = 'datefield'
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
				if (it.maxValue && typeof it.maxValue == 'string'
						&& it.maxValue.length > 10) {
					cfg.maxValue = it.maxValue.substring(0, 10);
				}
				if (it.minValue && typeof it.minValue == 'string'
						&& it.minValue.length > 10) {
					cfg.minValue = it.minValue.substring(0, 10);
				}
				break;
			case 'datetime' :
				cfg.xtype = 'datetimefieldEx'
				cfg.emptyText = "请选择日期时间"
				cfg.format = 'Y-m-d H:i:s'
				break;
			case 'text' :
				cfg.xtype = "htmleditor"
				cfg.enableSourceEdit = false
				cfg.enableLinks = false
				cfg.width = 300
				cfg.height = 180
				break;
		}
		this.changeFieldCfg(it, cfg);
		return cfg;
	},
	onFieldSpecialkey : function(f, e) {
		var key = e.getKey()
		var minChars;
		if (key == e.ENTER) {
			e.stopEvent()
			if (f.minChars) {
				minChars = f.minChars;
				f.minChars = 99;
				setTimeout(function() {
							f.minChars = minChars;
						}, 1000);
			}
			this.quickPickMCode(f)
			// add by yangl 回车事件只有在字段校验通过的时候才转移焦点
			if (f.validate) {
				if (f.validate()) {
					// add by yangl 2013.10.25
					// schema增加配置项nextFocus,指定跳转到那个field字段
					var items = this.schema.items;
					var item = items[f.index];
					if (item && item.nextFocus) {
						if (item.nextFocus.indexOf("btn_") >= 0) {
							this.focusToBtn(item.nextFocus.substr(4));
						} else if (item.nextFocus.indexOf("fire_") >= 0) {
							this.fireEvent(item.nextFocus.substr(5), this)
						} else {
							var fd = this.form.getForm()
									.findField(item.nextFocus);
							if (!fd) {
								this.focusFieldAfter(f.index)
							} else {
								fd.focus(false, 200);
							}
						}
					} else {
						this.focusFieldAfter(f.index)
					}
				} else {
					MyMessageTip.msg("提示", f.fieldLabel + ":" + f.activeError,
							true)
				}
			}
		}
		if (e.getKey() == e.BACKSPACE && (f.disabled || f.readOnly)) {
			e.stopEvent();
		}

	},
	focusToBtn : function(btnName) {
		var btns;
		if (this.showButtonOnTop) {
			btns = this.form.getTopToolbar().items
			if (btns) {
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					if (btn.cmd == btnName) {
						if (btn.rendered) {
							btn.focus()
						}
						return;
					}
				}
			}
		} else {
			btns = this.form.buttons;
			if (btns) {
				var n = btns.length
				for (var i = 0; i < n; i++) {
					var btn = btns[i]
					if (btn.cmd == btnName) {
						if (btn.rendered) {
							btn.focus()
						}
						return;
					}
				}
			}
		}
	},
	doNew : function() {
		this.op = "create"
		if (this.data) {
			this.data = {}
		}
		if (!this.schema) {
			return;
		}
		var form = this.form.getForm();
		form.reset();
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				if (!(arguments[0] == 1)) { // whether set defaultValue, it will
					// be setted when there is no args.
					var dv = it.defaultValue;
					if (dv) {
						if ((it.type == 'date' || it.xtype == 'datefield')
								&& typeof dv == 'string' && dv.length > 10) {
							dv = dv.substring(0, 10);
						}
						f.setValue(dv);
					}
				}
				if (!it.fixed && !it.evalOnServer) {// modify by yangl:remove
					// update
					f.enable();
				}
				// add by yangl 2012-06-29
				if (it.dic && it.dic.defaultIndex) {
					if (f.store.getCount() == 0)
						continue;
					if (isNaN(it.dic.defaultIndex)
							|| f.store.getCount() <= it.dic.defaultIndex)
						it.dic.defaultIndex = 0;
					f.setValue(f.store.getAt(it.dic.defaultIndex).get('key'));
				}
				f.validate();
			}
		}
		this.setKeyReadOnly(false)
		this.startValues = form.getValues(true);
		this.fireEvent("doNew", this.form)
		this.focusFieldAfter(-1, 800)
		this.afterDoNew();
		this.resetButtons();
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
						}]);
	},
	createRemoteDicField : function(it) {
		var mds_reader = this.getRemoteDicReader();
		// store远程url
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
							this.processReturnMsg(code, msg, this.refresh)
						}
					} else {
						this.processReturnMsg(404, 'ConnectionError',
								this.refresh)
					}
				}, this)
		this.remoteDicStore = mdsstore;
		Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + this.remoteTpl + '<tr>', '</table>', '</div>',
				'</tpl>');
		var _ctx = this;
		var width = 280;
		if (it.properties.width) {
			width = parseInt(it.properties.width);
		}
		var cfg = {
			name : it.id,
			fieldLabel : it.alias,
			width : width,
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
			onSelect : function(record) { // override default onSelect
				// to do
				_ctx.setBackInfo(this, record);
				this.collapse();
			}
		}
		if (it['showRed']) {
			cfg.fieldLabel = "<span style='color:red'>" + it.alias + "</span>"
		}
		if (it['not-null'] == "1" || it['not-null']) {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
			cfg.regex = /(^\S+)/
			cfg.regexText = "前面不能有空格字符"
			cfg.fieldLabel = "<span style='color:red'>" + it.alias + "</span>"
		}
		var remoteField = new Ext.form.ComboBox(cfg);
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);
		remoteField.on("keyup", function(obj, e) {// 实现数字键导航
					var key = e.getKey();
					if (key == e.ENTER && !obj.isExpanded()) {
						// 是否是字母
						if (key == e.ENTER) {
							if (!obj.isExpanded()) {
								// 是否是字母
								var patrn = /^[a-zA-Z.]+$/;
								if (patrn.exec(obj.getValue())) {
									// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
									obj.getStore().removeAll();
									obj.lastQuery = "";
									if (obj.doQuery(obj.getValue(), true) !== false) {
										e.stopEvent();
										return;
									}
								}
							}
							_ctx.focusFieldAfter(obj.index);
							return;
						}
						var patrn = /^[a-zA-Z.]+$/;
						if (patrn.exec(obj.getValue())) {
							// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
							obj.getStore().removeAll();
							obj.lastQuery = "";
							if (obj.doQuery(obj.getValue(), true) !== false) {
								e.stopEvent();
								return;
							}
						}
					}
					if ((key >= 48 && key <= 57) || (key >= 96 && key <= 105)) {
						if (obj.isExpanded()) {
							if (key == 48 || key == 96)
								key = key + 10;
							key = key < 59 ? key - 49 : key - 97;
							var record = this.getStore().getAt(key);
							_ctx.setBackInfo(obj, record);
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
		remoteField.on("beforequery", function(qe) {
					this.comboJsonData.query = qe.query;
					return true;
				}, this);
		remoteField.isSearchField = true;
		this.remoteDic = remoteField;
		return remoteField
	},
	setBackInfo : function(obj, record) {
		// 将选中的记录设置到行数据中，继承后实现具体功能
	},
	// add by caijy for checkbox
	initFormData : function(data) {
		Ext.apply(this.data, data)
		this.initDataId = this.data[this.schema.pkey]
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				var v = data[it.id]
				if (v != undefined) {
					if (f.getXType() == "checkbox") {
						var setValue = "";
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
							if (v == checkValue) {
								setValue = true;
							} else if (v == unCheckValue) {
								setValue = false;
							}
						}
						if (setValue == "") {
							if (v == 1) {
								setValue = true;
							} else {
								setValue = false;
							}
						}
						f.setValue(setValue);
					} else {
						if (it.dic && v !== "" && v === 0) {// add by yangl
							// 解决字典类型值为0(int)时无法设置的BUG
							v = "0";
						}
						f.setValue(v)
						if (it.dic && v != "0" && f.getValue() != v) {
							f.counter = 1;
							this.setValueAgain(f, v, it);
						}

					}
				}
				if (it.update == "false") {
					f.disable();
				}
			}
			this.setKeyReadOnly(true)
			this.focusFieldAfter(-1, 800)
		}
	},
	setValueAgain : function(f, v, it) {
		if (typeof(v) == 'object' || f.counter > 20)// 重新设置20次，不成功停止
			return;
		// MyMessageTip.msg("提示", "重新设置" + it.id + "--" + v, true);
		f.setValue(v);
		var this_ = this;
		if (f.getValue() != v) {
			f.counter = f.counter + 1;
			setTimeout(function() {
						this_.setValueAgain(f, v, it)
					}, "100");
		}
	},
	getWin : function() {
		var win = this.win
		var closeAction = this.closeAction || "hide";
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
						modal : this.modal || true
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
	focusFieldAfter : function(index, delay) {
		var items = this.schema.items
		var form = this.form.getForm()
		for (var i = index + 1; i < items.length; i++) {
			var next = items[i]
			var field = form.findField(next.id)
			if (field && !field.disabled && !field.readOnly) {
				field.focus(false, delay || 200)
				return;
			}
		}
		var btns;
		if (this.showButtonOnTop) {
			if (this.form.getTopToolbar()) {
				btns = this.form.getTopToolbar().items
				if (btns) {
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						if (btn.cmd == "save") {
							if (btn.rendered) {
								btn.focus()
							}
							return;
						}
					}
				}
			}
		} else {
			btns = this.form.buttons;
			if (btns) {
				var n = btns.length
				for (var i = 0; i < n; i++) {
					var btn = btns[i]
					if (btn.cmd == "save") {
						if (btn.rendered) {
							btn.focus()
						}
						return;
					}
				}
			}
		}
	}
});
