$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalDayQueryList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.group = "ZYHM";
	cfg.groupTextTpl = "<table width='96%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='16%'><b>姓名:{[values.rs[0].data.BRXM]}</b></td><td width='16%'><b>性质:{[values.rs[0].data.BRXZ]}</b></td><td width='16%'><b>住院天数:{[values.rs[0].data.ZYTS]}</b></td><td width='16%'><b>入院日期:{[values.rs[0].data.RYRQ]}</b></td><td width='16%'><b>自负合计:{[values.rs[0].data.ZFHJ]}</b></td></tr><tr><td width='16%'><b>住院号码:{[values.rs[0].data.ZYHM]}</b></td><td width='16%'>&nbsp;&nbsp;<b>病人科室:{[values.rs[0].data.BRKS]}</b></td><td width='16%'><b>病人床号:{[values.rs[0].data.BRCH]}</b></td><td width='16%'><b>出院日期:{[values.rs[0].data.CYRQ]}</b></td><td width='16%'><b>费用总额:{[values.rs[0].data.FYZE]}</b></td><td width='16%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.hos.script.HospitalDayQueryList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalDayQueryList,
		phis.script.SimpleList, {
		});