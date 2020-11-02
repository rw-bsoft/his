$package("phis.application.yb.script")
$import("phis.script.EditorList")

phis.application.yb.script.YpdzLeftList = function(cfg) {
	this.updateRecord = {}
	cfg.autoLoadData = false;
	phis.application.yb.script.YpdzLeftList.superclass.constructor.apply(this,
			[cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.yb.script.YpdzLeftList, phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.yb.script.YpdzLeftList.superclass.initPanel
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
					this.updateRecord[record.get("YPXH")+"_"+record.get("YPCD")] = record;
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
					var strs= new Array(); //定义一数组
					strs=key.split("_"); //字符分割 
					data["YPXH"] = strs[0];
					data["YPCD"] = strs.length>0?strs[1]:"";
					data["YYZBM"] = this.updateRecord[key].get("YYZBM");
					records.push(data)
				}
				return records;
			}
		})