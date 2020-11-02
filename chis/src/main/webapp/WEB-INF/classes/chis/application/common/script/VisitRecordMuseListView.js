$package("chis.application.common.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")
chis.application.common.script.VisitRecordMuseListView = function(cfg) {
	chis.application.common.script.VisitRecordMuseListView.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(chis.application.common.script.VisitRecordMuseListView,
		chis.script.BizSimpleListView, {
			loadData:function(){
				var me=this;
				if(!this.queryParam){
					return;
				}
				var EMPIID=this.queryParam.data.EMPIID;
				var JLBH=this.queryParam.data.JLBH;
				this.requestData.cnd=['and',['eq',['$','EMPIID'],['s',EMPIID]],['eq',['$','JLXH'],['s',JLBH]]];
				chis.application.common.script.VisitRecordMuseListView.superclass.loadData.call(this)
			}
});