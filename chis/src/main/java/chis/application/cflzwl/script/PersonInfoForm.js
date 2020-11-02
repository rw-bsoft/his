$package("chis.application.demo.script");

$import("chis.script.BizTableFormView");

chis.application.demo.script.PersonInfoForm = function(cfg){
	
	chis.application.demo.script.PersonInfoForm.superclass.constructor.apply(this,[cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this)
	//this.on("save",this.onSave,this);
};

Ext.extend(chis.application.demo.script.PersonInfoForm,chis.script.BizTableFormView,{
	getLoadRequest : function(){
		this.initDataId = this.exContext.args.pkey;
	},
	
	onBeforeCreate : function(saveData){
		this.data.empiId = this.exContext.ids.empiId;
	},

	onSave : function(entryName, op, json, data){
		this.fireEvent("toFreshList",entryName, op, json, data);
		this.win.hide();
	}
	
});
