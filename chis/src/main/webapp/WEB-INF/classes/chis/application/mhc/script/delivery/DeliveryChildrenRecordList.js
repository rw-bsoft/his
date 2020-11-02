/**
 * �и�����Ӥ��Ǽ���Ϣ�б�
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.delivery.DeliveryChildrenRecordList = function(cfg) {
	chis.application.mhc.script.delivery.DeliveryChildrenRecordList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false
	this.disablePagingTbr = true
	this.pageSize = 100;

}

Ext.extend(chis.application.mhc.script.delivery.DeliveryChildrenRecordList,
		chis.script.BizSimpleListView, {

			loadData : function() {
				this.initCnd = ["eq", ["$", "pregnantId"],
						["s", this.exContext.ids["MHC_PregnantRecord.pregnantId"]]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.delivery.DeliveryChildrenRecordList.superclass.loadData
						.call(this);
			},

			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			}
		});