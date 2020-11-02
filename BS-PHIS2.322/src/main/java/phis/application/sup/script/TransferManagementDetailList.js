$package("phis.application.sup.script")

$import("phis.script.EditorList")
/**
 * 转科管理新增修改界面可编辑列表
 * 
 * @author gaof
 */
phis.application.sup.script.TransferManagementDetailList = function(cfg) {
    cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	phis.application.sup.script.TransferManagementDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sup.script.TransferManagementDetailList,
		phis.script.EditorList, {
            loadData : function() {
                this.clear();
                this.requestData.pageNo = 1;
                this.requestData.pageSize = 25;
                this.requestData.serviceId = this.serviceId;
                this.requestData.serviceAction = "getZK02Info";
                if (this.store) {
                    if (this.disablePagingTbr) {
                        this.store.load()
                    } else {
                        var pt = this.grid.getBottomToolbar()
                        if (this.requestData.pageNo == 1) {
                            pt.cursor = 0;
                        }
                        pt.doLoad(pt.cursor)
                    }
                }
                this.resetButtons();
            },
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "WZSL" || it.id == "WZJG") {
					var wzje = 0;
					if (record.get("WZSL") == null || record.get("WZSL") == ""
							|| record.get("WZJG") == null
							|| record.get("WZJG") == "") {
						return;
					}
					wzje = (Number(record.get("WZSL")) * Number(record
							.get("WZJG"))).toFixed(2);
					record.set("WZJE", wzje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					// this.calculatEmount();
				}

				// 判断退回数量是否大于可退数量
				if (it.id == "WZSL") {
					if (record.get("TJSL")&&record.get("TJSL")!=""&&(Number(record.get("WZSL"))) > (Number(record
							.get("TJSL")))) {
						MyMessageTip.msg("提示", "转科数量不能大于推荐数量", true);
					}
				}
			}

		})