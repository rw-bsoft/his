/**
 * 孕妇随访计划列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.dbs.script.risk")
$import("chis.script.BizSimpleListView")
chis.application.dbs.script.risk.DiabetesRiskVisitPlanList = function(cfg) {
    cfg.pageSize = 50;
    cfg.disablePagingTbr = true;
	chis.application.dbs.script.risk.DiabetesRiskVisitPlanList.superclass.constructor.apply(this,
			[cfg]);
	this.autoLoadData = false;
	this.enableCnd = false;
	this.on("addfield", this.changeFieldPro, this);
}

Ext.extend(chis.application.dbs.script.risk.DiabetesRiskVisitPlanList, chis.script.BizSimpleListView,
		{


			changeFieldPro : function(c, it) {
				c.sortable = false;
			},

			loadData : function() {
				this.initCnd = ['and',
				                			[ 'ne', ['$', 'a.planStatus'], ['s', '9']],
											['eq',['$', 'empiId'],['s',this.exContext.ids.empiId]],
										 	['eq', ['$', 'businessType'], ['s', '12']]
									]
				this.requestData.cnd = this.initCnd
				chis.application.dbs.script.risk.DiabetesRiskVisitPlanList.superclass.loadData
						.call(this);
			}

		});