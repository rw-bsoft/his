$package("chis.application.piv.script")

$import("chis.script.BizSimpleListView")

chis.application.piv.script.VaccinateList=function(cfg){
	this.entryName = "chis.application.piv.schemas.PIV_VaccinateList"
	chis.application.piv.script.VaccinateList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.piv.script.VaccinateList,chis.script.BizSimpleListView,{
	refresh:function(){
		this.requestData.cnd=['eq',['$','phrId'],['s',this.phrId]]
		this.loadData();
	}
});