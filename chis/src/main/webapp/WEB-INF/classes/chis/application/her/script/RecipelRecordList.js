$package("chis.application.her.script");

$import("chis.script.BizSimpleListView");

chis.application.her.script.RecipelRecordList = function(cfg){
	chis.application.her.script.RecipelRecordList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.her.script.RecipelRecordList,chis.script.BizSimpleListView,{
	doAdd : function() {
		var module = this.createSimpleModule("RecipelRecordForm",
				this.createRef);
		module.initPanel();
		module.on("save", this.onSave, this);
		module.initDataId = null;
		module.op = "create"
		this.showWin(module);
		module.doNew();
	},
	doModify : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var module = this.createSimpleModule("RecipelRecordForm",
				this.createRef);
		module.initPanel();
		module.on("save", this.onSave, this);
		module.initDataId = r.id;
		this.showWin(module);
		module.loadData(); 
	},
	onSave:function(){
		this.loadData()
	}
	,
	doRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		Ext.Msg.show({
					title : '确认删除记录',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processRemove();
						}
					},
					scope : this
				})
	}
	,
	onRowClick : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return
		}
		
		var bts = this.grid.getTopToolbar().items;
		if(bts.items[7]){
			var createUser = r.get("createUser");
			if (createUser == this.mainApp.uid) {
				bts.items[7].enable();
			} else {
				bts.items[7].disable();
			}
		}
	},
	onStoreLoadData : function(store, records, ops) {
		chis.application.her.script.RecipelRecordList.superclass.onStoreLoadData
				.call(this, store, records, ops);
		this.onRowClick()
	}
});