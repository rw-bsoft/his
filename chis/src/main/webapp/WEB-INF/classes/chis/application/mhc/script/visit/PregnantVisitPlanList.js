/**
 * 孕妇随访计划列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.visit.PregnantVisitPlanList = function(cfg) {
  cfg.pageSize = 50;
	chis.application.mhc.script.visit.PregnantVisitPlanList.superclass.constructor.apply(this,
			[cfg]);
	this.disablePagingTbr = true;
	this.autoLoadData = false;
	this.enableCnd = false;
	this.on("addfield", this.changeFieldPro, this);
}

Ext.extend(chis.application.mhc.script.visit.PregnantVisitPlanList, chis.script.BizSimpleListView,
		{


			changeFieldPro : function(c, it) {
				c.sortable = false;
			},

			loadData : function() {
				this.initCnd = [
						'and',
						[
								'eq',
								['$', 'recordId'],
								[
										's',
										this.exContext.ids["MHC_PregnantRecord.pregnantId"]]],
						['or', ['eq', ['$', 'businessType'], ['s', '8']],
								['eq', ['$', 'businessType'], ['s', '9']]]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.visit.PregnantVisitPlanList.superclass.loadData
						.call(this);
			}

		});