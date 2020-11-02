$package("phis.application.sup.script")

$import("phis.script.SelectList")
/**
 * 转科管理库存列表
 * 
 * @author gaof
 */
phis.application.sup.script.TransferManagementKCList = function(cfg) {
    cfg.autoLoadData = false;
	phis.application.sup.script.TransferManagementKCList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.TransferManagementKCList,
		phis.script.SelectList, {
			loadData : function(ksdm) {
				this.clear();
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "KCQuery";
                var body={};
                body["KSDM"]=ksdm;
                body["ZBLB"]=this.zblb;
				this.requestData.cnd = body;
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
			doCommit : function() {
				var records = this.getSelectedRecords()
				if (records == null) {
					return;
				}
                this.clearSelect();
				this.fireEvent("checkData", records);
			},
			doClose : function() {
				this.fireEvent("winClose", this);
			}
		})