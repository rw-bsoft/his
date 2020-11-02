$package("app.bia.hc")
$import("chis.application.hc.script.HealthCheckForm")

chis.application.hc.script.ManaEffectiveForm =function(cfg){
	cfg.bbar=[{
					text : '备注: 血压单位：mmHg、血糖单位：mmol/L'
				}]
	chis.application.hc.script.ManaEffectiveForm.superclass.constructor.apply(this,[cfg])
	this.printurl = chis.script.util.helper.Helper.getUrl()
	this.on("doNew",this.onDoNew,this)
}
Ext.extend(chis.application.hc.script.ManaEffectiveForm,chis.application.hc.script.HealthCheckForm,{
	onReady:function(){
		chis.application.hc.script.ManaEffectiveForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("systolicpressureGoal").on("blur", this.onConstrictionChange, this);
		form.findField("systolicpressureGoal").on("keyup", this.onConstrictionChange, this);
		form.findField("diastolicpressureGoal").on("blur", this.onDiastolicChange, this);
		form.findField("diastolicpressureGoal").on("keyup", this.onDiastolicChange,	this);
	},
	onDoNew:function(){
		var form = this.form.getForm()
		var nextcheckDate = form.findField("nextcheckDate");
		if(nextcheckDate){
			var checkDate = new Date(this.checkDate)
			var y = checkDate.getFullYear();
			nextcheckDate.setMinValue((y+1)+"-01-01")
			nextcheckDate.setValue((y+1)+"-01-01")
		}
	},
	onDiastolicChange : function(field) {
		var diastolic = field.getValue();
		var constrictionFld = this.form.getForm().findField("systolicpressureGoal");
		var constriction = constrictionFld.getValue();
		if (constriction) {
			field.maxValue = constriction - 1;
		} else {
			field.maxValue = 500;
		}
		field.minValue = 10;
		if (diastolic) {
			constrictionFld.minValue = diastolic + 1;
		} else {
			constrictionFld.minValue = 10;
		}
		constrictionFld.maxValue = 500;
		field.validate();
		constrictionFld.validate();
	},
	onConstrictionChange : function(field) {
		var constriction = field.getValue();
		var diastolicFld = this.form.getForm().findField("diastolicpressureGoal");
		var diastolic = diastolicFld.getValue();
		if (constriction) {
			diastolicFld.maxValue = constriction - 1;
		} else {
			diastolicFld.maxValue = 500;
		}
		diastolicFld.minValue = 10;
		if (diastolic) {
			field.minValue = diastolic + 1;
		} else {
			field.minValue = 10;
		}
		field.maxValue = 500;
		field.validate();
		diastolicFld.validate();
	},
	doPrint : function() {
		//alert("健康检查打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
		if (!this.initDataId) {
			return
		}
		var url = "resources/chis.prints.template.manaEffective.print?type=" + 1 + "&empiId=" + this.empiId + "&healthCheck="
				+ this.healthCheck+"&checkDate="+this.checkDate+"&userName="+this.createUser_text
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}

	}		
})