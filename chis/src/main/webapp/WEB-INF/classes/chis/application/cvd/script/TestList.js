$package("chis.application.cvd.script");

$import("chis.script.BizEditorListView");

chis.application.cvd.script.TestList = function(cfg) {
	cfg.showButtonOnTop = true
	cfg.entryName = "chis.application.cvd.schemas.CVD_Test"
	cfg.superEntry = "chis.application.cvd.schemas.CVD_TestDict"
	cfg.pageSize = 50
	cfg.disablePagingTbr = true
	cfg.showRowNumber = true
	chis.application.cvd.script.TestList.superclass.constructor.apply(this, [cfg]);
	this.width = 800
	this.tabFlag = false
	this.enterFlag = false
	
};

Ext.extend(chis.application.cvd.script.TestList, chis.script.BizEditorListView, {
	loadData : function() {
		this.data = {};
		this.data["phrId"] = this.exContext.ids.phrId
		this.data["empiId"] = this.exContext.ids.empiId
		this.data["inquireId"] = this.exContext.args.visitId
		this.store.removeAll();
		this.initCnd = ['eq', ['$', 'inquireId'], ['s', this.data["inquireId"]]]
		this.requestData.cnd = this.initCnd
		this.store.load()
	},
	onStoreLoadData : function(store, records, ops) {
		if (store.getCount() == 0) {
			this.op = "create"
			var result = util.rmi.miniJsonRequestSync({
						serviceId : this.listServiceId,
						schema : this.superEntry,
						method : "execute",
						pageSize : 100,
						pageNo : 1
					})
			this.result = result.json.body
			var records = [];
			for (var i = 0; i < result.json.totalCount; i++) {
				var r = result.json.body[i]
				r.result_text = ""
				var record = new Ext.data.Record(r);
				records.push(record)
			}
			this.store.add(records)
		} else {
			this.op = "update"
			var records = []
			for (var i = 0; i < store.getCount(); i++) {
				var record = store.getAt(i)
				records.push(record.data)
			}
			this.result = records
		}
		this.resetButtons()
	},
	doSave : function() {
		var data = this.getData()
		this.saving = true
		this.grid.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : "chis.cvdTestService",
					serviceAction : "saveCVDTest",
					method : "execute",
					op : this.op,
					schema : this.entryName,
					body : {
						"inquireId" : this.data.inquireId,
						"empiId" : this.data.empiId,
						"phrId" : this.data.phrId,
						"data" : data
					}
				}, function(code, msg, json) {
					this.grid.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.doSave, [data]);
						return
					}
					if (this.op == "create") {
						this.op = "update";
					}
					this.loadData()
				}, this)
	},
	getData : function() {
		var records = []
		var store = this.store
		for (var i = 0; i < store.getCount(); i++) {
			var r = store.getAt(i)
			r.data.result = this.result[i].result
			records.push(r.data)
		}
		return records
	},
	onReady : function() {
		chis.application.cvd.script.TestList.superclass.onReady.call(this)
		this.store.on("load", this.onRecordsAdd, this);
		var columns = this.grid.getColumnModel().getColumnsBy(function(c) {
					c.sortable = false
				});
	},
	onRecordsAdd : function() {
		var cm = this.grid.getColumnModel();
		var count = this.store.getCount();
		for (var j = 0; j < count; j++) {
			var enditor = cm.getCellEditor(1, j);
			if (enditor) {
				var field = enditor.field;
				field.on("specialkey", this.onResultSpecialkey, this);
			}
		}
	},
	onResultSpecialkey : function(field, e) {
		if (e.getKey() == Ext.EventObject.TAB) {
			this.tabFlag = true;
			e.stopEvent()
			return;
		}
		if (e.getKey() == Ext.EventObject.ENTER) {
			this.enterFlag = true
			return;
		}
		if (e.getKey() != Ext.EventObject.ENTER) {
			return;
		}
	},
	afterCellEdit : function(e) {
		chis.application.cvd.script.TestList.superclass.afterCellEdit.call(this, e)
		if (this.tabFlag) {
			this.tabFlag = false
			if ((e.row == 7 || e.row == 9) && e.value != '1') {
				var nextRecord = this.grid.store.getAt(e.row + 1)
				if (nextRecord) {
					nextRecord.set("testResult", "")
					nextRecord.set("testResult_text", "")
				}
			}
		} else if (this.enterFlag) {
			this.enterFlag = false
			if ((e.row == 7 || e.row == 9) && e.value != '1') {
				var nextRecord = this.grid.store.getAt(e.row + 1)
				if (nextRecord) {
					nextRecord.set("testResult", "")
					nextRecord.set("testResult_text", "")
				}
				this.grid.startEditing(e.row + 2, e.column)
			} else {
				if (e.row == this.grid.store.getCount() - 1) {
					return
				}
				this.grid.startEditing(e.row + 1, e.column)
			}
		} else {
			return
		}
	}
});