/**
 * 贫困人群随访module中左边的列表
 * 
 * @author : tianj
 */
$package("chis.application.ppvr.script")

$import("chis.script.BizSimpleListView")

chis.application.ppvr.script.PoorPeopleVisitList = function(cfg) {
	cfg.autoLoadData = true;
	chis.application.ppvr.script.PoorPeopleVisitList.superclass.constructor.apply(this, [cfg]);
	this.requestData.actions = "create,update";// 判断修改操作权限
	this.on("getStoreFields", this.onGetStoreFields, this)
}

Ext.extend(chis.application.ppvr.script.PoorPeopleVisitList, chis.script.BizSimpleListView, {
	onGetStoreFields : function(fields) {
		fields.push({
					name : "_actions",
					type : "object"
				});
	},
	
	loadData : function(){
		this.requestData.cnd = ["eq", ["$", "a.empiId"], ["s", this.exContext.ids.empiId]];
		chis.application.ppvr.script.PoorPeopleVisitList.superclass.loadData.call(this);
	}
});