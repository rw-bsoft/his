$package("phis.application.yb.script")
$import("phis.script.EditorList")

phis.application.yb.script.xmdzLeftList = function(cfg) {
	this.updateRecord = {}
	cfg.autoLoadData = false;
	phis.application.yb.script.xmdzLeftList.superclass.constructor.apply(this,
			[cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.yb.script.xmdzLeftList, phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.yb.script.xmdzLeftList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr = this;
				this.loadData();
				return grid;
			},
			onBeforeCellEdit : function() {
				if (this.isRead) {
					return false;
				}
				return true;
			},
			onAfterCellEdit : function(it, record, field, v) {
				if (it.id == "YYZBM") {
					this.updateRecord[record.get("FYXH")] = record;
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
			},
			// 获取修改的数据 放到list中
			getUpdateRecord : function() {
				var records = new Array();
				for (var key in this.updateRecord) {
					var data = {};
					data["FYXH"] = key;
					data["YYZBM"] = this.updateRecord[key].get("YYZBM");
					records.push(data)
				}
				return records;
			}
		})