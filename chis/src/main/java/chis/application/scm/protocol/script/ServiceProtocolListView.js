$package("chis.application.scm.protocol.script")
$import("chis.script.BizSimpleListView")
chis.application.scm.protocol.script.ServiceProtocolListView = function(cfg){
	chis.application.scm.protocol.script.ServiceProtocolListView.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.scm.protocol.script.ServiceProtocolListView,chis.script.BizSimpleListView,{
	doCreateSP : function(){
		this.showSPModule("create",null);
	},
	onDblClick : function(grid, index, e) {
		this.doModify();
	},
	doModify : function(){
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		this.showSPModule("update",r.get("PRID"));
	},
	showSPModule : function(op,PRID){
		var module = this.createSimpleModule("SPForm",
				this.refModule);
		this.showWin(module);
		if(op=="update"){
			//module.on("save", this.onSave, this);
			module.initDataId= PRID;
			module.loadData();
		}else{
			module.initDataId = null;
			module.op = "create";
			module.doNew();
		}
		module.on("save",this.refresh,this);
	},
	onSave : function() {
	    this.refresh();
	}
});