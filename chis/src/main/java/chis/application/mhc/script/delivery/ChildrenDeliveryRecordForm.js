/**
 * 产时信息表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.delivery.ChildrenDeliveryRecordForm = function(cfg) {
	cfg.labelWidth = 95;
	cfg.fldDefaultWidth = 210
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.delivery.ChildrenDeliveryRecordForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.mhc.script.delivery.ChildrenDeliveryRecordForm,
		chis.script.BizTableFormView, {

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "empiId",
					"fieldValue" : this.exContext.ids.empiId
				};
			}

		});