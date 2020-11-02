$package("phis.application.cic.script")

$import("phis.script.EditorList")

phis.application.cic.script.ClinicPrescriptionComboList = function(cfg) {
	phis.application.cic.script.ClinicPrescriptionComboList.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.cic.script.ClinicPrescriptionComboList,
		phis.script.EditorList, {
			expansion : function (cfg) {
				var radioGroup = new Ext.form.RadioGroup({
					width : 150,
					items : [
					{boxLabel : '公共组套',inputValue:'1',name:'stack'}
					]
				});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(radioGroup,['-'],tbar);
			},
			
			doInsert : function(item, e, newGroup) {// 当前记录前插入一条记录
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = 0;
				if (cell) {
					row = cell[0];
				}
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
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v]
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
				}
				var r = new Record(data)
				store.insert(row, [r])
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("TS", "1");
				} else {
					var upRowItem = storeData.itemAt(row + 1);
					var rowItem = storeData.itemAt(row);
					rowItem.set("TS", upRowItem.get("TS"));
				}
				this.grid.startEditing(row, 1);

			},
			
			doInsertAfter : function(item, e, newGroup) {// 当前记录前插入一条记录
				this.doCreate();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("TS", "1");
				} else {

					var upRowItem = storeData.itemAt(maxIndex - 2);
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("TS", upRowItem.get("TS"));
				}
				this.grid.startEditing(maxIndex - 1, 1);

			},
			doNewGroup : function(item, e) {
				this.doInsertAfter(item, e, true);
			}
		})