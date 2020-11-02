$package("chis.application.tr.script.sms")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.sms.QuestCriterionDetailList = function(cfg){
	chis.application.tr.script.sms.QuestCriterionDetailList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
    this.isCombined = true;
}

Ext.extend(chis.application.tr.script.sms.QuestCriterionDetailList,chis.script.BizSimpleListView,{
	onDblClick:function(grid,index,e){
		var r = this.getSelectedRecord();
		if(r){
			this.store.remove(r);
			this.store.commitChanges();
		}
	}
});