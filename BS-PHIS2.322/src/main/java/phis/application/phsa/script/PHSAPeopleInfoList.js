$package("phis.application.phsa.script")

$import("phis.script.SimpleList");
phis.application.phsa.script.PHSAPeopleInfoList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.listServiceId = "phis.PHSAPeopleInfoService";
	cfg.serverParams = {
		serviceAction : "queryPeopleInfo"
	}
	phis.application.phsa.script.PHSAPeopleInfoList.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(phis.application.phsa.script.PHSAPeopleInfoList,
		phis.script.SimpleList, {
		
			
		});