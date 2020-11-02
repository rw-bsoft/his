$package("chis.application.mzf.script");

$import("chis.script.BizTableFormView");

chis.application.mzf.script.MZFVisitForm = function(cfg){
	cfg.labelWidth = 140;
	cfg.fldDefaultWidth = 100;
	cfg.colCount =2;
	cfg.width = 100;
	chis.application.mzf.script.MZFVisitForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.mZFVisitService";
	this.saveAction = "saveVisitMZF";
	this.on("doNew", this.onDoNew, this);
	this.on("save",this.onSave,this);
};

Ext.extend(chis.application.mzf.script.MZFVisitForm,chis.script.BizTableFormView,{
	doNew : function() {
		debugger
		chis.application.mzf.script.MZFVisitForm.superclass.doNew.call(this);
		var form = this.form.getForm();
		if (this.exContext.ids.empiId) {
			this.data["empiId"] = this.exContext.ids.empiId;
			var empiId = this.exContext.ids.empiId;
			var phrId = this.exContext.ids.phrId;	
			var form = this.form.getForm();		
			form.findField("empiId").setValue(empiId);
			form.findField("phrId").setValue(phrId);
			//form.findField("createDate").enable();
		}
	},	
	
	doAdd : function() {
		this.fireEvent("add");
//		this.doCreate();
	},
	
	onDoNew : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
	},
	
	onReady : function() {
		chis.application.mzf.script.MZFVisitForm.superclass.onReady.call(this);
		var form = this.form.getForm();
	},
	
	onSave:function(schmema,op,body){
		debugger;
		if(body.code < 300){
			MyMessageTip.msg("提示", "保存成功!", true);
		}else{
			MyMessageTip.msg("提示", "保存失败!", true);
		}	
	}
});