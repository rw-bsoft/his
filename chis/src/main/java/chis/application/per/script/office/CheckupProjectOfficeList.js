$package("chis.application.per.script.office");

$import("chis.script.BizSimpleListView");

chis.application.per.script.office.CheckupProjectOfficeList = function(cfg){

	chis.application.per.script.office.CheckupProjectOfficeList.superclass.constructor.apply(this,[cfg]);
	this.removeServiceId = "chis.checkupProjectOfficeService";
	this.removeAction = "deleteProjectOffice";
};

Ext.extend(chis.application.per.script.office.CheckupProjectOfficeList,chis.script.BizSimpleListView,{
	
});