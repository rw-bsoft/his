$package("util.dictionary")
$import("util.widgets.MyCombox", "util.dictionary.DictionaryLoader",
		"util.dictionary.HttpProxy")

util.dictionary.SimpleDicFactory = {
	getUrl : util.dictionary.DictionaryLoader.getUrl,
	getStore : function(dic) {
		var url = this.getUrl(dic);
		var proxy = new util.dictionary.HttpProxy({ // new Ext.data.HttpProxy
			method : "GET", // make cache,upper
			url : url
		});
		var store = new Ext.data.JsonStore({
					proxy : proxy,
					totalProperty : 'result',
					root : 'items',
					fields : dic.fields
							|| ['key', 'text', dic.searchField || "mCode"]
				})
		return store
	},
	getCombox : function(dic) {
		var combox = new util.widgets.MyCombox({
					store : this.getStore(dic),
					valueField : "key",
					displayField : "text",
					searchField : dic.searchField || "mCode",
					editable : (dic.editable == 'false') ? false : true,
					minChars : (!dic.remote ? 1 : 2),
					queryDelay : 10,
					selectOnFocus : true,
					triggerAction : dic.remote ? "query" : "all",
					emptyText : dic.emptyText || '请选择',
					pageSize : dic.pageSize,
					width : dic.width || 200,
					value : dic.defaultValue || dic.value,
					forceSelection : dic.forceSelection || false,
					customInput : dic.customInput || false // 允许用户输入下拉框以外的值
				})
		return combox
	},
	createDic : function(dic) {
		var combox = this.getCombox(dic)
		if (!dic.remote) {
			combox.mode = 'local';
			combox.store.filter = function(property, value, anyMatch,
					caseSensitive, exactMatch) {
				var fn;
				// we can accept an array of filter objects, or a single filter
				// object - normalize them here
				if (Ext.isObject(property)) {
					property = [property];
				}

				if (Ext.isArray(property)) {
					var filters = [];

					// normalize the filters passed into an array of filter
					// functions
					for (var i = 0, j = property.length; i < j; i++) {
						var filter = property[i], func = filter.fn, scope = filter.scope
								|| this;

						// if we weren't given a filter function, construct one
						// now
						if (!Ext.isFunction(func)) {
							func = this.createFilterFn(filter.property,
									filter.value, filter.anyMatch,
									filter.caseSensitive, filter.exactMatch);
						}

						filters.push({
									fn : func,
									scope : scope
								});
					}

					fn = this.createMultipleFilterFn(filters);
				} else {
					// classic single property filter
					// 判断输入是否是汉字
					//modify by zhaojian 2017-06-28 增加纯数字的判断
					if (!(/[\u4e00-\u9fa5]+/).test(value) && !(/^[0-9]*$/).test(value)) {
						property = dic.searchField;
					}
					fn = this.createFilterFn(property, value, anyMatch,
							caseSensitive, exactMatch);
				}

				return fn ? this.filterBy(fn) : this.clearFilter();
			};
			// combox.minChars = 99;
			if (dic.autoLoad || dic.defaultIndex) {
				// 默认选中第几行
				var defaultIndex = dic.defaultIndex;
				if (defaultIndex) {
					combox.store.on("load", function() {
								if (this.getCount() == 0)
									return;
								if (defaultIndex) {
									if (isNaN(defaultIndex)
											|| this.getCount() <= defaultIndex)
										defaultIndex = 0;
									combox.setValue(this.getAt(defaultIndex)
											.get('key'));
									defaultIndex = null;
								}
							})
				}
				combox.store.load()
			}
		}
		return combox;
	},
	createLocalStore : function(dic) {
		var store = new Ext.data.JsonStore({
					fields : ['key', 'text'],
					data : dic.data || []
				})
		return store
	},
	createLocalDic : function(dic) {
		var comb = new util.widgets.MyCombox({
					store : this.createLocalStore(dic),
					valueField : "key",
					displayField : "text",
					mode : "local",
					triggerAction : "all",
					emptyText : dic.emptyText || "请选择",
					value : dic.value,
					selectOnFocus : dic.selectOnFocus || false,
					editable : dic.editable || true,
					width : dic.width || 120
				});
		return comb
	}
}