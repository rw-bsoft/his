$package("chis.application.phe.script")
$import("chis.script.BizTableFormView");

chis.application.phe.script.PHE_EnvironmentalEventForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 160;
	cfg.labelWidth = 95;
	cfg.width=780;
	chis.application.phe.script.PHE_EnvironmentalEventForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("beforePrint", this.onBeforePrint, this)
};
Ext.extend(chis.application.phe.script.PHE_EnvironmentalEventForm,
		chis.script.BizTableFormView,{
onReady : function() {

	chis.application.phe.script.PHE_EnvironmentalEventForm.superclass.onReady.call(this);
	var form = this.form.getForm();
	var pathogenicFactor = form.findField("pathogenicFactor");
	if (pathogenicFactor) {
		pathogenicFactor.on("select", this.onSelectPathogenicFactor,this);
	}
	var eventReason = form.findField("eventReason");
		if (eventReason) {
	    eventReason.on("select", this.onSelectEventReason,this);
	}
	
},
onLoadData:function onLoadData(entry,body){
	var pathArray =  body.pathogenicFactor.key.split(",");
	var has1 , has2 = null ; 
	for(var i = 0 ; i<pathArray.length ; i++){
		if(pathArray[i]==1){
			has1 = true;
		}else if(pathArray[i]==2){
			has2 = true;
		}
	}
	this.changeFiledStatePath(has1,has2) ;
	
	var eventArray = body.eventReason.key.split(",");
	var has4 , has5,has6 ,has7= null ; 
	for(var i = 0 ; i<eventArray.length ; i++){
		if(eventArray[i]==4){
			has4 = true;
		}else if(eventArray[i]==5){
			has5 = true;
		}else if(eventArray[i]==6){
			has6 = true;
		}else if(eventArray[i]==7){
			has7 = true;
		}
	}
	this.changeFiledStateEvent(has4, has5, has6, has7);
	
	
},changeFiledStateEvent:function(has4,has5,has6,has7){
	if(has4){
		this.changeFieldState(false, "biologicReason");
	}else{
		this.changeFieldState(true, "biologicReason");
	}
	if(has5){
		this.changeFieldState(false, "indoorPollution");
	}else{
		this.changeFieldState(true, "indoorPollution");
	}
	if(has6){
		this.changeFieldState(false, "industrialPollution");
	}else{
		this.changeFieldState(true, "industrialPollution");
	}
	if(has7){
		this.changeFieldState(false, "otherReason");
	}else{
		this.changeFieldState(true, "otherReason");
	}
},changeFiledStatePath:function(has1,has2){

	if(has1){
		this.changeFieldState(false, "air");
	}else{
		this.changeFieldState(true, "air");
	}
	if(has2){
		this.changeFieldState(false, "water");
	}else{
		this.changeFieldState(true, "water");
	}
},onSelectPathogenicFactor:function(item){
	var pathArray = item.getValue().split(",");
	var has1 , has2 = null ; 
	for(var i = 0 ; i<pathArray.length ; i++){
		if(pathArray[i]==1){
			has1 = true;
		}else if(pathArray[i]==2){
			has2 = true;
		}
	}
  this.changeFiledStatePath(has1,has2);
			
},onSelectEventReason:function(item){
	var eventArray = item.getValue().split(",");
	var has4 , has5,has6,has7= null ; 
	for(var i = 0 ; i<eventArray.length ; i++){
		if(eventArray[i]==4){
			has4 = true;
		}else if(eventArray[i]==5){
			has5 = true;
		}else if(eventArray[i]==6){
			has6 = true;
		}else if(eventArray[i]==7){
			has7 = true;
		}
	}
	this.changeFiledStateEvent(has4, has5, has6, has7);
},
onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.sanitationEvent"];
				ids_str.value = "&RecordID=" + this.initDataId;
				return true;
			}
});