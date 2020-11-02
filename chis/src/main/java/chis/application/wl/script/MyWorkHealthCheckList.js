$package("chis.application.wl.script");
$import("chis.application.hr.script.HealthRecordList");

chis.application.wl.script.MyWorkHealthCheckList = function(cfg) {
	this.initCnd = cfg.cnds || ['in', ['$', 'a.status'], ['0', '2']];
	chis.application.wl.script.MyWorkHealthCheckList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkHealthCheckList, chis.application.hr.script.HealthRecordList, {
	
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	
		onSelectEMPI : function(r) {
			this.empiId = r.empiId
//			var result= util.rmi.miniJsonRequestSync({
//								serviceId:"hypertensionService",
//								serviceAction:"getId",
//								body:{empiId:this.empiId}})
//			if(result.code > 300){
//				this.processReturnMsg(result.code, result.msg);
//					return;
//			}
//		var body = result.json["body"];
//				if (body && body.length > 0) {
//					this.initModules = ["C06"]
//				}else{
//					this.initModules = ["D06"]
//				}
		var m = this.midiModules["ehrView"];
		if (!m) {
			$import("chis.script.EHRView")
			m = new chis.script.EHRView({
						closeNav : true,
						initModules :["B_10"],
						mainApp : this.mainApp,
						empiId : this.empiId,
						activeTab : 1
					});
			this.midiModules["ehrView"] = m;
			m.on("save", this.refresh, this);
		} else {
			m.ids = {};
			m.ids["empiId"] = this.empiId;
			m.refresh();
		}
		m.getWin().show();
	}
});