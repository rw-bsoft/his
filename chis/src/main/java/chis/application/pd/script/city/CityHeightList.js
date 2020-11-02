/**
 * 9市标准-身高别体重列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.city")
$import("chis.script.BizSimpleListView")
chis.application.pd.script.city.CityHeightList = function(cfg) {
	chis.application.pd.script.city.CityHeightList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.city.CityHeightList, chis.script.BizSimpleListView, {});