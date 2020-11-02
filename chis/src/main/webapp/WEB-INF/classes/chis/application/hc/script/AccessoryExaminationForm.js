$package("chis.application.hc.script")
$import("chis.script.BizTableFormView")

chis.application.hc.script.AccessoryExaminationForm =function(cfg){
	//赋值
	cfg.colCount = 4
	cfg.autoFieldWidth = false
	cfg.labelWidth  = 117
	cfg.fldDefaultWidth = 90
	chis.application.hc.script.AccessoryExaminationForm.superclass.constructor.apply(this,[cfg])
//	this.printurl = chis.script.util.helper.Helper.getUrl()
}
Ext.extend(chis.application.hc.script.AccessoryExaminationForm,chis.script.BizTableFormView,{
	onReady:function(){
		chis.application.hc.script.AccessoryExaminationForm.superclass.onReady.call(this)
		
		var form = this.form.getForm();
		
		var dentureF = this.form.getForm().findField("denture")
		dentureF.on("select", this.onDentureF, this);
		this.dentureF = dentureF
		
		var ecgF = this.form.getForm().findField("ecg")
		ecgF.on("select", this.onEcgF, this);
		ecgF.on("keyup", this.onEcgF, this);
		ecgF.on("blur", this.onEcgF, this);
		this.ecgF = ecgF
		
		var xF = this.form.getForm().findField("x")
		xF.on("select", this.onXF, this);
		xF.on("keyup", this.onXF, this);
		xF.on("blur", this.onXF, this);
		this.xF = xF
		
		var bF = this.form.getForm().findField("b")
		bF.on("select", this.onBF, this);
		bF.on("keyup", this.onBF, this);
		bF.on("blur", this.onBF, this);
		this.bF = bF
		
		var psF = this.form.getForm().findField("ps")
		psF.on("select", this.onPsF, this);
		psF.on("keyup", this.onPsF, this);
		psF.on("blur", this.onPsF, this);
		this.psF = psF
		
		form.findField("leftUp").disable();
		form.findField("leftDown").disable();
		form.findField("rightUp").disable();
		form.findField("rightDown").disable();
	},
	
	doNew : function() {
		chis.application.hc.script.AccessoryExaminationForm.superclass.doNew.call(this)
		this.onEcgF()
		this.onXF()
		this.onBF()
		this.onPsF()
		this.onDentureF()
	},
	
	initFormData:function(data){
		chis.application.hc.script.AccessoryExaminationForm.superclass.initFormData.call(this,data)
		this.onEcgF()
		this.onXF()
		this.onBF()
		this.onPsF()
		this.onDentureF()
	},
	
	onDentureF:function(combo, record, index){
		var frm = this.form.getForm();
		var dv = frm.findField("denture").getValue();
		if(dv && dv != 1){
			frm.findField("leftUp").enable();
			frm.findField("leftDown").enable();
			frm.findField("rightUp").enable();
			frm.findField("rightDown").enable();
		}else{
			frm.findField("leftUp").disable();
			frm.findField("leftDown").disable();
			frm.findField("rightUp").disable();
			frm.findField("rightDown").disable();
		}
	},
	
	onEcgF:function(){
		if(this.ecgF.getValue() == '2'){
			this.form.getForm().findField("ecgText").enable()
		}else{
			this.form.getForm().findField("ecgText").disable()
			this.form.getForm().findField("ecgText").setValue()
		}
	},
	
	onXF:function(){
		if(this.xF.getValue() == '2'){
			this.form.getForm().findField("xText").enable()
		}else{
			this.form.getForm().findField("xText").disable()
			this.form.getForm().findField("xText").setValue()
		}
	},
	
	onBF:function(){
		if(this.bF.getValue() == '2'){
			this.form.getForm().findField("bText").enable()
		}else{
			this.form.getForm().findField("bText").disable()
			this.form.getForm().findField("bText").setValue()
		}
	},
	
	onPsF:function(){
		if(this.psF.getValue() == '2'){
			this.form.getForm().findField("psText").enable()
		}else{
			this.form.getForm().findField("psText").disable()
			this.form.getForm().findField("psText").setValue()
		}
	},
	
	getSaveRequest : function(saveData) {
		saveData.healthCheck = this.exContext.args.healthCheck;
		saveData.empiId = this.exContext.args.empiId;
		saveData.phrId = this.exContext.args.phrId;
		return saveData;
	},

	getLoadRequest : function() {
		return {
			"healthCheck" : this.exContext.args.healthCheck
		}
	}
})