/**
 * 个人信息管理综合模块中的“其他属性”可编辑列表页面
 * 
 * @author tianj
 */
$package("chis.application.mpi.script.SubTableList");

$import("app.modules.list.EditorListView", "chis.application.mpi.script.SubTableForm",
		"util.dictionary.DictionaryLoader");

chis.application.mpi.script.SubTableEditList = function(cfg) {
	cfg.modified = false;
	this.disablePagingTbr = true ;
	chis.application.mpi.script.SubTableEditList.superclass.constructor.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this)
	this.on("beforeCellEdit",this.onBeforeCellEdit,this)
}

Ext.extend(chis.application.mpi.script.SubTableEditList, app.modules.list.EditorListView, {
	beforeCellEdit : function(e) {
		var f = e.field;
		var record = e.record;
		var op = record.get("_opStatus");
		var cm = this.grid.getColumnModel();

		var c = cm.config[e.column];
		var enditor = cm.getCellEditor(e.column);
		var it = c.schemaItem;
		var ac = util.Accredit;
		if (op == "create") {
			if (!ac.canCreate(it.acValue)) {
				return false;
			}
		} else {
			if (!ac.canUpdate(it.acValue)) {
				return false;
			}
		}
		if (it.dic) {
			e.value = {
				key : e.value,
				text : record.get(f + "_text")
			};
		} else {
			e.value = e.value || "";
		}
				
		if (this.fireEvent("beforeCellEdit", it, record, enditor.field,e.value, e)) {
			return true;
		}
	},
	
	onBeforeCellEdit : function(it, record, field, value, e) {
		var cm = e.grid.getColumnModel();
		if(e.column == 0) {
			cm.setEditable(0,false);
		}
	},
	
	onAfterCellEdit : function() {
		this.fireEvent("contentChanged", this.entryName);
	},

	onReady : function() {
		// 新建记录时不允许刷新。
		chis.application.mpi.script.SubTableEditList.superclass.onReady.call(this);
		this.fillStore();
	},

	onStoreBeforeLoad : function() {
		if (!this.empiId) {
			return false;
		}
	},

	reset : function() {
		this.modified = false;
		if (!this.store) {
			return;
		}
		for (var i = 0; i < this.store.getCount(); i++) {
			var rec = this.store.getAt(i);
			rec.set("propValue", "");
		}
	},

	refresh : function() {
		if (!this.empiId) {
			return;
		}
		chis.application.mpi.script.SubTableList.superclass.refresh.call(this);
	},

	onStoreLoadData : function() {
		this.fillStore();
	},

	fillStore : function() {
		if (!this.store) {
			return;
		}
		// 字典项目填充。
		if (!this.dic) {
			this.dic = util.dictionary.DictionaryLoader.load({
						id : "chis.dictionary.mpiExtension"
					});
		}

		var records = new Array();
		for (var i = 0; i < this.dic.items.length; i++) {
			var rec = {
				"propName" : this.dic.items[i].key,
				"propName_text" : this.dic.items[i].text
			};
			var value = this.findValue(this.dic.items[i].key);
			if (value) {
				rec["propValue"] = value;
			}
			if(!value){
				rec["propValue"] = "";
			}
			records.push(new Ext.data.Record(rec));
		}
		this.store.removeAll();
		this.store.add(records);
	},
	
	findValue : function(key) {
		if (!this.store || this.store.getCount() == 0){
			return;
		}
		for (var i = 0; i < this.store.getCount(); i++) {
			var r = this.store.getAt(i);
			if (r.get("propName") == key){
				return r.get("propValue");
			}
		}
	}
})
