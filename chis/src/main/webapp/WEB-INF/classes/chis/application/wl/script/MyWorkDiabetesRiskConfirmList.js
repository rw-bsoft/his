$package("chis.application.wl.script");
$import("chis.application.dbs.script.risk.DiabetesRiskListView");

chis.application.wl.script.MyWorkDiabetesRiskConfirmList = function(cfg) {
	this.initCnd = cfg.cnds
	chis.application.wl.script.MyWorkDiabetesRiskConfirmList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkDiabetesRiskConfirmList, chis.application.dbs.script.risk.DiabetesRiskListView, {
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
	onDblClick:function(){
		this.doConfirm()
	}
	,
	doConfirm : function(){
		var r = this.grid.getSelectionModel().getSelected()
		if(r == null){
			return;
		}
		var module = this.createSimpleModule("DiabetesRiskConfirmForm",
		"chis.application.dbs.DBS/DBS/D17-1");
		module.on("save",this.onSave,this)
		this.refreshExContextData(module, this.exContext);
		Ext.apply(module.exContext.args, r.data)
		module.exContext.args.riskId = r.data.otherId
//		module.initDataId = r.data.otherId
		module.getWin().show();
	}
	,
	onSave:function(entryName,data){
		var r = this.grid.getSelectionModel().getSelected()
		this.loadData()	
		if(data.result == 1){
			Ext.Msg.confirm("消息提示","是否进行首次高危评估",function(btn){
				if(btn == "yes"){
					if(r == null){
						return;
					}
					var empiId = r.get("empiId")
					var cfg = {}
					cfg.empiId = empiId
					cfg.initModules = ['D_06']
					cfg.closeNav = true
					cfg.mainApp = this.mainApp
					cfg.exContext = this.exContext
					var module = this.midiModules["DiabetesRiskListView_EHRView"]
					if (!module) {
						$import("chis.script.EHRView")
						module = new chis.script.EHRView(cfg)
						module.exContext.ids.riskId = r.get("otherId")
						module.on("save", this.onSave, this)
						this.midiModules["DiabetesRiskListView_EHRView"] = module
					} else {
						Ext.apply(module, cfg)
						module.exContext.ids = {}
						module.exContext.ids.empiId = empiId
						module.exContext.ids.riskId= r.get("otherId")
						module.refresh()
					}
					module.getWin().show()
				}
			},this);
		}
	}
});