/**
 * 儿童随访计划列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script")

$import("chis.script.BizSimpleListView")

chis.application.cdh.script.ChildrenVisitPlanListView = function(cfg) {
  cfg.pageSize = -1;
	chis.application.cdh.script.ChildrenVisitPlanListView.superclass.constructor.apply(this,
			[cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;
	this.on("addfield", this.changeFieldPro, this);
}

Ext.extend(chis.application.cdh.script.ChildrenVisitPlanListView, chis.script.BizSimpleListView, {

			loadData : function() {
				this.initCnd  = [
						'and',
						 [
								'eq',
								['$', 'recordId'],
								[
										's',
										this.exContext.ids["CDH_HealthCard.phrId"]]],
						['eq', ['$', 'businessType'],
								['s', this.exContext.args.businessType]]];
				var ageCnd;
				if (this.exContext.args.checkupType) {
					if (this.exContext.args.checkupType == "1") {
						ageCnd = ['lt', ['$', 'extend1'], ['i', 12]];
					} else if (this.exContext.args.checkupType == "2") {
						ageCnd = ['and', ['ge', ['$', 'extend1'], ['i', 12]],
								['lt', ['$', 'extend1'], ['i', 36]]];
					} else if (this.exContext.args.checkupType == "3") {
						ageCnd = ['ge', ['$', 'extend1'], ['i', 36]];
					}
				}

				if (ageCnd) {
					this.requestData.cnd = ['and', this.initCnd, ageCnd];
				} else {
					this.requestData.cnd = this.initCnd;
				}
				chis.application.cdh.script.ChildrenVisitPlanListView.superclass.loadData
						.call(this);
			},

			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			},

			changeFieldPro : function(c, it) {
				if (it.id == "extend1") {
					c.header = "月龄"
				}
				c.sortable = false;
			},

			onDblClick : function() {
			}
		});