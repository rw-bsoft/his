$package("chis.application.tr.script.sms")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.sms.QuestCriterionDictionaryMaintainList = function(cfg){
	chis.application.tr.script.sms.QuestCriterionDictionaryMaintainList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
    this.isCombined = true;
}

Ext.extend(chis.application.tr.script.sms.QuestCriterionDictionaryMaintainList,chis.script.BizSimpleListView,{
	onDblClick:function(grid,index,e){
		var r = this.getSelectedRecord();
		if(r){
			this.fireEvent("rowdblclick",r.data);
		}
	}
});