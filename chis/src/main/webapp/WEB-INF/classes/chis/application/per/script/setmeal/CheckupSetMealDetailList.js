$package("chis.application.per.script.setmeal");

$import("chis.script.BizSimpleListView");

chis.application.per.script.setmeal.CheckupSetMealDetailList = function(cfg){
	cfg.autoLoadData = false;
	chis.application.per.script.setmeal.CheckupSetMealDetailList.superclass.constructor.apply(this,[cfg]);
	this.on("loadData",this.onLoadData,this);
};

Ext.extend(chis.application.per.script.setmeal.CheckupSetMealDetailList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd = ['eq',['$','comboId'],['s',this.comboId]];
		chis.application.per.script.setmeal.CheckupSetMealDetailList.superclass.loadData.call(this);
	},
	
	onLoadData : function(){
		if(this.store.getCount() == 0 || this.setMealIsUsed){
			this.setTopBtnStatus(false,[2]);
		}else{
			this.setTopBtnStatus(true,[2]);
		}
	},
	
	doAdd : function(){
		if(!this.comboId){
			Ext.Msg.show({
				title : "提示",
				msg : "套餐未保存无法新建明细，是否保存？",
				modal : true,
				width : 300,
				buttons : {
						ok : '是',
						cancel : "否"
					},
				multiline : false,
				fn : function(btn, text) {
					if(btn == "ok"){
						this.fireEvent("saveSetMeal");
						return;
					}
					if (btn == "cancel") {
						return;
					}
				},
				scope : this
			});
			return;
		}
		var data = {};
		this.showSetMealDetailForm("create",data);
	},
	doModify : function(){
		this.openForm("update");
	},
	onDblClick : function(){
		this.doModify();
	},
	doReadSetMealDetail : function(){
		this.openForm("read");
	},
	openForm : function(op){
		if(!this.getSelectedRecord()){
			return;
		}
		var data = this.getSelectedRecord().data;
		this.showSetMealDetailForm(op,data);
	},
	showSetMealDetailForm : function(op,data){
		var module = this.midiModules["CheckupSetMealDetailForm"];
		if (!module) {
			var moduleCfg = this.loadModuleCfg(this.setmealDetailRefId);
			var cfg = {
				border : false,
				frame : false,
				autoLoadSchema : true,
				isCombined : false,
				exContext : {}
			};
			Ext.apply(cfg, moduleCfg);
			this.refreshExContextData(cfg, this.exContext);
			var cls = moduleCfg.script;
			if (!cls) {
				return;
			}
			$import(cls);
			module = eval("new " + cls + "(cfg)");
			module.setMainApp(this.mainApp);
			this.midiModules["CheckupSetMealDetailForm"] = module;
		}
		module.op = op;
		module.setMealIsUsed = this.setMealIsUsed;
		module.comboId = this.comboId;
		module.exContext.args.setmealDetailData = data;
		module.on("saveAfter",this.setmealDetailSaveAfter,this);
		module.on("projectOfficeBeforeSelect",this.onOfficeBeforeSelect,this);
		module.getWin().show();
	},
	setmealDetailSaveAfter : function(data){
//		this.store.add(new Ext.data.Record(data));
		if(this.store.getCount() > 0 && this.setMealIsUsed){
			this.setTopBtnStatus(true,[2]);
		}
		this.refresh();
	},
	onOfficeBeforeSelect : function(record){
		var len = this.store.getCount();
		var projectOfficeId = record.key;
		for(var i=0;i<len;i++){
			var r = this.store.getAt(i);
			if(projectOfficeId == r.get("projectOfficeId")){
				return true;
			}
		}
		return false;
	},
	
	setTopBtnStatus : function(enable,btns){
		var topBtns = this.grid.getTopToolbar().items;
		if (!topBtns) {
			return;
		}
		var n = topBtns.getCount();
		for(var j=0; j < btns.length; j++){
			for(var i=0; i < n; i++){
				if(i == btns[j]){
					if(enable){
						topBtns.item(i).enable();
					}else{
						topBtns.item(i).disable();
					}
				}
			}
		}
	}
});