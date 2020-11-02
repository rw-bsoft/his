$package("chis.application.ohr.script")

$import("chis.script.BizEditorListView", "util.dictionary.DictionaryLoader")

chis.application.ohr.script.OldPeopleCheckupList = function(cfg) {
	chis.application.ohr.script.OldPeopleCheckupList.superclass.constructor.apply(this, [cfg]);
	this.on("noRecord", this.onNoRecord, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(chis.application.ohr.script.OldPeopleCheckupList, chis.script.BizEditorListView, {
	onBeforeCellEdit : function(it, record, field, value) {
		if (it.id == "exceptionDesc") {
			var v = record.get("ifException");
			if (v == 1 || v.length == 0) {
				field.disable();
			} else {
				field.enable();
			}
		}
	},

	onAfterCellEdit : function(it, record, field, value) {
		if (it.id == "ifException") {
			var v = record.get("ifException");
			if (v == 1) {
				record.set("exceptionDesc", "");
			}
		}
	},

	loadData : function() {
		this.store.removeAll();
		if (this.recordId) {
			this.initCnd = ['eq', ['$', 'recordId'],['s', this.recordId]];
			this.requestData.cnd = this.initCnd;
			this.refresh();
		} else {
			this.getGridStore();
		}
		// ** add by yzh **
		this.resetButtons();
	},
	
	getGridStore : function() {
		var cls = "util.dictionary.DictionaryLoader";
		var dic;
		if (!dic) {
			dic = eval("(" + cls + ")");
		}
		var store = dic.load({
					id : "chis.dictionary.oldPeopleCheckup"
				});
		for (var j = 0; j < store.items.length; j++) {
			var storeItem = store.items[j];
			var items = this.schema.items;
			var r = {};
			for (var i = 0; i < items.length; i++) {
				var it = items[i];
				if (it.id == "indicatorName") {
					r[it.id] = storeItem["text"];
				} else if (it.id == "indicatorValue") {
					r[it.id] = storeItem["class"] || "";
				} else {
					r[it.id] = "";
				}
			}
			var record = new Ext.data.Record(r, storeItem["key"]);
			this.store.add(record);
		}
	},

	refresh : function() {
		if (this.store) {
			this.store.load();
		}
	},

	onStoreLoadData : function(store, records, ops) {
		if (records.length == 0) {
			this.fireEvent("noRecord");
			return;
		}
		this.fireEvent("loadData", store);
		if (!this.selectedIndex) {
			this.selectRow(0);
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
		}
	},
	
	onNoRecord : function() {
		this.getGridStore();
	},
	
	doSave : function() {
		this.fireEvent("save");
	}
});