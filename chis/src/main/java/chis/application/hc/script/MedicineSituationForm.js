$package("chis.application.hc.script");

$import("chis.script.BizTableFormView");

chis.application.hc.script.MedicineSituationForm = function(cfg) {
	this.backDataField = [{
				"medicineId" : "YPXH"
			}];
	this.backOtherField = [{
				"eachDose" : "YPJL,JLDW"
			}]
	chis.application.hc.script.MedicineSituationForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	// this.on("doNew",this.onDoNew,this);
};

Ext.extend(chis.application.hc.script.MedicineSituationForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.hc.script.MedicineSituationForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var isInsulin = form.findField("isInsulin");
				if (isInsulin) {
					isInsulin.on("blur", this.onIsInsulin, this)
					isInsulin.on("select", this.onIsInsulin, this)
				}
			},

			// onDoNew:function(){
			// var form = this.form.getForm()
			// var medicine = form.findField("medicine");
			// medicine.allowBlank=false;
			// medicine.getEl().parent().parent().first().dom.innerHTML = "<span
			// style='color:red'>药物名称:</span>";
			// this.validate()
			// },

			getSaveRequest : function(saveData) {
				saveData.healthCheck = this.exContext.args.healthCheck;
				return saveData;
			},

			onLoadData : function(entryName, body) {
				var form = this.form.getForm();
				var isInsulin = form.findField("isInsulin");
				if (isInsulin) {
					this.onIsInsulin(isInsulin)
				}
			},

			onIsInsulin : function(field) {
				var value = field.getValue();
				var form = this.form.getForm()
				var medicine = form.findField("medicine");
				var eachDose = form.findField("eachDose");
				var use = form.findField("use");
				var descr = form.findField("descr");
				if (value == 1) {
					medicine.allowBlank = true;
					medicine.getEl().parent().parent().first().dom.innerHTML = "<span style='color:black'>药物名称:</span>";
					descr.allowBlank = false;
					descr.getEl().parent().parent().first().dom.innerHTML = "<span style='color:red'>其他用药描述:</span>";
					eachDose.reset();
					use.reset();
					eachDose.disable()
					use.disable();
				} else {
					medicine.allowBlank = false;
					medicine.getEl().parent().parent().first().dom.innerHTML = "<span style='color:red'>药物名称:</span>";
					descr.allowBlank = true;
					descr.getEl().parent().parent().first().dom.innerHTML = "<span style='color:black'>其他用药描述:</span>";
					eachDose.enable()
					use.enable();
				}
				this.validate()
			}
		});