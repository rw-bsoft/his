$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.EditorList")

com.bsoft.phis.pub.UserPropertiesEditorList = function(cfg) {
	// cf.requestData.cnd
	cfg.disablePagingTbr = true;
	com.bsoft.phis.pub.UserPropertiesEditorList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(com.bsoft.phis.pub.UserPropertiesEditorList,
		com.bsoft.phis.EditorList, {
			expansion : function(cfg) {
				cfg.colspan = 3;
				cfg.width = 820;
				cfg.height = 200;
			},
			doNew : function() {
				this.clear();
			},
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
			}
		});