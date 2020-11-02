/**
 * 非重点人群随访module中左边的列表
 * 
 * @author tianj
 */
$package("chis.application.npvr.script")

$import("chis.script.BizSimpleListView")

chis.application.npvr.script.NormalPopulationVisitList = function(cfg) {
	cfg.autoLoadData = true;
	chis.application.npvr.script.NormalPopulationVisitList.superclass.constructor.apply(this,
			[cfg]);
	this.requestData.actions = "update";// 修改权限判断
	this.on("getStoreFields", this.onGetStoreFields, this)
}

Ext.extend(chis.application.npvr.script.NormalPopulationVisitList, chis.script.BizSimpleListView, {
	onGetStoreFields : function(fields) {
		fields.push({
					name : "_actions",
					type : "object"
				});
	},
	
	loadData : function(){
		this.requestData.cnd = ["eq", ["$", "a.empiId"], ["s", this.exContext.ids.empiId]];
		chis.application.npvr.script.NormalPopulationVisitList.superclass.loadData.call(this);
	}
});