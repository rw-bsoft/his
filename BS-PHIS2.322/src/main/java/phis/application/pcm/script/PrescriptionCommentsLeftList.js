/**
 * 药房模块,左边list模版
 * 
 * @author caijy
 */
$package("phis.application.pcm.script")
$import("phis.script.SimpleList")

phis.application.pcm.script.PrescriptionCommentsLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.pcm.script.PrescriptionCommentsLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsLeftList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = {
					"YEAR" : this.year,
					"DPLX" : this.dplx
				};
				phis.application.pcm.script.PrescriptionCommentsLeftList.superclass.loadData
						.call(this);
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.fireEvent("cyrqClick", r.get("CYXH"),r.get("WCZT"));
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.fireEvent("noRecord");
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.onRowClick();
			}
		})