$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.RecordManageYearConfigForm")
chis.application.conf.script.mdc.HypertensionRiskConfigManageForm = function(cfg) {
	cfg.autoFieldWidth = true;
	cfg.labelWidth = 120
	cfg.colCount = 3
	chis.application.conf.script.mdc.HypertensionRiskConfigManageForm.superclass.constructor.apply(
			this, [cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.conf.script.mdc.HypertensionRiskConfigManageForm,
		chis.application.conf.script.RecordManageYearConfigForm, {

			isEmpty:function(d){
				for(var name in d){
					return false
				}
				return true
			}
			,
			changePlanMode : function(field, record, index, isloadData) {
				var value = field.getValue();
				var form = this.form.getForm();
				var items = this.schema.items
				var size = items.length
				this.doNew()
				if (!this.isEmpty(record)) {
					for (var i = 0; i < size; i++) {
						var it = items[i];
							var field = form.findField(it.id);
							field.reset();
							field.disable();
							field.allowBlank = true;
							it["not-null"] = false;
					}
				} else {
					for (var i = 0; i < size; i++) {
						var it = items[i]
							var field = form.findField(it.id);
							it["not-null"] = true;
							field.allowBlank = false;
							field.enable();
					}
				}
				this.initFormData(record)
				this.validate();
			},

			onLoadData : function(entryName,body) {
//				var planMode = this.form.getForm().findField("planMode");
//				this.changePlanMode(planMode, body, null, true);
			},
			
			onReady : function(){
				chis.application.conf.script.mdc.HypertensionRiskConfigManageForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				form.findField("planType1").on("select", this.onPlanTypeChange, this); 
        		form.findField("planType1").on("change", this.onPlanTypeChange, this);
        		form.findField("planType1").on("blur", this.onPlanTypeChange, this);
			},
        	
        	onPlanTypeChange : function(combo, record, index){
				var nValue = combo.getValue();
				if(nValue == "01"){
					Ext.Msg.alert("提示","高血压高危随访计划周期最小为2周1次");
					combo.setValue("02");
					return ;
				}
			}

		});