$package("phis.application.yb.script")
$import("phis.script.EditorList")

phis.application.yb.script.typkLeftList = function(cfg) {
	this.updateRecord = {}
	cfg.autoLoadData = false;
	phis.application.yb.script.typkLeftList.superclass.constructor.apply(this,
			[cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.yb.script.typkLeftList, phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.yb.script.typkLeftList.superclass.initPanel
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
				if (it.id == "SBM") {
					this.updateRecord[record.get("YPXH")] = record;

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
					data["YPXH"] = key;
					data["SBM"] = this.updateRecord[key].get("SBM");
					records.push(data)
				}
				return records;
			}
		})