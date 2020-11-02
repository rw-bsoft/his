$package("phis.application.sup.script");

$import("phis.script.SelectList");

phis.application.sup.script.MeteringEquipmentTestList = function(cfg) {
	cfg.width = 880;
	cfg.height = 550;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sup.script.MeteringEquipmentTestList.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.MeteringEquipmentTestList,
				phis.script.SelectList,
				{
					loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.queryAction;
						phis.application.sup.script.MeteringEquipmentTestList.superclass.loadData
								.call(this);
					},
					// 确定
					doQd : function() {
						var record = this.getSelectedRecords();
						var count = record.length;
						var ret = [];
						for ( var i = 0; i < count; i++) {
							ret.push(record[i].data.JLXH);
						}
						if (ret.length == 0) {
							MyMessageTip.msg("提示", "未选中记录", true);
							return;
						}
						this.fireEvent("qd", ret)
						this.doClose();
					},
					doClose : function() {
						this.getWin().hide();
					}
				})