/**
 * 新生儿访视基本信息列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.babyVisit.BabyVisitInfoList = function(cfg) {
	chis.application.mhc.script.babyVisit.BabyVisitInfoList.superclass.constructor.apply(this,
			[cfg]);
	this.disablePagingTbr = true;
}

Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitInfoList, chis.script.BizSimpleListView,
		{

			loadData : function() {
				this.initCnd = [
						"eq",
						["$", "pregnantId"],
						[
								"s",
								this.exContext.ids["MHC_PregnantRecord.pregnantId"]]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.babyVisit.BabyVisitInfoList.superclass.loadData
						.call(this);
			},

			doMake : function() {
				this.fireEvent("create");
			},

			doVisit : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var pregnantId = r.get("pregnantId");
				var babyId = r.get("babyId");
				var data = {
					"pregnantId" : pregnantId,
					"babyId" : babyId
				};
				var module = this.createCombinedModule("BabyVisitInfoModule",
						this.refModule);
				Ext.apply(module.exContext.args, data);
				module.getWin().show();
			}
		});