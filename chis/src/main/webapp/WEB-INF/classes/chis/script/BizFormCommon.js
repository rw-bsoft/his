/**
 * 公共文件(表单页面公共函数)
 * 
 * @author : yaozh
 */
$package("chis.script")

chis.script.BizFormCommon = {
	labelAlign : "left",
	loadDataByDefaultValue : true,
	// showButtonOnTop : true,

	getRawValues : function(saveData) {
		if (!saveData) {
			return null;
		}
		var form = this.form.getForm();
		var values = {};
		var items = this.schema.items
		if (items) {
			var n = items.length
			for ( var i = 0; i < n; i++) {
				var it = items[i]
				var v = saveData[it.id]
				if (v == undefined || v == null || v == "") {
					continue;
				}
				if (!it.dic) {
					continue;
				}
				var f = form.findField(it.id)
				if (f) {
					values[it.id + "_text"] = f.getRawValue();
				}
			}
		}
		return values;
	},

	beforeInitFormData : function(data) {
		var control = data["_actions"];
		if (!control) {
			return;
		}
		Ext.apply(this.exContext.control, control);
	},

	/**
	 * 根据传递的标识来设置页面某个控件是否可用
	 * 
	 * @param {}
	 *            disabled 标识(true/false)
	 * @param {}
	 *            fieldName 控件名称
	 */
	changeFieldState : function(disabled, fieldName) {
		var field = this.form.getForm().findField(fieldName);
		if (!field) {
			return;
		}
		if (disabled) {
			field.reset();
			field.disable();
		} else {
			field.enable();
		}
	},

	changeButtonState : function(disable, index) {
		if (this.form.getTopToolbar()) {
			if (disable) {
				this.form.getTopToolbar().items.item(index).disable()
			} else {
				this.form.getTopToolbar().items.item(index).enable()
			}
		} else {
			var btns = this.form.buttons;
			if (!btns) {
				return;
			}
			if (disable) {
				btns[index].disable()
			} else {
				btns[index].enable()
			}

		}
	},

	/**
	 * 控制页面按钮权限
	 */
	resetButtons : function() {
		if (this.showButtonOnTop && this.form.getTopToolbar()) {
			var btns = this.form.getTopToolbar().items;
			if (!btns) {
				return;
			}
			var n = btns.getCount();
			for ( var i = 0; i < n; i++) {
				var btn = btns.item(i)
				this.setButtonControl(btn);
			}
		} else {
			var btns = this.form.buttons;
			if (!btns) {
				return;
			}
			for ( var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				this.setButtonControl(btn);
			}
		}
	},

	/**
	 * 判断item是否已经在schema中存在
	 * 
	 * @param {}
	 *            schema
	 * @param {}
	 *            item
	 * @return {Boolean}
	 */
	findSchemaItemExists : function(schema, item) {
		for ( var i = 0; i < schema.items.length; i++) {
			var schemaItem = schema.items[i];
			if (schemaItem.id == item.id)
				return true;
		}
		return false;
	},

	/**
	 * 弹出打印窗口，执行打印
	 */
	doPrint : function() {
		var w = this.printPreview({
			iconCls : "print"
		});
		if (Ext.isIE) {
			w.print();
		} else {
			w.onload = function() {
				w.print();
			}
		}
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
		var ids_str = {};
		type.value = 1;
		var lhref = location.href;
		var lastIndex = lhref.lastIndexOf("/");
		if (!this.fireEvent("beforePrint", type, pages, ids_str)) {
			return;
		}
		var url = "resources/" + pages.value + ".print?type=" + type.value + ids_str.value
//		var url = lhref.substring(0, lastIndex + 1) + "form.print?type="
//				+ type.value + ids_str.value + "&pages=" + pages.value;
		var printWin = window.open(url, "", "height=" + (screen.height - 100)
				+ ", width=" + (screen.width - 10)
				+ ", top=0, left=0, toolbar=no, menubar=yes"
				+ ", scrollbars=yes, resizable=yes,location=no, status=no");
		return printWin;
	},

	loadDataByLocal : function(body) {
		this.form.el.mask("正在载入数据...", "x-mask-loading")
		this.doNew();
		if (body && body[this.entryName + "_data"]) {
			var data = body[this.entryName + "_data"];
			if (data[this.schema.pkey]) {
				this.initDataId = data[this.schema.pkey]
				this.op = "update";
			}
			this.initFormData(data);
			if (data["_actions"]) {
				this.exContext.control = data["_actions"]
			}
		} else {
			this.initDataId = null;
		}
		this.form.el.unmask()
		this.fireEvent("loadDataByLocal", this.entryName, data, this.op)
	},

	updateView : function(result) {

	},

	fixSaveCfg : function(saveCfg) {
		saveCfg.serviceAction = this.saveAction || "";
	},

	fixLoadCfg : function(loadCfg) {
		loadCfg.serviceAction = this.loadAction || "";
	},

	afterDoNew : function() {
		this.validate();
		this.resetButtons();
		if (this.initDataId) {
			this.fireEvent("beforeUpdate", this); // ** 在数据加载之前做一些初始化操作
		} else {
			this.fireEvent("beforeCreate", this); // ** 在页面新建时做一些初始化操作
		}
	},

	changeFieldCfg : function(it,cfg) {
		if (it['showRed']) {
			cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
					+ "</span>"
		}
		if (it['not-null'] == "1" || it['not-null'] =="true") { 
			cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
					+ "</span>"
		}
	},

	getParentCMP : function() {
		return this.form.findParentBy(function(ct, form) {
			if (ct.items.itemAt(0).getId() == form.getId()) {
				return true;
			} else {
				return false
			}
		})
	},

	getFormHeight : function() {
		var parent = this.getParentCMP();
		if (parent) {
			return parent.getHeight();
		} else {
			return Ext.getBody().getHeight();
		}
	},

	getFormWidth : function() {
		var parent = this.getParentCMP();
		if (parent) {
			return parent.getWidth();
		} else {
			return Ext.getBody().getWidth();
		}
	},	
	// 药品字典
	createAreaGridField : function(it) {
		var mds_reader = this.getAreaGridReader();
		// store远程url
		// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
		var url = ClassLoader.serverAppUrl || "";
		var comboJsonData = {
			serviceId : "chis.CommonService",
			serviceAction : "loadAreaGridData",
			method : "execute",
			className : "AreaGridAllCHIS",
			filterType:it.filterType||"bottom"
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
		var remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="60px">{parentName3}</td><td width="60px">{parentName4}</td><td width="80px">{parentName5}</td><td width="160px">{regionName}</td>';;
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + remoteTpl + '</tr>', '</table>', '</div>', '</tpl>');
		var _ctx = this;
		var initConfig={
				// id : "YPMC",
				selectData : {},
				width : it.width || 120,
				name : it.id,
				fieldLabel : it.alias,
				labelSeparator:":",
				store : mdsstore,
				selectOnFocus : true,
				typeAhead : false,
				loadingText : '搜索中...',
				pageSize : 10,
				hideTrigger : true,
				minListWidth : it.minListWidth || 360,
				tpl : resultTpl,
				minChars : 1,
				listWidth:it.listWidth||430,
				enableKeyEvents : true,
				lazyInit : false,
				itemSelector : 'div.search-item',
				onSelect : function(record) { // override default
					// onSelect
					// to do
					this.bySelect = true;
					_ctx.setAreaGridBackInfo(this, record, it.afterSelect);
					// this.hasFocus = false;// add by yangl
					// 2013.9.4
					// 解决新增行搜索时重复调用setBack问题
				}
			};
		this.changeFieldCfg(it,initConfig);
		if(it.otherConfig)
		{
			Ext.apply(initConfig,it.otherConfig);
		}
		var remoteField = new Ext.form.ComboBox(initConfig);
		
		remoteField.getAreaCodeValue=function()
		{
			return remoteField.selectData.regionCode;
		}
		
		remoteField.comboJsonData;
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);

//		remoteField.on("blur", function(t) {
//					var a = t.getValue();
//					if (t.getValue() != t.selectData.YPMC) {
//						t.reset();
//						if (it.afterClear) {
//							if (it.afterClear instanceof Function)
//								it.afterClear(t);
//						}
//					}
//				}, this);

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
								_ctx.setAreaGridBackInfo(obj, record,
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
	setAreaGridBackInfo : function(obj, record, afterSelect) {
		obj.setValue(record.get("regionName"));
		obj.selectData = {};
		obj.selectData = record.data;
		obj.collapse();
		if (afterSelect) {
			if (afterSelect instanceof Function)
				afterSelect(obj, record);
			
		}
		obj.fireEvent('afterSelect',obj);
	},
	getAreaGridReader : function() {
		var id = "areaGridSearch_" + Ext.id();
		var fields = [{
					name : 'numKey'
				},{
					name : 'parentName3'
				},{
					name : 'parentName4'
				},{
					name : 'parentName5'
				},{
					name : 'regionName'
				},{
					name : 'regionCode'

				}];
		return new Ext.data.JsonReader({
					root : 'ags',
					totalProperty : 'count',
					id : id
				}, fields);
	}
}