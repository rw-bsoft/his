$package("chis.application.pd.script.cdc")

$import("chis.script.BizSimpleListView")

chis.application.pd.script.cdc.ChildrenDictCorrectionList = function(cfg) {
	chis.application.pd.script.cdc.ChildrenDictCorrectionList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.cdc.ChildrenDictCorrectionList, chis.script.BizSimpleListView,
		{});