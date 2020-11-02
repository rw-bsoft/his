$package("chis.application.scm.bag.script")
$import("chis.script.BizTableFormView","util.widgets.LookUpField")
chis.application.scm.bag.script.ServicePackageForm=function(cfg){
	cfg.colCount=2;
	chis.application.scm.bag.script.ServicePackageForm.superclass.constructor.apply(this,[cfg]);
//	Ext.apply(cfg.exContext,cfg.opener.exContext);
}

Ext.extend(chis.application.scm.bag.script.ServicePackageForm,chis.script.BizTableFormView,{
});