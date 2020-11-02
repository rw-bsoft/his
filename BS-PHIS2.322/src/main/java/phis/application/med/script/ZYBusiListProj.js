$package("phis.application.med.script")
phis.application.med.script.ZYBusiListProj = function(cfg) {
	cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.height = 220;
	phis.application.med.script.ZYBusiListProj.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.med.script.ZYBusiListProj,
		phis.script.SimpleList,{ 
	loadDatas : function(recordIds) {
		var records = new Array();
		for (var i = 0; i < recordIds.length; i++) {
			var yjxh = recordIds[i];
			for (var j = 0; j < this.opener.listDatas.length; j++) {
				var data = this.opener.listDatas[j];
				if(data.YJXH == yjxh){
					records.push(new Ext.data.Record(data));
				}
			}
		}
		this.store.removeAll();
		this.store.add(records);
	}
});

