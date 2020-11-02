$package("phis.application.pha.script")
$import("phis.application.pha.script.PharmacyMySimpleRightList")

phis.application.pha.script.PharmacyRequisitionList = function(cfg) {
	this.initCnd = ['eq', ['$', 'a.RKBZ'], ['i', 1]]
	phis.application.pha.script.PharmacyRequisitionList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyRequisitionList,
		phis.application.pha.script.PharmacyMySimpleRightList, {
			getQueryCnd : function(tag, dates) {
			this.requestData.serviceId=this.fullserviceId;
			this.requestData.serviceAction=this.queryActionId;
				if (tag == 1) {
					return [
							'and',
							['ge', ['$', "str(a.RKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[0]]],
							['le', ['$', "str(a.RKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[1]]],['eq', ['$', 'a.SQYF'], ['l', this.mainApp['phis'].pharmacyId]],['eq', ['$', 'a.TYPB'], ['i', 0]]];
				} else {
					return ['eq', ['$', 'a.MBYF'], ['i', this.selectValue]]
				}

			},
			getInitDataBody : function(r) {
				var initDataBody = {};
				initDataBody["SQYF"] = r.data.SQYF;
				initDataBody["SQDH"] = r.data.SQDH;
				return initDataBody;
			}
		})