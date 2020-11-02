$package("chis.application.pd.script.who")
$import("chis.script.BizSimpleListView")
chis.application.pd.script.who.WHOBMIList = function(cfg) {
	chis.application.pd.script.who.WHOBMIList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.who.WHOBMIList, chis.script.BizSimpleListView, {});