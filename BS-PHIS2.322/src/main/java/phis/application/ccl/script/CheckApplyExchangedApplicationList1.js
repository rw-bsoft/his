$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckApplyExchangedApplicationList1 = function(cfg) {
	phis.application.ccl.script.CheckApplyExchangedApplicationList1.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyExchangedApplicationList1,
		phis.script.SimpleList, {
			loadData : function() {
				var tbarIt = this.opener.panel.getTopToolbar().items;
				var dateFrom = tbarIt.get(1).getValue().format('Y-m-d')+" 00:00:00";
				var dateTo = tbarIt.get(3).getValue().format('Y-m-d')+" 23:59:59";
				if(this.openby == "CIC"){
					//this.requestData.cnd =['and',['and',['and',['and',['and',['and',['eq', ['$', 'b.JGID'],['s', this.mainApp.deptId]],['eq', ['$', 'b.BRID'],['l',this.opener.opener.opener.exContext.ids.brid]]],['eq', ['$', 'b.ZXPB'],['i',zt]]],['eq', ['$', 'a.YLLB'],['i',1]]],['eq',['$', 'b.ZFPB'],['i',0]]],['eq',['$', 'a.SSLX'],['i',1]]],['$', 'b.KDRQ'],['todate', ['s',dateFrom],['s','YYYY-MM-DD HH24:MI:SS']]];
					this.requestData.cnd =['and',['and',['and',['eq', ['$', 'b.JGID'],['s', this.mainApp.deptId]],['and',
					['and',	['ge', ['$', 'b.KDRQ'],['todate', ['s',dateFrom],['s','YYYY-MM-DD HH24:MI:SS']]],
							['le', ['$', 'b.KDRQ'],['todate', ['s',dateTo],['s','YYYY-MM-DD HH24:MI:SS']]]],
					['and',['eq', ['$', 'b.BRID'],['l',this.opener.opener.opener.exContext.ids.brid]],
					['in', ['$', 'b.ZXPB'],[0,1]]]]],['eq', ['$', 'a.YLLB'],['i',1]]],['eq',['$', 'b.ZFPB'],['i',0]]]
//					
				}else if(this.openby == "WAR"){
					//前台不能去重，不得后台处理
					this.requestData.serviceId = "phis.checkApplyService";
					this.requestData.serviceAction = "getCheckApplyExchangeApplication_WAR";
					this.requestData.pageNo=1;
					this.requestData.body={
						dateFrom : dateFrom,
						dateTo : dateTo,
						zyh : this.opener.opener.opener.kdInfo.zyh,
						zt : zt
					}
				}
				phis.application.ccl.script.CheckApplyExchangedApplicationList1.superclass.loadData
						.call(this);
			},
			onRowClick : function(){
				var sqdh = this.getSelectedRecord().data.SQDH;
				var checkApplyExchangedApplicationDetailsList = this.opener.midiModules["checkApplyExchangedApplicationDetailsList"];
				if(this.openby == "CIC"){
					checkApplyExchangedApplicationDetailsList.requestData.cnd=['and',['eq', ['$', 'a.SQDH'],['s', sqdh]],['eq', ['$', 'a.YLLB'],['s', 1]]];
				}else if(this.openby == "WAR"){
					checkApplyExchangedApplicationDetailsList.requestData.cnd=['and',['eq', ['$', 'a.SQDH'],['s', sqdh]],['eq', ['$', 'a.YLLB'],['s', 2]]];
				}
				checkApplyExchangedApplicationDetailsList.refresh();
			},
			// 刚打开页面时候默认选中数据
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.opener.midiModules["checkApplyExchangedApplicationDetailsList"].store.removeAll();
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			}
		});