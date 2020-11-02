/**
 * 体弱儿随访化验项目列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.visit")
$import("chis.script.BizEditorListView", "util.dictionary.DictionaryLoader")
chis.application.cdh.script.debility.visit.DebilityChildrenCheckList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.height = 390;
	cfg.disablePagingTbr = true;
	chis.application.cdh.script.debility.visit.DebilityChildrenCheckList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.cdh.script.debility.visit.DebilityChildrenCheckList,
		chis.script.BizEditorListView, {

			onReady : function() {
				this.store.on("add", this.onRecordsAdd, this);
				this.store.on("load", this.onRecordsAdd, this);
				chis.application.cdh.script.debility.visit.DebilityChildrenCheckList.superclass.onReady
						.call(this);
			},

			onRecordsAdd : function() {
				var cm = this.grid.getColumnModel();
				var count = this.store.getCount();
				for (var j = 0; j < count; j++) {
					var enditor = cm.getCellEditor(1, j);
					var field = enditor.field;
					field.on("specialkey", this.onResultSpecialkey, this);

					var enditor = cm.getCellEditor(2, j);
					var field = enditor.field;
					field.on("specialkey", this.onResultUnitSpecialkey, this);
				}
			},

			onResultSpecialkey : function(field, e) {
				if (e.getKey() != Ext.EventObject.ENTER) {
					return;
				}
				var selectCell = this.grid.getSelectionModel()
						.getSelectedCell();
				if (selectCell) {
					this.grid.getSelectionModel().select(selectCell[0],
							selectCell[1] + 1);
				}
			},

			onResultUnitSpecialkey : function(field, e) {
				if (e.getKey() != Ext.EventObject.ENTER) {
					return;
				}
				var selectCell = this.grid.getSelectionModel()
						.getSelectedCell();
				if (selectCell && selectCell[0] != 9) {
					this.grid.getSelectionModel().select(selectCell[0] + 1,
							selectCell[1] - 1);
				}
			},

			loadData : function() {
				var visitId = this.exContext.args.visitId;
				if (!visitId) {
					this.initDataId = null;
					return;
				}
				this.initDataId = visitId;
				this.initCnd = ['eq', ['$', 'visitId'], ['s', visitId]]
				this.requestData.cnd = this.initCnd;
				this.refresh();
				this.resetButtons()
			},

			getGridStore : function() {
				var cls = "util.dictionary.DictionaryLoader";
				var dic;
				if (!dic) {
					dic = eval("(" + cls + ")");
				}

				var store = dic.load({
							id : "chis.dictionary.debilityChildrenCheck"
						});
				var records = [];
				for (var j = 0; j < store.items.length; j++) {
					var storeItem = store.items[j];
					var items = this.schema.items;
					var r = {};
					for (var i = 0; i < items.length; i++) {
						var it = items[i];
						if (it.id == "checkCode") {
							r[it.id] = storeItem["key"];
						} else if (it.id == "checkName") {
							r[it.id] = storeItem["text"];
						} else if (it.id == "checkResult") {
							r[it.id] = storeItem["class"] || "";
						} else {
							r[it.id] = "";
						}
					}
					records[j] = new Ext.data.Record(r, storeItem["key"]);
				}
				this.store.add(records);
			},

			refresh : function() {
				if (this.store) {
					this.store.load();
				}
			},

			onStoreLoadData : function(store, records, ops) {
				if (store.getCount() == 0) {
					this.op = "create";
					this.getGridStore();
				} else {
					this.op = "update";
				}
			},

			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory";
				$import(cls);
				var factory = eval("(" + cls + ")");
				var field = factory.createDic(dic);
				field.on("focus", this.onDicFieldFocus, this)
				// field.store.on("load", this.onFieldStoreLoad, this);
				return field;
			},

			onDicFieldFocus : function(field) {
				var selectCell = this.grid.getSelectionModel()
						.getSelectedCell();
				if (!selectCell) {
					return;
				}
				var record2 = this.grid.store.getAt(selectCell[0]);
				var checkCode = record2.get("checkCode");
				// var cm = this.grid.getColumnModel();
				// var editor = cm.getCellEditor(selectCell[1], selectCell[0]);
				// var field = editor.field;
				field.store.removeAll();
				if (checkCode == "01") {
					field.store.add(new Ext.data.Record({
								key : "1",
								text : "g/L"
							}));
					field.setValue(1);
				} else if (checkCode == "02") {
					field.store.add(new Ext.data.Record({
								key : "2",
								text : "%"
							}));
					field.setValue(2);
				} else if (checkCode == "03") {
					field.store.add(new Ext.data.Record({
								key : "3",
								text : "fl"
							}));
					field.setValue(3);
				} else if (checkCode == "04") {
					field.store.add(new Ext.data.Record({
								key : "4",
								text : "pg"
							}));
					field.setValue(4);
				} else if (checkCode == "05") {
					field.store.add(new Ext.data.Record({
								key : "1",
								text : "g/L"
							}));
					field.store.add(new Ext.data.Record({
								key : "10",
								text : "g/dL"
							}));
				} else if (checkCode == "06") {
					field.store.add(new Ext.data.Record({
								key : "5",
								text : "U/L"
							}));
					field.setValue(5);
				} else if (checkCode == "07") {
					field.store.add(new Ext.data.Record({
								key : "6",
								text : "μg/dl"
							}));
					field.store.add(new Ext.data.Record({
								key : "7",
								text : "mmol/L"
							}));
					// field.setValue(6);
				} else if (checkCode == "08") {
					field.store.add(new Ext.data.Record({
								key : "6",
								text : "μg/dl"
							}));
					field.store.add(new Ext.data.Record({
								key : "8",
								text : "μmol/L"
							}));
					// field.setValue(6);
				} else if (checkCode == "09") {
					field.store.add(new Ext.data.Record({
								key : "6",
								text : "μg/dl"
							}));
					field.store.add(new Ext.data.Record({
								key : "8",
								text : "μmol/L"
							}));
					// field.setValue(6);
				} else if (checkCode == "10") {
					field.store.add(new Ext.data.Record({
								key : "7",
								text : "mmol/L"
							}));
					field.store.add(new Ext.data.Record({
								key : "9",
								text : "mg/dL"
							}));
					// field.value = {key : "7", text : "mmol/L"};
				}
				field.store.commitChanges();
			},

			getStoreData : function() {
				var count = this.store.getCount();
				var lst = [];
				for (var i = 0; i < count; i++) {
					var record = this.store.getAt(i);
					var items = this.schema.items;
					lst.push(record.data);
				}
				return lst;
			},

			doSave : function() {
				var saveData = {
					checkList : this.getStoreData(),
					visitId : this.exContext.args.visitId,
					empiId :  this.exContext.args.empiId ||  this.exContext.ids.empiId ,
					recordId : this.exContext.args.recordId
				};
				this.saveToServer(saveData);
			},

			saveToServer : function(saveData) {
				if (this.saving) {
					return
				}
				if (!saveData) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							method:"execute",
							op : this.op,
							body : saveData
						}, function(code, msg, json) {
							this.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							this.store.commitChanges();
							this.op = "update"
						}, this)
			}

		});