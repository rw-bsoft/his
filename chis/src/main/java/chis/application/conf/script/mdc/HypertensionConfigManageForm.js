$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.RecordManageYearConfigForm")
chis.application.conf.script.mdc.HypertensionConfigManageForm = function(cfg) {
	cfg.autoFieldWidth = true;
	cfg.labelWidth = 120
	cfg.colCount = 3
	this.groupName = "计划类型"
	chis.application.conf.script.mdc.HypertensionConfigManageForm.superclass.constructor.apply(
			this, [cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.conf.script.mdc.HypertensionConfigManageForm,
		chis.application.conf.script.RecordManageYearConfigForm, {

			doSave : function() {
				this.fireEvent("save", this);
			},

			changePlanMode : function(field,record,index, isloadData) {
				var value = field.getValue();
				var form = this.form.getForm();
				var items = this.schema.items
				var size = items.length
				if (value == '2') {
					for (var i = 0; i < size; i++) {
						var it = items[i]
						if (it.group == this.groupName) {
							it["not-null"] = false;
							var field = form.findField(it.id);
							field.allowBlank = true;
							if (!isloadData) {
								field.reset();
								field.disable();
							}
							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>"
									+ it.alias + ":</span>";
						}
					}
				} else {
					for (var i = 0; i < size; i++) {
						var it = items[i]
						if (it.group == this.groupName) {
							it["not-null"] = true;
							var field = form.findField(it.id);
							field.allowBlank = false;
							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>"
									+ it.alias + ":</span>";
							if (!isloadData) {
								field.enable();
							}
						}
					}
				}
				this.validate();
				this.fireEvent("planModeChange", value);
			},

			onLoadData : function() {
				var planMode = this.form.getForm().findField("planMode");
				this.changePlanMode(planMode,null,null,true);
			},
			
			onReady : function(){
				chis.application.conf.script.mdc.HypertensionConfigManageForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				form.findField("planType1").on("select", this.onPlanTypeChange, this); 
        		form.findField("planType1").on("change", this.onPlanTypeChange, this);
        		form.findField("planType1").on("blur", this.onPlanTypeChange, this);
        		
				form.findField("planType2").on("select", this.onPlanTypeChange, this); 
        		form.findField("planType2").on("change", this.onPlanTypeChange, this);
        		form.findField("planType2").on("blur", this.onPlanTypeChange, this);
        		
				form.findField("planType3").on("select", this.onPlanTypeChange, this); 
        		form.findField("planType3").on("change", this.onPlanTypeChange, this);
        		form.findField("planType3").on("blur", this.onPlanTypeChange, this);
			},
			
			onPlanTypeChange : function(combo, record, index){
				var nValue = combo.getValue();
				if(nValue == "01"){
					Ext.Msg.alert("提示","高血压随访计划周期最小为2周1次");
					combo.setValue("02");
					return ;
				}
			}

		});