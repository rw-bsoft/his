$package("chis.application.hy.script.similarity")
$import("util.Accredit", "app.modules.form.TableFormView",
		"chis.script.BizSimpleListView")

chis.application.hy.script.similarity.HypertensionSimilarityCheckList = function(cfg) {
	cfg.disablePagingTbr = true;
//	cfg.initCnd = ['eq',['$','a.empiId'],['s',cfg.exContext.args.empiId]]
	chis.application.hy.script.similarity.HypertensionSimilarityCheckList.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.application.hy.script.similarity.HypertensionSimilarityCheckList, chis.script.BizSimpleListView, {
		onRowClick : function(grid, index, e) {
			// 加载已经随访的记录
			this.selectedIndex = index
			this.fireEvent("rowClick", grid, index, e, this)
		}
});