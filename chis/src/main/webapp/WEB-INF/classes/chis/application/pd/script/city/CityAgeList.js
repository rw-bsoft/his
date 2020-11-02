/**
 * 9市标准-年龄别身高，体重，头围列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.city")
$import("chis.script.BizSimpleListView")
chis.application.pd.script.city.CityAgeList = function(cfg) {
	chis.application.pd.script.city.CityAgeList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.pd.script.city.CityAgeList, chis.script.BizSimpleListView, {});