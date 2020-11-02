$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.MedicalBackApplicationLeftList = function(cfg) {
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	phis.application.war.script.MedicalBackApplicationLeftList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.war.script.MedicalBackApplicationLeftList,
		phis.script.SimpleList, {
			loadData : function(lybq, zyh) {
				if(lybq==null||lybq==""||lybq==undefined){return;}
				var body = {};
				body["lybq"] = lybq;
				body["zyh"] = zyh;
				this.requestData.serviceId = "phis."+this.serviceId;
				this.requestData.serviceAction = this.queryActionId;
				this.requestData.body = body;
				phis.application.war.script.MedicalBackApplicationLeftList.superclass.loadData
						.call(this);
			},
			//双击数据回填到右边去
			onDblClick : function(grid, index, e) {
			var record=this.getSelectedRecord();
			this.fireEvent("recordClick",record.data);
			}
		});