$package("chis.application.scm.sr.script")
$import("chis.script.BizSimpleListView")

chis.application.scm.sr.script.SrContractPackageItemsList=function(cfg){
	cfg.autoLoadSchema=false;
	cfg.disablePagingTbr = true;
	chis.application.scm.sr.script.SrContractPackageItemsList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.scm.sr.script.SrContractPackageItemsList,chis.script.BizSimpleListView,{
	
});