$package("phis.application.odm.script")

$import("phis.script.SimpleList")

phis.application.odm.script.OwnedDrugManageRightList = function(cfg) {
	cfg.group = "HM";
	cfg.groupTextTpl = "<table width='65%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='30%'>&nbsp;&nbsp;<b>{[values.rs[0].data.KDSJ]}</b></td><td width='24%'><b>{[values.rs[0].data.HM]}</b></td><td width='30%'><div align='right'><b>{[values.rs[0].data.BRXM]}</b></div></td><td width='30%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.odm.script.OwnedDrugManageRightList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.odm.script.OwnedDrugManageRightList,
		phis.script.SimpleList, {
		})