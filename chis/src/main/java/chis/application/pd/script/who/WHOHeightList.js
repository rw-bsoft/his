/**
 * WHO标准-身高别体重列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.who")
$import("chis.script.BizSimpleListView")
chis.application.pd.script.who.WHOHeightList = function(cfg) {
	chis.application.pd.script.who.WHOHeightList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.who.WHOHeightList, chis.script.BizSimpleListView, {});