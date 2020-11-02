$package("chis.application.hy.script.risk")
$import("util.Accredit", "app.modules.form.TableFormView",
		"chis.script.BizSimpleListView")

chis.application.hy.script.risk.HypertensionRiskAssessmentList = function(cfg) {
	cfg.disablePagingTbr = true;
	//cfg.initCnd = ['eq',['$','a.empiId'],['s',cfg.exContext.args.empiId||'']]
	chis.application.hy.script.risk.HypertensionRiskAssessmentList.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskAssessmentList, chis.script.BizSimpleListView, {
		loadData : function(){
			this.initCnd = ['eq',['$','a.empiId'],['s',this.exContext.ids.empiId||'']];
			this.requestData.cnd = this.initCnd;
			chis.application.hy.script.risk.HypertensionRiskAssessmentList.superclass.loadData.call(this);
		},
		onRowClick : function(grid, index, e) {
			// 加载已经随访的记录
			this.selectedIndex = index
			this.fireEvent("rowClick", grid, index, e, this)
		}
});