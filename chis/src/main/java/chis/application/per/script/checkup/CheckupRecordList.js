$package("chis.application.per.script.checkup");

$import("chis.script.BizSimpleListView");

chis.application.per.script.checkup.CheckupRecordList = function(cfg){

	chis.application.per.script.checkup.CheckupRecordList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadSchema = false;
	this.autoLoadData = false;
	this.disablePagingTbr = true;
	this.selectFirst = true;
	this.isCombined = true;
};

Ext.extend(chis.application.per.script.checkup.CheckupRecordList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd = ['eq', ['$', 'a.empiId'],['s', this.exContext.ids.empiId]];
		chis.application.per.script.checkup.CheckupRecordList.superclass.loadData.call(this);
		this.selectedCheckupNo = this.exContext.args.selectCheckupNo;
	}

});