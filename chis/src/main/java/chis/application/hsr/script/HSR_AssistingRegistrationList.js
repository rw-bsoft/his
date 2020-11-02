$package("chis.application.hsr.script")

$import("chis.script.BizSimpleListView")

chis.application.hsr.script.HSR_AssistingRegistrationList=function(cfg){
	chis.application.hsr.script.HSR_AssistingRegistrationList.superclass.constructor.apply(this,[cfg]);
} 

Ext.extend(chis.application.hsr.script.HSR_AssistingRegistrationList,chis.script.BizSimpleListView,{
	doCreateAssis : function() {
	var module = this.createSimpleModule("DCHSR02_01",
			this.dchsr02form);
	module.on("save", this.onSave, this);
	module.initDataId = null;
	module.op = "create";
	module.exContext.control = {
		"update" : true
	};
	this.showWin(module);
	module.doNew();
},doModify : function() {
	var r = this.getSelectedRecord();
	if (!r) {
		return;
	}
	var module = this.createSimpleModule("DCHSR02_01",
			this.dchsr02form);
	module.on("save", this.onSave, this);
	module.initDataId = r.id;
	this.showWin(module);
	module.loadData();
},onDblClick : function(grid, index, e) {
	this.doModify();
},onSave : function() {
    this.refresh();
}

});