/**
 * 迁移记录列表
 * 
 * @author zhouw
 */
$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView");

chis.application.fhr.script.MoveRecordsList = function(cfg) {
	cfg.westWidth = 150;
	chis.application.fhr.script.MoveRecordsList.superclass.constructor.apply(this, [cfg]);
	this.enableCnd=false;
	this.showButtonOnPT = false;
}

Ext.extend(chis.application.fhr.script.MoveRecordsList,
		chis.script.BizSimpleListView, {
	loadData : function(){
		this.requestData.cnd = this.cnds || ["or",
			["eq", ["$", "a.familyId"],["s", this.exContext.args.initDataId]],
			["eq", ["$", "a.newFamilyId"],["s", this.exContext.args.initDataId]]
		];
		chis.application.fhr.script.MoveRecordsList.superclass.loadData.call(this);
	}
})
