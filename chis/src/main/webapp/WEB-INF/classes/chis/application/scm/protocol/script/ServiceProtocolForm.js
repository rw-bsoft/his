$package("chis.application.scm.protocol.script")
$import("chis.script.BizTableFormView")
chis.application.scm.protocol.script.ServiceProtocolForm=function(cfg){
	cfg.colCount=2;
	chis.application.scm.protocol.script.ServiceProtocolForm.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.scm.protocol.script.ServiceProtocolForm,chis.script.BizTableFormView,{
	
});