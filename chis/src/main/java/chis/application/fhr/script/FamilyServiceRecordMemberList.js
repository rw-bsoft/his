$package("chis.application.fhr.script")
$import("chis.script.BizSimpleListView")
chis.application.fhr.script.FamilyServiceRecordMemberList = function(cfg) {
	chis.application.fhr.script.FamilyServiceRecordMemberList.superclass.constructor
			.apply(this, [cfg]);
	this.disableBar = true;
	this.disablePagingTbr = true;
	this.showButtonOnTop = false;
}
Ext.extend(chis.application.fhr.script.FamilyServiceRecordMemberList,
		chis.script.BizSimpleListView, {
	
});