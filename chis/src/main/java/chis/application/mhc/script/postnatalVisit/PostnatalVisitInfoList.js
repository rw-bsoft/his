/**
 * 孕妇产后访视列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatalVisit")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoList = function(cfg) {
	chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false
	this.disablePagingTbr = true
	this.pageSize = 100;
}

Ext.extend(chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoList,
		chis.script.BizSimpleListView, {

			loadData : function() {
				this.initCnd = [
						"eq",
						["$", "pregnantId"],
						[
								"s",
								this.exContext.ids["MHC_PregnantRecord.pregnantId"]]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoList.superclass.loadData
						.call(this);
			}

		});