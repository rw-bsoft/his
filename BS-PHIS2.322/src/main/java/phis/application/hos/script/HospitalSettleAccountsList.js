$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalSettleAccountsList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.hos.script.HospitalSettleAccountsList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalSettleAccountsList, phis.script.SimpleList,
		{
			// loadData : function(data) {
			// this.requestData.serviceId = "hospitalPatientSelectionService";
			// this.requestData.serviceAction = "getSelectionList";
			// this.requestData.body = data;
			// phis.application.hos.script.HospitalCostsListList.superclass.loadData.call(this);
			// }
			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					document.getElementById("ZTJS_ZYJS_HJ1").innerHTML = " 合 计 金 额 ：";
					return
				}
//				var store = this.grid.getStore();
				var n = store.getCount();
				var JKHJ = 0;
				for(var i = 0;i < n; i ++){
					var r = store.getAt(i);
					JKHJ += r.get("JKJE");
				}
				JKHJ = parseFloat(JKHJ).toFixed(2);
				document.getElementById("ZTJS_ZYJS_HJ1").innerHTML = " 合 计 金 额 ："+JKHJ;
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZTJS_ZYJS_HJ1' align='left' style='color:blue'> 合 计 金 额 ："
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			}
		});