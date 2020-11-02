$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.form.TableFormView", "chis.script.util.Vtype")

chis.application.conf.script.admin.EHR_CommunityBasicSituationView = function(cfg) {
	cfg.colCount = 3
	cfg.showButtonOnTop = true
	cfg.width = 800
	cfg.labelWidth = 80 
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 170
	cfg.serviceId = "chis.empiService"
	chis.application.conf.script.admin.EHR_CommunityBasicSituationView.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(chis.application.conf.script.admin.EHR_CommunityBasicSituationView,
		chis.script.app.modules.form.TableFormView, {
		initPanel:function(){
			
		} 
		}); 
