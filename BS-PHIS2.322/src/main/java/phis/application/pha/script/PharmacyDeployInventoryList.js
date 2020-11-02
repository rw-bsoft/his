$package("phis.application.pha.script")
$import("phis.application.pha.script.PharmacyMySimpleRightList")

phis.application.pha.script.PharmacyDeployInventoryList = function(cfg) {
	this.initCnd = ['eq', ['$', 'a.CKBZ'], ['i', 1]]
	phis.application.pha.script.PharmacyDeployInventoryList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyDeployInventoryList,
		phis.application.pha.script.PharmacyMySimpleRightList, {
			getQueryCnd : function(tag, dates) {
			this.requestData.serviceId=this.fullserviceId;
			this.requestData.serviceAction=this.queryActionId;
				if (tag == 1) {
					return [
							'and',
							['ge', ['$', "str(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[0]]],
							['le', ['$', "str(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[1]]],['eq', ['$', 'a.MBYF'],
									['l', this.mainApp['phis'].pharmacyId]]];
				} else {
					return ['eq', ['$', 'a.SQYF'], ['i', this.selectValue]]
				}

			},
			getInitDataBody : function(r) {
				var initDataBody = {};
				initDataBody["SQYF"] = r.data.SQYF;
				initDataBody["SQDH"] = r.data.SQDH;
				return initDataBody;
			}
		})