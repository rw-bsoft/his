$package("chis.application.per.script.setmeal");

$import("chis.script.BizSimpleListView");

chis.application.per.script.setmeal.CheckupSetMealList = function(cfg){
	
	chis.application.per.script.setmeal.CheckupSetMealList.superclass.constructor.apply(this,[cfg]);
	this.removeServiceId = "chis.checkupSetMealService";
	this.removeAction = "removeSetMeal";
};

Ext.extend(chis.application.per.script.setmeal.CheckupSetMealList,chis.script.BizSimpleListView,{
	doCreateSetMeal : function(){
		var data={};
		data.id="";
		this.showSetMealForm("create",data);
	},
	doModify : function(){
		if(!this.getSelectedRecord()){
			return;
		}
		var data = this.getSelectedRecord().data;
		this.showSetMealForm("update",data);
	},
	showSetMealForm : function(op,data){
		var module = this.midiModules["CheckupSetMealModule"];
		if(!module){
			module = this.createCombinedModule("CheckupSetMealModule",this.setmealRefId);
			module.exContext.args.data = data;
		}else{
			module.exContext.args.data = data;
		}
		module.on("beforeSaveSetMeal",this.onBeforeSaveSetMeal,this);
		module.on("toRefreshMainList",this.onRefreshMainList,this);
		module.op = op;
		module.getWin().show();
	},
	onDblClick : function(grid, index, e) {
        this.doModify();
    },
    onBeforeSaveSetMeal : function(entryName,op,saveRequest){
    	var len = this.store.getCount();
    	var name = saveRequest.name;
    	for(var i = 0; i < len; i++){
    		var r = this.store.getAt(i);
    		if(name == r.get("name")){
    			return true;
    		}
    	}
    	return false;
    },
    onRefreshMainList : function(){
    	this.refresh();
    }
    
});