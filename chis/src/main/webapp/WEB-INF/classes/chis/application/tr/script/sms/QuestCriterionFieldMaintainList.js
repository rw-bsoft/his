$package("chis.application.tr.script.sms")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.sms.QuestCriterionFieldMaintainList = function(cfg){
	chis.application.tr.script.sms.QuestCriterionFieldMaintainList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
    this.isCombined = true;
}

Ext.extend(chis.application.tr.script.sms.QuestCriterionFieldMaintainList,chis.script.BizSimpleListView,{
	onRowClick:function(grid,index,e){
		this.selectedIndex = index;
		var r = this.getSelectedRecord();
		if(r){
			var fieldId = r.get("fieldId");
			var alias = r.get("alias");
			this.fireEvent("rowclick",fieldId,alias);
		}
	}
});