/**
 * 新生儿访视记录列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.babyVisit.BabyVisitRecordList = function(cfg) {
	chis.application.mhc.script.babyVisit.BabyVisitRecordList.superclass.constructor.apply(
			this, [cfg]);
	this.disablePagingTbr = true
}

Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitRecordList,
		chis.script.BizSimpleListView, {

			loadData : function() {
				this.initCnd = ["eq", ["$", "babyId"],
						["s", this.exContext.args.babyId]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.babyVisit.BabyVisitInfoList.superclass.loadData
						.call(this);
			},

			doMake : function() {
				this.fireEvent("create")
			}
		});