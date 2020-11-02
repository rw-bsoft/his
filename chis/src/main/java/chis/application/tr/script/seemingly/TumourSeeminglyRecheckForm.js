$package("chis.application.tr.script.seemingly")

$import("chis.script.BizTableFormView")

chis.application.tr.script.seemingly.TumourSeeminglyRecheckForm = function(cfg){
	cfg.colCount = 1;
	cfg.autoLoadSchema = false;
    cfg.autoLoadData = false;
	chis.application.tr.script.seemingly.TumourSeeminglyRecheckForm.superclass.constructor.apply(this,[cfg]);
	this.width = 300;
}

Ext.extend(chis.application.tr.script.seemingly.TumourSeeminglyRecheckForm,chis.script.BizTableFormView,{
	getSaveRequest:function(savaData){
		savaData.empiId = this.exContext.args.empiId;
		savaData.recheckStatus = 'y';
		return savaData;
	}
});