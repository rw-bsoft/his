/**
 * 医嘱发药-待发药病区选择List
 * 
 * @author : caijy
 */
$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyWaitBackMedicine = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.hph.script.HospitalPharmacyWaitBackMedicine.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyWaitBackMedicine,
		phis.script.SimpleList, {
			loadData:function(){
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.queryServiceActionID;
				phis.application.hph.script.HospitalPharmacyWaitBackMedicine.superclass.loadData.call(this);
			}
			,
			// 单击时调出退药列表
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.fireEvent("recordClick",r.data.TYBQ);
			}
			,
			//打开页面选中第一条记录 并显示右边的
		onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0)
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
		}
		this.onRowClick();
	}
		});