$package("chis.application.piv.script")

$import("app.modules.list.TreeNavListView")

chis.application.piv.script.VaccinateRecordListView=function(cfg){
	chis.application.piv.script.VaccinateRecordListView.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.piv.script.VaccinateRecordListView,app.modules.list.TreeNavListView,{
//	refresh:function(){
//		if(this.phrId && this.phrId.length>0){
//			var cnd = ['eq',['$','phrId'],['s',this.phrId]];
//			this.request.cnd = cnd ; 
//			this.loadData();
//		}
//	},
	doModify:function(){
		var r = this.getSelectedRecord();
		if(!r)
			return ;
		var empiId = r.get("empiId");
		var phrId = r.get("phrId");
		var cfg = {}
		cfg.initModules=['I01']
		cfg.closeNav = true
		cfg.empiId=empiId 
		cfg.phrId=phrId
		cfg.mainApp = this.mainApp
		var module = this.midiModules["EHRView"]
		if(!module){
			$import("chis.script.EHRView")
			module = new chis.script.EHRView(cfg)
			module.on("save",this.onSave,this)
			this.midiModules["EHRView"] = module
		}else{
			module.ids = {}
			module.ids.empiId = empiId
			module.ids.phrId = phrId
			module.refresh()
		}
		module.getWin().show()
	},
	onSave:function(){
		this.refresh();
	},
	onDblClick:function(){
		this.doModify();
	}
});