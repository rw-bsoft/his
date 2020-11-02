$package("chis.application.hy.script.inquire");

$import("chis.script.BizTableFormView");

chis.application.hy.script.inquire.HypertensionInquireForm = function(cfg){
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	chis.application.hy.script.inquire.HypertensionInquireForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.hypertensionInquireService";
	this.saveAction = "saveHypertensionInquire";
	this.on("doNew", this.onDoNew, this);
//	this.on("save",this.onSave,this);
};

Ext.extend(chis.application.hy.script.inquire.HypertensionInquireForm,chis.script.BizTableFormView,{
	doAdd : function() {
		this.fireEvent("add");
//		this.doCreate();
	},
	
	onDoNew : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
	},
	
	onReady : function() {
		chis.application.hy.script.inquire.HypertensionInquireForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("isReferral").on("select", this.onIsRefferralSelect, this);
		form.findField("isReferral").on("change", this.onIsRefferralSelect, this);
		
		form.findField("constriction").on("blur", this.onConstrictionChange, this);
		form.findField("constriction").on("keyup", this.onConstrictionChange, this);
		
		form.findField("diastolic").on("blur", this.onDiastolicChange, this);
		form.findField("diastolic").on("keyup", this.onDiastolicChange,	this);
	},
	
	onIsRefferralSelect : function(combo) {
		var value = combo.getValue();
		var form = this.form.getForm();
		var aadf = form.findField("agencyAndDept");
		var rrf = form.findField("referralReason");
		var items = this.schema.items;
		if (!this.notNullItems) {
			this.notNullItems = [];
			for (var i = 0; i < items.length; i++) {
				if (items[i]["not-null"]) {
					this.notNullItems.push(items[i].id);
				}
			}
		}
		if (value == 1) {// @@ 已转诊，清除其他所有必填项。
			aadf.enable();
			aadf.allowBlank = false;
			Ext.getCmp(aadf.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
								.update("<span style='color:red'>" + aadf.fieldLabel + ":</span>");
			rrf.enable();
			rrf.allowBlank = false;
			Ext.getCmp(rrf.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
								.update("<span style='color:red'>" + rrf.fieldLabel + ":</span>");
			for (var i = 0; i < items.length; i++) {
				if (items[i].id == "inquireDate" || items[i].id == "isReferral" 
							|| items[i].fixed || items.evalOnServer) {
					continue;
				}
				var field = form.findField(items[i].id);
				if (field) {
					field.allowBlank = true;
					items[i]["not-null"] = false;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(items[i].alias + ":"); 
				}
			}
		} else if (value == 2) {// @@ 未转诊，恢复原有必填项。
			aadf.disable();
			aadf.setValue();
			aadf.allowBlank = true;
			Ext.getCmp(aadf.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(aadf.fieldLabel + ":");
			rrf.setValue();
			rrf.disable();
			rrf.allowBlank = true;
			Ext.getCmp(rrf.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(rrf.fieldLabel + ":");
			for (var i = 0; i < items.length; i++) {
				var field = form.findField(items[i].id);
				if (field && this.notNullItems.indexOf(items[i].id) > -1) {
					field.allowBlank = false;
					items[i]["not-null"] = true;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').
								update("<span style='color:red'>" + items[i].alias + ":</span>");
				}
			}
		}
		this.validate();
	},
	
	onConstrictionChange : function(field) {
		var constriction = field.getValue();
		var diastolicFld = this.form.getForm().findField("diastolic");
		var diastolic = diastolicFld.getValue();
		if (constriction) {
			diastolicFld.maxValue = constriction - 1;
		} else {
			diastolicFld.maxValue = 500;
		}
		diastolicFld.minValue = 50;
		if (diastolic) {
			field.minValue = diastolic + 1;
		} else {
			field.minValue = 50;
		}
		field.maxValue = 500;
		field.validate();
		diastolicFld.validate();
	},
	
	onDiastolicChange : function(field) {
		var diastolic = field.getValue();
		var constrictionFld = this.form.getForm().findField("constriction");
		var constriction = constrictionFld.getValue();
		if (constriction) {
			field.maxValue = constriction - 1;
		} else {
			field.maxValue = 500;
		}
		field.minValue = 50;
		if (diastolic) {
			constrictionFld.minValue = diastolic + 1;
		} else {
			constrictionFld.minValue = 50;
		}
		constrictionFld.maxValue = 500;
		field.validate();
		constrictionFld.validate();
	}
});