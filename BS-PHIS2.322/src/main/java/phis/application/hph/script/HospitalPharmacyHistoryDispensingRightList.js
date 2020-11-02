$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyHistoryDispensingRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.group = "BRCH";
	cfg.groupTextTpl = "<table width='45%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='30%'>&nbsp;&nbsp;<b>床号:{[values.rs[0].data.BRCH]}</b></td><td width='24%'><b>姓名:{[values.rs[0].data.BRXM]}</b></td><td width='20%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
//	cfg.cnds = ['eq', ['$', '1'], ['i', 2]];
//	cfg.listServiceId="hospitalPharmacyService";
//	cfg.serverParams={"serviceAction":"queryHospitalPharmacyMethod"};
	cfg.listServiceId = "hospitalPharmacyService";
	cfg.serverParams = {
		serviceAction : "queryBRFYMX"
	};
	phis.application.hph.script.HospitalPharmacyHistoryDispensingRightList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensingRightList,
		phis.script.SimpleList, {
			LSJESummaryRenderer : function(v, params) {
				if (params.style) {
					params.style += "font-size:16px;"
				}
				return "总金额:" + v.toFixed(2)
			}
		});