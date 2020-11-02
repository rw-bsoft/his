$package("chis.application.hc.script");

$import("chis.script.BizTableFormView");

chis.application.hc.script.InhospitalSituationForm = function(cfg) {
	chis.application.hc.script.InhospitalSituationForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("beforeSave",this.onBeforeSave,this);
	this.on("loadData",this.onLoadData,this);
};

Ext.extend(chis.application.hc.script.InhospitalSituationForm, chis.script.BizTableFormView, {
	
	onReady:function(){
		chis.application.hc.script.InhospitalSituationForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var type = form.findField("type");
		if(type){
			type.on("blur",this.onType,this)
			type.on("select",this.onType,this)
		}
	},
	
	onType:function(comb){
		var value = comb.getValue();
		var form =  this.form.getForm();
		var inhospitalDate = form.findField("inhospitalDate");
		var outhospitalDate = form.findField("outhospitalDate");
		if(value==1){
			inhospitalDate.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>入院时间:</span>";
			outhospitalDate.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>出院时间:</span>";
		}else if(value==2){
			outhospitalDate.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>撤床时间:</span>";
			inhospitalDate.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>建床时间:</span>";
		}
	},
	
	onBeforeSave:function(entryName,op,saveData){
		var inhospitalDate = saveData.inhospitalDate
		var outhospitalDate = saveData.outhospitalDate
		
		if(outhospitalDate != "" && outhospitalDate < inhospitalDate){
			Ext.MessageBox.alert("提示","出院日期不能小于入院日期")
			return false;
		}
	},
	
	getSaveRequest : function(saveData) {
		saveData.healthCheck = this.exContext.args.healthCheck;
		return saveData;
	},
	
	onLoadData:function(entryName,body){
		var form = this.form.getForm();
		var type = form.findField("type");
		if(type){
			this.onType(type)
		}
	}
});