$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckApplyExchangedApplicationList2 = function(cfg) {
	com.bsoft.phis.checkapply.CheckApplyExchangedApplicationList2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyExchangedApplicationList2,
		com.bsoft.phis.SimpleList, {
			loadData : function() {
				var tbarIt = this.opener.panel.getTopToolbar().items;
				var dateFrom = tbarIt.get(1).getValue().format('Y-m-d')+" 00:00:00";
				var dateTo = tbarIt.get(3).getValue().format('Y-m-d')+" 23:59:59";
				var zt = tbarIt.get(4).getValue();
				if(this.openby == "CIC"){
					this.requestData.cnd =['and',['and',['and',['and',['eq', ['$', 'b.JGID'],['s', this.mainApp.deptId]],['and',
					['and',	['ge', ['$', 'b.KDRQ'],['todate', dateFrom, 'yyyy-mm-dd hh:mm:ss']],
							['le', ['$', 'b.KDRQ'],['todate', dateTo, 'yyyy-mm-dd hh:mm:ss']]],
					['and',['eq', ['$', 'b.BRID'],['s',this.opener.opener.opener.exContext.ids.brid]],
					['eq', ['$', 'b.ZXPB'],['s',zt]]]]],['eq', ['$', 'a.YLLB'],['s',1]]],['eq',['$', 'b.ZFPB'],['s',0]]],['eq',['$', 'a.SSLX'],['s',3]]]
				}else if(this.openby == "WAR"){
					//ǰ̨����ȥ�أ����ú�̨����
					this.requestData.serviceId = "checkApplyService";
					this.requestData.serviceAction = "getCheckApplyExchangeApplication_WAR";
					this.requestData.pageNo=1;
					this.requestData.body={
						dateFrom : dateFrom,
						dateTo : dateTo,
						zyh : this.opener.opener.opener.kdInfo.zyh,
						zt : zt
					}
				}
				com.bsoft.phis.checkapply.CheckApplyExchangedApplicationList2.superclass.loadData
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
			// �մ�ҳ��ʱ��Ĭ��ѡ������
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** �����Ƿ��м�¼����fire�����¼�
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