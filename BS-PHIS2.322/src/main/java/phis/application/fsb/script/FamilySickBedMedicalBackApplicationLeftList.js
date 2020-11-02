$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedMedicalBackApplicationLeftList = function(cfg) {
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	phis.application.fsb.script.FamilySickBedMedicalBackApplicationLeftList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedMedicalBackApplicationLeftList,
		phis.script.SimpleList, {
			loadData : function(zyh) {
				var body = {};
				body["zyh"] = zyh;
				this.requestData.serviceId = "phis."+this.serviceId;
				this.requestData.serviceAction = this.queryActionId;
				this.requestData.body = body;
				phis.application.fsb.script.FamilySickBedMedicalBackApplicationLeftList.superclass.loadData
						.call(this);
			},
			//双击数据回填到右边去
			onDblClick : function(grid, index, e) {
			var record=this.getSelectedRecord();
			this.fireEvent("recordClick",record.data);
			}
		});