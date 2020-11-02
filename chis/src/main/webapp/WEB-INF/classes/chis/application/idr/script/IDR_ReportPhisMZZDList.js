$package("chis.application.idr.script")

$import("chis.script.BizSimpleListView");

chis.application.idr.script.IDR_ReportPhisMZZDList = function(cfg){
	cfg.autoLoadData = true;
	cfg.showButtonOnTop = false;
//	cfg.enableCnd = false;
	cfg.disablePagingTbr = false;// 分页工具栏
	chis.application.idr.script.IDR_ReportPhisMZZDList.superclass.constructor.apply(this,[cfg]);
	this.selectFirst = false;
	this.on("loadData",this.afterLoad,this)
}

Ext.extend(chis.application.idr.script.IDR_ReportPhisMZZDList,chis.script.BizSimpleListView,{
	afterLoad : function() {
	},
	loadData : function(){
		var jbkindCnd = ['eq', ['$', 'a.JBPB'], ['s', '09']];
		var bridCnd = ['eq', ['$', 'a.BRID'], ['s', this.exContext.ids.brid|| '182']];
		var cnd = bridCnd;
		if (jbkindCnd) {
			cnd = ['and', cnd];
			if (jbkindCnd) {
				cnd.push(jbkindCnd);
			}
		}
		this.requestData.cnd = cnd;
//		this.requestData.BRID = this.exContext.ids.brid || '182';
		chis.application.idr.script.IDR_ReportPhisMZZDList.superclass.loadData.call(this);
	}
});