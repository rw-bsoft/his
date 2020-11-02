$package("chis.application.cvd.script")

$import("chis.script.BizTableFormView")

chis.application.cvd.script.SuggestionForm = function(cfg) {
	cfg.colCount = 1;
	cfg.fldDefaultWidth = 500
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.width = 700;
	cfg.saveServiceId = "chis.simpleSave"
	cfg.entryName = 'chis.application.cvd.schemas.CVD_Suggestion'
	chis.application.cvd.script.SuggestionForm.superclass.constructor.apply(this, [cfg])
	this.on("winShow",this.onWinShow,this)
}
Ext.extend(chis.application.cvd.script.SuggestionForm, chis.script.BizTableFormView, {
			
			onWinShow:function(){
				var form = this.form.getForm()
				var content = form.findField("content")
				content.getToolbar().setVisible(false);
			}
		})