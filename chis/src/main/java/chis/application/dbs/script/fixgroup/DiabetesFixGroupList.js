$package("chis.application.dbs.script.fixgroup")
$import("util.Accredit", "chis.script.BizSimpleListView")

chis.application.dbs.script.fixgroup.DiabetesFixGroupList = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesFixGroup"
	cfg.disablePagingTbr = true;
	this.initCnd = ["eq", ["$", "empiId"], ["s", this.empiId]]
	chis.application.dbs.script.fixgroup.DiabetesFixGroupList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(chis.application.dbs.script.fixgroup.DiabetesFixGroupList,
		chis.script.BizSimpleListView, {
			getPagingToolbar : function() {

			},
			onStoreBeforeLoad : function() {
				// 覆盖父类方法
			},
			refreshWhenTabChange : function() {
				this.refresh();
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
				}
			},
			loadData : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesRecordService",
							serviceAction : "initializeFixGroup",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId,
								phrId : this.exContext.ids.phrId
							}
						})
				this.result = result
				this.loadDataByLocal(result.json.body);
			}
//			,
//			onRowClick : function(grid, index, e) {
//				
//			}
		});