$package("chis.application.per.script.setmeal");

$import("chis.script.BizTableFormView");

chis.application.per.script.setmeal.CheckupSetMealForm = function(cfg){
	cfg.autoLoadData = false;
	chis.application.per.script.setmeal.CheckupSetMealForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.checkupSetMealService";
	this.saveAction = "saveSetMeal";
};

Ext.extend(chis.application.per.script.setmeal.CheckupSetMealForm,chis.script.BizTableFormView,{
	doCreate : function(){
		this.op = "create";
		this.initDataId = null;
		this.fireEvent("create");
	},
	doCancel : function(){
		this.fireEvent("cancel");
	},
	onReady : function(){
		this.setManaUnitId();
		chis.application.per.script.setmeal.CheckupSetMealForm.superclass.onReady.call(this);
	},
	setManaUnitId : function() {
		var manageUnit = this.form.getForm().findField("manaUnitId");
		if (manageUnit) {
//			if (this.op == "create") {
//				if (this.mainApp.deptId.length < 9){
//					manageUnit.enable();
//				}else{
//					manageUnit.disable();
//				}
//			} else {
//				manageUnit.disable();
//			}
			manageUnit.on("beforeselect", this.checkManaUnit, this);
		}
	},
	checkManaUnit : function(comb, node) {
		var key = node.attributes['key'];
		if(this.mainApp.uid == 'system'){
			return true;
		}else{
			if(node.getDepth() == 0){//根节点
				return true;
			}else if (key.length == 9){
				return true;
			}else{
				return false;
			}
		}
	}
});