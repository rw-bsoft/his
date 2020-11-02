$package("chis.application.per.script.setmeal");

$import("chis.script.BizCombinedModule2");

chis.application.per.script.setmeal.CheckupSetMealModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.per.script.setmeal.CheckupSetMealModule.superclass.constructor.apply(this,
			[cfg]);
	this.width = 780;
	this.height = 400;
	this.itemHeight = 66;
	this.layOutRegion = "north";
	this.itemCollapsible = false;
	this.frame = true;
	this.setMealEntry = "chis.application.per.schemas.PER_Combo";
	this.setMealDetailEntry = "chis.application.per.schemas.PER_ComboDetail";
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.per.script.setmeal.CheckupSetMealModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.per.script.setmeal.CheckupSetMealModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("create",this.createSetMeal,this);
				this.form.on("beforeSave",this.onBeforeSaveSetmealForm,this);
				this.form.on("save",this.onSaveSetMeal,this);
				this.form.on("cancel",this.cancelSetMeal,this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("saveSetMeal",this.saveSetMeal,this);
				return panel;
			},
			loadData : function() {
				this.form.op = this.op;
				if(this.op == "create"){
					this.form.doCreate();
					return;
				}
				var mealId = this.exContext.args.data.id;
				if(mealId){
					var result = util.rmi.miniJsonRequestSync({
						serviceId : "chis.checkupSetMealService",
						serviceAction : "getSetMealIsUsed",
						method:"execute",
						body : {
							setMealId : mealId
						}
					});
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg, this.loadInitData);
						return;
					}
					this.list.setMealIsUsed = result.json.body.isUsed;
				}
				var setmealData = this.castListDataToForm(
						this.exContext.args.data, this.form.schema);
				this.form.initFormData(setmealData);
				this.form.setManaUnitId();
				this.list.comboId = this.exContext.args.data.id;
				this.list.loadData();
			},
			createSetMeal : function(){
				this.form.doNew();
				this.list.comboId = "";
				this.list.requestData.cnd = ['eq',['$','comboId'],['s','']];
				this.list.store.removeAll();
				this.list.store.commitChanges();
			},
			onBeforeSaveSetmealForm : function(entryName,op,saveRequest){
				if(op == "update"){
					return true;
				}
				if(this.fireEvent("beforeSaveSetMeal",entryName,op,saveRequest)){
					Ext.Msg.alert("套餐名称重复","套餐名称[" + saveRequest.name + "]已经存在!");
					return false;
				}
				return true;
			},
			onSaveSetMeal : function(entryName,op,json,data){
				if(op=="create"){
					this.list.comboId = json.body.id;
					this.comboId = json.body.id;
				}else{
					this.list.comboId = this.exContext.args.data.id || this.comboId;
				}
				this.fireEvent("toRefreshMainList");
				var listRecordNum = this.list.store.getCount();
				if(this.openAddSetMealDetailFrom || listRecordNum == 0){
					var data = {};
					this.list.showSetMealDetailForm("create",data);
				}
			},
			cancelSetMeal : function(){
				this.win.hide();
			},
			saveSetMeal :function(){
				this.form.doSave();
				this.openAddSetMealDetailFrom = false;
			},
			onWinShow : function() {
				this.loadData();
			}

		});