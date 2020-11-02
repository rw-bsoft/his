/**
 * 孕妇随访计划列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.hy.script.risk")
$import("chis.script.BizSimpleListView")
chis.application.hy.script.risk.HypertensionRiskVisitPlanList = function(cfg) {
    cfg.pageSize = 50;
    cfg.disablePagingTbr = true;
	chis.application.hy.script.risk.HypertensionRiskVisitPlanList.superclass.constructor.apply(this,
			[cfg]);
	this.autoLoadData = false;
	this.enableCnd = false;
	this.on("addfield", this.changeFieldPro, this);
}

Ext.extend(chis.application.hy.script.risk.HypertensionRiskVisitPlanList, chis.script.BizSimpleListView,
		{


			changeFieldPro : function(c, it) {
				c.sortable = false;
			},

			loadData : function() {
				this.initCnd = ['and',
				                			[ 'ne', ['$', 'a.planStatus'], ['s', '9']],
											['eq',['$', 'a.empiId'],['s',this.exContext.ids.empiId]],
										 	['eq', ['$', 'a.businessType'], ['s', '13']]
									]
				this.requestData.cnd = this.initCnd
				chis.application.hy.script.risk.HypertensionRiskVisitPlanList.superclass.loadData
						.call(this);
			}

		});