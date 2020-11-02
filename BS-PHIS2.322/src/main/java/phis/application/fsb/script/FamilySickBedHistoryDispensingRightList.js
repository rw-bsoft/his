$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedHistoryDispensingRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.cnds = ['eq', ['$', '1'], ['i', 2]];
//	cfg.listServiceId="hospitalPharmacyService";
//	cfg.serverParams={"serviceAction":"queryHospitalPharmacyMethod"};
	phis.application.fsb.script.FamilySickBedHistoryDispensingRightList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedHistoryDispensingRightList,
		phis.script.SimpleList, {
			LSJESummaryRenderer : function(v, params) {
				if (params.style) {
					params.style += "font-size:16px;"
				}
				return "当前页金额合计:" + v.toFixed(2)
			}
		});