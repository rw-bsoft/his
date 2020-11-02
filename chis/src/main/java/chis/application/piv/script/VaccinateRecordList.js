$package("chis.application.piv.script")

$import("chis.script.BizTreeNavListView")

chis.application.piv.script.VaccinateRecordList=function(cfg){
	chis.application.piv.script.VaccinateRecordList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.piv.script.VaccinateRecordList,chis.script.BizTreeNavListView,{
	doModify:function(){
		var r = this.getSelectedRecord();
		if(!r){
			return ;
		}
		var empiId = r.get("empiId");
		var phrId = r.get("phrId");
		var cfg = {}
		cfg.initModules=['I_01']
		cfg.closeNav = true
		cfg.empiId = empiId
		cfg.phrId = phrId
		cfg.mainApp = this.mainApp
		var module = this.midiModules["EHRView"]
		if(!module){
			$import("chis.script.EHRView")
			module = new chis.script.EHRView(cfg)
			this.midiModules["EHRView"] = module
		}else{
			module.exContext.ids["empiId"] = empiId;
			module.refresh()
		}
		module.getWin().show()
	},
	
	onDblClick:function(){
		this.doModify();
	}
});