$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectRightList = function(cfg) {
	cfg.autoLoadData = false;
	//cfg.cnds=['eq',['$','1'],['i',2]];
	phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectRightList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectRightList,
		phis.script.SimpleList, {
			//重写是因为框架不支持引用的字段不用查询
			getCndBar:function(items){
			return [];
			}
		});