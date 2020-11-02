$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizCombinedTabModule","chis.script.util.widgets.MyMessageTip");

chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionModule = function(cfg){
	cfg.autoLoadData = false;
    cfg.itemWidth = 170;
    cfg.width = 830;
    cfg.height = 400;
    cfg.title="初筛转高危标准维护器";
	chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionModule.superclass.constructor.apply(this,[cfg]);
	this.on("loadModule", this.onLoadModule, this);
	this.modal = true;
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionModule,chis.script.BizCombinedTabModule,{
	initPanel : function() {
		var panel = chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.otherActions.id];
        this.list.on("loadData", this.onLoadGridData, this);
        this.grid = this.list.grid;
        this.grid.on("rowClick", this.onRowClick, this);
		return panel;
	},
	onLoadModule : function(moduleId, module) {
         Ext.apply(moduleId.exContext,this.exContext);
		 if (moduleId == this.actions[0].id) {
            this.THCForm = module;
            this.THCForm.on("save",this.THCFormSaveAfter,this);
		 }
		 if (moduleId == this.actions[1].id) {
            this.THCDList = module;
		 }
		 if (moduleId == this.actions[2].id) {
            this.THCQList = module;
		 }
	},
	THCFormSaveAfter : function(entryName,op,json,data){
		var body = json.body;
		var isExist = false;
		if(body){
			isExist = body.isExist;
		}
		if(isExist){
			MyMessageTip.msg("提示","该年分该来源该类别该高危因素的初筛转高危标准已存在！",true);
			return;
		}
		if(op == "create" && !isExist){
			this.setTabItemDisabled(1,false);
			var hrcId = json.body.hrcId;
			if(!this.exContext.args){
				this.exContext.args = {};
			}
			var highRiskTypeVal = this.THCForm.getHighRiskType();
			this.exContext.args.hrcId = hrcId;
			this.exContext.args.highRiskType = highRiskTypeVal.key;
			this.exContext.args.highRiskType_text = highRiskTypeVal.text;
			Ext.apply(this.list.exContext,this.exContext);
			if(this.THCDList){
				Ext.apply(this.THCDList.exContext,this.exContext);
			}
			if(this.THCQList){
				Ext.apply(this.THCQList.exContext,this.exContext);
			}
		}
		this.list.refresh()
		this.fireEvent("save",entryName,op,json,data);
	},
	onLoadGridData : function(store) {
		if (store.getCount() == 0) {
            this.activeModule(0);
            return;
        }
        var index = this.list.selectedIndex;
        for(var i=0,len=store.getCount();i<len;i++){
        	var r = store.getAt(i);
        	var hrcId = r.get("hrcId");
        	if(hrcId == this.exContext.args.hrcId){
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
        this.process(r, index);
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
	   this.process(r, index);
	},
	process : function(r, index) {
		this.activeModule(0);
		var frmData = this.castListDataToForm(r.data,this.THCForm.schema);
		this.THCForm.initFormData(frmData);
		var hrcId = r.get("hrcId");
		this.exContext.args.hrcId = hrcId;
		this.exContext.args.year = r.get("year");
		this.exContext.args.highRiskType={key:r.get("highRiskType"),text:r.get("highRiskType_text")};
		this.exContext.args.criterionType = {key:"3",text:"高危"};
		this.exContext.args.criterionExplain={key:"2",text:"可疑"};
		if(this.THCDList){
			Ext.apply(this.THCDList.exContext,this.exContext);
		}
		var highRiskFactor = r.get("highRiskFactor");
		if(highRiskFactor == "1"){
			this.setTabItemDisabled(2,false);
		}else{
			this.setTabItemDisabled(2,true);
		}
		if(this.THCQList){
			Ext.apply(this.THCQList.exContext,this.exContext);
		}
	},
	doNew : function(){
		this.activeModule(0);
		this.list.exContext = {};
		this.THCForm.exContext = {};
		this.list.clear();
		this.THCForm.doCreate();
		if(this.THCDList){
			this.THCDList.clear();
			this.THCDList.exContext = {};
		}
		if(this.THCQList){
			this.THCQList.clear();
			this.THCQList.exContext = {};
		}
		this.setTabItemDisabled(1,true);
		this.setTabItemDisabled(2,true);
	},
	loadData : function(){
		this.activeModule(0);
		Ext.apply(this.list.exContext,this.exContext);
		Ext.apply(this.THCForm.exContext,this.exContext);
		if(this.THCDList){
			Ext.apply(this.THCDList.exContext,this.exContext);
		}
		if(this.THCQList){
			Ext.apply(this.THCQList.exContext,this.exContext);
		}
		this.list.loadData();
		this.setTabItemDisabled(1,false);
	}
	
});