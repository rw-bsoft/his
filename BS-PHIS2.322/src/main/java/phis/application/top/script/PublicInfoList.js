// @@ 首页公告列表。
$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.SimpleList")

com.bsoft.phis.pub.PublicInfoList = function(cfg) {
	var manageUnitId = cfg.mainApp.deptId
	var c = [];

	if (manageUnitId >= 6) {
		var sub = manageUnitId.substr(0, 2);
		// 特殊处理四个直辖市的情况
		if (sub == "11" || sub == "12" || sub == "50" || sub == "31") {
			c.push(manageUnitId.substring(0, 2))
		} else {
			c.push(manageUnitId.substring(0, 4))
		}
		if (manageUnitId.length >= 11) {
			c.push(manageUnitId.substring(0, 6))
			// c.push(manageUnitId.substring(0, 9))//如果村站需要显示医院发布的公告请去掉该注释
			c.push(manageUnitId)
		} else if (manageUnitId.length == 9) {
			c.push(manageUnitId.substring(0, 6))
			c.push(manageUnitId.substring(0, 9))
		} else if (manageUnitId.length == 6) {
			c.push(manageUnitId.substring(0, 6))
		}
	} else {
		c.push(manageUnitId)
	}
	var cnd
	if (cfg.mainApp.jobtitleId == 'system') {
		cnd = ['like', ['$', 'a.publishUnit'], ['s', c[0] + '%']]
	} else {
		if (c.length == 1) {
			cnd = ['eq', ['$', 'a.publishUnit'], ['s', c[0]]]
		} else {
			cnd = ['or']
			for (var i = 0; i < c.length; i++) {
				cnd.push(['eq', ['$', 'a.publishUnit'], ['s', c[i]]])
			}
		}
	}
	this.initCnd = cnd
	cfg.winState = 'center';
	cfg.updateCls = "com.bsoft.phis.pub.PublicInfoForm";
	com.bsoft.phis.pub.PublicInfoList.superclass.constructor.apply(this, [cfg])

}

Ext.extend(com.bsoft.phis.pub.PublicInfoList, com.bsoft.phis.SimpleList, {})