$package("chis.application.demo.script");

$import("chis.script.BizSimpleListView","chis.application.mpi.script.EMPIInfoModule","chis.script.EHRView");

chis.application.demo.script.PersonInfoList = function(cfg){
	chis.application.demo.script.PersonInfoList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.demo.script.PersonInfoList,chis.script.BizSimpleListView,{
//	doAdd : function(){
//		var module = this.createSimpleModule("demoForm",this.ref);
//		module.on("toFreshList", this.onFormSaveAfter, this);
//		module.initDataId = null;
//		module.op = "create";
//		module.initPanel();
//		this.showWin(module);
//		module.doNew();
//	},
	doAdd : function() {
		var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
		if (!advancedSearchView) {
			advancedSearchView = new chis.application.mpi.script.EMPIInfoModule({
					title : "个人基本信息查找",
					modal : true,
					mainApp : this.mainApp
				});
			advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,this);
			this.midiModules["EMPI.ExpertQuery"] = advancedSearchView;
		}
		var win = advancedSearchView.getWin();
		win.setPosition(250, 100);
		win.show();
	},

	onEmpiSelected : function(empi) {
		this.empiId = empi["empiId"];
		this.pkey = null;
		this.recordStatus = 0;
		this.activeTab = 0;
		this.showEhrViewWin();
	},

	onFormSaveAfter : function(entryName, op, json, data){
		this.refresh();
	},
	
//	doModify : function(){
//		var r = this.getSelectedRecord();
//		if(!r){
//			return;
//		}
//		var pkey = r.get("id");
//		var module = this.createSimpleModule("demoForm",this.ref);
//		module.on("toFreshList", this.onFormSaveAfter, this);
//		module.initDataId = pkey;
//		module.op = "update";
//		this.showWin(module);
//		module.loadData();
//	},
	
	doModify : function(){
		var r = this.getSelectedRecord();
		this.pkey = r.get("id");
		this.empiId = r.get("empiId");
		this.activeTab = 0;
		this.showEhrViewWin();
	},

	onDblClick : function(){
		this.doModify();
	},
	
	showEhrViewWin : function() {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['DEMO_01'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab || 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["PersonInfo_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["PersonInfo_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.args.pkey = this.pkey;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.args.pkey = this.pkey;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	}

});