$package("chis.application.common.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")
chis.application.common.script.VisitRecordDetialListView = function(cfg) {
	chis.application.common.script.VisitRecordDetialListView.superclass.constructor.apply(this,
			[cfg]);
	this.requestData.serviceId='chis.simpleQuery';
}
Ext.extend(chis.application.common.script.VisitRecordDetialListView,
		chis.script.BizSimpleListView, {
			loadData:function(){
				var me=this;
				if(!this.queryParam){
					return;
				}
				var EMPIID=this.queryParam.data.EMPIID;
				var JLBH=this.queryParam.data.JLBH;
				this.requestData.cnd=['and',['eq',['$','a.EMPIID'],['s',EMPIID]],['eq',['$','a.JLXH'],['s',JLBH]]];
				chis.application.common.script.VisitRecordDetialListView.superclass.loadData.call(this)
			}
		});