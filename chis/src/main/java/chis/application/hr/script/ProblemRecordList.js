/**
 * 公共页面
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.ProblemRecordList = function(cfg) {
	this.initCnd = ["eq", ["$", "empiId"], ["s", cfg.exContext.ids.empiId]];
	chis.application.hr.script.ProblemRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hr.script.ProblemRecordList, chis.script.BizSimpleListView, {
	loadData : function() {
		this.initCnd = ["eq", ["$", "empiId"], ["s", this.exContext.ids.empiId]];
			this.requestData.cnd = this.initCnd;
		chis.application.hr.script.ProblemRecordList.superclass.loadData.call(this);
	},
	
	onSave : function(entryName, op, json, data) {
		this.refresh();
	}
	,doRefresh : function() {
		this.refresh();
	}
});