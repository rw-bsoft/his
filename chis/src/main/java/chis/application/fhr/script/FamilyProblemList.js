/**
 * 家庭档案-->问题列表-->新建或查看表单页面
 * 
 * @author tianj
 */
$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView");

chis.application.fhr.script.FamilyProblemList = function(cfg) {
	chis.application.fhr.script.FamilyProblemList.superclass.constructor.apply(this, [cfg]);
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.fhr.script.FamilyProblemList, chis.script.BizSimpleListView, {
	loadData : function() {
		if(this.initDataId !=this.exContext.args.initDataId){
			this.initCnd = ['eq', ['$', 'familyId'],
				['s', this.exContext.args.initDataId]];
			
			this.requestData.cnd = this.initCnd ;
			this.initDataId =this.exContext.args.initDataId
			//清理查询条件
			this.cndFldCombox.setValue();
			this.cndField.setValue();
		}
		chis.application.fhr.script.FamilyProblemList.superclass.loadData.call(this);
	},

	onSave : function(entryName, op, json, rec) {
		this.exContext.args.initDataId = rec.familyId
		this.refresh();
	}
});