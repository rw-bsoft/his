$package("chis.application.tr.script.sms")

$import("chis.script.BizCombinedModule2")

chis.application.tr.script.sms.TumourQuestionnaireCriterionModule = function(cfg){
	chis.application.tr.script.sms.TumourQuestionnaireCriterionModule.superclass.constructor.apply(this,[cfg]);
	this.layOutRegion = "north";
	this.itemHeight = 125;
	this.saveServiceId = "chis.tumourCriterionService";
	this.saveAction = "saveTQCriterion";
	this.on("save",this.onSaveAfter,this);
}

Ext.extend(chis.application.tr.script.sms.TumourQuestionnaireCriterionModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.sms.TumourQuestionnaireCriterionModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.form=this.midiModules[this.actions[0].id];
		this.form.on("saveToServer",this.onSaveToServer,this);
		this.qcdModule = this.midiModules[this.actions[1].id];
		this.form.on("selectMasterplate",this.onLoadQuestMasterplate,this);
		this.form.on("selectNoMasterplate",this.onLoadNoQuestMasterplate,this);
		return panel;
	},
	
	onLoadQuestMasterplate : function(masterplateId){
		this.qcdModule.QFieldListReLoad(masterplateId);
	},
	onLoadNoQuestMasterplate: function(masterplateId){
		this.qcdModule.doNew();
	},
	
	onSaveToServer:function(formData){
		if(!formData.recordId){
			this.op = "create";
		}else{
			this.op = "update";
		}
		var saveData = {};
		saveData.tqcRecord = formData;
		saveData.tqcdList = this.qcdModule.getQCDListData();
		this.data = {};
		this.saveToServer(saveData);
	},
	
	onSaveAfter : function(entryName,op,json,data){
		this.form.initFormData(json.body);
	},
	
	doNew : function(){
		Ext.apply(this.form.exContext,this.exContext);
		this.form.doCreate();
		this.form.data={};
		this.qcdModule.doNew();
	},
	
	loadData : function(){
		this.form.initDataId = this.exContext.args.recordId;
		this.form.loadData();
		Ext.apply(this.qcdModule.exContext.args,this.exContext.args);
		this.qcdModule.loadData();
	}
});