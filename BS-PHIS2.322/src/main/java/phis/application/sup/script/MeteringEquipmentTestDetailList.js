$package("phis.application.sup.script");

$import("phis.script.EditorList");

phis.application.sup.script.MeteringEquipmentTestDetailList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sup.script.MeteringEquipmentTestDetailList.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.MeteringEquipmentTestDetailList,
				phis.script.EditorList,
				{
					loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.queryAction;
						phis.application.sup.script.MeteringEquipmentTestLeftList.superclass.loadData
								.call(this);
					}
				})