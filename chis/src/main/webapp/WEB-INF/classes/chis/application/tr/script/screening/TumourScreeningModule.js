$package("chis.application.tr.script.screening");

$import("chis.script.BizCombinedTabModule");

chis.application.tr.script.screening.TumourScreeningModule = function(cfg){
	cfg.autoLoadData = false;
    cfg.itemWidth = 170;
    cfg.width = 980;
    cfg.height = 600;
    cfg.title="T初筛人群管理器";
	chis.application.tr.script.screening.TumourScreeningModule.superclass.constructor.apply(this,[cfg]);
	this.on("loadModule", this.onLoadModule, this);
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningModule,chis.script.BizCombinedTabModule,{
	initPanel : function() {
		var panel = chis.application.tr.script.screening.TumourScreeningModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.superList = panel.items.items[0]; 
		this.list = this.midiModules[this.otherActions.id];
        this.list.on("loadData", this.onLoadGridData, this);
        this.grid = this.list.grid;
        this.grid.on("rowClick", this.onRowClick, this);
		return panel;
	},
	onLoadModule : function(moduleId, module) {
         Ext.apply(moduleId.exContext,this.exContext);
		 if (moduleId == this.actions[0].id) {
            this.TSForm = module;
            this.TSForm.on("doNew",this.TSFormDoNew,this);
            this.TSForm.on("save",this.TSFormSaveAfter,this);
		 }
		  if (moduleId == this.actions[1].id) {
            this.TSCRList = module;
		 }
		 if(moduleId == this.actions[2].id){
		 	this.PMHM = module;
		 	this.PMHM.on("PMHSave",this.onPMHSave,this);
		 }
	},
	TSFormDoNew : function(form){
		Ext.apply(this.TSForm.exContext,this.exContext);
		if(this.TSCRList){
			this.TSCRList.clear();
			this.TSCRList.exContext = {};
		}
		this.setTabItemDisabled(1,true);
		this.setTabItemDisabled(2,true);
	},
	TSFormSaveAfter : function(entryName,op,json,data){
		if(op == "create"){
			this.setTabItemDisabled(1,false);
			this.setTabItemDisabled(2,false);
			var recordId = json.body.recordId;
			if(!this.exContext.args){
				this.exContext.args = {};
			}
			this.exContext.args.screeningId = recordId;
			this.exContext.args.empiId = data.empiId;
			this.exContext.args.phrId = data.phrId;
			this.exContext.args.highRiskType = data.highRiskType;
			this.exContext.args.highRiskType_text=data.highRiskType_text;
			Ext.apply(this.list.exContext,this.exContext);
			if(this.TSCRList){
				Ext.apply(this.TSCRList.exContext,this.exContext);
			}
		}
		this.list.refresh()
		this.fireEvent("save",entryName,op,json,data);
	},
	onPMHSave : function(entryName,op,json,data){
		if(this.exContext.args.comefrom == 'definiteDiagnosis'){
			this.showConfirmedWin(this.exContext.args.phrId,this.exContext.args.tsData);
		}
	},
	showConfirmedWin : function(phrId,tsData){
		var tcrData = {};
		tcrData.phrId = phrId;
		tcrData.empiId = tsData.empiId;
		tcrData.highRiskType = tsData.highRiskType;
		tcrData.highRiskSource = tsData.highRiskSource;
		tcrData.confirmedSource = {"key":"1","text":"初筛"};
		tcrData.year = tsData.year;
		tcrData.notification = {"key":"n","text":"否"};
		tcrData.status = {"key":"0","text":"正常"};
		tcrData.nature = {"key":"4","text":"确诊"};
		tcrData.recordId = tsData.recordId;
		this.showTCEhrViewWin(tcrData);
	},
	showTCEhrViewWin : function(tcrData) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_06'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourConfirmed_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourConfirmed_EHRView"] = module;
			module.exContext.ids.empiId = tcrData.empiId;
			module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = 0;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.empiId = tcrData.empiId;;
			module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.turnConfirmed = true;
			module.exContext.args.recordId = tcrData.recordId;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
			module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
			module.exContext.args.loadAction="getTCByEH";
			module.exContext.control = {};
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids.empiId = tcrData.empiId;
			module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = 0;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.empiId = tcrData.empiId;;
			module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.turnConfirmed = true;
			module.exContext.args.recordId = tcrData.recordId;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
			module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
			module.exContext.args.loadAction="getTCByEH";
			module.exContext.control = {};
			module.refresh();
		}
		module.getWin().show();
	},
	onLoadGridData : function(store) {
		if (store.getCount() == 0) {
            this.activeModule(0);
            return;
        }
        var index = this.list.selectedIndex;
        for(var i=0,len=store.getCount();i<len;i++){
        	var r = store.getAt(i);
        	var recordId = r.get("recordId");
        	if(recordId == this.exContext.args.screeningId){
        		index = i;
        		break;
        	}
        }
        if(!index){
        	index = 0;
        }
        this.list.selectedIndex = index;
        this.list.selectRow(index);
        var r = store.getAt(index);
        if(this.TSFormCreate){
        	this.TSForm.doCreate();
        }else{
        	this.process(r, index,"loadData");
        }
	},
	onRowClick : function(grid, index, e) {
	  if (!this.list) {
           return;
       }
       var r = this.list.grid.getSelectionModel().getSelected();
       if (!r) {
           return;
       }
       this.list.selectedIndex = index;
	   this.process(r, index,"rowClick");
	},
	process : function(r, index,op) {
		if(op == "rowClick"){
			this.activeModule(0);
		}
		this.exContext.args.highRiskType=r.get("highRiskType");
		this.exContext.args.highRiskType_text=r.get("highRiskType_text");
		this.exContext.args.screeningId = r.get("recordId");
		this.exContext.args.empiId = r.get("empiId");
		this.exContext.args.phrId = r.get("phrId");
		this.exContext.args.nature = r.get("nature");
		var frmData = this.castListDataToForm(r.data,this.list.schema);
		this.exContext.args.tsData = frmData;
		if(this.TSForm){
			this.TSForm.initFormData(frmData);
			Ext.apply(this.TSForm.exContext,this.exContext);
		}
		if(this.PMHM){
			Ext.apply(this.PMHM.exContext,this.exContext);
		}
		this.setTabItemDisabled(1,false);
		this.setTabItemDisabled(2,false);
		if(this.TSCRList){
			Ext.apply(this.TSCRList.exContext,this.exContext);
		}
	},
	doNew : function(){
		this.activeModule(0);
		this.list.exContext = {};
		this.list.clear();
		Ext.apply(this.list.exContext,this.exContext);
		if(this.TSForm){
			this.TSForm.exContext = {};
			this.TSForm.doCreate();
			Ext.apply(this.TSForm.exContext,this.exContext);
		}
		if(this.TSCRList){
			//this.TSCRList.clear();
			this.TSCRList.exContext = {};
			Ext.apply(this.TSCRList.exContext,this.exContext);
		}
		if(this.PMHM){
			this.PMHM.exContext = {};
			Ext.apply(this.PMHM.exContext,this.exContext);
		}
		this.setTabItemDisabled(1,true);
		this.setTabItemDisabled(2,true);
		this.list.loadData();
		this.TSFormCreate = true;		
	},
	loadData : function(){
		this.activeModule(this.activeTabIndex || 0);
		Ext.apply(this.list.exContext,this.exContext);
		if(this.TSForm){
			Ext.apply(this.TSForm.exContext,this.exContext);
		}
		if(this.TSCRList){
			Ext.apply(this.TSCRList.exContext,this.exContext);
		}
		if(this.PMHM){
			Ext.apply(this.PMHM.exContext,this.exContext);
		}
		this.list.loadData();
		this.setTabItemDisabled(1,false);
		this.setTabItemDisabled(2,false);
		this.TSFormCreate = false;
	},
	/**
	 * 面板切换事件
	 * 
	 * 
	 * @param {}
	 *            tabPanel
	 * @param {}
	 *            newTab
	 * @param {}
	 *            curTab
	 */
	onTabChange : function(tabPanel, newTab, curTab) {
		if (!newTab) {
			return;
		}
		// ** 模块是否已经激活过，即已经加载过数据，若为true则不再往下执行
		if (newTab.__actived) {
			if(newTab.name=="PMHM"){
				this.superList.collapse(true);
			}else{
				this.superList.expand(true);
			}
			return;
		}

		// ** 模块是否已经初始化过，即是否已经构建过，若为true则刷新页面，fase则构建页面
		if (newTab.__inited) {
			this.refreshModule(newTab);
			return;
		}
		var p = this.getCombinedModule(newTab.name, newTab.exCfg.ref);
		newTab.add(p);
		newTab.__inited = true;
		this.tab.doLayout()
		var m = this.midiModules[newTab.name];
		if(newTab.name=="PMHM"){
			this.superList.collapse(true);
		}else{
			this.superList.expand(true);
		}
		// **面板加载完成，可捕获此事件，完善面板，如增加模块的事件捕捉
		this.fireEvent("loadModule", newTab.name, m);
		if (m.loadData && m.autoLoadData == false) {
			m.loadData()
			newTab.__actived = true;
		}
	}
});