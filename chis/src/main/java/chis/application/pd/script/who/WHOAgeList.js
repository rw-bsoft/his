/**
 * WHO标准-年龄别身高，体重，头围列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.who")
$import("chis.script.BizSimpleListView")
chis.application.pd.script.who.WHOAgeList = function(cfg) {
	chis.application.pd.script.who.WHOAgeList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.who.WHOAgeList, chis.script.BizSimpleListView, {});