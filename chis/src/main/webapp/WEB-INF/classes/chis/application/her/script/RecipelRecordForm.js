$package("chis.application.her.script");

$import("chis.script.BizTableFormView");

chis.application.her.script.RecipelRecordForm = function(cfg){
	cfg.autoLoadSchema = false
	chis.application.her.script.RecipelRecordForm.superclass.constructor.apply(this,[cfg]);
	this.loadServiceId = "chis.healthRecipelManageService";
	this.loadAction = "getHealthRecipel";
	this.saveServiceId = "chis.healthRecipelManageService";
	this.saveAction = "saveHealthRecipel";
	this.on("loadData",this.onLoadData,this)
};

Ext.extend(chis.application.her.script.RecipelRecordForm,chis.script.BizTableFormView,{
	doCreate : function() {
		this.initDataId = null;
		this.form.getTopToolbar().items.item(0).enable()
		this.doNew();
	},
	onLoadData:function(entryName,body){
		if(body.createUser.key == this.mainApp.uid){
			this.form.getTopToolbar().items.item(0).enable()
		}else{
			this.form.getTopToolbar().items.item(0).disable()
		}
	}
});