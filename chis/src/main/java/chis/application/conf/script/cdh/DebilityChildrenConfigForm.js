$package("chis.application.conf.script.cdh")

$import("chis.application.conf.script.SystemConfigUtilForm")

chis.application.conf.script.cdh.DebilityChildrenConfigForm = function(cfg) {
    cfg.fldDefaultWidth = 220
    cfg.autoFieldWidth = false;
    cfg.colCount = 2;
    cfg.labelWidth = 120;
	chis.application.conf.script.cdh.DebilityChildrenConfigForm.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.conf.script.cdh.DebilityChildrenConfigForm, chis.application.conf.script.SystemConfigUtilForm,
		{
      
			onReady : function() {
				this.form.getForm().findField("exceptionalCase").on("check", this.onExceptionalCase, this);
			},
			
			onExceptionalCase : function(combo){
				this.fireEvent("exceptionalCase", combo.getValue());
			},
      
			saveToServer : function(saveData){
		        this.fireEvent("save",saveData,this);
			}
		});