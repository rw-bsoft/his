$package("chis.application.psy.script.assessment");

$import("chis.script.BizCombinedModule2");

chis.application.psy.script.assessment.AnnualAssessmentModule = function(cfg){
	cfg.autoLoadData = false;
	cfg.itemWidth = 210;
	chis.application.psy.script.assessment.AnnualAssessmentModule.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.psy.script.assessment.AnnualAssessmentModule,chis.script.BizCombinedModule2,{
	initPanel : function(){
		var panel = chis.application.psy.script.assessment.AnnualAssessmentModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
     	this.grid.on("rowClick", this.onRowClick, this);
		this.form = this.midiModules[this.actions[1].id];
		this.form.on("saveAnnual", this.onSave, this);
		this.form.on("add", this.onAdd, this);
		return panel;
	},
	
	loadData : function(){
		var body = {
			"empiId" : this.exContext.ids.empiId,
			"phrId" : this.exContext.ids.phrId,
			"EHR_HealthRecord.status" : this.exContext.ids["phrId.status"],
			"PSY_PsychosisRecord.status" : this.exContext.ids["PSY_PsychosisRecord.phrId.status"],
			"PSY_PsychosisRecord.manaUnitId" : this.mainApp.deptId,
			"PSY_PsychosisRecord.manaDoctorId" : this.mainApp.uid
		};
		var result = util.rmi.miniJsonRequestSync({
			serviceId : "chis.psychosisRecordPaperService",
			serviceAction : "getPsyPaperControl",
			method:"execute",
			body : body
		});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		Ext.apply(this.list.exContext,this.exContext);
		Ext.apply(this.form.exContext,this.exContext);
		Ext.apply(this.form.exContext.control,result.json.body._actions);
		this.list.loadData();
	},
	
	onLoadGridData : function(store){
    	if (store.getCount() == 0) {
    		this.form.doCreate();
            return;
        }
        var createEnable = this.exContext.control.create;
        if(createEnable){
        	var year =Date.parseDate(this.mainApp.serverDate, "Y-m-d").getFullYear();
			var n = store.getCount();
			for(var i=0;i<n;i++){
				var r = store.getAt(i);
				var createDate = new Date(r.get("createDate"));
				if(createDate){
					var ryear = createDate.getFullYear();
					if(year <= ryear){
						this.createDisable = true;
						break;
					}
				}
			}
        }
        var index = this.list.selectedIndex;
        if(!index){
        	index = 0;
        }
        if(this.op && this.op =="create"){
        	index = store.getCount() - 1;
        }
        this.list.selectedIndex = index;
        this.list.selectRow(index);
        var r = store.getAt(index);
        this.process(r, index);
    },
    
    onRowClick : function(grid, index, e) {
    	this.list.selectedIndex = index;
		var r = grid.store.getAt(index);
		this.process(r, index);
	},
    
    process : function(r, n) {
		if (!r) {
			return;
		}
		this.form.initDataId = r.get("assessmentId");
		var formData = this.castListDataToForm(r.data,this.list.schema);
		this.form.initFormData(formData);
		//this.form.setCreateDisOrEnable(this.createDisable);
	},
	onSave : function(op,json,data) {
		this.list.loadData();
		this.op = op;
	}
	,
	onAdd : function() {
		if(this.checkHasAssessment()){
			return
		}
		this.midiModules[this.actions[1].id].doNew();
	}
	,
	checkHasAssessment:function(){
		var assDate = this.mainApp.serverDate
		for (var i = 0; i < this.list.store.getCount(); i++) {
			var r = this.list.store.getAt(i);
			if (assDate == r.get("createDate").substring(0,10)) {
				Ext.Msg.alert("提示", r.get("createDate").substr(0,10) +"已有询问记录，不需再重新录入。");
				return true;
			}
		}
		return false
	}
});